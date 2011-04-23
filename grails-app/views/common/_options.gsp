<g:hiddenField name="numberOfOptions" value="${questionInstance.options?.size()}"/>
<table>
    <g:each in="${questionInstance.options}" var="op" status="index">
        <tr>
            <td style="vertical-align:middle;">
                <g:textField name="options[${index}].value" value="${op.value}"/>
            </td>
            <td style="vertical-align:middle;">
                <g:select name="options[${index}].weight" from="${0..10}" value="${op.weight}"/>
            </td>
        </tr>
    </g:each>
    <tr>
        <td colspan="2">
            <div class="buttons">
                <span class="button">
                    <g:select name="numberOfOptionsToAdd" from="${0..10}" value="0"/>
                    <g:actionSubmit class="edit" value="Add Options" action="addOptions" onclick="return preAddOptions();"/>
                </span>
                <span class="button">
                    <g:actionSubmit class="edit" value="Copy Options from Previous Question" action="copyOptionsFromPreviousQuestion"/>
                </span>
            </div>
        </td>
    </tr>
</table>
