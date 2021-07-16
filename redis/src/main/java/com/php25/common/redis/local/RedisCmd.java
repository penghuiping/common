package com.php25.common.redis.local;

/**
 * @author penghuiping
 * @date 2021/3/2 21:12
 */
class RedisCmd {
    public static final String REMOVE = "remove";
    public static final String EXISTS = "exists";
    public static final String GET_EXPIRE = "get_expire";
    public static final String EXPIRE = "expire";
    public static final String CLEAN_ALL_EXPIRE = "clean_all_expire";

    public static final String BLOOM_FILTER_GET = "bloom_filter_get";
    public static final String RATE_LIMIT_GET = "rate_limit_get";

    public static final String STRING_GET = "string_get";
    public static final String STRING_SET = "string_set";
    public static final String STRING_SET_BIT = "string_set_bit";
    public static final String STRING_GET_BIT = "string_get_bit";
    public static final String STRING_SET_NX = "string_set_nx";
    public static final String STRING_INCR = "string_incr";
    public static final String STRING_DECR = "string_decr";

    public static final String SET_ADD = "set_add";
    public static final String SET_REMOVE = "set_remove";
    public static final String SET_MEMBERS = "set_members";
    public static final String SET_IS_MEMBER = "set_is_member";
    public static final String SET_POP = "set_pop";
    public static final String SET_UNION = "set_union";
    public static final String SET_INTER = "set_inter";
    public static final String SET_DIFF = "set_diff";
    public static final String SET_SIZE = "set_size";
    public static final String SET_GET_RANDOM_MEMBER = "set_get_random_member";

    public static final String SORTED_SET_ADD = "sorted_set_add";
    public static final String SORTED_SET_SIZE = "sorted_set_size";
    public static final String SORTED_SET_RANGE = "sorted_set_range";
    public static final String SORTED_SET_REVERSE_RANGE = "sorted_set_reverse_range";
    public static final String SORTED_SET_RANGE_BY_SCORE = "sorted_set_range_by_score";
    public static final String SORTED_SET_REVERSE_RANGE_BY_SCORE = "sorted_set_reverse_range_by_score";
    public static final String SORTED_SET_RANK = "sorted_set_rank";
    public static final String SORTED_SET_REVERSE_RANK = "sorted_set_reverse_rank";
    public static final String SORTED_SET_REMOVE_RANGE_BY_SCORE = "sorted_set_remove_range_by_score";


    public static final String LIST_INIT = "list_init";
    public static final String LIST_RIGHT_PUSH = "list_right_push";
    public static final String LIST_LEFT_PUSH = "list_left_push";
    public static final String LIST_RIGHT_POP = "list_right_pop";
    public static final String LIST_LEFT_POP = "list_left_pop";
    public static final String LIST_LEFT_RANGE = "list_left_range";
    public static final String LIST_LEFT_TRIM = "list_left_trim";
    public static final String LIST_SIZE = "list_size";

    public static final String HASH_PUT = "hash_put";
    public static final String HASH_PUT_NX = "hash_put_nx";
    public static final String HASH_HAS_KEY = "hash_has_key";
    public static final String HASH_GET = "hash_get";
    public static final String HASH_DELETE = "hash_delete";
    public static final String HASH_INCR = "hash_incr";
    public static final String HASH_DECR = "hash_decr";


}
