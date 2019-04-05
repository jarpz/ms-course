package com.microservice.song;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.http.HttpHeaders.HOST;

@Slf4j
@Component
public class HostHeaderResponseDecorator implements WebFilter {

    private AtomicReference<String> hostName = new AtomicReference<>();

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange,
                             WebFilterChain webFilterChain) {

        serverWebExchange.getResponse()
                .getHeaders().add(HOST, hostName());
        return webFilterChain.filter(serverWebExchange);
    }


    private String hostName() {
        return hostName.updateAndGet(current -> {
            if (Objects.isNull(current)) {
                try {
                    return InetAddress
                            .getLocalHost()
                            .getHostAddress();
                } catch (UnknownHostException ex) {
                    return "UnknownHost";
                }
            }
            return current;
        });
    }

}