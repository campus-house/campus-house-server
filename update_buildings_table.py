#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import json
import psycopg2
import os
from datetime import datetime

# 데이터베이스 연결 설정
db_config = {
    'host': 'localhost',
    'port': 5432,
    'database': 'campus_house',
    'user': 'postgres',
    'password': 'helloworld2682'
}

def backup_current_data():
    """현재 buildings 테이블 데이터를 백업합니다."""
    print("📦 현재 buildings 테이블 데이터 백업 중...")
    
    try:
        conn = psycopg2.connect(**db_config)
        cursor = conn.cursor()
        
        # 현재 데이터 조회
        cursor.execute("SELECT * FROM buildings ORDER BY id")
        buildings = cursor.fetchall()
        
        # 컬럼명 조회
        cursor.execute("""
            SELECT column_name 
            FROM information_schema.columns 
            WHERE table_name = 'buildings' 
            ORDER BY ordinal_position
        """)
        columns = [row[0] for row in cursor.fetchall()]
        
        # 백업 파일 생성
        backup_data = []
        for building in buildings:
            building_dict = dict(zip(columns, building))
            backup_data.append(building_dict)
        
        backup_filename = f"buildings_backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
        with open(backup_filename, 'w', encoding='utf-8') as f:
            json.dump(backup_data, f, ensure_ascii=False, indent=2, default=str)
        
        print(f"✅ 백업 완료: {backup_filename} ({len(buildings)}개 건물)")
        
        cursor.close()
        conn.close()
        return backup_filename
        
    except Exception as e:
        print(f"❌ 백업 실패: {e}")
        return None

def clear_buildings_table():
    """buildings 테이블의 모든 데이터를 삭제합니다."""
    print("🗑️  buildings 테이블 데이터 삭제 중...")
    
    try:
        conn = psycopg2.connect(**db_config)
        cursor = conn.cursor()
        
        # 외래키 제약조건 비활성화
        cursor.execute("SET session_replication_role = replica;")
        
        # buildings 테이블 데이터 삭제
        cursor.execute("DELETE FROM buildings")
        
        # 시퀀스 리셋
        cursor.execute("ALTER SEQUENCE buildings_id_seq RESTART WITH 1")
        
        conn.commit()
        print("✅ buildings 테이블 데이터 삭제 완료")
        
        cursor.close()
        conn.close()
        return True
        
    except Exception as e:
        print(f"❌ 테이블 삭제 실패: {e}")
        return False

def load_new_buildings_data():
    """새로운 JSON 데이터를 buildings 테이블에 로드합니다."""
    print("📥 새로운 건물 데이터 로드 중...")
    
    # JSON 파일 경로
    json_file_path = "/Users/sjaize/Desktop/campus-house-server/data/buildings/processed/buildings_processed.json"
    
    if not os.path.exists(json_file_path):
        print(f"❌ JSON 파일을 찾을 수 없습니다: {json_file_path}")
        return False
    
    try:
        # JSON 파일 읽기
        with open(json_file_path, 'r', encoding='utf-8') as f:
            buildings = json.load(f)
        
        print(f"📊 로드할 건물 수: {len(buildings)}")
        
        conn = psycopg2.connect(**db_config)
        cursor = conn.cursor()
        
        # 각 건물 데이터 삽입
        for building in buildings:
            insert_query = """
                INSERT INTO buildings (
                    id, building_name, address, area, deposit, monthly_rent, 
                    construction_year, road_name, sample_count, households, 
                    floors_ground, elevators, building_usage, heating_type, 
                    approval_date, completion_date, latitude, longitude, 
                    school_walking_time, station_walking_time, nearby_convenience_stores, 
                    nearby_marts, nearby_hospitals, created_at, updated_at
                ) VALUES (
                    %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s
                )
            """
            
            # 빈 문자열을 None으로 변환
            approval_date = building.get('approval_date')
            completion_date = building.get('completion_date')
            
            if approval_date == '':
                approval_date = None
            if completion_date == '':
                completion_date = None
            
            values = (
                building.get('id'),
                building.get('building_name'),
                building.get('address'),
                building.get('area'),
                building.get('avg_deposit'),
                building.get('avg_monthly_rent'),
                building.get('construction_year'),
                building.get('road_name'),
                building.get('sample_count'),
                building.get('households'),
                building.get('ground_floors'),
                building.get('elevators'),
                building.get('building_usage'),
                building.get('heating_type'),
                approval_date,
                completion_date,
                building.get('latitude'),
                building.get('longitude'),
                building.get('school_walking_time'),
                building.get('station_walking_time'),
                building.get('nearby_convenience_stores', 0),
                building.get('nearby_marts', 0),
                building.get('nearby_hospitals', 0),
                datetime.now(),
                datetime.now()
            )
            
            cursor.execute(insert_query, values)
        
        conn.commit()
        print(f"✅ {len(buildings)}개 건물 데이터 로드 완료")
        
        cursor.close()
        conn.close()
        return True
        
    except Exception as e:
        print(f"❌ 데이터 로드 실패: {e}")
        return False

def verify_data():
    """갱신된 데이터를 검증합니다."""
    print("🔍 갱신된 데이터 검증 중...")
    
    try:
        conn = psycopg2.connect(**db_config)
        cursor = conn.cursor()
        
        # 총 건물 수 확인
        cursor.execute("SELECT COUNT(*) FROM buildings")
        count = cursor.fetchone()[0]
        print(f"📊 총 건물 수: {count}")
        
        # ID 범위 확인
        cursor.execute("SELECT MIN(id), MAX(id) FROM buildings")
        min_id, max_id = cursor.fetchone()
        print(f"🔢 ID 범위: {min_id} ~ {max_id}")
        
        # 샘플 데이터 확인
        cursor.execute("SELECT id, building_name, address FROM buildings ORDER BY id LIMIT 5")
        samples = cursor.fetchall()
        print("📋 샘플 데이터:")
        for sample in samples:
            print(f"  ID {sample[0]}: {sample[1]} - {sample[2]}")
        
        cursor.close()
        conn.close()
        return True
        
    except Exception as e:
        print(f"❌ 데이터 검증 실패: {e}")
        return False

def main():
    """메인 함수"""
    print("🏠 PostgreSQL buildings 테이블 갱신 시작")
    print("=" * 60)
    
    # 1. 현재 데이터 백업
    backup_file = backup_current_data()
    if not backup_file:
        print("❌ 백업 실패로 인해 작업을 중단합니다.")
        return
    
    # 2. 테이블 데이터 삭제
    if not clear_buildings_table():
        print("❌ 테이블 삭제 실패로 인해 작업을 중단합니다.")
        return
    
    # 3. 새로운 데이터 로드
    if not load_new_buildings_data():
        print("❌ 데이터 로드 실패로 인해 작업을 중단합니다.")
        return
    
    # 4. 데이터 검증
    if not verify_data():
        print("❌ 데이터 검증 실패")
        return
    
    print("\n" + "=" * 60)
    print("🎉 buildings 테이블 갱신 완료!")
    print(f"💾 백업 파일: {backup_file}")

if __name__ == "__main__":
    main()
