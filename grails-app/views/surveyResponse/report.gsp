<%@ page import="org.bd.survey.utils.Utils; org.bd.survey.QuestionType; org.bd.survey.SurveyResponse" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Survey Response Report</title>
</head>
<body>

<g:render template="/common/navbar"/>

<div class="body">

    <g:render template="/common/surveyHead" params="${[surveyInstance:surveyInstance]}"/>

    <g:set var="hasAccessToLinks" value="${surveyInstance?.creator == Utils.getKey(session.user)}"/>
    <h1>
        Survey Response Report ::
        <g:if test="${hasAccessToLinks}">
            <g:link controller="survey" action="show" id="${surveyInstance?.id}">${surveyInstance?.name?.encodeAsHTML()}</g:link>
        </g:if>
        <g:else>
            ${surveyInstance?.name?.encodeAsHTML()}
        </g:else>
    </h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
        <table>
            <thead>
            <tr>

                <th>Responder</th>
                <g:each in="${surveyInstance?.questions}" var="question" status="i">
                    <th title="${question?.title}">Q. ${i + 1}</th>
                </g:each>
                <th>Total Weight (${totalWeight})</th>
            </tr>
            </thead>
            <tbody>
            <g:set var="alphabets" value="${'a'..'z'}"/>
            <g:each in="${responseList}" status="i" var="resp">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                    <td>
                        <g:if test="${hasAccessToLinks}">
                            <g:link controller="surveyResponse" action="show" id="${resp.id}" target="_blank">${resp.user?.encodeAsHTML()}</g:link>
                        </g:if>
                        <g:else>
                            ${resp.user?.encodeAsHTML()}
                        </g:else>
                    </td>

                    <g:each in="${surveyInstance?.questions}" var="question">
                        <td>
                            <g:if test="${question?.type == QuestionType.TEXT}">
                                <span class="floating">${resp[question.id]?.encodeAsHTML()}</span>
                            </g:if>
                            <g:else>
                                <g:set var="selectedOptions" value="${resp[question.id]}"/>
                                <g:each in="${question?.options}" var="opt" status="opIndex">
                                    <g:if test="${opt.id in selectedOptions}">
                                        <span class="floating" title="${opt?.value}">(${alphabets[opIndex]})</span>
                                    </g:if>
                                </g:each>
                            </g:else>
                        </td>
                    </g:each>

                    <td>${resp.weight}</td>

                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
