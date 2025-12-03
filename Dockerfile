# 使用轻量级JRE镜像运行应用
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 复制应用构件（需要在构建镜像前本地构建）
COPY target/*.jar app.jar

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=docker

# 暴露应用端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Xms256m", "-jar", "app.jar"]