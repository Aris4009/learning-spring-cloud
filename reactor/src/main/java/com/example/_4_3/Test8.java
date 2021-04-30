package com.example._4_3;

import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

public class Test8 {

	public static void main(String[] args) {
		Flux.range(1, 10).subscribe(new BaseSubscriber<Integer>() {
			@Override
			public void hookOnSubscribe(Subscription subscription) {
				request(1);
			}

			@Override
			public void hookOnNext(Integer value) {
				System.out.println("Cancelling after have received " + value);
				cancel();
			}
		});
	}
}
