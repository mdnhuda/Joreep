package org.bd.survey

import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.PrimaryKey
import javax.jdo.annotations.Persistent
import javax.jdo.annotations.IdGeneratorStrategy
import com.google.appengine.api.datastore.Key

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class Question implements Serializable {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Key id

    @Persistent(defaultFetchGroup = "true")
    Survey survey

	@Persistent
	String title

    @Persistent
    QuestionType type

    @Persistent
    Boolean hasOtherCommentField

    @Persistent(defaultFetchGroup = "true", mappedBy = 'question')
    List<Option> options = []

    static constraints = {
    	id( visible:false)
        title(nullable:false)
	}

    def String toString() {
        "${title}"
    }
}
