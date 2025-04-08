#!/bin/bash

LOG_FILE=/home/ubuntu/deploy.log
DEPLOY_PATH=/home/ubuntu/app/
HEALTH_URL="http://localhost:8080/api/auth/health-check"

echo ">>> 배포 시작: $(date)" >> $LOG_FILE

cd $DEPLOY_PATH || exit

# .env 로부터 환경변수 불러오기
export $(grep -v '^#' ${DEPLOY_PATH}.env | xargs)

# Docker Hub 로그인
echo ">>> Docker Hub 로그인" >> $LOG_FILE
docker login -u ${DOCKER_HUB_USERNAME} -p ${DOCKER_HUB_ACCESS_TOKEN}
if [ $? -ne 0 ]; then
  echo ">>> [ERROR] Docker Hub 로그인 실패" >> $LOG_FILE
  exit 1
fi

# configs 디렉토리 생성 및 설정파일 복사
echo ">>> 설정 파일 복사" >> $LOG_FILE
mkdir -p ${DEPLOY_PATH}configs

cp ${DEPLOY_PATH}application-prod.yml ${DEPLOY_PATH}configs/
cp ${DEPLOY_PATH}application-oauth.yml ${DEPLOY_PATH}configs/
cp ${DEPLOY_PATH}application-common.yml ${DEPLOY_PATH}configs/

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
