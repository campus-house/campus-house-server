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
        // createSampleBuildings(); // 실제 건물 데이터는 별도 스크립트로 로드
        System.out.println("🏢 건물 데이터는 별도 스크립트를 통해 로드됩니다.");
        System.out.println("   - 건물: data/buildings/processed/buildings_processed.json (1,804개 건물)");
        
        // 캐릭터 데이터 초기화
        createSampleCharacters();
        
        // 알림 데이터 초기화
        createSampleNotifications();
        
        // 생활시설 데이터 초기화
        // initFacilities(); // 실제 생활시설 데이터는 별도 스크립트로 로드
    }
    
    private void initUsers() {
        // 시연용 계정이 이미 있는지 확인
        if (userRepository.count() > 0) {
            return;
        }
        
        // 시연용 계정 1: 거주지 인증 완료된 거주자
        User verifiedResident = User.builder()
                .email("resident@example.com")
                .username("resident")
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
                .username("nonresident")
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
                .username("resident2")
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
        
        // 시연용 계정 4: 방미오 계정
        User bangmio = User.builder()
                .email("bangmiooo1@khu.ac.kr")
                .username("miomio")
                .password(passwordUtil.encodePassword("miomio"))
                .nickname("방미오")
                .userType(User.UserType.RESIDENT)
                .isVerified(true)
                .verifiedBuildingId(48L)
                .verifiedBuildingName("영통역아이파크")
                .verifiedAt(java.time.LocalDateTime.now())
                .location("경기도 수원시")
                .university("경희대학교")
                .major("컴퓨터공학과")
                .introduction("이번에 이사 온 미오라고 해요!! ^-^")
                .rewards(2000)
                .build();
        
        userRepository.save(bangmio);
        
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
        System.out.println("🌟 방미오 계정");
        System.out.println("  이메일: bangmiooo1@khu.ac.kr");
        System.out.println("  아이디: miomio");
        System.out.println("  비밀번호: miomio");
        System.out.println("  닉네임: 방미오");
        System.out.println("  인증 상태: ✅ 거주지 인증 완료 (영통역아이파크)");
        System.out.println("  접근 가능: 동네 게시판, 아파트 일반 게시판, 아파트 질문 게시판");
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
    
    // 실제 건물 데이터는 data/buildings/processed/buildings_processed.json 파일을 통해 로드됩니다.
    // 시연용 건물 데이터는 생성하지 않습니다.
    
    private void initFacilities() {
        // 실제 생활시설 데이터는 data/facilities/processed/ 폴더의 JSON/CSV 파일을 통해 로드됩니다.
        // 시연용 데이터는 생성하지 않습니다.
        System.out.println("📁 생활시설 데이터는 별도 스크립트를 통해 로드됩니다.");
        System.out.println("   - 편의점: data/facilities/processed/convenience_stores_processed.json");
        System.out.println("   - 병원: data/facilities/processed/hospitals_processed.json");
        System.out.println("   - 마트: data/facilities/processed/marts_processed.json");
    }
}
