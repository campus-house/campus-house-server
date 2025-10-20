import csv
from pathlib import Path
from datetime import datetime

def generate_sql_inserts():
    processed_csv_path = Path("data/facilities/processed/convenience_stores_processed.csv")
    output_sql_path = Path("data/facilities/processed/insert_convenience_stores.sql")
    
    if not processed_csv_path.exists():
        print(f"❌ 처리된 CSV 파일을 찾을 수 없습니다: {processed_csv_path}")
        return
        
    print(f"📄 데이터 파일 읽기: {processed_csv_path}")
    
    convenience_stores = []
    with open(processed_csv_path, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            convenience_stores.append(row)
    
    print(f"📊 총 {len(convenience_stores)}개 편의점 데이터 로드됨")
    
    sql_statements = []
    sql_statements.append(f"-- 편의점 데이터 INSERT SQL")
    sql_statements.append(f"-- 생성일: {datetime.now()}")
    sql_statements.append(f"\nINSERT INTO facilities (\n    business_name, \n    address, \n    road_address, \n    business_status, \n    category, \n    sub_category, \n    latitude, \n    longitude, \n    description\n) VALUES ")
    
    values_list = []
    for i, store in enumerate(convenience_stores):
        business_name = store.get('사업장명', '').replace("'", "''")
        address = store.get('소재지지번주소', '').replace("'", "''")
        road_address = store.get('소재지도로명주소', '').replace("'", "''")
        business_status = store.get('영업상태명', '').replace("'", "''")
        business_type = store.get('위생업태명', '').replace("'", "''")
        latitude = store.get('WGS84위도', 0.0)
        longitude = store.get('WGS84경도', 0.0)
        
        # 카테고리 및 서브 카테고리 분류
        category = "CONVENIENCE_STORE"
        sub_category = "편의점"
        
        # 브랜드별 서브 카테고리 분류
        if "세븐일레븐" in business_name or "7-ELEVEN" in business_name:
            sub_category = "세븐일레븐"
        elif "씨유" in business_name or "CU" in business_name:
            sub_category = "CU"
        elif "GS25" in business_name or "지에스25" in business_name:
            sub_category = "GS25"
        elif "미니스톱" in business_name:
            sub_category = "미니스톱"
        
        values_list.append(
            f"(\n    '{business_name}',\n    '{address}',\n    '{road_address}',\n    '{business_status}',\n    '{category}',\n    '{sub_category}',\n    {latitude},\n    {longitude},\n    '{business_type}'\n)"
        )
    
    sql_statements.append(",\n".join(values_list) + ";")
    
    with open(output_sql_path, 'w', encoding='utf-8') as f:
        f.write("\n".join(sql_statements))
    print(f"📁 SQL 파일 저장: {output_sql_path}")
    print(f"💡 이 SQL 파일을 데이터베이스에서 실행하세요.")
    
    # 브랜드별 개수 출력
    brand_counts = {}
    for store in convenience_stores:
        business_name = store.get('사업장명', '')
        if "세븐일레븐" in business_name or "7-ELEVEN" in business_name:
            brand_counts['세븐일레븐'] = brand_counts.get('세븐일레븐', 0) + 1
        elif "씨유" in business_name or "CU" in business_name:
            brand_counts['CU'] = brand_counts.get('CU', 0) + 1
        elif "GS25" in business_name or "지에스25" in business_name:
            brand_counts['GS25'] = brand_counts.get('GS25', 0) + 1
        elif "미니스톱" in business_name:
            brand_counts['미니스톱'] = brand_counts.get('미니스톱', 0) + 1
        else:
            brand_counts['기타'] = brand_counts.get('기타', 0) + 1
    
    print(f"\n📊 브랜드별 개수:")
    for brand, count in brand_counts.items():
        print(f"  - {brand}: {count}개")

if __name__ == "__main__":
    generate_sql_inserts()
