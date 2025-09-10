1.本机使用SSH隧道连接云服务器
在本地电脑的终端中执行，本机端口3307映射到云服务器3306端口，并输入密码(右键复制)，光标持续闪烁代表连接成功
ssh -N -L 3307:localhost:3306 root@121.41.176.238
见doc/images/1.png

ssh -N -L 6380:localhost:6379 root@121.41.176.238
2.mysql见doc/images/2.png

