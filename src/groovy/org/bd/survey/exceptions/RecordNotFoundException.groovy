package org.bd.survey.exceptions

import org.codehaus.groovy.grails.exceptions.GrailsException

/**
 * User: naim
 * Date: Nov 11, 2010
 */
class RecordNotFoundException extends GrailsException {
    private String message = "Record Not Found"

    def RecordNotFoundException() {
    }

    def RecordNotFoundException(message) {
        this.message = message;
    }

    public String getMessage() {
        return message
    }
}
