# 使用openjdk:8作为基础镜像
FROM openjdk:8
# 拷贝当前目录下所有的.jar文件到/app目录下
COPY ./user-0.0.1.jar /tmp/user-0.0.1.jar
# 设置工作目录
WORKDIR /tmp
# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=test
# 对外暴露端口
EXPOSE 8092
# 指定容器启动时需要执行的命令，运行Java虚拟机并加载app.jar文件
ENTRYPOINT ["java","-jar","/tmp/user-0.0.1.jar"]