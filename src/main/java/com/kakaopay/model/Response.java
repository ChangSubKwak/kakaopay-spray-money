package com.kakaopay.model;

import lombok.Getter;

@Getter
public class Response {
	private String resCd;
	private String resMsg;
	
	public Response(String resCode, String resMsg) {
		this.resCd = resCode;
		this.resMsg = resMsg;
	}
	
//	public String getResMsg() {
//		return resMsg;
//	}
//	
//	public String getResCd() {
//		return resCd;
//	}
}
