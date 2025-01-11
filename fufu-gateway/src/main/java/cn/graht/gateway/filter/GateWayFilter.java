package cn.graht.gateway.filter;

import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 注解的方式实现过滤，与MyLogGateWayFilter不能同时存在
 * 除了在yml中使用filters，还可以自定义过滤器
 */
@Configuration
@Slf4j
public class
GateWayFilter {

    @Bean
    @Order(1)//越小越靠前
    public GlobalFilter checkLogin() {
        return (ex, chain) -> {
            ServerHttpRequest serverHttpRequest = ex.getRequest();
            String url = serverHttpRequest.getPath().value();
            String method = serverHttpRequest.getMethod().name();
            boolean b = true;
            if ("POST".equals(method)) {
                //从请求里获取Post请求体
                String bodyStr = resolveBodyFromRequest(serverHttpRequest);
                //得到Post请求的请求参数后，做想做的事
                log.debug("访问网关uri:{},post请求参数：{}",url,bodyStr);
                if(bodyStr != null){
                    //下面的将请求体再次封装写回到request里，传到下一级，否则，由于请求体已被消费，后续的服务将取不到值
                    URI uri = serverHttpRequest.getURI();
                    ServerHttpRequest request = serverHttpRequest.mutate().uri(uri).build();
                    DataBuffer bodyDataBuffer = stringBuffer(bodyStr);
                    Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
                    request = new ServerHttpRequestDecorator(request) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return bodyFlux;
                        }
                    };
                    //封装request，传给下一级
                    return chain.filter(ex.mutate().request(request).build());
                }else {
                    //拦截请求
                    //获取响应对象
                    ServerHttpResponse response = ex.getResponse();
                    //设置响应的数据格式和编码格式
                    response.getHeaders().add("Content-Type","application/json;charset=UTF-8");
                    //构建构建的对象
                    DataBuffer buffer=response.bufferFactory().wrap("请求方式错误,post请求body为null".getBytes());
                    //拦截请求，并返回结果
                    response.setStatusCode(HttpStatus.NOT_ACCEPTABLE);
                    return response.writeWith(Mono.just(buffer));
                }
            } else if ("GET".equals(method)) {
                Map requestQueryParams = serverHttpRequest.getQueryParams();
                //得到Get请求的请求参数后，做你想做的事
                log.info("访问网关uri:{},get请求参数：{}",url,requestQueryParams);
                return chain.filter(ex);
            }
            return chain.filter(ex);
        };
    }


    /**
     * 从Flux<DataBuffer>中获取字符串的方法
     * @return 请求体
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();

        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        //获取request body
        return bodyRef.get();
    }

    private DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }


}
