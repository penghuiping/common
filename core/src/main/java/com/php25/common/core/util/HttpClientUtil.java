package com.php25.common.core.util;

import com.google.common.collect.Maps;
import com.php25.common.core.exception.Exceptions;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: penghuiping
 * @date: 2018/6/6 09:58
 */
public abstract class HttpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    public static OkHttpClient getProxyClient(String proxyHostOrIp, int port, String username, String password) {
        logger.info(String.format("代理服务器ip:%s,端口%d", proxyHostOrIp, port));
        logger.info(String.format("代理服务器用户名:%s,密码%s", username, password));
        SocketAddress sa = new InetSocketAddress(proxyHostOrIp, port);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);
        return CLIENT.newBuilder().proxy(proxy).proxyAuthenticator((route, response) -> {
            if (response.request().header("Proxy-Authorization") != null) {
                // Give up, we've already failed to authenticate.
                return null;
            }
            String credential = Credentials.basic(username, password);
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        }).build();
    }

    public static OkHttpClient getProxyClient(String proxyHostOrIp, int port) {
        logger.info(String.format("代理服务器ip:%s,端口%d", proxyHostOrIp, port));
        SocketAddress sa = new InetSocketAddress(proxyHostOrIp, port);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);
        return CLIENT.newBuilder().proxy(proxy).build();
    }

    public static OkHttpClient getClient() {
        return CLIENT;
    }


    private static String appendParams(final String url, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (url.contains("?")) {
            for (String key : params.keySet()) {
                sb.append(String.format("&%s=%s", key, params.get(key)));
            }
        } else {
            int i = 0;
            for (String key : params.keySet()) {
                if (i == 0) {
                    sb.append(String.format("?%s=%s", key, params.get(key)));
                } else {
                    sb.append(String.format("&%s=%s", key, params.get(key)));
                }
                i++;
            }
        }
        return sb.toString();
    }

    public static String httpGet(OkHttpClient client, final String url, Map<String, String> params) {
        return httpGet(client, url, params, null);
    }

    public static String httpGet(OkHttpClient client, final String url, Map<String, String> params, Map<String, String> headers) {
        return httpGet(client, url, params, headers, "utf-8");
    }

    public static String httpGet(OkHttpClient client, final String url, Map<String, String> params, Map<String, String> headers, String charset) {
        AssertUtil.hasText(url, "url不能为空");
        if (params == null) {
            params = Maps.newHashMap();
        }

        if (headers == null) {
            headers = Maps.newHashMap();
        }

        String urlNew = appendParams(url, params);

        //拼接headers
        Headers.Builder headersBuilder = new Headers.Builder();
        for (String key : headers.keySet()) {
            headersBuilder.set(key, headers.get(key));
        }
        Request request = new Request.Builder().url(urlNew).headers(headersBuilder.build()).get().build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            return new String(response.body().bytes(), charset);
        } catch (IOException e) {
            throw Exceptions.throwIllegalStateException("HttpClientUtil类中的httpGet方法调用地址url:" + url + "失败", e);
        }
    }


    public static String httpPost(OkHttpClient client, String url, Map<String, String> params) {
        return httpPost(client, url, params, null);
    }


    public static String httpPost(OkHttpClient client, String url, Map<String, String> params, Map<String, String> headers) {
        return httpPost(client, url, params, headers, "utf-8");
    }

    public static String httpPost(OkHttpClient client, String url, Map<String, String> params, Map<String, String> headers, String charset) {
        AssertUtil.hasText(url, "url不能为空");
        if (null == params) {
            params = Maps.newHashMap();
        }

        if (null == headers) {
            headers = Maps.newHashMap();
        }

        FormBody.Builder builder = new FormBody.Builder();
        //拼接参数
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }

        //拼接headers
        Headers.Builder headersBuilder = new Headers.Builder();
        for (String key : headers.keySet()) {
            headersBuilder.set(key, headers.get(key));
        }

        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).headers(headersBuilder.build()).post(body).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            return new String(response.body().bytes(), charset);
        } catch (IOException e) {
            throw Exceptions.throwIllegalStateException("HttpClientUtil类中的httpPost方法调用地址url:" + url + "失败", e);
        }
    }

    public static String httpPost(OkHttpClient client, String url, String json) {
        return httpPost(client, url, json, null);
    }

    public static String httpPost(OkHttpClient client, String url, String json, Map<String, String> headers) {
        return httpPost(client, url, json, headers, "utf-8");
    }

    public static String httpPost(OkHttpClient client, String url, String json, Map<String, String> headers, String charset) {
        AssertUtil.notNull(client, "okHttpClient不能为null");
        AssertUtil.hasText(url, "url不能为空");
        AssertUtil.hasText(json, "json不能为空");

        if (headers == null) {
            headers = Maps.newHashMap();
        }

        //拼接headers
        Headers.Builder headersBuilder = new Headers.Builder();
        for (String key : headers.keySet()) {
            headersBuilder.set(key, headers.get(key));
        }

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).headers(headersBuilder.build()).post(body).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            return new String(response.body().bytes(), charset);
        } catch (IOException e) {
            throw Exceptions.throwIllegalStateException("HttpClientUtil类中的httpPost方法调用地址url:" + url + "失败", e);
        }
    }
}
