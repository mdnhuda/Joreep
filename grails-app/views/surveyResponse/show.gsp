<%@ page import="org.bd.survey.utils.Utils; org.bd.survey.QuestionType; org.bd.survey.SurveyResponse" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show Survey Response :: ${surveyName}</title>
</head>
<body>

<g:render template="/common/navbar"/>

<div class="body">

    <g:render template="/common/surveyHead" params="${[surveyInstance:surveyInstance]}"/>

    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
        <ol>
            <g:set var="total" value="${0}"/>
            <g:set var="obtained" value="${0}"/>
            <g:each in="${surveyResponseInstance.answers}" var="answer">
                <g:set var="question" value="${questionMap[answer.question]}"/>
                <li>${question?.title?.encodeAsHTML()}

                    <g:if test="${question.type == QuestionType.TEXT}">
                        <div class="selected"> ${answer.otherComment?.encodeAsHTML()} </div>
                    </g:if>
                    <g:else>
                        <g:if test="${question?.options?.size()}"><g:set var="total" value="${total + (question?.options*.weight).max()}"/></g:if>
                        <ol style="list-style:lower-latin;">
                            <g:set var="score" value="${0}"/>
                            <g:each in="${question?.options}" var="opt">
                                <g:set var="optValue" value="${surveyInstance?.showResultAtResponseSubmission ? opt?.toString()?.encodeAsHTML() : opt?.value?.encodeAsHTML()}"/>
                                <g:if test="${opt.id in answer.selectedOptions}">
                                    <g:if test="${opt.weight > score}"><g:set var="score" value="${opt.weight}"/></g:if>
                                    <li class="option selected">*&nbsp; ${optValue}</li>
                                </g:if>
                                <g:else>
                                    <li class="option">${optValue}</li>
                                </g:else>
                            </g:each>
                            <g:set var="obtained" value="${obtained + score}"/>
                        </ol>
                        <g:if test="${answer.otherComment}">
                            <div>
                                <div class="floating selected">Other:</div>
                                <div class="floating">${answer.otherComment?.encodeAsHTML()}</div>
                                <div class="clearBoth">&nbsp;</div>
                            </div>
                        </g:if>
                    </g:else>
                </li>
            </g:each>
        </ol>
    </div>

    <g:if test="${surveyInstance?.showResultAtResponseSubmission}">
        <div style="width:100%; border-top:solid 1px; padding-top:2px">
            <div class="floating name">Your Score:</div>
            <div class="floating">${obtained} out of ${total}</div>
            <div class="clearBoth">&nbsp;</div>
        </div>
    </g:if>

    <g:if test="${surveyResponseInstance?.creator != Utils.getKey(session.user)}">
        <div class="description">
            Answered by ${surveyResponseInstance?.createdBy}
        </div>
    </g:if>

    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${surveyResponseInstance?.id}"/>
            <g:if test="${editable}">
                <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
                <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
            </g:if>
        </g:form>
    </div>
</div>
</body>
</html>
