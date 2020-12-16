package com.transwarp.generator.kafka.utils;

import java.util.List;

public class StringUtils {
  public static Boolean isBlank(String str) {
    return str == null || str.isEmpty();
  }

  public static String getDBFromFullTableName(String str) {
    return str.split("\\.")[0];
  }

  public static String getTableNameFromFullTableName(String str) {
    return str.split("\\.")[1];
  }

  public static String zip(List<String> keys, String splitter) {
    if(isBlank(keys)) {
      return "";
    }
    return String.join(splitter, keys);
  }

  public static Boolean isBlank(List<String> stringCollection) {
    return stringCollection == null || stringCollection.isEmpty();
  }
}
