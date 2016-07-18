package com.github.hgomez.influxdb;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class InfluxDBFetcher {
  /**
   * http://influxdb.example.com:8086 login password collectd_db "SELECT * from cpu_value limit 10000";
   * 
   * To get tags, GROUP BY tag1, tag2 should be used, ie :
   * 
   * http://influxdb.example.com:8086 login password collectd_db "SELECT * from cpu_value GROUP BY host, instance, type, type_instance LIMIT 10000";
   *
   * @param args
   */
  public static void main(String args[]) {
    if (args.length == 0) {
      System.out.println("usage is : url login password dbname query");
      System.out.println(" ie: http://influxdb.example.com:8086 login password collectd_db \"SELECT * from cpu_value LIMIT 10000\"");
      System.out.println("To get tags, add a GROUP BY clause:");
      System.out.println(" ie: http://influxdb.example.com:8086 login password collectd_db \"SELECT * from cpu_value GROUP BY host, instance, type, type_instance LIMIT 10000\"");
      System.exit(1);
    }

    if (args.length < 5) {
      System.err.println("usage is : url login password dbname query");
      System.exit(1);
    }

    String url = args[0];
    String login = args[1];
    String password = args[2];
    String dbName = args[3];
    String queryString = args[4];

    InfluxDB influxDB = InfluxDBFactory.connect(url, login, password);
    Query query = new Query(queryString, dbName);

    QueryResult queryResult = influxDB.query(query);

    for (Result result : queryResult.getResults()) {
      for (Series serie : result.getSeries()) {
        List<String> columns = serie.getColumns();
        Map<String, String> tags = serie.getTags();
        for (List<Object> values : serie.getValues()) {
          System.out.println(buildWireString(serie.getName(), tags, columns, values));
        }
      }
    }
  }

  /**
   * Build a WireProtocol Line String from tags, columns and values
   *
   * @param serieName
   * @param tags
   * @param columns
   * @param values
   * @return
   */

  public static String buildWireString(String serieName, Map<String, String> tags, List<String> columns, List<Object> values) {
    // Format could be :
    // 2016-07-06T13:30:05Z
    // 2016-07-06T13:55:27.06649Z
    // 2016-07-06T13:55:26.283844Z
    DateTimeFormatter parser20 = ISODateTimeFormat.dateTimeNoMillis();
    DateTimeFormatter parser26 = ISODateTimeFormat.dateTime();

    // Ensure will have a default timestamp
    DateTime datetime = new DateTime();
    // timestamp is first value
    String time = (String) values.get(0);

    // If time string is 20 chars longs, there is no millisecondes
    if (time.length() == 20) {
      datetime = parser20.parseDateTime(time);
    }
    // If time string is 26+ chars longs, there is millisecondes
    else if (time.length() >= 26) {
      datetime = parser26.parseDateTime(time);
    }

    // Build WireProtocol Line (https://docs.influxdata.com/influxdb/v0.13/write_protocols/line/)
    //
    // cpu,host=server01,region=uswest value=1 1434055562000000000
    // cpu,host=server02,region=uswest value=3 1434055562000010000
    // temperature,machine=unit42,type=assembly internal=32,external=100 1434055562000000035
    // temperature,machine=unit143,type=assembly internal=22,external=130 1434055562005000035
    //
    StringBuilder builder = new StringBuilder(escapeSpaceCommaString(serieName));

    // When no tags, blank is separator
    if (tags == null) {
      builder.append(' ');
    } else {
      builder.append(',');
      int tagIdx = 0;

      // Iterate over tags to append tagkey1=tagvalue1,tagkey2=tagvalue2
      for (Entry<String, String> entry : tags.entrySet()) {
        builder.append(escapeSpaceCommaString(entry.getKey()));
        builder.append('=');
        builder.append(escapeSpaceCommaString(entry.getValue()));
        tagIdx++;

        // Still not latest tag, add , as separator
        if (tagIdx < tags.size()) {
          builder.append(',');
        } else
        // Latest tag, add blank as separator
        {
          builder.append(' ');
        }
      }
    }

    // Add columns/values on line
    for (int i = 1; i < values.size(); i++) {
      Object value = values.get(i);

      if (value instanceof Double) {
        builder.append(columns.get(i));
        builder.append('=');
        builder.append(generateNumeric((Double) value));
      } else if (value instanceof String) {
        builder.append(columns.get(i));
        builder.append("=\"");
        builder.append(value.toString());
        builder.append('"');
      }

      // Add virg if not latest value
      if (i != values.size() - 1) {
        builder.append(',');
      }

    }

    builder.append(' ');
    builder.append(datetime.getMillis());
    builder.append("000000");

    return builder.toString();
  }

  /**
   * Escape Space&Commas by /
   *
   * @param pStr
   * @return escaped String or original if nothing was required
   */
  public static String escapeSpaceCommaString(String pStr) {

    String nStr = pStr;

    CharSequence cs1 = " ";
    CharSequence cs2 = "\\ ";

    if (nStr.contains(cs1)) {
      nStr = nStr.replace(cs1, cs2);
    }

    cs1 = ",";
    cs2 = "\\,";

    if (nStr.contains(cs1)) {
      nStr = nStr.replace(cs1, cs2);
    }

    return nStr;
  }

  /**
   * Generate a String for Numeric Value (in Double), could be Integers or Float (damn't protocol)
   * Integer value are suffix by i
   *
   * @param pValue
   * @return escaped String or original if nothing was required
   */
  public static String generateNumeric(Double pValue) {

    StringBuilder builder = new StringBuilder();

    if ((pValue == Math.rint(pValue))) {
      builder.append(pValue.longValue());
      builder.append('i');
    } else {
      builder.append(pValue);
    }


    return builder.toString();
  }
}