# FootballV2 1차 배포 가이드

## 구성

- Flutter Web: GitHub Pages
- Spring Boot API: Railway
- MySQL: Railway의 같은 프로젝트 안에 생성
- Spring profile: `prod`

## Railway 백엔드 환경변수

Railway에서 MySQL 서비스 이름이 `MySQL`인 경우 아래와 같이 설정한다.
서비스 이름이 다르면 `MySQL` 부분을 실제 이름으로 바꾼다.

```text
SPRING_PROFILES_ACTIVE=prod
DB_HOST=${{MySQL.RAILWAY_PRIVATE_DOMAIN}}
DB_PORT=3306
DB_NAME=${{MySQL.MYSQLDATABASE}}
DB_USERNAME=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}
JWT_SECRET=충분히_긴_무작위_비밀키
JWT_ACCESS_TOKEN_EXPIRATION=604800000
CORS_ALLOWED_ORIGINS=https://qlrxn1152.github.io
DB_MAX_POOL_SIZE=5
DB_MIN_IDLE=1
```

JWT 비밀키는 로컬 터미널에서 다음 명령으로 생성할 수 있다.

```bash
openssl rand -base64 48
```

출력값은 Railway 환경변수에만 저장하고 GitHub에 커밋하지 않는다.

## Railway 설정 순서

1. 새 프로젝트를 만든다.
2. `+ New`에서 MySQL을 추가한다.
3. `+ New > GitHub Repo`에서 `qlrxn1152/footballv2`를 선택한다.
4. 백엔드 서비스 Variables에 위 환경변수를 등록한다.
5. Settings > Networking에서 Railway 도메인을 생성한다.
6. `/actuator/health`가 HTTP 200을 반환하는지 확인한다.
7. 사용량 이메일 알림을 `$6`, Compute Hard Limit를 `$8`로 설정한다.

백엔드와 MySQL은 같은 Railway 프로젝트의 private network로 연결한다.
MySQL TCP Proxy나 공개 주소는 애플리케이션 연결에 사용하지 않는다.

## 운영 주의사항

- `ddl-auto=create` 또는 `create-drop`을 운영에서 사용하지 않는다.
- 1차 시험 운영은 `ddl-auto=update`를 사용한다.
- 운영이 안정되면 Flyway를 도입하고 `ddl-auto=validate`로 전환한다.
- Swagger와 H2 Console은 운영 프로필에서 비활성화된다.
- SQL 파라미터 TRACE 로그는 운영에서 비활성화된다.
- 첫 실제 경기 전과 이후에 DB 백업 상태를 확인한다.

## 배포 확인

```bash
curl https://RAILWAY_API_DOMAIN/actuator/health
```

정상 응답 예시:

```json
{"status":"UP"}
```
