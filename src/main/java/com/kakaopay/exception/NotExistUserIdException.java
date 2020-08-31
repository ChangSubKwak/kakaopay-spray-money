package com.kakaopay.exception;

import com.kakaopay.Message;

@SuppressWarnings("serial")
public class NotExistUserIdException extends RuntimeException {
    public NotExistUserIdException() {
    	super(Message.M03.getMsg());
    }
}
