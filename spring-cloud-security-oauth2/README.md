����Ŀ¼��

���ַ�ʽ����
��һ�ַ�ʽ��ʹ��docker-compose����
��jar����docker-compose.yaml�ļ��Լ�Dockerfile�ļ��ŵ�ͬһĿ¼�£�ֱ������docker-compose up -d������Ŀ
�������
[root@node0 spring-cloud-security-oauth2]# docker-compose up -d
ERROR: .UnicodeDecodeError: 'utf-8' codec can't decode byte 0xb6 in position 16: invalid start byte
ִ����������
[root@node0 spring-cloud-security-oauth2]# iconv -f ISO-8859-1 -t UTF-8 /opt/app/spring-cloud-security-oauth2/docker-compose.yaml > /tmp/docker-compose.yaml.tmp && mv /tmp/docker-compose.yaml.tmp /opt/app/spring-cloud-security-oauth2/docker-compose.yaml
mv: overwrite ��/opt/app/spring-cloud-security-oauth2/docker-compose.yaml��? y
[root@node0 spring-cloud-security-oauth2]# docker-compose up -d


�ڶ��ַ�ʽ��ʹ���������ű�
���������ű�Ŀ¼���ж�Ӧ�����ã���jar����Ŀ¼�µ��ļ��ϴ���������ͬһĿ¼��
�����Ȩ�����ÿ���������
chmod +x /opt/app/spring-cloud-security-oauth2/security-oauth2-start.sh
chmod +x /opt/app/spring-cloud-security-oauth2/security-oauth2-stop.sh
sudo mv security-oauth2.service /etc/systemd/system/
cd /etc/systemd/system/
systemctl daemon-reload
systemctl enable security-oauth2
systemctl start security-oauth2
systemctl status security-oauth2


���������������Զ����нű���������