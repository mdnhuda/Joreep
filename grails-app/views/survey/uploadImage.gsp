<%@ page import="org.bd.survey.Survey" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Upload Image</title>
</head>
<body>

<g:render template="/common/navbar"/>

<div class="body">
    <h1>Upload Image</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <form action="<g:createLink controller='survey' action='processImageUpload'/>" method="post" enctype="multipart/form-data">
        <input type="hidden" name="id" value="${id}"/>
        <input type="file" name="myFile"/>
        <div class="buttons">
            <span class="button">
                <input class="save" type="submit" name="upload" value="Upload">
            </span>
        </div>
    </form>
</div>
</body>
</html>
