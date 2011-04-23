package org.bd.survey



import javax.jdo.annotations.*;
// import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class AppUser implements Serializable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Long id

    @Persistent
    String email

    @Persistent
    String firstName

    @Persistent
    String lastName

    @Persistent
    UserOrigin origin

    @Persistent
    String originId

    static constraints = {
    	id( visible:false)
	}

    def String toString() {
        "${firstName} ${lastName} (${email?.replace('@', 'AT')})"
    }

}
