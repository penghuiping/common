package com.php25.common.coresample;

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie;
import org.junit.Test;

import java.util.List;
import java.util.TreeMap;

/**
 * @author penghuiping
 * @date 2021/4/15 22:27
 */
public class TrieTest {

    @Test
    public void test() {
// Collect test data set
        TreeMap<String, String> map = new TreeMap<String, String>();
        String[] keyArray = new String[]
                {
                        "不健康",
                        "少儿",
                        "不宜"
                };
        for (String key : keyArray) {
            map.put(key, key);
        }
        // Build an AhoCorasickDoubleArrayTrie
        AhoCorasickDoubleArrayTrie<String> acdat = new AhoCorasickDoubleArrayTrie<String>();
        acdat.build(map);
        // Test it
        final String text = "这是一篇少儿不宜的文章";
        List<AhoCorasickDoubleArrayTrie.Hit<String>> wordList = acdat.parseText(text);
        System.out.println(wordList);
    }
}
