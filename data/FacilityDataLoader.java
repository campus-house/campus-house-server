package com.example.campus_house.data;

import com.example.campus_house.entity.Facility;
import com.example.campus_house.repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 주변시설 데이터를 데이터베이스에 로드하는 컴포넌트
 * 정제된 CSV 파일들을 읽어서 Facility 테이블에 삽입
 */
@Component
public class FacilityDataLoader implements CommandLineRunner {

    @Autowired
    private FacilityRepository facilityRepository;

    @Override
    public void run(String... args) throws Exception {
        // 기존 데이터가 있는지 확인
        if (facilityRepository.count() > 0) {
            System.out.println("📊 기존 주변시설 데이터가 있습니다. 건너뜁니다.");
            return;
        }

        System.out.println("🚀 주변시설 데이터 로딩을 시작합니다...");
        
        // 병원 데이터 로드
        loadHospitals();
        
        // 편의점 데이터 로드
        loadConvenienceStores();
        
        // 마트 데이터 로드
        loadMarts();
        
        System.out.println("✅ 주변시설 데이터 로딩이 완료되었습니다!");
        System.out.println("📊 총 " + facilityRepository.count() + "개의 주변시설이 등록되었습니다.");
        
        // 카테고리별 개수 출력
        System.out.println("📊 카테고리별 개수:");
        System.out.println("  - 병원: " + facilityRepository.countByCategory("HOSPITAL"));
        System.out.println("  - 편의점: " + facilityRepository.countByCategory("CONVENIENCE_STORE"));
        System.out.println("  - 마트: " + facilityRepository.countByCategory("MART"));
    }

    private void loadHospitals() {
        System.out.println("🏥 병원 데이터 로딩 중...");
        List<Facility> hospitals = loadFacilitiesFromCsv("data/facilities/processed/hospitals_processed.csv", "HOSPITAL");
        facilityRepository.saveAll(hospitals);
        System.out.println("✅ " + hospitals.size() + "개의 병원 데이터가 로드되었습니다.");
    }

    private void loadConvenienceStores() {
        System.out.println("🏪 편의점 데이터 로딩 중...");
        List<Facility> convenienceStores = loadFacilitiesFromCsv("data/facilities/processed/convenience_stores_processed.csv", "CONVENIENCE_STORE");
        facilityRepository.saveAll(convenienceStores);
        System.out.println("✅ " + convenienceStores.size() + "개의 편의점 데이터가 로드되었습니다.");
    }

    private void loadMarts() {
        System.out.println("🛒 마트 데이터 로딩 중...");
        List<Facility> marts = loadFacilitiesFromCsv("data/facilities/processed/marts_processed.csv", "MART");
        if (marts.isEmpty()) {
            System.out.println("❌ 마트 데이터가 로드되지 않았습니다. 파일을 확인해주세요.");
            return;
        }
        facilityRepository.saveAll(marts);
        System.out.println("✅ " + marts.size() + "개의 마트 데이터가 로드되었습니다.");
    }

    private List<Facility> loadFacilitiesFromCsv(String filePath, String category) {
        List<Facility> facilities = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] headers = null;
            boolean isFirstLine = true;
            int lineCount = 0;
            
            while ((line = br.readLine()) != null) {
                lineCount++;
                if (isFirstLine) {
                    headers = line.split(",");
                    System.out.println("📋 헤더: " + String.join(", ", headers));
                    isFirstLine = false;
                    continue;
                }
                
                String[] values = parseCsvLine(line);
                System.out.println("📄 라인 " + lineCount + ": " + values.length + "개 컬럼");
                if (values.length >= headers.length) {
                    Facility facility = createFacilityFromCsvRow(headers, values, category);
                    if (facility != null) {
                        facilities.add(facility);
                        System.out.println("  ✅ 추가: " + facility.getBusinessName());
                    } else {
                        System.out.println("  ❌ 건너뜀: " + (values.length > 0 ? values[0] : "알 수 없음"));
                    }
                } else {
                    System.out.println("  ❌ 컬럼 수 부족: " + values.length + " < " + headers.length);
                }
            }
            System.out.println("📊 총 " + lineCount + "줄 처리, " + facilities.size() + "개 시설 추가");
        } catch (IOException e) {
            System.err.println("❌ 파일 읽기 오류: " + filePath + " - " + e.getMessage());
        }
        
        return facilities;
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        result.add(current.toString().trim());
        return result.toArray(new String[0]);
    }

    private Facility createFacilityFromCsvRow(String[] headers, String[] values, String category) {
        try {
            String businessName = getValueByHeader(headers, values, "사업장명");
            String address = getValueByHeader(headers, values, "소재지지번주소");
            String roadAddress = getValueByHeader(headers, values, "소재지도로명주소");
            String businessStatus = getValueByHeader(headers, values, "영업상태명");
            
            // 영업상태명 정규화
            if ("정상영업".equals(businessStatus)) {
                businessStatus = "영업/정상";
            }
            String latitudeStr = getValueByHeader(headers, values, "WGS84위도");
            String longitudeStr = getValueByHeader(headers, values, "WGS84경도");
            
            // 위도/경도가 유효한 경우만 처리
            double latitude = parseDouble(latitudeStr);
            double longitude = parseDouble(longitudeStr);
            
            if (latitude == 0.0 || longitude == 0.0) {
                return null;
            }
            
            String subCategory = determineSubCategory(headers, values, category);
            String description = generateDescription(headers, values, category);
            
            return Facility.builder()
                    .businessName(businessName)
                    .address(address)
                    .roadAddress(roadAddress)
                    .businessStatus(businessStatus)
                    .category(category)
                    .subCategory(subCategory)
                    .latitude(latitude)
                    .longitude(longitude)
                    .phoneNumber("")
                    .businessHours("")
                    .description(description)
                    .build();
                    
        } catch (Exception e) {
            System.err.println("❌ 데이터 변환 오류: " + e.getMessage());
            return null;
        }
    }

    private String getValueByHeader(String[] headers, String[] values, String headerName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(headerName) && i < values.length) {
                return values[i];
            }
        }
        return "";
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private String determineSubCategory(String[] headers, String[] values, String category) {
        if ("HOSPITAL".equals(category)) {
            String medicalType = getValueByHeader(headers, values, "의료기관종별명");
            String departments = getValueByHeader(headers, values, "진료과목내용");
            
            if (medicalType.contains("종합병원") || medicalType.contains("대학병원")) {
                return "종합병원";
            } else if (medicalType.contains("치과") || departments.contains("치과")) {
                return "치과";
            } else if (medicalType.contains("한의원") || departments.contains("한방")) {
                return "한의원";
            } else if (departments.contains("산부인과")) {
                return "산부인과";
            } else if (departments.contains("소아")) {
                return "소아과";
            } else {
                return "의원";
            }
        } else if ("CONVENIENCE_STORE".equals(category)) {
            String businessName = getValueByHeader(headers, values, "사업장명").toUpperCase();
            if (businessName.contains("CU") || businessName.contains("씨유")) {
                return "CU";
            } else if (businessName.contains("GS25") || businessName.contains("지에스")) {
                return "GS25";
            } else if (businessName.contains("세븐일레븐") || businessName.contains("7-ELEVEN")) {
                return "세븐일레븐";
            } else if (businessName.contains("미니스톱") || businessName.contains("MINISTOP")) {
                return "미니스톱";
            } else if (businessName.contains("이마트24") || businessName.contains("EMART24")) {
                return "이마트24";
            } else {
                return "편의점";
            }
        } else if ("MART".equals(category)) {
            String businessName = getValueByHeader(headers, values, "사업장명").toUpperCase();
            String businessType = getValueByHeader(headers, values, "업태구분명정보");
            
            if (businessName.contains("이마트")) {
                return "이마트";
            } else if (businessName.contains("롯데마트") || businessName.contains("롯데몰")) {
                return "롯데마트";
            } else if (businessName.contains("홈플러스")) {
                return "홈플러스";
            } else if (businessName.contains("코스트코") || businessName.contains("COSTCO")) {
                return "코스트코";
            } else if (businessName.contains("트레이더스")) {
                return "트레이더스";
            } else if (businessType.contains("백화점")) {
                return "백화점";
            } else if (businessType.contains("쇼핑센터")) {
                return "쇼핑센터";
            } else if (businessType.contains("시장")) {
                return "시장";
            } else {
                return "대형마트";
            }
        }
        
        return "기타";
    }

    private String generateDescription(String[] headers, String[] values, String category) {
        if ("HOSPITAL".equals(category)) {
            String departments = getValueByHeader(headers, values, "진료과목내용");
            if (!departments.isEmpty()) {
                return "진료과목: " + departments;
            }
        } else if ("MART".equals(category)) {
            String businessType = getValueByHeader(headers, values, "업태구분명정보");
            if (!businessType.isEmpty()) {
                return "업태: " + businessType;
            }
        }
        
        return "";
    }
}
