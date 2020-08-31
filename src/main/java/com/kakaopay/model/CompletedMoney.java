package com.kakaopay.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class CompletedMoney {
	private long partAmt;
	private String rcvId;
}
