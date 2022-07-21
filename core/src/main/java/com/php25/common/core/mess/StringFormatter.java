package com.php25.common.core.mess;

import com.php25.common.core.util.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * @author penghuiping
 * @date 2021/1/20 13:32
 */
public class StringFormatter {

    public static final String KEY_WRAPPER_PREFIX = "${";

    public static final String KEY_WRAPPER_SUFFIX = "}";

    private String value;

    public StringFormatter(String template) {
        this.value = template;
    }

    public String format(Map<String, Object> params) {
        for (Map.Entry<String, Object> item : params.entrySet()) {
            BMSearch bmSearch = new BMSearch();
            String patten = KEY_WRAPPER_PREFIX + item.getKey() + KEY_WRAPPER_SUFFIX;
            List<Integer> occurrences = bmSearch.findOccurrences(this.value, patten);
            String tmp = null;
            for (int i = 0; i < occurrences.size(); i++) {
                int occurrence = occurrences.get(i);
                String left = null;
                String middle = item.getValue().toString();
                String right = null;

                if (StringUtil.isBlank(tmp)) {
                    left = this.value.substring(0, occurrence);
                } else {
                    left = tmp;
                }

                if (i + 1 < occurrences.size()) {
                    int occurrenceNext = occurrences.get(i + 1);
                    right = this.value.substring(occurrence + patten.length(), occurrenceNext);
                } else {
                    right = this.value.substring(occurrence + patten.length());
                }
                tmp = left + middle + right;
            }
            this.value = tmp;
        }
        return this.value;
    }
}
