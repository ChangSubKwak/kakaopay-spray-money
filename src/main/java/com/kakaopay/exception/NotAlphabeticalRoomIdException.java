package com.kakaopay.exception;

import com.kakaopay.Message;

@SuppressWarnings("serial")
public class NotAlphabeticalRoomIdException extends RuntimeException {
    public NotAlphabeticalRoomIdException() {
        super(Message.M02.getMsg());
    }
}
