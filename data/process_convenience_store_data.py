import csv
import json
from pathlib import Path

def safe_float(value):
    try:
        return float(value)
    except (ValueError, TypeError):
        return 0.0

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

def process_convenience_store_data():
    input_dir = Path("data/facilities/raw")
    output_dir = Path("data/facilities/processed")
    
    output_dir.mkdir(parents=True, exist_ok=True)
    
    files = [
        "휴게음식점(편의점)현황_수원시.csv",
        "휴게음식점(편의점)현황_용인시.csv"
    ]
    
    print(f"📂 입력 디렉토리: {input_dir}")
    print(f"📂 출력 디렉토리: {output_dir}")
    print(f"📋 처리할 파일들: {files}")
    
    all_convenience_stores = []
    
    for filename in files:
        file_path = input_dir / filename
        print(f"📄 처리 중: {filename}")
        
        if not file_path.exists():
            print(f"❌ 파일을 찾을 수 없습니다: {file_path}")
            continue
        
        try:
            # EUC-KR 인코딩으로 파일 읽기
            with open(file_path, 'r', encoding='euc-kr') as f:
                reader = csv.reader(f)
                header = next(reader) # 헤더 읽기
                
                # 필요한 컬럼 인덱스 찾기
                col_map = {
                    '사업장명': header.index('사업장명'),
                    '소재지지번주소': header.index('소재지지번주소'),
                    '소재지도로명주소': header.index('소재지도로명주소'),
                    '영업상태명': header.index('영업상태명'),
                    '위생업태명': header.index('위생업태명'),
                    'WGS84위도': header.index('WGS84위도'),
                    'WGS84경도': header.index('WGS84경도')
                }
                
                for row in reader:
                    address = row[col_map['소재지지번주소']]
                    
                    # 수원시 영통구, 팔달구, 권선구, 용인시 기흥구 서천동 필터링
                    if (("수원시 영통구" in address) or 
                        ("수원시 팔달구" in address) or 
                        ("수원시 권선구" in address) or 
                        ("용인시 기흥구 서천동" in address)):
                        convenience_store_data = {
                            '사업장명': row[col_map['사업장명']],
                            '소재지지번주소': address,
                            '소재지도로명주소': row[col_map['소재지도로명주소']],
                            '영업상태명': row[col_map['영업상태명']],
                            '위생업태명': row[col_map['위생업태명']],
                            'WGS84위도': safe_float(row[col_map['WGS84위도']]),
                            'WGS84경도': safe_float(row[col_map['WGS84경도']]),
                            '지역구분': get_area_type(address)
                        }
                        all_convenience_stores.append(convenience_store_data)
                        print(f"  ✅ 추가: {convenience_store_data['사업장명']} ({convenience_store_data['지역구분']})")
        except Exception as e:
            print(f"❌ 파일 처리 중 오류 발생 ({file_path}): {e}")
    
    if not all_convenience_stores:
        print("❌ 처리된 데이터가 없습니다.")
        return
        
    # 결과 CSV 저장
    output_csv_path = output_dir / "convenience_stores_processed.csv"
    with open(output_csv_path, 'w', encoding='utf-8', newline='') as f:
        fieldnames = all_convenience_stores[0].keys()
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(all_convenience_stores)
    print(f"📁 CSV 파일 저장: {output_csv_path}")
    
    # 결과 JSON 저장
    output_json_path = output_dir / "convenience_stores_processed.json"
    with open(output_json_path, 'w', encoding='utf-8') as f:
        json.dump(all_convenience_stores, f, ensure_ascii=False, indent=2)
    print(f"📁 JSON 파일 저장: {output_json_path}")
    
    print(f"\n🎉 처리 완료! 총 {len(all_convenience_stores)}개 편의점 데이터 처리됨")
    
    # 지역별 개수 출력
    영통구_count = sum(1 for c in all_convenience_stores if c['지역구분'] == '영통구')
    팔달구_count = sum(1 for c in all_convenience_stores if c['지역구분'] == '팔달구')
    권선구_count = sum(1 for c in all_convenience_stores if c['지역구분'] == '권선구')
    서천동_count = sum(1 for c in all_convenience_stores if c['지역구분'] == '서천동')
    print(f"  - 영통구: {영통구_count}개")
    print(f"  - 팔달구: {팔달구_count}개")
    print(f"  - 권선구: {권선구_count}개")
    print(f"  - 서천동: {서천동_count}개")

if __name__ == "__main__":
    process_convenience_store_data()
