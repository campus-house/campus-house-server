#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
H2 데이터베이스에 직접 병원 데이터를 저장하는 스크립트
"""

import requests
import json

def insert_hospitals_via_sql():
    """SQL을 통해 병원 데이터 저장"""
    
    # H2 데이터베이스에 직접 접근하는 방법
    # 실제로는 Spring Boot의 DataInitializer를 사용하는 것이 좋습니다.
    
    print("🏥 병원 데이터를 DataInitializer에 추가합니다...")
    
    # DataInitializer.java 파일에 병원 데이터 추가 코드를 작성
    insert_code = '''
    // 병원 데이터 추가
    @PostConstruct
    public void initFacilities() {
        if (facilityRepository.count() == 0) {
            log.info("생활시설 데이터 초기화 시작");
            
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
            
            log.info("생활시설 데이터 초기화 완료: 2개 병원 저장");
        }
    }
    '''
    
    print("📝 DataInitializer.java에 다음 코드를 추가하세요:")
    print("=" * 60)
    print(insert_code)
    print("=" * 60)
    
    print("\n💡 이 코드를 추가한 후 애플리케이션을 재시작하면 병원 데이터가 자동으로 저장됩니다.")

if __name__ == "__main__":
    insert_hospitals_via_sql()
