package com.example.demo1;

import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Single;

@Service
public class ExampleService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void exampleOne() {
        long start = System.currentTimeMillis();
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        Single<String> stringSingle = Single.just("hello").delay(15, TimeUnit.SECONDS);
        stringSingle.subscribe(completableFuture::complete,
                completableFuture::completeExceptionally);
        Mono<String> exclamation =  Mono.just("!").delayElement(Duration.ofSeconds(7)).log();
        Mono<String> mono = Mono.fromFuture(completableFuture).log();
        Mono<String> depay = Mono.just("World").delayElement(Duration.ofSeconds(10)).log();
        Mono<String> dot = Mono.just(".").delayElement(Duration.ofSeconds(2)).log();
        Mono<String> message = mono.zipWith(depay).map(tuple -> {
            logger.info("{} {}",tuple.getT1() , tuple.getT2());
            return tuple.getT1() +" "+tuple.getT2();
        }).log();

        Mono<String> phrase = message.zipWith(exclamation).map(tuple -> {
            return tuple.getT1()+" "+tuple.getT2();
        });
        Mono<String> phrase2 = phrase.zipWith(dot).map(tuple -> {
            return tuple.getT1()+" "+tuple.getT2();
        });
        logger.info("Message {}, {}s.", phrase2.block(), (System.currentTimeMillis() - start) /1000);
    }

    public void exampleTwo() {
        long start = System.currentTimeMillis();

        Mono<ImmutableList<String>> listMono = Mono
                .just(ImmutableList.of("1","2","3","4","5"))
                .delayElement(Duration.ofSeconds(1));

        Flux<String> flux = listMono.flatMapMany(Flux::fromIterable)
                .log().delaySequence(Duration.ofSeconds(1));
       Flux<String> out = flux.flatMap( s -> {
           int delay = RandomUtils.nextInt(1,5);
           return Flux.just("Number " + s).delaySequence(Duration.ofSeconds(delay));
       });
       List<String> list = out.collectList().log().block();
       logger.info("List {}", list);





    }
}
