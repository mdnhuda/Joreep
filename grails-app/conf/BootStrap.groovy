import org.codehaus.groovy.grails.web.util.WebUtils
import org.codehaus.groovy.grails.commons.GrailsApplication

class BootStrap {

    def init = { servletContext ->
        ConfigObject config = WebUtils.lookupApplication(servletContext).getConfig()
        config.put('grails.web.disable.multipart', true)
    }
    def destroy = {
    }
}
