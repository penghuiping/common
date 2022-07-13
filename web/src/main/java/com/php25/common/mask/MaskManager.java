package com.php25.common.mask;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author penghuiping
 * @date 2022/3/27 15:04
 */
public class MaskManager {
    /**
     * 移动电话
     */
    private final static Pattern MOBILE = Pattern.compile("((13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8})", Pattern.MULTILINE);
    /**
     * 18位身份证号码
     */
    private final static Pattern CITIZEN_ID = Pattern.compile("((\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\d{3})([0-9]|X))", Pattern.MULTILINE);
    private static final List<Function<String, String>> processFunctions = Lists.newArrayList(MaskManager::processMobile,
            MaskManager::processIdCard,
            MaskManager::processDefault);
    private final List<Pattern> compiledPatterns = Lists.newArrayList(MOBILE, CITIZEN_ID);

    private static String processMask(String content, int startLength, int endLength) {
        StringBuilder sb = new StringBuilder(content);
        //保留前startLength位,于后endLength位
        for (int i = 0; i < sb.length(); i++) {
            if (i >= startLength && i < sb.length() - endLength) {
                sb.setCharAt(i, '*');
            }
        }
        return sb.toString();
    }

    private static String processMobile(String content) {
        return processMask(content, 3, 3);
    }

    private static String processIdCard(String content) {
        return processMask(content, 3, 4);
    }

    private static String processDefault(String content) {
        return processMask(content, 0, 0);
    }

    public void addMaskPattern(String maskPattern) {
        compiledPatterns.add(Pattern.compile(maskPattern, Pattern.MULTILINE));
    }

    /**
     * 脱敏处理
     *
     * @param message 文本内容
     * @return 脱敏后的文本内容
     */
    public String maskMessage(String message) {
        StringBuilder sb = new StringBuilder(message);
        for (int i = 0; i < compiledPatterns.size(); i++) {
            maskMessage0(sb, compiledPatterns.get(i), i);
        }
        return sb.toString();
    }

    private void maskMessage0(StringBuilder sb, Pattern multilinePattern, int idx) {
        if (multilinePattern == null) {
            return;
        }
        Matcher matcher = multilinePattern.matcher(sb);
        while (matcher.find()) {
            IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
                if (matcher.group(group) != null) {
                    int start = matcher.start(group);
                    int end = matcher.end(group);
                    String content = sb.substring(start, end);
                    Function<String, String> processFunction = MaskManager::processDefault;
                    if (idx < compiledPatterns.size()) {
                        processFunction = processFunctions.get(idx);
                    }
                    content = processFunction.apply(content);
                    int index = 0;
                    for (int i = start; i < end; i++) {
                        sb.setCharAt(i, content.charAt(index++));
                    }
                }
            });
        }
    }
}
