package ${escapeKotlinIdentifiers(packageName)}

import <#include "PackageName.ftl">.ui.base.delegate.ItemListDelegate
<#if (generateItem!false)>
import ${itemPackage}.${itemName}
<#else>
import <#include "PackageName.ftl">.ui.base.listcell.Cell
</#if>
<#if (setupMode!= "SETUP_DEFAULT" && setupMode!= "SETUP_TIMELY")>
import <#include "PackageName.ftl">.widget.statelayout.StatePage
</#if>
<#if (generateLayout!false)>
import <#include "PackageName.ftl">.R
</#if>
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
class ${pageName}Delegate : ItemListDelegate<${pageName}Delegate.Callback, <#include "ItemModel.ftl">>() {

    <#include "PageStateFlags.ftl">

    <#if (generateLayout!false)>
        override val rootLayoutId = R.layout.${layoutName}
    </#if>

    interface Callback : ItemListDelegateCallback<<#include "ItemModel.ftl">>
}
