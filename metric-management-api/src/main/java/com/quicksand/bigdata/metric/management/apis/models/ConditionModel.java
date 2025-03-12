package com.quicksand.bigdata.metric.management.apis.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quicksand.bigdata.metric.management.consts.CompareSymbol;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ConditionModel
 *
 * @author page
 * @date 2022/10/19
 */
@Schema(name = "限制性条件")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionModel {

    /**
     * 左子式
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ConditionModel left;

    /**
     * 右子式
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ConditionModel right;

    /**
     * 限制纬度名称
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    /**
     * 限制符
     * <p>
     *
     * @see CompareSymbol
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    int symbol;

    /**
     * 选中的值
     * <p>
     * <p>
     * yyyyMMdd hh:mm:ss
     */
    @Schema(description = "")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> hitValues;

    /**
     * 验证model是否合规
     *
     * @param model 条件model
     * @return true/false
     */
    public static boolean validation(ConditionModel model) {
        return Objects.equals(CompareSymbol.AND, CompareSymbol.findFilterSymbolByCode(model.getSymbol()))
                || Objects.equals(CompareSymbol.OR, CompareSymbol.findFilterSymbolByCode(model.getSymbol()))
                ? null != model.getLeft()
                && null != model.getRight()
                && validation(model.getLeft())
                && validation(model.getRight())
                : StringUtils.hasText(model.getName())
                && !CollectionUtils.isEmpty(model.getHitValues())
                && model.getHitValues().size() >= CompareSymbol.findFilterSymbolByCode(model.getSymbol()).getParameterCount();
    }

    /**
     * 解析涉及的名称
     *
     * @param model 条件model
     * @return list of String
     */
    public static List<String> resolveNames(ConditionModel model) {
        List<String> names = new ArrayList<>();
        if (Objects.equals(CompareSymbol.AND, CompareSymbol.findFilterSymbolByCode(model.getSymbol()))
                || Objects.equals(CompareSymbol.OR, CompareSymbol.findFilterSymbolByCode(model.getSymbol()))) {
            names.addAll(resolveNames(model.left));
            names.addAll(resolveNames(model.right));
            names = new ArrayList<>(new HashSet<>(names));
        } else {
            names.add(model.name);
        }
        return names;
    }

    /**
     * 转换为命令行参数形式
     *
     * @return String
     */
    public static String toCmdSegement(ConditionModel model) {
        CompareSymbol compareSymbol = CompareSymbol.findFilterSymbolByCode(model.getSymbol());
        return Objects.equals(CompareSymbol.AND, CompareSymbol.findFilterSymbolByCode(model.getSymbol()))
                || Objects.equals(CompareSymbol.OR, CompareSymbol.findFilterSymbolByCode(model.getSymbol()))
                ? String.format("%s %s %s", toCmdSegement(model.left), compareSymbol.getSymbol(), toCmdSegement(model.right))
                : (1 == compareSymbol.getParameterCount()
                ? String.format("%s %s '%s'", model.getName(), compareSymbol.getSymbol(), model.getHitValues().get(0))
                : (2 == compareSymbol.getParameterCount()
                ? String.format("%s %s '%s' AND '%s'", model.getName(), compareSymbol.getSymbol(), model.getHitValues().get(0), model.getHitValues().get(1))
                : String.format("%s %s (%s)", model.getName(), compareSymbol.getSymbol(),
                StringUtils.collectionToCommaDelimitedString(model.getHitValues().stream()
                        .map(v -> String.format("'%s'", v))
                        .collect(Collectors.toList())))
        ));
    }

    public static void main(String[] args) {
        ConditionModel conditionModel = new ConditionModel();
        ConditionModel left = new ConditionModel();
        left.hitValues = Arrays.asList("20221201");
        left.name = "pt";
        left.symbol = 11;
        ConditionModel right = new ConditionModel();
        right.hitValues = Arrays.asList("午餐", "晚餐");
        right.name = "mealperiod_name";
        right.symbol = 40;
        conditionModel.left = left;
        conditionModel.right = right;
        conditionModel.symbol = 100;
        System.out.println(toCmdSegement(conditionModel));

    }

}
