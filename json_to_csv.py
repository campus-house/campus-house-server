#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
JSON 데이터를 CSV 파일로 변환하는 스크립트
"""

import json
import csv
import os

def convert_json_to_csv(json_file_path, csv_file_path):
    """
    JSON 파일을 CSV 파일로 변환
    
    Args:
        json_file_path (str): 입력 JSON 파일 경로
        csv_file_path (str): 출력 CSV 파일 경로
    """
    try:
        # JSON 파일 읽기
        with open(json_file_path, 'r', encoding='utf-8') as json_file:
            data = json.load(json_file)
        
        if not data:
            print("JSON 파일이 비어있습니다.")
            return
        
        # CSV 파일 작성
        with open(csv_file_path, 'w', newline='', encoding='utf-8-sig') as csv_file:
            # 첫 번째 객체의 키를 컬럼 헤더로 사용
            fieldnames = data[0].keys()
            writer = csv.DictWriter(csv_file, fieldnames=fieldnames)
            
            # 헤더 작성
            writer.writeheader()
            
            # 데이터 행 작성
            for row in data:
                writer.writerow(row)
        
        print(f"성공적으로 변환되었습니다: {csv_file_path}")
        print(f"총 {len(data)}개의 레코드가 변환되었습니다.")
        
    except FileNotFoundError:
        print(f"JSON 파일을 찾을 수 없습니다: {json_file_path}")
    except json.JSONDecodeError:
        print(f"JSON 파일 형식이 올바르지 않습니다: {json_file_path}")
    except Exception as e:
        print(f"변환 중 오류가 발생했습니다: {str(e)}")

if __name__ == "__main__":
    # 파일 경로 설정
    json_file = "src/main/resources/data/buildings/processed/buildings_processed.json"
    csv_file = "buildings_data.csv"
    
    # JSON을 CSV로 변환
    convert_json_to_csv(json_file, csv_file)
