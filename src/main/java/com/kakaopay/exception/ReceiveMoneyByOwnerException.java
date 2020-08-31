package com.kakaopay.exception;

import com.kakaopay.Message;

@SuppressWarnings("serial")
public class ReceiveMoneyByOwnerException extends RuntimeException {
    public ReceiveMoneyByOwnerException() {
        super(Message.M21.getMsg());
    }
}
