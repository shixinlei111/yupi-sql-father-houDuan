package com.yupi.sqlfather.core.moniSxl;

import com.yupi.sqlfather.core.generator.DataGenerator;
import com.yupi.sqlfather.core.generator.DataGeneratorFactory;
import com.yupi.sqlfather.core.model.enums.MockTypeEnum;
import com.yupi.sqlfather.core.schema.TableSchema;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class DataBuild2 {


    //生成数据
    public static List<Map<String, Object>> generateData(TableSchema tableSchema, int rowNum){

        //初始化 结果数据
        List<TableSchema.Field> fieldList = tableSchema.getFieldList();
        ArrayList<Map<String,Object>> resultList = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            resultList.add(new HashMap<>());
        }
        //依次生成每一列
        for (TableSchema.Field field : fieldList) {
            //根据字符串内容 得到 类型
            MockTypeEnum mockTypeEnum = Optional.ofNullable(MockTypeEnum.getEnumByValue(field.getMockType())).orElse(MockTypeEnum.NONE);
            //数据生成器
            DataGenerator generator = DataGeneratorFactory2.getGenerator(mockTypeEnum);
            List<String> mockDataList = generator.doGenerate(field, rowNum);

            String fieldName = field.getFieldName();
            if (!CollectionUtils.isEmpty(mockDataList)){
                for (int i = 0; i < rowNum; i++) {
                    resultList.get(i).put(fieldName,mockDataList.get(i));
                }
            }
        }
        return  resultList;
    }
}
