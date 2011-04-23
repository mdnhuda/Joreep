<%@ page import="org.bd.survey.QuestionType; org.bd.survey.Question" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Question</title>
    <g:javascript>
        function preAddOptions() {
            var numberOfOptionsToAdd = document.getElementById('numberOfOptionsToAdd').value;
            return numberOfOptionsToAdd > 0;
        }
    </g:javascript>

</head>
<body>

<g:render template="/common/navbar"/>

<div class="body">
    <h1>Create Question :: <g:link controller="survey" action="show" id="${questionInstance?.survey?.id}">${questionInstance.survey?.name?.encodeAsHTML()}</g:link></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${questionInstance}">
        <div class="errors">
            <g:renderErrors bean="${questionInstance}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post">
        <g:hiddenField name="survey.id" value="${questionInstance.survey?.id}"/>
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="title">Title:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: questionInstance, field: 'title', 'errors')}">
                        <g:textField name="title" value="${questionInstance?.title}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="type">Question Type:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: questionInstance, field: 'type', 'errors')}">
                        <g:select name="type" from="${QuestionType.values()}" value="${questionInstance?.type}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="options">Options:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: questionInstance, field: 'options', 'errors')}">
                        <g:render template="/common/options" model="${[questionInstance:questionInstance]}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="hasOtherCommentField">Put extra Comment box?</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: questionInstance, field: 'hasOtherCommentField', 'errors')}">
                        <g:checkBox name="hasOtherCommentField" value="${questionInstance?.hasOtherCommentField}"/>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
