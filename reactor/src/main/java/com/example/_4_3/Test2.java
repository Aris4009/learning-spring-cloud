package com.example._4_3;

import reactor.core.publisher.Flux;

public class Test2 {

    public static void main(String[] args) {
        Flux<Integer> i = Flux.range(1, 3);
        i.subscribe();
    }
}
