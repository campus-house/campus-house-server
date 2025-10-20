#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
간단한 건물 데이터 정제 스크립트
"""

import csv
import json
import os
from collections import defaultdict

def process_building_files():
    """건물 파일들을 처리"""
    raw_path = "data/buildings/raw/"
    processed_path = "data/buildings/processed/"
    
    buildings = {}
    building_info = {}  # 건물 기본 정보 저장
    
    # 1. 표제부 데이터 처리 (건물 기본 정보)
    print("📄 건물 표제부 데이터 처리 중...")
    process_building_info_file(raw_path + "표제부_영통.csv", building_info)
    process_building_info_file(raw_path + "표제부_서천.csv", building_info)
    
    # 2. 실거래가 파일들 처리
    transaction_files = [
        "단독다가구(전월세)_실거래가_20251019153905_영통동.csv",
        "단독다가구(전월세)_실거래가_20251019153939_서천동.csv", 
        "아파트(전월세)_실거래가_20251019154009_서천동.csv",
        "아파트(전월세)_실거래가_20251019154026_영통동.csv",
        "오피스텔(전월세)_실거래가_20251019153748_영통동.csv",
        "오피스텔(전월세)_실거래가_20251019153832_서천동.csv"
    ]
    
    for file_name in transaction_files:
        file_path = os.path.join(raw_path, file_name)
        if os.path.exists(file_path):
            print(f"📄 처리 중: {file_name}")
            process_transaction_file(file_path, file_name, buildings, building_info)
    
    # 결과 저장
    save_results(buildings, processed_path)
    print(f"✅ 처리 완료! 총 {len(buildings)}개 건물 데이터 생성")

def process_building_info_file(file_path, building_info):
    """건물 표제부 파일 처리 - 요청된 컬럼만 추출"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            lines = f.readlines()
        
        print(f"    📊 파일 읽기 완료: {len(lines)}줄")
        
        # 헤더 스킵하고 데이터 처리
        processed_count = 0
        for i, line in enumerate(lines[1:], 1):
            try:
                reader = csv.reader([line])
                row = next(reader)
                
                if len(row) < 70:  # 최소 컬럼 수 체크
                    continue
                
                # 요청된 컬럼들만 추출 (실제 CSV 구조에 맞춤)
                location = row[0].strip()  # 대지위치 (1번째)
                road_address = row[11].strip()  # 도로명대지위치 (12번째)
                building_name = row[12].strip()  # 건물명 (13번째)
                dong_name = row[22].strip()  # 동명칭 (23번째)
                structure_name = row[32].strip()  # 구조코드명 (33번째)
                main_use_name = row[35].strip()  # 주용도코드명 (36번째)
                ground_floors = safe_int(row[43])  # 지상층수 (44번째)
                basement_floors = safe_int(row[44])  # 지하층수 (45번째)
                elevators = safe_int(row[45])  # 승용승강기수 (46번째)
                site_area = safe_float(row[25])  # 대지면적(㎡) (26번째)
                building_area = safe_float(row[26])  # 건축면적(㎡) (27번째)
                coverage_ratio = safe_float(row[27])  # 건폐율(%) (28번째)
                total_floor_area = safe_float(row[28])  # 연면적(㎡) (29번째)
                floor_area_ratio = safe_float(row[30])  # 용적률(%) (31번째)
                households = safe_int(row[40])  # 세대수(세대) (41번째)
                families = safe_int(row[41])  # 가구수(가구) (42번째)
                units = safe_int(row[66])  # 호수(호) (67번째)
                permit_date = row[58].strip() if len(row) > 58 else ''  # 허가일 (59번째)
                start_date = row[59].strip() if len(row) > 59 else ''  # 착공일 (60번째)
                approval_date = row[60].strip() if len(row) > 60 else ''  # 사용승인일 (61번째)
                
                # 건물명이 없는 경우 주소로 생성
                if not building_name or building_name == '':
                    building_name = f"건물_{location.split()[-1]}"
                
                # 건물 정보 저장 (요청된 컬럼만)
                building_key = f"{building_name}_{location}"
                building_info[building_key] = {
                    'building_name': building_name,
                    'address': location,
                    'road_address': road_address,
                    'dong_name': dong_name,
                    'structure_name': structure_name,
                    'main_use_name': main_use_name,
                    'ground_floors': ground_floors,
                    'basement_floors': basement_floors,
                    'elevators': elevators,
                    'site_area': site_area,
                    'building_area': building_area,
                    'coverage_ratio': coverage_ratio,
                    'total_floor_area': total_floor_area,
                    'floor_area_ratio': floor_area_ratio,
                    'households': households,
                    'families': families,
                    'units': units,
                    'permit_date': permit_date,
                    'start_date': start_date,
                    'approval_date': approval_date,
                }
                
                processed_count += 1
                if processed_count <= 3:  # 처음 3개만 디버깅
                    print(f"    ✅ 건물 처리: {building_name} ({location})")
                
            except Exception as e:
                if i <= 3:  # 처음 3개만 디버깅
                    print(f"    ❌ 행 {i} 처리 오류: {str(e)[:50]}")
                continue
        
        print(f"  ✅ 건물 기본 정보 {processed_count}개 처리 완료")
        
    except Exception as e:
        print(f"  ❌ 표제부 파일 처리 오류: {str(e)}")

def process_transaction_file(file_path, file_name, buildings, building_info):
    """실거래가 파일 처리"""
    building_type = get_building_type(file_name)
    
    try:
        with open(file_path, 'r', encoding='cp949') as f:
            lines = f.readlines()
        
        # 16번째 줄부터 데이터 시작 (헤더)
        data_lines = lines[16:]
        
        for line in data_lines:
            try:
                # CSV 파싱
                reader = csv.reader([line])
                row = next(reader)
                
                if len(row) < 15:
                    continue
                
                # 단독다가구와 아파트/오피스텔의 컬럼 구조가 다름
                if building_type == "단독다가구":
                    # 단독다가구: NO, 시군구, 번지, 도로조건, 계약면적, 전월세구분, 계약년월, 계약일, 보증금, 월세금, 건축년도, 도로명, ...
                    building_name = f"단독다가구_{row[2].strip()}"  # 번지로 건물명 생성
                    sigungu = row[1].strip()  # 시군구
                    jibun = row[2].strip()    # 번지
                    address = f"{sigungu} {jibun}"
                    
                    # 면적
                    area = 0
                    try:
                        area = float(row[4].strip())  # 계약면적
                    except:
                        pass
                    
                    # 가격 (보증금, 월세 분리)
                    deposit = 0
                    monthly = 0
                    try:
                        deposit = int(row[8].replace(',', ''))  # 보증금
                        monthly = int(row[9].replace(',', ''))  # 월세금
                    except:
                        pass
                    
                    # 층수 (단독다가구는 층수 정보 없음)
                    floor = 1  # 기본값
                    
                    # 건축년도
                    construction_year = 0
                    try:
                        construction_year = int(row[10].strip())
                    except:
                        pass
                    
                    # 도로명
                    road_name = row[11].strip()
                else:
                    # 아파트/오피스텔: NO, 시군구, 번지, 본번, 부번, 단지명, 전월세구분, 전용면적, 계약년월, 계약일, 보증금, 월세금, 층, 건축년도, 도로명, ...
                    building_name = row[5].strip()  # 단지명
                    if not building_name or building_name == '' or building_name in ['전세', '월세']:
                        continue
                    
                    sigungu = row[1].strip()  # 시군구
                    jibun = row[2].strip()    # 번지
                    address = f"{sigungu} {jibun}"
                    
                    # 면적
                    area = 0
                    try:
                        area = float(row[7].strip())  # 전용면적
                    except:
                        pass
                    
                    # 가격 (보증금, 월세 분리)
                    deposit = 0
                    monthly = 0
                    try:
                        deposit = int(row[10].replace(',', ''))  # 보증금
                        monthly = int(row[11].replace(',', ''))  # 월세금
                    except:
                        pass
                    
                    # 층수
                    floor = 0
                    try:
                        floor = int(row[12].strip())
                    except:
                        pass
                    
                    # 건축년도
                    construction_year = 0
                    try:
                        construction_year = int(row[13].strip())
                    except:
                        pass
                    
                    # 도로명
                    road_name = row[14].strip()
                
                # 방 타입 구분 (면적 기반)
                room_type = get_room_type(area, building_type)
                
                # 건물 키 (건물명 + 주소 + 방타입으로 구분)
                building_key = f"{building_name}_{address}_{room_type}"
                
                # 건물 기본 정보 찾기
                basic_info = find_building_info(building_name, address, building_info)
                
                # 디버깅: 매칭 결과 확인
                if basic_info and (basic_info.get('households', 0) > 0 or basic_info.get('ground_floors', 0) > 0):
                    print(f"    ✅ 매칭 성공: {building_name} -> 세대수:{basic_info.get('households', 0)}, 층수:{basic_info.get('ground_floors', 0)}")
                
                if building_key in buildings:
                    # 기존 데이터와 병합
                    buildings[building_key]['deposits'].append(deposit)
                    buildings[building_key]['monthly_rents'].append(monthly)
                    if area > 0 and buildings[building_key]['area'] == 0:
                        buildings[building_key]['area'] = area
                    if floor > 0 and buildings[building_key]['floor'] == 0:
                        buildings[building_key]['floor'] = floor
                    if construction_year > 0 and buildings[building_key]['construction_year'] == 0:
                        buildings[building_key]['construction_year'] = construction_year
                else:
                    # 새 건물 데이터 (기본 정보 포함)
                    buildings[building_key] = {
                        'building_name': building_name,
                        'address': address,
                        'building_type': building_type,
                        'room_type': room_type,
                        'area': area,
                        'deposits': [deposit] if deposit > 0 else [],
                        'monthly_rents': [monthly] if monthly > 0 else [],
                        'floor': floor,
                        'construction_year': construction_year,
                        'road_name': road_name,
                        # 건물 기본 정보 추가
                        'households': basic_info.get('households', 0),
                        'ground_floors': basic_info.get('ground_floors', 0),
                        'elevators': basic_info.get('elevators', 0),
                        'emergency_elevators': basic_info.get('emergency_elevators', 0),
                        'main_use_code': basic_info.get('main_use_code', ''),
                        'approval_date': basic_info.get('approval_date', ''),
                        'completion_date': basic_info.get('approval_date', ''),  # 사용승인일을 준공일로 사용
                        'heating_type': get_heating_type(basic_info.get('main_use_code', '')),
                        'building_usage': get_building_usage(basic_info.get('main_use_code', '')),
                        'parking_spaces': 0,  # 주차장 데이터 없음
                    }
                    
            except Exception as e:
                continue
        
        print(f"  ✅ 처리 완료: {len([b for b in buildings.values() if b['building_type'] == building_type])}개 건물")
        
    except Exception as e:
        print(f"  ❌ 파일 처리 오류: {str(e)}")

def get_building_type(file_name):
    """파일명으로 건물 타입 판단"""
    if "아파트" in file_name:
        return "아파트"
    elif "오피스텔" in file_name:
        return "오피스텔"
    elif "단독다가구" in file_name:
        return "단독다가구"
    return "기타"

def safe_float(value):
    """안전한 float 변환"""
    try:
        if value and str(value).strip():
            return float(str(value).strip())
        return 0.0
    except:
        return 0.0

def safe_int(value):
    """안전한 int 변환"""
    try:
        if value and str(value).strip():
            return int(float(str(value).strip()))
        return 0
    except:
        return 0

def normalize_address(address):
    """주소 정규화"""
    # "경기도 수원시 영통구 영통동 1153" -> "수원시영통구 영통동 1153"
    if "경기도" in address:
        return address.replace("경기도 ", "")
    return address

def find_building_info(building_name, address, building_info):
    """건물 기본 정보 찾기 (주소 기반 매칭)"""
    # 1. 정확한 매칭 시도 (건물명 + 주소)
    key = f"{building_name}_{address}"
    if key in building_info:
        return building_info[key]
    
    # 2. 주소 기반 매칭 (지번 주소 추출 및 정규화)
    address_parts = address.split()
    if len(address_parts) >= 3:
        # "경기도 수원시 영통구 영통동 1153" -> "영통동 1153"
        dong_ho = f"{address_parts[-2]} {address_parts[-1]}"
        
        # 정규화된 주소로 매칭
        normalized_address = normalize_address(address)
        
        for info_key, info in building_info.items():
            info_address = info.get('address', '')
            if dong_ho in info_address or normalized_address in info_address:
                return info
    
    # 3. 부분 매칭 시도 (건물명만으로)
    for info_key, info in building_info.items():
        if building_name in info_key:
            return info
    
    # 4. 기본값 반환
    return {}

def get_heating_type(main_use_code):
    """주용도코드에 따른 난방방식 추정"""
    if main_use_code in ['01000', '02000']:  # 주거용
        return "개별난방"
    elif main_use_code in ['04000', '05000']:  # 사무용, 상업용
        return "중앙난방"
    else:
        return "개별난방"

def get_building_usage(main_use_code):
    """주용도코드에 따른 건물 용도 설명"""
    usage_map = {
        '01000': '주거용',
        '02000': '공동주택',
        '03000': '숙박시설',
        '04000': '사무용',
        '05000': '상업용',
        '06000': '업무시설',
        '07000': '위락시설',
        '08000': '집회시설',
        '09000': '종교시설',
        '10000': '교육연구시설',
        '11000': '의료시설',
        '12000': '노유자시설',
        '13000': '수련시설',
        '14000': '운동시설',
        '15000': '창고시설',
        '16000': '위험물저장시설',
        '17000': '자동차관련시설',
        '18000': '동물관련시설',
        '19000': '기타'
    }
    return usage_map.get(main_use_code, '기타')

def get_room_type(area, building_type):
    """면적과 건물 타입에 따라 방 타입 구분"""
    if building_type == "단독다가구":
        if area < 30:
            return "원룸형"
        elif area < 50:
            return "투룸형"
        else:
            return "쓰리룸형"
    elif building_type == "오피스텔":
        if area < 20:
            return "미니원룸"
        elif area < 30:
            return "원룸"
        elif area < 40:
            return "투룸"
        else:
            return "쓰리룸"
    else:  # 아파트
        # 평형 계산 (1평 = 3.3058㎡)
        pyeong = area / 3.3058
        if pyeong < 10:
            return f"{int(pyeong)}평형"
        elif pyeong < 15:
            return f"{int(pyeong)}평형"
        elif pyeong < 20:
            return f"{int(pyeong)}평형"
        elif pyeong < 25:
            return f"{int(pyeong)}평형"
        elif pyeong < 30:
            return f"{int(pyeong)}평형"
        else:
            return f"{int(pyeong)}평형"

def save_results(buildings, processed_path):
    """결과 저장"""
    # CSV로 저장
    csv_data = []
    for i, (key, building) in enumerate(buildings.items(), 1):
        # 평균 보증금과 월세 계산
        avg_deposit = sum(building['deposits']) / len(building['deposits']) if building['deposits'] else 0
        avg_monthly = sum(building['monthly_rents']) / len(building['monthly_rents']) if building['monthly_rents'] else 0
        
        csv_data.append({
            'id': i,
            'building_name': building['building_name'],
            'address': building['address'],
            'building_type': building['building_type'],
            'room_type': building['room_type'],
            'area': building['area'],
            'avg_deposit': avg_deposit,
            'avg_monthly_rent': avg_monthly,
            'construction_year': building['construction_year'],
            'road_name': building['road_name'],
            'sample_count': len(building['deposits']),
            # 건물 기본 정보 추가
            'households': building.get('households', 0),
            'ground_floors': building.get('ground_floors', 0),
            'elevators': building.get('elevators', 0),
            'emergency_elevators': building.get('emergency_elevators', 0),
            'main_use_code': building.get('main_use_code', ''),
            'building_usage': building.get('building_usage', ''),
            'heating_type': building.get('heating_type', ''),
            'approval_date': building.get('approval_date', ''),
            'completion_date': building.get('completion_date', ''),
            'parking_spaces': building.get('parking_spaces', 0)
        })
    
    # CSV 저장
    csv_path = os.path.join(processed_path, 'buildings_processed.csv')
    with open(csv_path, 'w', encoding='utf-8-sig', newline='') as f:
        if csv_data:
            writer = csv.DictWriter(f, fieldnames=csv_data[0].keys())
            writer.writeheader()
            writer.writerows(csv_data)
    
    # JSON 저장
    json_path = os.path.join(processed_path, 'buildings_processed.json')
    with open(json_path, 'w', encoding='utf-8') as f:
        json.dump(csv_data, f, ensure_ascii=False, indent=2)
    
    print(f"📊 CSV 저장 완료: {csv_path}")
    print(f"📄 JSON 저장 완료: {json_path}")
    
    # 통계 출력
    print(f"\n📈 처리 결과:")
    print(f"  - 총 건물 수: {len(buildings)}")
    for building_type in ['아파트', '오피스텔', '단독다가구']:
        count = len([b for b in buildings.values() if b['building_type'] == building_type])
        print(f"  - {building_type}: {count}개")

if __name__ == "__main__":
    process_building_files()
