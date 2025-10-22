#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
카카오맵 API를 이용해서 건물 좌표 정보를 가져오는 스크립트
"""

import json
import requests
import time
from pathlib import Path

class KakaoBuildingCoordinateExtractor:
    def __init__(self):
        # 카카오맵 API 설정 (REST API 키 필요)
        self.api_key = "aac62a405399430b88d8e8d96329458d"  # 카카오 개발자 콘솔에서 발급받은 REST API 키
        self.base_url = "https://dapi.kakao.com/v2/local/search/address.json"
        
        # 경희대학교 국제캠퍼스 정문 좌표 (카카오맵 API로 가져온 정확한 좌표)
        self.kyunghee_univ_lat = 37.247359629174
        self.kyunghee_univ_lng = 127.078422039016
        
        # 영통역 좌표 (카카오맵 API로 가져온 정확한 좌표)
        self.yeongtong_station_lat = 37.2515035095752
        self.yeongtong_station_lng = 127.07133191773
        
        # 도보 속도 (km/h)
        self.walking_speed_kmh = 4.0
        
    def get_coordinates_from_address(self, address, road_name=None, building_name=None):
        """카카오맵 API로 주소를 좌표로 변환 (여러 방법으로 시도)"""
        try:
            headers = {
                "Authorization": f"KakaoAK {self.api_key}"
            }
            
            # 1. address로 먼저 시도
            if address and address.strip():
                print(f"📍 address로 시도: {address}")
                params = {"query": address}
                response = requests.get(self.base_url, headers=headers, params=params, timeout=5)
                
                if response.status_code == 200:
                    data = response.json()
                    if "documents" in data and len(data["documents"]) > 0:
                        address_info = data["documents"][0]
                        print(f"✅ address로 좌표 조회 성공: {address}")
                        return {
                            "latitude": float(address_info["y"]),
                            "longitude": float(address_info["x"]),
                            "road_address": address_info.get("road_address", {}).get("address_name", "") if address_info.get("road_address") else "",
                            "jibun_address": address_info.get("address", {}).get("address_name", "") if address_info.get("address") else "",
                            "method": "kakao_address"
                        }
                    else:
                        print(f"⚠️ address로 검색 실패: {address}")
                else:
                    print(f"❌ address API 호출 실패: {response.status_code}")
            
            # 2. road_name으로 시도
            if road_name and road_name.strip():
                print(f"📍 road_name으로 시도: {road_name}")
                params = {"query": road_name}
                response = requests.get(self.base_url, headers=headers, params=params, timeout=5)
                
                if response.status_code == 200:
                    data = response.json()
                    if "documents" in data and len(data["documents"]) > 0:
                        address_info = data["documents"][0]
                        print(f"✅ road_name으로 좌표 조회 성공: {road_name}")
                        return {
                            "latitude": float(address_info["y"]),
                            "longitude": float(address_info["x"]),
                            "road_address": address_info.get("road_address", {}).get("address_name", "") if address_info.get("road_address") else "",
                            "jibun_address": address_info.get("address", {}).get("address_name", "") if address_info.get("address") else "",
                            "method": "kakao_road_name"
                        }
                    else:
                        print(f"⚠️ road_name으로 검색 실패: {road_name}")
                else:
                    print(f"❌ road_name API 호출 실패: {response.status_code}")
            
            # 3. building_name으로 시도
            if building_name and building_name.strip():
                print(f"📍 building_name으로 시도: {building_name}")
                params = {"query": building_name}
                response = requests.get(self.base_url, headers=headers, params=params, timeout=5)
                
                if response.status_code == 200:
                    data = response.json()
                    if "documents" in data and len(data["documents"]) > 0:
                        address_info = data["documents"][0]
                        print(f"✅ building_name으로 좌표 조회 성공: {building_name}")
                        return {
                            "latitude": float(address_info["y"]),
                            "longitude": float(address_info["x"]),
                            "road_address": address_info.get("road_address", {}).get("address_name", "") if address_info.get("road_address") else "",
                            "jibun_address": address_info.get("address", {}).get("address_name", "") if address_info.get("address") else "",
                            "method": "kakao_building_name"
                        }
                    else:
                        print(f"⚠️ building_name으로 검색 실패: {building_name}")
                else:
                    print(f"❌ building_name API 호출 실패: {response.status_code}")
            
            print(f"❌ 모든 방법으로 검색 실패")
            return None
                
        except Exception as e:
            print(f"❌ 좌표 조회 중 오류: {e}")
            return None
    
    def calculate_walking_distance(self, origin_lat, origin_lng, dest_lat, dest_lng):
        """카카오맵 API를 사용한 실제 도보 거리 계산"""
        import requests
        
        headers = {
            "Authorization": f"KakaoAK {self.api_key}"
        }
        
        # 카카오맵 경로 찾기 API (도보)
        url = "https://apis-navi.kakaomobility.com/v1/directions"
        params = {
            "origin": f"{origin_lng},{origin_lat}",
            "destination": f"{dest_lng},{dest_lat}",
            "waypoints": "",
            "priority": "RECOMMEND",
            "car_fuel": "GASOLINE",
            "car_hipass": False,
            "alternatives": False,
            "road_details": False
        }
        
        try:
            response = requests.get(url, headers=headers, params=params, timeout=10)
            
            if response.status_code == 200:
                data = response.json()
                if "routes" in data and len(data["routes"]) > 0:
                    route = data["routes"][0]
                    # 도보 경로가 있는지 확인
                    if "sections" in route and len(route["sections"]) > 0:
                        section = route["sections"][0]
                        if "distance" in section:
                            # 미터를 킬로미터로 변환
                            distance_km = section["distance"] / 1000.0
                            return distance_km
        except Exception as e:
            print(f"⚠️ 경로 찾기 API 오류: {e}")
        
        # API 실패시 하버사인 공식으로 대체
        return self.calculate_straight_distance(origin_lat, origin_lng, dest_lat, dest_lng)
    
    def calculate_straight_distance(self, lat1, lng1, lat2, lng2):
        """두 좌표 간의 직선 거리 계산 (하버사인 공식) - 대체용"""
        import math
        
        # 지구 반지름 (km)
        R = 6371
        
        # 라디안 변환
        lat1_rad = math.radians(lat1)
        lng1_rad = math.radians(lng1)
        lat2_rad = math.radians(lat2)
        lng2_rad = math.radians(lng2)
        
        # 위도, 경도 차이
        dlat = lat2_rad - lat1_rad
        dlng = lng2_rad - lng1_rad
        
        # 하버사인 공식
        a = math.sin(dlat/2)**2 + math.cos(lat1_rad) * math.cos(lat2_rad) * math.sin(dlng/2)**2
        c = 2 * math.asin(math.sqrt(a))
        
        return R * c
    
    def calculate_walking_time(self, distance_km):
        """거리를 도보 시간으로 변환"""
        return int((distance_km / self.walking_speed_kmh) * 60)
    
    def process_building(self, building_data):
        """개별 건물 처리"""
        building_id = building_data.get("id")
        building_name = building_data.get("building_name", "")
        address = building_data.get("address", "")
        road_name = building_data.get("road_name", "")
        
        print(f"\n[{building_id}] 처리 중...")
        print(f"🏢 건물명: {building_name}")
        print(f"📍 주소: {address}")
        print(f"🛣️ 도로명: {road_name}")
        
        # 좌표 조회
        coordinates = self.get_coordinates_from_address(address, road_name, building_name)
        
        if coordinates:
            lat = coordinates["latitude"]
            lng = coordinates["longitude"]
            
            # 경희대학교까지 실제 도보 거리 및 시간 계산
            distance_to_school = self.calculate_walking_distance(
                lat, lng, self.kyunghee_univ_lat, self.kyunghee_univ_lng)
            school_walking_time = self.calculate_walking_time(distance_to_school)
            
            # 영통역까지 실제 도보 거리 및 시간 계산
            distance_to_station = self.calculate_walking_distance(
                lat, lng, self.yeongtong_station_lat, self.yeongtong_station_lng)
            station_walking_time = self.calculate_walking_time(distance_to_station)
            
            # 건물 데이터 업데이트
            building_data["latitude"] = lat
            building_data["longitude"] = lng
            building_data["school_walking_time"] = school_walking_time
            building_data["station_walking_time"] = station_walking_time
            building_data["road_address"] = coordinates.get("road_address", "")
            building_data["jibun_address"] = coordinates.get("jibun_address", "")
            building_data["coordinate_method"] = coordinates.get("method", "")
            
            # coordinate_description 필드 제거 (카카오맵 API 사용시 불필요)
            if "coordinate_description" in building_data:
                del building_data["coordinate_description"]
            
            print(f"✅ 좌표 업데이트 완료: 위도 {lat:.10f}, 경도 {lng:.10f}")
            print(f"🏫 경희대까지: {distance_to_school:.2f}km, {school_walking_time}분")
            print(f"🚇 영통역까지: {distance_to_station:.2f}km, {station_walking_time}분")
            
            return building_data
        else:
            print(f"❌ 좌표를 찾을 수 없습니다.")
            return building_data
    
    def process_buildings(self, buildings_data, max_buildings=5):
        """건물 데이터 처리 (테스트용으로 처음 5개만)"""
        print(f"🏢 카카오맵 API 건물 좌표 추출 스크립트 시작")
        print(f"📊 총 {len(buildings_data)}개 건물 중 {max_buildings}개 처리")
        print("=" * 50)
        
        processed_buildings = []
        success_count = 0
        
        for i, building in enumerate(buildings_data[:max_buildings]):
            try:
                processed_building = self.process_building(building)
                processed_buildings.append(processed_building)
                
                if processed_building.get("latitude") is not None:
                    success_count += 1
                
                # API 호출 제한 고려 (카카오맵 API: 초당 10회)
                time.sleep(0.1)
                
            except Exception as e:
                print(f"❌ 건물 처리 중 오류: {e}")
                processed_buildings.append(building)
        
        print(f"\n✅ 처리 완료!")
        print(f"   성공: {success_count}개")
        print(f"   실패: {max_buildings - success_count}개")
        print(f"   성공률: {success_count / max_buildings * 100:.1f}%")
        
        return processed_buildings

def main():
    """메인 함수"""
    # JSON 파일 경로 (원본 파일을 직접 수정)
    target_file = "buildings/processed/buildings_processed.json"
    
    # 파일 존재 확인
    if not Path(target_file).exists():
        print(f"❌ 파일을 찾을 수 없습니다: {target_file}")
        return
    
    print(f"📂 수정할 파일: {target_file}")
    
    # 건물 데이터 로드
    try:
        with open(target_file, 'r', encoding='utf-8') as f:
            buildings_data = json.load(f)
        print(f"📄 총 {len(buildings_data)}개 건물 데이터 로드 완료")
    except Exception as e:
        print(f"❌ 건물 데이터 로드 실패: {e}")
        return
    
    # 건물 데이터 처리 (모든 건물 처리)
    extractor = KakaoBuildingCoordinateExtractor()
    processed_buildings = extractor.process_buildings(buildings_data, max_buildings=len(buildings_data))
    
    # 원본 파일에 결과 저장 (덮어쓰기)
    try:
        with open(target_file, 'w', encoding='utf-8') as f:
            json.dump(processed_buildings, f, ensure_ascii=False, indent=2)
        print(f"💾 원본 파일 수정 완료: {target_file}")
    except Exception as e:
        print(f"❌ 파일 수정 실패: {e}")

if __name__ == "__main__":
    main()
