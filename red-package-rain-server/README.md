����Ŀ¼��

���ַ�ʽ����
��һ�ַ�ʽ��ʹ��docker-compose����
��jar����docker-compose.yaml�ļ��Լ�Dockerfile�ļ��ŵ�ͬһĿ¼�£�ֱ������docker-compose up -d������Ŀ

�ڶ��ַ�ʽ��ʹ���������ű�
���������ű�Ŀ¼���ж�Ӧ�����ã���jar����Ŀ¼�µ��ļ��ϴ���������ͬһĿ¼��
�����Ȩ,���ÿ���������
chmod +x /opt/app/red-package-rain-server/red-package-rain-server-start.sh
chmod +x /opt/app/red-package-rain-server/red-package-rain-server-stop.sh
sudo mv red-package-rain-server.service /etc/systemd/system/
systemctl daemon-reload
systemctl start red-package-rain-server
systemctl enable red-package-rain-server
systemctl status red-package-rain-server


���������������Զ����нű���������

��Ҫ����rocketmq����������
RedPackageRainGroup1
RedPackageRainGroup2
RedPackageRainGroup3

һ������
RED_PACKAGE_RAIN_TOPIC
