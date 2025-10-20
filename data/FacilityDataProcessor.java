package com.example.campus_house.data;

import com.example.campus_house.entity.Facility;
import com.example.campus_house.repository.FacilityRepository;
import com.example.campus_house.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 생활시설 데이터 처리 클래스
 * CSV 파일에서 생활시설 데이터를 읽어와서 데이터베이스에 저장합니다.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FacilityDataProcessor {
    
    private final FacilityRepository facilityRepository;
    private final DistanceCalculator distanceCalculator;
    
    // 카테고리 매핑 패턴
    private static final Pattern CONVENIENCE_STORE_PATTERN = Pattern.compile(
        "편의점|CU|GS25|세븐일레븐|7-ELEVEN|미니스톱|MINISTOP|이마트24|EMART24", 
        Pattern.CASE_INSENSITIVE);
    
    private static final Pattern MART_PATTERN = Pattern.compile(
        "마트|슈퍼마켓|이마트|롯데마트|홈플러스|코스트코|메가마트|하이마트|한마트", 
        Pattern.CASE_INSENSITIVE);
    
    private static final Pattern HOSPITAL_PATTERN = Pattern.compile(
        "병원|의원|클리닉|의료원|종합병원|대학병원|치과|한의원|산부인과|소아과|내과|외과", 
        Pattern.CASE_INSENSITIVE);
    
    /**
     * 생활시설 CSV 파일을 처리합니다.
     * 
     * @param filePath CSV 파일 경로
     * @param hasHeader 헤더가 있는지 여부
     */
    public void processFacilityCsvFile(String filePath, boolean hasHeader) {
        log.info("생활시설 데이터 처리 시작: {}", filePath);
        
        List<Facility> facilities = new ArrayList<>();
        int processedCount = 0;
        int errorCount = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // 헤더 스킵
                if (hasHeader && isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                try {
                    Facility facility = parseFacilityFromCsvLine(line);
                    if (facility != null) {
                        facilities.add(facility);
                        processedCount++;
                    }
                } catch (Exception e) {
                    log.warn("라인 처리 중 오류 발생: {} - {}", line, e.getMessage());
                    errorCount++;
                }
            }
            
            // 배치로 저장
            if (!facilities.isEmpty()) {
                facilityRepository.saveAll(facilities);
                log.info("생활시설 데이터 저장 완료: {}개 저장됨", facilities.size());
            }
            
        } catch (IOException e) {
            log.error("파일 읽기 중 오류 발생: {}", filePath, e);
            throw new RuntimeException("파일 읽기 중 오류가 발생했습니다.", e);
        }
        
        log.info("생활시설 데이터 처리 완료 - 처리됨: {}, 오류: {}", processedCount, errorCount);
    }
    
    /**
     * CSV 라인에서 생활시설 정보를 파싱합니다.
     * 
     * @param line CSV 라인
     * @return 파싱된 Facility 객체
     */
    private Facility parseFacilityFromCsvLine(String line) {
        String[] columns = parseCsvLine(line);
        
        if (columns.length < 3) {
            log.warn("컬럼 수가 부족합니다: {}", line);
            return null;
        }
        
        try {
            String businessName = columns[0].trim();
            String address = columns[1].trim();
            String businessStatus = columns[2].trim();
            
            // 필수 필드 검증
            if (businessName.isEmpty() || address.isEmpty()) {
                log.warn("필수 필드가 비어있습니다: {}", line);
                return null;
            }
            
            // 영업상태가 영업중이 아닌 경우 스킵
            if (!isOperatingStatus(businessStatus)) {
                log.debug("영업중이 아닌 시설 스킵: {} - {}", businessName, businessStatus);
                return null;
            }
            
            // 카테고리 결정
            Facility.Category category = determineCategory(businessName);
            if (category == null) {
                log.debug("카테고리를 결정할 수 없는 시설 스킵: {}", businessName);
                return null;
            }
            
            // 위도/경도 추출 (주소에서 추출하거나 기본값 사용)
            double[] coordinates = extractCoordinatesFromAddress(address);
            
            return Facility.builder()
                    .businessName(businessName)
                    .address(address)
                    .businessStatus(businessStatus)
                    .category(category.name())
                    .subCategory(determineSubCategory(businessName, category))
                    .latitude(coordinates[0])
                    .longitude(coordinates[1])
                    .build();
                    
        } catch (Exception e) {
            log.warn("라인 파싱 중 오류 발생: {} - {}", line, e.getMessage());
            return null;
        }
    }
    
    /**
     * CSV 라인을 파싱합니다.
     * 
     * @param line CSV 라인
     * @return 파싱된 컬럼 배열
     */
    private String[] parseCsvLine(String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder currentColumn = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                columns.add(currentColumn.toString().trim());
                currentColumn = new StringBuilder();
            } else {
                currentColumn.append(c);
            }
        }
        
        columns.add(currentColumn.toString().trim());
        return columns.toArray(new String[0]);
    }
    
    /**
     * 영업상태가 영업중인지 확인합니다.
     * 
     * @param businessStatus 영업상태
     * @return 영업중이면 true
     */
    private boolean isOperatingStatus(String businessStatus) {
        if (businessStatus == null || businessStatus.isEmpty()) {
            return false;
        }
        
        String status = businessStatus.toLowerCase().trim();
        return status.contains("영업") || status.contains("운영") || status.contains("정상");
    }
    
    /**
     * 사업장명을 기반으로 카테고리를 결정합니다.
     * 
     * @param businessName 사업장명
     * @return 카테고리
     */
    private Facility.Category determineCategory(String businessName) {
        if (CONVENIENCE_STORE_PATTERN.matcher(businessName).find()) {
            return Facility.Category.CONVENIENCE_STORE;
        } else if (MART_PATTERN.matcher(businessName).find()) {
            return Facility.Category.MART;
        } else if (HOSPITAL_PATTERN.matcher(businessName).find()) {
            return Facility.Category.HOSPITAL;
        }
        
        return null;
    }
    
    /**
     * 세부 카테고리를 결정합니다.
     * 
     * @param businessName 사업장명
     * @param category 카테고리
     * @return 세부 카테고리
     */
    private String determineSubCategory(String businessName, Facility.Category category) {
        switch (category) {
            case CONVENIENCE_STORE:
                if (businessName.contains("CU")) return "CU";
                if (businessName.contains("GS25")) return "GS25";
                if (businessName.contains("세븐일레븐") || businessName.contains("7-ELEVEN")) return "세븐일레븐";
                if (businessName.contains("미니스톱") || businessName.contains("MINISTOP")) return "미니스톱";
                if (businessName.contains("이마트24") || businessName.contains("EMART24")) return "이마트24";
                return "편의점";
                
            case MART:
                if (businessName.contains("이마트")) return "이마트";
                if (businessName.contains("롯데마트")) return "롯데마트";
                if (businessName.contains("홈플러스")) return "홈플러스";
                if (businessName.contains("코스트코")) return "코스트코";
                if (businessName.contains("메가마트")) return "메가마트";
                if (businessName.contains("하이마트")) return "하이마트";
                return "마트";
                
            case HOSPITAL:
                if (businessName.contains("종합병원")) return "종합병원";
                if (businessName.contains("대학병원")) return "대학병원";
                if (businessName.contains("치과")) return "치과";
                if (businessName.contains("한의원")) return "한의원";
                if (businessName.contains("산부인과")) return "산부인과";
                if (businessName.contains("소아과")) return "소아과";
                if (businessName.contains("내과")) return "내과";
                if (businessName.contains("외과")) return "외과";
                return "병원";
                
            default:
                return "기타";
        }
    }
    
    /**
     * 주소에서 위도/경도를 추출합니다.
     * 실제 구현에서는 주소를 위도/경도로 변환하는 API를 사용해야 합니다.
     * 
     * @param address 주소
     * @return [위도, 경도] 배열
     */
    private double[] extractCoordinatesFromAddress(String address) {
        // TODO: 실제 구현에서는 주소를 위도/경도로 변환하는 API 사용
        // 현재는 기본값 반환 (수원시 영통구 기준)
        return new double[]{37.2636, 127.0286};
    }
    
    /**
     * 모든 생활시설 데이터를 처리합니다.
     * 
     * @param directoryPath 데이터 디렉토리 경로
     */
    public void processAllFacilityFiles(String directoryPath) {
        log.info("모든 생활시설 데이터 처리 시작: {}", directoryPath);
        
        // TODO: 디렉토리에서 CSV 파일들을 찾아서 처리
        // 현재는 예시 파일 경로 사용
        String[] filePaths = {
            directoryPath + "/convenience_stores.csv",
            directoryPath + "/marts.csv",
            directoryPath + "/hospitals.csv"
        };
        
        for (String filePath : filePaths) {
            try {
                processFacilityCsvFile(filePath, true);
            } catch (Exception e) {
                log.error("파일 처리 중 오류 발생: {}", filePath, e);
            }
        }
        
        log.info("모든 생활시설 데이터 처리 완료");
    }
}
