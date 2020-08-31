package com.kakaopay.exception;

import com.kakaopay.Message;

@SuppressWarnings("serial")
public class ReceiveMoneyByDifferentRoomUserException extends RuntimeException {
    public ReceiveMoneyByDifferentRoomUserException() {
        super(Message.M22.getMsg());
    }
}
