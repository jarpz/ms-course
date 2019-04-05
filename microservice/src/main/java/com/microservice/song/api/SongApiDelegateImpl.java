package com.microservice.song.api;

import com.microservice.song.api.models.RegisterSongRequest;
import com.microservice.song.api.models.RegisterSongResponse;
import com.microservice.song.api.models.SongResponse;
import com.microservice.song.mapping.SongMapper;
import com.microservice.song.persistence.SongRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;

import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static reactor.core.publisher.Flux.fromIterable;
import static reactor.core.publisher.Mono.*;
import static reactor.core.publisher.Mono.fromCallable;
import static reactor.core.scheduler.Schedulers.elastic;

@Component
@RequiredArgsConstructor
public class SongApiDelegateImpl implements SongsApiDelegate {

    @NonNull
    private SongRepository repository;
    @NonNull
    private SongMapper mapper;

    private static MessageFormat DELETE_ERROR_MESSAGE =
            new MessageFormat("Failed to delete song with id: {0} not found");

    @Override
    public Mono<ResponseEntity<Flux<SongResponse>>> getAllSongs(
            ServerWebExchange exchange) {
        return just(ok(
                fromIterable(repository.findAll())
                        .subscribeOn(elastic())
                        .map(mapper::modelToResponse)));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteSongById(
            Integer id,
            ServerWebExchange exchange) {
        return fromSupplier(() -> repository
                        .findById(id))
                .subscribeOn(elastic())
                .map(optional -> optional
                        .map(song -> {
                            repository.delete(song);
                            return ok();
                        })
                        .orElse(status(PRECONDITION_FAILED)
                                .header("X-Error", DELETE_ERROR_MESSAGE.format(new Object[]{id})))
                        .build());

    }

    @Override
    public Mono<ResponseEntity<SongResponse>> getSongById(
            Integer id,
            ServerWebExchange exchange) {
        return fromSupplier(() -> repository.findById(id))
                .flatMap(optional -> optional
                        .map(Mono::just)
                        .orElse(empty()))
                .map(mapper::modelToResponse)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<RegisterSongResponse>> newSong(
            Mono<RegisterSongRequest> request,
            ServerWebExchange exchange) {
        return request
                .map(mapper::requestToModel)
                .map(repository::save)
                .subscribeOn(elastic())
                .map(result -> new RegisterSongResponse()
                        .id(result.getId()))
                .map(ResponseEntity::ok);
    }


    @Bean
    public RouterFunction<ServerResponse> exposeProperty(
            Environment environment) {

        return route(GET("/property/{key}"),
                request -> ServerResponse
                        .ok().contentType(TEXT_PLAIN)
                        .body(fromCallable(() -> environment
                                .getProperty(request
                                        .pathVariable("key"), "none")), String.class));
    }
}
