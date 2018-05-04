package com.php25.common.specification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.util.StringUtil;
import org.nutz.dao.Cnd;
import org.nutz.dao.sql.Criteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

/**
 * Created by penghuiping on 16/4/12.
 */
public class BaseNutzSpecs {
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(BaseNutzSpecs.class);

    public static Criteria getNutzSpecs(final String json) {
        if (StringUtil.isBlank(json)) return Cnd.cri();
        try {
            final List<SearchParam> searchParams = objectMapper.readValue(json, new TypeReference<List<SearchParam>>() {
            });

            Criteria cri = Cnd.cri();
            for (SearchParam s : searchParams) {
                switch (s.getOperator().toLowerCase()) {
                    case "eq":
                        cri.where().andEquals(s.getFieldName(), s.getValue());
                        break;
                    case "ne":
                        cri.where().andNotEquals(s.getFieldName(), s.getValue());
                        break;
                    case "like":
                        cri.where().andLike(s.getFieldName(), (String) s.getValue());
                        break;
                    case "gt":
                        cri.where().andGT(s.getFieldName(), (Long) s.getValue());
                        break;
                    case "lt":
                        cri.where().andLT(s.getFieldName(), (Long) s.getValue());
                        break;
                    case "gte":
                        cri.where().andGTE(s.getFieldName(), (Long) s.getValue());
                        break;
                    case "lte":
                        cri.where().andLTE(s.getFieldName(), (Long) s.getValue());
                        break;
                    case "in":
                        Assert.isInstanceOf(String.class, s.getValue(), "使用in操作值必须是List的json字符串");
                        int errorCount = 0;
                        //尝试是否可以转化为String
                        try {
                            List<String> list = objectMapper.readValue((String) s.getValue(), new TypeReference<List<String>>() {
                            });
                            String[] tmp = new String[list.size()];
                            cri.where().andIn(s.getFieldName(), list.toArray(tmp));
                        } catch (Exception e) {
                            errorCount++;
                        }

                        if (errorCount > 0) {
                            //尝试是否可以转化成Long
                            try {
                                List<Long> list1 = objectMapper.readValue((String) s.getValue(), new TypeReference<List<Long>>() {
                                });
                                long[] tmp = new long[list1.size()];

                                for (int i = 0; i < list1.size(); i++) {
                                    tmp[i] = list1.get(i);
                                }
                                cri.where().andIn(s.getFieldName(), tmp);
                            } catch (Exception e) {
                                throw new RuntimeException("使用in操作值必须是List的json字符串,并且List的泛型只能是String或者Long", e);
                            }
                        }
                        break;
                    case "nin":
                        Assert.isInstanceOf(String.class, s.getValue(), "使用not in操作值必须是List的json字符串");
                        int errorCount0 = 0;
                        //尝试是否可以转化为String
                        try {
                            List<String> list = objectMapper.readValue((String) s.getValue(), new TypeReference<List<String>>() {
                            });
                            String[] tmp = new String[list.size()];
                            cri.where().andNotIn(s.getFieldName(), list.toArray(tmp));
                        } catch (Exception e) {
                            errorCount0++;
                        }

                        if (errorCount0 > 0) {
                            //尝试是否可以转化成Long
                            try {
                                List<Long> list1 = objectMapper.readValue((String) s.getValue(), new TypeReference<List<Long>>() {
                                });
                                long[] tmp = new long[list1.size()];

                                for (int i = 0; i < list1.size(); i++) {
                                    tmp[i] = list1.get(i);
                                }
                                cri.where().andNotIn(s.getFieldName(), tmp);
                            } catch (Exception e) {
                                throw new RuntimeException("使用not in操作值必须是List的json字符串,并且List的泛型只能是String或者Long", e);
                            }
                        }
                        break;
                    default:
                        cri.where().andEquals(s.getFieldName(), s.getValue());
                        break;

                }
            }
            return cri;
        } catch (IOException e) {
            logger.error("查询json解析出错", e);
            return null;
        }
    }
}
