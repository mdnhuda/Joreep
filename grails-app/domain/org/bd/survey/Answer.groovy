package org.bd.survey



import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.PrimaryKey
import javax.jdo.annotations.Persistent
import javax.jdo.annotations.IdGeneratorStrategy
import com.google.appengine.api.datastore.Key

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class Answer implements Serializable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Key key

    @Persistent
    Key question

    @Persistent
    Set<Key> selectedOptions

    @Persistent
    String otherComment

    static constraints = {
    	id( visible:false)
	}
}
