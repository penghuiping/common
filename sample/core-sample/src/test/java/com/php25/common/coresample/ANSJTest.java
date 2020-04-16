package com.php25.common.coresample;

import org.ansj.domain.Result;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.junit.Test;

/**
 * @author penghuiping
 * @date 2020/4/9 11:42
 */
public class ANSJTest {

    /**
     * 分词处理
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        DicLibrary.insert("dic", "新冠状病毒");
        DicLibrary.insert("dic", "习主席");
        Result result = DicAnalysis.parse("中国人民坚持抗击新冠状病毒,习主席坚持领导党中央及各级政府开展有效的防疫措施");
        result.getTerms().stream().filter(term -> term.getNatureStr().equals(DicLibrary.DEFAULT_NATURE)).forEach(term -> System.out.println(term.getName()));

        DicLibrary.delete("dic", "习主席");
        Result result1 = DicAnalysis.parse("中国人民坚持抗击新冠状病毒,习主席坚持领导党中央及各级政府开展有效的防疫措施");
        result1.getTerms().stream().filter(term -> term.getNatureStr().equals(DicLibrary.DEFAULT_NATURE)).forEach(term -> System.out.println(term.getName()));
    }
}
