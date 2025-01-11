package cn.graht.gateway.utils;

import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;


public class GatewayHttpUtil {


    public static String getBodyContent(ServerWebExchange exchange) {
        Object attribute = exchange.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);
        if (attribute == null || !(attribute instanceof NettyDataBuffer)) {
            return null;
        }
        MediaType contentType = exchange.getRequest().getHeaders().getContentType();
        if (contentType != null && contentType.toString().contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            return null;
        }
        NettyDataBuffer nettyDataBuffer = (NettyDataBuffer) attribute;
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(nettyDataBuffer.asByteBuffer());
        return charBuffer.toString();
    }
}

