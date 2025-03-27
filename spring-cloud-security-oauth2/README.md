部署目录在

两种方式部署：
第一种方式：使用docker-compose部署
将jar包和docker-compose.yaml文件以及Dockerfile文件放到同一目录下，直接运行docker-compose up -d运行项目
如果出现
[root@node0 spring-cloud-security-oauth2]# docker-compose up -d
ERROR: .UnicodeDecodeError: 'utf-8' codec can't decode byte 0xb6 in position 16: invalid start byte
执行以下命令
[root@node0 spring-cloud-security-oauth2]# iconv -f ISO-8859-1 -t UTF-8 /opt/app/spring-cloud-security-oauth2/docker-compose.yaml > /tmp/docker-compose.yaml.tmp && mv /tmp/docker-compose.yaml.tmp /opt/app/spring-cloud-security-oauth2/docker-compose.yaml
mv: overwrite ‘/opt/app/spring-cloud-security-oauth2/docker-compose.yaml’? y
[root@node0 spring-cloud-security-oauth2]# docker-compose up -d


第二种方式：使用自启动脚本
在自启动脚本目录下有对应的配置，将jar包和目录下的文件上传到服务器同一目录下
添加授权，设置开机自启动
chmod +x /opt/app/spring-cloud-security-oauth2/security-oauth2-start.sh
chmod +x /opt/app/spring-cloud-security-oauth2/security-oauth2-stop.sh
sudo mv security-oauth2.service /etc/systemd/system/
cd /etc/systemd/system/
systemctl daemon-reload
systemctl enable security-oauth2
systemctl start security-oauth2
systemctl status security-oauth2


待服务器重启后自动运行脚本启动服务