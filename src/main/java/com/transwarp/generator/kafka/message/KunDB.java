package com.transwarp.generator.kafka.message;


import org.apache.kafka.clients.producer.ProducerRecord;

public class KunDB {

  private static long i = 0L;
  private static long max;

  public KunDB(long max) {
    this.max = max;
  }

  public static ProducerRecord genKafkaRecord(String kafkaTopic ,boolean isPrint) {
    String valuesString = getKunDBMessage(isPrint);
    ProducerRecord record = new ProducerRecord<String, String>(kafkaTopic, String.valueOf(i), valuesString);
    i++;
    return record;
  }

  public static String getKunDBMessage(boolean isPrint) {
    String record = "%s,%d,%s,%s,%s,%d,%d,%s,%s,%s,%s,%s,%s,%d,%d,%d,%d,%d,%s,%d,%d,%s,%d,%d,%s,%s,%s,%d,%s,%d,%s,%s," +
            "%s," +
            "%s," +
            "%s," +
            "%s," +
            "%s," +
            "%s," +
            "%s,%s,%d," +
            "%d," +
            "%s," +
            "%s,%d,%d,%s";
    String isDeletaAndTime = "";
    if(isPrint) {
      isDeletaAndTime = "false|" + System.currentTimeMillis();
    } else {
      isDeletaAndTime = "false|";
    }
    record = String.format(record,"20201023", i, "100", "100", "100", 1, 1, "1", "1", "1", "1", "1", "1", 1, 1, 1, 1, 1
            , "1",
            1, 1, "1", 1, 1, "1", "1", "1", 1, "1", 1, "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", 1, 1, "1",
            "1", 1, 1,isDeletaAndTime);
    return record;
  }

}
