<%@ page import="org.bd.survey.Status; org.bd.survey.Survey" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show Survey</title>
</head>
<body>

<g:render template="/common/navbar"/>

<div class="body">
    <h1>Show Survey :: ${surveyInstance?.name?.encodeAsHTML()}</h1>
    <g:if test="${flash.message}">
        <div class="message">
            ${flash.message}
        </div>
    </g:if>

    <g:if test="${surveyInstance?.status == Status.DRAFT}">
        <div class="message">
            <g:if test="${surveyInstance?.questions?.size()}">
                Publish the survey to make it available to the world!
            </g:if>
            <g:else>
                Click on the 'Add Question' link to add new question to the survey.
            </g:else>
        </div>
    </g:if>

    <div class="dialog">
        <table>
            <tbody>

            <tr class="prop">
                <td valign="top" class="name">Status:</td>

                <td valign="top" class="value">${surveyInstance?.status}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Survey Header Image:</td>

                <td valign="top" class="value">
                    <a href="${createLink(action: 'uploadImage', id: surveyInstance?.id)}">
                        <g:if test="${surveyInstance?.headerImage}">
                            <img src="${createLink(action: 'showImage', id: surveyInstance?.headerImage?.id)}"/>
                        </g:if>
                        <g:else>
                            Upload Image
                        </g:else>
                    </a>
                </td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Description:</td>

                <td valign="top" class="value">${surveyInstance?.description?.value}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Make the survey result public?</td>

                <td valign="top" class="value">${surveyInstance?.isPublic}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Show the Result at Response Submission?</td>
                <td valign="top" class="value">${surveyInstance?.showResultAtResponseSubmission}</td>
            </tr>

            <tr class="prop">
                <td valign="top" class="name">Questions:</td>

                <td valign="top" class="value">
                    <div style="padding-bottom:10px;">
                        <span class="menuButton"><g:link class="create" controller="question" action="create" id="${surveyInstance?.id}">Add Question</g:link></span>
                    </div>
                    
                    <ol>
                        <g:each in="${surveyInstance?.questions}">
                            <li>
                                <a href="${createLink(controller: 'question', action: 'show', params: ['surveyId': surveyInstance?.id, 'questionId': it?.id?.id])}">
                                    ${it?.title?.encodeAsHTML()}
                                </a>
                                <ol style="list-style:lower-latin;">
                                    <g:each in="${it.options}" var="op">
                                        <li>${op?.value?.encodeAsHTML()} (${op?.weight})</li>
                                    </g:each>
                                </ol>
                            </li>
                        </g:each>
                    </ol>
                </td>

            </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${surveyInstance?.id}"/>
            <g:if test="${surveyInstance?.status == Status.DRAFT}">
                <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
                <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Survey will be permanently deleted. Are you sure?');" value="Delete"/></span>
                <g:if test="${surveyInstance?.questions?.size()}">
                    <span class="button" title="Publish the survey to make it available to the world!"><g:actionSubmit class="publish" onclick="confirm('Published Survey can not be edited. Are you sure you want to Publish it?');" value="Publish"/></span>
                </g:if>
            </g:if>

            <g:if test="${surveyInstance?.status == Status.PUBLISHED}">
                <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Survey will be closed. Are you sure?');" value="Close"/></span>
            </g:if>
            <g:if test="${surveyInstance?.status in [Status.PUBLISHED, Status.CLOSED]}">
                <span class="button menuButton">
                    <g:link controller="surveyResponse" action="report" id="${surveyInstance?.id}" class="list">
                        Show Report
                    </g:link>
                </span>
            </g:if>
        </g:form>
    </div>
</div>
</body>
</html>
