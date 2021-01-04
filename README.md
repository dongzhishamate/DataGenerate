DataGenerate配置文件
====

安全配置
----
isAutoColumnType=true
kafkaSecuredMode = <br>
saslKerberosServiceName = <br>
principal = <br>
keytabPath = 

kafka配置
----
bootstrapServerAddress = 172.18.120.27:9092,172.18.120.28:9092,172.18.120.29:9092,172.18.120.30:9092
##### Slipstream用到的zookeeper地址
ZkAddress = 172.18.120.30 <br>
kafkaTopic = test09 <br>
kafkaRefactor = 3 <br>
kafkaConsumerTimeOut = 30000 <br>
intervalFlushTime = 100
##### 是否开启kafka压缩算法，默认none可选gzip，snappy，lz4，zstd
compressionCodec = snappy
##### producer配置，要满足消息顺序性的话这个得设为1
maxInFlightRequestsPerConnection = 1 <br>
batchSize = 16384 <br>
lingerMs = 100 <br>
bufferSize = 33554432 <br>
ack = 1

消息发送配置
----
##### 是否永久执行数据插入
isInsertUnlimited = false
##### 如果没有开启永久插入每个线程能插入的最大数据条数
maxNum = 100000000
##### 是否开启argo插入数据时的性能计算
startThroughputCalculate = true
##### 每隔多少条打印一条日志
ThroughputCalculateInterval = 100000
##### 是否开启限流
isLimiting = false
##### 单个线程的限制tps
tps = 1000000
##### 是否是分区表
isPartition = false
##### 哪些属性是被分区的（对应属性的index）
partitionIndex = 3
##### column数量（包含rowkey）
columnNum = 6
##### 这是用kundb的数据集
isKunDB = false

发送邮件配置
----
##### 是否在消息发送完之后发送邮件
sendEmail = true
##### 发件人地址
senderAddress = 
##### 收件人地址
recipientAddress = 
##### 发件人账户名
senderAccount = 
##### 发件人账户密码
senderPassword =
##### 设置用户的认证方式
mailSmtpAuth = true
##### 设置传输协议
mailTransportProtocol = smtp
##### 设置发件人的SMTP服务器地址
mailStmpHost = smtp.qq.com


发送线程数配置
----
##### 每个线程的最大tps
tpsPerThread = 100000
##### 启动发送的线程个数
threadNum = 24

