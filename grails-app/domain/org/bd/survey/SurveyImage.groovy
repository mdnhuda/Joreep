package org.bd.survey



import javax.jdo.annotations.*;
// import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class SurveyImage implements Serializable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Long id

    @Persistent
    com.google.appengine.api.datastore.Blob image

    @Persistent
    String imageType

    static constraints = {
    	id( visible:false)
	}
}
