package com.quicksand.bigdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class Params {
    String name;
    Object val;
    Class<?> type;

    public static Params build(String name, Object val) {
        return new Params(name, val, val.getClass());
    }

}

