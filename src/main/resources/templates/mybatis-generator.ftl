<!DOCTYPE generatorConfiguration PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>
    <context id="simple" targetRuntime="${generateType?string("MyBatis3", "MyBatis3Simple")}">
        <plugin type="com.zengdw.mybatis.plugins.LombokPlugin">
            <property name="hasLombok" value="${needLombok?c}"/>
        </plugin>
        <plugin type="org.mybatis.generator.plugins.SerializablePlugin">
            <property name="suppressJavaInterface" value="false"/>
        </plugin>
        <plugin type="com.zengdw.mybatis.plugins.EmptyStrPlugin"/>

        <commentGenerator type="com.zengdw.mybatis.generator.CommentGenerator"/>

        <jdbcConnection driverClass="${driverClass}" connectionURL="${url}" userId="${userName}" password="${password}">
            <#--获取表注释-->
            <#if dataType == 'oracle'>
                <property name="remarksReporting" value="true" />
            <#elseif dataType == 'mysql'>
                <property name="useInformationSchema" value="true" />
            </#if>
        </jdbcConnection>

        <javaTypeResolver>
            <property name="useJSR310Types" value="true"/>
        </javaTypeResolver>

        <javaModelGenerator targetPackage="${modelPath}" targetProject="${projectPath}/src/main/java"/>

        <sqlMapGenerator targetPackage="${mapperXmlPath}" targetProject="${projectPath}/src/main/java"/>

        <javaClientGenerator type="XMLMAPPER" targetPackage="${mapperPath}" targetProject="${projectPath}/src/main/java"/>

        <#list tables as table>
            <table tableName="${table}" />
        </#list>

    </context>
</generatorConfiguration>