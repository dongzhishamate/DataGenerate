package com.transwarp.generator.kafka.context;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Properties;

@Data
@NoArgsConstructor
public class EmailContext {
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

  public EmailContext(Properties properties) {
    this.senderAddress = String.valueOf(properties.getProperty("senderAddress"));
    this.recipientAddress = String.valueOf(properties.getProperty("recipientAddress"));
    this.senderAccount = String.valueOf(properties.getProperty("senderAccount"));
    this.senderPassword = String.valueOf(properties.getProperty("senderPassword"));
    this.mailSmtpAuth = String.valueOf(properties.getProperty("mailSmtpAuth"));
    this.mailTransportProtocol = String.valueOf(properties.getProperty("mailTransportProtocol"));
    this.mailStmpHost = String.valueOf(properties.getProperty("mailStmpHost"));
  }

}
