<table>
    <thead>
    <tr>
        <g:set var="numberOfCols" value="${columns.findAll { it.value == true }?.size() ?: 1}"/>
        <td colspan="${numberOfCols}">
            <div class="center tableCaption">${tableCaption}</div>
        </td>
    </tr>
    <tr>
        <g:if test="${columns.name == true}"><th>Name</th></g:if>
        <g:if test="${columns.status == true}"><th>Status</th></g:if>
        <g:if test="${columns.creator == true}"><th>Surveyor</th></g:if>
        <g:if test="${columns.dateCreated == true}"><th>Create Date</th></g:if>
        <g:if test="${columns.reportLink == true}"><th>&nbsp;</th></g:if>
    </tr>
    </thead>
    <tbody>
    <g:if test="${surveyInstanceList?.size()}">
        <g:each in="${surveyInstanceList}" status="i" var="surveyInstance">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                <g:if test="${columns.name == true}">
                    <td>
                        <g:if test="${showSurveyLink}">
                            <g:link controller="survey" action="show" id="${surveyInstance.id}">${fieldValue(bean: surveyInstance, field: 'name')}</g:link>
                        </g:if>
                        <g:elseif test="${showSurveyResponseLink}">
                            <g:link controller="surveyResponse" action="create" id="${surveyInstance.id}">${fieldValue(bean: surveyInstance, field: 'name')}</g:link>
                        </g:elseif>
                        <g:else>
                            ${fieldValue(bean: surveyInstance, field: 'name')}
                        </g:else>
                    </td>
                </g:if>

                <g:if test="${columns.status == true}"><td>${surveyInstance?.status}</td></g:if>
                <g:if test="${columns.creator == true}"><td>${surveyInstance?.createdBy}</td></g:if>
                <g:if test="${columns.dateCreated == true}"><td><g:formatDate date="${surveyInstance?.dateCreated}" format="MMM dd, yyyy"/></td></g:if>

                <g:if test="${columns.reportLink == true}">
                    <td><g:link controller="surveyResponse" action="report" id="${surveyInstance.id}">Show Report</g:link></td>
                </g:if>
            </tr>
        </g:each>
    </g:if>
    <g:else>
        <tr>
            <td colspan="4">
                <p class="center"> No Survey found! </p>
            </td>
        </tr>
    </g:else>
    </tbody>
</table>
