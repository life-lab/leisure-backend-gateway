FROM openjdk:8-jre-alpine

ADD ./leisure-backend-gateway-application/target/*.jar /app/app.jar

# 设置编码格式
ENV LANG="zh_CN.UTF-8"

VOLUME ["/data/log/"]

EXPOSE 8080 50983

ENTRYPOINT java -server -Dfile.encoding=UTF-8 -Duser.timezone=GMT+08 -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=50983 -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar