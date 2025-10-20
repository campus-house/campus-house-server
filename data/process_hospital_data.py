#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
병원 데이터 처리 스크립트
- 영통동, 서천동만 필터링
- 필요한 컬럼만 추출
- UTF-8로 변환하여 저장
"""

import csv
import os
import re
from pathlib import Path

def process_hospital_data():
    """병원 데이터 처리"""
    input_dir = Path("data/facilities/raw")
    output_dir = Path("data/facilities/processed")
    
    # 출력 디렉토리 생성
    output_dir.mkdir(parents=True, exist_ok=True)
    
    # 처리할 파일들
    files = [
        "병원현황(병원급)_수원시.csv",
        "병원현황(병원급)_용인시.csv"
    ]
    
    print(f"📂 입력 디렉토리: {input_dir}")
    print(f"📂 출력 디렉토리: {output_dir}")
    print(f"📋 처리할 파일들: {files}")
    
    all_hospitals = []
    
    for filename in files:
        file_path = input_dir / filename
        if not file_path.exists():
            print(f"❌ 파일을 찾을 수 없습니다: {filename}")
            continue
            
        print(f"📄 처리 중: {filename}")
        
        try:
            # CP949로 읽기
            with open(file_path, 'r', encoding='cp949') as f:
                reader = csv.DictReader(f)
                
                for row in reader:
                    # 영통동, 서천동만 필터링
                    address = row.get('소재지지번주소', '')
                    if not is_target_area(address):
                        continue
                    
                    # 필요한 컬럼만 추출
                    hospital = {
                        '사업장명': row.get('사업장명', '').strip(),
                        '소재지지번주소': address.strip(),
                        '소재지도로명주소': row.get('소재지도로명주소', '').strip(),
                        '영업상태명': row.get('영업상태명', '').strip(),
                        '의료기관종별명': row.get('의료기관종별명', '').strip(),
                        '진료과목내용': row.get('진료과목내용', '').strip(),
                        'WGS84위도': row.get('WGS84위도', '').strip(),
                        'WGS84경도': row.get('WGS84경도', '').strip(),
                        '지역구분': get_area_type(address)
                    }
                    
                    # 영업중인 병원만 추가
                    if hospital['영업상태명'] == '영업/정상':
                        all_hospitals.append(hospital)
                        print(f"  ✅ 추가: {hospital['사업장명']} ({hospital['지역구분']})")
                
        except Exception as e:
            print(f"❌ 파일 처리 중 오류 발생: {filename} - {e}")
    
    # 결과 저장
    if all_hospitals:
        save_processed_data(all_hospitals, output_dir)
        print(f"\n🎉 처리 완료! 총 {len(all_hospitals)}개 병원 데이터 처리됨")
        
        # 지역별 통계
        영통구_count = len([h for h in all_hospitals if h['지역구분'] == '영통구'])
        팔달구_count = len([h for h in all_hospitals if h['지역구분'] == '팔달구'])
        권선구_count = len([h for h in all_hospitals if h['지역구분'] == '권선구'])
        서천동_count = len([h for h in all_hospitals if h['지역구분'] == '서천동'])
        print(f"  - 영통구: {영통구_count}개")
        print(f"  - 팔달구: {팔달구_count}개")
        print(f"  - 권선구: {권선구_count}개")
        print(f"  - 서천동: {서천동_count}개")
    else:
        print("❌ 처리된 데이터가 없습니다.")

def is_target_area(address):
    """수원시 영통구, 팔달구, 권선구, 용인시 기흥구 서천동인지 확인"""
    if not address:
        return False
    
    # 수원시 영통구 전체 (영통동, 원천동, 망포동, 하동, 이의동 등)
    if '수원시 영통구' in address:
        return True
    
    # 수원시 팔달구 전체 (인계동, 팔달로, 정조로 등)
    if '수원시 팔달구' in address:
        return True
    
    # 수원시 권선구 전체 (세류동, 서둔동, 구운동, 권선동 등)
    if '수원시 권선구' in address:
        return True
    
    # 용인시 기흥구 서천동
    if '용인시 기흥구 서천동' in address or '기흥구 서천동' in address:
        return True
    
    return False

def get_area_type(address):
    """지역 구분 반환"""
    if '수원시 영통구' in address:
        return '영통구'
    elif '수원시 팔달구' in address:
        return '팔달구'
    elif '수원시 권선구' in address:
        return '권선구'
    elif '서천동' in address:
        return '서천동'
    else:
        return '기타'

def save_processed_data(hospitals, output_dir):
    """처리된 데이터 저장"""
    # CSV 파일로 저장
    csv_path = output_dir / "hospitals_processed.csv"
    with open(csv_path, 'w', encoding='utf-8', newline='') as f:
        if hospitals:
            fieldnames = hospitals[0].keys()
            writer = csv.DictWriter(f, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(hospitals)
    
    print(f"📁 CSV 파일 저장: {csv_path}")
    
    # JSON 파일로도 저장 (선택사항)
    import json
    json_path = output_dir / "hospitals_processed.json"
    with open(json_path, 'w', encoding='utf-8') as f:
        json.dump(hospitals, f, ensure_ascii=False, indent=2)
    
    print(f"📁 JSON 파일 저장: {json_path}")

if __name__ == "__main__":
    process_hospital_data()
