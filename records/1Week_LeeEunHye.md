### 1. 필수 미션 : 호감상대 삭제

- [x] 로그인한 사용자에게 삭제 권한이 있는지 확인 (likeablePerson Id 기반)
- [x] 삭제 권한이 없는 사용자가 삭제시 권한이 없다는 메시지 출력
    - [x] 삭제 메시지를 경고창(historyBack)으로 출력 : 강사님 도움으로 해결
    - 삭제 확인 메세지(confirm)는 toastr 사용 안됨
- [x] 호감데이터 삭제
- [x] 삭제 후 목록페이지로 redirect
- [x] 삭제 메시지 출력

### 2. 선택 미션 : 구글 로그인

- [x] 구글 OAuth 클라이언트 id 생성
- [x] 구글 로그인 작동(로그인 완료후 메인페이지로 돌아오기)
- [x] 로그인 후 닉네임 확인
- [ ] API 로그인을 하고 다시 접속하면 오류
    - : 세션 삭제 후 접속 시 오류 안나긴함

### 참고 문서

- 삭제 기능 : 이전에 만들었던 점프투스프링부트 프로젝트 참고
- 구글 로그인 기능
    - 클라이언트 아이디 생성 : https://yeonyeon.tistory.com/34
    - 코드에 로그인 연동: https://yeonyeon.tistory.com/34

### 1차 리팩토링

- 호감 목록

1. 내가 받은 호감 목록 표시, 받은 호감 데이터는 삭제 불가
2. 호감 표시한 상대방의 인스타 정보 가져오기

- 로그인

1. 연동된 API 로그인 : 텍스트를 이미지 버튼으로
2. 로그인된 플랫폼에서 개인정보 가져오기

### 2차 리팩토링

- 피어리뷰 피드백 : likeable 객체 만들어놓고 id로 삭제한거 객체로 삭제하도록 수정


- history.back()을 url로 접근시 이전 페이지로 돌아갈 수 없다.
    - 직접 url을 입력해 페이지를 이동한 경우 이전 페이지를 보안상 저장하지 않음
    - 강사님 도움으로 해결


- 구글 로그인 후 다시 접속하려고 했을 때 오류
    - NotProd에 whenSocialLogin 추가 / ddl-auto : update로 수정