package com.example._4_3;

import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

public class Test7 {

	public static void main(String[] args) {
		SampleSub<Integer> i = new SampleSub<>();
		Flux<Integer> flux = Flux.range(1, 4);
		flux.subscribe(i);
	}

	static class SampleSub<T> extends BaseSubscriber<T> {

		@Override
		public void hookOnSubscribe(Subscription subscription) {
			System.out.println("Sub");
			request(1);
		}

		@Override
		public void hookOnNext(T value) {
			System.out.println(value);
			request(1);
		}
	}
}
