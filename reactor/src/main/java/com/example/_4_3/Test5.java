package com.example._4_3;

import reactor.core.publisher.Flux;

public class Test5 {

	public static void main(String[] args) {
		Flux<Integer> i = Flux.range(1, 4);
		i.subscribe(a -> System.out.println(a), error -> System.err.println(error), () -> System.out.println("Done"));
	}
}
