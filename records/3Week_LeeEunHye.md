## 이번주 수행 사항

### 필수미션

- [ ] 네이버클라우드플랫폼을 통한 배포(도메인 없이, IP로 접속)

- [x] 호감표시/호감사유변경 후, 개별 호감표시건에 대해서 <br> 3시간 동안은 호감취소와 호감사유변경을 할 수 없도록 작업

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


### 2. 호감표시/호감사유변경 후, 개별 호감표시건에 대해서 3시간 동안은 호감취소와 호감사유변경을 할 수 없도록 작업

**배경** <br>
현재 UI에서는 이 요구사항에 대한 작업이 완료되었습니다. <br>
백엔드 쪽에서 체크하는 로직만 추가하면 됩니다.

**목표** <br>
호감표시를 한 후 개별호감표시건에 대해서, 3시간 동안은 호감취소와 호감사유변경을 할 수 없도록 작업

**구현** <br>
1. usr/likeablePerson/list.html 확인
```
th:unless="${likeablePerson.modifyUnlocked}"
th:text="${likeablePerson.modifyUnlockDateRemainStrHuman}">
```
likeablePerson entity 테이블에 로직 추가 필요성 확인

2. likeablePerson entity 테이블
```
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

```
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
```
    public String getModifyUnlockDateRemainStrHuman() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime modifyUnlockDate = AppConfig.getLikeablePersonModifyUnlockDate();

        if(isModifyUnlocked())
            return "변경 가능";

        long duration = Duration.between(now, modifyUnlockDate).getSeconds();
        long remainHours = (long) Math.ceil(duration / (60.0 * 60));
        long remainMinutes = (long) Math.ceil((duration % (60.0 * 60)) / 60);
        return remainHours + "시간 " + remainMinutes + "분 후";
    }
```

현재 시간과 modifyUnlockDate 변수를 만들어서 두 시간의 초 차이로 남아있는 시간과 분을 구해 반환해주도록 한다.
시간과 분은 초단위에서 올림하도록 해준다.

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
   
2. 1. notification list에 등록된 데이터가 없는 경우(Empty)
   등록된 호감이 없습니다 라는 문구가 보이고 있는 경우에는 알림만 뜰 수 있도록<br><br>
   2. 호감 표시 가져오는 것까지는 ok,<br>
   최대 몇개까지 띄울건지 여부와 notifycation list에 등록해야할 필요성<br>
   readDate값의 기본값은 null로 받고, 읽은 시간을 표시할 수 있도록 함<br>
   호감등록/변경시 null로 저장되어야 함 = 호감등록/변경시마다 notifycation list에 등록

