package org.bd.survey

import org.bd.survey.utils.Utils
import javax.jdo.Extent

class HomeController {
    def persistenceManager

    def index = {
        javax.jdo.Query queryRespondedSurvey = persistenceManager.newQuery(SurveyResponse)
        queryRespondedSurvey.filter = "creator == creatorParam"
        queryRespondedSurvey.declareParameters "com.google.appengine.api.datastore.Key creatorParam"

        def respondedSurveyInstanceList = queryRespondedSurvey.execute(Utils.getKey(session.user))
        def respondedSurveyIds = respondedSurveyInstanceList?.collect { it.survey?.id }

        def sessionUserKey = Utils.getKey(session.user)
        def mySurveys = []
        def publicSurveys = []
        def openSurveys = []
        Extent extent = persistenceManager.getExtent(Survey.class, false)
        for (Survey s: extent) {
            if (s.creator == sessionUserKey) {
                mySurveys << s
            }

            if (s.creator != sessionUserKey && s.isPublic && (s.status == Status.PUBLISHED || s.status == Status.CLOSED)) {
                publicSurveys << s
            }

            if (!(s.id in respondedSurveyIds) && s.status == Status.PUBLISHED) {
                openSurveys << s
            }
        }
        mySurveys = mySurveys.sort { s1, s2 -> s2.dateCreated <=> s1.dateCreated}
        publicSurveys = publicSurveys.sort { s1, s2 -> s2.dateCreated <=> s1.dateCreated}
        openSurveys = openSurveys.sort { s1, s2 -> s2.dateCreated <=> s1.dateCreated}
        [mySurveyInstanceList:mySurveys, publicSurveyInstanceList: publicSurveys, openSurveyInstanceList:openSurveys]
    }

    def list = {
        
    }
}
