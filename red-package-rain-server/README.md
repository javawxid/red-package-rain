部署目录在

两种方式部署：
第一种方式：使用docker-compose部署
将jar包和docker-compose.yaml文件以及Dockerfile文件放到同一目录下，直接运行docker-compose up -d运行项目

第二种方式：使用自启动脚本
在自启动脚本目录下有对应的配置，将jar包和目录下的文件上传到服务器同一目录下
添加授权,设置开机自启动
chmod +x /opt/app/red-package-rain-server/red-package-rain-server-start.sh
chmod +x /opt/app/red-package-rain-server/red-package-rain-server-stop.sh
sudo mv red-package-rain-server.service /etc/systemd/system/
systemctl daemon-reload
systemctl start red-package-rain-server
systemctl enable red-package-rain-server
systemctl status red-package-rain-server


待服务器重启后自动运行脚本启动服务

需要创建rocketmq三个消费组
RedPackageRainGroup1
RedPackageRainGroup2
RedPackageRainGroup3

一个主题
RED_PACKAGE_RAIN_TOPIC
