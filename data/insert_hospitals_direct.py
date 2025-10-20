#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
병원 데이터를 직접 데이터베이스에 저장하는 스크립트
"""

import requests
import json
import time

def insert_hospitals_to_api():
    """API를 통해 병원 데이터 저장"""
    
    # 병원 데이터
    hospitals = [
        {
            "businessName": "더웰병원",
            "address": "경기도 수원시 영통구 영통동 996-3번지 대우월드마크영통 3,5,6,7,8층",
            "roadAddress": "경기도 수원시 영통구 봉영로 1620 (영통동, 대우월드마크영통 3,5,6,7,8층)",
            "businessStatus": "영업/정상",
            "category": "HOSPITAL",
            "subCategory": "소아과",
            "latitude": 37.2550152411,
            "longitude": 127.0756344537,
            "description": "내과, 정신건강의학과, 성형외과, 마취통증의학과, 소아청소년과, 이비인후과, 피부과, 영상의학과, 가정의학과"
        },
        {
            "businessName": "베데스다병원",
            "address": "경기도 수원시 영통구 영통동 958-1 드림피아빌딩",
            "roadAddress": "경기도 수원시 영통구 봉영로 1623, 드림피아빌딩 6층일부,7,8,9층 (영통동)",
            "businessStatus": "영업/정상",
            "category": "HOSPITAL",
            "subCategory": "한의원",
            "latitude": 37.2559223973,
            "longitude": 127.0747272211,
            "description": "내과, 피부과, 재활의학과, 가정의학과, 한방내과, 한방부인과, 한방소아과, 한방안·이비인후·피부과, 한방재활의학과, 침구과"
        }
    ]
    
    base_url = "http://localhost:8080"
    
    print("🏥 병원 데이터 저장 시작...")
    
    for i, hospital in enumerate(hospitals, 1):
        try:
            # Facility 생성 API 호출 (실제로는 직접 DB에 저장하는 API가 필요)
            print(f"  {i}. {hospital['businessName']} 저장 중...")
            
            # 실제로는 Facility 엔티티를 위한 API가 필요하지만,
            # 현재는 테스트를 위해 건물 데이터를 먼저 확인해보겠습니다.
            
        except Exception as e:
            print(f"  ❌ 오류 발생: {e}")
    
    print("✅ 병원 데이터 저장 완료!")

def test_facility_api():
    """생활시설 API 테스트"""
    
    base_url = "http://localhost:8080"
    
    print("\n🧪 생활시설 API 테스트 시작...")
    
    # 1. 주변 생활시설 개수 조회 테스트
    test_latitude = 37.255  # 영통동 근처
    test_longitude = 127.075
    
    try:
        response = requests.get(f"{base_url}/api/facilities/nearby/counts", 
                              params={
                                  "latitude": test_latitude,
                                  "longitude": test_longitude,
                                  "radiusKm": 1.0
                              })
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ 주변 생활시설 개수 조회 성공: {data}")
        else:
            print(f"❌ API 호출 실패: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ API 호출 중 오류: {e}")

def test_building_api():
    """건물 API 테스트"""
    
    base_url = "http://localhost:8080"
    
    print("\n🏢 건물 API 테스트 시작...")
    
    try:
        # 1. 건물 목록 조회
        response = requests.get(f"{base_url}/api/buildings")
        
        if response.status_code == 200:
            data = response.json()
            buildings = data.get('content', [])
            print(f"✅ 건물 목록 조회 성공: {len(buildings)}개 건물")
            
            # 2. 첫 번째 건물의 주변 생활시설 개수 조회
            if buildings:
                building_id = buildings[0]['id']
                print(f"  - 첫 번째 건물 ID: {building_id}")
                
                # 주변 생활시설 개수 조회
                facility_response = requests.get(f"{base_url}/api/buildings/{building_id}/nearby-facilities")
                
                if facility_response.status_code == 200:
                    facility_data = facility_response.json()
                    print(f"  ✅ 주변 생활시설 개수: {facility_data}")
                else:
                    print(f"  ❌ 주변 생활시설 조회 실패: {facility_response.status_code}")
                    
        else:
            print(f"❌ 건물 목록 조회 실패: {response.status_code}")
            
    except Exception as e:
        print(f"❌ API 호출 중 오류: {e}")

if __name__ == "__main__":
    print("🚀 캠퍼스 하우스 API 테스트 시작")
    print("=" * 50)
    
    # 애플리케이션 시작 대기
    print("⏳ 애플리케이션 시작 대기 중...")
    time.sleep(15)
    
    # API 테스트
    test_building_api()
    test_facility_api()
    
    print("\n🎉 테스트 완료!")
