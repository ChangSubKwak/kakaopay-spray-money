package com.kakaopay.exception;

import com.kakaopay.Message;

@SuppressWarnings("serial")
public class CheckMoneyAfterSevenDaysException extends RuntimeException {
    public CheckMoneyAfterSevenDaysException() {
        super(Message.M32.getMsg());
    }
}
