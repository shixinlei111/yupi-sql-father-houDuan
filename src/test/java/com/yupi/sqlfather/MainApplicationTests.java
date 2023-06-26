package com.yupi.sqlfather;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class MainApplicationTests {

    @Test
    void contextLoads() {

    }

    @Test
    public void test1(){
//        String  id = "[{\"children\":[],\"factoryName\":\"二区121\",\"id\":\"DPD59481297\",\"isFactory\":\"1\"},{\"children\":[],\"" +
//                "factoryName\":\"111厂\",\"id\":\"DPJ82663958\",\"isFactory\":\"1\"},{\"children\":[],\"factoryName\":\"一厂\"," +
//                "\"id\":\"DPD22483722\",\"isFactory\":\"1\"},{\"children\":[],\"factoryName\":\"二厂\",\"id\":\"DPE96908914\",\"isFactory\":" +
//                "\"1\"},{\"children\":[],\"factoryName\":\"测试\",\"id\":\"DPW38667550\",\"isFactory\":\"1\"},{\"children\":[{\"children\":[]," +
//                "\"factoryName\":\"内置联合站\",\"id\":\"DPC23221876\",\"isFactory\":\"3\"}],\"factoryName\":\"内置厂\",\"id\":\"DPY23639405\",\"" +
//                "isFactory\":\"1\"},{\"children\":[],\"factoryName\":\"新增测试组织\",\"id\":\"DPA17676522\",\"isFactory\":\"3\"},{\"children\":[],\"" +
//                "factoryName\":\"测试新增厂\",\"id\":\"DPV63065569\",\"isFactory\":\"1\"},{\"children\":[],\"factoryName\":\"111\",\"id\":\"DPI44816876\"," +
//                "\"isFactory\":\"1\"}]";
        ArrayList<FactoryInfo> child1 = new ArrayList<>();
        ArrayList<FactoryInfo> child2 = new ArrayList<>();

        ArrayList<FactoryInfo> origin = new ArrayList<>();
        FactoryInfo one = new FactoryInfo("DPD59481297", "二区121", "1", child1);
        origin.add(one);

        FactoryInfo one2 = new FactoryInfo("DPJ82663958", "111厂", "1", null);
        FactoryInfo one3 = new FactoryInfo("DPD22483722", "一厂", "1", null);
        FactoryInfo one4 = new FactoryInfo("DPW38667550", "内置联合站2", "1", null);

        FactoryInfo one13 = new FactoryInfo("DPJ8266395822", "111厂2", "1", null);
        FactoryInfo one14 = new FactoryInfo("DPD2248372222", "一厂2", "1", null);
        FactoryInfo one15 = new FactoryInfo("DPW3866755022", "内置联合站2", "1", null);



        child1.add(one3);
        child1.add(one2);
        child1.add(one4);
        child2.add(one13);
        child2.add(one14);
        child2.add(one15);
        one4.setChildren(child2);



        GetStationSeparatorListVo result = new GetStationSeparatorListVo(1, "1号", 0, null, "DPD2248372222");
        getoneFactoryName(result,"DPD2248372222",origin);
        System.out.printf("结果:"+result.getFactoryName());

    }


    /**
     * 递归搜索子联合站 名字
     * @param one
     * @param groupId1
     * @param factoryInfos
     */
    private void getoneFactoryName(GetStationSeparatorListVo one, String groupId1, List<FactoryInfo> factoryInfos) {

        if (one.getFactoryName() != null) {
            return;
        }
        if (factoryInfos != null && !factoryInfos.isEmpty()) {
            //遍历获取每个分离器所在的联合站名称
            Optional<String> optionalS = factoryInfos.stream().filter(f -> groupId1.equals(f.getId())).map(FactoryInfo::getFactoryName).findFirst();
            if (optionalS.isPresent()){
                one.setFactoryName(optionalS.get());
                return;
            }
            for (FactoryInfo factoryInfo : factoryInfos) {
                List<FactoryInfo> children = factoryInfo.getChildren();
                getoneFactoryName(one, groupId1,children);
            }
        }
    }



    @Test
    public void test2(){
        String fdsafdsa = String.format("`%s`", "fdsafdsa");
        System.out.println(fdsafdsa);
    }

}


