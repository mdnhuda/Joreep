package org.bd.survey

import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.PrimaryKey
import javax.jdo.annotations.Persistent
import javax.jdo.annotations.IdGeneratorStrategy
import com.google.appengine.api.datastore.Key

// import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class Option implements Serializable {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Key id

	@Persistent
	String value

    @Persistent
    Integer weight

    @Persistent
    Question question

    static constraints = {
    	id( visible:false)
	}

    def String toString() {
        "${value}  (${weight})"
    }
}
