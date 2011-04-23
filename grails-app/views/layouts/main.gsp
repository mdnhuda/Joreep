<html>
<head>
    <title><g:layoutTitle default="Grails"/></title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}"/>
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
    <g:layoutHead/>
    <g:javascript library="application"/>
</head>
<body>
<div id="appLogo">
    <div style="width:100%;height:50px;background-color:darkblue;color:#b2d1ff;float:left;">
        <div style="width:20%;font-size:40px;font-weight:bold;float:left;text-align:center">
            Joreep
        </div >
        <div style="width:80%;height:50px;text-align:left;font-size:24px;font-weight:bold;background-color:#b2d1ff;color:darkblue;float:left">
            <span style="float:left;padding-left:20px;padding-top:20px;">free, online and flexible survey tool</span>            
        </div>
    </div>
    <div class="clearBoth"></div>

    %{--<g:link url="/"><img src="${resource(dir: 'images', file: 'joreep-logo.png')}" alt="Joreep" border="0"/></g:link>--}%
</div>
<g:if test="${session?.user}">
    <div class="floatRight padAround">
        ${session?.user?.email} &nbsp;|&nbsp; <a href="${createLink(controller: 'login', action: 'logout')}">Logout</a>
    </div>
</g:if>
<g:layoutBody/>

<div class="clearBoth">&nbsp;</div>
<div style="width:100%;background-color:darkblue;color:whitesmoke;float:left;margin:auto;">
    <div style="text-align:center;font-size:12px;padding-top:3px;padding-bottom:3px;">Developed and maintained by mdnhuda@gmail.com</div>
</div>
<div class="clearBoth">&nbsp;</div>

<div style="width:100%;margin-left:auto;margin-right:auto;">

<div style="float:left;padding-left:10px;"><img src="/images/appengine.gif" alt="Powered by Google AppEngine"></div>
<div style="float:right;padding-right:10px;"><img src="/images/grails_logo.png" alt="Developed with Grails"></div>
</div>
<div class="clearBoth">&nbsp;</div>

</body>
</html>