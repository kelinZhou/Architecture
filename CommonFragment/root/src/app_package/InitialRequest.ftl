<#if (haveParameter!false)>
override val initialRequestId: ${parameterName} by lazy { ${parameterName}() }
<#else>
override val initialRequestId: ${parameterType} = <#switch parameterType>
<#case "Any">
defaultAny
<#break>
<#case "Boolean">
true
<#break>
<#case "String">
""
<#break>
<#case "Int">
0
<#break>
<#case "Long">
0
<#break>
<#default>
emptyList()
</#switch>
</#if>
