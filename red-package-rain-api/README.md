����Ŀ¼��

���ַ�ʽ����
��һ�ַ�ʽ��ʹ��docker-compose����
��jar����docker-compose.yaml�ļ��Լ�Dockerfile�ļ��ŵ�ͬһĿ¼�£�ֱ������docker-compose up -d������Ŀ

�ڶ��ַ�ʽ��ʹ���������ű�
���������ű�Ŀ¼���ж�Ӧ�����ã���jar����Ŀ¼�µ��ļ��ϴ���������ͬһĿ¼��
�����Ȩ,���ÿ���������

chmod +x /opt/app/red-package-rain-api/red-package-rain-api-start.sh
chmod +x /opt/app/red-package-rain-api/red-package-rain-api-stop.sh
sudo mv red-package-rain-api.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable red-package-rain-api
systemctl start red-package-rain-api
systemctl status red-package-rain-api

���������������Զ����нű���������


��Ҫ��ǰ׼����rocketmq�������������
RED_PACKAGE_RAIN_TOPIC ��ͨ���� 
TRANSACTION_TOPIC ��������
RedPackageRainGroup ������