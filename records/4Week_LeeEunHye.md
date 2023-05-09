## 이번주 수행 사항

### 필수미션

- [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 성별 필터링기능 구현

- [x] 네이버클라우드플랫폼을 통한 배포, 도메인, HTTPS 까지 적용

### 추가미션

- [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 호감사유 필터링기능 구현

- [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 정렬기능

- [ ] 젠킨스를 통해서 리포지터리의 main 브랜치에 커밋 이벤트가 발생하면 자동으로 배포가 진행되도록

---

### 1. 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 성별 필터링기능 구현
**배경** <br>
현재 UI에서는 이 요구사항에 대한 작업이 완료되었습니다. <br>
백엔드 쪽에서 필터링 로직만 구현하시면 됩니다.

**목표** <br>
내가 받은 호감리스트에서 특정 성별을 가진 사람에게서 받은 호감만 필터링해서 볼 수 있다.

**구현** <br>
1. LikeablePersonController에 기능 추가
```java
@PreAuthorize("isAuthenticated()")
@GetMapping("/toList")
public String showToList(Model model, String gender, String attractiveTypeCode, @RequestParam(defaultValue = "1") int sortCode) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            // 해당 인스타회원이 좋아하는 사람들 목록
            List<LikeablePerson> likeablePeople = instaMember.getToLikeablePeople();
            Stream<LikeablePerson> likeablePeopleStream = likeablePeople.stream();
    
            if(gender != null) {
                likeablePeopleStream = likeablePeopleStream.filter(
                    lp -> lp.getFromInstaMember().getGender().equals(gender)
                );
            }
    
            if(attractiveTypeCode != null) {
                likeablePeopleStream = likeablePeopleStream.filter(
                    lp -> lp.getAttractiveTypeCode() == Integer.parseInt(attractiveTypeCode)
                );
            }
    
            likeablePeople = likeablePeopleStream.collect(Collectors.toList());
            model.addAttribute("likeablePeople", likeablePeople);
        }

            return "usr/likeablePerson/toList";
        }
```

2. 에러 발생
```
java.lang.NumberFormatException: For input string: ""
	at java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:67)
	at java.base/java.lang.Integer.parseInt(Integer.java:678)
	at java.base/java.lang.Integer.parseInt(Integer.java:786)
```
" "으로 들어온 문자열을 int형으로 바꾸려고 하니 발생하는 에러인 것 같다..

```java
            if(gender != null && !gender.isEmpty()) {
                likeablePeopleStream = likeablePeopleStream.filter(
                                            lp -> lp.getFromInstaMember().getGender().equals(gender)
                                       );
            }

            if(attractiveTypeCode != null && !attractiveTypeCode.isEmpty()){
                    likeablePeopleStream = likeablePeopleStream.filter(
                                                lp -> lp.getAttractiveTypeCode() == Integer.parseInt(attractiveTypeCode)
                                            );
            }
```
처음에는 Objects.equals(gender,"")로 수정했으나,
좀 더 가독성이 좋은 챗봇 답변으로 수정함