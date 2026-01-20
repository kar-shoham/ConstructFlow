package com.constructflow.api_gateway_service.filter;

import com.constructflow.api_gateway_service.dto.JwtPayloadDto;
import com.constructflow.api_gateway_service.utils.JwtUtils;
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
            if (Objects.nonNull(config.getRelaxedPaths()) && url.contains(config.getRelaxedPaths())) {
                return chain.filter(exchange);
            }

            String bearerToken = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (!StringUtils.hasText(bearerToken) || !bearerToken.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            JwtPayloadDto payloadDto;
            try {
                String token = bearerToken.split("Bearer ")[1];
                payloadDto = jwtUtils.verifyJwt(token);
            }
            catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

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
