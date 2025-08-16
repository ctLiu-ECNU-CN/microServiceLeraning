package com.heima.gateway.filters;

import cn.hutool.core.text.AntPathMatcher;
import com.heima.gateway.config.AuthProperties;
import com.hmall.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import com.heima.gateway.utils.JwtTool;
import java.util.List;


@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTool jwtTool;
    private final AuthProperties authProperties;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        获取用户信息
        ServerHttpRequest request = exchange.getRequest();
//        判断是否要拦截
        if(isExclude(request.getPath().toString())){
            System.out.println(request.getPath().toString());
            return chain.filter(exchange);
        }
        String token = null;
//        获取token (请求头是个map)
        List<String> headers = request.getHeaders().get("authorization");
        if(headers != null && !headers.isEmpty()){
            token = headers.get(0);
        }
//        解析token
        Long userId = null;
        try {
             userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
//            拦截,回应401
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
//        TODO 传递用户信息
        System.out.println("userId = "+ userId);

//放心
        return chain.filter(exchange);
    }

    private boolean isExclude(String string) {

        for (String pathPattern: authProperties.getExcludePaths()){
            System.out.println(pathPattern + "  111");
            if(antPathMatcher.match(pathPattern, string)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
