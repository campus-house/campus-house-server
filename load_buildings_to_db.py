#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
buildings_data.csv 파일의 데이터를 PostgreSQL 데이터베이스에 삽입하는 스크립트
"""

import pandas as pd
import psycopg2
from psycopg2.extras import execute_values
import sys
import os
from datetime import datetime

# 데이터베이스 연결 설정
DB_CONFIG = {
    'host': 'localhost',
    'port': 5432,
    'database': 'campus_house',
    'user': 'postgres',
    'password': 'helloworld2682'
}

def _format_date(date_value):
    """날짜 형식을 PostgreSQL timestamp로 변환"""
    if pd.isna(date_value):
        return None
    
    date_str = str(date_value)
    if date_str == 'nan' or date_str == '':
        return None
    
    # YYYYMMDD 형식을 YYYY-MM-DD로 변환
    if len(date_str) == 8 and date_str.isdigit():
        year = date_str[:4]
        month = date_str[4:6]
        day = date_str[6:8]
        return f"{year}-{month}-{day}"
    
    return None

def connect_to_db():
    """데이터베이스에 연결"""
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        print("✅ 데이터베이스 연결 성공")
        return conn
    except Exception as e:
        print(f"❌ 데이터베이스 연결 실패: {e}")
        return None

def load_csv_data(csv_file_path):
    """CSV 파일에서 데이터 로드"""
    try:
        df = pd.read_csv(csv_file_path)
        print(f"✅ CSV 파일 로드 성공: {len(df)}개 행")
        return df
    except Exception as e:
        print(f"❌ CSV 파일 로드 실패: {e}")
        return None

def insert_buildings_data(conn, df):
    """건물 데이터를 데이터베이스에 삽입"""
    cursor = conn.cursor()
    
    try:
        # 기존 데이터 삭제 (선택사항)
        cursor.execute("DELETE FROM buildings")
        print("🗑️ 기존 buildings 데이터 삭제 완료")
        
        # 데이터 삽입을 위한 SQL 쿼리
        insert_query = """
        INSERT INTO buildings (
            id, building_name, address, latitude, longitude, deposit, monthly_rent,
            households, heating_type, elevators, building_usage, approval_date, 
            completion_date, school_walking_time, station_walking_time, scrap_count,
            floors_ground, area, construction_year, road_name, sample_count, avg_price,
            created_at, updated_at
        ) VALUES %s
        """
        
        # 데이터프레임을 튜플 리스트로 변환
        data_tuples = []
        for _, row in df.iterrows():
            data_tuple = (
                int(row['id']),
                str(row['building_name']),
                str(row['address']),
                float(row['latitude']) if pd.notna(row['latitude']) else None,
                float(row['longitude']) if pd.notna(row['longitude']) else None,
                float(row['avg_deposit']) if pd.notna(row['avg_deposit']) else None,
                float(row['avg_monthly_rent']) if pd.notna(row['avg_monthly_rent']) else None,
                int(row['households']) if pd.notna(row['households']) else None,
                str(row['heating_type']) if pd.notna(row['heating_type']) else None,
                int(row['elevators']) if pd.notna(row['elevators']) else None,
                str(row['building_usage']) if pd.notna(row['building_usage']) else None,
                _format_date(row['approval_date']) if pd.notna(row['approval_date']) else None,
                _format_date(row['completion_date']) if pd.notna(row['completion_date']) else None,
                int(row['school_walking_time']) if pd.notna(row['school_walking_time']) else None,
                int(row['station_walking_time']) if pd.notna(row['station_walking_time']) else None,
                0,  # scrap_count 기본값
                int(row['ground_floors']) if pd.notna(row['ground_floors']) else None,
                float(row['area']) if pd.notna(row['area']) else None,
                int(row['construction_year']) if pd.notna(row['construction_year']) else None,
                str(row['road_name']) if pd.notna(row['road_name']) else None,
                int(row['sample_count']) if pd.notna(row['sample_count']) else None,
                float(row['avg_deposit']) if pd.notna(row['avg_deposit']) else None,  # avg_price로 사용
                datetime.now(),  # created_at
                datetime.now()   # updated_at
            )
            data_tuples.append(data_tuple)
        
        # 배치 삽입 실행
        execute_values(cursor, insert_query, data_tuples)
        conn.commit()
        
        print(f"✅ {len(data_tuples)}개 건물 데이터 삽입 완료")
        
    except Exception as e:
        conn.rollback()
        print(f"❌ 데이터 삽입 실패: {e}")
        raise
    finally:
        cursor.close()

def verify_data(conn):
    """삽입된 데이터 확인"""
    cursor = conn.cursor()
    try:
        cursor.execute("SELECT COUNT(*) FROM buildings")
        count = cursor.fetchone()[0]
        print(f"📊 데이터베이스에 저장된 건물 수: {count}개")
        
        # 샘플 데이터 출력
        cursor.execute("SELECT id, building_name, address, building_usage FROM buildings LIMIT 5")
        samples = cursor.fetchall()
        print("\n📋 샘플 데이터:")
        for sample in samples:
            print(f"  - ID: {sample[0]}, 이름: {sample[1]}, 주소: {sample[2]}, 용도: {sample[3]}")
            
    except Exception as e:
        print(f"❌ 데이터 확인 실패: {e}")
    finally:
        cursor.close()

def main():
    """메인 함수"""
    print("🏢 건물 데이터 PostgreSQL 삽입 스크립트 시작")
    print("=" * 50)
    
    # CSV 파일 경로
    csv_file_path = "buildings_data.csv"
    
    # 파일 존재 확인
    if not os.path.exists(csv_file_path):
        print(f"❌ CSV 파일을 찾을 수 없습니다: {csv_file_path}")
        sys.exit(1)
    
    # 데이터베이스 연결
    conn = connect_to_db()
    if not conn:
        sys.exit(1)
    
    try:
        # CSV 데이터 로드
        df = load_csv_data(csv_file_path)
        if df is None:
            sys.exit(1)
        
        # 데이터 삽입
        insert_buildings_data(conn, df)
        
        # 데이터 확인
        verify_data(conn)
        
        print("\n🎉 모든 작업이 성공적으로 완료되었습니다!")
        
    except Exception as e:
        print(f"❌ 오류 발생: {e}")
        sys.exit(1)
    finally:
        conn.close()
        print("🔌 데이터베이스 연결 종료")

if __name__ == "__main__":
    main()
