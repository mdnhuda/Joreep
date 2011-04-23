<%@ page import="org.bd.survey.QuestionType; org.bd.survey.Question" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Choose Login Mechanism</title>

</head>
<body>

<div class="body">
    <h1>Choose Login Mechanism</h1>
    <g:form>
        <g:hiddenField name="redirectTo" value="${params.redirectTo ?: '/'}"/>
        <div class="buttons">
            <span class="button"><g:actionSubmit value="Use your Google Account to Login" action="loginByGoogle"/></span>
        </div>
%{--
        <div class="buttons">
            <span class="button"><g:actionSubmit value="Use your Facebook Account to Login" action="loginByFacebook"/></span>
        </div>
--}%
    </g:form>
</div>
</body>
</html>
