package com.yupi.sqlfather.core.moniSxl;

import com.sun.xml.internal.fastinfoset.util.ValueArrayResourceException;
import com.yupi.sqlfather.core.model.enums.MockParamsRandomTypeEnum;
import com.yupi.sqlfather.core.model.enums.MockTypeEnum;
import com.yupi.sqlfather.core.schema.TableSchema;
import com.yupi.sqlfather.core.utils.FakerUtils;
import net.datafaker.Faker;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 固定值 数据生成器
 */
public class FixedDataGenerator2 {

    private  static final Faker ZH_FAKER = new Faker(new Locale("zh-CN"));


    public List<String> doGenerate(TableSchema.Field field, int rowNum) {
        //规则为固定的时候，，就直接用这个  mockParam
//        String mockParams = field.getMockParams();
//        if (StringUtils.isBlank(mockParams)){
//            mockParams = "6";
//        }
//        ArrayList<String> list = new ArrayList<>();
//        for (int i = 0; i < rowNum; i++) {
//            list.add(mockParams);
//        }
//        return list;


        //随机值数据生成器
        // 没选择的时候，传过来的是 空，
        String mockParams1 = field.getMockParams();
        ArrayList<String> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            MockParamsRandomTypeEnum paramTypeEnum = Optional.ofNullable(
                            MockParamsRandomTypeEnum.getEnumByValue(mockParams1))
                    .orElse(MockParamsRandomTypeEnum.STRING);

            String randomStr = FakerUtils.getRandomValue(paramTypeEnum);
            list.add(randomStr);
        }
        return list;
    }
}
