package com.example.campus_house.data;

import com.example.campus_house.entity.Building;
import com.example.campus_house.repository.BuildingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 건물 데이터 정제 및 변환 처리기
 * CSV 파일들을 읽어서 Building 엔티티로 변환하고 데이터베이스에 저장
 */
@Component
public class BuildingDataProcessor {
    
    @Autowired
    private BuildingRepository buildingRepository;
    
    /**
     * 모든 CSV 파일을 처리하여 건물 데이터를 생성
     */
    public void processAllCsvFiles() {
        String rawDataPath = "data/buildings/raw/";
        String processedDataPath = "data/buildings/processed/";
        
        // 처리할 파일 목록
        List<String> csvFiles = Arrays.asList(
            "경기부동산포털_건물_표제부.csv",
            "단독다가구(전월세)_실거래가_20251019153905_영통동.csv",
            "단독다가구(전월세)_실거래가_20251019153939_서천동.csv",
            "아파트(전월세)_실거래가_20251019154009_서천동.csv",
            "아파트(전월세)_실거래가_20251019154026_영통동.csv",
            "오피스텔(전월세)_실거래가_20251019153748_영통동.csv",
            "오피스텔(전월세)_실거래가_20251019153832_서천동.csv"
        );
        
        Map<String, BuildingData> buildingMap = new HashMap<>();
        
        // 각 CSV 파일 처리
        for (String fileName : csvFiles) {
            try {
                processCsvFile(rawDataPath + fileName, buildingMap);
            } catch (Exception e) {
                System.err.println("파일 처리 중 오류 발생: " + fileName + " - " + e.getMessage());
            }
        }
        
        // 건물 데이터를 Building 엔티티로 변환하여 저장
        saveBuildingData(buildingMap);
        
        // 정제된 데이터를 CSV로 저장
        saveProcessedDataToCsv(buildingMap, processedDataPath + "buildings_processed.csv");
        
        System.out.println("건물 데이터 처리 완료! 총 " + buildingMap.size() + "개 건물 처리됨");
    }
    
    /**
     * 개별 CSV 파일 처리
     */
    private void processCsvFile(String filePath, Map<String, BuildingData> buildingMap) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("파일을 찾을 수 없습니다: " + filePath);
            return;
        }
        
        // 파일명으로 건물 타입 판단
        String buildingType = determineBuildingType(filePath);
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // 헤더 라인 스킵 (처음 몇 줄은 설명)
                if (lineNumber <= 20 || line.trim().isEmpty() || line.startsWith("\"")) {
                    continue;
                }
                
                // CSV 파싱
                String[] columns = parseCsvLine(line);
                if (columns.length < 10) continue; // 최소 컬럼 수 확인
                
                // 건물 데이터 추출
                BuildingData buildingData = extractBuildingData(columns, buildingType, filePath);
                if (buildingData != null) {
                    String key = buildingData.getBuildingKey();
                    if (buildingMap.containsKey(key)) {
                        // 기존 데이터와 병합 (가격 정보 업데이트)
                        mergeBuildingData(buildingMap.get(key), buildingData);
                    } else {
                        buildingMap.put(key, buildingData);
                    }
                }
            }
        }
    }
    
    /**
     * 파일명으로 건물 타입 판단
     */
    private String determineBuildingType(String filePath) {
        if (filePath.contains("아파트")) return "아파트";
        if (filePath.contains("오피스텔")) return "오피스텔";
        if (filePath.contains("단독다가구")) return "단독다가구";
        if (filePath.contains("표제부")) return "표제부";
        return "기타";
    }
    
    /**
     * CSV 라인 파싱 (따옴표 처리)
     */
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
    
    /**
     * 건물 데이터 추출
     */
    private BuildingData extractBuildingData(String[] columns, String buildingType, String filePath) {
        try {
            BuildingData data = new BuildingData();
            
            if (buildingType.equals("표제부")) {
                // 표제부 데이터 처리 (인코딩 문제로 추후 처리)
                return null;
            } else {
                // 실거래가 데이터 처리
                if (columns.length < 15) return null;
                
                // 기본 정보 추출
                data.setBuildingName(cleanString(columns[5])); // 아파트명
                data.setAddress(buildAddress(columns[1], columns[2], columns[3], columns[4])); // 주소
                data.setBuildingType(buildingType);
                
                // 면적 정보
                String areaStr = cleanString(columns[6]);
                if (!areaStr.isEmpty()) {
                    data.setArea(Double.parseDouble(areaStr));
                }
                
                // 가격 정보
                String priceStr = cleanString(columns[9]);
                if (!priceStr.isEmpty()) {
                    long price = Long.parseLong(priceStr.replace(",", ""));
                    data.addPrice(price);
                }
                
                // 층수 정보
                String floorStr = cleanString(columns[10]);
                if (!floorStr.isEmpty()) {
                    data.setFloor(Integer.parseInt(floorStr));
                }
                
                // 건축년도
                String yearStr = cleanString(columns[11]);
                if (!yearStr.isEmpty()) {
                    data.setConstructionYear(Integer.parseInt(yearStr));
                }
                
                // 도로명
                data.setRoadName(cleanString(columns[12]));
                
                return data;
            }
        } catch (Exception e) {
            System.err.println("데이터 추출 중 오류: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 문자열 정리 (따옴표, 공백 제거)
     */
    private String cleanString(String str) {
        if (str == null) return "";
        return str.replace("\"", "").trim();
    }
    
    /**
     * 주소 구성
     */
    private String buildAddress(String sido, String sigungu, String dong, String jibun) {
        return cleanString(sido) + " " + cleanString(sigungu) + " " + cleanString(dong) + " " + cleanString(jibun);
    }
    
    /**
     * 건물 데이터 병합
     */
    private void mergeBuildingData(BuildingData existing, BuildingData newData) {
        // 가격 정보 추가
        existing.getPrices().addAll(newData.getPrices());
        
        // 기타 정보 업데이트 (더 상세한 정보가 있으면)
        if (newData.getArea() > 0 && existing.getArea() == 0) {
            existing.setArea(newData.getArea());
        }
        if (newData.getFloor() > 0 && existing.getFloor() == 0) {
            existing.setFloor(newData.getFloor());
        }
        if (newData.getConstructionYear() > 0 && existing.getConstructionYear() == 0) {
            existing.setConstructionYear(newData.getConstructionYear());
        }
    }
    
    /**
     * 건물 데이터를 Building 엔티티로 변환하여 저장
     */
    private void saveBuildingData(Map<String, BuildingData> buildingMap) {
        for (BuildingData data : buildingMap.values()) {
            try {
                Building building = convertToBuilding(data);
                buildingRepository.save(building);
            } catch (Exception e) {
                System.err.println("건물 저장 중 오류: " + data.getBuildingName() + " - " + e.getMessage());
            }
        }
    }
    
    /**
     * BuildingData를 Building 엔티티로 변환
     */
    private Building convertToBuilding(BuildingData data) {
        // 평균 가격 계산
        double avgPrice = data.getPrices().stream().mapToLong(Long::longValue).average().orElse(0);
        
        // 기본값 설정
        BigDecimal deposit = BigDecimal.valueOf(avgPrice * 0.1); // 보증금 (가격의 10%)
        BigDecimal monthlyRent = BigDecimal.valueOf(avgPrice * 0.01); // 월세 (가격의 1%)
        
        return Building.builder()
                .buildingName(data.getBuildingName())
                .address(data.getAddress())
                .latitude(37.5665) // 기본값 (나중에 네이버 API로 변환)
                .longitude(126.9780) // 기본값 (나중에 네이버 API로 변환)
                .deposit(deposit)
                .monthlyRent(monthlyRent)
                .jeonse(BigDecimal.ZERO)
                .households(50) // 기본값
                .heatingType("개별난방") // 기본값
                .parkingSpaces(10) // 기본값
                .elevators(1) // 기본값
                .buildingUsage(data.getBuildingType())
                .approvalDate(LocalDateTime.now().minusYears(5)) // 기본값
                .completionDate(LocalDateTime.now().minusYears(5)) // 기본값
                .nearbyConvenienceStores(3) // 기본값
                .nearbyMarts(1) // 기본값
                .nearbyHospitals(1) // 기본값
                .schoolWalkingTime(15) // 기본값
                .stationWalkingTime(10) // 기본값
                .scrapCount(0)
                .build();
    }
    
    /**
     * 정제된 데이터를 CSV로 저장
     */
    private void saveProcessedDataToCsv(Map<String, BuildingData> buildingMap, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
            // 헤더 작성
            writer.println("id,buildingName,address,buildingType,area,avgPrice,floor,constructionYear,roadName,sampleCount");
            
            // 데이터 작성
            int id = 1;
            for (BuildingData data : buildingMap.values()) {
                double avgPrice = data.getPrices().stream().mapToLong(Long::longValue).average().orElse(0);
                writer.printf("%d,%s,%s,%s,%.2f,%.0f,%d,%d,%s,%d%n",
                    id++,
                    escapeCsv(data.getBuildingName()),
                    escapeCsv(data.getAddress()),
                    data.getBuildingType(),
                    data.getArea(),
                    avgPrice,
                    data.getFloor(),
                    data.getConstructionYear(),
                    escapeCsv(data.getRoadName()),
                    data.getPrices().size()
                );
            }
        } catch (IOException e) {
            System.err.println("CSV 저장 중 오류: " + e.getMessage());
        }
    }
    
    /**
     * CSV 이스케이프 처리
     */
    private String escapeCsv(String str) {
        if (str == null) return "";
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }
    
    /**
     * 건물 데이터 임시 클래스
     */
    private static class BuildingData {
        private String buildingName;
        private String address;
        private String buildingType;
        private double area;
        private List<Long> prices = new ArrayList<>();
        private int floor;
        private int constructionYear;
        private String roadName;
        
        public String getBuildingKey() {
            return buildingName + "_" + address;
        }
        
        // Getters and Setters
        public String getBuildingName() { return buildingName; }
        public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getBuildingType() { return buildingType; }
        public void setBuildingType(String buildingType) { this.buildingType = buildingType; }
        
        public double getArea() { return area; }
        public void setArea(double area) { this.area = area; }
        
        public List<Long> getPrices() { return prices; }
        public void addPrice(long price) { this.prices.add(price); }
        
        public int getFloor() { return floor; }
        public void setFloor(int floor) { this.floor = floor; }
        
        public int getConstructionYear() { return constructionYear; }
        public void setConstructionYear(int constructionYear) { this.constructionYear = constructionYear; }
        
        public String getRoadName() { return roadName; }
        public void setRoadName(String roadName) { this.roadName = roadName; }
    }
}
