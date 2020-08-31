package com.kakaopay.exception;

import com.kakaopay.Message;

@SuppressWarnings("serial")
public class NotNumericUserIdException extends RuntimeException {
    public NotNumericUserIdException() {
        super(Message.M01.getMsg());
    }
}
