package com.quicksand.bigdata.metric.management.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.quicksand.bigdata.metric.management.consts.AggregationType;
import com.quicksand.bigdata.metric.management.consts.YamlSegmentKeys;
import com.quicksand.bigdata.metric.management.yaml.vos.MetricSegment;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class YamlUtilTest {

    public static void main(String[] args) throws JsonProcessingException {
        String val = "---\n" +
                "  measures:\n" +
                "    - name: transaction_amount_usd\n" +
                "      description: The total USD value of the transaction.\n" +
                "      agg: SUM\n" +
                "      create_metric: true\n" +
                "    - name: transacting_customers\n" +
                "      description: The distinct count of customers transacting on any given day.\n" +
                "      expr: id_customer\n" +
                "      agg: COUNT_DISTINCT\n" +
                "---      \n" +
                "  dimensions:\n" +
                "    - name: ds\n" +
                "      type: time\n" +
                "      type_params:\n" +
                "        is_primary: True\n" +
                "        time_granularity: day\n" +
                "    - name: is_large\n" +
                "      type: categorical\n" +
                "      expr: CASE WHEN transaction_amount_usd >= 30 THEN TRUE ELSE FALSE END\n" +
                "---\n" +
                "metric: \n" +
                "  name: cancellations \n" +
                "  type: measure_proxy \n" +
                "  type_params:\n" +
                "    measure: transaction_amount_usd\n";
        Map<String, String> map = new HashMap<>();
        map.put("yamlconent", val);
        String json = new ObjectMapper().writeValueAsString(map);
        System.out.println(json);
    }

    @Test
    void toYaml() throws IOException {
        MetricSegment build = MetricSegment.builder().metric(MetricSegment.UserMetric.builder().name("aaa").type("bbbc").build()).build();
        new ObjectMapper(new YAMLFactory()).writeValue(new File("/Users/zhaoxin/servers/docker-env/mfc/sample_models/admin/test.yaml"), build);
        try {
            String ss = new JsonMapper().writeValueAsString(AggregationType.values());
            System.out.println(ss);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void yamlToObject() {
        String yamlStr = "---\n" +
                "measures:\n" +
                "    - name: transaction_amount_usd\n" +
                "      description: The total USD value of the transaction.\n" +
                "      agg: SUM\n" +
                "      create_metric: true\n" +
                "    - name: transactions\n" +
                "      description: The total number of transactions.\n" +
                "      expr: \"1\"\n" +
                "      agg: SUM\n" +
                "      create_metric: true\n" +
                "    - name: quick_buy_amount_usd\n" +
                "      description: The total USD value of the transactions that were \n" +
                "                   purchased using the \"quick buy\" button.\n" +
                "      expr: CASE WHEN transaction_type_name = 'quick-buy' THEN transaction_amount_usd ELSE 0 END\n" +
                "      agg: SUM\n" +
                "      create_metric: true\n" +
                "    - name: quick_buy_transactions\n" +
                "      description: The total transactions bought as quick buy.\n" +
                "      expr: CASE WHEN transaction_type_name = 'quick-buy' THEN TRUE ELSE FALSE END\n" +
                "      agg: SUM_BOOLEAN\n" +
                "      create_metric: true\n" +
                "    - name: cancellations_usd\n" +
                "      description: The total USD value of the transactions that were \n" +
                "                   cancelled.\n" +
                "      expr: CASE WHEN transaction_type_name = 'cancellation' THEN transaction_amount_usd ELSE 0 END\n" +
                "      agg: SUM\n" +
                "    - name: alterations_usd\n" +
                "      description: The total USD value of the transactions that were \n" +
                "                   altered.\n" +
                "      expr: CASE WHEN transaction_type_name = 'alteration' THEN transaction_amount_usd ELSE 0 END\n" +
                "      agg: SUM\n" +
                "    - name: transacting_customers\n" +
                "      description: The distinct count of customers transacting on any given day.\n" +
                "      expr: id_customer\n" +
                "      agg: COUNT_DISTINCT\n" +
                "---      \n" +
                "dimensions:\n" +
                "  - name: ds\n" +
                "    type: time\n" +
                "    type_params:\n" +
                "      is_primary: True\n" +
                "      time_granularity: day\n" +
                "  - name: is_large\n" +
                "    type: categorical\n" +
                "    expr: CASE WHEN transaction_amount_usd >= 30 THEN TRUE ELSE FALSE END\n" +
                "  - name: quick_buy_transaction\n" +
                "    type: categorical\n" +
                "    expr: |\n" +
                "      CASE \n" +
                "        WHEN transaction_type_name = 'quick-buy' THEN 'Quick Buy'\n" +
                "        ELSE 'Not Quick Buy' \n" +
                "      END\n" +
                "\n" +
                "sql_table: postgresdb.mf_demo_transactions\n" +
                "mutability:\n" +
                "  type: immutable\n" +
                "\n" +
                "---\n" +
                "metric: \n" +
                "  name: cancellations \n" +
                "  owners: \n" +
                "    - support@transformdata.io\n" +
                "  type: measure_proxy \n" +
                "  type_params:\n" +
                "    measure: cancellations_usd\n" +
                "---\n" +
                "metric:\n" +
                "  name: cancellation_rate\n" +
                "  owners:\n" +
                "    - support@transformdata.io\n" +
                "  type: ratio\n" +
                "  type_params:\n" +
                "    numerator: cancellations_usd\n" +
                "    denominator: transaction_amount_usd\n" +
                "---\n" +
                "metric:\n" +
                "  name: revenue_usd\n" +
                "  owners:\n" +
                "    - support@transformdata.io\n" +
                "  type: expr \n" +
                "  type_params:\n" +
                "    expr: transaction_amount_usd - cancellations_usd + alterations_usd\n" +
                "    measures:\n" +
                "      - transaction_amount_usd\n" +
                "      - cancellations_usd\n" +
                "      - alterations_usd\n" +
                "---\n" +
                "metric:\n" +
                "  name: cancellations_mx\n" +
                "  owners:\n" +
                "    - support@transformdata.io\n" +
                "  type: measure_proxy \n" +
                "  type_params:\n" +
                "    measure: cancellations_usd\n" +
                "  constraint: |\n" +
                "    customer__country = 'MX'\n" +
                "---\n" +
                "metric:\n" +
                "  name: transaction_usd_na\n" +
                "  owners:\n" +
                "    - support@transformdata.io\n" +
                "  type: measure_proxy \n" +
                "  type_params:\n" +
                "    measure: transaction_amount_usd\n" +
                "  constraint: |\n" +
                "    customer__country__region = 'NA'\n" +
                "---\n" +
                "metric:\n" +
                "  name: transaction_usd_l7d_mx\n" +
                "  owners:\n" +
                "    - support@transformdata.io\n" +
                "  type: cumulative \n" +
                "  type_params:\n" +
                "    measures:\n" +
                "      - transaction_amount_usd\n" +
                "    window: 7 days\n" +
                "  constraint: |\n" +
                "    customer__country = 'MX'\n" +
                "---\n" +
                "metric: \n" +
                "  name: transaction_usd_mtd\n" +
                "  owners: \n" +
                "    - support@transformdata.io\n" +
                "  type: cumulative\n" +
                "  type_params:\n" +
                "    measures:\n" +
                "      - transaction_amount_usd\n" +
                "    grain_to_date: month\n" +
                "---\n" +
                "metric:\n" +
                "  name: transaction_usd_na_l7d\n" +
                "  owners:\n" +
                "    - support@transformdata.io\n" +
                "  type: cumulative \n" +
                "  type_params:\n" +
                "    measures: \n" +
                "      - transaction_amount_usd\n" +
                "    window: 7 days\n" +
                "  constraint: |\n" +
                "    customer__country__region = 'NA'\n";
        String measuresStr = "  measures:\n" +
                "    - name: new_customers\n" +
                "      expr: \"1\"\n" +
                "      agg: SUM\n" +
                "      create_metric: true";
        for (String s : yamlStr.split(YamlSegmentKeys.SEGMENT_SEPARATOR)) {
            Try<JsonNode> ds = YamlUtil.yamlToObject(s, JsonNode.class);
            JsonNode dss = ds.isSuccess() ? ds.get() : null;
            if (dss == null) {
                continue;
            }
            Iterator<String> fields = dss.fieldNames();
            while (fields.hasNext()) {
                String keys = fields.next();
                System.out.println(keys);
            }
            System.out.println(dss);
        }

//        Try<MeasuresSegment> ds = YamlUtil.yamlToObject(measuresStr, MeasuresSegment.class);
//        MeasuresSegment dss= ds.isSuccess()?ds.get():null;

    }

    @Test
    void changeLine() throws IOException {
        /*
         WRITE_DOC_START_MARKER(true),
        USE_NATIVE_OBJECT_ID(true),
        USE_NATIVE_TYPE_ID(true),
        CANONICAL_OUTPUT(false),
        SPLIT_LINES(true),
        MINIMIZE_QUOTES(false),
        ALWAYS_QUOTE_NUMBERS_AS_STRINGS(false),
        LITERAL_BLOCK_STYLE(false),
        INDENT_ARRAYS(false),
        INDENT_ARRAYS_WITH_INDICATOR(false),
        USE_PLATFORM_LINE_BREAKS(false);
         */
        ObjectMapper objMapper = new ObjectMapper(new YAMLFactory());
        YamlModel model = new YamlModel();
        model.name = "zx";
        model.desc = "   line1 \n     line2     \nline3";
        String testStr = objMapper.writeValueAsString(model);
        System.out.println(testStr);
        YamlModel model1 = objMapper.readValue(testStr, YamlModel.class);

        objMapper.writeValue(new File("./test.yaml"), model);

    }
}