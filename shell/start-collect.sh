#linux  启动脚本
nohup java -jar etlweb-log-collect.jar --jasypt.encryptor.password=bigdata > bigdata-collect-log.out 2>&1 &
tail -f bigdata-collect-log.out
