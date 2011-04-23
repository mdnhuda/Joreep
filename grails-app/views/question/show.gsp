<%@ page import="org.bd.survey.Question" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show Question</title>
    <g:javascript>
        function preDeleteOption(optionId) {
            document.getElementById('optionId').value = optionId;
            return confirm('Are you sure?');
        }
    </g:javascript>
</head>
<body>

<g:render template="/common/navbar"/>

<div class="body">
    <h1>Show Question :: <g:link controller="survey" action="show" id="${questionInstance?.survey?.id}">${questionInstance.survey?.name?.encodeAsHTML()}</g:link></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:form>
        <div class="dialog">
            <table>
                <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Title:</td>

                    <td valign="top" class="value">${fieldValue(bean: questionInstance, field: 'title')}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Question Type:</td>

                    <td valign="top" class="value">${questionInstance.type}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Options:</td>

                    <td valign="top" class="value">
                        <input type="hidden" name="optionId" id="optionId" value=""/>
                        <table>
                            <g:each in="${questionInstance.options}">
                                <tr>
                                    <td style="vertical-align:middle;">
                                        ${it?.value?.encodeAsHTML()} (${it?.weight})
                                    </td>
                                    <g:if test="${editable}">
                                        <td style="vertical-align:middle;">
                                            <div class="buttons">
                                                <span class="button"><g:actionSubmit class="delete" value="Delete" action="deleteOption" onclick="return preDeleteOption('${it?.id?.id}');"/></span>
                                            </div>
                                        </td>
                                    </g:if>
                                </tr>
                            </g:each>
                        </table>
                    </td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Put extra Comment box?</td>

                    <td valign="top" class="value">${fieldValue(bean: questionInstance, field: 'hasOtherCommentField')}</td>

                </tr>


                </tbody>
            </table>
        </div>
        <div class="buttons">
            <g:hiddenField name="questionId" value="${questionInstance?.id?.id}"/>
            <g:hiddenField name="surveyId" value="${questionInstance?.survey?.id}"/>
            <g:if test="${editable}">
                <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
                <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
            </g:if>
        </div>
    </g:form>
</div>
</body>
</html>
