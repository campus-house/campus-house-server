# 🏠 캠퍼스 하우스 (Campus House)

자취 기반 커뮤니티 앱의 백엔드 서버입니다. Spring Boot를 사용하여 개발되었습니다.

## 📋 프로젝트 개요

캠퍼스 하우스는 대학생들의 자취 생활을 위한 커뮤니티 플랫폼입니다. 거주자와 비거주자가 함께 소통하며 정보를 공유할 수 있는 공간을 제공합니다.

## 🚀 주요 기능

### 1. 게시판 시스템 ✅
- **게시판 타입별 관리**: 아파트소식, 질문게시판, 동네소식
- **게시글 CRUD**: 작성, 조회, 수정, 삭제 (작성자만 수정/삭제 가능)
- **이미지 첨부**: 게시글에 이미지 포함 가능
- **검색 기능**: 제목 + 내용 통합 검색
- **정렬 옵션**: 최신순, 인기순 (좋아요 수 기준)
- **상호작용**: 좋아요, 북마크 (스크랩) 기능

### 2. 댓글 시스템 ✅
- **댓글 관리**: 댓글 작성, 조회, 수정, 삭제
- **대댓글 지원**: 트리 구조로 계층적 댓글 시스템
- **실시간 알림**: 댓글/대댓글 작성 시 알림 전송
- **권한 관리**: 작성자만 수정/삭제 가능

### 3. 채팅 시스템 ✅
- **1:1 채팅**: 사용자 간 실시간 채팅
- **채팅방 관리**: 자동 생성/조회, 메시지 히스토리
- **읽음 처리**: 메시지 읽음 상태 관리
- **알림 시스템**: 읽지 않은 메시지 수 표시
  - 채팅 알림: 새 메시지 수신 시 알림(NotificationType=CHAT_MESSAGE) 생성 (REST 기반)

### 4. 인증 시스템 ✅
- **JWT 토큰 기반 인증**: 안전한 토큰 기반 인증 시스템
- **회원가입/로그인**: 이메일, 닉네임, 사용자 타입별 가입
- **프로필 관리**: 개인정보 수정, 거주지 인증 상태 확인
- **거주지 인증**: 건물별 거주자 인증 및 권한 관리
- **시연용 계정**: 미리 생성된 테스트 계정 제공

### 5. 마이페이지 ✅
- **프로필 관리**: 개인정보 수정, 거주지 인증 상태 확인
- **활동 내역**: 내가 작성한 게시글/댓글, 좋아요한 글, 저장한 글(게시글 북마크)
- **캐릭터 시스템**: 포인트 획득/사용, 캐릭터 가챠, 대표 캐릭터 설정
- **포인트 관리**: 포인트 내역 조회, 통계 확인

### 6. 건물 정보 관리 ✅
- **네이버 지도 API 연동**: 주소-좌표 변환, 지역 검색
- **건물 정보 조회**: 지도에서 직관적인 건물 정보 탐색
- **검색 기능**: 건물명, 주소, 키워드 검색
- **필터 기능**: 보증금/월세/전세, 주차장, 엘리베이터, 학교/영통역 접근성별 필터링
- **건물 상세 정보**: 기본 정보, 실거주자 후기, Q&A, 양도 정보
- **스크랩 기능**: 관심 건물 저장 및 관리

## 🛠 기술 스택

- **Backend**: Spring Boot 3.5.5
- **Database**: MySQL 8.0
- **ORM**: JPA/Hibernate
- **Security**: Spring Security + JWT
- **Build Tool**: Gradle
- **Java Version**: 21

## 📦 의존성

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-websocket'
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    implementation 'io.jsonwebtoken:jjwt-impl:0.11.5'
    implementation 'io.jsonwebtoken:jjwt-jackson:0.11.5'
    implementation 'mysql:mysql-connector-java:8.0.33'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
}
```

## 🗄 데이터베이스 설정

### MySQL 설정
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_house?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### JPA 설정
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

## 📁 프로젝트 구조

```
src/main/java/com/example/campus_house/
├── CampusHouseApplication.java          # 메인 애플리케이션
├── config/
│   ├── JpaConfig.java                  # JPA 설정
│   ├── SecurityConfig.java             # Spring Security 설정
│   ├── SwaggerConfig.java              # Swagger 설정
│   └── WebConfig.java                  # Web 설정
├── controller/                         # REST API 컨트롤러
│   ├── PostController.java             # 게시글 관리
│   ├── CommentController.java          # 댓글 관리
│   ├── ChatController.java             # 채팅 관리 (구현 예정)
│   ├── AuthController.java             # 인증 관리
│   ├── MyPageController.java           # 마이페이지
│   ├── CharacterController.java        # 캐릭터 관리
│   ├── NotificationController.java     # 알림 관리
│   ├── ScrapController.java            # 스크랩 관리 (매물 스크랩 목록)
│   └── PropertyController.java         # 매물 관리
├── entity/                             # JPA 엔티티
│   ├── User.java                       # 사용자
│   ├── Post.java                       # 게시글
│   ├── Comment.java                    # 댓글 (트리 구조)
│   ├── Like.java                       # 좋아요
│   ├── Bookmark.java                   # 북마크
│   ├── ChatRoom.java                   # 채팅방
│   ├── BoardType.java                  # 게시판 타입 enum
│   ├── Character.java                  # 캐릭터
│   ├── UserCharacter.java              # 사용자 캐릭터
│   ├── PointHistory.java               # 포인트 내역
│   ├── Notification.java               # 알림
│   └── Property.java                   # 매물
├── repository/                         # 데이터 접근 계층
│   ├── UserRepository.java
│   ├── PostRepository.java
│   ├── CommentRepository.java
│   ├── LikeRepository.java
│   ├── BookmarkRepository.java
│   ├── ChatRoomRepository.java
│   └── ...
├── service/                           # 비즈니스 로직
│   ├── PostService.java
│   ├── CommentService.java
│   ├── LikeService.java
│   ├── BookmarkService.java
│   ├── AuthService.java
│   ├── CharacterService.java
│   ├── PointService.java
│   ├── NotificationService.java
│   └── ...
└── scheduler/                         # 스케줄러
    └── BoardScheduler.java
```

## 🔧 실행 방법

### 1. 데이터베이스 설정
```sql
CREATE DATABASE campus_house;
```

### 2. 네이버 API 설정
네이버 클라우드 플랫폼에서 API 키를 발급받고 설정합니다.

#### 2.1 네이버 클라우드 플랫폼 설정
1. [네이버 클라우드 플랫폼](https://www.ncloud.com/) 접속
2. Maps API 서비스 신청
3. API 키 발급 (Client ID, Client Secret)

#### 2.2 application.properties 설정
```properties
# 네이버 API 설정
naver.api.client-id=your-naver-client-id
naver.api.client-secret=your-naver-client-secret
naver.api.style-code=922c3502-bc54-427b-a1fa-f99887a68a64
naver.api.version=20250913162051
```

#### 2.3 스타일코드와 버전 정보
- **스타일코드**: `922c3502-bc54-427b-a1fa-f99887a68a64`
  - 지도 스타일링을 위한 고유 식별자
  - 커스텀 지도 테마 적용 시 사용
- **버전**: `20250913162051`
  - API 버전 관리용
  - 향후 API 업데이트 시 호환성 보장

### 3. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 4. 서버 접속
```
http://localhost:8080
```

### 5. 시연용 계정
애플리케이션 시작 시 자동으로 생성되는 테스트 계정입니다.

#### 🏠 거주지 인증 완료된 거주자 계정
- **이메일**: `resident@example.com`
- **비밀번호**: `password123`
- **닉네임**: `기숙사생`
- **인증 상태**: ✅ 거주지 인증 완료 (캠퍼스 하우스 A동)
- **포인트**: 1000 포인트
- **접근 가능**: 동네 게시판, 아파트 일반 게시판, 아파트 질문 게시판

#### 🏠 거주지 인증 완료된 거주자 계정 2
- **이메일**: `resident2@example.com`
- **비밀번호**: `password123`
- **닉네임**: `선배`
- **인증 상태**: ✅ 거주지 인증 완료 (캠퍼스 하우스 A동)
- **포인트**: 1000 포인트
- **접근 가능**: 동네 게시판, 아파트 일반 게시판, 아파트 질문 게시판

#### 🏢 거주지 인증 안 된 사용자 계정
- **이메일**: `nonresident@example.com`
- **비밀번호**: `password123`
- **닉네임**: `외부거주생`
- **인증 상태**: ❌ 거주지 인증 안 됨
- **포인트**: 500 포인트
- **접근 가능**: 동네 게시판, 아파트 질문 게시판 (아파트 일반 게시판 접근 불가)

## 📚 API 문서

### 게시글 관련 API

#### 게시글 관리
```http
# 게시글 작성 (APARTMENT, QUESTION 게시판은 거주지 인증 필수)
POST /api/boards/{type}/posts
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "기숙사 식당 운영시간이 궁금해요",
  "content": "새로 입사했는데 식당 운영시간을 모르겠어요",
  "imageUrl": "https://example.com/image.jpg"
}

# 게시판별 접근 권한:
# - APARTMENT: 거주지 인증 필수
# - QUESTION: 거주지 인증 필수  
# - LOCAL: 인증 불필요
# - TRANSFER: 인증 불필요

# 모든 게시글 조회 (페이징)
GET /api/boards/{type}/posts?page=0&size=20

# 특정 게시글 조회
GET /api/posts/{id}

# 내가 작성한 게시글만 수정
PUT /api/posts/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "수정된 제목",
  "content": "수정된 내용",
  "imageUrl": "https://example.com/new-image.jpg"
}

# 내가 작성한 게시글만 삭제
DELETE /api/posts/{id}
Authorization: Bearer {token}
```

#### 게시글 조회
```http
# 최신순 조회
GET /api/boards/{type}/posts/latest?page=0&size=20

# 인기순 조회 (좋아요 수 기준)
GET /api/boards/{type}/posts/popular?page=0&size=20

# 제목 + 내용에서 검색
GET /api/boards/{type}/posts/search?keyword=검색어&page=0&size=20

# QUESTION 게시판 조회 (거주지 기반 필터링) - 거주지 인증 필수
GET /api/boards/question/posts?page=0&size=20
Authorization: Bearer {token}  # 거주지 인증된 사용자만 자신이 거주하는 건물의 질문 조회

# APARTMENT 게시판 조회 (거주지 기반 필터링) - 거주지 인증 필수
GET /api/boards/apartment/posts?page=0&size=20
Authorization: Bearer {token}  # 거주지 인증된 사용자만 자신이 거주하는 건물의 게시글 조회

# 거주지 기반 질문 수 조회 - 거주지 인증 필수
GET /api/boards/question/posts/count
Authorization: Bearer {token}

# 거주지 기반 APARTMENT 게시글 수 조회 - 거주지 인증 필수
GET /api/boards/apartment/posts/count
Authorization: Bearer {token}
```

#### 상호작용
```http
# 좋아요 달기
POST /api/posts/{postId}/like
Authorization: Bearer {token}

# 북마크 하기 (스크랩)
POST /api/posts/{postId}/bookmark
Authorization: Bearer {token}
```

### 댓글 관련 API

#### 댓글 관리
```http
# 댓글 작성
POST /api/posts/{postId}/comments
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "저도 궁금했는데 감사합니다!",
  "parentId": null
}

# 대댓글 작성
POST /api/comments/{parentCommentId}/replies
Authorization: Bearer {token}
Content-Type: application/json

{
  "postId": 1,
  "content": "대댓글 내용입니다"
}

# 댓글 목록 조회
GET /api/posts/{postId}/comments

# 댓글 수정
PUT /api/comments/{commentId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "수정된 댓글 내용"
}

# 댓글 삭제
DELETE /api/comments/{commentId}
Authorization: Bearer {token}
```

### 채팅 관련 API ✅

#### 채팅 관리
```http
# 채팅방 생성/조회
POST /api/chat/rooms?otherUserName={userName}
Authorization: Bearer {token}

# 사용자의 채팅방 목록
GET /api/chat/rooms/user/{userName}
Authorization: Bearer {token}

# 특정 채팅방 조회
GET /api/chat/rooms/{roomId}
Authorization: Bearer {token}

# 두 사용자 간 채팅방 조회
GET /api/chat/rooms/users/{user1Name}/{user2Name}
Authorization: Bearer {token}

# 메시지 전송
POST /api/chat/rooms/{roomId}/messages
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "안녕하세요!",
  "messageType": "TEXT"
}

# 메시지 목록 조회
GET /api/chat/rooms/{roomId}/messages?page=0&size=50
Authorization: Bearer {token}

# 메시지 읽음 처리
POST /api/chat/rooms/{roomId}/read
Authorization: Bearer {token}

# 읽지 않은 메시지 수 조회
GET /api/chat/unread-count/{userName}
Authorization: Bearer {token}

# 특정 채팅방의 읽지 않은 메시지 수 조회
GET /api/chat/rooms/{roomId}/unread-count/{userName}
Authorization: Bearer {token}
```

#### 채팅 알림 동작
```text
- 새로운 채팅 메시지 전송 시, 수신자에게 알림 생성
- NotificationType: CHAT_MESSAGE
- 제목: "새 메시지", 내용: 메시지 본문 미리보기
- relatedType=CHAT_ROOM, relatedId=채팅방 ID
- 읽음 처리: /api/notifications/{notificationId}/read 또는 /api/notifications/read-all
```

### 인증 관련 API

#### 회원가입
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "nickname": "사용자",
  "userType": "RESIDENT",
  "location": "서울대학교 기숙사",
  "university": "서울대학교",
  "major": "컴퓨터공학과"
}
```

#### 로그인
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### 토큰 검증
```http
POST /api/auth/verify
Authorization: Bearer {token}
```

#### 이메일 중복 확인
```http
GET /api/auth/check-email?email=user@example.com
```

#### 닉네임 중복 확인
```http
GET /api/auth/check-nickname?nickname=사용자
```

#### 비밀번호 변경
```http
POST /api/auth/change-password
Authorization: Bearer {token}
Content-Type: application/json

{
  "currentPassword": "oldpassword",
  "newPassword": "newpassword"
}
```

#### 프로필 수정
```http
PUT /api/auth/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "nickname": "새닉네임",
  "location": "새위치",
  "university": "서울대학교",
  "major": "새전공",
  "introduction": "자기소개"
}
```

### 건물 정보 관련 API ✅

#### 위치 기반 건물 검색
```http
GET /api/buildings/nearby?latitude=37.5665&longitude=126.9780&radiusKm=1.0
```

#### 건물 정보 검색
```http
# 키워드 검색
GET /api/buildings/search?keyword=서울대학교

# 건물명으로 검색
GET /api/buildings/search/building?buildingName=아파트

# 주소로 검색
GET /api/buildings/search/address?address=서울시 관악구
```

#### 건물 정보 필터링
```http
# 보증금 범위 필터 (0~3000만원 이상)
GET /api/buildings/search/filters?minDeposit=500&maxDeposit=2000

# 월세 범위 필터 (0~100만원 이상)
GET /api/buildings/search/filters?minMonthlyRent=30&maxMonthlyRent=80

# 전세 범위 필터 (0~10억 이상)
GET /api/buildings/search/filters?minJeonse=5000&maxJeonse=30000

# 주차장 필터 (필요해요/필요없어요)
GET /api/buildings/search/filters?parkingRequired=true

# 학교 접근성 필터 (도보 10분 이내, 10~20분, 30분)
GET /api/buildings/search/filters?schoolAccessibility=WALKING_10MIN

# 영통역 접근성 필터 (도보 5분, 10분, 15분, 20분 이내)
GET /api/buildings/search/filters/station-accessibility?maxWalkingTime=10

# 엘리베이터 필터 (필요해요/필요없어요)
GET /api/buildings/search/filters?elevatorRequired=true

# 복합 필터 (여러 조건 조합)
GET /api/buildings/search/filters?minDeposit=500&maxDeposit=2000&parkingRequired=true&schoolAccessibility=WALKING_10MIN
```

#### 건물 상세 정보 조회
```http
GET /api/buildings/{buildingId}
```

#### 건물 상세 페이지 탭별 API
```http
# 기본 정보 탭
GET /api/buildings/{buildingId}/building-info

# 실거주자 후기 탭
GET /api/buildings/{buildingId}/reviews?page=0&size=20

# 건물 후기 작성 (거주지 인증 필수)
POST /api/mypage/building-reviews
Authorization: Bearer {token}
Content-Type: application/json

{
  "buildingId": 1,
  "title": "후기 제목",
  "content": "후기 내용",
  "imageUrl": "https://example.com/image.jpg",
  "rating": 5,
  "noiseLevel": 3,
  "safetyLevel": 4,
  "convenienceLevel": 5,
  "managementLevel": 4,
  "pros": "장점들",
  "cons": "단점들",
  "livingPeriod": "1년"
}

# 질문하기 탭 (Post 기반)
GET /api/buildings/{buildingId}/qnas?page=0&size=20

# 건물별 질문 작성 (거주지 인증 필수)
POST /api/buildings/{buildingId}/questions
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "질문 제목",
  "content": "질문 내용"
}

# 양도 탭 (Post 기반)
GET /api/buildings/{buildingId}/transfers?page=0&size=20

# 건물별 양도 글 작성
POST /api/buildings/{buildingId}/transfers
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "양도 글 제목",
  "content": "양도 글 내용"
}
```

#### 최근 등록된 건물 정보 조회
```http
GET /api/buildings/recent?page=0&size=20
```

#### 인기 건물 조회 (스크랩 수 기준)
```http
GET /api/buildings/popular?page=0&size=20
```

#### 건물 용도별 필터
```http
# 건물 용도별 검색 (오피스텔, 아파트, 원룸 등)
GET /api/buildings/search/filters/building-usage?buildingUsage=오피스텔

# 건물 용도 목록 조회
GET /api/buildings/building-usages
```

#### 주변 시설 기반 검색 (외부 API 연동 예정)
```http
# 편의점 개수로 검색
GET /api/buildings/search/filters/convenience-stores?minConvenienceStores=3

# 마트 개수로 검색
GET /api/buildings/search/filters/marts?minMarts=2

# 병원 개수로 검색
GET /api/buildings/search/filters/hospitals?minHospitals=1
```

#### 건물 정보 생성
```http
POST /api/buildings
Content-Type: application/json

{
  "buildingName": "캠퍼스 하우스 A동",
  "address": "서울시 강남구 테헤란로 123",
  "latitude": 37.5665,
  "longitude": 126.9780,
  "deposit": 1000,
  "monthlyRent": 50,
  "jeonse": 10000,
  "managementFee": 5,
  "households": 200,
  "heatingType": "개별난방",
  "elevators": 2,
  "buildingUsage": "오피스텔 84%, 기타제2종근린생활시설 16%",
  "approvalDate": "2020-01-15T00:00:00",
  "completionDate": "2019-12-01T00:00:00",
  "nearbyFacilities": "편의점, 카페, 은행",
  "schoolWalkingTime": 15,
  "stationWalkingTime": 8
}
```

#### 건물 정보 수정
```http
PUT /api/buildings/{buildingId}
Content-Type: application/json

{
  "buildingName": "수정된 건물명",
  "deposit": 1200,
  "monthlyRent": 60
}
```

#### 건물 정보 삭제
```http
DELETE /api/buildings/{buildingId}
```

#### 건물 스크랩
```http
# 건물 스크랩
POST /api/buildings/{buildingId}/scrap
Authorization: Bearer {token}

# 건물 스크랩 취소
DELETE /api/buildings/{buildingId}/scrap
Authorization: Bearer {token}

# 건물 스크랩 여부 확인
GET /api/buildings/{buildingId}/scrap/status
Authorization: Bearer {token}
```

#### 네이버 API 연동
```http
# 주소를 좌표로 변환
GET /api/buildings/geocode?address=서울시 관악구

# 좌표를 주소로 변환
GET /api/buildings/reverse-geocode?latitude=37.5665&longitude=126.9780

# 장소 검색
GET /api/buildings/search/places?query=서울대학교&display=10

# 학교까지 걸리는 시간 계산
GET /api/buildings/{buildingId}/school-walking-time?schoolId=1
```

### 메모 관련 API ✅

#### 메모 관리
```http
# 활성 메모 목록 조회
GET /api/memos

# 특정 타입의 메모 조회
GET /api/memos/type/{type}

# 메모 생성
POST /api/memos
Content-Type: application/json

{
  "userId": 1,
  "content": "메모 내용",
  "imageUrl": "https://example.com/image.jpg",
  "type": "MEETUP",
  "location": "서울대학교",
  "maxParticipants": 5,
  "contactInfo": "010-1234-5678",
  "deadline": "2024-01-20T18:00:00"
}

# 메모 답장/채팅 작성
POST /api/memos/{memoId}/replies
Content-Type: application/json

{
  "userId": 1,
  "content": "답장 내용",
  "imageUrl": "https://example.com/image.jpg",
  "type": "REPLY"
}

# 메모 참여 신청
POST /api/memos/{memoId}/participate
Content-Type: application/json

{
  "userId": 1,
  "message": "참여하고 싶습니다!"
}

# 메모 참여 승인/거부
PUT /api/memos/participants/{participantId}
Content-Type: application/json

{
  "status": "APPROVED"
}

# 메모 답장/채팅 목록 조회
GET /api/memos/{memoId}/replies

# 메모 참여자 목록 조회
GET /api/memos/{memoId}/participants

# 메모 삭제
DELETE /api/memos/{memoId}?userId=1
```

### 알림 관련 API ✅

#### 알림 관리
```http
# 내 알림 목록 조회 (페이징)
GET /api/notifications?page=0&size=20
Authorization: Bearer {token}

# 읽지 않은 알림 조회
GET /api/notifications/unread
Authorization: Bearer {token}

# 읽지 않은 알림 개수 조회
GET /api/notifications/unread/count
Authorization: Bearer {token}

# 알림 읽음 처리
POST /api/notifications/{notificationId}/read
Authorization: Bearer {token}

# 모든 알림 읽음 처리
POST /api/notifications/read-all
Authorization: Bearer {token}

# 알림 삭제
DELETE /api/notifications/{notificationId}
Authorization: Bearer {token}

# 특정 타입의 알림 조회
GET /api/notifications/type/{type}
Authorization: Bearer {token}
```

### 스크랩 관련 API ✅

#### 건물 스크랩 관리
```http
# 사용자의 건물 스크랩 목록 (페이징)
GET /api/scraps/buildings?page=0&size=20
Authorization: Bearer {token}

# 건물 스크랩
POST /api/scraps/buildings/{buildingId}
Authorization: Bearer {token}

# 건물 스크랩 취소
DELETE /api/scraps/buildings/{buildingId}
Authorization: Bearer {token}
```

### 거주지 인증 관련 API ✅

#### 거주지 인증 신청
```http
POST /api/verification/request
Content-Type: application/json

{
  "userId": 1,
  "buildingId": 1,
  "buildingName": "캠퍼스 하우스 A동",
  "buildingAddress": "서울시 강남구 테헤란로 123",
  "roomNumber": "101호",
  "verificationDocument": "임대계약서_이미지_URL"
}
```

#### 사용자 인증 상태 조회
```http
GET /api/verification/user/{userId}
```

#### 사용자의 승인된 인증 조회
```http
GET /api/verification/user/{userId}/approved
```

#### 대기 중인 인증 신청 목록 조회 (관리자용)
```http
GET /api/verification/pending
```

#### 거주지 인증 승인 (관리자용)
```http
POST /api/verification/{verificationId}/approve
Content-Type: application/json

{
  "adminId": 1,
  "comment": "인증 완료"
}
```

#### 거주지 인증 거부 (관리자용)
```http
POST /api/verification/{verificationId}/reject
Content-Type: application/json

{
  "adminId": 1,
  "comment": "서류 부족"
}
```

#### 거주지 인증 취소
```http
DELETE /api/verification/{verificationId}?userId=1
```

#### 건물별 인증된 사용자 조회
```http
GET /api/verification/building/{buildingId}
```

### 마이페이지 관련 API

#### 프로필 조회
```http
GET /api/mypage/profile
Authorization: Bearer {token}
```

#### 프로필 수정
```http
PUT /api/mypage/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "nickname": "새닉네임",
  "profileImage": "새프로필이미지.png",
  "characterImage": "새캐릭터이미지.png",
  "location": "새위치",
  "university": "서울대학교",
  "major": "새전공",
  "introduction": "새자기소개"
}
```

#### 내 게시글 조회
```http
GET /api/mypage/posts?page=0&size=20
Authorization: Bearer {token}
```

#### 내 북마크 조회
```http
GET /api/mypage/bookmarks?page=0&size=20
Authorization: Bearer {token}
```

#### 내 건물 스크랩 조회 (마이페이지에서 분리)
```http
GET /api/scraps/buildings?page=0&size=20
Authorization: Bearer {token}
```

#### 내 댓글 조회
```http
GET /api/mypage/comments
Authorization: Bearer {token}
```

#### 내 거주지 조회
```http
GET /api/mypage/residence
Authorization: Bearer {token}
```

#### 내 건물 후기 조회
```http
GET /api/mypage/building-reviews?page=0&size=20
Authorization: Bearer {token}
```

#### 건물 후기 작성
```http
POST /api/mypage/building-reviews
Authorization: Bearer {token}
Content-Type: application/json

{
  "buildingId": 1,
  "title": "후기 제목",
  "content": "후기 내용",
  "imageUrl": "https://example.com/image.jpg",
  "rating": 5,
  "noiseLevel": 3,
  "safetyLevel": 4,
  "convenienceLevel": 5,
  "managementLevel": 4,
  "pros": "장점들",
  "cons": "단점들",
  "livingPeriod": "1년"
}
```

#### 캐릭터 통계 조회
```http
GET /api/mypage/characters/stats
Authorization: Bearer {token}
```

### 캐릭터 관련 API ✅

#### 모든 캐릭터 조회
```http
GET /api/characters
```

#### 희귀도별 캐릭터 조회
```http
GET /api/characters/rarity/{rarity}
```
- `rarity`: `COMMON`, `RARE`, `EPIC`, `LEGENDARY`

#### 특정 캐릭터 상세 조회
```http
GET /api/characters/{characterId}
```

#### 내 보유 캐릭터 조회
```http
GET /api/mypage/characters
Authorization: Bearer {token}
```

#### 캐릭터 가챠
```http
POST /api/mypage/characters/gacha
Authorization: Bearer {token}
```

#### 캐릭터 구매
```http
POST /api/mypage/characters/{characterId}/purchase
Authorization: Bearer {token}
```

#### 대표 캐릭터 설정
```http
POST /api/mypage/characters/{characterId}/set-main
Authorization: Bearer {token}
```

### 포인트 관련 API

#### 포인트 내역 조회
```http
GET /api/mypage/points/history?page=0&size=20
Authorization: Bearer {token}
```

#### 포인트 통계 조회
```http
GET /api/mypage/points/stats
Authorization: Bearer {token}
```

## 🗃 데이터 모델

### User (사용자)
- `id`: 사용자 ID
- `email`: 이메일
- `nickname`: 닉네임
- `userType`: 사용자 타입 (RESIDENT/NON_RESIDENT)
- `isVerified`: 거주지 인증 여부
- `verifiedBuildingId`: 인증된 건물 ID
- `verifiedBuildingName`: 인증된 건물명
- `points`: 보유 포인트
- `mainCharacterId`: 대표 캐릭터 ID
- `characterImage`: 캐릭터 이미지

### Post (게시글)
- `id`: 게시글 ID
- `title`: 제목
- `content`: 내용
- `imageUrl`: 이미지 URL
- `boardType`: 게시판 타입 (APARTMENT/QUESTION/LOCAL)
- `author`: 작성자 (User 참조)
- `likeCount`: 좋아요 수
- `bookmarkCount`: 북마크 수
- `commentCount`: 댓글 수
- `viewCount`: 조회수
- `createdAt`: 생성일시
- `updatedAt`: 수정일시

### Comment (댓글)
- `id`: 댓글 ID
- `post`: 게시글 (Post 참조)
- `author`: 작성자 (User 참조)
- `parent`: 부모 댓글 (Comment 참조, 대댓글용)
- `children`: 자식 댓글들 (Comment 리스트)
- `content`: 댓글 내용
- `createdAt`: 생성일시
- `updatedAt`: 수정일시

### Like (좋아요)
- `id`: 좋아요 ID
- `post`: 게시글 (Post 참조)
- `user`: 사용자 (User 참조)
- `createdAt`: 생성일시

### Bookmark (북마크)
- `id`: 북마크 ID
- `post`: 게시글 (Post 참조)
- `user`: 사용자 (User 참조)
- `createdAt`: 생성일시

### ChatRoom (채팅방)
- `id`: 채팅방 ID
- `user1`: 사용자1 (User 참조)
- `user2`: 사용자2 (User 참조)
- `createdAt`: 생성일시

### BoardType (게시판 타입)
- `APARTMENT`: 아파트소식
- `QUESTION`: 질문게시판
- `LOCAL`: 동네소식
- `TRANSFER`: 양도게시판

### Building (건물 정보)
- `id`: 건물 ID
- `buildingName`: 건물명
- `address`: 주소
- `latitude/longitude`: 위도/경도
- `deposit`: 보증금 (만원)
- `monthlyRent`: 월세 (만원)
- `jeonse`: 전세 (만원)
- `households`: 세대수
- `heatingType`: 난방방식
- `elevators`: 승강기대수
- `buildingUsage`: 건축물용도
- `approvalDate`: 사용승인일
- `completionDate`: 준공일
- `nearbyConvenienceStores`: 반경 1km 이내 편의점 개수 (외부 API 연동 예정)
- `nearbyMarts`: 반경 1km 이내 마트 개수 (외부 API 연동 예정)
- `nearbyHospitals`: 반경 1km 이내 병원 개수 (외부 API 연동 예정)
- `schoolWalkingTime`: 학교까지 걸리는 시간 (분)
- `stationWalkingTime`: 영통역까지 걸리는 시간 (분)
- `scrapCount`: 스크랩 수
- `createdAt`: 생성일시
- `updatedAt`: 수정일시

### BuildingReview (건물 후기)
- `id`: 후기 ID
- `buildingId`: 건물 ID
- `userId`: 작성자 ID
- `title`: 후기 제목
- `content`: 후기 내용
- `imageUrl`: 후기 이미지
- `rating`: 평점 (1-5)
- `pros/cons`: 장점/단점
- `livingPeriod`: 거주 기간
- `noiseLevel`: 소음 수준 (1-5)
- `safetyLevel`: 안전 수준 (1-5)
- `convenienceLevel`: 편의성 수준 (1-5)
- `managementLevel`: 관리 수준 (1-5)
- `likeCount`: 좋아요 수

### Post (게시글) - 건물 관련 확장
- `id`: 게시글 ID
- `title`: 제목
- `content`: 내용
- `imageUrl`: 이미지 URL
- `boardType`: 게시판 타입 (APARTMENT/QUESTION/LOCAL/TRANSFER)
- `author`: 작성자 (User 참조)
- `building`: 건물 (Building 참조, QUESTION/TRANSFER 게시판에서만 사용)
- `likeCount`: 좋아요 수
- `bookmarkCount`: 북마크 수
- `commentCount`: 댓글 수
- `viewCount`: 조회수
- `scrapCount`: 스크랩 수
- `createdAt`: 생성일시
- `updatedAt`: 수정일시

### BuildingImage (건물 이미지)
- `id`: 이미지 ID
- `buildingId`: 건물 ID
- `imageUrl`: 이미지 URL
- `description`: 이미지 설명
- `sortOrder`: 정렬 순서

### BuildingScrap (건물 스크랩)
- `id`: 스크랩 ID
- `userId`: 사용자 ID
- `buildingId`: 건물 ID
- `createdAt`: 스크랩 일시

### Character (캐릭터)
- `id`: 캐릭터 ID
- `name`: 캐릭터 이름
- `description`: 캐릭터 설명
- `imageUrl`: 캐릭터 이미지 URL
- `rarity`: 희귀도 (COMMON/RARE/EPIC/LEGENDARY)
- `price`: 가격 (포인트)
- `isActive`: 활성화 여부

### UserCharacter (사용자 캐릭터)
- `id`: 사용자 캐릭터 ID
- `user`: 사용자
- `character`: 캐릭터
- `isMain`: 대표 캐릭터 여부
- `quantity`: 보유 수량
- `obtainedAt`: 획득 시간

### PointHistory (포인트 내역)
- `id`: 포인트 내역 ID
- `user`: 사용자
- `type`: 포인트 타입 (획득/사용)
- `amount`: 포인트 양 (양수: 획득, 음수: 사용)
- `balance`: 잔액
- `description`: 설명
- `relatedId`: 관련 ID (게시글 ID, 캐릭터 ID 등)

### ResidenceVerification (거주지 인증)
- `id`: 인증 ID
- `user`: 사용자
- `buildingId`: 건물 ID
- `buildingName`: 건물명
- `buildingAddress`: 건물 주소
- `roomNumber`: 호수
- `status`: 인증 상태 (PENDING/APPROVED/REJECTED)
- `verificationDocument`: 인증 서류
- `adminComment`: 관리자 코멘트

## ⚙️ 자동화 기능

### 스케줄러
- **메모 만료 처리**: 1시간마다 만료된 메모를 자동으로 비활성화
- **새 질문 표시 해제**: 24시간마다 24시간 이전의 새 질문 표시를 해제

### 실시간 업데이트
- 댓글 작성 시 게시글 댓글 수 자동 증가
- 좋아요 시 게시글 좋아요 수 자동 증가
- 스크랩 시 건물 스크랩 수 자동 증가

## 🔒 보안

- JWT 토큰 기반 인증 (구현 예정)
- 비밀번호 암호화
- SQL 인젝션 방지 (JPA 사용)
- XSS 방지 (입력값 검증)

## 📈 성능 최적화

- 페이징을 통한 대용량 데이터 처리
- Lazy Loading을 통한 N+1 문제 방지
- 인덱스를 통한 검색 성능 향상
- 스케줄러를 통한 배치 처리

## 🧪 테스트

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests "MemoServiceTest"
```

## 📝 개발 로그

### v1.5.0 (현재)
- ✅ 건물 후기 시스템 고도화
  - BuildingReview 엔티티 및 서비스 구현
  - 상세 평가 항목 (소음, 안전, 편의성, 관리 등)
  - 후기 작성, 수정, 삭제, 좋아요 기능
  - 사용자별 후기 관리 및 통계
- ✅ 게시판 시스템 통합 및 최적화
  - BuildingQnA, BuildingTransfer 엔티티 삭제
  - Post 엔티티로 통합 (QUESTION, TRANSFER 게시판)
  - 건물별 질문/양도 게시글 관리
  - 게시판 타입 확장 (TRANSFER 추가)
- ✅ 건물 정보 시스템 확장
  - 주변 시설 정보 추가 (편의점, 마트, 병원 개수)
  - 건물 용도별 필터링 기능
  - 인기 건물 조회 (스크랩 수 기준)
  - 외부 API 연동 준비 (주변 시설 정보)
- ✅ API 엔드포인트 확장
  - 건물 후기 관련 API 추가
  - 건물별 질문/양도 게시글 API
  - 주변 시설 기반 검색 API
  - 건물 용도별 필터링 API

### v1.4.0
- ✅ 채팅 시스템 구현 완료
  - 1:1 실시간 채팅 기능
  - 채팅방 자동 생성/조회
  - 메시지 히스토리 관리
  - 읽음 처리 및 읽지 않은 메시지 수 표시
  - 채팅 알림 시스템
- ✅ 알림 시스템 구현 완료
  - 실시간 알림 생성 및 관리
  - 알림 타입별 분류 (댓글, 좋아요, 채팅, 거주지 인증 등)
  - 읽음 처리 및 알림 삭제 기능
  - 알림 통계 및 필터링
- ✅ 거주지 인증 시스템 구현 완료
  - 거주지 인증 신청 및 관리
  - 관리자 승인/거부 기능
  - 인증 상태 조회 및 통계
  - 건물별 인증된 사용자 관리
- ✅ 건물 정보 시스템 구현 완료
  - 건물 정보 조회 및 검색
  - 상세 필터링 (보증금, 월세, 전세, 주차장, 엘리베이터, 학교/영통역 접근성)
  - 학교/영통역까지 걸리는 시간 정보 제공
  - 건물별 상세 정보 (세대수, 난방방식, 주차대수, 승강기대수, 건축물용도 등)
- ✅ 스크랩 시스템 구현 완료
  - 건물 스크랩 기능
  - 사용자별 스크랩 목록 관리
  - 페이징 및 정렬 기능
- ✅ 캐릭터 시스템 고도화
  - 캐릭터 통계 및 분석 기능
  - 사용자별 캐릭터 보유 현황
  - 캐릭터 희귀도별 분류 및 관리

### v1.3.0
- ✅ 게시판 탭 기능 구현 완료
  - 메모 기능 (24시간 지속)
  - 동네/아파트 게시판 구분
  - 접근 권한 시스템 (거주지 인증 기반)
  - 질문 게시판 (QnA 시스템)
  - 일반 게시판 (사진 첨부, 좋아요, 저장)
  - 댓글 시스템 (대댓글 지원)
  - 검색 및 정렬 기능
  - 자동화 스케줄러
- ✅ 지도 탭 기능 구현 완료
  - 네이버 지도 API 연동
  - 건물 정보 탐색 및 검색 기능
  - 필터링 시스템 (보증금, 월세, 전세, 주차장, 엘리베이터, 학교/영통역 접근성)
  - 건물 상세 정보 (후기, Q&A, 양도)
  - 스크랩 기능
- ✅ 로그인/회원가입 기능 구현 완료
  - JWT 토큰 기반 인증
  - 회원가입/로그인 API
  - 프로필 관리 기능
  - 거주지 인증 시스템
  - 시연용 계정 자동 생성
- ✅ 마이페이지 기능 구현 완료
  - 프로필 관리 (개인정보 수정, 거주지 인증 상태)
  - 활동 내역 (게시글, 댓글, 좋아요, 북마크, 건물 스크랩)
  - 캐릭터 시스템 (포인트, 가챠, 구매, 대표 캐릭터 설정)
  - 포인트 관리 (내역 조회, 통계 확인)

### v1.2.0
- ✅ 게시판 탭 기능 구현 완료
- ✅ 지도 탭 기능 구현 완료  
- ✅ 로그인/회원가입 기능 구현 완료

### 다음 버전 예정 (v1.6.0)
- 🔄 WebSocket 기반 실시간 채팅
- 🔄 푸시 알림 시스템
- 🔄 건물 추천 알고리즘 (사용자 선호도 기반)
- 🔄 사용자 활동 분석 대시보드
- 🔄 건물 정보 CSV 데이터 자동 업데이트
- 🔄 영통역까지 걸리는 시간 자동 계산 (지도 API 연동)
- 🔄 주변 시설 정보 자동 업데이트 (외부 API 연동)
- 🔄 건물 후기 통계 및 분석 기능

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해주세요.

---

**캠퍼스 하우스** - 대학생들의 자취 생활을 더 편리하게 만들어가는 커뮤니티 플랫폼입니다. 🏠✨
