package com.yupi.sqlfather.core.moniSxl;


import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlCreateTableParser;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.gson.Gson;
import com.yupi.sqlfather.common.ErrorCode;
import com.yupi.sqlfather.core.builder.sql.MySQLDialect;
import com.yupi.sqlfather.core.schema.TableSchema;
import com.yupi.sqlfather.exception.BusinessException;
import com.yupi.sqlfather.model.entity.FieldInfo;
import com.yupi.sqlfather.service.FieldInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TableSchemaBuilder2 {

    private  static final Gson gson = new Gson();
    @Autowired
    private FieldInfoService fieldInfoService;

    private  final MySQLDialect sqlDialect = new MySQLDialect();

    /**
     * 智能构建
     *
     * @param content
     * @return
     */
    public  TableSchema buildFromAuto(String content) {

        if (StringUtils.isBlank(content)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //切分单词
        String[] words = content.split("[,，]");
        if (ArrayUtils.isEmpty(words)|| words.length > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //根据单词去词库里匹配列信息，未匹配到的使用默认值
        QueryWrapper<FieldInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("name", Arrays.asList(words)).or().in("fieldName", Arrays.asList(words));

        List<FieldInfo> fieldInfoList = fieldInfoService.list(queryWrapper);
        Map<String, List<FieldInfo>> nameFieldInfoMap = fieldInfoList.stream().collect(Collectors.groupingBy(FieldInfo::getName));
        Map<String, List<FieldInfo>> fieldNameFieldInfoMap = fieldInfoList.stream().collect(Collectors.groupingBy(FieldInfo::getName));

        TableSchema tableSchema = new TableSchema();
        tableSchema.setTableName("my_table");
        tableSchema.setTableComment("自动生成的表");
        List<TableSchema.Field> fields = new ArrayList<>();
        for (String word : words) {
            TableSchema.Field field = null;
            List<FieldInfo> infoList = Optional.ofNullable(nameFieldInfoMap.get(word))
                    .orElse(fieldNameFieldInfoMap.get(word));
            if (CollectionUtils.isNotEmpty(infoList)){
                field = gson.fromJson(infoList.get(0).getContent(), TableSchema.Field.class);
            }
            fields.add(field);
        }

        tableSchema.setFieldList(fields);
        return tableSchema;
    }


    /**
     * 根据建表 SQL 构建
     *
     * @param sql 建表 SQL
     * @return 生成的 TableSchema
     */
    public TableSchema buildFromSql(String sql) {

        if (StringUtils.isBlank(sql)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //alibaba  解析 建表 sql语句  TODO 这个怎么用的？
        MySqlCreateTableParser parser = new MySqlCreateTableParser(sql);
        SQLCreateTableStatement sqlCreateTableStatement = parser.parseCreateTable();
        //构造result
        TableSchema tableSchema = new TableSchema();
        tableSchema.setDbName(sqlCreateTableStatement.getSchema());
        tableSchema.setTableName(sqlDialect.parseTableName(sqlCreateTableStatement.getTableName()));
        String tableComment = null;
        if (sqlCreateTableStatement.getComment() != null) {
            tableComment = sqlCreateTableStatement.getComment().toString();
            if (tableComment.length() > 2) {
                tableComment = tableComment.substring(1, tableComment.length() - 1);
            }
        }
        tableSchema.setTableComment(tableComment);

        List<TableSchema.Field> fieldList = new ArrayList<>();
        //解析列
        List<SQLTableElement> tableElementList = sqlCreateTableStatement.getTableElementList();
        for (SQLTableElement sqlTableElement : tableElementList) {
            if (sqlTableElement instanceof SQLPrimaryKey){
                SQLPrimaryKey sqlPrimaryKey = (SQLPrimaryKey) sqlTableElement;
                String primaryFieldName = sqlDialect.parseFieldName(sqlPrimaryKey.getColumns().get(0).toString());
                for (TableSchema.Field field : fieldList) {
                    if (primaryFieldName.equals(field.getFieldName())){
                        field.setPrimaryKey(true);
                    }
                    break;
                }

            }
            //就是普通列吧，
            else if(sqlTableElement instanceof SQLColumnDefinition){
                SQLColumnDefinition columnDefinition = (SQLColumnDefinition) sqlTableElement;

                TableSchema.Field field = new TableSchema.Field();

            }
        }

        return null;
    }
}
