#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
처리된 병원 데이터를 데이터베이스에 저장하는 스크립트
"""

import csv
import json
import sys
import os

# Spring Boot 프로젝트의 루트 디렉토리를 Python 경로에 추가
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))

def load_hospitals_to_database():
    """처리된 병원 데이터를 데이터베이스에 저장"""
    
    # 처리된 데이터 파일 경로
    csv_file = "data/facilities/processed/hospitals_processed.csv"
    
    if not os.path.exists(csv_file):
        print(f"❌ 파일을 찾을 수 없습니다: {csv_file}")
        return
    
    print(f"📄 데이터 파일 읽기: {csv_file}")
    
    hospitals = []
    
    # CSV 파일 읽기
    with open(csv_file, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            hospitals.append(row)
    
    print(f"📊 총 {len(hospitals)}개 병원 데이터 로드됨")
    
    # 데이터베이스에 저장할 SQL 생성
    generate_insert_sql(hospitals)

def generate_insert_sql(hospitals):
    """INSERT SQL 생성"""
    
    sql_file = "data/facilities/processed/insert_hospitals.sql"
    
    with open(sql_file, 'w', encoding='utf-8') as f:
        f.write("-- 병원 데이터 INSERT SQL\n")
        f.write("-- 생성일: " + str(datetime.now()) + "\n\n")
        
        for hospital in hospitals:
            # SQL 이스케이프 처리
            business_name = hospital['사업장명'].replace("'", "''")
            address = hospital['소재지지번주소'].replace("'", "''")
            road_address = hospital['소재지도로명주소'].replace("'", "''")
            business_status = hospital['영업상태명'].replace("'", "''")
            medical_type = hospital['의료기관종별명'].replace("'", "''")
            medical_departments = hospital['진료과목내용'].replace("'", "''")
            
            latitude = hospital['WGS84위도']
            longitude = hospital['WGS84경도']
            area_type = hospital['지역구분']
            
            # 카테고리 결정
            category = "HOSPITAL"
            
            # 세부 카테고리 결정
            sub_category = determine_hospital_subcategory(business_name, medical_departments)
            
            sql = f"""INSERT INTO facilities (
    business_name, 
    address, 
    road_address, 
    business_status, 
    category, 
    sub_category, 
    latitude, 
    longitude, 
    description
) VALUES (
    '{business_name}',
    '{address}',
    '{road_address}',
    '{business_status}',
    '{category}',
    '{sub_category}',
    {latitude},
    {longitude},
    '{medical_departments}'
);\n"""
            
            f.write(sql)
    
    print(f"📁 SQL 파일 생성: {sql_file}")
    print("💡 이 SQL 파일을 데이터베이스에서 실행하세요.")

def determine_hospital_subcategory(business_name, medical_departments):
    """병원 세부 카테고리 결정"""
    
    name_lower = business_name.lower()
    dept_lower = medical_departments.lower()
    
    # 종합병원
    if '종합병원' in business_name or '대학병원' in business_name:
        return '종합병원'
    
    # 대학병원
    if '대학병원' in business_name:
        return '대학병원'
    
    # 전문과목별 분류
    if '치과' in business_name or '치과' in medical_departments:
        return '치과'
    elif '한의원' in business_name or '한방' in medical_departments:
        return '한의원'
    elif '산부인과' in medical_departments:
        return '산부인과'
    elif '소아' in medical_departments:
        return '소아과'
    elif '내과' in medical_departments and '외과' not in medical_departments:
        return '내과'
    elif '외과' in medical_departments:
        return '외과'
    else:
        return '병원'

if __name__ == "__main__":
    from datetime import datetime
    load_hospitals_to_database()
