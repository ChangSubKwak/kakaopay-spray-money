# kakaopay-spray-money

카카오페이 뿌리기 기능 구현하기

## 차례
1. [핵심 문제해결 전략](#how-to-solve)
2. [개발 환경](#dev-env)
3. [설치 및 실행 방법](#how-to-install)
4. [API 명세](#api-spec)
5. [테스트케이스 명세](#testcase)
6. [DB구성](#db-schema)

<br><br>
<h2 id="how-to-solve">
    1. 핵심 문제해결 전략
</h2>

<h3>ㆍ모든(뿌리기/받기/조회) REST API전문 포맷은 JSON으로 하였음</h3>
<h4>
<pre>
  - 전문 통신으로 개발자들이 JSON을 많이 사용
  - 관련 라이브러리 구하기 쉽게 구할 수 있음(https://mvnrepository.com)
  - 트러블슈팅 정보를 쉽게 찾을 수 있을 것 같아 선택하였음(http://json.com)
</pre>
<br>
</h4>
<h3>ㆍ네이밍 룰</h3>
<h4>
<pre>
  - Java소스코드 camel표기법을 따름
  - JSON의 키이름 camel표기법을 따름
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
    ex) TC00 (정상완료), TC01(X-USER-ID가 숫자형태 아님) ···
  - 테스트케이스 함수명은 해당코드에 대한 예외를 발생시키는 것임
  - 테스트케이스별 명세는 아래 5번째 항목에 기록하였음
</pre>
</h4>
  
  
<h3>ㆍ뿌리기API기능 구현 관련</h3>
<h4>
<pre>
  - 뿌릴 금액, 뿌릴 인원에 다음과 같이 메타데이터 결정
    cf) amt(금액), personNum(인원수)
  - 고유 token 생성은 [a-zA-Z]에 대하여 3자리의 무작위 문자열 생성
  - 문자열 생성은 Fisher-Yates Shuffle(https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle) 이용
  - 랜덤값 생성은 예측이 어렵도록 SecureRandom객체 생성하였음
  - 금액을 분배하는 로직은 다음과 같음
    1) (받을금액) = ( (총금액) / (인원수) ) * (0.5 ~ 1.5)
    2) (총금액) = (총금액) - (받을금액)
    3) 1) ~ 2) 과정을 ((인원수) - 1) 만큼 반복
    4) 마지막 (총금액)은 그냥 사용
  - DB에 데이터 삽입 내용중 등록시간을 저장해야 받기/조회시의 시간과 비교하여 예외처리 가능
</pre>
</h4>

    
* ㆍ뿌리기API기능 구현 관련 설명  
* ㆍ뿌리기API기능 구현 관련 설명  

