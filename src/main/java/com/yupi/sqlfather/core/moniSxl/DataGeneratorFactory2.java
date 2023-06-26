package com.yupi.sqlfather.core.moniSxl;

import com.yupi.sqlfather.core.generator.*;
import com.yupi.sqlfather.core.model.enums.MockTypeEnum;

import javax.swing.plaf.PanelUI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DataGeneratorFactory2 {


    /**
     * ，模拟类型 =》 生成器映射
     * 初始化块，{{}} 最里面是 匿名内部类
     */
    private static final Map<MockTypeEnum,DataGenerator> mockTypeDataGeneratorMap = new HashMap<MockTypeEnum, DataGenerator>(){{
          put(MockTypeEnum.NONE,new DefaultDataGenerator());
          put(MockTypeEnum.FIXED,new FixedDataGenerator());
          put(MockTypeEnum.RANDOM,new RandomDataGenerator());
          put(MockTypeEnum.RULE,new RuleDataGenerator());
          put(MockTypeEnum.DICT,new DictDataGenerator());
          put(MockTypeEnum.INCREASE,new IncreaseDataGenerator());
    }};




    private DataGeneratorFactory2() {
    }


    /**
     * 获取实例
     * @param mockTypeEnum
     * @return
     */
    public static DataGenerator getGenerator(MockTypeEnum mockTypeEnum) {

        //每个方法是一个独立的个体，还是应该做个校验的，
        mockTypeEnum = Optional.ofNullable(mockTypeEnum).orElse(MockTypeEnum.NONE);

        return mockTypeDataGeneratorMap.get(mockTypeEnum);
    }
}
