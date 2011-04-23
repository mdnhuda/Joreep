<%@ page import="org.bd.survey.SurveyResponse" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>SurveyResponse List</title>
    </head>
    <body>

        <g:render template="/common/navbar"/>

        <div class="body">
            <h1>SurveyResponse List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <g:if test="${surveyInstance}">
                    Survey: ${surveyInstance.name?.encodeAsHTML()}
                </g:if>
                <g:if test="${responder}">
                    Responses by: ${responder?.encodeAsHTML()}
                </g:if>
                <table>
                    <thead>
                        <tr>

                            <g:if test="${!surveyInstance}"><g:sortableColumn property="survey" title="Survey" /></g:if>
                            <g:if test="${!responder}"><g:sortableColumn property="creator" title="Responder"/></g:if>
                            <g:sortableColumn property="dateCreated" title="Response Date"/>

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${surveyResponseInstanceList}" status="i" var="surveyResponseInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <g:if test="${!surveyInstance}">
                                <td>
                                    <g:link action="show" id="${surveyResponseInstance.id}">
                                        ${surveyResponseInstance?.surveyName?.encodeAsHTML()}
                                    </g:link>
                                </td>
                            </g:if>

                            <g:if test="${!responder}">
                                <td>${fieldValue(bean:surveyResponseInstance, field:'createdBy')}</td>
                            </g:if>

                            <td><g:formatDate date="${surveyResponseInstance?.dateCreated}" format="MMM dd, yyyy"/></td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            %{--<div class="paginateButtons">--}%
                %{--<g:paginate total="${surveyResponseInstanceTotal}" />--}%
            %{--</div>--}%
        </div>
    </body>
</html>
