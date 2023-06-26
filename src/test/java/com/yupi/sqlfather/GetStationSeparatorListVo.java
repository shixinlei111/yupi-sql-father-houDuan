package com.yupi.sqlfather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetStationSeparatorListVo {
    //id
    private Integer id;
    //名称
    private String name;
    /**
     * 是否可操控
     */
    private Integer controllable;


    /**
     * 分离器所在的厂区，联合站名字
     */
    private String factoryName;
    /**
     * 厂区id
     */
    private String groupId;
}
