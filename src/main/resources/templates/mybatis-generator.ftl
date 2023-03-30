<!DOCTYPE generatorConfiguration PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>
    <context id="simple" targetRuntime="${example?string("MyBatis3", "MyBatis3Simple")}">
        <plugin type="com.zengdw.mybatis.plugins.LombokPlugin">
            <property name="hasLombok" value="${lombok?c}"/>
        </plugin>
        <#if serializable??>
            <plugin type="org.mybatis.generator.plugins.SerializablePlugin">
                <property name="suppressJavaInterface" value="false"/>
            </plugin>
        </#if>
        <plugin type="com.zengdw.mybatis.plugins.EmptyStrPlugin"/>

        <#if comment??>
            <commentGenerator type="com.zengdw.mybatis.generate.CommentGenerator"/>
        </#if>

        <#--<jdbcConnection driverClass="${driverClass}" connectionURL="${url}" userId="${userName}" password="${password}">
            &lt;#&ndash;获取表注释&ndash;&gt;
            <#if dataType == 'oracle'>
                <property name="remarksReporting" value="true"/>
            <#elseif dataType == 'mysql'>
                <property name="useInformationSchema" value="true"/>
            </#if>
        </jdbcConnection>-->

        <javaTypeResolver>
            <property name="useJSR310Types" value="true"/>
        </javaTypeResolver>

        <javaModelGenerator targetPackage="${javaModelPackage}" targetProject="${modulePath}/${javaModelPath}"/>

        <sqlMapGenerator targetPackage="${mapperXmlPackage}" targetProject="${modulePath}/${mapperXmlPath}"/>

        <javaClientGenerator type="XMLMAPPER" targetPackage="${mapperPackage}"
                             targetProject="${modulePath}/${mapperPath}"/>

        <#--<#list tables as table>
            <table tableName="${table}"/>
        </#list>-->

    </context>
</generatorConfiguration>