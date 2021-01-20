package com.php25.common.db.util;

import com.php25.common.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author penghuiping
 * @date 2021/1/20 10:40
 */
public class BMSearch {
    private static final int MAX_ALPHABET_SIZE = 256;

    /**
     * Performs Boyer-Moore search on a given string with a given pattern
     *
     * @param text    the string being searched in
     * @param pattern the string being searched for
     * @return List of indexes where the pattern occurs
     */
    public List<Integer> findOccurrences(String text, String pattern) {
        if (StringUtil.isBlank(text)
                || StringUtil.isBlank(pattern)
                || pattern.length() > text.length()
                || pattern.length() == 0) {
            return new ArrayList<>();
        }
        List<Integer> occurrences = new ArrayList<>();
        int[] skipTable = generateSkipTable(pattern);

        int n = pattern.length();
        for (int textIndex = n - 1, patternIndex = n - 1; textIndex < text.length(); ) {
            // Found a match!
            if (patternIndex >= 0 && pattern.charAt(patternIndex) == text.charAt(textIndex)) {
                if (patternIndex == 0) {
                    occurrences.add(textIndex);
                } else {
                    textIndex--;
                }
                patternIndex--;
            } else {
                textIndex += n - Math.min(Math.max(patternIndex, 0), skipTable[text.charAt(textIndex)] + 1);
                patternIndex = n - 1;
            }
        }
        return occurrences;
    }

    private int[] generateSkipTable(String pattern) {
        int[] skipTable = new int[MAX_ALPHABET_SIZE];
        for (int i = 0; i < pattern.length(); i++) {
            skipTable[pattern.charAt(i)] = i;
        }
        return skipTable;
    }
}
