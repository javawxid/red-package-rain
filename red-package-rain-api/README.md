部署目录在

两种方式部署：
第一种方式：使用docker-compose部署
将jar包和docker-compose.yaml文件以及Dockerfile文件放到同一目录下，直接运行docker-compose up -d运行项目

第二种方式：使用自启动脚本
在自启动脚本目录下有对应的配置，将jar包和目录下的文件上传到服务器同一目录下
添加授权,设置开机自启动

chmod +x /opt/app/red-package-rain-api/red-package-rain-api-start.sh
chmod +x /opt/app/red-package-rain-api/red-package-rain-api-stop.sh
sudo mv red-package-rain-api.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable red-package-rain-api
systemctl start red-package-rain-api
systemctl status red-package-rain-api

待服务器重启后自动运行脚本启动服务


需要提前准备好rocketmq的主题和消费组
RED_PACKAGE_RAIN_TOPIC 普通主题 
TRANSACTION_TOPIC 事务主题
RedPackageRainGroup 消费组