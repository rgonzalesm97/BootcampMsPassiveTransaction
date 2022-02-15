package com.bank.passiveTransaction.model;

import java.util.Date;

import lombok.Data;

@Data
public class History {

	private String id;
	private String idProduct;
	private String type;
	private Double amount;
	private Date date;
}
