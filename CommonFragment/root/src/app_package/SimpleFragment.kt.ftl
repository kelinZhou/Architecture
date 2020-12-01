package ${escapeKotlinIdentifiers(packageName)}

import <#include "PackageName.ftl">.ui.base.presenter.CommonFragmentPresenter
<#if (haveParameter!false)>
import <#include "ParameterPackage.ftl">.${parameterName}
</#if>
<#if (haveResponse!false)>
import <#include "ResponsePackage.ftl">.${responseName}
</#if>
import io.reactivex.Observable


/**
* **描述:** ${description}
*
* **创建人:** ${author}
*
* **创建时间:** ${dateTime}
*
* **版本:** v 1.0.0
*/
class ${pageName}Fragment : CommonFragmentPresenter<${pageName}Delegate, ${pageName}Delegate.Callback, <#include "Parameter.ftl">, <#include "ResponseBody.ftl">, <#include "ResponseBody.ftl">>() {

<#include "SetupMode.ftl">

<#include "InitialRequest.ftl">

    override fun getApiObservable(id: <#include "Parameter.ftl">): Observable<<#include "ResponseBody.ftl">> {
        TODO("not implemented")
    }

    override val viewCallback: ${pageName}Delegate.Callback by lazy { ${pageName}DelegateCallback() }

    private inner class ${pageName}DelegateCallback : CommonDelegateCallbackImpl(), ${pageName}Delegate.Callback
}
