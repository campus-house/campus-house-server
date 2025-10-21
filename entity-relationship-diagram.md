# 캠퍼스 하우스 시스템 - 엔티티 관계도

## ERD (Entity Relationship Diagram)

```mermaid
erDiagram
    User {
        bigint id PK
        varchar email UK
        varchar password
        varchar nickname
        varchar profileImage
        varchar characterImage
        varchar userType
        boolean isVerified
        bigint verifiedBuildingId
        varchar verifiedBuildingName
        datetime verifiedAt
        int points
        bigint mainCharacterId
        varchar location
        varchar university
        varchar major
        text introduction
        varchar status
        datetime createdAt
        datetime updatedAt
    }
    
    Post {
        bigint id PK
        varchar title
        text content
        varchar imageUrl
        varchar boardType
        bigint authorId FK
        int likeCount
        int bookmarkCount
        int commentCount
        int viewCount
        int scrapCount
        datetime createdAt
        datetime updatedAt
    }
    
    Comment {
        bigint id PK
        text content
        bigint postId FK
        bigint authorId FK
        datetime createdAt
        datetime updatedAt
    }
    
    Like {
        bigint id PK
        bigint postId FK
        bigint userId FK
        datetime createdAt
    }
    
    Bookmark {
        bigint id PK
        bigint postId FK
        bigint userId FK
        datetime createdAt
    }
    
    Memo {
        bigint id PK
        text content
        varchar imageUrl
        varchar type
        varchar location
        int maxParticipants
        int currentParticipants
        varchar contactInfo
        datetime deadline
        varchar status
        bigint userId FK
        datetime createdAt
        datetime expiresAt
    }
    
    Building {
        bigint id PK
        varchar buildingName
        varchar address
        varchar detailAddress
        double latitude
        double longitude
        int households
        int elevators
        varchar heatingType
        decimal deposit
        decimal jeonse
        decimal monthlyRent
        decimal managementFee
        datetime createdAt
        datetime updatedAt
    }
    
    Property {
        bigint id PK
        varchar title
        text description
        varchar propertyType
        varchar floorType
        int floor
        decimal deposit
        decimal monthlyRent
        decimal managementFee
        varchar address
        varchar contactInfo
        varchar agentName
        varchar agentPhone
        varchar status
        int viewCount
        int scrapCount
        double latitude
        double longitude
        bigint buildingId FK
        datetime createdAt
        datetime updatedAt
    }
    
    Character {
        bigint id PK
        varchar name
        text description
        varchar imageUrl
        varchar rarity
        int price
        boolean isActive
        datetime createdAt
        datetime updatedAt
    }
    
    UserCharacter {
        bigint id PK
        bigint userId FK
        bigint characterId FK
        boolean isMain
        int quantity
        datetime obtainedAt
    }
    
    ChatRoom {
        bigint id PK
        bigint user1Id FK
        bigint user2Id FK
        datetime user1LastReadAt
        datetime user2LastReadAt
        datetime createdAt
        datetime updatedAt
    }
    
    ChatMessage {
        bigint id PK
        text content
        bigint chatRoomId FK
        bigint senderId FK
        datetime createdAt
    }
    
    PointHistory {
        bigint id PK
        int points
        varchar reason
        bigint userId FK
        datetime createdAt
    }
    
    User ||--o{ Post : "writes"
    User ||--o{ Comment : "writes"
    User ||--o{ Like : "likes"
    User ||--o{ Bookmark : "bookmarks"
    User ||--o{ Memo : "creates"
    User ||--o{ UserCharacter : "owns"
    User ||--o{ PointHistory : "has"
    User ||--o{ ChatRoom : "participates"
    User ||--o{ ChatMessage : "sends"
    
    Post ||--o{ Comment : "has"
    Post ||--o{ Like : "receives"
    Post ||--o{ Bookmark : "bookmarked"
    
    Building ||--o{ Property : "contains"
    
    Character ||--o{ UserCharacter : "owned_by"
    
    ChatRoom ||--o{ ChatMessage : "contains"
```

## 시스템 아키텍처 다이어그램

```mermaid
graph TB
    subgraph "Frontend"
        A[웹 애플리케이션]
        B[모바일 앱]
    end
    
    subgraph "Backend API"
        C[Spring Boot Server]
        D[Authentication]
        E[Business Logic]
        F[Data Access]
    end
    
    subgraph "Database"
        G[(MySQL Database)]
    end
    
    subgraph "External Services"
        H[Naver Maps API]
        I[File Storage]
    end
    
    A --> C
    B --> C
    C --> D
    C --> E
    C --> F
    F --> G
    E --> H
    E --> I
```

## API 엔드포인트 구조

```mermaid
graph TD
    A[캠퍼스 하우스 API] --> B[인증 시스템]
    A --> C[게시판 시스템]
    A --> D[메모 시스템]
    A --> E[매물 시스템]
    A --> F[건물 시스템]
    A --> G[채팅 시스템]
    A --> H[캐릭터 시스템]
    
    B --> B1[회원가입/로그인]
    B --> B2[토큰 검증]
    B --> B3[프로필 관리]
    
    C --> C1[게시글 CRUD]
    C --> C2[좋아요/북마크]
    C --> C3[검색 기능]
    
    D --> D1[메모 생성/조회]
    D --> D2[참여/답장]
    D --> D3[타입별 필터링]
    
    E --> E1[매물 등록/조회]
    E --> E2[검색/필터링]
    E --> E3[스크랩 기능]
    
    F --> F1[건물 정보 조회]
    F --> F2[위치 기반 검색]
    
    G --> G1[채팅방 생성]
    G --> G2[메시지 송수신]
    G --> G3[읽음 상태 관리]
    
    H --> H1[캐릭터 조회]
    H --> H2[희귀도별 분류]
    H --> H3[사용자 보유 캐릭터]
```

## 간단한 엔티티 관계도

```mermaid
graph LR
    User[👤 User] --> Post[📝 Post]
    User --> Comment[💬 Comment]
    User --> Memo[📋 Memo]
    User --> ChatRoom[💬 ChatRoom]
    
    Post --> Comment
    Post --> Like[❤️ Like]
    Post --> Bookmark[🔖 Bookmark]
    
    Building[🏢 Building] --> Property[🏠 Property]
    Property --> PropertyReview[⭐ Review]
    Property --> PropertyQnA[❓ Q&A]
    
    Character[🎮 Character] --> UserCharacter[👤🎮 UserCharacter]
    User --> UserCharacter
    
    ChatRoom --> ChatMessage[💬 Message]
    User --> ChatMessage
```

## 데이터베이스 테이블 구조

### 주요 테이블
- **users**: 사용자 정보
- **posts**: 게시글
- **comments**: 댓글
- **likes**: 좋아요
- **bookmarks**: 북마크
- **memos**: 메모
- **buildings**: 건물 정보
- **properties**: 매물 정보
- **characters**: 캐릭터 정보
- **user_characters**: 사용자 캐릭터 보유
- **chat_rooms**: 채팅방
- **chat_messages**: 채팅 메시지
- **point_histories**: 포인트 내역

### 인덱스 최적화
- **users.email**: UNIQUE INDEX
- **users.nickname**: UNIQUE INDEX
- **posts.author_id**: INDEX
- **posts.board_type**: INDEX
- **properties.building_id**: INDEX
- **properties.latitude, longitude**: SPATIAL INDEX
- **chat_rooms.user1_id, user2_id**: INDEX
