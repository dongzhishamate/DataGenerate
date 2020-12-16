#配置文件
#是否自动设定各个列属性的类型，如果为true就默认为String，不然得自己设定
isAutoColumnType=true
#################kafka安全配置#################
=======
# 说明
bootstrapServerAddress = 172.18.120.27:9092,172.18.120.28:9092,172.18.120.29:9092,172.18.120.30:9092
ZkAddress = 172.18.120.30
kafkaTopic = test09
#kafka是否开启压缩,默认是none，可选gzip，snappy，lz4，zstd
compressionCodec = snappy

#column数量（包含rowkey）
columnNum = 6

#################kafka配置#################
bootstrapServerAddress = 172.18.120.27:9092,172.18.120.28:9092,172.18.120.29:9092,172.18.120.30:9092
ZkAddress = 172.18.120.30
kafkaTopic = test09
kafkaRefactor = 3
kafkaConsumerTimeOut = 30000
intervalFlushTime = 100
#是否开启kafka压缩算法，默认none可选gzip，snappy，lz4，zstd
compressionCodec = snappy
#producer配置，要满足消息顺序性的话这个得设为1
maxInFlightRequestsPerConnection = 1
batchSize = 16384
lingerMs = 0
bufferSize = 33554432
ack = 1
#################kafka配置#################

#################消息发送配置#################
#是否永久执行数据插入
isInsertUnlimited = true
#如果执行有限制的话设置插入的数据量，考虑废除
maxNum = 100000
#是否开启argo插入数据时的性能计算
startThroughputCalculate = true
#每隔多少条打印一条日志
ThroughputCalculateInterval = 100000
#是否开启限流
isLimiting = false
#限制tps
tps = 1000000
#是否是分区表
isPartition = false
#哪些属性是被分区的（对应属性的index）
partitionIndex = 3
#column数量（包含rowkey）
columnNum = 6
#################消息发送配置#################

#################发送邮件配置#################
#是否在消息发送完之后发送邮件
sendEmail = true
#发件人地址
senderAddress = 280246517@qq.com
#收件人地址
recipientAddress = 280246517@qq.com
#发件人账户名
senderAccount = 280246517@qq.com
#发件人账户密码
senderPassword = vxyllngwplsvcbbb
#设置用户的认证方式
mailSmtpAuth = true
#设置传输协议
mailTransportProtocol = smtp
#设置发件人的SMTP服务器地址
mailStmpHost = smtp.qq.com

#这是用kundb的数据集
isKunDB = false
#################发送邮件配置#################

#################发送线程数配置#################
#每个线程的最大tps
tpsPerThread = 100000
#启动发送的线程个数
threadNum = 24
#################发送邮件配置#################

#gg.handler.tdthandler.SaslKerberosServiceName=kafka
#gg.handler.tdthandler.Principal=admin@SGIDCTDH
#gg.handler.tdthandler.KeytabPath=/root/kafka.keytab
#gg.handler.tdthandler.PrincipalStream=admin@SGIDCTDH
#gg.handler.tdthandler.KeytabPathStream=/etc/slipstream2/conf/kafka.keytab

