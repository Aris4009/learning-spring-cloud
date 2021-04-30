package com.example._4_3;

import reactor.core.publisher.Flux;

public class Test6 {

	public static void main(String[] args) {
		Flux<Integer> i = Flux.range(1, 12);
		i.subscribe(a -> System.out.println(a), error -> System.err.println(error), () -> System.out.println("Done"),
				sub -> sub.request(10));
	}
}
