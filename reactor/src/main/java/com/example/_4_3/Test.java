package com.example._4_3;

import java.util.Arrays;
import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Test {

	public static void main(String[] args) {
		Flux<String> seq1 = Flux.just("foo", "bar", "foobar");
		List<String> iterable = Arrays.asList("foo", "bar", "foobar");
		Flux<String> seq2 = Flux.fromIterable(iterable);

		Mono<String> noData = Mono.empty();
		Mono<String> data = Mono.just("foo");
		Flux<Integer> numbersFromFiveToSeven = Flux.range(5, 3);
	}
}
