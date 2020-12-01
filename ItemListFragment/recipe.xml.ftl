<?xml version="1.0"?>
<#import "root://activities/common/kotlin_macros.ftl" as kt>
<recipe>
    <@kt.addAllKotlinDependencies />

<#if generateLayout>
    <#include "../common/recipe_simple.xml.ftl" />
    <open file="${escapeXmlAttribute(resOut)}/layout/${layoutName}.xml" />
</#if>
<#if generateItemLayout>
    <#include "recipe_item_layout_simple.xml.ftl" />
    <open file="${escapeXmlAttribute(resOut)}/layout/${itemLayoutName}.xml" />
</#if>

    <instantiate from="root/src/app_package/SimpleFragment.kt.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${pageName}Fragment.kt" />
                   
    <instantiate from="root/src/app_package/SimpleDelegate.kt.ftl"
                   to="${escapeXmlAttribute(srcOut)}/${pageName}Delegate.kt" />
                   
<#if haveParameter>
    <instantiate from="root/src/app_package/SimpleReq.kt.ftl"
    to="${escapeXmlAttribute(parameterOut)}/${parameterName}.kt" />
</#if>

<#if haveResponse>
    <instantiate from="root/src/app_package/SimpleResp.kt.ftl"
    to="${escapeXmlAttribute(responseOut)}/${responseName}.kt" />
</#if>

<#if generateItem>
    <instantiate from="root/src/app_package/SimpleItemModel.ftl"
    to="${escapeXmlAttribute(itemModelOut)}/${itemName}.kt" />
</#if>

    <open file="${escapeXmlAttribute(srcOut)}/${pageName}Fragment.kt" />

</recipe>
