package com.yupi.sqlfather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactoryInfo {
    private String id;
    private String factoryName;
    private String isFactory;
    private List<FactoryInfo> children;
}
