###############################
Research about InfluxDB Fetcher
###############################


**********
Background
**********
Many people are in need for such a program to export data from InfluxDB
in line protocol format, so we want to collect different resources here
where they have been asking about it.

While it is possible to use Telegraf for the job (see `InfluxDB Line Protocol output data format`_),
we dearly wanted to have a standalone command line program. We also believe that ``influx_inspect``
and ``influx_dumptsi`` also don't fit the bill appropriately.

- https://github.com/influxdata/influxdb/issues/6234
- https://github.com/influxdata/influxdb/issues/7260
- https://github.com/influxdata/influxdb/issues/10579
- https://github.com/influxdata/influxdb/issues/10993
- https://stackoverflow.com/questions/36956878/influxdb-write-with-rest-api-and-json-data
- https://stackoverflow.com/questions/27779472/export-data-from-influxdb
- https://community.influxdata.com/t/influxdb-can-not-export-only-a-series-or-measurement-to-line-feed-protocol/7909
- https://www.influxdata.com/blog/tldr-influxdb-tech-tips-november-3-2016/
- https://docs.influxdata.com/influxdb/v1.8/tools/influx_inspect/


This program should definitively be included into the curated `awesome-influxdb`_ compilation.


.. _InfluxDB Line Protocol output data format: https://docs.influxdata.com/telegraf/v1.17/data_formats/output/influx/
.. _awesome-influxdb: https://project-awesome.org/mark-rushakoff/awesome-influxdb



**********
References
**********
A collected list of resources with references to this program.

- https://github.com/influxdata/influxdb/issues/3904
- https://community.influxdata.com/t/export-big-data-from-influxdb/2204
- https://www.reddit.com/r/golang/comments/5cj7no/gernestblue_generate_influxdb_line_protocol_from/



****************************
Other programs and resources
****************************

A collected list of resources where different types of (JSON)
payloads want to be converted into InfluxDB line protocol format
or the other way round.


To line protocol
================
- https://www.quora.com/How-do-I-convert-a-JSON-into-line-protocol
- https://www.npmjs.com/package/json-to-line-protocol
- https://github.com/airyland/x-influxdb
- https://community.grafana.com/t/source-data-in-json-best-way-to-render-dashboard/29932
- https://community.influxdata.com/t/json-file-line-protocol-influxdb/7818
- https://github.com/jhrv/json2lineprotocol
- https://github.com/qn7o/lineprotocol
- https://github.com/docker-rubygem/influxdb-lineprotocol-writer
- https://community.influxdata.com/t/push-json-file-to-influxdb/14452
- https://github.com/earthcubeprojects-chords/chords/issues/170
- https://druid.apache.org/docs/latest/development/extensions-contrib/influxdb-emitter.html
- https://questdb.io/docs/reference/api/java-embedded/#influxdb-sender-library


From line protocol
==================
- https://github.com/mohammadGh/influxdb-line-protocol-to-json
- https://hub.docker.com/r/prom/influxdb-exporter/
- https://druid.apache.org/docs/latest/development/extensions-contrib/influx.html
- https://dzone.com/articles/sending-influxdb-line-protocol-to-questdb
- https://github.com/apache/druid/tree/master/extensions-contrib/influx-extensions
- https://questdb.io/docs/reference/api/influxdb
  - https://github.com/questdb/questdb/blob/master/benchmarks/src/main/java/org/questdb/LineUDPSenderMain.java
  - https://github.com/questdb/questdb/issues/593


More tools and libraries
========================
- https://github.com/barasher/influxdb-pusher
- https://github.com/SebastianCzoch/influx-line-protocol
- https://github.com/lchateau/import-export-influxdb
- https://github.com/adliih/csv-to-influxdb-export-format
