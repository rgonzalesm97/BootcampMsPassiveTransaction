package com.bank.passiveTransaction.service;

import com.bank.passiveTransaction.model.History;

import reactor.core.publisher.Mono;

public interface PassiveTransactionService {
	public Mono<History> depositIntoAccount(String idAccount, Double amount);
	public Mono<History> withdrawFromAccount(String idAccount, Double amount);
	public Mono<History> transferToAccount(String idAccountFrom, String idAccountTo, Double amount);
}