package com.kakaopay.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kakaopay.Message;
import com.kakaopay.model.Response;

@RestControllerAdvice
public class ResponseHandler {
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NotNumericUserIdException.class)
	public Response notNumericUserIdException(NotNumericUserIdException e) {
		return new Response(Message.M01.getCd(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NotAlphabeticalRoomIdException.class)
	public Response notAlphabeticalRoomIdException(NotAlphabeticalRoomIdException e) {
		return new Response(Message.M02.getCd(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NotExistUserIdException.class)
	public Response notExistUserIdException(NotExistUserIdException e) {
		return new Response(Message.M03.getCd(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NotExistRoomIdException.class)
	public Response notExistRoomIdException(NotExistRoomIdException e) {
		return new Response(Message.M04.getCd(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NotExistReqValException.class)
	public Response notExistReqValException(NotExistReqValException e) {
		return new Response(Message.M10.getCd(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ReceiveMoneyOverTwoTimesException.class)
	public Response requestOverTwoTimesException(ReceiveMoneyOverTwoTimesException e) {
		return new Response(Message.M20.getCd(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ReceiveMoneyByOwnerException.class)
	public Response requestByOwnerException(ReceiveMoneyByOwnerException e) {
		return new Response(Message.M21.getCd(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ReceiveMoneyByDifferentRoomUserException.class)
	public Response requestByDifferentRoomUserException(ReceiveMoneyByDifferentRoomUserException e) {
		return new Response(Message.M22.getCd(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ReceiveMoneyAfterTenMinuesException.class)
	public Response requestAfterTenMinuesException(ReceiveMoneyAfterTenMinuesException e) {
		return new Response(Message.M23.getCd(), e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(OtherTokenException.class)
	public Response otherTokenException(OtherTokenException e) {
		return new Response(Message.M30.getCd(), e.getMessage());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(NotValidTokenException.class)
	public Response notValidTokenException(NotValidTokenException e) {
		return new Response(Message.M31.getCd(), e.getMessage());
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(CheckMoneyAfterSevenDaysException.class)
	public Response checkMoneyAfterSevenDaysException(CheckMoneyAfterSevenDaysException e) {
		return new Response(Message.M32.getCd(), e.getMessage());
	}
}
