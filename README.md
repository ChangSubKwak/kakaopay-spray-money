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

### 폴더 구성
```bash
└── kakaopay-backend-server-project
    ├── src/main/java
    │   ├── com.kakaopay
    │   │   ├── controller              # 클라이언트로 부터 요청 데이터를 처리하는 TokenController 클래스 포함
    │   │   ├── exception               # RuntimeException을 상속받은 클래스들과 이것들을 처리하는 핸들러 클래스 포함
    │   │   ├── model                   # DB테이블과 일치하는 클래스 및 응답용 클래스 포함
    │   │   ├── repo                    # JpaRepository를 상속받아 DB처리용 함수가 구현된 클래스 포함
    │   │   └── util                    # 날짜계산, 빈데이터 JSON변환, token값 생성 함수를 보유한 클래스 포함
    │   ├── Application.java            # 이 프로젝트의 실행 진입점 main함수를 보유한 클래스
    │   └── Message.java                # 예외발생시 반환용 응답값 및 응답코드를 보유한 열거형 클래스
    ├── src/main/resources
    │   └── application.properties      # 리슨포트, DB접속정보 등을 저장하고 있는 설정파일
    ├── src/main/test
    │   └── com.kakaopay.project
    │       └── ApplicationTests.java   # JUnit을 이용한 Testcase를 실행할 수 있는 함수포함 클래스
    ├── mvnw.cmd                        # 윈도우 커맨드 환경에서 메이븐을 실행할 수 있는 파일
    ├── mysql_ddl.sql                   # DB 및 테이블 생성 스키마 DDL을 포함하는 파일
    └── pom.xml                         # 메이븐이 프로젝트를 빌드, 실행하기 위한 설정파일
```

<h2 id="how-to-install">    3. 설치 및 실행 방법</h2>

### 서버구동
~~~javascript
// 1) 원하는 폴더에 해당 프로젝트 파일을 다운 받아 압축풀기 또는 git clone를 이용하여 프로젝트 폴더를 가져오기
// 2) 윈도우키 + R, cmd + 엔터로 명령창 실행
// 3) mysqld.exe --console로 DB 서비스(데몬)를 구동 (mysql 설치는 되어 있다고 가정)

// 디렉토리 이동(workDir은 사용자에 따라 다를 수 있음)
[PROMPT] cd workDir/kakaopay-backend-server-project

// 메이븐을 이용 스프링부트기반으로 실행
[PROMPT] mvnw.cmd spring-boot:run
~~~

### 클라이어트 REST API 실행
~~~javascript
// 1) 윈도우키 + R, cmd + 엔터로 별도의 명령창 실행
// 2) chcp 65001로 charset은 UTF-8로 변경
// 3) curl은 설치되어 있다고 가정

// 10000원짜리 5명이 가져갈 수 있는 _뿌리기_ 시도
curl --location --request POST "http://localhost:8080/token" --header "X-USER-ID:100" --header "X-ROOM-ID:ABC" --header "Content-Type:application/json" --data-raw "{\"amt\":10000,\"personNum\":5}"

// 위에서 뿌리기시도후 받은 응답token이 img라고 할 경우 _받기_시도 아래와 같이 차례대로 실행
// X-USER-ID가 뿌리기한 값인 100과 다르게, X-ROOM-ID는 ABC로 통일하였음 그렇기 않으면 Bad Request받음
curl --location --request PUT "http://localhost:8080/token/img" --header "X-USER-ID:101" --header "X-ROOM-ID:ABC"
curl --location --request PUT "http://localhost:8080/token/img" --header "X-USER-ID:102" --header "X-ROOM-ID:ABC"
curl --location --request PUT "http://localhost:8080/token/img" --header "X-USER-ID:103" --header "X-ROOM-ID:ABC"
curl --location --request PUT "http://localhost:8080/token/img" --header "X-USER-ID:104" --header "X-ROOM-ID:ABC"
curl --location --request PUT "http://localhost:8080/token/img" --header "X-USER-ID:105" --header "X-ROOM-ID:ABC"

// 뿌리기 했을때의 X-USER-ID와 X-ROOM-ID를 동일하게 세팅후 _조회_ 시도
curl --location --request GET "http://localhost:8080/token/img" --header "X-USER-ID:100" --header "X-ROOM-ID:ABC"
~~~


<h2 id="api-spec">    4. API 명세</h2>

### 요청
|  항목|   뿌리기   |   받기   |   조회   |
|------|------------|---------|----------|
|medhod|   POST     |   PUT   |   GET    |
| path |   /token   |/token/{토큰값}|/token/{토큰값}|
|Header.X-USER-ID|유저아이디|유저아이디|유저아이디|
|Header.X-ROOM-ID|방아이디|방아이디|방아이디|
|Header.Content-Type|application/json|없음|없음|
|body|{<br>"amt":뿌릴금액,<br>"personNum":뿌릴인원<br>}|없음|없음|

### 응답
|코드   |   뿌리기   |   받기   |   조회   |
|------|------------|---------|----------|
|200|{<br>"resCd":"00",<br>"resMsg":"정상완료",<br>"token":토큰값<br>}|{<br>"resCd":"00",<br>"resMsg":"정상완료",<br>"recvAmt":받은금액<br>}|{<br>"resCd":"00",<br>"resMsg":"정상완료",<br>"recvCmptAmt":받기완료금액,<br>"sprayAmt":뿌린금액,<br>"cmptInfo":\[{"partAmt":받은금액,"rcvId":받은유저아이디},···]}<br>}|
|400|{<br>"resCd":예외코드,<br>"resMsg":예외메시지<br>}|{<br>"resCd":예외코드,<br>"resMsg":예외메시지<br>}|{<br>"resCd":예외코드,<br>"resMsg":예외메시지<br>}|
|404|{<br>"timestamp":발생시간,<br>"status":404,<br>"error":"Not Found",<br>"message":"",<br>"path":입력경로값<br>}|좌동|좌동|
|500|{<br>"timestamp":발생시간,<br>"status":500,<br>"error":"Internal Server Error",<br>"message":"",<br>"path":입력경로값<br>}|좌동|좌동|


<h2 id="testcase">    5. 테스트케이스 명세</h2>

테스트코드는 resCd(응답코드)값과 일치하는 값이며, JUnit테스트시 함수명은 TC+응답코드 형태로 정하였음
|코드   |   메시지   |   클래스명   |
|------|------------|---------|
|00|정상완료|-|
|01|X-USER-ID가 숫자형태 아님|NotNumericUserIdException|
|02|X-ROOM-ID가 문자형태 아님|NotAlphabeticalRoomIdException|
|03|X-USER-ID값이 없음|NotExistUserIdException|
|04|X-ROOM-ID값이 없음|NotExistRoomIdException|
|10|요청값 존재하지 않음|NotExistReqValException|
|20|뿌리기 당한 사용자는 한번만 받을 수 있음|ReceiveMoneyOverTwoTimesException|
|21|자신이 뿌리기한 건은 자신이 받을 수 없음|ReceiveMoneyByOwnerException|
|22|뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만 받을 수 있음|ReceiveMoneyByDifferentRoomUserException|
|23|뿌린 건은 10분간만 유효함|ReceiveMoneyAfterTenMinuesException|
|30|다른 사람의 뿌리기 건임|OtherTokenException|
|31|유효하지 않은 토큰임|NotValidTokenException|
|32|조회가능한 일수인 7일이 경과하였음|CheckMoneyAfterSevenDaysException|

<h2 id="db-schema">    6. DB스키마</h2>
    
