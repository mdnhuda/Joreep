<%@ page import="org.bd.survey.Survey" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit Survey</title>
</head>
<body>

<g:render template="/common/navbar"/>

<div class="body">
    <h1>Edit Survey</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${surveyInstance}">
        <div class="errors">
            <g:renderErrors bean="${surveyInstance}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" name="id" value="${surveyInstance?.id}"/>
        <input type="hidden" name="version" value="${surveyInstance?.version}"/>
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: surveyInstance, field: 'name', 'errors')}">
                        <g:textField name="name" value="${surveyInstance?.name}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="description">Description:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: surveyInstance, field: 'description', 'errors')}">
                        <g:textArea name="description" value="${surveyInstance?.description?.value}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="name">Make the survey result public?</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: surveyInstance, field: 'isPublic', 'errors')}">
                        <g:checkBox name="isPublic" value="${surveyInstance?.isPublic}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="showResultAtResponseSubmission">Show the Result at Response Submission?</label>
                        <br/>(requires you to assign proper 'Weight' to the Options)
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: surveyInstance, field: 'showResultAtResponseSubmission', 'errors')}">
                        <g:checkBox name="showResultAtResponseSubmission" value="${surveyInstance?.showResultAtResponseSubmission}"/>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
