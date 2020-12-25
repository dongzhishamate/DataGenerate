package com.transwarp.generator.kafka.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailConfig {
  //发件人地址
  private String senderAddress;
  //收件人地址
  private String recipientAddress;
  //发件人账户名
  private String senderAccount;
  //发件人账户密码
  private String senderPassword;
  //设置用户的认证方式
  private String mailSmtpAuth;
  //设置传输协议
  private String mailTransportProtocol;
  //设置发件人的SMTP服务器地址
  private String mailStmpHost;
}
