<%@ page import="org.bd.survey.Question" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Question List</title>
    </head>
    <body>

        <g:render template="/common/navbar"/>

        <div class="body">
            <h1>Question List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <g:sortableColumn property="options" title="Options" />
                        
                   	        <th>Survey</th>
                   	    
                   	        <g:sortableColumn property="title" title="Title" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${questionInstanceList}" status="i" var="questionInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${fieldValue(bean:questionInstance, field:'id')}">${fieldValue(bean:questionInstance, field:'id')}</g:link></td>
                        
                            <td>${fieldValue(bean:questionInstance, field:'options')}</td>
                        
                            <td>${fieldValue(bean:questionInstance, field:'survey')}</td>
                        
                            <td>${fieldValue(bean:questionInstance, field:'title')}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
%{--
            <div class="paginateButtons">
                <g:paginate total="${questionInstanceTotal}" />
            </div>
--}%
        </div>
    </body>
</html>
