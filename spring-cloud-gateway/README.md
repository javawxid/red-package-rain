����Ŀ¼��

���ַ�ʽ����
��һ�ַ�ʽ��ʹ��docker-compose����
��jar����docker-compose.yaml�ļ��Լ�Dockerfile�ļ��ŵ�ͬһĿ¼�£�ֱ������docker-compose up -d������Ŀ

�ڶ��ַ�ʽ��ʹ���������ű�
���������ű�Ŀ¼���ж�Ӧ�����ã���jar����Ŀ¼�µ��ļ��ϴ���������ͬһĿ¼��
�����Ȩ,���ÿ���������

chmod +x /opt/app/gateway/gateway-start.sh
chmod +x /opt/app/gateway/gateway-stop.sh
sudo mv gateway.service /etc/systemd/system/
cd /etc/systemd/system/
systemctl daemon-reload
systemctl enable gateway
systemctl start gateway
systemctl status gateway

���������������Զ����нű���������