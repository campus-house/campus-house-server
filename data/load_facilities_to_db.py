#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
정제된 주변시설 데이터를 데이터베이스에 로드하는 스크립트
- 병원, 편의점, 마트 데이터를 Facility 테이블에 삽입
"""

import csv
import json
from pathlib import Path

def load_facilities_to_database():
    """정제된 주변시설 데이터를 데이터베이스에 로드"""
    
    # 처리된 데이터 파일들
    processed_dir = Path("data/facilities/processed")
    
    files_to_process = [
        ("hospitals_processed.csv", "HOSPITAL"),
        ("convenience_stores_processed.csv", "CONVENIENCE_STORE"),
        ("marts_processed.csv", "MART")
    ]
    
    all_facilities = []
    
    for filename, category in files_to_process:
        file_path = processed_dir / filename
        if not file_path.exists():
            print(f"❌ 파일을 찾을 수 없습니다: {filename}")
            continue
            
        print(f"📄 처리 중: {filename}")
        facilities = load_csv_data(file_path, category)
        all_facilities.extend(facilities)
        print(f"  ✅ {len(facilities)}개 {category} 데이터 로드됨")
    
    if not all_facilities:
        print("❌ 로드할 데이터가 없습니다.")
        return
    
    # SQL 파일 생성
    generate_insert_sql(all_facilities)
    
    print(f"\n🎉 총 {len(all_facilities)}개의 주변시설 데이터 처리 완료!")
    
    # 카테고리별 통계
    category_stats = {}
    for facility in all_facilities:
        cat = facility['category']
        category_stats[cat] = category_stats.get(cat, 0) + 1
    
    print("\n📊 카테고리별 통계:")
    for category, count in category_stats.items():
        print(f"  - {category}: {count}개")

def load_csv_data(file_path, category):
    """CSV 파일에서 데이터 로드"""
    facilities = []
    
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            
            for row in reader:
                facility = {
                    'businessName': row.get('사업장명', '').strip(),
                    'address': row.get('소재지지번주소', '').strip(),
                    'roadAddress': row.get('소재지도로명주소', '').strip(),
                    'businessStatus': row.get('영업상태명', '').strip(),
                    'category': category,
                    'subCategory': determine_subcategory(row, category),
                    'latitude': safe_float(row.get('WGS84위도', '0')),
                    'longitude': safe_float(row.get('WGS84경도', '0')),
                    'phoneNumber': '',  # 원본 데이터에 전화번호가 없음
                    'businessHours': '',  # 원본 데이터에 영업시간이 없음
                    'description': generate_description(row, category)
                }
                
                # 위도/경도가 유효한 경우만 추가
                if facility['latitude'] != 0 and facility['longitude'] != 0:
                    facilities.append(facility)
    
    except Exception as e:
        print(f"❌ 파일 읽기 오류 ({file_path}): {e}")
    
    return facilities

def determine_subcategory(row, category):
    """세부 카테고리 결정"""
    if category == "HOSPITAL":
        # 병원의 경우 의료기관종별명이나 진료과목으로 판단
        medical_type = row.get('의료기관종별명', '').strip()
        departments = row.get('진료과목내용', '').strip()
        
        if '종합병원' in medical_type or '대학병원' in medical_type:
            return '종합병원'
        elif '치과' in medical_type or '치과' in departments:
            return '치과'
        elif '한의원' in medical_type or '한방' in departments:
            return '한의원'
        elif '산부인과' in departments:
            return '산부인과'
        elif '소아' in departments:
            return '소아과'
        else:
            return '의원'
    
    elif category == "CONVENIENCE_STORE":
        # 편의점 브랜드로 판단
        business_name = row.get('사업장명', '').upper()
        if 'CU' in business_name or '씨유' in business_name:
            return 'CU'
        elif 'GS25' in business_name or '지에스' in business_name:
            return 'GS25'
        elif '세븐일레븐' in business_name or '7-ELEVEN' in business_name:
            return '세븐일레븐'
        elif '미니스톱' in business_name or 'MINISTOP' in business_name:
            return '미니스톱'
        elif '이마트24' in business_name or 'EMART24' in business_name:
            return '이마트24'
        else:
            return '편의점'
    
    elif category == "MART":
        # 마트 브랜드로 판단
        business_name = row.get('사업장명', '').upper()
        업태구분 = row.get('업태구분명정보', '').strip()
        
        if '이마트' in business_name:
            return '이마트'
        elif '롯데마트' in business_name or '롯데몰' in business_name:
            return '롯데마트'
        elif '홈플러스' in business_name:
            return '홈플러스'
        elif '코스트코' in business_name or 'COSTCO' in business_name:
            return '코스트코'
        elif '트레이더스' in business_name:
            return '트레이더스'
        elif '백화점' in 업태구분:
            return '백화점'
        elif '쇼핑센터' in 업태구분:
            return '쇼핑센터'
        elif '시장' in 업태구분:
            return '시장'
        else:
            return '대형마트'
    
    return '기타'

def generate_description(row, category):
    """설명 생성"""
    if category == "HOSPITAL":
        departments = row.get('진료과목내용', '').strip()
        if departments:
            return f"진료과목: {departments}"
    
    elif category == "MART":
        업태구분 = row.get('업태구분명정보', '').strip()
        if 업태구분:
            return f"업태: {업태구분}"
    
    return ""

def safe_float(value):
    """안전한 float 변환"""
    try:
        return float(value)
    except (ValueError, TypeError):
        return 0.0

def generate_insert_sql(facilities):
    """SQL INSERT 문 생성"""
    sql_file = Path("data/facilities/processed/insert_facilities.sql")
    
    with open(sql_file, 'w', encoding='utf-8') as f:
        f.write("-- 주변시설 데이터 삽입 SQL\n")
        f.write("-- 생성일: " + str(Path().cwd()) + "\n\n")
        
        # 기존 데이터 삭제 (선택사항)
        f.write("-- 기존 주변시설 데이터 삭제 (필요시 주석 해제)\n")
        f.write("-- DELETE FROM facilities;\n\n")
        
        f.write("-- 주변시설 데이터 삽입\n")
        f.write("INSERT INTO facilities (business_name, address, road_address, business_status, category, sub_category, latitude, longitude, phone_number, business_hours, description, created_at, updated_at) VALUES\n")
        
        for i, facility in enumerate(facilities):
            # SQL 인젝션 방지를 위한 이스케이프
            business_name = facility['businessName'].replace("'", "''")
            address = facility['address'].replace("'", "''")
            road_address = facility['roadAddress'].replace("'", "''")
            business_status = facility['businessStatus'].replace("'", "''")
            sub_category = facility['subCategory'].replace("'", "''")
            description = facility['description'].replace("'", "''")
            
            sql = f"('{business_name}', '{address}', '{road_address}', '{business_status}', '{facility['category']}', '{sub_category}', {facility['latitude']}, {facility['longitude']}, '{facility['phoneNumber']}', '{facility['businessHours']}', '{description}', NOW(), NOW())"
            
            if i < len(facilities) - 1:
                sql += ","
            else:
                sql += ";"
            
            f.write(sql + "\n")
    
    print(f"📁 SQL 파일 생성: {sql_file}")
    print("💡 이 SQL 파일을 데이터베이스에서 실행하세요.")

if __name__ == "__main__":
    load_facilities_to_database()
