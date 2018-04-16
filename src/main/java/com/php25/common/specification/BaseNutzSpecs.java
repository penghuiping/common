package com.php25.common.specification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.php25.common.util.StringUtil;
import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.sql.Criteria;

import java.io.IOException;
import java.util.List;

/**
 * Created by penghuiping on 16/4/12.
 */
public class BaseNutzSpecs {
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    public static Criteria getNutzSpecs(final String json) {
        if (StringUtil.isBlank(json)) return Cnd.cri();
        try {
            final List<SearchParam> searchParams = objectMapper.readValue(json, new TypeReference<List<SearchParam>>() {
            });

            Criteria cri = Cnd.cri();
            for (SearchParam s : searchParams) {
                switch (s.getOperator()) {
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
                        List<String> list = objectMapper.readValue((String) s.getValue(), new TypeReference<List<String>>() {
                        });
                        String[] tmp = new String[list.size()];
                        cri.where().andIn(s.getFieldName(), list.toArray(tmp));
                        break;
                    default:
                        cri.where().andEquals(s.getFieldName(), s.getValue());
                        break;

                }
            }
            return cri;
        } catch (IOException e) {
            Logger.getLogger(BaseNutzSpecs.class).error(e);
            return null;
        }
    }
}
