package com.constructflow.api_gateway_service.filter;

import com.constructflow.api_gateway_service.dto.JwtPayloadDto;
import com.constructflow.api_gateway_service.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@Slf4j
public class JwtGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JwtGatewayFilterFactory.Config>
{
    @Autowired
    private JwtUtils jwtUtils;

    public JwtGatewayFilterFactory()
    {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config)
    {
        GatewayFilter gatewayFilter = (exchange, chain) -> {
            String url = exchange.getRequest().getURI().getPath();
            log.info("JwtGatewayFilter - Path: {}, RelaxedPaths: {}", url, config.getRelaxedPaths());

            if (Objects.nonNull(config.getRelaxedPaths()) && url.contains(config.getRelaxedPaths())) {
                log.info("JwtGatewayFilter - Relaxed path matched, skipping JWT check for: {}", url);
                return chain.filter(exchange);
            }

            String bearerToken = exchange.getRequest().getHeaders().getFirst("Authorization");
            log.info("JwtGatewayFilter - Bearer token present: {}", StringUtils.hasText(bearerToken));

            if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
                log.error("JwtGatewayFilter - No valid bearer token, returning 401");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            JwtPayloadDto payloadDto;
            try {
                String token = bearerToken.split("Bearer ")[1];
                payloadDto = jwtUtils.verifyJwt(token);
                log.info("JwtGatewayFilter - JWT verified, userId: {}", payloadDto.getUserId());
            }
            catch (Exception e) {
                log.error("JwtGatewayFilter - JWT verification failed: {}", e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            log.info("JwtGatewayFilter - Adding headers - userId: {}, username: {}, role: {}",
                    payloadDto.getUserId(), payloadDto.getUsername(), payloadDto.getUserRole());

            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", payloadDto.getUserId().toString())
                    .header("X-User-Username", payloadDto.getUsername())
                    .header("X-User-Role", payloadDto.getUserRole())
                    .build();

            exchange = exchange.mutate().request(request).build();
            return chain.filter(exchange);
        };

        return new OrderedGatewayFilter(gatewayFilter, config.getOrder());
    }

    public static class Config
    {
        private Integer order;

        private String relaxedPaths;

        public int getOrder()
        {
            return order;
        }

        public void setOrder(Integer order)
        {
            this.order = order;
        }

        public String getRelaxedPaths()
        {
            return relaxedPaths;
        }

        public void setRelaxedPaths(String relaxedPaths)
        {
            this.relaxedPaths = relaxedPaths;
        }
    }
}
