package org.bd.survey

import com.google.appengine.api.datastore.*
import org.bd.survey.utils.Utils
import org.bd.survey.exceptions.WebSecurityException
import org.bd.survey.exceptions.RecordNotFoundException

class SurveyResponseController {

	def persistenceManager
    def mailService
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def responses = {
//        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
        if (params.id) {
            Survey surveyInstance = persistenceManager.getObjectById( Survey.class, Long.parseLong( params.id ) )

            if(!surveyInstance) {
                throw new RecordNotFoundException("${message(code:'not.found', args:[message(code:'label.survey')])}")
            }

            if (!session.isAdminUser && !Utils.canAccess(surveyInstance, session.user)) {
                throw new WebSecurityException()
            }
            javax.jdo.Query query = persistenceManager.newQuery(SurveyResponse)
            query.filter = "survey == surveyParam && temporary == false"
            query.ordering = "dateCreated desc"
            query.declareParameters "com.google.appengine.api.datastore.Key surveyParam"

            def surveyResponseInstanceList = query.execute(Utils.getKey(surveyInstance))
            def total = 0
            if (surveyResponseInstanceList && surveyResponseInstanceList.size() > 0) {
                total = surveyResponseInstanceList.size()
            }
            render(view:'list', model: [surveyInstance:surveyInstance, surveyResponseInstanceList: surveyResponseInstanceList, surveyResponseInstanceTotal: total])
        } else {
            throw new RuntimeException("Survey ID not present in Request")
        }
    }

    def report = {
        if (params.id) {
            Survey surveyInstance = persistenceManager.getObjectById( Survey.class, Long.parseLong( params.id ) )

            if(!surveyInstance) {
                throw new RecordNotFoundException("${message(code:'not.found', args:[message(code:'label.survey')])}")
            }

            if (!session.isAdminUser && !surveyInstance?.isPublic && !Utils.canAccess(surveyInstance, session.user)) {
                throw new WebSecurityException()
            }

            def questionMap = [:] as LinkedHashMap
            def totalWeight = 0
            surveyInstance?.questions?.each {
                questionMap[it.id] = it
                if (it.options?.size()) totalWeight += (it?.options*.weight).max()
            }

            javax.jdo.Query query = persistenceManager.newQuery(SurveyResponse)
            query.filter = "survey == surveyParam && temporary == false"
            query.ordering = "dateCreated desc"
            query.declareParameters "com.google.appengine.api.datastore.Key surveyParam"
            def surveyResponseInstanceList = query.execute(Utils.getKey(surveyInstance))

            def responseList = []
            surveyResponseInstanceList?.each { sr ->
                def resp = [:] as LinkedHashMap
                resp['user'] = sr.createdBy
                resp['id'] = sr.id
                def weight = 0
                sr?.answers?.each { ans ->
                    Question q = questionMap[ans.question]
                    if (q.type == QuestionType.TEXT) {
                        resp[ans.question] = ans.otherComment
                    } else {
                        resp[ans.question] = ans.selectedOptions
                        if (q?.options?.size() && ans.selectedOptions?.size()) {
                            weight += (q?.options?.findAll { it.id in  ans.selectedOptions}*.weight).max()
                        }
                    }
                }
                resp['weight'] = weight
                responseList << resp
            }
            [surveyInstance:surveyInstance, responseList:responseList, totalWeight:totalWeight]
        } else {
            throw new RuntimeException("Survey ID not present in Request")
        }
    }

    def list = {
//        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
        javax.jdo.Query query = persistenceManager.newQuery(SurveyResponse)
        query.filter = "creator == creatorParam"
        query.ordering = "dateCreated desc"
        query.declareParameters "com.google.appengine.api.datastore.Key creatorParam"

        def surveyResponseInstanceList = query.execute(Utils.getKey(session.user))

		def total = 0
		if(  surveyResponseInstanceList &&  surveyResponseInstanceList.size() > 0){
			total =  surveyResponseInstanceList.size()
		}
		[responder:session.user, surveyResponseInstanceList :  surveyResponseInstanceList,  surveyResponseInstanceTotal: total]
    }

    def show = {
        if (params.id) {
            SurveyResponse surveyResponseInstance = persistenceManager.getObjectById( SurveyResponse.class, Long.parseLong( params.id ) )
            Survey surveyInstance = persistenceManager.getObjectById(Survey.class, surveyResponseInstance.survey)

            if (!session.isAdminUser && !Utils.canAccess(surveyResponseInstance, session.user) && !Utils.canAccess(surveyInstance, session.user)) {
                throw new WebSecurityException()
            }
            def questionMap = [:]
            surveyInstance?.questions?.each {
                questionMap[it.id] = it
            }
            [ surveyResponseInstance : surveyResponseInstance, questionMap:questionMap, surveyInstance:surveyInstance, surveyName:surveyInstance.name, editable:(surveyInstance.status == Status.PUBLISHED) ]
        }
        else {
            throw new RuntimeException("No Survey Response Id found in Request")
        }
    }

    def edit = {
        if (params.id) {
            SurveyResponse surveyResponseInstance = persistenceManager.getObjectById( SurveyResponse.class, Long.parseLong( params.id ) )
            if(!surveyResponseInstance) {
                throw new RecordNotFoundException("Survey Response not found with id ${params.id}")
            }
            if (!session.isAdminUser && !Utils.canAccess(surveyResponseInstance, session.user)) {
                throw new WebSecurityException()
            }

            Survey surveyInstance = persistenceManager.getObjectById(Survey.class, surveyResponseInstance.survey)

            if (surveyInstance.status != Status.PUBLISHED) {
                flash.message = "${message(code:'not.published', args:[surveyInstance.status])}"
                redirect(action: show, id: params.id)
                return
            }

            def questionMap = [:]
            surveyInstance?.questions?.each {
                questionMap[it.id] = it
            }
            [ surveyResponseInstance : surveyResponseInstance, questionMap:questionMap, surveyInstance:surveyInstance, surveyName:surveyInstance.name ]
        }
        else {
            throw new RuntimeException("No Survey Response Id found in Request")
        }
    }

    def create = {
        if (params.id) {
            Survey surveyInstance = persistenceManager.getObjectById(Survey.class, Long.parseLong( params.id ))
            if (surveyInstance) {
                if (surveyInstance.status != Status.PUBLISHED) {
                    flash.message = "${message(code:'not.published', args:[surveyInstance.status])}"
                    redirect(uri: '/')
                    return
                }

                SurveyResponse surveyResponseInstance

                Key surveyKey = KeyFactory.createKey(Survey.class.simpleName, surveyInstance.id)
                javax.jdo.Query query = persistenceManager.newQuery(SurveyResponse, "survey == surveyParam && creator == creatorParam")
                query.declareParameters "com.google.appengine.api.datastore.Key surveyParam, com.google.appengine.api.datastore.Key creatorParam"
                def surveyResponses = query.execute(surveyKey, Utils.getKey(session.user))

                if (surveyResponses?.size()) {
                    surveyResponseInstance = surveyResponses?.get(0)
                } else {
                    surveyResponseInstance = new SurveyResponse(survey:surveyKey, surveyName:surveyInstance?.name, answers:[], temporary:true)
                    surveyInstance.questions?.each {
                        surveyResponseInstance.answers << new Answer(question:it.id)
                    }
                    surveyResponseInstance.creator = Utils.getKey(session.user)
                    surveyResponseInstance.createdBy = session.user?.toString()
                    surveyResponseInstance.dateCreated = new Date()
                    persistenceManager.makePersistent(surveyResponseInstance)
                }

                redirect(action:edit, id:surveyResponseInstance.id)
                return
            }
            else {
                throw new RecordNotFoundException("${message(code:'not.found', args:[message(code:'label.survey')])}")
            }
        }
        else {
            throw new RuntimeException("No Survey Id found in Request")
        }
    }

    def save = {
        if (params.id) {
            SurveyResponse surveyResponseInstance = persistenceManager.getObjectById(SurveyResponse.class, Long.parseLong( params.id ))
            if(!surveyResponseInstance) {
                throw new RecordNotFoundException("Survey Response not found with id ${params.id}")
            }

            if (!session.isAdminUser && !Utils.canAccess(surveyResponseInstance, session.user)) {
                throw new WebSecurityException()
            }

            if (surveyResponseInstance) {
                Survey surveyInstance = persistenceManager.getObjectById(Survey.class, surveyResponseInstance.survey)
                if (!surveyInstance) {
                    throw new RecordNotFoundException("Survey not found")
                }
                if (surveyInstance.status != Status.PUBLISHED) {
                    flash.message = "${message(code:'not.published', args:[surveyInstance.status])}"
                    redirect(action:show,id:surveyResponseInstance.id)
                    return
                }

                if (surveyResponseInstance.answers?.size()) {
                    for(def i = 0; i < surveyResponseInstance.answers.size(); i++) {
                        Answer ans = surveyResponseInstance.answers[i]
                        Question q = surveyInstance?.questions?.find { it.id == ans.question }
                        if (q) {
                            if (params["answer[${i}].optionId"]) {
                                def selectedOptions = Utils.makeOptionList(q, params["answer[${i}].optionId"])
                                ans.selectedOptions = selectedOptions
                            } else {
                                ans.selectedOptions = [] as Set
                            }

                            if (q.type == QuestionType.TEXT || q.hasOtherCommentField) {
                                ans.otherComment = params["answer[${i}].otherComment"]
                            }
                        }
                    }
                }

                if(!surveyResponseInstance.hasErrors() ) {
                    try{
                        surveyResponseInstance.temporary = false
                        surveyResponseInstance.lastUpdated = new Date()
                        persistenceManager.makePersistent(surveyResponseInstance)

                        AppUser surveyCreator = persistenceManager.getObjectById(AppUser, surveyInstance.creator)
                        mailService.sendMail(surveyCreator.email, "${session.user?.firstName} has just participated in your '${surveyInstance.name}' survey!")
                        mailService.sendMail(session.user?.email, "Thank you for participating in ${surveyInstance.name}", "Thank you for participating in ${surveyInstance.name} survey. Please note that, you can always come back and edit your responses as long as the survey is open!")
                    } finally{
                        flash.message = "Survey Response ${surveyResponseInstance.id} saved"
                        redirect(action:show,id:surveyResponseInstance.id)
                        return
                    }
                }

                def questionMap = [:]
                surveyInstance?.questions?.each { questionMap[it.id] = it }
                render(view:'edit',model:[ surveyResponseInstance : surveyResponseInstance, questionMap:questionMap, surveyName:surveyInstance.name ])
            }
            else {
                throw new RuntimeException("Survey Response not found with id ${params.id}")
            }
        }
        else {
            throw new RuntimeException("No Survey Response Id found in Request")
        }
    }

    def delete = {
        if (params.id) {
            SurveyResponse surveyResponseInstance = persistenceManager.getObjectById(SurveyResponse.class, Long.parseLong( params.id ))
            if(!surveyResponseInstance) {
                throw new RecordNotFoundException("Survey Response not found with id ${params.id}")
            }

            if (!session.isAdminUser && !Utils.canAccess(surveyResponseInstance, session.user)) {
                throw new WebSecurityException()
            }

            if (surveyResponseInstance) {
                Survey surveyInstance = persistenceManager.getObjectById(Survey.class, surveyResponseInstance.survey)
                if (!surveyInstance) {
                    throw new RecordNotFoundException("Survey not found")
                }
                if (surveyInstance.status != Status.PUBLISHED) {
                    flash.message = "${message(code:'not.published', args:[surveyInstance.status])}"
                    redirect(action:show,id:surveyResponseInstance.id)
                    return
                }

                try {
                    persistenceManager.deletePersistent(surveyResponseInstance)
                    flash.message = "Survey Response ${params.id} deleted"
                    redirect(action: list)
                }
                catch (Exception e) {
                    flash.message = "Survey Response ${params.id} could not be deleted"
                    redirect(action: show, id: params.id)
                }
            }
            else {
                throw new RuntimeException("Survey Response not found with id ${params.id}")
            }
        }
        else {
            throw new RuntimeException("No Survey Response Id found in Request")
        }
    }

}

