package com.kakaopay;

public enum Message {
	M00("00", "정상완료"),
	M01("01", "X-USER-ID가 숫자형태 아님"),
	M02("02", "X-ROOM-ID가 문자형태 아님"),
	M03("03", "X-USER-ID값이 없음"),
	M04("04", "X-ROOM-ID값이 없음"),
	
	M10("10", "요청값 존재하지 않음"),
	
	M20("20", "뿌리기 당한 사용자는 한번만 받을 수 있음"),
	M21("21", "자신이 뿌리기한 건은 자신이 받을 수 없음"),
	M22("22", "뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만 받을 수 있음"),
	M23("23", "뿌린 건은 10분간만 유효함"),
	
	M30("30", "다른 사람의 뿌리기 건임"),
	M31("31", "유효하지 않은 토큰임"),
	M32("32", "조회가능한 일수인 7일이 경과하였음"),
	
	M99("ZZ", "Dummy(End of Message)");
	
	private String cd;
	private String msg;

	Message(String cd, String msg) {
		this.cd = cd;
		this.msg = msg;
	}

	public String getCd() {
		return cd;
	}

	public String getMsg() {
		return msg;
	}
	
}
