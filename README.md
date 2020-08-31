# kakaopay-spray-money

카카오페이 뿌리기 기능 구현하기

## 차례
1. [핵심 문제해결 전략](#how-to-solve)
2. [개발 환경](#dev-env)
3. [설치 및 실행 방법](#how-to-install)
4. [API 명세](#api-spec)
5. [테스트케이스 명세](#testcase)
6. [DB스키마](#db-schema)

<br><br>
<h2 id="how-to-solve">
    1. 핵심 문제해결 전략
</h2>

<h3>ㆍ모든(뿌리기/받기/조회) REST API전문 포맷은 JSON으로 하였음</h3>
<h4>
<pre>
  - 전문 통신프로토콜은 HTTP, HTML전문의 BODY포맷은 JSON
  - 관련 라이브러리 구하기 쉽게 구할 수 있음(https://mvnrepository.com)
  - 트러블슈팅 정보를 쉽게 찾을 수 있을 것 같아 선택하였음(http://json.com)
</pre>
</h4>
<br>

<h3>ㆍ네이밍 룰</h3>
<h4>
<pre>
  - Java소스내 변수, 함수와 JSON의 키는 camel표기법을 따름
  - DB의 테이블명은 앞에 't'를 붙였음
  - DB의 컬럼명은 의미가 구분되는(camel표기의 대문자지점)곳은 '\_'로 연결후 소문자로 시작
    ex) regDt(camel) --> reg_dt
</pre>
</h4>
<br>

<h3>ㆍ단위 테스트</h3>
<h4>
<pre>
  - JUnit기반 MockMvc를 주로 사용한 테스트케이스 작성
  - 테스트케이스 함수명은 TC(TestCase)를 접두어로 응답코드(두자리)를 접미어로 붙여 사용
  - 테스트케이스 함수명은 해당코드에 대한 예외를 발생시키는 것임
</pre>
</h4>
<br>

<h3>ㆍAPI공통</h3>
<h4>
<pre>
  - 모든 요청 HTML전문의 Header의 userId(유저아이디)와 roomId(방아이디)는 기본적으로 DB에 저장
  - userId에 문자가 존재하거나, roomId에 숫자가 존재하면 예외 발생
  - 모든 응답 HTML전문의 응답BODY에는 resCd(응답코드)와 resMsg(응답메시지)를 포함
</pre>
</h4>
<br>

  
<h3>ㆍ뿌리기API기능 구현 설명</h3>
<h4>
<pre>
  - 뿌릴 금액, 뿌릴 인원에 다음과 같이 메타데이터 결정 cf) amt(금액), personNum(인원수)
  - 고유 token 생성은 [a-zA-Z]에 대하여 3자리의 무작위 문자열 생성
  - 문자열 생성은 Fisher-Yates Shuffle(https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle) 이용
  - 랜덤값 생성은 예측이 어렵도록 SecureRandom객체 생성하였음
  - DB에 데이터 삽입 내용중 등록시간을 저장해야 받기/조회시의 시간과 비교하여 예외처리 가능
</pre>
</h4>
<br>

<h3>ㆍ받기API기능 구현 설명</h3>
<h4>
<pre>
  - 할당되지 않은 분배건은 ttoken(토큰테이블)에서 reg_id(등록일시)에 데이터 없는 row가 하나라도 있음면 분배 가능한 것임
  - token으로 조회한 분배건중 regId에 요청자 userId가 존재하면 받을 수 없음
  - 요청자 userId와 token을 뿌린 userId와 같으면 자신이 뿌린 것을 받으려고 시도하는 것임
  - 요청자의 roomId가 있으며, 요청 token기반 뿌려진 금액의 roomId와 다르면 받을 수 없음
  - 뿌리기API에서 실행된 등록시간 기준 10분 지났는지 확인하여 처림
</pre>
</h4>
<br>

<h3>ㆍ조회API기능 구현 설명</h3>
<h4>
<pre>
  - token으로 조회한 ttoken테이블을 조회하면 regDt(뿌린시각), amt(뿌린금액)을 알 수 있음
  - token으로 tmoney테이블을 조회하면 완료된 분배건을 확인할 수 있음
  - tmoney테이블에서 조회된 데이터를 기반으로 완료된 건(rcv_id is not null)의 part_amt의 합을 구하면 받기 완료된 금액을 알 수 있음
  - 건단위 받은 금액, 받은 사용자 아이디를 리스트 데이터에 삽입하여 응답값의 body에 삽입처리
</pre>
</h4>
<br>

<h2 id="dev-env">    2. 개발 환경</h2>

#### 기술 스택
* JDK 1.8
* SpringBoot 2.3.3
* Maven
* Lombok 1.18.12
* JSON
* mysql  8.0.21
* JPA(hibernate)
* PostMan
* STS 4.7.1

# 폴더 구성
```bash
├── kakaopay-backend-server-project
│   ├── src/main/java
│   │   ├── com.kakaopay
│   │   │   ├── controller      # 클라이언트로 부터 요청 데이터를 처리하는 TokenController 클래스 포함
│   │   │   ├── exception       # RuntimeException을 상속받은 클래스들과 이것들을 처리하는 핸들러 클래스 포함
│   │   │   ├── model           # DB테이블과 일치하는 클래스 및 응답용 클래스 포함
│   │   │   ├── repo            # JpaRepository를 상속받아 DB처리용 함수가 구현된 클래스 포함
│   │   │   └── util            # 날짜계산, 빈데이터 JSON변환, token값 생성 함수를 보유한 클래스 포함
│   │   ├── Application.java    # 이 프로젝트의 실행 진입점 main함수를 보유한 클래스
│   │   └── Message.java        # 예외발생시 반환용 응답값 및 응답코드를 보유한 열거형 클래스
│   ├── src/main/resources      # 
│


└── calendar-back
    └── src
        ├── app             # back source code를 포함(controllers, hook, models, policies등)
        ├── bifido          # express 서버의 미들웨어 및 설정 등을 포함
        └── config          # 서버 route, api end point, cors, mongodb 설정 값들을 포함

<h2 id="dev-env">    3. 설치 및 실행 방법</h2>
<h2 id="dev-env">    4. API 명세</h2>
<h2 id="dev-env">    5. 테스트케이스 명세</h2>
<h2 id="dev-env">    6. DB스키마</h2>
    
