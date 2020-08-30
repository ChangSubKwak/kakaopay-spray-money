# kakaopay-spray-money

카카오페이 뿌리기 기능 구현하기

## 차례
1. [핵심 문제해결 전략](#how-to-solve)
1. [개발 환경](#dev-env)
2. [설치 및 실행 방법](#how-to-install)
3. [Dependencies](#dependencies)
4. [API 명세](#api-spec)
5. [과제 요구사항](#requirement)


<h2 id="how-to-solve">
    1. 핵심 문제해결 전략
</h2>

* ㆍ모든(뿌리기/받기/조회) REST API전문 포맷은 JSON으로 하였음  
- 특별한 이유가 있다기 보다는 일반적으로 전문 통신을 위해 개발자들이 JSON을 많이 사용<br>
  * - 데이터 처리시 관련 라이브러리 및 트러블슈팅 정보를 쉽게 구글링할 수 있겠다는 생각으로 선택하였음<br>
* 뿌리기API기능 구현 관련 설명

