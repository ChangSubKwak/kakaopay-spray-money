package com.kakaopay.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.Message;
import com.kakaopay.exception.CheckMoneyAfterSevenDaysException;
import com.kakaopay.exception.NotAlphabeticalRoomIdException;
import com.kakaopay.exception.NotExistReqValException;
import com.kakaopay.exception.NotExistRoomIdException;
import com.kakaopay.exception.NotExistUserIdException;
import com.kakaopay.exception.NotNumericUserIdException;
import com.kakaopay.exception.NotValidTokenException;
import com.kakaopay.exception.OtherTokenException;
import com.kakaopay.exception.ReceiveMoneyAfterTenMinuesException;
import com.kakaopay.exception.ReceiveMoneyByDifferentRoomUserException;
import com.kakaopay.exception.ReceiveMoneyByOwnerException;
import com.kakaopay.exception.ReceiveMoneyOverTwoTimesException;
import com.kakaopay.model.CompletedMoney;
import com.kakaopay.model.Money;
import com.kakaopay.model.Token;
import com.kakaopay.model.User;
import com.kakaopay.repo.MoneyJpaRepo;
import com.kakaopay.repo.TokenJpaRepo;
import com.kakaopay.repo.UserJpaRepo;
import com.kakaopay.util.DateUtil;
import com.kakaopay.util.FisherYatesShuffle;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/token")
public class TokenController {
	private final UserJpaRepo  userJpaRepo;
	private final TokenJpaRepo tokenJpaRepo;
	private final MoneyJpaRepo moneyJpaRepo;
	
	public boolean checkIsNumber(String s) {
		return s.matches("[0-9]+");
	}
	
	public boolean checkIsAlpha(String s) {
		return s.matches("[a-zA-Z]+");
	}
	
	public boolean existReqVal(String body) {
		Map<String, Object> bodyMap = getMapFromBody(body);
		if (!bodyMap.containsKey("amt") || !bodyMap.containsKey("personNum"))
			return false;
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getMapFromBody(String body) {
		Map<String, Object> bodyMap = null;
		try {
			bodyMap = new ObjectMapper().readValue(body, Map.class);
		} catch (JsonMappingException e ) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return bodyMap;
	}
	
	public String getUserId(HttpServletRequest request) {
		return (String) Optional.ofNullable(request.getHeader("X-USER-ID")).orElseThrow(() -> new NotExistUserIdException());
	}
	
	public String getRoomId(HttpServletRequest request) {
		return (String) Optional.ofNullable(request.getHeader("X-ROOM-ID")).orElseThrow(() -> new NotExistRoomIdException());
	}
	
	public void checkVaildHeaderAndBody(String userId, String roomId, String body) {
		if (!checkIsNumber(userId)) throw new NotNumericUserIdException();
		if (!checkIsAlpha(roomId))  throw new NotAlphabeticalRoomIdException();
		if (!existReqVal(body))		throw new NotExistReqValException();
	}
	
	public void checkVaildHeader(String userId, String roomId) {
		if (!checkIsNumber(userId)) throw new NotNumericUserIdException();
		if (!checkIsAlpha(roomId))  throw new NotAlphabeticalRoomIdException();
	}
	
	public String getCurrentDateTime() {
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(dt);
		return currentTime;
	}
	
	public void insertUserIfNotExist(String userId, String roomId) {
		// User 데이터 미등록시 저장
		User user = new User(userId, roomId);
		userJpaRepo.save(user);
	}
	
	@SuppressWarnings("static-access")
	public void insertToken(String tokenStr, String body, String userId, String roomId) {
		// Token 생성 및 저장
		Map<String, Object> bodyMap = getMapFromBody(body);
		String regDt = (String) bodyMap.get("testRegDt");	// 테스트용
		Token token = new Token().builder()
								 .token(tokenStr)
								 .amt(Long.valueOf( bodyMap.get("amt").toString()))
								 .personNum(Integer.valueOf(bodyMap.get("personNum").toString()))
								 .regDt( regDt == null ? getCurrentDateTime() : regDt)
								 .regId(userId)
								 .regRoomId(roomId)
								 .build();
		tokenJpaRepo.save(token);
	}
	
	/**
	 * 
	 * 뿌릴 금액을 인원수에 맞게 분배하여 저장
	 * 
	 * @param tokenStr
	 * @param body
	 */
	@SuppressWarnings("static-access")
	public void insertMoneyByPeopleNum(String tokenStr, String body) {
		Map<String, Object> bodyMap = getMapFromBody(body);
		long amt       = Long.parseLong(bodyMap.get("amt").toString());
		int  peopleNum = Integer.parseInt(bodyMap.get("personNum").toString());
		
		long partAmt = 0;
		for (int i = peopleNum ; i > 1 ; i--) {
			 partAmt = (long) ((Math.random() + 0.5 ) * (amt / (long)i) );
			 Money money = new Money().builder().token(tokenStr).partAmt(partAmt).build();
			 moneyJpaRepo.save(money);
			 amt -= partAmt;
		}
		moneyJpaRepo.save(new Money().builder().token(tokenStr).partAmt(amt).build());
	}
	
	public Map<String, Object> createSprayResData(String tokenStr) {
		Map<String, Object> map = new HashMap<>();
		map.put("token", tokenStr);
		map.put("resMsg", Message.M00.getMsg());
		map.put("resCd", Message.M00.getCd());
		
		return map;
	}
	
	public Token getTokenObject(String tokenStr) {
		Token token = null;
		try { token = tokenJpaRepo.findById(tokenStr).get(); }
		catch (NoSuchElementException e) {
			// TC31 유효하지 않은 토큰(발행되지 않은 토큰)을 요청하면 예외 발생
			throw new NotValidTokenException();
		}
		return token;
	}
	
	public void checkIfAlreadyRecv(String tokenStr, String userId) {
		// TC20 : 이미 받은 돈이 있는지 회수 확인
		int rcvCnt = moneyJpaRepo.countByTokenAndUserId(tokenStr, userId);
		if (rcvCnt > 0) throw new ReceiveMoneyOverTwoTimesException();
	}
	
	public void checkIfSelfRecv(String reqUserId, String hostUserId) {
		// TC21 : 자신이 뿌리기한 건은 자신이 받을 수 없습니다
		if (reqUserId.equals(hostUserId))
			throw new ReceiveMoneyByOwnerException();
	}
	
	public void checkIfRecvInSprayedRoom(String reqRoomId, String hostRoomId) {
		// TC22 : 뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받기 요청 가능
		if (!reqRoomId.equals(hostRoomId))
			throw new ReceiveMoneyByDifferentRoomUserException();
	}
	
	public void checkIfRecvWithinTenMin(Token token) {
		// TC23 : 뿌린 건에 대한 받기는 10분간만 유효합니다.
		int diffMin = DateUtil.getDiffBetweenCurrentTime(token.getRegDt(), 1);
		if (diffMin >= 10)
			throw new ReceiveMoneyAfterTenMinuesException();
	}
	
	public void resterUserIdToRecvMoney(Money money, String userId) {
		// rcv_id 등록하기		
		money.setRcvId(userId);
		moneyJpaRepo.save(money);
	}
	
	public Map<String, Object> createRecvResData(Long recvAmt) {
		Map<String, Object> map = new HashMap<>();
		map.put("recvAmt", recvAmt);
		map.put("resMsg", Message.M00.getMsg());
		map.put("resCd", Message.M00.getCd());
		return map;
	}

	public void checkIfTryOtherToken(String reqUserId, String hostUserId) {
		// TC30 : 다른 사람의 뿌리기건은 조회할 수 없음
		if (!reqUserId.equals(hostUserId))
			throw new OtherTokenException();
	}
	
	public void checkIfTokenPassSevenDay(String getRegDt) {
		// TC32 : 7일이 지난 뿌리기건은 조회할 수 없음 
		int diffDays = DateUtil.getDiffBetweenCurrentTime(getRegDt, 1440);
		if (diffDays >= 7)
			throw new CheckMoneyAfterSevenDaysException();
	}

	public Map<String, Object> createCheckResData(Token token) {
		//List<CompletedMoney> list = (List<CompletedMoney>) moneyJpaRepo.findRecvCompletedPartInfoAllByToken(tokenStr);
		List<CompletedMoney> list = (List<CompletedMoney>) moneyJpaRepo.findRecvCompletedPartInfoAllByToken(token.getToken());
		
		long recvCmptAmt = 0;
		for (int i = 0 ; i < list.size() ; i++)
			recvCmptAmt += list.get(i).getPartAmt();
		
		Map<String, Object> map = new HashMap<>();
		map.put("sprayDt", token.getRegDt());		// 뿌린 시각
		map.put("sprayAmt", token.getAmt());		// 뿌린 금액
		map.put("recvCmptAmt", recvCmptAmt);		// 받기 완료된 금액
		map.put("cmptInfo", list);
		map.put("resMsg", Message.M00.getMsg());
		map.put("resCd", Message.M00.getCd());

		return map;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * 
	 * 뿌리기 API 
	 * 
	 * @param  request		클라이언트에서 요청한 HTTP 전문전체
	 * @param  body			받은 HTTP전문중 바디 부분 
	 * @return map			응답 맵 객체				
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Map<String, Object> sprayMoney(HttpServletRequest request, @RequestBody final String body) {
		String userId = getUserId(request);
		String roomId = getRoomId(request);
		String tokenStr = FisherYatesShuffle.getShuffleVal();
		
		checkVaildHeaderAndBody(userId, roomId, body);
		insertUserIfNotExist(userId, roomId);
		insertToken(tokenStr, body, userId, roomId);
		insertMoneyByPeopleNum(tokenStr, body);
		
		return createSprayResData(tokenStr);
	}
	
	/**
	 * 
	 * 받기 API
	 * 
	 * @param  request		클라이언트에서 요청한 HTTP 전문전체		
	 * @param  tokenStr		요청된 path상의 토큰 값
	 * @return map			응답 맵 객체
	 */
	@RequestMapping(value = "/{val}", method=RequestMethod.PUT)
	public Map<String, Object> receiveMoney(HttpServletRequest request, @PathVariable("val") String tokenStr) {
		String userId = getUserId(request);
		String roomId = getRoomId(request);
		Token token   = getTokenObject(tokenStr);
		Money money = moneyJpaRepo.findOneByToken(tokenStr);				// 등록되지 않은(rcv_id is null) 분배건 하나 출력하기
		
		checkVaildHeader(userId, roomId);
		insertUserIfNotExist(userId, roomId);
		checkIfAlreadyRecv(tokenStr, userId);
		
		User  reqUser = userJpaRepo.findById(userId).get();					// 토큰 요청 객체 얻기
		User  hostUser = userJpaRepo.findById(token.getRegId()).get();		// 토큰 발행 유저 객체 얻기
		checkIfSelfRecv(reqUser.getUserId(), hostUser.getUserId());
		checkIfRecvInSprayedRoom(reqUser.getRoomId(), hostUser.getRoomId());
		checkIfRecvWithinTenMin(token);
		resterUserIdToRecvMoney(money, userId);

		return createRecvResData(money.getPartAmt());
	}

	/**
	 * 
	 * 조회 API
	 * 
	 * @param  request		클라이언트에서 요청한 HTTP 전문전체		
	 * @param  tokenStr		요청된 path상의 토큰 값
	 * @return map			응답 맵 객체
	 */
	@RequestMapping(value = "/{val}", method=RequestMethod.GET)
	public Map<String, Object> checkRecvStatusOfMoney(HttpServletRequest request, @PathVariable("val") String tokenStr) {
		String userId = getUserId(request);
		String roomId = getRoomId(request);
		Token token   = getTokenObject(tokenStr);
		User  reqUser  = userJpaRepo.findById(userId).get();					// 토큰 요청 객체 얻기
		User  hostUser  = userJpaRepo.findById(token.getRegId()).get();			// 토큰 발행 유저 객체 얻기
		
		checkVaildHeader(userId, roomId);
		insertUserIfNotExist(userId, roomId);
		checkIfTryOtherToken(reqUser.getUserId(), hostUser.getUserId());
		checkIfTokenPassSevenDay(token.getRegDt());
		
		return createCheckResData(token);
	}
}
