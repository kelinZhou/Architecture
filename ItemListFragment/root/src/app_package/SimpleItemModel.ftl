package ${escapeKotlinIdentifiers(itemPackage)}

<#if (itemClickable!false)>
import android.content.Context
</#if>
import <#include "PackageName.ftl">.ui.base.listcell.SimpleCell
import android.view.View
<#if (haveResponse!false)>
import <#include "ResponsePackage.ftl">.${responseName}
</#if>
import <#include "PackageName.ftl">.R
<#if (itemClickable!false)>
import <#include "PackageName.ftl">.utils.ToastUtil
</#if>
<#if (includeCppSupport!false) && generateItemLayout>
import kotlinx.android.synthetic.main.${itemLayoutName}.view.*
</#if>

/**
* **描述:** 封装 ${activityClass} 的ItemModel。
*
* **创建人:** ${author}
*
* **创建时间:** ${dateTime}
*
* **版本:** v 1.0.0
*/
class ${itemName}(val item: <#if (haveResponse!false)>${responseName}<#else>${responseType}</#if>) : SimpleCell() { 

    override val itemLayoutRes = <#if (generateItemLayout!false)>R.layout.${itemLayoutName}<#else>TODO("not implemented")</#if>

    override fun onBindData(iv: View) {
        TODO("not implemented")
    }

    <#if (itemClickable!false)>
        override val itemClickable = true

        override fun onItemClick(iv: View, context: Context, position: Int) {
            ToastUtil.showShortToast("${itemName}")
        }
    </#if>
}
