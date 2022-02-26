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
									  .flatMap(this::checkMonthlyMovements)
									  .flatMap(resp->deposit(resp, amount))
									  .flatMap(passiveTransactionProxy::updateAccount)
									  .flatMap(resp->saveHistory(idAccount, "deposit", amount, null));
		
	}

	@Override
	public Mono<History> withdrawFromAccount(String idAccount, Double amount) {
		
		return passiveTransactionProxy.getAccount(idAccount)
									  .flatMap(resp->checkBalance(resp, amount))
									  .flatMap(this::checkMonthlyMovements)
									  .flatMap(resp->withdraw(resp, amount))
									  .flatMap(passiveTransactionProxy::updateAccount)
									  .flatMap(resp->saveHistory(idAccount, "withdraw", amount, null));
		
	}
	
	@Override
	public Mono<History> transferToAccount(String idAccountFrom, String idAccountTo, Double amount) {
		return Mono.empty();
//		Mono<Account> accountFrom = passiveTransactionProxy.getAccount(idAccountFrom);
//		Mono<Account> accountTo = passiveTransactionProxy.getAccount(idAccountTo);
//		
//		return accountFrom.flatMap(monoAccountFrom -> {
//			if(monoAccountFrom.getMonthlyMovements()<=0 && monoAccountFrom.getBalance()<amount+monoAccountFrom.getCommission()) return Mono.empty();
//			
//			if(monoAccountFrom.getMonthlyMovements()>0 && monoAccountFrom.getBalance()>=amount) {
//				monoAccountFrom.setMonthlyMovements(monoAccountFrom.getMonthlyMovements()-1);
//				monoAccountFrom.setBalance(monoAccountFrom.getBalance()-amount);
//				
//				accountTo.flatMap(monoAccountTo -> {
//					monoAccountTo.setBalance(monoAccountTo.getBalance()+amount);
//					
//					return passiveTransactionProxy.updateAccount(monoAccountTo)
//													.doOnSuccess(updatedAccountTo -> {
//														if(updatedAccountTo.getId()!=null) {
//															saveHistory(updatedAccountTo.getId(), "transfer from: "+idAccountFrom, amount);
//														}
//													});
//				}).subscribe();
//				
//				return passiveTransactionProxy.updateAccount(monoAccountFrom)
//												.doOnSuccess(updatedAccountFrom -> {
//													if(updatedAccountFrom.getId()!=null) {
//														saveHistory(updatedAccountFrom.getId(), "transfer to: "+idAccountTo, amount);
//													}
//												});
//			}else if(monoAccountFrom.getBalance()>=amount+monoAccountFrom.getCommission()){
//				monoAccountFrom.setBalance(monoAccountFrom.getBalance()-amount-monoAccountFrom.getCommission());
//				
//				accountTo.flatMap(monoAccountTo -> {
//					monoAccountTo.setBalance(monoAccountTo.getBalance()+amount);
//					
//					return passiveTransactionProxy.updateAccount(monoAccountTo)
//													.doOnSuccess(updatedAccountTo -> {
//														if(updatedAccountTo.getId()!=null) {
//															saveHistory(updatedAccountTo.getId(), "transfered from: "+idAccountFrom, amount);
//														}
//													});
//				}).subscribe();
//				
//				return passiveTransactionProxy.updateAccount(monoAccountFrom)
//												.doOnSuccess(updatedAccountFrom -> {
//													if(updatedAccountFrom.getId()!=null) {
//														saveHistory(updatedAccountFrom.getId(), "transfer to: "+idAccountTo, amount);
//														saveHistory(updatedAccountFrom.getId(), "commission", updatedAccountFrom.getCommission());
//													}
//												});
//			}else {
//				return Mono.empty();
//			}
//		});
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
	
	public Mono<Account> checkMonthlyMovements(Account account){		
		
		Integer movements = account.getMonthlyMovements();
		
		if(movements>0) {
			account.setMonthlyMovements(movements-1);
			return Mono.just(account);
		}else {
			return Mono.error(()->new IllegalArgumentException("Not movements available"));
		}
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
