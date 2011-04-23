package org.bd.survey

import org.bd.survey.exceptions.WebSecurityException
import org.bd.survey.utils.Utils
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.FileItemIterator
import org.apache.commons.fileupload.FileItemStream
import org.apache.commons.fileupload.util.Streams
import org.bd.survey.exceptions.RecordNotFoundException

import com.google.appengine.api.images.ImagesService
import com.google.appengine.api.images.Image
import com.google.appengine.api.images.ImagesServiceFactory
import com.google.appengine.api.images.Transform

class SurveyController {
    ImagesService googleImagesService
    def persistenceManager
    def mailService

    def index = { redirect(action: list, params: params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
//        params.max = Math.min(params.max ? params.max.toInteger() : 10, 100)
        javax.jdo.Query query = persistenceManager.newQuery(Survey)
        query.filter = "creator == creatorParam"
        query.ordering = "dateCreated desc"
        query.declareParameters "com.google.appengine.api.datastore.Key creatorParam"

        def surveyInstanceList = query.execute(Utils.getKey(session.user))

        def total = 0
        if (surveyInstanceList && surveyInstanceList.size() > 0) {
            total = surveyInstanceList.size()
        }

        [surveyor:session.user, surveyInstanceList: surveyInstanceList, surveyInstanceTotal: total]
    }

    def listAll = {
        if (!session.isAdminUser) {
            throw new WebSecurityException("Only admin can access this page!")
        }
        javax.jdo.Query query = persistenceManager.newQuery(Survey)
        //TODO set max range
        query.ordering = "dateCreated desc"

        def surveyInstanceList = query.execute()

        def total = 0
        if (surveyInstanceList && surveyInstanceList.size() > 0) {
            total = surveyInstanceList.size()
        }
        render(view: 'list', model: [surveyInstanceList: surveyInstanceList, surveyInstanceTotal: total])
    }

    def show = {
        def surveyInstance = persistenceManager.getObjectById(Survey.class, Long.parseLong(params.id))
        if (!session.isAdminUser && !Utils.canAccess(surveyInstance, session.user)) {
            throw new WebSecurityException()
        }

        if (!surveyInstance) {
            flash.message = "${message(code:'not.found', args:[message(code:'label.survey')])}"
            redirect(action: list)
        }
        else { return [surveyInstance: surveyInstance] }
    }

    def delete = {
        def surveyInstance = persistenceManager.getObjectById(Survey.class, Long.parseLong(params.id))
        if (!session.isAdminUser && !Utils.canAccess(surveyInstance, session.user)) {
            throw new WebSecurityException()
        }

        if (surveyInstance) {
            try {
                persistenceManager.deletePersistent(surveyInstance)
                flash.message = "Survey ${params.id} deleted"
                redirect(action: list)
            }
            catch (Exception e) {
                flash.message = "Survey ${params.id} could not be deleted"
                redirect(action: show, id: params.id)
            }
        }
        else {
            flash.message = "${message(code:'not.found', args:[message(code:'label.survey')])}"
            redirect(action: list)
        }
    }

    def edit = {
        def surveyInstance = persistenceManager.getObjectById(Survey.class, Long.parseLong(params.id))
        if (!session.isAdminUser && !Utils.canAccess(surveyInstance, session.user)) {
            throw new WebSecurityException()
        }

        if (!surveyInstance) {
            flash.message = "${message(code:'not.found', args:[message(code:'label.survey')])}"
            redirect(action: list)
        }
        else if (surveyInstance.status != Status.DRAFT) {
            flash.message = "${message(code:'not.editable')}"
            redirect(action: show, id: params.id)
        }
        else {
            surveyInstance = persistenceManager.detachCopy(surveyInstance)
            return [surveyInstance: surveyInstance]
        }
    }

    def update = {
        def surveyInstance = persistenceManager.getObjectById(Survey.class, Long.parseLong(params.id))
        if (!session.isAdminUser && !Utils.canAccess(surveyInstance, session.user)) {
            throw new WebSecurityException()
        }

        if (surveyInstance) {
            if (surveyInstance.status != Status.DRAFT) {
                flash.message = "${message(code:'not.editable')}"
                redirect(action: show, id: params.id)
                return
            }
            surveyInstance.properties = params
            if (!surveyInstance.hasErrors()) {

                try {
                    surveyInstance.lastUpdated = new Date()
                    persistenceManager.makePersistent(surveyInstance)
                } catch (Exception e) {
                    render(view: 'edit', model: [surveyInstance: surveyInstance])
                } finally {
                    flash.message = "Survey ${params.id} updated"
                    redirect(action: show, id: surveyInstance.id)
                }
            }
            else {
                render(view: 'edit', model: [surveyInstance: surveyInstance])
            }
        }
        else {
            flash.message = "${message(code:'not.found', args:[message(code:'label.survey')])}"
            redirect(action: list)
        }
    }

    def create = {
        def surveyInstance = new Survey()
        surveyInstance.properties = params
        return ['surveyInstance': surveyInstance]
    }

    def save = {
        def surveyInstance = new Survey(params)
        if (!surveyInstance.hasErrors()) {
            try {
                surveyInstance.creator = Utils.getKey(session.user)
                surveyInstance.createdBy = session.user?.toString()
                surveyInstance.dateCreated = new Date()
                surveyInstance.status = Status.DRAFT
                persistenceManager.makePersistent(surveyInstance)
                mailService?.sendMail("'${surveyInstance.name}' created by ${surveyInstance?.createdBy}")
            } finally {
                flash.message = "Survey ${surveyInstance.id} created"
                redirect(action: show, id: surveyInstance.id)
            }
        }

        render(view: 'create', model: [surveyInstance: surveyInstance])
    }

    def publish = {
        def surveyInstance = persistenceManager.getObjectById(Survey.class, Long.parseLong(params.id))
        if (!surveyInstance) {
            throw new RecordNotFoundException("${message(code:'not.found', args:[message(code:'label.survey')])}")
        }
        if (!session.isAdminUser && !Utils.canAccess(surveyInstance, session.user)) {
            throw new WebSecurityException()
        }

        if (surveyInstance.status != Status.DRAFT) {
            flash.message = "${message(code:'not.editable')}"
            redirect(action: show, id: params.id)
            return
        }

        surveyInstance.status = Status.PUBLISHED
        if (!surveyInstance.hasErrors()) {
            try {
                surveyInstance.lastUpdated = new Date()
                persistenceManager.makePersistent(surveyInstance)
                mailService?.sendMail("'${surveyInstance.name}' published by ${surveyInstance?.createdBy}")
            } catch (Exception e) {
                flash.message = "${message(code:'action.failed')}"
                redirect(action: show, id: surveyInstance.id)
            } finally {
                flash.message = "Survey ${params.id} Published"
                redirect(action: show, id: surveyInstance.id)
            }
        }
        else {
            flash.message = "${message(code:'action.failed')}"
            redirect(action: show, id: surveyInstance.id)
        }
    }

    def close = {
        def surveyInstance = persistenceManager.getObjectById(Survey.class, Long.parseLong(params.id))
        if (!surveyInstance) {
            throw new RecordNotFoundException("${message(code:'not.found', args:[message(code:'label.survey')])}")
        }
        if (!session.isAdminUser && !Utils.canAccess(surveyInstance, session.user)) {
            throw new WebSecurityException()
        }

        if (surveyInstance.status != Status.PUBLISHED) {
            flash.message = "${message(code:'not.closable')}"
            redirect(action: show, id: params.id)
            return
        }

        surveyInstance.status = Status.CLOSED
        if (!surveyInstance.hasErrors()) {
            try {
                surveyInstance.lastUpdated = new Date()
                persistenceManager.makePersistent(surveyInstance)
                mailService?.sendMail("'${surveyInstance.name}' closed by ${surveyInstance?.createdBy}")
            } catch (Exception e) {
                flash.message = "${message(code:'action.failed')}"
                redirect(action: show, id: surveyInstance.id)
            } finally {
                flash.message = "Survey ${params.id} Closed"
                redirect(action: show, id: surveyInstance.id)
            }
        }
        else {
            flash.message = "${message(code:'action.failed')}"
            redirect(action: show, id: surveyInstance.id)
        }
    }

    def uploadImage = {
        def surveyInstance = persistenceManager.getObjectById(Survey.class, Long.parseLong(params.id))
        if (!session.isAdminUser && !Utils.canAccess(surveyInstance, session.user)) {
            throw new WebSecurityException()
        }
        [id:surveyInstance?.id]
    }

    def processImageUpload = {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request)
        if (isMultipart) {
            def surveyId = null
            def fileData = null
            def contentType = null
            ServletFileUpload sfu = new ServletFileUpload()
            FileItemIterator  fileItemIterator = sfu.getItemIterator(request)
            while(fileItemIterator.hasNext()) {
                FileItemStream item = fileItemIterator.next()
                String fieldName = item.getFieldName()
                InputStream is =item.openStream()
                if (item.isFormField()) {
                    if (fieldName == 'id') {
                        surveyId = Streams.asString(is)
                    }
                } else {
                    contentType = item.contentType
                    fileData = Utils.readBytes(is)
                }
            }

            if (!surveyId) {
                throw new RuntimeException("id not present in request")
            }

            if (!Utils.isAllowedImageType(contentType)) {
                flash.message = 'Image Type not supported'
                redirect(action: 'uploadImage', id:surveyId)
                return
            }

            if (fileData.length) {
                def surveyInstance = persistenceManager.getObjectById(Survey.class, Long.parseLong(surveyId))
                if (!surveyInstance) {
                    throw new RecordNotFoundException("Survey with id=${surveyId} not found")
                }
                if (!session.isAdminUser && !Utils.canAccess(surveyInstance, session.user)) {
                    throw new WebSecurityException()
                }
                SurveyImage surveyImage
                if (surveyInstance.headerImage) {
                    surveyImage = persistenceManager.getObjectById(SurveyImage.class, surveyInstance.headerImage)
                } else {
                    surveyImage = new SurveyImage()
                }

                surveyImage.imageType = contentType

                Image googleImage = ImagesServiceFactory.makeImage(fileData)
                Transform headResize = ImagesServiceFactory.makeResize(700, 200)
                Image headerGoogleImage = googleImagesService.applyTransform(headResize, googleImage)
                surveyImage.image = new com.google.appengine.api.datastore.Blob(headerGoogleImage.getImageData())

                persistenceManager.makePersistent(surveyImage)
                surveyInstance.headerImage = Utils.getKey(surveyImage)
                persistenceManager.makePersistent(surveyInstance)
                redirect(action: show, id: surveyInstance.id)
            } else {
                flash.message = 'image cannot be empty'
                redirect(action: 'uploadImage', id:surveyId)
            }
        } else {
            throw new RuntimeException("Not a multipart Request")
        }
    }

    def showImage = {
        if (params.id) {
            SurveyImage imageInstance = persistenceManager.getObjectById(SurveyImage.class, Long.parseLong(params.id))
            if (!imageInstance) {
                throw new RecordNotFoundException("No Image found for id=${params.id}")
            }
            response.contentType = imageInstance.imageType
            response.outputStream << imageInstance.image?.bytes
            response.outputStream.flush()
            return
        } else {
            response.outputStream << []
            response.outputStream.flush()
            return
        }
    }
}
