<#if (haveResponse!false)><#if (isListResponse!false)><#if (isEnablePage!false)>MutableList<${responseName}><#else>List<${responseName}></#if><#else>${responseName}</#if><#else><#if (isEnablePage!false)>MutableList<${responseType}><#else>List<${responseType}></#if></#if>