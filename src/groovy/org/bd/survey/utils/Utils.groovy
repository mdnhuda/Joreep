package org.bd.survey.utils

import com.google.appengine.api.datastore.KeyFactory
import org.bd.survey.Question
import org.bd.survey.QuestionType

/**
 * Created by IntelliJ IDEA.
 * User: naim
 * Date: Nov 10, 2010
 * Time: 11:38:10 PM
 * To change this template use File | Settings | File Templates.
 */
class Utils {
    static String ADMIN_EMAIL = 'mdnhuda@gmail.com'

    static def makeOptionList(Question question, optionStr) {
        def lst
        switch (question.type) {
            case QuestionType.RADIO:
            case QuestionType.DROPDOWN:
                lst = question.options?.findAll { it.id.id == Long.parseLong(optionStr) }
                break
            case QuestionType.CHECKBOX:
                if (optionStr instanceof String) {
                    lst = question.options?.findAll { it.id.id == Long.parseLong(optionStr) }
                } else {
                    def ids = optionStr?.collect { Long.parseLong(it) }
                    lst = question.options?.findAll { it.id.id in ids }
                }
        }
        return lst?.collect {it.id} as Set
    }

    static def getKey(def obj) {
        return KeyFactory.createKey(obj.class.simpleName, obj.id)
    }

    static def ALLOWED_IMAGE_TYPES = ['image/jpeg', 'image/png']
    static def isAllowedImageType(String type) {
        return (type in ALLOWED_IMAGE_TYPES)
    }

    static def canAccess(obj, user) {
        return (obj?.properties?.creator == getKey(user))
    }

    static def readBytes(InputStream inputStream) {
        byte[] buffer = new byte[1000]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while(1) {
            def len = inputStream.read(buffer)
            if (len > 0) {
                baos.write(buffer, 0, len)
            } else {
                break
            }
        }
        return baos.toByteArray()
    }
}
