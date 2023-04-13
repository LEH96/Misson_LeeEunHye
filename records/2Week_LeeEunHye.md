### 필수미션 - 호감표시 할 때 예외처리 케이스 3가지 처리

- [x] 예외 처리 케이스 4 : 중복으로 호감표시 하려는 경우
  - 중복 체크 해야할 것들
  - [x] from insta id가 중복
  - [x] to insta id가 중복
  - [x] attractive_type_code가 중복 -> 다르면 6번 케이스로 넘어감
  - [x] select문 확인 <br>
    >SELECT * <br>
    FROM likeable_person <br>
    WHERE from_insta_member_id = 1 <br>
    AND to_insta_member_id = 2;

- [x] 예외 처리 케이스 5 : 호감 상대를 11명 이상 등록하려는 경우

- [x] 예외 처리 케이스 6 : 다른 호감 표시로 같은 상대를 등록하려는 경우 
    - [x] resultCode = S-2, 새로 등록하지 않고 사유만 수정
    - [x] 바뀐 내용도 보여주기
    - [x] update문 확인 - 수정일과 호감코드만 바뀌게 수정
      >UPDATE likeable_person <br>
      SET modify_date = NOW(), <br>
      attractive_type_code = 2 <br>
      WHERE id = 5;

- [x] 코드 리팩토링
- [x] 테스트케이스 추가
  - [x] 서비스 테스트 추가
  - [x] 컨트롤러 테스트 추가

- 참고문헌
- https://velog.io/@roro/JPA-JPQL-update-쿼리벌크와-영속성-컨텍스트
- https://wakestand.tistory.com/946

### 선택미션 - 네이버 로그인
- [x] 선택미션 (네이버 로그인으로 가입한 회원의 providerTypeCode : NAVER)
- [x] insert문 확인
>INSERT INTO member <br>
SET create_date = NOW(), <br>
modify_date = NOW(), <br>
provider_type_code = 'NAVER', <br>
username = 'NAVER__2731659195', <br>
password = '', <br>
insta_member_id = NULL;

- 참고문헌
https://velog.io/@mardi2020/Spring-Boot-Spring-Security-OAuth2-네이버-로그인-해보기

### 개인적으로 진행
- [x] UI 추가하기
  - [x] 메인페이지
  - [x] 로그인 페이지
  - [x] 회원가입 페이지
  - [x] 인스타 아이디 인증 페이지
  - [x] 호감 표시 페이지
- [ ] 갑자기 구글 로그인 안됨?

- [ ] 인스타 아이디 새로 등록 시 이전 아이디 다시 등록 못함 + 호감 표시 데이터 안보이게 됨
- [ ] 인스타 아이디에 한글 넣었을 때 에러 페이지 나옴 : JDBC exception executing
- [ ] 수정된 데이터를 목록에서 보여줄때 다르게 보여줄 방법
