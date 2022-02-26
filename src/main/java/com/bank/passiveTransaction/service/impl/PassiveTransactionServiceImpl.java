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
	public Mono<History> depositIntoAccount(String idAccount, Double amount) {
		
		return passiveTransactionProxy.getAccount(idAccount)
									  .flatMap(resp->checkMonthlyMovements(resp, amount))
									  .flatMap(resp->deposit(resp, amount))
									  .flatMap(passiveTransactionProxy::updateAccount)
									  .flatMap(resp->saveHistory(idAccount, "deposit", amount, null));
		
	}

	@Override
	public Mono<History> withdrawFromAccount(String idAccount, Double amount) {
		
		return passiveTransactionProxy.getAccount(idAccount)
									  .flatMap(resp->checkBalance(resp, amount))
									  .flatMap(resp->checkMonthlyMovements(resp, amount))
									  .flatMap(resp->withdraw(resp, amount))
									  .flatMap(passiveTransactionProxy::updateAccount)
									  .flatMap(resp->saveHistory(idAccount, "withdraw", amount, null));
		
	}
	
	@Override
	public Mono<History> transferToAccount(String idAccountFrom, String idAccountTo, Double amount) {
		
		return passiveTransactionProxy.getAccount(idAccountFrom)
									  .flatMap(resp->checkBalance(resp, amount))
									  .flatMap(resp->checkMonthlyMovements(resp, amount))
									  .flatMap(resp->makeTransaction(resp, idAccountTo, amount));
	}
	
	
	//TRANSACTIONS UTIL METHODS
	public Mono<Account> checkBalance(Account account, Double amount){
		return account.getBalance()>amount ? Mono.just(account)
										   : Mono.error(()->new IllegalArgumentException("Not enough balance"));
	}
	
	public Mono<Account> deposit(Account account, Double amount){
		account.setBalance(account.getBalance()+amount);
		return Mono.just(account);
	}
	
	public Mono<Account> withdraw(Account account, Double amount){
		account.setBalance(account.getBalance()-amount);
		return Mono.just(account);
	}
	
	public Mono<Account> checkMonthlyMovements(Account account, Double amount){		
		
		Integer movements = account.getMonthlyMovements();
		
		if(movements>0) {
			account.setMonthlyMovements(movements-1);
			return Mono.just(account);
		}else {
			if(account.getBalance()>amount+account.getCommission()) {
				account.setBalance(account.getBalance()-account.getCommission());
				saveHistory(account.getId(), "commission", account.getCommission(), null).subscribe();
				return Mono.just(account);
			}else {
				return Mono.error(() -> new IllegalArgumentException("Not enough balance"));
			}
		}
	}
	
	public Mono<History> makeTransaction(Account accountFrom, String idAccountTo, Double amount){
		return passiveTransactionProxy.getAccount(idAccountTo)
								      .flatMap(accountTo->deposit(accountTo, amount))
								      .flatMap(passiveTransactionProxy::updateAccount)
								      .flatMap(accountTo->withdraw(accountFrom, amount))
								      .flatMap(passiveTransactionProxy::updateAccount)
								      .flatMap(resp->saveHistory(idAccountTo, "transfer from", amount, accountFrom.getId()))
								      .flatMap(resp->saveHistory(accountFrom.getId(), "transfer to", amount, idAccountTo));
	}
	
	public Mono<History> saveHistory(String idProduct,
									String type,
									Double amount,
									String idThirdPartyProduct) {
		
		History history = new History();
		history.setIdProduct(idProduct);
		history.setType(type);
		history.setAmount(amount);
		history.setIdThirdPartyProduct(idThirdPartyProduct);
		history.setDate(new Date());
		
		return passiveTransactionProxy.saveHistory(history);
		
	}

}
