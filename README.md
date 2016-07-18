# influxdb fetcher tool

## Use this tool to fetch InfluxDB measurements and dump an ascii file in InfluxDB Wire Protocol.

     mvn clean package
     java -cp target/influxdb-fetcher-1.0.0-SNAPSHOT.jar com.github.hgomez.influxdb.InfluxDBFetcher http://influxdb.example.com:8086 login password collectd_db "SELECT * from cpu_value LIMIT 10000"

* To get Tags properly generated in Wire Protocol, you should add a GROUP BY clause  
      
    java -cp target/influxdb-fetcher-1.0.0-SNAPSHOT.jar com.github.hgomez.influxdb.InfluxDBFetcher http://influxdb.example.com:8086 login password collectd_db "SELECT * from cpu_value GROUP BY host, instance, type, type_instance LIMIT 10000"

