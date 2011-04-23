<%@ page import="org.bd.survey.QuestionType; org.bd.survey.SurveyResponse" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit Survey Response :: ${surveyName}</title>
    </head>
    <body>

        <g:render template="/common/navbar"/>

        <div class="body">

            <g:render template="/common/surveyHead" params="${[surveyInstance:surveyInstance]}"/>

            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${surveyResponseInstance}">
            <div class="errors">
                <g:renderErrors bean="${surveyResponseInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${surveyResponseInstance?.id}" />
                <input type="hidden" name="version" value="${surveyResponseInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>

                        <g:each in="${surveyResponseInstance.answers}" var="answer" status="i">
                            <g:set var="question" value="${questionMap[answer.question]}"/>
                            <tr class="prop">
                                <td valign="top" class="name">

                                    ${question?.title?.encodeAsHTML()}

                                    <g:if test="${question.type == QuestionType.RADIO}">
                                        <ul style="list-style:none;">
                                            <g:each in="${question?.options}" var="opt">
                                                <li style="padding-left:15px;">
                                                    <g:radio name="answer[${i}].optionId" value="${opt.id?.id}" checked="${opt.id in answer.selectedOptions}"/>&nbsp; ${opt.value?.encodeAsHTML()}
                                                </li>
                                            </g:each>
                                        </ul>
                                    </g:if>
                                    <g:elseif test="${question.type == QuestionType.CHECKBOX}">
                                        <ul style="list-style:none;">
                                            <g:each in="${question?.options}" var="opt">
                                                <li style="padding-left:15px;">
                                                    <input type='checkbox' name="answer[${i}].optionId" value="${opt.id?.id}"
                                                            <g:if test="${opt.id in answer.selectedOptions}"> checked='checked' </g:if>
                                                        />&nbsp; ${opt.value?.encodeAsHTML()}
                                                </li>
                                                <input type='hidden' name="_answer[${i}].optionId"/>
                                            </g:each>
                                        </ul>
                                    </g:elseif>
                                    <g:elseif test="${question.type == QuestionType.DROPDOWN}">
                                        <select name="answer[${i}].optionId">
                                            <g:each in="${question?.options}" var="opt">
                                                <option value="${opt?.id?.id}" <g:if test="${opt?.id in answer.selectedOptions}"> selected='selected' </g:if> >${opt?.value?.encodeAsHTML()}</option>
                                            </g:each>
                                        </select>
                                    </g:elseif>
                                    <g:elseif test="${question.type == QuestionType.TEXT}">
                                        <g:textField name="answer[${i}].otherComment" value="${answer.otherComment}"/>
                                    </g:elseif>

                                    <g:if test="${question.hasOtherCommentField && question.type != QuestionType.TEXT}">
                                        Other Comments:
                                        <g:textField name="answer[${i}].otherComment" value="${answer.otherComment}"/>
                                    </g:if>
                                </td>
                            </tr>
                        </g:each>

                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" value="Save" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
