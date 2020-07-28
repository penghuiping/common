package com.php25.common.coresample;

import com.google.common.collect.Sets;
import com.php25.common.core.util.JsonUtil;
import org.ansj.domain.Result;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/4/9 11:42
 */
public class ANSJTest {

    private String value = "{\"appId\":\"jintianli_syj\",\"msgType\":5,\"content\":[{\"id\":1,\"title\":\"卡片标题\",\"description\":\"这是一个多卡片的描述\",\"media\":{\"mediaId\":339094478503542784,\"thumbnailId\":339094355862093824,\"height\":\"MEDIUM_HEIGHT\",\"contentDescription\":\"媒体赌博素材的描述\"},\"suggestions\":[{\"index\":1,\"btnType\":0,\"displayText\":\"回复按钮名称\",\"data\":\"回复内容\"},{\"index\":2,\"btnType\":1,\"url\":\"https://www.baidu.com\",\"displayText\":\"连接按钮名称\",\"data\":\"回复内容\"},{\"index\":3,\"btnType\":2,\"dialerPhone\":\"18956195421\",\"displayText\":\"拨号按钮名称\",\"data\":\"回复内容\"}]},{\"id\":2,\"title\":\"卡片标题\",\"description\":\"这是另一个单卡片的描述\",\"media\":{\"mediaId\":339094478503542784,\"thumbnailId\":339094355862093824,\"height\":\"MEDIUM_HEIGHT\",\"contentDescription\":\"媒体素材的描述\"},\"suggestions\":[{\"index\":4,\"btnType\":3,\"dialerPhone\":\"18956195421\",\"displayText\":\"拨打视频按钮名称\",\"data\":\"回复内容\"},{\"index\":5,\"btnType\":4,\"latitude\":\"37.4220041\",\"longitude\":\"-122.0862515\",\"label\":\"伦敦\",\"displayText\":\"打开地图按钮\",\"data\":\"回复内容\"},{\"index\":6,\"btnType\":5,\"displayText\":\"打开日历\",\"title\":\"标题\",\"description\":\"描述\",\"startTime\":\"2020-07-22T00:00:00Z\",\"endTime\":\"2020-07-23T00:00:00Z\",\"data\":\"回复内容\"}]}],\"phones\":\"18018568460\",\"smsSupported\":0,\"trafficType\":\"advertisement\",\"storeSupported\":0}";
    /**
     * 分词处理
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
//        DicLibrary.insert("dic", "新冠状病毒");
//        DicLibrary.insert("dic", "习主席");
//        Result result = DicAnalysis.parse("中国人民坚持抗击新冠状病毒,习主席坚持领导党中央及各级政府开展有效的防疫措施");
//        result.getTerms().stream().filter(term -> !term.getNatureStr().equals("null")).forEach(term -> System.out.println(term.getName()));

//        DicLibrary.delete("dic", "习主席");
//        Result result1 = DicAnalysis.parse("中国人民坚持抗击新冠状病毒,习主席坚持领导党中央及各级政府开展有效的防疫措施");
//        result1.getTerms().stream().filter(term -> term.getNatureStr().equals(DicLibrary.DEFAULT_NATURE)).forEach(term -> System.out.println(term.getName()));

        Result result1 = DicAnalysis.parse(value);
        DicLibrary.insert("dic", "赌博");
        DicLibrary.insert("dic", "卖淫");
        Set set =  result1.getTerms().stream()
                .filter(term -> !term.getNatureStr().equals("null")).map(term -> term.getName()).collect(Collectors.toSet());
        Sets.SetView setView = Sets.intersection(DicLibrary.get().toMap().keySet(),set);
        System.out.println(JsonUtil.toJson(setView));

    }
}
