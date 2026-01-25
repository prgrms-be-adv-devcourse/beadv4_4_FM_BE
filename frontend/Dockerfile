# ===== 1단계: 빌드 스테이지 =====
FROM node:20-alpine AS build

WORKDIR /app

# 패키지 먼저 복사 (캐시 활용)
COPY package*.json ./
RUN npm install

# 소스 복사
COPY . .

# 프로덕션 빌드
RUN npm run build


# ===== 2단계: 실행 스테이지 (nginx) =====
FROM nginx:stable-alpine

# nginx 기본 설정 삭제
RUN rm /etc/nginx/conf.d/default.conf

# 우리가 만든 nginx 설정 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 빌드된 정적 파일 복사
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
