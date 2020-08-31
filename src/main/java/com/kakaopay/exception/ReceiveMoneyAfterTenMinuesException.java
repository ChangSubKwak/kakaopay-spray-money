package com.kakaopay.exception;

import com.kakaopay.Message;

@SuppressWarnings("serial")
public class ReceiveMoneyAfterTenMinuesException extends RuntimeException {
    public ReceiveMoneyAfterTenMinuesException() {
        super(Message.M23.getMsg());
    }
}
