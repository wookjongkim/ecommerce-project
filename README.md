# 🛍️ 이커머스 프로젝트

## 📌 프로젝트 소개
이커머스 플랫폼들의 주요 기능 및 정책들을 이해하고 구현해 보았습니다.

또한 동시에 여러 사용자가 상호작용하는 환경에서 발생할 수 있는 동시성 문제를 제어해 보았고, 각 기능에 대한 테스트 코드를 작성해 보았습니다.


## 🛠 기술 스택
- `Java`(version 11)
- `Spring Boot`(version 2.7.13)
- `Spring Data JPA` & `Hibernate`
- `Spring Security`
- `Spring Scheduling`
- `MySQL`(version 8.0.33)
- `Swagger`(version 3.0.0)
- `GitHub` & `SourceTree`

## 🧰 RESTful API 설계
| URL                                         | HTTP 메소드 | 설명          |
|---------------------------------------------|-------------|-------------|
| /members                                    | POST        | 회원 가입       |
| /login                                      | POST        | 로그인         |
| /buyers/items                               | GET         | 판매 상품 조회    |
| /buyers/{buyerId}/balance                   | POST        | 잔액 충전       |
| /buyers/{buyerId}/orders                    | POST        | 상품 주문       |
| /buyers/{buyerId}/orders/{orderId}          | GET         | 주문 내역 조회    |
| /buyers/{buyerId}/orders/{orderId}/cancel   | PATCH       | 주문 취소       |
| /sellers/{sellerId}/item                    | POST        | 상품 등록       |
| /sellers/{sellerId}/items                   | GET         | 판매자의 상품 조회  |
| /sellers/{sellerId}/items/{itemId}          | GET         | 상품 상세 정보 조회 |
| /sellers/{sellerId}/items/{itemId}          | PATCH       | 상품 수정       |
| /sellers/{sellerId}/items/{itemId}/stock    | POST        | 재고 추가       |
| /sellers/{sellerId}/balance/withdraw        | POST        | 수익 인출       |

## 🚀 주요 기능
### 회원 가입 및 로그인 
사용자는 이름, 이메일, 비밀번호, 주소, 전화번호 그리고 역할(판매자 혹은 구매자)정보를 통해 회원가입을 진행할 수 있습니다.

회원 가입 과정에서 구매자로 가입한 경우엔 잔액 정보를 생성하고, 판매자로 가입한 경우 수익 정보를 생성합니다.

회원의 비밀번호는 BCryptPasswordEncoder를 통해 암호화 후 저장하고, 로그인시 matches(복호화 메서드)를 통해 입력 암호와 DB에 저장된 암호를 비교합니다.

### 상품 검색 
이 프로젝트의 상품 검색 기능은 JPA Specification을 통해 사용자의 다양한 입력 조건을 반영한 동적 쿼리 생성을 지원합니다. 
이를 통해 사용자가 설정한 조건에 딱 맞는 상품들만을 효과적으로 필터링하여 출력할 수 있습니다. 

추가로, 데이터의 양이 방대해질 것을 고려하여, 사용자에게 보다 효율적인 정보 전달을 위해 페이징 처리를 적용하였습니다.

판매자용 상품 검색 조건
- 등록 일자 : 특정 기간 동안 등록된 상품을 검색합니다.
- 가격 범위 : 설정한 가격 범위 내에서 상품을 검색합니다.
- 재고 정렬 : 재고량 기준 오름차순 또는 내림차순으로 상품을 정렬합니다.
- 판매 상태 : 판매 중(`SELL`), 품절(`SOLD_OUT`), 판매 중지(`SELL_STOPPED`) 등의 상태를 기준으로 상품을 필터링합니다

구매자용 상품 검색 조건
- 상품 이름: 원하는 상품의 이름을 키워드로 검색합니다.
- 가격 범위: 설정한 가격 범위 내의 상품을 검색합니다.
- 가격 정렬: 가격 기준 오름차순 또는 내림차순으로 상품을 정렬합니다.
- 판매 상태: 원하는 판매 상태를 기준으로 상품을 필터링합니다.
- 카테고리: 특정 카테고리의 상품만을 검색합니다.

### 판매자 용 API
판매자는 플랫폼 내에서 아래와 같은 활동을 수행할 수 있습니다.

- 상품 등록 : 판매자는 상품의 이름, 설명, 가격, 재고, 그리고 카테고리를 기입하여 새로운 상품을 등록할 수 있습니다.
- 상품 정보 수정 : 등록한 상품의 정보를 변경할 수 있습니다.
- 재고 관리 : 판매자가 필요에 따라 재고를 추가할 수 있습니다.
- 수익 관리 : 판매자는 상품 판매를 통해 얻은 수익을 출금할 수 있습니다.

특히 재고 및 수익과 같이 돈과 관련된 중요한 정보들은 동시성 이슈를 방지하기 위해 락을 사용하여 처리하였습니다.

### 구매자 용 API
구매자는 플랫폼 내에서 아래와 같은 활동을 할 수 있습니다.

- 잔고 관리: 구매를 원활히 진행하기 위해, 구매자는 개인 잔고를 충전할 수 있습니다. 이 잔고는 상품 구매 시 사용됩니다.

- 상품 주문: 구매자는 선택한 상품들을 잔고를 활용하여 주문할 수 있습니다. 이때, 사용자의 잔고는 차감되며, 판매자의 수익은 증가하고, 상품의 재고는 줄어듭니다.

- 주문 취소: 주문한 상품에 대한 결정을 변경하였을 경우, 구매자는 주문을 취소할 수 있습니다. 주문을 취소하면, 사용자의 잔고는 다시 증가하며, 판매자의 수익은 감소하고, 상품의 재고는 다시 늘어납니다.

데이터 변동 작업들 중, 특히 잔고, 판매 수익 및 재고와 같은 데이터는 동시성 이슈를 방지하기 위해 락을 사용하여 처리하였습니다.

### 주기적인 상품 정보 정리
판매자에게 상품 정보를 직접 삭제하는 기능을 제공하는 것은 여러 문제점을 야기할 수 있다고 생각하였습니다.
이런 문제점 들을 고려하여, 판매자에게는 상품의 상태를 SELL_STOPPED로 변경하는 기능만 제공하였습니다.

현재 플랫폼에서는 트래픽이 가장 적을것으로 예상되는 매일 새벽 3시마다, 3년 동안 상품 정보가 업데이트 되지 않으면서 SELL_STOPPED 또는 SOLD_OUT 상태의 상품을
향후 판매 재개 가능성이 없는 상품으로 판단하여 이를 삭제합니다.

## 📊 ERD
![erd](https://github.com/wookjongkim/ecommerce-project/assets/121083077/a3fd2d3b-fc7a-42e6-9d5a-a4ef8f3643bf)

## 📚 블로그 포스팅
제가 프로젝트를 진행하면서 생각 및 경험 해보았던 것들 중, 다른 분들과 공유하고 싶은 내용에 대해 별도의 포스팅을 작성해보았습니다.

- [트러블 슈팅 - save the transient instance before flushing](https://wookjongbackend.tistory.com/40)
- [판매자의 상품 검색 기능 - 동적 쿼리 생성을 위해 JPA Specification 적용](https://wookjongbackend.tistory.com/41)
- [재고 추가하기 - POST(멱등성 x), 비관적 락,낙관적 락](https://wookjongbackend.tistory.com/42)
- [상품의 수정 및 삭제에 대해 정책적으로 다루어보자!](https://wookjongbackend.tistory.com/43)
- [상품 주문 로직 구현해보기(with 동시성 제어 테스트 및 트러블 슈팅)](https://wookjongbackend.tistory.com/44)
