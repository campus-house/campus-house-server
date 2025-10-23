@echo off
echo ========================================
echo 건물 데이터 PostgreSQL 삽입 스크립트
echo ========================================

echo.
echo 1. JSON을 CSV로 변환 중...
python json_to_csv.py

echo.
echo 2. PostgreSQL에 데이터 삽입 중...
python import_to_postgresql.py

echo.
echo 작업이 완료되었습니다!
pause
