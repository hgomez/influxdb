# InfluxDB Fetcher


## About
Use this program to fetch data from InfluxDB using the HTTP query API and
export it into InfluxDB line protocol format.


## Synopsis
```sh
influxdb-fetcher uri username password database query [fieldstotags]
```


### Example
```sh
influxdb-fetcher \
    http://influxdb.example.org:8086 root root aqi_readings \
    "SELECT station_type, latitude, longitude, aqi_value FROM testdrive GROUP BY station_type LIMIT 100"
```


## Setup
```sh
wget --no-clobber --output-document=/usr/local/bin/influxdb-fetcher https://raw.githubusercontent.com/hgomez/influxdb/master/bin/influxdb-fetcher
chmod +x /usr/local/bin/influxdb-fetcher
```


## Advanced usage


### Transform fields to tags
You could also transform fields into tags by adding them as a list of comma-
separated labels as a last parameter to the command line.

The background of this is that since as of today, there is now way to transform
fields into tags. See also: https://github.com/influxdata/influxdb/issues/3904.

In the following example, `latitude` and `longitude` fields will be put as tags
into the line protocol format output.
```sh
influxdb-fetcher \
    http://influxdb.example.org:8086 root root aqi_readings \
    "SELECT * FROM testdrive GROUP BY station_type LIMIT 100" \
    latitude,longitude
```


### Rename fields

As the line protocol format is pure ASCII, it is easy to use standard Unix tools
like `sed` to manipulate the content.


```sh
# Fetch data.
influxdb-fetcher ... > data.wireproto

# Manipulate data: Rename field.
sed -i -e "s/foo\=\([0-9.]*\)/bar=\1/g" data.wireproto

# Manipulate data: Cast from Int64 to Float.
sed -i -e "s/value\=\([0-9]*\)i/value=\1/g" data.wireproto

# Upload data to different destination.
curl -u login:password -i -POST \
    "http://dest-influxdb.example.org:8086/write?db=new_db" \
    --data-binary @data.wireproto
```


## Notes

* To get InfluxDB tags properly populated into line protocol format, you
  should add a `GROUP BY` clause.

* The Java API returns numeric values as float. InfluxDB Fetcher will try
  to figure out if numbers will be an Int64 (terminated by i in line protocol)
  or a Float. Sometimes, a field may be falsly classified as Int64.
  The `sed` example above might save you here.

* By carefully crafting InfluxQL expressions (using `WHERE time=...`), you
  should be able to fetch data in batches, ideally 10K-20K at a time.
  This way you can extract chunks of the source data. The background of this
  is that InfluxDB will OOM with too large POST requests.


## Development

### Build code
```sh
cd influxdb-fetcher
mvn clean package
```
