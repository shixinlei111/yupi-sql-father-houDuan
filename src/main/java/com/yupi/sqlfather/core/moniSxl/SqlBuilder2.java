package com.yupi.sqlfather.core.moniSxl;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.yupi.sqlfather.common.ErrorCode;
import com.yupi.sqlfather.core.builder.sql.SQLDialect;
import com.yupi.sqlfather.core.model.enums.FieldTypeEnum;
import com.yupi.sqlfather.core.model.enums.MockTypeEnum;
import com.yupi.sqlfather.core.schema.TableSchema;
import com.yupi.sqlfather.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SqlBuilder2 {

    private SQLDialect sqlDialect;


    /**
     * 构造建表sql
     * @param tableSchema
     * @return
     */
    public String buildCreateTableSql(TableSchema tableSchema) {
        //构造模板
        String template = "%s\n"
                + "create table if not exists %s\n"
                + "(\n"
                + "%s\n"
                + ") %s;";
        //构造表名
        String tableName = sqlDialect.wrapTableName(tableSchema.getTableName());
        String dbName = tableSchema.getDbName();
        //例子： db1.`table1`
        if (StringUtils.isNotBlank(dbName)){
            tableName = String.format("%s.%s",dbName,tableName);
        }
        //构造表前缀注释
        String tableComment = tableSchema.getTableComment();
        if (StringUtils.isBlank(tableComment)){
            tableComment = tableName;
        }

        String tablePrefixComment = String.format("-- %s", tableComment);
        //构造表后缀注释
        String tableSuffixComment = String.format("comment '%s'", tableComment);
        //构造表字段
        List<TableSchema.Field> fieldList = tableSchema.getFieldList();
        StringBuilder fieldStrBuilder = new StringBuilder();
        int fieldSize = fieldList.size();
        for (int i = 0; i < fieldSize; i++) {
            TableSchema.Field field = fieldList.get(i);
            fieldStrBuilder.append(buildCreateFieldSql(field));
        }


        return null;
    }


    /**
     * 生成创建字段的  sql
     * @param field
     * @return
     */
    private String buildCreateFieldSql(TableSchema.Field field) {
        if (field == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //给字段名加一个符号   ``
        String fieldName = sqlDialect.wrapFieldName(field.getFieldName());
        String fieldType = field.getFieldType();
        String defaultValue = field.getDefaultValue();
        boolean notNull = field.isNotNull();
        String comment = field.getComment();
        String onUpdate = field.getOnUpdate();
        boolean primaryKey = field.isPrimaryKey();
        boolean autoIncrement = field.isAutoIncrement();
        // e.g. column_name int default 0 not null auto_increment comment '注释' primary key,
        StringBuilder fieldStrBuilder = new StringBuilder();
        // 字段名
        fieldStrBuilder.append(fieldName);
        // 字段类型
        fieldStrBuilder.append(" ").append(fieldType);
        // 默认值
        if (StringUtils.isNotBlank(defaultValue)) {
            fieldStrBuilder.append(" ").append("default ").append(getValueStr(field, defaultValue));
        }
        // 是否非空
        fieldStrBuilder.append(" ").append(notNull ? "not null" : "null");
        // 是否自增
        if (autoIncrement) {
            fieldStrBuilder.append(" ").append("auto_increment");
        }
        // 附加条件
        if (StringUtils.isNotBlank(onUpdate)) {
            fieldStrBuilder.append(" ").append("on update ").append(onUpdate);
        }
        // 注释
        if (StringUtils.isNotBlank(comment)) {
            fieldStrBuilder.append(" ").append(String.format("comment '%s'", comment));
        }
        // 是否为主键
        if (primaryKey) {
            fieldStrBuilder.append(" ").append("primary key");
        }
        return fieldStrBuilder.toString();
    }


    /**
     * 根据列的属性获取值字符串
     * @param field
     * @param value
     * @return
     */
    private String getValueStr(TableSchema.Field field, Object value) {
        if (field == null || value == null){
            return "''";
        }

        FieldTypeEnum fieldTypeEnum = Optional.ofNullable(FieldTypeEnum.getEnumByValue(field.getFieldType()))
                .orElse(FieldTypeEnum.TEXT);
        String result = String.valueOf(value);
        switch (fieldTypeEnum){
            case DATETIME:
            case TIMESTAMP:
                return result.equalsIgnoreCase("CURRENT_TIMESTAMP") ? result : String.format("'%s'", value);
            case DATE:
            case TIME:
            case CHAR:
            case VARCHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
            case BINARY:
            case VARBINARY:
                return String.format("'%s'", result);
            default:
                return result;
        }

    }


    /**
     * 构造插入数据 sql
     * @param tableSchema
     * @param dataList
     * @return
     */
    public String buildInsertSql(TableSchema tableSchema, List<Map<String, Object>> dataList) {

        //构造模板
        String template = "insert into %s (%s) values(%s);";
        //构造表名
        String tableName = sqlDialect.wrapTableName(tableSchema.getTableName());
        String dbName = tableSchema.getDbName();
        if (StringUtils.isNotBlank(dbName)){
            tableName = String.format("%s.%s",dbName,tableName);
        }

        //构造表字段
        List<TableSchema.Field> fieldList = tableSchema.getFieldList();
        fieldList = fieldList.stream().filter(field -> {
            MockTypeEnum mockTypeEnum = Optional.ofNullable(MockTypeEnum.getEnumByValue(field.getMockType()))
                    .orElse(MockTypeEnum.NONE);
            return !MockTypeEnum.NONE.equals(mockTypeEnum);
        }).collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();
        int total = dataList.size();
        for (int i = 0; i < total; i++) {
            Map<String, Object> dataRow = dataList.get(i);
            String keyStr = fieldList.stream()
                    .map(field -> sqlDialect.wrapFieldName(field.getFieldName()))
                    .collect(Collectors.joining(", "));

            String valueStr = fieldList.stream()
                    .map(field -> getValueStr(field, dataRow.get(field.getFieldName())))
                    .collect(Collectors.joining(", "));

            String result = String.format(template, tableName, keyStr, valueStr);
            stringBuilder = new StringBuilder();
            // 最后一个字段后没有换行
            if (i != total - 1) {
                stringBuilder.append("\n");
            }
        }

        return stringBuilder.toString();
    }
}
