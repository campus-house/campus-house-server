package com.example.campus_house.config;

import com.example.campus_house.entity.Building;
import com.example.campus_house.entity.Character;
import com.example.campus_house.entity.Facility;
import com.example.campus_house.entity.Notification;
import com.example.campus_house.entity.User;
import com.example.campus_house.repository.BuildingRepository;
import com.example.campus_house.repository.CharacterRepository;
import com.example.campus_house.repository.FacilityRepository;
import com.example.campus_house.repository.NotificationRepository;
import com.example.campus_house.repository.UserRepository;
import com.example.campus_house.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final CharacterRepository characterRepository;
    private final NotificationRepository notificationRepository;
    private final BuildingRepository buildingRepository;
    private final FacilityRepository facilityRepository;
    private final PasswordUtil passwordUtil;
    
    @Override
    public void run(String... args) throws Exception {
        // 사용자 데이터 초기화
        initUsers();
        
        // 건물 데이터 초기화
        createSampleBuildings();
        
        // 캐릭터 데이터 초기화
        createSampleCharacters();
        
        // 알림 데이터 초기화
        createSampleNotifications();
        
        // 생활시설 데이터 초기화
        initFacilities();
    }
    
    private void initUsers() {
        // 시연용 계정이 이미 있는지 확인
        if (userRepository.count() > 0) {
            return;
        }
        
        // 시연용 계정 1: 거주지 인증 완료된 거주자
        User verifiedResident = User.builder()
                .email("resident@example.com")
                .password(passwordUtil.encodePassword("password123"))
                .nickname("기숙사생")
                .userType(User.UserType.RESIDENT)
                .isVerified(true)
                .verifiedBuildingId(1L)
                .verifiedBuildingName("캠퍼스 하우스 A동")
                .verifiedAt(java.time.LocalDateTime.now().minusDays(30))
                .location("서울대학교 기숙사")
                .university("서울대학교")
                .major("컴퓨터공학과")
                .introduction("기숙사에서 2년째 살고 있어요! 주변 맛집과 편의시설에 대해 잘 알고 있습니다.")
                .characterImage("character1.png")
                .build();
        
        userRepository.save(verifiedResident);
        
        // 시연용 계정 2: 거주지 인증 안 된 사용자
        User nonVerifiedUser = User.builder()
                .email("nonresident@example.com")
                .password(passwordUtil.encodePassword("password123"))
                .nickname("외부거주생")
                .userType(User.UserType.NON_RESIDENT)
                .isVerified(false)
                .location("서울시 관악구")
                .university("서울대학교")
                .major("경영학과")
                .introduction("올해 새내기예요! 자취방 정보가 궁금합니다.")
                .characterImage("character2.png")
                .build();
        
        userRepository.save(nonVerifiedUser);
        
        // 시연용 계정 3: 거주지 인증 완료된 거주자 2
        User verifiedResident2 = User.builder()
                .email("resident2@example.com")
                .password(passwordUtil.encodePassword("password123"))
                .nickname("선배")
                .userType(User.UserType.RESIDENT)
                .isVerified(true)
                .verifiedBuildingId(1L)
                .verifiedBuildingName("캠퍼스 하우스 A동")
                .verifiedAt(java.time.LocalDateTime.now().minusDays(15))
                .location("서울대학교 기숙사")
                .university("서울대학교")
                .major("전자공학과")
                .introduction("4학년 선배입니다. 기숙사 생활에 대한 조언을 해드릴 수 있어요!")
                .characterImage("character3.png")
                .build();
        
        userRepository.save(verifiedResident2);
        
        System.out.println("=== 시연용 계정이 생성되었습니다 ===");
        System.out.println();
        System.out.println("🏠 거주지 인증 완료된 거주자 계정");
        System.out.println("  이메일: resident@example.com");
        System.out.println("  비밀번호: password123");
        System.out.println("  닉네임: 기숙사생");
        System.out.println("  인증 상태: ✅ 거주지 인증 완료 (캠퍼스 하우스 A동)");
        System.out.println("  접근 가능: 동네 게시판, 아파트 일반 게시판, 아파트 질문 게시판");
        System.out.println();
        System.out.println("🏠 거주지 인증 완료된 거주자 계정 2");
        System.out.println("  이메일: resident2@example.com");
        System.out.println("  비밀번호: password123");
        System.out.println("  닉네임: 선배");
        System.out.println("  인증 상태: ✅ 거주지 인증 완료 (캠퍼스 하우스 A동)");
        System.out.println("  접근 가능: 동네 게시판, 아파트 일반 게시판, 아파트 질문 게시판");
        System.out.println();
        System.out.println("🏢 거주지 인증 안 된 사용자 계정");
        System.out.println("  이메일: nonresident@example.com");
        System.out.println("  비밀번호: password123");
        System.out.println("  닉네임: 외부거주생");
        System.out.println("  인증 상태: ❌ 거주지 인증 안 됨");
        System.out.println("  접근 가능: 동네 게시판, 아파트 질문 게시판 (아파트 일반 게시판 접근 불가)");
        System.out.println();
        System.out.println("📋 게시판 구조:");
        System.out.println("  - 동네 게시판: 일반 게시판만 (모든 사용자 접근 가능)");
        System.out.println("  - 아파트 게시판: 일반 게시판(거주지 인증된 사용자만) + 질문 게시판(모든 사용자)");
        System.out.println();
        System.out.println("🔐 거주지 인증 시스템:");
        System.out.println("  - 회원가입 시 모든 사용자는 기본적으로 비거주자");
        System.out.println("  - 거주지 인증 완료 시 해당 건물의 거주자로 전환");
        System.out.println("  - 거주지 인증 API: /api/verification/*");
        System.out.println("================================");
    }
    
    private void createSampleCharacters() {
        // 캐릭터가 이미 있는지 확인
        if (characterRepository.count() > 0) {
            return;
        }
        
        // 일반 등급 캐릭터들
        Character common1 = Character.builder()
                .name("기숙사 고양이")
                .description("기숙사에서 자주 보이는 귀여운 고양이")
                .imageUrl("character_common_cat.png")
                .price(50)
                .isActive(true)
                .build();
        characterRepository.save(common1);
        
        Character common2 = Character.builder()
                .name("편의점 알바생")
                .description("24시간 편의점에서 일하는 친근한 알바생")
                .imageUrl("character_common_convenience.png")
                .price(50)
                .isActive(true)
                .build();
        characterRepository.save(common2);
        
        Character common3 = Character.builder()
                .name("도서관 사서")
                .description("조용한 도서관을 지키는 사서")
                .imageUrl("character_common_librarian.png")
                .price(50)
                .isActive(true)
                .build();
        characterRepository.save(common3);
        
        // 레어 등급 캐릭터들
        Character rare1 = Character.builder()
                .name("기숙사 사감선생님")
                .description("기숙사를 관리하는 엄격하지만 따뜻한 사감선생님")
                .imageUrl("character_rare_dormitory_teacher.png")
                .price(200)
                .isActive(true)
                .build();
        characterRepository.save(rare1);
        
        Character rare2 = Character.builder()
                .name("캠퍼스 보안요원")
                .description("캠퍼스의 안전을 지키는 보안요원")
                .imageUrl("character_rare_security.png")
                .price(200)
                .isActive(true)
                .build();
        characterRepository.save(rare2);
        
        // 에픽 등급 캐릭터들
        Character epic1 = Character.builder()
                .name("기숙사 학생회장")
                .description("기숙사 학생들을 이끄는 카리스마 있는 학생회장")
                .imageUrl("character_epic_student_council.png")
                .price(500)
                .isActive(true)
                .build();
        characterRepository.save(epic1);
        
        Character epic2 = Character.builder()
                .name("캠퍼스 마스코트")
                .description("학교의 상징이 되는 귀여운 마스코트")
                .imageUrl("character_epic_mascot.png")
                .price(500)
                .isActive(true)
                .build();
        characterRepository.save(epic2);
        
        // 레전더리 등급 캐릭터들
        Character legendary1 = Character.builder()
                .name("캠퍼스의 전설")
                .description("캠퍼스에서 전설로 불리는 신비로운 존재")
                .imageUrl("character_legendary_legend.png")
                .price(1000)
                .isActive(true)
                .build();
        characterRepository.save(legendary1);
        
        Character legendary2 = Character.builder()
                .name("기숙사의 수호신")
                .description("기숙사를 수호하는 신비로운 수호신")
                .imageUrl("character_legendary_guardian.png")
                .price(1000)
                .isActive(true)
                .build();
        characterRepository.save(legendary2);
        
        System.out.println("🎮 시연용 캐릭터 데이터가 생성되었습니다!");
        System.out.println("  - 일반 등급: 3개 (50 포인트)");
        System.out.println("  - 레어 등급: 2개 (200 포인트)");
        System.out.println("  - 에픽 등급: 2개 (500 포인트)");
        System.out.println("  - 레전더리 등급: 2개 (1000 포인트)");
        System.out.println("  - 가챠 비용: 100 포인트");
    }
    
    private void createSampleNotifications() {
        // 알림이 이미 있는지 확인
        if (notificationRepository.count() > 0) {
            return;
        }
        
        // 사용자 조회
        User resident = userRepository.findByEmail("resident@example.com").orElse(null);
        User resident2 = userRepository.findByEmail("resident2@example.com").orElse(null);
        User nonResident = userRepository.findByEmail("nonresident@example.com").orElse(null);
        
        if (resident == null || resident2 == null || nonResident == null) {
            return;
        }
        
        // 시연용 알림 생성
        Notification welcomeNotification = Notification.builder()
                .user(resident)
                .type(Notification.NotificationType.WELCOME)
                .title("캠퍼스 하우스에 오신 것을 환영합니다!")
                .content("기숙사생님, 캠퍼스 하우스에 가입해주셔서 감사합니다. 다양한 기능을 이용해보세요!")
                .isRead(false)
                .build();
        notificationRepository.save(welcomeNotification);
        
        Notification pointNotification = Notification.builder()
                .user(resident)
                .type(Notification.NotificationType.POINT_EARNED)
                .title("포인트를 획득했습니다!")
                .content("+100 포인트를 획득했습니다. (회원가입 보너스)")
                .relatedId("1")
                .relatedType("POINT")
                .isRead(false)
                .build();
        notificationRepository.save(pointNotification);
        
        Notification characterNotification = Notification.builder()
                .user(resident)
                .type(Notification.NotificationType.CHARACTER_OBTAINED)
                .title("새로운 캐릭터를 획득했습니다!")
                .content("기숙사 고양이 캐릭터를 획득했습니다! (COMMON 등급)")
                .relatedId("1")
                .relatedType("CHARACTER")
                .isRead(true)
                .build();
        notificationRepository.save(characterNotification);
        
        // 비거주자에게도 환영 알림
        Notification welcomeNotification2 = Notification.builder()
                .user(nonResident)
                .type(Notification.NotificationType.WELCOME)
                .title("캠퍼스 하우스에 오신 것을 환영합니다!")
                .content("외부거주생님, 캠퍼스 하우스에 가입해주셔서 감사합니다. 다양한 기능을 이용해보세요!")
                .isRead(false)
                .build();
        notificationRepository.save(welcomeNotification2);
        
        System.out.println("🔔 시연용 알림 데이터가 생성되었습니다!");
        System.out.println("  - 환영 메시지");
        System.out.println("  - 포인트 획득 알림");
        System.out.println("  - 캐릭터 획득 알림");
    }
    
    private void createSampleBuildings() {
        // 건물이 이미 있는지 확인
        if (buildingRepository.count() > 0) {
            return;
        }
        
        // 샘플 건물 데이터 생성
        Building campusHouseA = Building.builder()
                .buildingName("캠퍼스 하우스 A동")
                .address("서울시 관악구 관악로 1")
                .latitude(37.5665)
                .longitude(126.9780)
                .deposit(new java.math.BigDecimal("1000"))
                .monthlyRent(new java.math.BigDecimal("50"))
                .jeonse(new java.math.BigDecimal("10000"))
                .households(80)
                .heatingType("개별난방")
                .parkingSpaces(20)
                .elevators(2)
                .buildingUsage("오피스텔")
                .approvalDate(java.time.LocalDateTime.now().minusYears(5))
                .completionDate(java.time.LocalDateTime.now().minusYears(5))
                .nearbyConvenienceStores(5)
                .nearbyMarts(2)
                .nearbyHospitals(1)
                .schoolWalkingTime(15)
                .stationWalkingTime(8)
                .scrapCount(0)
                .floorsGround(15)
                .area(42.7)
                .constructionYear(2019)
                .roadName("관악로")
                .sampleCount(6)
                .avgPrice(new java.math.BigDecimal("2000"))
                .build();
        
        buildingRepository.save(campusHouseA);
        
        Building campusHouseB = Building.builder()
                .buildingName("캠퍼스 하우스 B동")
                .address("서울시 관악구 관악로 2")
                .latitude(37.5666)
                .longitude(126.9781)
                .deposit(new java.math.BigDecimal("1500"))
                .monthlyRent(new java.math.BigDecimal("38"))
                .jeonse(new java.math.BigDecimal("15000"))
                .households(30)
                .heatingType("중앙난방")
                .parkingSpaces(15)
                .elevators(0)
                .buildingUsage("아파트")
                .approvalDate(java.time.LocalDateTime.now().minusYears(3))
                .completionDate(java.time.LocalDateTime.now().minusYears(3))
                .nearbyConvenienceStores(3)
                .nearbyMarts(1)
                .nearbyHospitals(1)
                .schoolWalkingTime(12)
                .stationWalkingTime(6)
                .scrapCount(0)
                .floorsGround(12)
                .area(38.5)
                .constructionYear(2021)
                .roadName("관악로")
                .sampleCount(3)
                .avgPrice(new java.math.BigDecimal("1500"))
                .build();
        
        buildingRepository.save(campusHouseB);
        
        Building hiVille = Building.builder()
                .buildingName("하이빌 영통")
                .address("경기 수원시 영통구 영통동 1012-1")
                .latitude(37.2636)
                .longitude(127.0286)
                .deposit(new java.math.BigDecimal("2300"))
                .monthlyRent(new java.math.BigDecimal("50"))
                .jeonse(new java.math.BigDecimal("23000"))
                .households(250)
                .heatingType("개별난방")
                .parkingSpaces(50)
                .elevators(4)
                .buildingUsage("오피스텔")
                .approvalDate(java.time.LocalDateTime.now().minusYears(2))
                .completionDate(java.time.LocalDateTime.now().minusYears(2))
                .nearbyConvenienceStores(8)
                .nearbyMarts(3)
                .nearbyHospitals(2)
                .schoolWalkingTime(5)
                .stationWalkingTime(3)
                .scrapCount(0)
                .floorsGround(20)
                .area(50.1)
                .constructionYear(2022)
                .roadName("영통로")
                .sampleCount(12)
                .avgPrice(new java.math.BigDecimal("2300"))
                .build();
        
        buildingRepository.save(hiVille);
        
        System.out.println("🏢 시연용 건물 데이터가 생성되었습니다!");
        System.out.println("  - 캠퍼스 하우스 A동 (오피스텔)");
        System.out.println("  - 캠퍼스 하우스 B동 (아파트)");
        System.out.println("  - 하이빌 영통 (오피스텔)");
        System.out.println("  - 총 3개 건물 데이터 생성 완료");
    }
    
    private void initFacilities() {
        // 병원 데이터가 없으면 초기화
        if (facilityRepository.count() == 0) {
            System.out.println("🏥 생활시설 데이터 초기화 시작");
            
            // 더웰병원
            Facility hospital1 = Facility.builder()
                .businessName("더웰병원")
                .address("경기도 수원시 영통구 영통동 996-3번지 대우월드마크영통 3,5,6,7,8층")
                .roadAddress("경기도 수원시 영통구 봉영로 1620 (영통동, 대우월드마크영통 3,5,6,7,8층)")
                .businessStatus("영업/정상")
                .category(Facility.Category.HOSPITAL.name())
                .subCategory("소아과")
                .latitude(37.2550152411)
                .longitude(127.0756344537)
                .description("내과, 정신건강의학과, 성형외과, 마취통증의학과, 소아청소년과, 이비인후과, 피부과, 영상의학과, 가정의학과")
                .build();
            
            // 베데스다병원
            Facility hospital2 = Facility.builder()
                .businessName("베데스다병원")
                .address("경기도 수원시 영통구 영통동 958-1 드림피아빌딩")
                .roadAddress("경기도 수원시 영통구 봉영로 1623, 드림피아빌딩 6층일부,7,8,9층 (영통동)")
                .businessStatus("영업/정상")
                .category(Facility.Category.HOSPITAL.name())
                .subCategory("한의원")
                .latitude(37.2559223973)
                .longitude(127.0747272211)
                .description("내과, 피부과, 재활의학과, 가정의학과, 한방내과, 한방부인과, 한방소아과, 한방안·이비인후·피부과, 한방재활의학과, 침구과")
                .build();
            
            facilityRepository.save(hospital1);
            facilityRepository.save(hospital2);
            
            // 편의점 데이터 추가 (샘플 5개)
            Facility convenience1 = Facility.builder()
                .businessName("세븐일레븐 영통경희대점")
                .address("경기도 수원시 영통구 영통동 996-3")
                .roadAddress("경기도 수원시 영통구 봉영로 1620")
                .businessStatus("영업/정상")
                .category(Facility.Category.CONVENIENCE_STORE.name())
                .subCategory("세븐일레븐")
                .latitude(37.2550152411)
                .longitude(127.0756344537)
                .description("편의점")
                .build();
            
            Facility convenience2 = Facility.builder()
                .businessName("씨유 영통중앙점")
                .address("경기도 수원시 영통구 영통동 958-1")
                .roadAddress("경기도 수원시 영통구 봉영로 1623")
                .businessStatus("영업/정상")
                .category(Facility.Category.CONVENIENCE_STORE.name())
                .subCategory("CU")
                .latitude(37.2559223973)
                .longitude(127.0747272211)
                .description("편의점")
                .build();
            
            Facility convenience3 = Facility.builder()
                .businessName("GS25 영통럭키점")
                .address("경기도 수원시 영통구 영통동 1000-1")
                .roadAddress("경기도 수원시 영통구 봉영로 1625")
                .businessStatus("영업/정상")
                .category(Facility.Category.CONVENIENCE_STORE.name())
                .subCategory("GS25")
                .latitude(37.2560152411)
                .longitude(127.0766344537)
                .description("편의점")
                .build();
            
            Facility convenience4 = Facility.builder()
                .businessName("세븐일레븐 용인서천본점")
                .address("경기도 용인시 기흥구 서천동 123-4")
                .roadAddress("경기도 용인시 기흥구 서천로 123")
                .businessStatus("영업/정상")
                .category(Facility.Category.CONVENIENCE_STORE.name())
                .subCategory("세븐일레븐")
                .latitude(37.2636)
                .longitude(127.0286)
                .description("편의점")
                .build();
            
            Facility convenience5 = Facility.builder()
                .businessName("씨유서천파크원점")
                .address("경기도 용인시 기흥구 서천동 456-7")
                .roadAddress("경기도 용인시 기흥구 서천로 456")
                .businessStatus("영업/정상")
                .category(Facility.Category.CONVENIENCE_STORE.name())
                .subCategory("CU")
                .latitude(37.2646)
                .longitude(127.0296)
                .description("편의점")
                .build();
            
            facilityRepository.save(convenience1);
            facilityRepository.save(convenience2);
            facilityRepository.save(convenience3);
            facilityRepository.save(convenience4);
            facilityRepository.save(convenience5);
            
            System.out.println("🏥 생활시설 데이터 초기화 완료: 2개 병원, 5개 편의점 저장");
        } else {
            // 편의점 데이터가 없으면 추가
            long convenienceStoreCount = facilityRepository.countByCategory(Facility.Category.CONVENIENCE_STORE.name());
            if (convenienceStoreCount == 0) {
                System.out.println("🏪 편의점 데이터 추가 시작");
                
                // 편의점 데이터 추가 (샘플 5개)
                Facility convenience1 = Facility.builder()
                    .businessName("세븐일레븐 영통경희대점")
                    .address("경기도 수원시 영통구 영통동 996-3")
                    .roadAddress("경기도 수원시 영통구 봉영로 1620")
                    .businessStatus("영업/정상")
                    .category(Facility.Category.CONVENIENCE_STORE.name())
                    .subCategory("세븐일레븐")
                    .latitude(37.2550152411)
                    .longitude(127.0756344537)
                    .description("편의점")
                    .build();
                
                Facility convenience2 = Facility.builder()
                    .businessName("씨유 영통중앙점")
                    .address("경기도 수원시 영통구 영통동 958-1")
                    .roadAddress("경기도 수원시 영통구 봉영로 1623")
                    .businessStatus("영업/정상")
                    .category(Facility.Category.CONVENIENCE_STORE.name())
                    .subCategory("CU")
                    .latitude(37.2559223973)
                    .longitude(127.0747272211)
                    .description("편의점")
                    .build();
                
                Facility convenience3 = Facility.builder()
                    .businessName("GS25 영통럭키점")
                    .address("경기도 수원시 영통구 영통동 1000-1")
                    .roadAddress("경기도 수원시 영통구 봉영로 1625")
                    .businessStatus("영업/정상")
                    .category(Facility.Category.CONVENIENCE_STORE.name())
                    .subCategory("GS25")
                    .latitude(37.2560152411)
                    .longitude(127.0766344537)
                    .description("편의점")
                    .build();
                
                Facility convenience4 = Facility.builder()
                    .businessName("세븐일레븐 용인서천본점")
                    .address("경기도 용인시 기흥구 서천동 123-4")
                    .roadAddress("경기도 용인시 기흥구 서천로 123")
                    .businessStatus("영업/정상")
                    .category(Facility.Category.CONVENIENCE_STORE.name())
                    .subCategory("세븐일레븐")
                    .latitude(37.2636)
                    .longitude(127.0286)
                    .description("편의점")
                    .build();
                
                Facility convenience5 = Facility.builder()
                    .businessName("씨유서천파크원점")
                    .address("경기도 용인시 기흥구 서천동 456-7")
                    .roadAddress("경기도 용인시 기흥구 서천로 456")
                    .businessStatus("영업/정상")
                    .category(Facility.Category.CONVENIENCE_STORE.name())
                    .subCategory("CU")
                    .latitude(37.2646)
                    .longitude(127.0296)
                    .description("편의점")
                    .build();
                
                facilityRepository.save(convenience1);
                facilityRepository.save(convenience2);
                facilityRepository.save(convenience3);
                facilityRepository.save(convenience4);
                facilityRepository.save(convenience5);
                
                System.out.println("🏪 편의점 데이터 추가 완료: 5개 편의점 저장");
            }
        }
    }
}
