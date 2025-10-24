#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
처리된 건물 데이터를 데이터베이스에 로드하는 스크립트
"""

import json
import pandas as pd
import psycopg2
from psycopg2.extras import RealDictCursor
import os
from typing import Dict, Any, List
import math

class BuildingDataLoader:
    def __init__(self):
        # 데이터베이스 연결 정보
        self.db_config = {
            'host': 'localhost',
            'database': 'campus_house',
            'user': 'postgres',
            'password': 'helloworld2682',
            'port': '5432',
            'client_encoding': 'utf8'
        }
        self.conn = None
        
    def connect_db(self):
        """데이터베이스 연결"""
        try:
            self.conn = psycopg2.connect(**self.db_config)
            print("✅ 데이터베이스 연결 성공")
            return True
        except Exception as e:
            print(f"❌ 데이터베이스 연결 실패: {e}")
            return False
    
    def close_db(self):
        """데이터베이스 연결 종료"""
        if self.conn:
            self.conn.close()
            print("🔌 데이터베이스 연결 종료")
    
    def clear_existing_data(self):
        """기존 건물 데이터 삭제 (샘플 데이터 제외)"""
        try:
            cursor = self.conn.cursor()
            # 샘플 데이터는 유지하고 실제 데이터만 삭제
            cursor.execute("""
                DELETE FROM buildings 
                WHERE building_name NOT IN ('캠퍼스 하우스 A동', '캠퍼스 하우스 B동', '하이빌 영통')
            """)
            deleted_count = cursor.rowcount
            self.conn.commit()
            cursor.close()
            print(f"🗑️ 기존 데이터 삭제 완료: {deleted_count}개 건물")
        except Exception as e:
            print(f"❌ 기존 데이터 삭제 실패: {e}")
            self.conn.rollback()
    
    def load_buildings_from_csv(self, csv_file_path: str):
        """CSV 파일에서 건물 데이터 로드"""
        try:
            df = pd.read_csv(csv_file_path, encoding='utf-8')
            print(f"📄 CSV 파일 로드 완료: {len(df)}개 건물")
            
            # DataFrame을 딕셔너리 리스트로 변환
            buildings_data = df.to_dict('records')
            return buildings_data
        except Exception as e:
            print(f"❌ CSV 파일 로드 실패: {e}")
            return []
    
    def load_buildings_from_json(self, json_file_path: str):
        """JSON 파일에서 건물 데이터 로드"""
        try:
            with open(json_file_path, 'r', encoding='utf-8') as f:
                buildings_data = json.load(f)
            print(f"📄 JSON 파일 로드 완료: {len(buildings_data)}개 건물")
            return buildings_data
        except Exception as e:
            print(f"❌ JSON 파일 로드 실패: {e}")
            return []
    
    def get_coordinates_for_address(self, address: str) -> tuple:
        """주소를 기반으로 위도/경도 좌표 반환 (수원시 영통구 기준)"""
        # 수원시 영통구의 대략적인 중심 좌표
        base_lat = 37.2636
        base_lng = 127.0286
        
        # 주소에 따라 약간의 오프셋 추가 (실제로는 지오코딩 API 사용해야 함)
        if "영통동" in address:
            lat_offset = (hash(address) % 100) / 10000  # ±0.01도 범위
            lng_offset = (hash(address + "1") % 100) / 10000
        elif "서천동" in address:
            lat_offset = (hash(address) % 50) / 10000 + 0.005  # 약간 남쪽
            lng_offset = (hash(address + "2") % 50) / 10000 + 0.005  # 약간 동쪽
        else:
            lat_offset = (hash(address) % 200) / 10000 - 0.01
            lng_offset = (hash(address + "3") % 200) / 10000 - 0.01
        
        return base_lat + lat_offset, base_lng + lng_offset
    
    def calculate_walking_time(self, building_type: str, area: float) -> int:
        """건물 타입과 면적을 기반으로 학교/역까지 걸리는 시간 계산"""
        base_time = 10  # 기본 10분
        
        # 건물 타입별 조정
        if building_type == "아파트":
            base_time += 2  # 아파트는 보통 조금 더 멀리
        elif building_type == "오피스텔":
            base_time -= 1  # 오피스텔은 보통 중심가에 위치
        
        # 면적별 조정 (큰 건물일수록 중심가에 위치할 가능성)
        if area > 50:
            base_time -= 2
        elif area > 30:
            base_time -= 1
        
        # 랜덤 요소 추가
        import random
        random_factor = random.randint(-3, 3)
        
        return max(3, min(25, base_time + random_factor))  # 3-25분 범위
    
    def insert_building(self, building_data: Dict[str, Any]) -> bool:
        """개별 건물 데이터를 DB에 삽입"""
        try:
            cursor = self.conn.cursor()
            
            # 실제 좌표 사용 (CSV에서 가져온 좌표)
            latitude = building_data.get('latitude', 37.2636)
            longitude = building_data.get('longitude', 127.0286)
            
            # 걸리는 시간 계산 (CSV에서 직접 가져오거나 계산)
            school_time = building_data.get('school_walking_time', self.calculate_walking_time(building_data['building_type'], building_data.get('area', 30)))
            station_time = building_data.get('station_walking_time', max(3, school_time - 2))
            
            # SQL 쿼리
            insert_query = """
                INSERT INTO buildings (
                    building_name, address, latitude, longitude,
                    deposit, monthly_rent, jeonse, households, heating_type,
                    elevators, building_usage, nearby_convenience_stores,
                    nearby_marts, nearby_hospitals, school_walking_time, station_walking_time,
                    scrap_count, floors_ground, area, construction_year,
                    road_name, sample_count, avg_price, created_at, updated_at
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW(), NOW()
                )
            """
            
            # 데이터 준비
            values = (
                building_data['building_name'],
                building_data['address'],
                latitude,
                longitude,
                building_data.get('avg_deposit', 0) * 10000,  # 만원 단위를 원 단위로 변환
                building_data.get('avg_monthly_rent', 0) * 10000,
                None,  # 전세는 별도 계산 필요
                building_data.get('households', 0),
                building_data.get('heating_type', '개별난방'),
                building_data.get('elevators', 0),
                building_data.get('building_usage', '기타'),
                0,  # nearby_convenience_stores (나중에 업데이트)
                0,  # nearby_marts
                0,  # nearby_hospitals
                school_time,
                station_time,
                0,  # scrap_count
                building_data.get('ground_floors', 0),
                building_data.get('area', 0),
                building_data.get('construction_year', 2000),
                building_data.get('road_name', ''),
                building_data.get('sample_count', 0),
                building_data.get('avg_deposit', 0) * 10000  # avg_price
            )
            
            cursor.execute(insert_query, values)
            self.conn.commit()
            cursor.close()
            return True
            
        except Exception as e:
            print(f"❌ 건물 삽입 실패: {building_data['building_name']} - {e}")
            self.conn.rollback()
            return False
    
    def load_all_buildings(self, csv_file_path: str):
        """모든 건물 데이터를 DB에 로드"""
        if not self.connect_db():
            return False
        
        try:
            # 기존 데이터 삭제
            self.clear_existing_data()
            
            # CSV 데이터 로드
            buildings_data = self.load_buildings_from_csv(csv_file_path)
            if not buildings_data:
                return False
            
            # 건물 데이터 삽입
            success_count = 0
            total_count = len(buildings_data)
            
            print(f"🏢 건물 데이터 삽입 시작: {total_count}개 건물")
            
            for i, building_data in enumerate(buildings_data, 1):
                if self.insert_building(building_data):
                    success_count += 1
                
                if i % 50 == 0:  # 50개마다 진행상황 출력
                    print(f"  📊 진행상황: {i}/{total_count} ({i/total_count*100:.1f}%)")
            
            print(f"✅ 건물 데이터 로드 완료!")
            print(f"  - 성공: {success_count}개")
            print(f"  - 실패: {total_count - success_count}개")
            print(f"  - 성공률: {success_count/total_count*100:.1f}%")
            
            return True
            
        except Exception as e:
            print(f"❌ 건물 데이터 로드 실패: {e}")
            return False
        finally:
            self.close_db()

def main():
    loader = BuildingDataLoader()
    
    # CSV 파일 경로
    csv_file_path = "buildings_data.csv"
    
    if not os.path.exists(csv_file_path):
        print(f"❌ CSV 파일을 찾을 수 없습니다: {csv_file_path}")
        return
    
    # 건물 데이터 로드
    success = loader.load_all_buildings(csv_file_path)
    
    if success:
        print("\n🎉 모든 건물 데이터가 성공적으로 로드되었습니다!")
        print("이제 애플리케이션에서 실제 건물 데이터를 확인할 수 있습니다.")
    else:
        print("\n❌ 건물 데이터 로드에 실패했습니다.")

if __name__ == "__main__":
    main()
