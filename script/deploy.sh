#!/bin/bash

echo ">>> 배포 시작: $(date)" >> /home/ubuntu/deploy.log

# 배포 디렉토리 설정
DEPLOY_PATH=/home/ubuntu/app/

# .env 파일 복사 확인
if [ ! -f "$DEPLOY_PATH/.env" ]; then
  echo ">>> .env 파일이 없습니다. 복사합니다." >> /home/ubuntu/deploy.log
  cp /home/ubuntu/.env $DEPLOY_PATH/  # 경로 주의
fi

# docker-compose.yml 복사
echo ">>> docker-compose.yml 파일 복사" >> /home/ubuntu/deploy.log
cp /home/ubuntu/docker-compose.yml $DEPLOY_PATH/

# 배포 디렉토리로 이동
cd $DEPLOY_PATH || exit

# Docker 이미지 pull
echo ">>> Docker 이미지 가져오기" >> /home/ubuntu/deploy.log
docker-compose pull

# 기존 컨테이너 중지 및 제거
echo ">>> 애플리케이션 컨테이너 중지 및 제거" >> /home/ubuntu/deploy.log
docker-compose stop server
docker-compose rm -f server

# 애플리케이션 컨테이너 재시작
echo ">>> Docker Compose로 애플리케이션 시작" >> /home/ubuntu/deploy.log
docker-compose up -d

echo ">>> 배포 완료: $(date)" >> /home/ubuntu/deploy.log
