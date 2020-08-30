# kakaopay-spray-money

카카오페이 뿌리기 기능 구현하기

## 차례
1. [핵심 문제해결 전략](#how-to-solve)
2. [개발 환경](#dev-env)
3. [설치 및 실행 방법](#how-to-install)
4. [API 명세](#api-spec)
5. [테스트케이스 명세](#testcase)
6. [DB구성](#db-schema)



<h2 id="how-to-solve">
    1. 핵심 문제해결 전략
</h2>

* ㆍ모든(뿌리기/받기/조회) REST API전문 포맷은 JSON으로 하였음 
    * \- 전문 통신으로 개발자들이 JSON을 많이 사용  
    * \- 관련 라이브러리 구하기 쉽게 구할 수 있음(https://mvnrepository.com)
    * \- 트러블슈팅 정보를 쉽게 찾을 수 있을 것 같아 선택하였음(http://json.com)  
  
* ㆍ네이밍 룰  
    * \- Java소스코드 camel표기법을 따름
    * \- JSON의 키이름 camel표기법을 따름  
    * \- DB의 테이블명은 앞에 't'를 붙였음
    * \- DB의 컬럼명은 의미가 구분되는(camel표기의 대문자지점)곳은 '\_'로 연결후 소문자로 시작  
        * ex) regDt(camel) --> reg_dt  
  
* ㆍ단위 테스트
    * \- JUnit기반 MockMvc를 주로 사용한 테스트케이스 작성
    * \- 테스트케이스 함수명은 TC(TestCase)를 접두어로 응답코드(두자리)를 접미어로 붙여 사용
    * \- 테스트케이스 함수명은 해당코드에 대한 예외를 발생시키는 것임
    * \- 테스트케이스별 명세는 아래 5번째 항목에 기록하였음

* ㆍ뿌리기API기능 구현 관련 설명  
* ㆍ뿌리기API기능 구현 관련 설명  
* ㆍ뿌리기API기능 구현 관련 설명  

