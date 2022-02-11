package com.bank.passiveTransaction.model;

import lombok.Data;

@Data
public class Product {

	private String id;
	private String idClient;
	private String type;
	private Long maintenance;
	private Integer movements;
	private Long credit;
	private Long balance;
	private Long debt;
}
