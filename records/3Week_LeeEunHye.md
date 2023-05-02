## 이번주 수행 사항

### 필수미션

- [ ] 네이버클라우드플랫폼을 통한 배포(도메인 없이, IP로 접속)

- [x] 호감표시/호감사유변경 후, 개별 호감표시건에 대해서 <br> 3시간 동안은 호감취소와 호감사유 변경을 할 수 없도록 작업

### 추가미션

- [x] 알림기능 구현

---

### 1. 네이버 클라우드 플랫폼을 통한 배포(도메인 없이, IP로 접속)

**배경** <br>
네이버클라우드플랫폼을 이용합니다. <br>
강사의 가이드영상대로 진행하시면 됩니다. <br>
나머지 미션을 수행하신 후 마지막에 진행하시면 됩니다. <br>

**목표** <br>
`https://서버IP:포트/` 형태로 접속이 가능 <br>
운영 서버에서는 각종 소셜로그인, 인스타 아이디 연결이 안되어도 됩니다.

---

### 2. 호감표시/호감사유변경 후, 개별 호감표시건에 대해서 3시간 동안은 호감취소와 호감사유 변경을 할 수 없도록 작업

**배경** <br>
현재 UI에서는 이 요구사항에 대한 작업이 완료되었습니다. <br>
백엔드 쪽에서 체크하는 로직만 추가하면 됩니다.

**목표** <br>
호감표시를 한 후 개별호감표시건에 대해서, 3시간 동안은 호감취소와 호감사유변경을 할 수 없도록 작업

**구현** <br>
1. usr/likeablePerson/list.html 확인
```html
th:unless="${likeablePerson.modifyUnlocked}"
th:text="${likeablePerson.modifyUnlockDateRemainStrHuman}">
```
likeablePerson entity 테이블에 로직 추가 필요성 확인

2. likeablePerson entity 테이블
```java
    public boolean isModifyUnlocked() {
        return modifyUnlockDate.isBefore(LocalDateTime.now());
    }

    // 초 단위에서 올림 해주세요.
    public String getModifyUnlockDateRemainStrHuman() {
        return "2시간 16분";
    }
```

isModifyUnlocked() 메서드를 통해 modifyUnlockDate가 수정가능해지는 시간임을 확인
getModifyUnlockDateRemainStrHuman() 메서드에서 isModifyUnlocked()를 이용해
남은 시간을 반환해주는 메서드임을 확인

3. 최대 등록 가능한 호감표시 수처럼 쿨타임 등록하기 위해
application.yml과 AppConfig 확인

```java
    @Getter
    private static long likeablePersonModifyCoolTime;

    @Value("${custom.likeablePerson.modifyCoolTime}")
    public void setLikeablePersonModifyCoolTime(long likeablePersonModifyCoolTime) {
        AppConfig.likeablePersonModifyCoolTime = likeablePersonModifyCoolTime;
    }

    public static LocalDateTime getLikeablePersonModifyUnlockDate() {
        return LocalDateTime.now().plusSeconds(likeablePersonModifyCoolTime);
    }
```
getLikeablePersonModifyUnlockDate() 메서드 활용 확인

4. 확인된 메서드들을 이용하여 쿨타임에 따라 호감표시를 수정/삭제 가능하게 하는 로직 구현
```java
    public String getModifyUnlockDateRemainStrHuman() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime modifyUnlockDate = AppConfig.getLikeablePersonModifyUnlockDate();

        if(isModifyUnlocked())
            return "변경 가능";

        long diff = ChronoUnit.SECONDS.between(now, modifyUnlockDate);
        long remainHours = (long) Math.floor(diff / (60.0 * 60));
        long remainMinutes = (long) Math.ceil((diff % (60.0 * 60)) / 60);
        return remainHours + "시간 " + remainMinutes + "분 후";
    }
```

현재 시간과 modifyUnlockDate 변수를 만들어서 두 시간의 초 차이로 남아있는 시간과 분을 구해 반환해주도록 한다.
시간과 분은 초단위에서 올림하도록 해준다.

5. 1분 미만 남았을 때의 케이스 추가
```java
if(Math.floor(diff / (60.0 * 60)) == 0 && Math.floor((diff % (60.0 * 60)) / 60 ) == 0)
return "1분 후";
```

---

선택미션 - 알림기능 구현

**배경** <br>
현재 알림페이지의 UI 레이아웃은 구현이 된 상태입니다. <br>
이를 기반으로 나머지 구현을 이어나가시면 됩니다. <br>
페이징 처리는 하지 않아도 됩니다. <br>

**목표** <br>
호감표시를 받았거나, 본인에 대한 호감사유가 변경된 경우에 알림페이지에서 확인이 가능하도록 해주세요. <br>
각각의 알림은 생성시에 readDate가 null이고, 사용자가 알림을 읽으면 readDate가 현재 날짜로 세팅되어야 합니다.

**구현** <br>
1. 내정보 페이지(usr/member/me.html)의 최근에 받은 호감 이용<br><br>
   
2. 1. 알림 목록에 등록된 데이터가 없는 경우(Empty)
   - 등록된 호감이 없습니다 라는 문구가 보이고 있는 경우에는 알림만 뜰 수 있도록 한다.<br><br>
   2. 알림 목록에 호감 표시 띄우기<br>
   - 최대 몇개까지 띄울건지 여부
   - 호감등록/변경시마다 notifycation list에 등록<br>
   - readDate값의 기본값은 null로 받고, 알림을 확인하면 읽은 시간을 표시할 수 있도록 함<br>

3. InstaMemberService의 whenAfterLike 메서드 안에서 알림 구현
```java
   public void whenAfterLike(LikeablePerson likeablePerson) {
        ~

        // 알림
        Notification notification = Notification.builder()
                .toInstaMember(toInstaMember)
                .fromInstaMember(fromInstaMember)
                .newGender(fromInstaMember.getGender())
                .newAttractiveTypeCode(likeablePerson.getAttractiveTypeCode())
                .typeCode("Like")
                .build();

        notificationService.save(notification);
    }
```
notification 엔티티의 toInstaMember와 fromInstaMember, newGender, newAttractiveTypeCode를 등록해준다
oldGender와 oldAttractiveTypeCode, readDate는 null 값이 등록된다

4.  usr/notification/list.html 알림페이지에서 알림 목록 구현
```html
   <div class="max-w-2xl w-full px-4">
        <h1 class="ml-8">
            <i class="fa-regular fa-bell"></i>
            알림
        </h1>

        <div class="text-center mt-10" th:if="${#lists.isEmpty(notifications)}">
            <i class="fa-regular fa-face-laugh"></i> 최근에 받은 알림이 없습니다.
        </div>

        <div class="card bg-base-100 shadow-xl flex mt-3"
             th:each="notification, i: ${notifications}">
            <div class="card-body">
                <a href="#" class="card-title">
                    <span th:text="|No. ${i.index + 1} / ${i.size}|"></span>
                </a>

                <div class="mt-4">
                    <div>
                        <i class="fa-regular fa-face-laugh-beam"></i> 호감표시자
                    </div>
                    <div class="mt-2">
                        <svg th:data-jdenticon-value="|${notification.jdenticon}|" width="120" height="120"></svg>
                    </div>
                </div>

                <div class="mt-4">
                    <div>
                        <i class="fa-regular fa-clock"></i>
                        호감표시
                    </div>
                    <div class="mt-2">
                            <span class="badge badge-primary"
                                  th:text="${#temporals.format(notification.createDate, 'yy.MM.dd HH:mm')}"></span>
                    </div>
                </div>

                <div class="mt-4">
                    <div>
                        <i class="fa-solid fa-person-half-dress"></i> 성별
                    </div>
                    <div class="mt-2">
                            <span class="badge badge-primary"
                                  th:utext="${notification.fromInstaMember.genderDisplayNameWithIcon}"></span>
                    </div>
                </div>

                <div class="mt-4">
                    <div>
                        <i class="fa-solid fa-check"></i>
                        호감사유
                    </div>
                    <div class="mt-2">
                            <span class="badge badge-primary"
                                  th:utext="${notification.attractiveTypeDisplayNameWithIcon}"></span>
                    </div>
                </div>
            </div>
        </div>

    </div>
```
jdenticon과 genderDisplayNameWithIcon이 notification entity 테이블에 없어서
테이블 내에 똑같이 구현해주었음

순서가 내정보 페이지와 다르게 반대로 나오는 것을 확인함 -> 추가 확인 필요

5. 호감 표시 수정 이벤트에도 구현 추가
```java
   public void whenAfterModifyAttractiveType(LikeablePerson likeablePerson, int oldAttractiveTypeCode) {
        ~

        // 알림
        Notification notification = Notification.builder()
                .toInstaMember(toInstaMember)
                .fromInstaMember(fromInstaMember)
                .newGender(fromInstaMember.getGender())
                .oldAttractiveTypeCode(oldAttractiveTypeCode)
                .newAttractiveTypeCode(likeablePerson.getAttractiveTypeCode())
                .typeCode("ModifyAttractiveType")
                .build();

        notificationService.save(notification);
    }
```

6. 수정 케이스
   1. **사용자의 성별이 변경된 경우(구현 안되어 있어 패스)** <br>
   2. 호감사유 변경된 경우 <br>
      oldAttractiveTypeCode와 newAttractiveTypeCode를 등록해준다
      변경된 내역을 페이지에서 보여줄 수 있도록 해준다.
      ```html
      <div class="mt-2">
          <span class="badge badge-primary"
                th:utext="${notification.getAttractiveTypeDisplayNameWithIcon(notification.newAttractiveTypeCode)}"
                th:if="${notification.typeCode.equals('Like')}"></span>

          <span th:if="${notification.typeCode.equals('ModifyAttractiveType')}">
              <span class="badge badge-primary"
                    th:utext="${notification.getAttractiveTypeDisplayNameWithIcon(notification.oldAttractiveTypeCode)}">
              </span>
              이(가)
              <span class="badge badge-primary"
                    th:utext="${notification.getAttractiveTypeDisplayNameWithIcon(notification.newAttractiveTypeCode)}">
              </span>
              으로 변경 되었습니다.
          </span>
      </div>
      ```
      
7.  알림 순서 최신순으로 정렬
```java
   @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showList(Model model) {
        ~
        notifications.sort(Comparator.comparing(Notification::getId).reversed());
        ~
    }
```

NotificationController에서 notification의 리스트를 id 기준으로 내림차순 정렬해주었다.

8. 알림 확인후 읽음 확인(readDate 갱신)
```html
      <div class="card bg-base-100 shadow-xl flex mt-3"
           th:if="${notification.readDate == null}"
           th:each="notification, i: ${notifications}"
           onclick="$(this).find('form').submit()">

            <form hidden th:action="@{|/usr/notification/read|}"></form>
      </div>
```
각 카드를 눌렀을 때 해당 알림의 읽음을 확인하고
readDate 추가 후 알림 목록에는 더 이상 뜨지 않게 하도록 함

- NotificationController에 read 추가
```java
    @GetMapping("/read/{id}")
    @PreAuthorize("isAuthenticated()")
    public String read(@PathVariable Long id, Model model) {
        notificationService.readNotification(id);
        return "redirect:/usr/notification/list";
    }
```
notificationService에서 알림을 읽었을때에 대한 처리를 해주고
해당 페이지로 돌아와준다.

- NotificationService에 readNotification 추가
```java
    public void readNotification(Long id) {
        Optional<Notification> OptNoti = notificationRepository.findById(id);
        if(OptNoti.isPresent()) {
        Notification notification = OptNoti.get();
        notification.setReadDate(LocalDateTime.now());
        save(notification);
        }
    }
```

파라미터로 받은 id값으로 해당 알림을 찾아 readDate를 갱신하고 저장해준다

9. 알림 페이지에서 읽은 알림은 안뜨게 하기
   - [x] readDate가 null인 알림만 뜨게 하기
   - [ ] readDate가 null이 아니여서 알림을 표시할 내용이 없는 경우 (알림 리스트 크기 - readDate가 null이 아닌 값들의 크기 == 0)
   

   1. 챗지피티의 답변을 이용해 구현해봤으나 lists.size가 'readDate != null'을 조건으로 인식하지 못한다.
      해당 문법이 없는 것 같다.
   ```html
        <div class="text-center mt-10" th:if="${#lists.isEmpty(notifications) or notifications.size() - #lists.size(notifications, 'readDate != null') == 0}">
            <i class="fa-regular fa-face-laugh"></i> 최근에 받은 알림이 없습니다.
        </div>
   ```
   
   2. 알림 목록 페이지를 켤때 notification을 toInstaMember로 찾아 list를 만들어준다.
      이때 readDate != null이 아닌 값은 list에 등록하지 않도록 해준다.
      stream의 filter 기능을 이용해주고 내림차순 정렬도 stream을 이용함.
   ```java
      @GetMapping("/list")
      @PreAuthorize("isAuthenticated()")
      public String showList(Model model) {
              if (!rq.getMember().hasConnectedInstaMember()) {
              return rq.redirectWithMsg("/usr/instaMember/connect", "먼저 본인의 인스타그램 아이디를 입력해주세요.");
              }
      
              List<Notification> notifications = notificationService.findByToInstaMember(rq.getMember().getInstaMember())
              .stream()
              .filter(notification -> notification.getReadDate() == null)
              .sorted(Comparator.comparing(Notification::getId).reversed()).collect(Collectors.toList());
              model.addAttribute("notifications", notifications);
      
              return "usr/notification/list";
              }
   ```

10. 추가 작업
   - [ ] 알림이 없는 경우 종 버튼 옆에 표시는 없게 만들어준다
   - [ ] 알림을 눌렀을 때 인스타아이디가 없으면 이동하는 페이지 수정
   - **해당 호감표시 등록을 삭제한 경우 알림도 삭제 될건지...**
