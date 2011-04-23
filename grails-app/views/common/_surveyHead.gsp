<div style="width:720px;">
    <g:if test="${surveyInstance?.headerImage}">
        <img style="border:solid black" src="${createLink(controller:'survey', action:'showImage', id:surveyInstance?.headerImage?.id)}" title="${surveyInstance?.name}" alt="${surveyInstance?.name}"/>
    </g:if>
    <g:else>
        <h1>${surveyInstance?.name?.encodeAsHTML()}</h1>
    </g:else>
    <g:if test="${surveyInstance?.description?.value}">
        <div class="description">${surveyInstance?.description?.value?.encodeAsHTML()} - Created by ${surveyInstance?.createdBy?.encodeAsHTML()}</div>
    </g:if>
</div>
