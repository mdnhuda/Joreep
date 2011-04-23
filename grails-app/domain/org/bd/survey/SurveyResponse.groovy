package org.bd.survey



import javax.jdo.annotations.PersistenceCapable
import javax.jdo.annotations.IdentityType
import javax.jdo.annotations.PrimaryKey
import javax.jdo.annotations.Persistent
import javax.jdo.annotations.IdGeneratorStrategy
import com.google.appengine.api.datastore.Key

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
class SurveyResponse implements Serializable {

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Long id

    @Persistent
    Key survey

    @Persistent
    String surveyName

    @Persistent(defaultFetchGroup = "true")
    List<Answer> answers

    @Persistent
    Key creator

    @Persistent
    String createdBy

    @Persistent
    Date dateCreated

    @Persistent
    Date lastUpdated

    @Persistent
    Boolean temporary

    static constraints = {
    	id( visible:false)
	}
}
