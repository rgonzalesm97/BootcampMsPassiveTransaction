package com.bank.passiveTransaction.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.bank.passiveTransaction.service.PassiveTransactionService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Service
public class PassiveTransactionServiceImpl implements PassiveTransactionService{
	
	private WebClient productClient = WebClient.builder().baseUrl("http://localhost:8081/product").build();
	
	@Override
	public void deposit(String idProduct, Long amount) {
		
	}
	
}
