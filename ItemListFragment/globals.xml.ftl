<?xml version="1.0"?>
<globals>
    <#assign date = .now>
    <#assign domainDir = "domain/src/main/java">
    <#assign appDir ="app/src/main/java">
        
    <global id="hasNoActionBar" type="boolean" value="false" />
    <global id="parentActivityClass" value="" />
    <global id="simpleLayoutName" value="${layoutName}" />
    <global id="simpleItemLayoutName" value="${itemLayoutName}" />
    <global id="excludeMenu" type="boolean" value="true" />
    <global id="generateActivityTitle" type="boolean" value="false" />
    <global id="dateTime" type="String" value="${date?string["yyyy-MM-dd HH:mm:ss"]}" />
    <global id="author" type="String" value="kelin" />
    <global id="parameterOut" value="${domainDir}/${slashedPackageName(packageName?substring(0, packageName?index_of(".ui"))+".domain.argument")}" />
    <global id="responseOut" value="${domainDir}/${slashedPackageName(packageName?substring(0, packageName?index_of(".ui"))+".domain.model")}" />
    <global id="itemModelOut" value="${appDir}/${slashedPackageName(itemPackage)}" />
    <#include "../common/common_globals.xml.ftl" />
</globals>
