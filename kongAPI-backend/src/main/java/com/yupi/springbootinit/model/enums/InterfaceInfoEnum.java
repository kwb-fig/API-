package com.yupi.springbootinit.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 接口信息状态枚举
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public enum InterfaceInfoEnum {

    ONLINE("上线",1),
    OFFLINE("下线",0);

    private final String text;

    private final int value;

    InterfaceInfoEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }


    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
