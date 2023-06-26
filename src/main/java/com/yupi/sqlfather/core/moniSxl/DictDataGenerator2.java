package com.yupi.sqlfather.core.moniSxl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.asm.Type;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.yupi.sqlfather.common.ErrorCode;
import com.yupi.sqlfather.core.schema.TableSchema;
import com.yupi.sqlfather.exception.BusinessException;
import com.yupi.sqlfather.model.entity.Dict;
import com.yupi.sqlfather.service.DictService;
import com.yupi.sqlfather.utils.SpringContextUtils;
import org.apache.commons.lang3.RandomUtils;


import java.util.ArrayList;
import java.util.List;

/**
 * 词库数据 生成器
 */
public class DictDataGenerator2 {

    private static final DictService dictService = SpringContextUtils.getBean(DictService.class);


    private static final Gson gson = new Gson();

    public List<String> doGenerate(TableSchema.Field field, int rowNum) {
        //模仿数据 ，此时为词库 ，就是选 第几个 词库，按照顺序
        String mockParams = field.getMockParams();
        //词库id
        long id = Long.parseLong(mockParams);
        Dict dict = dictService.getById(id);
        if (dict == null){
            throw  new BusinessException(ErrorCode.NOT_FOUND_ERROR,"词库不存在");
        }
        //得到 泛型 的 class对象。
        List<String> wordList = gson.fromJson(dict.getContent(),
                    new TypeToken<List<String>>() {
                       }.getType());

        ArrayList<String> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            //随机拿到的字符串
            String randomStr = wordList.get(RandomUtils.nextInt(0, wordList.size()));
            list.add(randomStr);
        }

        return list;
    }

}
