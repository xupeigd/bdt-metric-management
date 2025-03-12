package com.quicksand.bigdata.metric.management.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * CompareSymbol
 * (比较符)
 *
 * @author page
 * @date 2022/10/19
 */
@Getter
@AllArgsConstructor
public enum CompareSymbol {


    EQUALS(0, "等于", 1, "="),

    NOT_EQUALS(1, "不等于", 1, "<>"),

    GTE(10, "大于等于", 1, ">="),

    GT(11, "大于", 1, ">"),

    LTE(20, "小于等于", 1, "<="),

    LT(21, "小于", 1, "<"),

    BETWEEN(30, "区间", 2, "BETWEEN"),

    IN(40, "in", -1, "IN"),

    NOT_IN(41, "not in", -1, "NOT IN"),

    LIKE(50, "like", 1, "LIKE"),

    NOT_LIKE(51, "not like", 1, "NOT LIKE"),

    AND(100, "AND", 2, "AND"),

    OR(101, "OR", 2, "OR"),

    FATA(-1, "", Integer.MAX_VALUE, ""),

    ;

    /**
     * code
     */
    int code;

    /**
     * 标识
     */
    String flag;

    /**
     * 参数个数
     * <p>
     * -1 多个 其余表示个数
     */
    int parameterCount;

    /**
     * 符号片段
     */
    String symbol;

    public static CompareSymbol findFilterSymbolByCode(int code) {
        for (CompareSymbol value : CompareSymbol.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return FATA;
    }
}
