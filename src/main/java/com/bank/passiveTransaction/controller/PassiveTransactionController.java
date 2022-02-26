package com.bank.passiveTransaction.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.passiveTransaction.model.History;
import com.bank.passiveTransaction.service.PassiveTransactionService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/passive-transaction")
public class PassiveTransactionController {
	
	private final PassiveTransactionService passiveTransactionService;
	
	@PostMapping("/deposit/{id}")
	public Mono<History> deposit(@PathVariable("id") String idProduct,
								@RequestParam Double amount) {
		
		return passiveTransactionService.depositIntoAccount(idProduct, amount);
		
	}
	
	@PostMapping("/withdraw/{id}")
	public Mono<History> withdraw(@PathVariable("id") String idProduct,
								@RequestParam Double amount) {
		
		return passiveTransactionService.withdrawFromAccount(idProduct, amount);
		
	}
	
	@PostMapping("/transfer/{idFrom}/{idTo}")
	public Mono<History> transferTo(@PathVariable("idFrom") String idProductFrom,
									@PathVariable("idTo") String idProductTo,
									@RequestParam Double amount) {
		
		return passiveTransactionService.transferToAccount(idProductFrom, idProductTo, amount);

	}
	
}
