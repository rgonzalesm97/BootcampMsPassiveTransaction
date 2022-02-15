package com.bank.passiveTransaction.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.bank.passiveTransaction.model.History;
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

				return passiveTransactionProxy.updateAccount(x)
												.doOnSuccess(y -> {
													if(y.getId()!=null) {
														saveHistory(y.getId(), "deposit into account", amount);
													}
												});
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
				
				return passiveTransactionProxy.updateAccount(x)
												.doOnSuccess(y -> {
													if(y.getId()!=null) {
														saveHistory(y.getId(), "withdraw form account", amount);
													}
												});
			}else {
				return Mono.empty();
			}
		});
	}
	
	public void saveHistory(String idProduct,
							String type,
							Double amount) {
		History history = new History();
		history.setIdProduct(idProduct);
		history.setType(type);
		history.setAmount(amount);
		history.setDate(new Date());
		
		passiveTransactionProxy.saveHistory(history);

	}	
}
