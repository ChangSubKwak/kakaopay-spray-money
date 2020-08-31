package com.kakaopay.exception;

import com.kakaopay.Message;

@SuppressWarnings("serial")
public class NotExistReqValException extends RuntimeException {
    public NotExistReqValException() {
    	super(Message.M10.getMsg());
    }
}
