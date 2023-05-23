## 이번주 수행 사항

### 필수미션

- [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 성별 필터링기능 구현

- [x] 네이버클라우드플랫폼을 통한 배포, 도메인, HTTPS 까지 적용

### 추가미션

- [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 호감사유 필터링기능 구현

- [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 정렬기능

- [ ] 젠킨스를 통해서 리포지터리의 main 브랜치에 커밋 이벤트가 발생하면 자동으로 배포가 진행되도록

---

### 1. 네이버클라우드플랫폼을 통한 배포, 도메인, HTTPS 까지 적용
**배경** <br>
네이버클라우드플랫폼을 이용합니다. <br>
강사의 가이드영상대로 진행하시면 됩니다. <br>
나머지 미션을 수행하신 후 마지막에 진행하시면 됩니다.

**목표** <br>
`https://도메인/` 형태로 접속이 가능 <br>
운영서버에서 각종 소셜로그인, 인스타 아이디 연결이 잘 되어야 합니다.

**구현** :
`https://www.leh.works`


### 2. 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 성별 필터링기능 구현
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
                        .reversed()
                    );
                    break;
                case 4: //인기가 적은 사람 순
                    likeablePeopleStream = likeablePeopleStream.sorted(
                        Comparator.comparingLong((LikeablePerson lp) -> lp.getFromInstaMember().getLikes())
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

2. 추가작업: QueryDSL 이용

- LikeablePersonController
```java
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/toList")
    public String showToList(Model model, String gender, String attractiveTypeCode, @RequestParam(defaultValue = "1") int sortCode) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            // 해당 인스타회원이 좋아하는 사람들 목록
            List<LikeablePerson> likeablePeople = instaMember.getToLikeablePeople();

            if(gender != null && !Objects.equals(gender,"")) {
                likeablePeople =
                    likeablePersonService.findQslByToInstaMemberAndGender(likeablePeople, gender);
            }
    
            if(attractiveTypeCode != null && !attractiveTypeCode.isEmpty()){
                likeablePeople =
                    likeablePersonService.findQslByToInstaMemberAndAttractiveTypeCode(likeablePeople, Integer.parseInt(attractiveTypeCode));
            }
    
            switch (sortCode) {
                case 1: //최신순(기본)
                    break;
                case 2: //날짜가 오래된 순
                    likeablePeople = likeablePersonService.sortQslByOldCreateDate(likeablePeople);
                    break;
                case 3: //인기가 많은 사람 순
                    likeablePeople = likeablePersonService.sortQslByMorePopularFromInstaMember(likeablePeople);
                    break;
                case 4: //인기가 적은 사람 순
                    likeablePeople = likeablePersonService.sortQslByLessPopularFromInstaMember(likeablePeople);
                    break;
                case 5: //1. 성별 순(여성 -> 남성 순) 2. 최신순
                    likeablePeople = likeablePersonService.sortQslByGender(likeablePeople);
                    break;
                case 6: //1. 호감사유순(외모 -> 성격 순) 2. 최신순
                    likeablePeople = likeablePersonService.sortQslByAttractiveType(likeablePeople);
                    break;
            }
            
            model.addAttribute("likeablePeople", likeablePeople);
            }
    
            return "usr/likeablePerson/toList";
        }
```

- LikeablePersonRepositoryImpl
```java
@RequiredArgsConstructor
public class LikeablePersonRepositoryImpl implements LikeablePersonRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    ...

    public List<LikeablePerson> findQslByToInstaMemberAndGender(List<LikeablePerson> likeablePeople, String gender){
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(
                        likeablePerson.in(likeablePeople)
                                .and(likeablePerson.fromInstaMember.gender.eq(gender))
                )
                .fetch();
    }

    public List<LikeablePerson> findQslByToInstaMemberAndAttractiveTypeCode(List<LikeablePerson> likeablePeople, int attractiveTypeCode) {
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(
                        likeablePerson.in(likeablePeople)
                                .and(likeablePerson.attractiveTypeCode.eq(attractiveTypeCode))
                )
                .fetch();
    }

    public List<LikeablePerson> sortQslByOldCreateDate(List<LikeablePerson> likeablePeople) {
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.in(likeablePeople))
                .orderBy(likeablePerson.createDate.asc())
                .fetch();
    }

    public List<LikeablePerson> sortQslByMorePopularFromInstaMember(List<LikeablePerson> likeablePeople) {
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.in(likeablePeople))
                .orderBy(likeablePerson.fromInstaMember.likes.desc())
                .fetch();
    }

    public List<LikeablePerson> sortQslByLessPopularFromInstaMember(List<LikeablePerson> likeablePeople) {
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.in(likeablePeople))
                .orderBy(likeablePerson.fromInstaMember.likes.asc())
                .fetch();
    }

    public List<LikeablePerson> sortQslByGender(List<LikeablePerson> likeablePeople) {
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.in(likeablePeople))
                .orderBy(likeablePerson.fromInstaMember.gender.desc())
                .fetch();
    }

    public List<LikeablePerson> sortQslByAttractiveType(List<LikeablePerson> likeablePeople) {
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.in(likeablePeople))
                .orderBy(likeablePerson.attractiveTypeCode.asc())
                .fetch();
    }
}
```
### 5. 젠킨스를 통해서 리포지터리의 main 브랜치에 커밋 이벤트가 발생하면 자동으로 배포가 진행되도록
**배경** <br>
네이버클라우드플랫폼을 이용합니다. <br>
젠킨스를 이용합니다. <br>
강사의 가이드영상대로 진행하시면 됩니다. <br>
나머지 미션을 수행하신 후 마지막에 진행하시면 됩니다.

**목표** <br>
리포지터리의 main 브랜치에 커밋 이벤트가 발생하면 자동으로 배포가 진행

**구현** <br>


---
### 1차 리팩토링

1. 필터링과 정렬 기능을 컨트롤러 -> 서비스로 이동

- LikeablePersonController
```java
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/toList")
    public String showToList(Model model,@RequestParam(defaultValue = "") String gender,@RequestParam(defaultValue = "0") int attractiveTypeCode, @RequestParam(defaultValue = "1") int sortCode) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            // 해당 인스타회원이 좋아하는 사람들 목록
            List<LikeablePerson> likeablePeople = instaMember.getToLikeablePeople();

            if(!gender.isEmpty() || attractiveTypeCode != 0)
                likeablePeople = likeablePersonService.filterByGenderAndAttractiveTypeCode(likeablePeople, gender, attractiveTypeCode);

            if(sortCode != 1)
                likeablePeople = likeablePersonService.sort(likeablePeople, sortCode);

            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/toList";
    }
```

- LikeablePersonService
```java
    public List<LikeablePerson> filterByGenderAndAttractiveTypeCode(List<LikeablePerson> likeablePeople, String gender, int attractiveTypeCode) {
        return likeablePersonRepository.findQslByGenderAndAttractiveTypeCode(likeablePeople, gender, attractiveTypeCode);
    }

    public List<LikeablePerson> sort(List<LikeablePerson> likeablePeople, int sortCode) {
        return likeablePersonRepository.sortQslBySortCode(likeablePeople, sortCode);
    }
```

- LikeablePersonRepositoryImpl
```java
@RequiredArgsConstructor
public class LikeablePersonRepositoryImpl implements LikeablePersonRepositoryCustom {
    ...

    public List<LikeablePerson> findQslByGenderAndAttractiveTypeCode(List<LikeablePerson> likeablePeople, String gender, int attractiveTypeCode){
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(
                        likeablePerson.in(likeablePeople)
                                .and(gender.isEmpty() ? null : likeablePerson.fromInstaMember.gender.eq(gender))
                                .and(attractiveTypeCode == 0 ? null : likeablePerson.attractiveTypeCode.eq(attractiveTypeCode))
                )
                .orderBy(likeablePerson.id.desc())
                .fetch();
    }

    public List<LikeablePerson> sortQslBySortCode(List<LikeablePerson> likeablePeople, int sortCode){
        JPAQuery<LikeablePerson> query = jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.in(likeablePeople));

        query = switch(sortCode) {
                    case 2 //날짜순
                            -> query.orderBy(likeablePerson.id.asc());
                    case 3 //인기가 많은 사람 순
                            -> query.orderBy(likeablePerson.fromInstaMember.likes.desc(), likeablePerson.id.desc());
                    case 4 //인기가 적은 사람 순
                            -> query.orderBy(likeablePerson.fromInstaMember.likes.asc(), likeablePerson.id.desc());
                    case 5 //1. 성별 순(여성 -> 남성 순) 2. 최신순
                            -> query.orderBy(likeablePerson.fromInstaMember.gender.desc(), likeablePerson.id.desc());
                    case 6 //1. 호감사유순(외모 -> 성격 순) 2. 최신순
                            -> query.orderBy(likeablePerson.attractiveTypeCode.asc(), likeablePerson.id.desc());
                    default //최신순
                            -> query.orderBy(likeablePerson.id.desc());
        };

        return query.fetch();
    }
}
```

- likeablePerson.fromInstaMember.likes를 불러오는 과정에서 예외 발생
```
org.hibernate.query.SemanticException: Could not resolve attribute 'likes' of 'com.ll.gramgram.boundedContext.instaMember.entity.InstaMember'
```

- 해결

InstaMemberBase likes 변수 추가
InstaMember에 increaseLikesCount와 decreaseLikesCount에도 추가해줌

2. where절 수정
```java
    public List<LikeablePerson> findQslByGenderAndAttractiveTypeCode(InstaMember toInstaMember, String gender, int attractiveTypeCode){
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(
                        likeablePerson.toInstaMember.eq(toInstaMember)
                                .and(gender.isEmpty() ? null : likeablePerson.fromInstaMember.gender.eq(gender))
                                .and(attractiveTypeCode == 0 ? null : likeablePerson.attractiveTypeCode.eq(attractiveTypeCode))
                )
                .orderBy(likeablePerson.id.desc())
                .fetch();
    }

    public List<LikeablePerson> sortQslBySortCode(InstaMember toInstaMember, int sortCode){
        JPAQuery<LikeablePerson> query = jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.toInstaMember.eq(toInstaMember));

        query = switch(sortCode) {
                    case 2 //날짜순
                            -> query.orderBy(likeablePerson.id.asc());
                    case 3 //인기가 많은 사람 순
                            -> query.orderBy(likeablePerson.fromInstaMember.likes.desc(), likeablePerson.id.desc());
                    case 4 //인기가 적은 사람 순
                            -> query.orderBy(likeablePerson.fromInstaMember.likes.asc(), likeablePerson.id.desc());
                    case 5 //1. 성별 순(여성 -> 남성 순) 2. 최신순
                            -> query.orderBy(likeablePerson.fromInstaMember.gender.desc(), likeablePerson.id.desc());
                    case 6 //1. 호감사유순(외모 -> 성격 순) 2. 최신순
                            -> query.orderBy(likeablePerson.attractiveTypeCode.asc(), likeablePerson.id.desc());
                    default //최신순
                            -> query.orderBy(likeablePerson.id.desc());
        };

        return query.fetch();
    }
```
likeablePeople 리스트를 이용하는 경우, 리스트의 크기에 따라 쿼리가 여러 번 실행될 수 있다
영속성 컨텍스트에서 가져오기 위해 toInstaMember를 사용

3. 테스트 케이스 추가

- 컨트롤러 테스트 케이스 추가
```java
    @Test
    @DisplayName("성별 필터링 - 남성")
    @WithUserDetails("user4")
    void t018() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/likeablePerson//toList")
                        .with(csrf()) // CSRF 키 생성
                        .param("gender", "M")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showToList"))
                .andExpect(model().attribute("likeablePeople", hasSize(2)));
    }

    @Test
    @DisplayName("호감사유 필터링 - 성격")
    @WithUserDetails("user4")
    void t019() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/usr/likeablePerson//toList")
                        .with(csrf()) // CSRF 키 생성
                        .param("attractiveTypeCode", "2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showToList"))
                .andExpect(model().attribute("likeablePeople", hasSize(1)));
    }
```

- 서비스 테스트 케이스 추가
```java
    @Test
    @DisplayName("정렬 - 날짜 순")
    void t009() throws Exception {
            // Given
            List<LikeablePerson> likeablePeople = likeablePersonService.sort(memberService.findByUsername("user4").get().getInstaMember(), 2);

        assertThat(likeablePeople)
        .isSortedAccordingTo(
        Comparator.comparing(LikeablePerson::getId)
        );
        }

    @Test
    @DisplayName("정렬 - 인기 적은 순")
    void t010() throws Exception {
            // Given
            List<LikeablePerson> likeablePeople = likeablePersonService.sort(memberService.findByUsername("user4").get().getInstaMember(), 4);

        assertThat(likeablePeople)
        .isSortedAccordingTo(
        Comparator.comparing((LikeablePerson lp) -> lp.getFromInstaMember().getLikes())
        .thenComparing(Comparator.comparing(LikeablePerson::getId).reversed())
        );
        }

    @Test
    @DisplayName("정렬 - 호감사유순")
    void t011() throws Exception {
            // Given
            List<LikeablePerson> likeablePeople = likeablePersonService.sort(memberService.findByUsername("user4").get().getInstaMember(), 6);

        assertThat(likeablePeople)
        .isSortedAccordingTo(
        Comparator.comparing(LikeablePerson::getAttractiveTypeCode)
        .thenComparing(Comparator.comparing(LikeablePerson::getId).reversed())
        );
        }
```