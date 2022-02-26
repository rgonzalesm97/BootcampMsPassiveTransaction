package com.bank.passiveTransaction.proxy;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.bank.passiveTransaction.model.History;
import com.bank.passiveTransaction.model.Account;

import reactor.core.publisher.Mono;

public class PassiveTransactionProxy {
	
	private final WebClient.Builder webClientBuilder = WebClient.builder();
	
	public Mono<Account> getAccount(String idProduct){
		return webClientBuilder.build()
								.get()
								.uri("http://localhost:8090/account/" + idProduct)
								.retrieve()
								.bodyToMono(Account.class);
	}
	
	public Mono<Account> updateAccount(Account account){
		return webClientBuilder.build()
				.put()
				.uri("http://localhost:8090/account/")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(account))
				.retrieve()
				.bodyToMono(Account.class);
	}
	
	public Mono<History> saveHistory(History history) {
		return webClientBuilder.build()
						.post()
						.uri("http://localhost:8090/history")
						.contentType(MediaType.APPLICATION_JSON)
						.body(BodyInserters.fromValue(history))
						.retrieve()
						.bodyToMono(History.class);
	}
}
