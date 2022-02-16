package com.bank.passiveTransaction.service;

import com.bank.passiveTransaction.model.Account;

import reactor.core.publisher.Mono;

public interface PassiveTransactionService {
	public Mono<Account> depositIntoAccount(String idAccount, Double amount);
	public Mono<Account> withdrawFromAccount(String idAccount, Double amount);
	public Mono<Account> transferToAccount(String idAccountFrom, String idAccountTo, Double amount);
}