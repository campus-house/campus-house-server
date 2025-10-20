#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
건물 데이터 정제 및 변환 스크립트
CSV 파일들을 읽어서 Building 엔티티에 맞는 형태로 변환
"""

import pandas as pd
import numpy as np
import os
import re
from typing import Dict, List, Any
import json

class BuildingDataProcessor:
    def __init__(self, raw_data_path: str = "data/buildings/raw/", 
                 processed_data_path: str = "data/buildings/processed/"):
        self.raw_data_path = raw_data_path
        self.processed_data_path = processed_data_path
        self.buildings = {}
        
    def process_all_files(self):
        """모든 CSV 파일을 처리"""
        print("🏢 건물 데이터 처리 시작...")
        
        # 파일 목록
        files = [
            "경기부동산포털_건물_표제부.csv",
            "단독다가구(전월세)_실거래가_20251019153905_영통동.csv",
            "단독다가구(전월세)_실거래가_20251019153939_서천동.csv",
            "아파트(전월세)_실거래가_20251019154009_서천동.csv",
            "아파트(전월세)_실거래가_20251019154026_영통동.csv",
            "오피스텔(전월세)_실거래가_20251019153748_영통동.csv",
            "오피스텔(전월세)_실거래가_20251019153832_서천동.csv"
        ]
        
        for file_name in files:
            file_path = os.path.join(self.raw_data_path, file_name)
            if os.path.exists(file_path):
                print(f"📄 처리 중: {file_name}")
                self.process_file(file_path, file_name)
            else:
                print(f"❌ 파일을 찾을 수 없습니다: {file_name}")
        
        # 결과 저장
        self.save_processed_data()
        print(f"✅ 처리 완료! 총 {len(self.buildings)}개 건물 데이터 생성")
        
    def process_file(self, file_path: str, file_name: str):
        """개별 파일 처리"""
        try:
            # 파일명으로 건물 타입 판단
            building_type = self.determine_building_type(file_name)
            
            # CSV 파일 읽기 (여러 인코딩 시도)
            df = None
            for encoding in ['cp949', 'euc-kr', 'utf-8', 'utf-8-sig', 'iso-8859-1', 'latin1']:
                try:
                    df = pd.read_csv(file_path, encoding=encoding, on_bad_lines='skip')
                    print(f"  ✅ 인코딩 성공: {encoding}")
                    break
                except Exception as e:
                    print(f"  ❌ 인코딩 실패: {encoding} - {str(e)[:50]}")
                    continue
            
            if df is None:
                print(f"  ❌ 인코딩 실패: {file_name}")
                return
            
            # 데이터 정제
            if building_type == "표제부":
                self.process_building_info(df, file_name)
            else:
                self.process_transaction_data(df, building_type, file_name)
                
        except Exception as e:
            print(f"  ❌ 파일 처리 오류: {file_name} - {str(e)}")
    
    def determine_building_type(self, file_name: str) -> str:
        """파일명으로 건물 타입 판단"""
        if "아파트" in file_name:
            return "아파트"
        elif "오피스텔" in file_name:
            return "오피스텔"
        elif "단독다가구" in file_name:
            return "단독다가구"
        elif "표제부" in file_name:
            return "표제부"
        return "기타"
    
    def process_building_info(self, df: pd.DataFrame, file_name: str):
        """건물 표제부 데이터 처리"""
        print(f"  🏢 건물 정보 처리: {len(df)}개 레코드")
        # 표제부 데이터는 인코딩 문제로 추후 처리
        pass
    
    def process_transaction_data(self, df: pd.DataFrame, building_type: str, file_name: str):
        """실거래가 데이터 처리"""
        print(f"  💰 실거래가 데이터 처리: {len(df)}개 레코드")
        
        # 16번째 줄부터 읽기 (실제 데이터 시작)
        if len(df) > 16:
            df = df.iloc[16:].reset_index(drop=True)
            print(f"  📊 16번째 줄부터 데이터 시작")
        else:
            print(f"  ❌ 데이터가 충분하지 않습니다")
            return
        
        # 컬럼명 정리 (실제 데이터 구조에 맞게)
        if len(df.columns) >= 15:
            df.columns = [
                'NO', '시군구', '번지', '본번', '부번', '단지명', '전월세구분', '전용면적', 
                '계약년월', '계약일', '보증금', '월세금', '층', '건축년도', '도로명', 
                '계약기간', '계약구분', '갱신요구권', '종전보증금', '종전월세'
            ]
        
        # 데이터 처리
        processed_count = 0
        for i, row in df.iterrows():
            try:
                if i < 3:  # 처음 3개 행만 디버깅
                    print(f"    🔍 디버깅 - 행 {i}: {row.to_dict()}")
                
                building_data = self.extract_building_from_transaction(row, building_type)
                if building_data:
                    key = building_data['building_key']
                    if key in self.buildings:
                        # 기존 데이터와 병합
                        self.merge_building_data(self.buildings[key], building_data)
                    else:
                        self.buildings[key] = building_data
                    processed_count += 1
                    if processed_count <= 3:  # 처음 3개만 출력
                        print(f"    ✅ 건물 데이터 추출 성공: {building_data['building_name']}")
            except Exception as e:
                print(f"    ❌ 데이터 추출 오류: {str(e)[:50]}")
                continue
        
        print(f"  📊 처리된 건물: {processed_count}개")
    
    def extract_building_from_transaction(self, row: pd.Series, building_type: str) -> Dict[str, Any]:
        """실거래가 데이터에서 건물 정보 추출"""
        try:
            # 기본 정보
            building_name = str(row.get('단지명', '')).strip()
            if not building_name or building_name == 'nan' or building_name == '':
                return None
            
            # 주소 구성
            sigungu = str(row.get('시군구', '')).strip()
            jibun = str(row.get('번지', '')).strip()
            address = f"{sigungu} {jibun}".strip()
            
            # 면적
            area = 0
            try:
                area_str = str(row.get('전용면적', '')).strip()
                if area_str and area_str != 'nan':
                    area = float(area_str)
            except:
                pass
            
            # 가격 (보증금 + 월세)
            price = 0
            try:
                deposit_str = str(row.get('보증금', '')).strip().replace(',', '')
                monthly_str = str(row.get('월세금', '')).strip().replace(',', '')
                if deposit_str and deposit_str != 'nan':
                    deposit = int(deposit_str)
                    monthly = int(monthly_str) if monthly_str and monthly_str != 'nan' else 0
                    price = deposit + monthly  # 보증금 + 월세로 총 가격 계산
            except:
                pass
            
            # 층수
            floor = 0
            try:
                floor_str = str(row.get('층', '')).strip()
                if floor_str and floor_str != 'nan':
                    floor = int(floor_str)
            except:
                pass
            
            # 건축년도
            construction_year = 0
            try:
                year_str = str(row.get('건축년도', '')).strip()
                if year_str and year_str != 'nan':
                    construction_year = int(year_str)
            except:
                pass
            
            # 도로명
            road_name = str(row.get('도로명', '')).strip()
            
            building_data = {
                'building_name': building_name,
                'address': address,
                'building_type': building_type,
                'area': area,
                'prices': [price] if price > 0 else [],
                'floor': floor,
                'construction_year': construction_year,
                'road_name': road_name,
                'building_key': f"{building_name}_{address}"
            }
            
            return building_data
            
        except Exception as e:
            print(f"    ❌ 건물 데이터 추출 오류: {str(e)[:50]}")
            return None
    
    def merge_building_data(self, existing: Dict[str, Any], new: Dict[str, Any]):
        """건물 데이터 병합"""
        # 가격 정보 추가
        if new['prices']:
            existing['prices'].extend(new['prices'])
        
        # 기타 정보 업데이트 (더 상세한 정보가 있으면)
        if new['area'] > 0 and existing['area'] == 0:
            existing['area'] = new['area']
        if new['floor'] > 0 and existing['floor'] == 0:
            existing['floor'] = new['floor']
        if new['construction_year'] > 0 and existing['construction_year'] == 0:
            existing['construction_year'] = new['construction_year']
    
    def save_processed_data(self):
        """정제된 데이터 저장"""
        # CSV로 저장
        csv_data = []
        for i, (key, building) in enumerate(self.buildings.items(), 1):
            avg_price = np.mean(building['prices']) if building['prices'] else 0
            csv_data.append({
                'id': i,
                'building_name': building['building_name'],
                'address': building['address'],
                'building_type': building['building_type'],
                'area': building['area'],
                'avg_price': avg_price,
                'floor': building['floor'],
                'construction_year': building['construction_year'],
                'road_name': building['road_name'],
                'sample_count': len(building['prices'])
            })
        
        # CSV 저장
        df = pd.DataFrame(csv_data)
        csv_path = os.path.join(self.processed_data_path, 'buildings_processed.csv')
        df.to_csv(csv_path, index=False, encoding='utf-8-sig')
        print(f"📊 CSV 저장 완료: {csv_path}")
        
        # JSON으로도 저장 (Java에서 사용)
        json_path = os.path.join(self.processed_data_path, 'buildings_processed.json')
        with open(json_path, 'w', encoding='utf-8') as f:
            json.dump(csv_data, f, ensure_ascii=False, indent=2)
        print(f"📄 JSON 저장 완료: {json_path}")
        
        # 통계 출력
        print(f"\n📈 처리 결과:")
        print(f"  - 총 건물 수: {len(self.buildings)}")
        print(f"  - 아파트: {len([b for b in self.buildings.values() if b['building_type'] == '아파트'])}")
        print(f"  - 오피스텔: {len([b for b in self.buildings.values() if b['building_type'] == '오피스텔'])}")
        print(f"  - 단독다가구: {len([b for b in self.buildings.values() if b['building_type'] == '단독다가구'])}")

def main():
    processor = BuildingDataProcessor()
    processor.process_all_files()

if __name__ == "__main__":
    main()
