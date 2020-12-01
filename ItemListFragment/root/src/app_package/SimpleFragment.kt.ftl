package ${escapeKotlinIdentifiers(packageName)}

import <#include "PackageName.ftl">.ui.base.presenter.ItemListFragmentPresenter
<#if (haveParameter!false)>
import <#include "ParameterPackage.ftl">.${parameterName}
</#if>
<#if (haveResponse!false)>
import <#include "ResponsePackage.ftl">.${responseName}
</#if>
<#if (generateItem!false)>
import ${itemPackage}.${itemName}
<#else>
import <#include "PackageName.ftl">.ui.base.listcell.Cell
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
class ${pageName}Fragment : ItemListFragmentPresenter<${pageName}Delegate, ${pageName}Delegate.Callback, <#include "Parameter.ftl">, <#include "ItemModel.ftl">, <#include "ResponseBody.ftl">>() {

    <#include "SetupMode.ftl">

    <#if (!isEnablePage)>
    override val isEnablePage = false
    </#if>

    <#include "InitialRequest.ftl">

    override fun getApiObservable(id: <#include "Parameter.ftl">, page: Int, size: Int): Observable<<#include "ResponseBody.ftl">> {
        TODO("not implemented")
    }

    override fun transformUIData(page: Int, itemList: MutableList<<#include "ItemModel.ftl">>, data: <#include "ResponseBody.ftl">) {
        <#if (generateItem!false)>
            <#if (!haveResponse || isListResponse!false)>
                itemList.addAll(data.map { <#include "ItemModel.ftl">(it) })
            <#else>
                TODO("not implemented")
            </#if>
        <#else>
            TODO("not implemented")
        </#if>
    }

    <#if (isEnablePage)>
        override fun addMoreData(initialData: <#include "ResponseBody.ftl">, data: <#include "ResponseBody.ftl">): <#include "ResponseBody.ftl"> {
            <#if (!haveResponse || isListResponse!false)>
                return initialData.apply {addAll(data)}
            <#else>
                TODO("not implemented")
            </#if>
        }
    </#if>

    <#if (isEnablePage && (!isListResponse))>
        override fun checkIfGotAllData(data: <#include "ResponseBody.ftl">): Boolean {
            TODO("not implemented")
        }
    </#if>

    override val viewCallback: ${pageName}Delegate.Callback by lazy { ${pageName}DelegateCallback() }

    private inner class ${pageName}DelegateCallback : ItemListDelegateCallbackImpl(), ${pageName}Delegate.Callback
}
