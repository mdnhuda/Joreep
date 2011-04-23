<%@ page import="org.bd.survey.Survey" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Survey List</title>
    </head>
    <body>

        <g:render template="/common/navbar"/>

        <div class="body">
            <h1>Survey List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <g:if test="${surveyor}">
                    Surveys created by: ${surveyor.toString()?.encodeAsHTML()}
                </g:if>
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="name" title="Name" />
                            <g:if test="${!surveyor}">
                                <g:sortableColumn property="creator" title="Surveyor" />
                            </g:if>
                   	        <g:sortableColumn property="status" title="Status" />
                   	        <g:sortableColumn property="dateCreated" title="Create Date" />

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${surveyInstanceList}" status="i" var="surveyInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${surveyInstance.id}">${fieldValue(bean:surveyInstance, field:'name')}</g:link></td>

                            <g:if test="${!surveyor}">
                                <td>${fieldValue(bean:surveyInstance, field:'createdBy')}</td>
                            </g:if>

                            <td>${surveyInstance?.status}</td>
                            <td><g:formatDate date="${surveyInstance?.dateCreated}" format="MMM dd, yyyy"/></td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
%{--
            <div class="paginateButtons">
                <g:paginate total="${surveyInstanceTotal}" />
            </div>
--}%
        </div>
    </body>
</html>
