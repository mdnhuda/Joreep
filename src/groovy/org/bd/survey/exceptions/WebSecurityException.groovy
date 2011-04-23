package org.bd.survey.exceptions

import org.codehaus.groovy.grails.exceptions.GrailsException

/**
 * User: naim
 * Date: Nov 11, 2010
 */
class WebSecurityException extends GrailsException {
    private String message = "You Don't Have Sufficient Privilege!"

    def WebSecurityException() {
    }

    def WebSecurityException(message) {
        this.message = message;
    }

    public String getMessage() {
        return message
    }
}
