# FootMatch V2 ERD Draft

## 1. member

회원 정보를 저장한다.

| 컬럼            | 설명      |
|---------------|---------|
| member_id     | 회원 ID   |
| username      | 로그인 ID  |
| password      | 해쉬 비밀번호 |
| member_rating | 회원 레이팅  |
| created_at    | 회원가입 시간 |

---

## 2. team

팀 정보를 저장한다.

| 컬럼          | 설명 |
|-------------|---|
| team_id     | 팀 ID |
| team_name   | 팀 이름 |
| team_rating | 팀 레이팅 |
| created_at  | 생성 시간 |

---

## 3. team_member

회원의 팀 소속과 역할을 저장한다.

| 컬럼               | 설명 |
|------------------|---|
| team_member_id   | 팀 멤버 ID |
| team_id          | 팀 ID |
| member_id        | 회원 ID |
| team_role (ENUM) | LEADER / MEMBER |
| joined_at        | 가입 시간 |

### 제약 조건

- member_id는 unique 처리한다.
- 한 회원은 하나의 팀에만 소속될 수 있다.
- 하나의 팀은 여러명의 회원이 있을수있다. ( 1명 `이상`의 회원이 존재 )
- 팀 생성 시 생성자는 team_members.team_role에 LEADER로 저장된다.

---

## 4. team_join_request

팀 가입 신청 정보를 저장한다.

| 컬럼                       | 설명 |
|--------------------------|---|
| team_join_request_id     | 가입 신청 ID |
| team_id                  | 신청 대상 팀 ID |
| member_id                | 신청한 회원 ID |
| team_join_request_status | PENDING / ACCEPTED / REJECTED |
| created_at               | 신청 시간 |

### 제약 조건

- 같은 회원은 같은 팀에 중복 신청할 수 없다.
- 이미 팀에 소속된 회원은 가입 신청할 수 없다. ( 1명의 회원은 0개 또는 1개의 팀만 가능하다.)
- 승인 시 team_members에 team_id 로, member_id 가 추가된다.

---

## 5. team_match

팀 매치 정보를 저장한다.

| 컬럼                | 설명 |
|-------------------|---|
| team_match_id     | 매치 ID |
| home_team_id      | 매치를 등록한 팀 |
| away_team_id      | 수락된 상대 팀 |
| team_match_status | PENDING / MATCHED / COMPLETED / CANCELLED |
| created_at        | 등록 시간 |

### 제약 조건

- 매치를 등록하면, 매치 등록 팀은 home_team이 된다.
- away_team은 매치 요청 수락 전까지 null일 수 있다. (매치가 수락되면, away_team 로 설정된다.)
- MATCHED 상태가 되면 away_team이 반드시 존재해야 한다.
- COMPLETED 상태가 되면 TeamMatchResult가 반드시 존재해야 한다. (결과를 입력하면, COMPLETED 가 된다.)

---

## 6. team_match_request

등록된 매치에 대한 참가 요청을 저장한다.

| 컬럼                        | 설명 |
|---------------------------|---|
| team_match_request_id     | 매치 요청 ID |
| team_match_id             | 요청 대상 매치 ID |
| request_team_id           | 요청을 보낸 팀 ID |
| team_match_request_status | PENDING / ACCEPTED / REJECTED |
| created_at                | 요청 시간 |

### 제약 조건

- 같은 팀은 같은 매치에 중복 요청할 수 없다.
- 홈팀은 자기 매치에 요청할 수 없다.
- 하나의 요청이 ACCEPTED 되면 같은 매치의 다른 PENDING 요청은 REJECTED 처리한다. ( 여러개의 신청이 들어온경우, 홈팀이 특정팀의 요청을 수락하면 나머지 요청의 status = REJECTED)

---

## 7. team_match_result

완료된 매치 결과를 저장한다.

| 컬럼                   | 설명 |
|----------------------|---|
| team_match_result_id | 결과 ID |
| match_id             | 매치 ID |
| home_score           | 홈팀 점수 |
| away_score           | 어웨이팀 점수 |
| created_at           | 결과 입력 시간 |

### 제약 조건

- match_id는 unique 처리한다.
- 하나의 매치는 하나의 결과만 가진다.
- MATCHED 상태의 매치에만 결과를 입력할 수 있다.
- 결과 입력 후 TeamMatch는 COMPLETED 상태가 된다.
