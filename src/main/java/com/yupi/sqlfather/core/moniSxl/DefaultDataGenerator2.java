package com.yupi.sqlfather.core.moniSxl;

import cn.hutool.core.date.DateUtil;
import com.yupi.sqlfather.core.generator.DataGenerator;
import com.yupi.sqlfather.core.schema.TableSchema;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * none :没有模仿规则  数据生成器
 */
public class DefaultDataGenerator2   {




    public List<String> doGenerate(TableSchema.Field field, int rowNum) {

        String mockParams = field.getMockParams();
        ArrayList<String> list = new ArrayList<>(rowNum);
        //这块逻辑，是啥？

        //主键采用递增策略
        if (field.isPrimaryKey()){
            if (StringUtils.isBlank(mockParams)){
                mockParams = "1";
            }
            int initValue = Integer.parseInt(mockParams);
            for (int i = 0; i < rowNum; i++) {
                list.add(String.valueOf(initValue+i));
            }
            return list;
        }

        //使用默认值
        String defaultValue = field.getDefaultValue();
        //特殊逻辑，日期要伪造数据, 相当于 日期就不用默认值，使用当前时间
        if ("CURRENT_TIMESTAMP".equals(defaultValue)){
             defaultValue = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        }
        if (StringUtils.isNotBlank(defaultValue)){
            for (int i = 0; i < rowNum; i++) {
                list.add(defaultValue);
            }
        }
        return list;
    }
}
