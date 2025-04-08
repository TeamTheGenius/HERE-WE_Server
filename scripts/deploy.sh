#!/bin/bash

LOG_FILE=/home/ubuntu/deploy.log
DEPLOY_PATH=/home/ubuntu/app/
HEALTH_URL="http://localhost:8080/api/auth/health-check"

echo ">>> 배포 시작: $(date)" >> $LOG_FILE

cd $DEPLOY_PATH || exit

# 기존 컨테이너 종료 및 삭제
echo ">>> 기존 컨테이너 종료 및 제거" >> $LOG_FILE
docker-compose down

# 최신 이미지 Pull
echo ">>> Docker 이미지 pull" >> $LOG_FILE
docker-compose pull

# 컨테이너 실행
echo ">>> Docker Compose로 애플리케이션 실행" >> $LOG_FILE
docker-compose up -d

# Health check
echo ">>> 애플리케이션 Health Check 시작" >> $LOG_FILE

for i in {1..20}
do
  STATUS=$(curl -s $HEALTH_URL | grep '"status":"UP"')
  if [ -n "$STATUS" ]; then
    echo ">>> 서버가 정상적으로 기동되었습니다." >> $LOG_FILE
    break
  fi
  echo ">>> 서버가 아직 기동되지 않았습니다. 재시도: $i" >> $LOG_FILE
  sleep 5
done

if [ -z "$STATUS" ]; then
  echo ">>> [ERROR] 서버가 정상적으로 기동되지 않았습니다." >> $LOG_FILE
  exit 1
fi

echo ">>> 배포 완료: $(date)" >> $LOG_FILE
