package com.example._4_3;

import reactor.core.publisher.Flux;

public class Test4 {

	public static void main(String[] args) {
		Flux<Integer> i = Flux.range(1, 4).map(a -> {
			if (a <= 3) {
				return a;
			} else {
				throw new RuntimeException("Got to 4");
			}
		});
		i.subscribe(a -> System.out.println(a), error -> System.err.println("Error:" + error));
	}
}
