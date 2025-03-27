部署目录在

两种方式部署：
第一种方式：使用docker-compose部署
将jar包和docker-compose.yaml文件以及Dockerfile文件放到同一目录下，直接运行docker-compose up -d运行项目

第二种方式：使用自启动脚本
在自启动脚本目录下有对应的配置，将jar包和目录下的文件上传到服务器同一目录下
添加授权,设置开机自启动

chmod +x /opt/app/user/user-start.sh
chmod +x /opt/app/user/user-stop.sh
sudo mv user.service /etc/systemd/system/
cd /etc/systemd/system/
systemctl daemon-reload
systemctl enable user
systemctl start user
systemctl status user

待服务器重启后自动运行脚本启动服务


使用mvn dependency:tree查看依赖树，查找潜在的冲突
如果出现[INFO] BUILD SUCCESS说明没有依赖冲突