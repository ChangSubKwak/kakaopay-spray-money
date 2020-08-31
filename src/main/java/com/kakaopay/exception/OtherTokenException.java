package com.kakaopay.exception;

import com.kakaopay.Message;

@SuppressWarnings("serial")
public class OtherTokenException extends RuntimeException {
    public OtherTokenException() {
        super(Message.M30.getMsg());
    }
}
