# kafka性能测试工具
    能测试kafka的最大吞吐，能多线程向kafka中插入数据，实现了限流功能，消息发送完邮箱通知功能，吞吐量计算，控制启用插入的线程数，可配置kafka中的参数


## 是否自动设定各个列属性的类型，如果为true就默认为String，不然得自己设定
    isAutoColumnType=true

## kafka是否开启压缩,默认是none，可选gzip，snappy，lz4，zstd
    compressionCodec = snappy

## column数量（包含rowkey）
    columnNum = 6

    #################kafka配置#################
    bootstrapServerAddress = 
    ZkAddress = 
    kafkaTopic = 
    kafkaRefactor = 3
    kafkaConsumerTimeOut = 30000
    intervalFlushTime = 100
## 是否开启kafka压缩算法，默认none可选gzip，snappy，lz4，zstd
    compressionCodec = snappy
## producer配置，要满足消息顺序性的话这个得设为1
    maxInFlightRequestsPerConnection = 1
    batchSize = 16384
    lingerMs = 0
    bufferSize = 33554432
    ack = 1
#################kafka配置#################

#################消息发送配置#################
## 是否永久执行数据插入
    isInsertUnlimited = true
## 如果执行有限制的话设置插入的数据量，考虑废除
    maxNum = 100000
## 是否开启argo插入数据时的性能计算
    startThroughputCalculate = true
## 每隔多少条打印一条日志
    ThroughputCalculateInterval = 100000
## 是否开启限流
    isLimiting = false
## 限制tps
    tps = 1000000
## 是否是分区表
    isPartition = false
## 哪些属性是被分区的（对应属性的index）
    partitionIndex = 3
## column数量（包含rowkey）
    columnNum = 6
#################消息发送配置#################

#################发送邮件配置#################
## 是否在消息发送完之后发送邮件
    sendEmail = true
## 发件人地址
    senderAddress =
## 收件人地址
    recipientAddress = 
## 发件人账户名
    senderAccount = 
## 发件人账户密码
    senderPassword = 
## 设置用户的认证方式
    mailSmtpAuth = true
## 设置传输协议
    mailTransportProtocol = smtp
## 设置发件人的SMTP服务器地址
    mailStmpHost = smtp.qq.com

## 这是用kundb的数据集
    isKunDB = false
#################发送邮件配置#################

#################发送线程数配置#################
## 每个线程的最大tps
    tpsPerThread = 100000
## 启动发送的线程个数
    threadNum = 24
#################发送邮件配置#################
