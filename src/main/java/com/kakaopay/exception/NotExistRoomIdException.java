package com.kakaopay.exception;

import com.kakaopay.Message;

@SuppressWarnings("serial")
public class NotExistRoomIdException extends RuntimeException {
    public NotExistRoomIdException() {
    	super(Message.M04.getMsg());
    }
}
