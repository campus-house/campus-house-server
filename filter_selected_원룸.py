#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
지정된 줄들에서 room_type이 "원룸"인 항목만 필터링
"""

import pandas as pd

def filter_selected_원룸():
    """지정된 줄들에서 원룸만 필터링"""
    
    # 원본 CSV 파일 읽기
    df = pd.read_csv('data/buildings/processed/buildings_processed.csv')
    
    # 지정된 주소들 (선택된 줄들)
    selected_addresses = [
        "경기도 수원시 영통구 영통동 1012-1",
        "경기도 수원시 영통구 영통동 958-2", 
        "경기도 수원시 영통구 영통동 1024-14",
        "경기도 용인시 기흥구 서천동 7-6",
        "경기도 용인시 기흥구 서천동 399-1",
        "경기도 용인시 기흥구 서천동 265-14"
    ]
    
    print("지정된 주소들의 원룸 건물 필터링:")
    print("=" * 50)
    
    # 지정된 주소들 중에서 원룸인 것만 필터링
    selected_원룸 = df[
        (df['address'].isin(selected_addresses)) & 
        (df['room_type'] == '원룸')
    ].copy()
    
    print(f"지정된 주소 수: {len(selected_addresses)}개")
    print(f"원룸 건물 수: {len(selected_원룸)}개")
    
    if len(selected_원룸) > 0:
        # 결과를 CSV 파일로 저장
        output_file = 'data/buildings/processed/selected_원룸만.csv'
        selected_원룸.to_csv(output_file, index=False, encoding='utf-8-sig')
        
        print(f"\n원룸 건물 데이터가 '{output_file}'에 저장되었습니다.")
        
        # 각 건물 정보 출력
        print(f"\n원룸 건물 상세 정보:")
        print("-" * 50)
        
        for _, building in selected_원룸.iterrows():
            print(f"🏢 {building['building_name']}")
            print(f"   주소: {building['address']}")
            print(f"   도로명: {building['road_name']}")
            print(f"   면적: {building['area']}㎡")
            print(f"   보증금: {building['avg_deposit']:,.0f}만원")
            print(f"   월세: {building['avg_monthly_rent']:,.0f}만원")
            print(f"   건설년도: {building['construction_year']}년")
            print(f"   샘플 수: {building['sample_count']}개")
            print()
        
        # 통계 정보
        print(f"통계 정보:")
        print(f"  - 평균 면적: {selected_원룸['area'].mean():.2f}㎡")
        print(f"  - 평균 보증금: {selected_원룸['avg_deposit'].mean():,.0f}만원")
        print(f"  - 평균 월세: {selected_원룸['avg_monthly_rent'].mean():,.0f}만원")
        print(f"  - 평균 건설년도: {selected_원룸['construction_year'].mean():.0f}년")
        
        # 지역별 분포
        region_counts = selected_원룸['address'].str.split(' ').str[:3].str.join(' ').value_counts()
        print(f"\n지역별 분포:")
        for region, count in region_counts.items():
            print(f"  {region}: {count}개")
    
    else:
        print("지정된 주소들에서 원룸 건물을 찾을 수 없습니다.")
        
        # 지정된 주소들의 모든 건물 확인
        print(f"\n지정된 주소들의 모든 건물:")
        all_selected = df[df['address'].isin(selected_addresses)]
        for _, building in all_selected.iterrows():
            print(f"  - {building['building_name']}: {building['room_type']}")

if __name__ == "__main__":
    filter_selected_원룸()
