- [ ] 필수 미션 : 호감상대 삭제

- 목표 :
- 호감목록 페이지에서 특정 항목에서 삭제버튼을 누르면, 해당 항목은 삭제되어야 한다. 
- 
- 삭제를 처리하기 전에 해당 항목에 대한 소유권이 본인(로그인한 사람)에게 있는지 체크해야 한다.
삭제 후 다시 호감목록 페이지로 돌아와야 한다. 
- 
- rq.redirectWithMsg 함수 사용 
- SQL
#### 5번 항목의 삭제버튼을 눌렀을 경우에 실행되어야 하는 SQL
DELETE
FROM likeable_person
WHERE id = 5;

- [ ] 선택미션: 구글 로그인
- 배경
  현재 일반 로그인과 카카오 로그인까지 구현되어 있다.

일반 회원의 providerTypeCode : GRAMGRAM

카카오 로그인으로 가입한 회원의 providerTypeCode : KAKAO

스프링 OAuth2 클라이언트로 구현되어 있다.

카카오 개발자 도구에서 앱 등록, 앱으로 부터 앱키(REST API)를 받아서 프로젝트에 삽입하는 과정이 선행되었음

구글 로그인도 카카오 로그인 구현과정을 그대로 따라하면 된다.

목표
카카오 로그인이 가능한것 처럼, 구글 로그인으로도 가입 및 로그인 처리가 되도록 해주세요.

스프링 OAuth2 클라이언트로 구현해주세요.

구글 로그인으로 가입한 회원의 providerTypeCode : GOOGLE

SQL
# 최초로 구글 로그인을 통해서 가입이 될 때 실행되어야 할 SQL
# 구글 앱에서의 해당 회원번호를 2731659195 라고 가정
INSERT INTO `member`
SET create_date = NOW(),
modify_date = NOW(),
provider_type_code = 'GOOGLE',
username = 'GOOGLE__2731659195',
password = '',
insta_member_id = NULL;