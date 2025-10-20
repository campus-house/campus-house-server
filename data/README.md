# 📊 데이터 폴더 구조

이 폴더는 캠퍼스 하우스 프로젝트의 건물 정보 데이터를 관리합니다.

## 📁 폴더 구조

```
data/
├── buildings/              # 건물 관련 데이터
│   ├── raw/               # 원본 CSV 파일들
│   │   ├── buildings_2024_10.csv
│   │   └── ...
│   └── processed/         # 정제된 데이터
│       ├── buildings_processed.csv
│       └── ...
├── upload/                # 업로드용 임시 폴더
└── README.md             # 이 파일
```

## 📋 사용 방법

### 1. 원본 데이터 업로드
- `buildings/raw/` 폴더에 원본 CSV 파일들을 업로드
- 파일명 형식: `buildings_YYYY_MM.csv`

### 2. 데이터 정제
- 원본 데이터를 Building 엔티티에 맞게 정제
- 정제된 데이터는 `buildings/processed/` 폴더에 저장

### 3. 데이터베이스 입력
- 정제된 데이터를 자동으로 데이터베이스에 입력
- DataInitializer 또는 별도 스크립트를 통해 처리

## 🔧 데이터 형식

### 원본 CSV 형식 (예시)
```csv
id,name,address,usage,floors_ground,has_elevator,elevator_count,households,avg_rent,avg_deposit,sample_count
1001,캠퍼스하우스 A,서울 관악구 관악로 1,오피스텔,15,True,2,80,42.7,2000,6
```

### 정제된 데이터 형식
- Building 엔티티에 맞게 변환된 데이터
- 추가 계산된 필드들 포함 (위도/경도, 접근성 정보 등)

## 📝 주의사항

- 원본 데이터는 백업 보관
- 정제 과정에서 데이터 손실 방지
- 개인정보가 포함된 경우 보안 처리 필요
