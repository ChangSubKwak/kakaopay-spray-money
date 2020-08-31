package com.kakaopay.exception;

import com.kakaopay.Message;

@SuppressWarnings("serial")
public class ReceiveMoneyOverTwoTimesException extends RuntimeException {
    public ReceiveMoneyOverTwoTimesException() {
        super(Message.M20.getMsg());
    }
}
