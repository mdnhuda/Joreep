package org.bd.survey

import com.google.appengine.api.datastore.*
import org.bd.survey.utils.Utils
import org.bd.survey.exceptions.WebSecurityException

class QuestionController {

	def persistenceManager
    
    def index = { redirect(uri: '/') }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def show = {
        Key parentKey = KeyFactory.createKey(Survey.class.simpleName, Long.parseLong(params.surveyId))
        Key realChildKey = KeyFactory.createKey(parentKey, Question.class.simpleName, Long.parseLong(params.questionId));
        def questionInstance = persistenceManager.getObjectById( Question.class, realChildKey)
        if(!questionInstance) {
            flash.message = "Question not found with surveyId ${params.surveyId}, questionId ${params.questionId}"
            redirect(controller:'survey', action:show, id: params.surveyId)
            return
        }

        if (!session.isAdminUser && !Utils.canAccess(questionInstance.survey, session.user)) {
            throw new WebSecurityException()
        }

        [questionInstance : questionInstance, editable:(questionInstance?.survey?.status == Status.DRAFT)]
    }

    def delete = {
        Key parentKey = KeyFactory.createKey(Survey.class.simpleName, Long.parseLong(params.surveyId))
        Key realChildKey = KeyFactory.createKey(parentKey, Question.class.simpleName, Long.parseLong(params.questionId));
        def questionInstance = persistenceManager.getObjectById( Question.class, realChildKey)
        if(questionInstance) {
            if (!session.isAdminUser && !Utils.canAccess(questionInstance.survey, session.user)) {
                throw new WebSecurityException()
            }

            if (questionInstance.survey?.status != Status.DRAFT) {
                flash.message = "${message(code:'not.editable')}"
                redirect(action:show, params:[surveyId:params.surveyId, questionId:params.questionId])
                return
            }

            try {
                persistenceManager.deletePersistent(questionInstance)
                flash.message = "Question with surveyId ${params.surveyId}, questionId ${params.questionId} deleted"
                redirect(controller:'survey', action:show, id: params.surveyId)
            }
            catch(Exception e) {
                flash.message = "Question with surveyId ${params.surveyId}, questionId ${params.questionId} could not be deleted"
                redirect(action:show, params:[surveyId:params.surveyId, questionId:params.questionId])
            }
        }
        else {
            flash.message = "Question not found with surveyId ${params.surveyId}, questionId ${params.questionId}"
            redirect(controller:'survey', action:show, id: params.surveyId)
        }
    }

    def deleteOption = {
        Key surveyKey = KeyFactory.createKey(Survey.class.simpleName, Long.parseLong(params.surveyId))
        Key questionKey = KeyFactory.createKey(surveyKey, Question.class.simpleName, Long.parseLong(params.questionId))
        Key optionKey = KeyFactory.createKey(questionKey, Option.class.simpleName, Long.parseLong(params.optionId));
        def optionInstance = persistenceManager.getObjectById( Option.class, optionKey)
        if(optionInstance) {

            if (!session.isAdminUser && !Utils.canAccess(optionInstance.question?.survey, session.user)) {
                throw new WebSecurityException()
            }

            if (optionInstance.question?.survey?.status != Status.DRAFT) {
                flash.message = "${message(code:'not.editable')}"
                redirect(action:show, params:[surveyId:params.surveyId, questionId:params.questionId])
                return
            }

            try {
                persistenceManager.deletePersistent(optionInstance)
                flash.message = "Option with surveyId=${params.surveyId}, questionId=${params.questionId}, optionId=${params.optionId} deleted"
                redirect(action:show, params: [surveyId: params.surveyId, questionId: params.questionId])
            }
            catch(Exception e) {
                flash.message = "Option with surveyId=${params.surveyId}, questionId=${params.questionId}, optionId=${params.optionId} could not be deleted"
                redirect(action:show, params: [surveyId: params.surveyId, questionId: params.questionId])
            }
        }
        else {
            flash.message = "Option not found with surveyId=${params.surveyId}, questionId=${params.questionId}, optionId=${params.optionId}."
            redirect(action:show, params: [surveyId: params.surveyId, questionId: params.questionId])
        }
    }

    def addOptions = {
        def questionInstance
        def editFlag
        if (params.surveyId && params.questionId) {
            Key parentKey = KeyFactory.createKey(Survey.class.simpleName, Long.parseLong(params.surveyId))
            Key realChildKey = KeyFactory.createKey(parentKey, Question.class.simpleName, Long.parseLong(params.questionId));
            questionInstance = persistenceManager.getObjectById( Question.class, realChildKey)
            if (!questionInstance) {
                flash.message = "Question not found with surveyId ${params.surveyId}, questionId ${params.questionId}"
                redirect(controller:'survey', action:show, id: params.surveyId)
                return
            }
            editFlag = true
        } else {
            def surveyInstance = persistenceManager.getObjectById( Survey.class, Long.parseLong( params['survey.id'] ) )
            questionInstance = new Question()
            questionInstance.survey = surveyInstance
            editFlag = false
        }

        if (!session.isAdminUser && !Utils.canAccess(questionInstance.survey, session.user)) {
            throw new WebSecurityException()
        }

        if (questionInstance.survey?.status != Status.DRAFT) {
            flash.message = "${message(code:'not.editable')}"
            redirect(action:show, params:[surveyId:params.surveyId, questionId:params.questionId])
            return
        }

        if (params.numberOfOptions) {
            def numberOfOptions = Long.parseLong(params.numberOfOptions)
            if (numberOfOptions > questionInstance.options.size()) {
                (0..(numberOfOptions - questionInstance.options.size() - 1)).each { questionInstance.options << new Option() }
            }
        }
        bindData(questionInstance, params)

        if (params.numberOfOptionsToAdd) {
            def numberOfOptionsToAdd = Long.parseLong(params.numberOfOptionsToAdd)
            if (numberOfOptionsToAdd > 0) {
                (0..(numberOfOptionsToAdd - 1)).each { questionInstance.options << new Option() }
            }
        }

        render(view:(editFlag ? 'edit' : 'create'), model:[questionInstance:questionInstance])
    }

    def copyOptionsFromPreviousQuestion = {
        def questionInstance
        def editFlag
        if (params.surveyId && params.questionId) {
            Key parentKey = KeyFactory.createKey(Survey.class.simpleName, Long.parseLong(params.surveyId))
            Key realChildKey = KeyFactory.createKey(parentKey, Question.class.simpleName, Long.parseLong(params.questionId));
            questionInstance = persistenceManager.getObjectById( Question.class, realChildKey)
            if (!questionInstance) {
                flash.message = "Question not found with surveyId ${params.surveyId}, questionId ${params.questionId}"
                redirect(controller:'survey', action:show, id: params.surveyId)
                return
            }
            editFlag = true
        } else {
            def surveyInstance = persistenceManager.getObjectById( Survey.class, Long.parseLong( params['survey.id'] ) )
            questionInstance = new Question()
            questionInstance.survey = surveyInstance

            editFlag = false
        }
        bindData(questionInstance, params)

        if (!session.isAdminUser && !Utils.canAccess(questionInstance.survey, session.user)) {
            throw new WebSecurityException()
        }

        if (questionInstance.survey?.status != Status.DRAFT) {
            flash.message = "${message(code:'not.editable')}"
            redirect(action:show, params:[surveyId:params.surveyId, questionId:params.questionId])
            return
        }

        Survey surveyInstance = questionInstance.survey
        if (surveyInstance.questions?.size()) {
            Question lastQuestion = surveyInstance.questions?.last()
            lastQuestion?.options?.each {
                questionInstance.options << new Option(question:questionInstance, value: it.value, weight: it.weight)
            }
        } else {
            //TODO show error
        }
        render(view:(editFlag ? 'edit' : 'create'), model:[questionInstance:questionInstance])
    }

    def edit = {
        Key parentKey = KeyFactory.createKey(Survey.class.simpleName, Long.parseLong(params.surveyId))
        Key realChildKey = KeyFactory.createKey(parentKey, Question.class.simpleName, Long.parseLong(params.questionId));
        def questionInstance = persistenceManager.getObjectById( Question.class, realChildKey)
        if(!questionInstance) {
            flash.message = "Question not found with surveyId ${params.surveyId}, questionId ${params.questionId}"
            redirect(controller:'survey', action:show, id: params.surveyId)
        }
        else {
            if (!session.isAdminUser && !Utils.canAccess(questionInstance.survey, session.user)) {
                throw new WebSecurityException()
            }
            if (questionInstance.survey?.status != Status.DRAFT) {
                flash.message = "${message(code:'not.editable')}"
                redirect(action:show, params:[surveyId:params.surveyId, questionId:params.questionId])
                return
            }

			questionInstance = persistenceManager.detachCopy( questionInstance )
        	return [ questionInstance : questionInstance ]
        }
    }

    def update = {
        Key parentKey = KeyFactory.createKey(Survey.class.simpleName, Long.parseLong(params.surveyId))
        Key realChildKey = KeyFactory.createKey(parentKey, Question.class.simpleName, Long.parseLong(params.questionId));
        def questionInstance = persistenceManager.getObjectById( Question.class, realChildKey)

    	if(questionInstance) {

            if (!session.isAdminUser && !Utils.canAccess(questionInstance.survey, session.user)) {
                throw new WebSecurityException()
            }

            if (questionInstance.survey?.status != Status.DRAFT) {
                flash.message = "${message(code:'not.editable')}"
                redirect(action:show, params:[surveyId:params.surveyId, questionId:params.questionId])
                return
            }

            if (params.numberOfOptions) {
                def numberOfOptions = Long.parseLong(params.numberOfOptions)
                if (numberOfOptions > questionInstance.options.size()) {
                    (0..(numberOfOptions - questionInstance.options.size() - 1)).each { questionInstance.options << new Option() }                    
                }
            }

            bindData(questionInstance, params)

            if(!questionInstance.hasErrors()){	
				try{
					persistenceManager.makePersistent(questionInstance)
				} catch( Exception e ){
				   	render(view:'edit',model:[questionInstance:questionInstance])
				}finally{
                    flash.message = "Question ${questionInstance.id} updated"
                    redirect(controller:'survey', action:show,id:questionInstance.survey?.id)
				}        
 			}
            else {
                render(view:'edit',model:[questionInstance:questionInstance])
            }
        }
        else {
            flash.message = "Question not found with surveyId ${params.surveyId}, questionId ${params.questionId}"
            redirect(controller:'survey', action:show, id: params.surveyId)
        }
    }

    def create = {
        def surveyInstance = persistenceManager.getObjectById( Survey.class, Long.parseLong( params.id ) )
        if (!session.isAdminUser && !Utils.canAccess(surveyInstance, session.user)) {
            throw new WebSecurityException()
        }

        if (surveyInstance.status != Status.DRAFT) {
            flash.message = "${message(code:'not.editable')}"
            redirect(controller: 'survey', action: show, id: params.id)
            return
        }

        def questionInstance = new Question()
        questionInstance.survey = surveyInstance
        return ['questionInstance':questionInstance]
    }

    def save = {
        def questionInstance = new Question()
        if (params.numberOfOptions) {
            def numberOfOptions = Long.parseLong(params.numberOfOptions)
            if (numberOfOptions > 0) {
                (0..(numberOfOptions - 1)).each { questionInstance.options << new Option(question:questionInstance) }                
            }
        }
        bindData(questionInstance, params)

		if(!questionInstance.hasErrors() ) {
			try {
                Key k = KeyFactory.createKey(Question.class.getSimpleName(), new Date().getTime());
                questionInstance.id = k

                def surveyInstance = persistenceManager.getObjectById( Survey.class, Long.parseLong( params['survey.id'] ) )

                if (!session.isAdminUser && !Utils.canAccess(surveyInstance, session.user)) {
                    throw new WebSecurityException()
                }
                if (surveyInstance.status != Status.DRAFT) {
                    flash.message = "${message(code:'not.editable')}"
                    redirect(controller: 'survey', action: show, id: params.id)
                    return
                }

                questionInstance.survey = surveyInstance
                surveyInstance.questions << questionInstance
				persistenceManager.makePersistent(surveyInstance)
			} finally {
				flash.message = "Question ${questionInstance.id} created"
				redirect(controller:'survey', action:show,id:questionInstance.survey?.id)
			}
		}
   
		render(view:'create',model:[questionInstance:questionInstance])
        
    }
}
