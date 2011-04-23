package survey

class LoginFilters {
    def filters = {
        all(controller: '*', action: '*') {
            before = {
                if (!session.user && !controllerName.equals('login')) {
                    def queryStr = params?.entrySet()?.findAll { !(it.key in ['action', 'controller']) }?.collect { "${it.key}=${it.value?.encodeAsURL()}" }?.join('&')

                    def requestUri = "/"
                    if (controllerName && actionName) {
                        requestUri += "${controllerName}/${actionName}"
                    }
                    if (queryStr?.trim()) {
                        requestUri += "?${queryStr}"
                    }  
                    redirect(controller:'login', action:'choose', params:['redirectTo':requestUri])
                    return false
                }
            }
        }
    }

}
