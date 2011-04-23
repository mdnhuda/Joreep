package org.bd.survey



import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.PrimaryKey
import javax.jdo.annotations.Persistent
import javax.jdo.annotations.IdGeneratorStrategy
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class Survey implements Serializable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Long id

	@Persistent
	String name

	@Persistent
	com.google.appengine.api.datastore.Text description

    @Persistent
    Key headerImage

    @Persistent(mappedBy = 'survey')
	List<Question> questions

    @Persistent
    Key creator

    @Persistent
    String createdBy

    @Persistent
    Date dateCreated

    @Persistent
    Date lastUpdated

    @Persistent()
    Status status

    @Persistent
    Boolean isPublic

    @Persistent
    Boolean showResultAtResponseSubmission

    static constraints = {
    	id( visible:false)
	}

    def String toString() {
        "${name}"
    }
}
