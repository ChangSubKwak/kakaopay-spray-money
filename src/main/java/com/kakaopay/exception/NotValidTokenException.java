package com.kakaopay.exception;

import com.kakaopay.Message;

@SuppressWarnings("serial")
public class NotValidTokenException extends RuntimeException {
    public NotValidTokenException() {
        super(Message.M31.getMsg());
    }
}
