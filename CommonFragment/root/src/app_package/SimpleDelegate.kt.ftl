package ${escapeKotlinIdentifiers(packageName)}

import <#include "PackageName.ftl">.ui.base.delegate.CommonViewDelegate
<#if (setupMode!= "SETUP_DEFAULT" && setupMode!= "SETUP_TIMELY")>
import <#include "PackageName.ftl">.widget.statelayout.StatePage
</#if>
import <#include "PackageName.ftl">.R
<#if (haveResponse!false)>
import <#include "ResponsePackage.ftl">.${responseName}
</#if>
<#if (includeCppSupport!false) && generateLayout>
import kotlinx.android.synthetic.main.${layoutName}.*
</#if>


/**
* **描述:** ${description}
*
* **创建人:** ${author}
*
* **创建时间:** ${dateTime}
*
* **版本:** v 1.0.0
*/
class ${pageName}Delegate : CommonViewDelegate<${pageName}Delegate.Callback, <#include "ResponseBody.ftl">>() {

    <#include "PageStateFlags.ftl">

    override val rootLayoutId = R.layout.${layoutName}

    override fun setInitialData(data: <#include "ResponseBody.ftl">) {
        TODO("not implemented")
    }

    interface Callback : CommonViewDelegateCallback
}
