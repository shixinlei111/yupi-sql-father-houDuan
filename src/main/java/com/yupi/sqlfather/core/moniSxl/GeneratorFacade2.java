package com.yupi.sqlfather.core.moniSxl;

import cn.hutool.core.util.StrUtil;
import com.yupi.sqlfather.core.builder.*;
import com.yupi.sqlfather.core.model.dto.JavaEntityGenerateDTO;
import com.yupi.sqlfather.core.model.dto.JavaObjectGenerateDTO;
import com.yupi.sqlfather.core.model.dto.TypescriptTypeGenerateDTO;
import com.yupi.sqlfather.core.model.enums.FieldTypeEnum;
import com.yupi.sqlfather.core.model.vo.GenerateVO;
import com.yupi.sqlfather.core.schema.TableSchema;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class GeneratorFacade2 {


    private static Configuration configuration;

    @Resource
    public void setConfiguration(Configuration configuration) {
        GeneratorFacade2.configuration = configuration;
    }



    public static GenerateVO generateAll(TableSchema tableSchema){
        validSchema(tableSchema);

        SqlBuilder2 sqlBuilder = new SqlBuilder2();
        //构造建表sql
        String createSql = sqlBuilder.buildCreateTableSql(tableSchema);

        //生成模拟数据
        Integer mockNum = tableSchema.getMockNum();
        List<Map<String, Object>> dataList = DataBuilder.generateData(tableSchema, mockNum);

        //生成插入sql,没看到啊,这个生成在哪？
        String insertSql = sqlBuilder.buildInsertSql(tableSchema, dataList);
        //生成数据json
        String dataJson = JsonBuilder.buildJson(dataList);
        //生成 java实体代码
        String javaEntityCode = JavaCodeBuilder.buildJavaEntityCode(tableSchema);
        //生成 java 对象代码
        String javaObjectCode = JavaCodeBuilder.buildJavaObjectCode(tableSchema, dataList);
        //生成 typescript类型代码
        String typeScriptTypeCode = FrontendCodeBuilder.buildTypeScriptTypeCode(tableSchema);
        //封装返回
        GenerateVO generateVO = new GenerateVO();
        generateVO.setTableSchema(tableSchema)
                .setCreateSql(createSql)
                .setDataList(dataList)
                .setInsertSql(insertSql)
                .setDataJson(dataJson)
                .setJavaEntityCode(javaEntityCode)
                .setJavaObjectCode(javaObjectCode)
                .setTypescriptTypeCode(typeScriptTypeCode)
        ;
        return generateVO;
    }


    private static void validSchema(TableSchema tableSchema) {

    }

    /**
     * 构造 Java 实体代码
     *
     * @param tableSchema 表概要
     * @return 生成的 java 代码
     */
    @SneakyThrows
    public static String buildJavaEntityCode(TableSchema tableSchema) {

        JavaEntityGenerateDTO javaEntityGenerateDTO = new JavaEntityGenerateDTO();
        String tableName = tableSchema.getTableName();
        String tableComment = tableSchema.getTableComment();
        String upperCamelTableName = StringUtils.capitalize(StrUtil.toCamelCase(tableName));
        //类名 大写的，驼峰的 表名
        javaEntityGenerateDTO.setClassName(upperCamelTableName);
        //类注释 ，表注释 》表名
        javaEntityGenerateDTO.setClassComment(Optional.ofNullable(tableComment).orElse(upperCamelTableName));
        //依次填充每一列
        ArrayList<JavaEntityGenerateDTO.FieldDTO> fieldDTOList = new ArrayList<>();
        for (TableSchema.Field field : tableSchema.getFieldList()) {
            JavaEntityGenerateDTO.FieldDTO fieldDTO = new JavaEntityGenerateDTO.FieldDTO();
            fieldDTO.setComment(field.getComment());
            FieldTypeEnum fieldTypeEnum = Optional.ofNullable(FieldTypeEnum.getEnumByValue(field.getFieldType())).orElse(FieldTypeEnum.TEXT);
            fieldDTO.setJavaType(fieldTypeEnum.getJavaType());
            fieldDTO.setFieldName(StrUtil.toCamelCase(field.getFieldName()));
            fieldDTOList.add(fieldDTO);
        }
        javaEntityGenerateDTO.setFieldList(fieldDTOList);
        StringWriter stringWriter = new StringWriter();
        Template template = configuration.getTemplate("java_entity.ftl");
        template.process(javaEntityGenerateDTO,stringWriter);
        return stringWriter.toString();
    }


    /**
     * 构造 Typescript 类型代码
     *
     * @param tableSchema 表概要
     * @return 生成的代码
     */
    @SneakyThrows
    public static String buildTypeScriptTypeCode(TableSchema tableSchema) {

        TypescriptTypeGenerateDTO generateDTO = new TypescriptTypeGenerateDTO();
        String tableName = tableSchema.getTableName();

        String tableComment = tableSchema.getTableComment();
        String upperCamelTableName = StringUtils.capitalize(StrUtil.toCamelCase(tableName));
        // 类名为大写的表名
        generateDTO.setClassName(upperCamelTableName);

        // 类注释为表注释 > 表名
        generateDTO.setClassComment(Optional.ofNullable(tableComment).orElse(upperCamelTableName));

        //依次填充每一列
        List<TypescriptTypeGenerateDTO.FieldDTO> fieldDTOList = new ArrayList<>();
        for (TableSchema.Field field : tableSchema.getFieldList()) {
            TypescriptTypeGenerateDTO.FieldDTO fieldDTO = new TypescriptTypeGenerateDTO.FieldDTO();
            fieldDTO.setComment(field.getComment());
            FieldTypeEnum fieldTypeEnum = Optional.ofNullable(FieldTypeEnum.getEnumByValue(field.getFieldType())).orElse(FieldTypeEnum.TEXT);
            fieldDTO.setTypescriptType(fieldTypeEnum.getTypescriptType());
            fieldDTO.setFieldName(StrUtil.toCamelCase(field.getFieldName()));
            fieldDTOList.add(fieldDTO);
        }
        generateDTO.setFieldList(fieldDTOList);
        StringWriter stringWriter = new StringWriter();
        Template temp = configuration.getTemplate("typescript_type.ftl");
        temp.process(generateDTO, stringWriter);
        return stringWriter.toString();
    }
}
