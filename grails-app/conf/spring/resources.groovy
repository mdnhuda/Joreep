// Place your Spring DSL code here
beans = {
    googleUserService(com.google.appengine.api.users.UserServiceFactory) { bean ->
        bean.factoryMethod = "getUserService"
    }

    googleImagesService(com.google.appengine.api.images.ImagesServiceFactory) { bean ->
        bean.factoryMethod = "getImagesService"
    }
}
