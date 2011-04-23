<%@ page import="org.bd.survey.Survey" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Survey Home</title>
</head>
<body>

<g:render template="/common/navbar"/>

<div class="body">

    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>

    <div class="dialog">
        <div class="clearBoth">&nbsp;</div>

        <div class="list">
            <g:set var="columns" value="${['name':true, 'status':false, 'creator':true, 'dateCreated':true]}"/>
            <g:render template="/common/surveyList" model="${[surveyInstanceList: openSurveyInstanceList, tableCaption:'Open Survey(s)', columns:columns, showSurveyResponseLink:true]}"/>
        </div>

        <div class="clearBoth">&nbsp;</div>

        <div class="list">
            <g:set var="columns" value="${['name':true, 'status':true, 'creator':false, 'dateCreated':true, 'reportLink':true]}"/>
            <g:render template="/common/surveyList" model="${[surveyInstanceList: mySurveyInstanceList, tableCaption:'My Survey(s)', columns:columns, showSurveyLink:true]}"/>
        </div>

        <div class="clearBoth">&nbsp;</div>

        <div class="list">
            <g:set var="columns" value="${['name':true, 'status':true, 'creator':false, 'dateCreated':true, 'reportLink':true]}"/>
            <g:render template="/common/surveyList" model="${[surveyInstanceList: publicSurveyInstanceList, tableCaption:'See Results of Other Public Survey(s)', columns:columns]}"/>
        </div>

    </div>

</div>
</body>
</html>
