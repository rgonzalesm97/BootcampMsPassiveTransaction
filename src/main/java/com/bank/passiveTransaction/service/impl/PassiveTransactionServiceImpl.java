package com.bank.passiveTransaction.service.impl;

import org.springframework.stereotype.Service;

import com.bank.passiveTransaction.model.Account;
import com.bank.passiveTransaction.proxy.PassiveTransactionProxy;
import com.bank.passiveTransaction.service.PassiveTransactionService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PassiveTransactionServiceImpl implements PassiveTransactionService{
	
	private PassiveTransactionProxy passiveTransactionProxy = new PassiveTransactionProxy();

	@Override
	public Mono<Account> depositIntoAccount(String idAccount, Double amount) {
		
		Mono<Account> actualAccount = passiveTransactionProxy.getAccount(idAccount);
		
		return actualAccount.flatMap(x -> {
			if(x.getMonthlyMovements()>0) {
				x.setMonthlyMovements(x.getMonthlyMovements()-1);
				x.setBalance(x.getBalance() + amount);

				return passiveTransactionProxy.updateAccount(x);
			}else {
				return Mono.empty();
			}
		});
	}

	@Override
	public Mono<Account> withdrawFromAccount(String idAccount, Double amount) {
		
		Mono<Account> actualAccount = passiveTransactionProxy.getAccount(idAccount);
		
		return actualAccount.flatMap(x -> {
			if(x.getMonthlyMovements()>0 && x.getBalance()>=amount) {
				x.setMonthlyMovements(x.getMonthlyMovements()-1);
				x.setBalance(x.getBalance()-amount);
				
				return passiveTransactionProxy.updateAccount(x);
			}else {
				return Mono.empty();
			}
		});
	}
}
