package com.quicksand.bigdata.metric.management.utils;

import org.springframework.util.StringUtils;

import static com.quicksand.bigdata.metric.management.consts.YamlSegmentKeys.SEGMENT_SEPARATOR;

/**
 * MetricYamlContentUtil
 *
 * @author zhao_xin
 * @version 1.0
 * @date 2022/9/16 09:43
 * @description
 */
public class MetricYamlContentUtil {
    private static final char lineBreaks = '\n';
    private static final char space = ' ';
    private static final String lineBreaksSymbol = "/0x01/";
    private static final String spaceSymbol = "/0x02/";
    private static final String escapeRegex = "\\\\";
    private static final String escapeSymbol = "/0x03/";

    //指定key内容替换
    public static String encodeYamlContent(String content) {
        String[] encodeList = new String[]{"processing_logic", "description"};
        StringBuilder newContent = new StringBuilder(200);
        for (String segment : content.split(SEGMENT_SEPARATOR)) {
            if (StringUtils.hasText(segment)) {
                for (String keyWord : encodeList) {
                    if (segment.contains(keyWord)) {
                        segment = replaceYamlContent(segment, keyWord);
                    }
                }
                newContent.append(SEGMENT_SEPARATOR).append(segment);
            }

        }
        return newContent.toString();
    }

    //全部回替换
    public static String decodeYamlContent(String content) {
        return decodeReplace(content);
    }


    private static String replaceYamlContent(String content, String keyword) {
        //多个关键词时，用于跳过
        int keywordFromIndex = 0;
        //替换key起始位置
        int beginIndex = content.indexOf(keyword, keywordFromIndex);
        while (beginIndex > -1) {

            //key所在层级缩进起始位置
            int signIndex = beginIndex;
            while (signIndex > 0) {
                signIndex--;
                if (content.charAt(signIndex) == lineBreaks) {
                    break;
                }
            }
            //获取key缩进字符串标识
            String signSegment = content.substring(signIndex, beginIndex);
            //要替换字符串的前截取
            String searchStr = content.substring(beginIndex);

            int firstEndIndex = -1;
            for (int i = signSegment.length(); i > 0; i--) {
                String nodeSign = signSegment.substring(0, i);
                int fromIndex = 0; //跳过位置，key的子目录缩进会包含父级别缩进，需要跳过精准匹配
                int endIndex = 0; //下个有效signSegment起始位置
                while (fromIndex < searchStr.length()) {
                    endIndex = searchStr.indexOf(nodeSign, fromIndex);
                    if (endIndex < 0 || endIndex + nodeSign.length() >= searchStr.length()) {
                        break;
                    }
                    if (searchStr.charAt(endIndex + nodeSign.length()) == space) {
                        fromIndex = endIndex + signSegment.length() + 1;
                        endIndex = -1;
                    } else {
                        break;
                    }
                }
                if (endIndex > 0) {
                    if (firstEndIndex > 0) {
                        firstEndIndex = Math.min(firstEndIndex, endIndex);
                    } else {
                        firstEndIndex = endIndex;
                    }
                }

            }


            //从search中截取精准替换部分内容
            String replacePartStr = firstEndIndex > 0 ? searchStr.substring(0, firstEndIndex) : searchStr;

            //开头格式不转换
            String encodeStr = encodeReplace(replacePartStr, 0).replaceFirst(spaceSymbol, String.valueOf(space));
            content = content.replace(replacePartStr, encodeStr);
            //有多个关键词时候，下一个关键词
            keywordFromIndex = beginIndex + encodeStr.length();
            if (keywordFromIndex >= content.length()) {
                break;
            }
            beginIndex = content.indexOf(keyword, keywordFromIndex);
        }
        return content;
    }


    //替换关键字
    public static String encodeReplace(String replaceStr, int retractionNum) {
        StringBuilder replacePart = new StringBuilder(lineBreaksSymbol);
        for (int i = 0; i < retractionNum; i++) {
            replacePart.append(spaceSymbol);
        }

        return replaceStr.replaceAll(String.valueOf(lineBreaks), replacePart.toString())
                .replaceAll(String.valueOf(space), spaceSymbol)
                .replaceAll(escapeRegex, escapeSymbol)
                ;
    }

    //回换关键字
    private static String decodeReplace(String replaceStr) {
        return replaceStr.replaceAll(lineBreaksSymbol, String.valueOf(lineBreaks))
                .replaceAll(spaceSymbol, String.valueOf(space))
                .replaceAll(escapeSymbol, escapeRegex)
                ;
    }

}
