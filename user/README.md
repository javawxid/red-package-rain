����Ŀ¼��

���ַ�ʽ����
��һ�ַ�ʽ��ʹ��docker-compose����
��jar����docker-compose.yaml�ļ��Լ�Dockerfile�ļ��ŵ�ͬһĿ¼�£�ֱ������docker-compose up -d������Ŀ

�ڶ��ַ�ʽ��ʹ���������ű�
���������ű�Ŀ¼���ж�Ӧ�����ã���jar����Ŀ¼�µ��ļ��ϴ���������ͬһĿ¼��
�����Ȩ,���ÿ���������

chmod +x /opt/app/user/user-start.sh
chmod +x /opt/app/user/user-stop.sh
sudo mv user.service /etc/systemd/system/
cd /etc/systemd/system/
systemctl daemon-reload
systemctl enable user
systemctl start user
systemctl status user

���������������Զ����нű���������


ʹ��mvn dependency:tree�鿴������������Ǳ�ڵĳ�ͻ
�������[INFO] BUILD SUCCESS˵��û��������ͻ