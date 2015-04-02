package ${entity.javaPackageDao};

import android.database.Cursor;
import android.net.Uri;
import android.content.UriMatcher;
import android.content.ContentResolver;

public final class ${entity.className}Helper {

    private ${entity.className}Helper() { }

    <#list entity.properties as property>
    public static final String ${property.propertyName?upper_case} = ${entity.className}Dao.Properties.${property.propertyName?cap_first}.columnName;
    </#list>

    public static final String TABLENAME = ${entity.classNameDao}.TABLENAME;
    public static final String PK = ${entity.classNameDao}.Properties.${entity.pkProperty.propertyName?cap_first}.columnName;

    <#assign counter = id>
    public static final int ${entity.className?upper_case}_DIR = ${counter};
    public static final int ${entity.className?upper_case}_ID = ${counter+1};

    public static final String BASE_PATH = "${entity.className?lower_case}";
    public static final Uri CONTENT_URI = Uri.parse("content://" + ${contentProvider.className}.AUTHORITY + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + BASE_PATH;


    public static void addURI(UriMatcher sURIMatcher) {
        sURIMatcher.addURI(${contentProvider.className}.AUTHORITY, BASE_PATH, ${entity.className?upper_case}_DIR);
        sURIMatcher.addURI(${contentProvider.className}.AUTHORITY, BASE_PATH + "/#", ${entity.className?upper_case}_ID);
    }

    private static final String[] PROJECTION = {
    <#list entity.properties as property>
        ${property.propertyName?upper_case}<#if property_has_next>,</#if>
    </#list>
    };

    public static String[] getProjection() {
        return PROJECTION;
    }

    public static ${entity.className} fromCursor(Cursor data) {
        final ${entity.className} entity = new ${entity.className}();
        <#list entity.properties as property>
        <#if property.propertyType?lower_case == "boolean">
        entity.set${property.propertyName?cap_first}(data.getInt(data.getColumnIndex(${property.propertyName?upper_case})) > 0);
        <#else>
        entity.set${property.propertyName?cap_first}(data.get${property.propertyType?cap_first}(data.getColumnIndex(${property.propertyName?upper_case})));
        </#if>
        </#list>
        return entity;
    }
}
