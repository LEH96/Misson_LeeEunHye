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

### 2. 네이버클라우드플랫폼을 통한 배포, 도메인, HTTPS 까지 적용
**배경** <br>
네이버클라우드플랫폼을 이용합니다. <br>
강사의 가이드영상대로 진행하시면 됩니다. <br>
나머지 미션을 수행하신 후 마지막에 진행하시면 됩니다.

**목표** <br>
`https://도메인/` 형태로 접속이 가능 <br>
운영서버에서 각종 소셜로그인, 인스타 아이디 연결이 잘 되어야 합니다.

**구현** :
`https://www.leh.works`

---

### 3. 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 호감사유 필터링 기능 구현
**배경** <br>
현재 UI에서는 이 요구사항에 대한 작업이 완료되었습니다. <br>
백엔드 쪽에서 필터링 로직만 구현하시면 됩니다.

**목표** <br>
내가 받은 호감리스트에서 특정 호감사유의 호감만 필터링해서 볼 수 있다.

**구현** <br>
1번 필수미션에서 같이 구현함.

### 4. 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 정렬 기능
**배경** <br>
현재 UI에서는 이 요구사항에 대한 작업이 완료되었습니다. <br>
백엔드 쪽에서 필터링 로직만 구현하시면 됩니다.

**목표** <br>
아래 케이스 대로 작동해야 한다.

**최신순(기본)** <br>
가장 최근에 받은 호감표시를 우선적으로 표시

**날짜순** <br>
가장 오래전에 받은 호감표시를 우선적으로 표시

**인기 많은 순** <br>
가장 인기가 많은 사람들의 호감표시를 우선적으로 표시

**인기 적은 순** <br>
가장 인기가 적은 사람들의 호감표시를 우선적으로 표시

**성별순** <br>
여성에게 받은 호감표시를 먼저 표시하고, 그 다음 남자에게 받은 호감표시를 후에 표시 <br>
**2순위** 정렬조건으로는 최신순

**호감사유순** <br>
외모 때문에 받은 호감표시를 먼저 표시하고, 그 다음 성격 때문에 받은 호감표시를 후에 표시 <br>
마지막으로 능력 때문에 받은 호감표시를 후에 표시 <br>
**2순위** 정렬조건으로는 최신순

**구현** <br>
1. 정렬 조건을 switch문으로 구분후 스트림을 이용하여 정렬
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

            if(gender != null && !Objects.equals(gender,"")) {
                likeablePeopleStream = likeablePeopleStream.filter(
                                            lp -> lp.getFromInstaMember().getGender().equals(gender)
                                       );
            }

            if(attractiveTypeCode != null && !attractiveTypeCode.isEmpty()){
                    likeablePeopleStream = likeablePeopleStream.filter(
                                                lp -> lp.getAttractiveTypeCode() == Integer.parseInt(attractiveTypeCode)
                                            );
            }

            switch (sortCode) {
                case 1: //최신순(기본)
                    break;
                case 2: //날짜가 오래된 순
                    likeablePeopleStream = likeablePeopleStream.sorted(
                        Comparator.comparing(LikeablePerson::getCreateDate)
                    );
                    break;
                case 3: //인기가 많은 사람 순
                    likeablePeopleStream = likeablePeopleStream.sorted(
                        Comparator.comparingLong((LikeablePerson lp) -> lp.getFromInstaMember().getLikes())
                    );
                    break;
                case 4: //인기가 적은 사람 순
                    likeablePeopleStream = likeablePeopleStream.sorted(
                        Comparator.comparingLong((LikeablePerson lp) -> lp.getFromInstaMember().getLikes())
                        .reversed()
                    );
                    break;
                case 5: //1. 성별 순(여성 -> 남성 순) 2. 최신순
                    likeablePeopleStream = likeablePeopleStream.sorted(
                        Comparator.comparing((LikeablePerson lp) -> lp.getFromInstaMember().getGender())
                        .reversed()
                    );
                    break;
                case 6: //1. 호감사유순(외모 -> 성격 순) 2. 최신순
                    likeablePeopleStream = likeablePeopleStream.sorted(
                        Comparator.comparingInt(LikeablePerson::getAttractiveTypeCode)
                    );
                    break;
            }

            likeablePeople = likeablePeopleStream.collect(Collectors.toList());
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/toList";
    }
```