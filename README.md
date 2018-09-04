# influxdb fetcher tool

Use this tool to fetch InfluxDB measurements and dump an ascii file in InfluxDB Wire Protocol.

* Build code

    ```sh
     cd influxdb-fetcher
     mvn clean package
    ```

To get Tags properly generated in Wire Protocol, you should add a GROUP BY clause  
     
* Run code
 
    ```sh
     cd influxdb-fetcher
     java -cp target/influxdb-fetcher-1.0.1-SNAPSHOT.jar com.github.hgomez.influxdb.InfluxDBFetcher http://influxdb.example.com:8086 login password collectd_db "SELECT * from cpu_value GROUP BY host, instance, type, type_instance LIMIT 10000"
    ```
You could also transform fields in tags by adding them in command line. In following example, data1 and data2 fields will be put as tags in Wire Protocol, since as of today, there is now way to transform fields in tags (see: https://github.com/influxdata/influxdb/issues/3904) 

    ```sh
     cd influxdb-fetcher
     java -cp target/influxdb-fetcher-1.0.1-SNAPSHOT.jar com.github.hgomez.influxdb.InfluxDBFetcher http://influxdb.example.com:8086 login password collectd_db "SELECT * from cpu_value GROUP BY host, instance, type, type_instance LIMIT 10000" data1,data2
    ```
    
# Side notes

* Using an ASCII file in WireProtocol will allow you to rework fields and use curl to send metrics to destination influxdb ie :

    ```sh
     # Fetch metrics from source collectd_db in influxdb.example.com
     java -cp target/influxdb-fetcher-1.0.1-SNAPSHOT.jar com.github.hgomez.influxdb.InfluxDBFetcher http://influxdb.example.com:8086 login password collectd_db "SELECT * from cpu_value GROUP BY host, instance, type, type_instance LIMIT 10000" > cpu.wireproto

     # Bring sed magics if need in WireProtocol file
     # ie:
     # sed -i -e "s/value\=\([0-9]*\)i/value=\1/g" cpu.wireproto
     
     # Post metrics to destination new_db in dest-influxdb.example.com
     curl -u login:password -i -POST "http://dest-influxdb.example.com:8086/write?db=new_db" --data-binary @cpu.wireproto
    ```

* Java API return numerics as float, so influxdb-fetcher try to figure if number will be a Int64 (terminated by i in WireProtocol) or a Float by checking if a value is an Integer. Some times, a field may be dump as Int64 but should be send as Float. sed example upper should do the trick :)

* By carefully crafting InfluxDB QL requests (using WHERE time), you should be able to fetch sources metrics by batchs, ideally 10 or 20K at a time. This way you'll 'extract part of source metrics or all of them but in smaller chunk. InfluxDB will OOM with too large POST contents. 
