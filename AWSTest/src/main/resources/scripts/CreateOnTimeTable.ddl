drop table if exists on_time_on_time;
CREATE EXTERNAL TABLE IF NOT EXISTS on_time_on_time ( year INT,
 quarter INT,
 month INT,
 dayofmonth INT,
 dayofweek INT,
 flightdate DATE,
 uniquecarrier STRING,
 airlineid INT,
 carrier STRING,
 tailnum STRING,
 flightnum STRING,
 originairportid INT,
 originairportseqid INT,
 origincitymarketid INT,
 origin STRING,
 origincityname STRING,
 originstate STRING,
 originstatefips STRING,
 originstatename STRING,
 originwac INT,
 destairportid INT,
 destairportseqid INT,
 destcitymarketid INT,
 dest STRING,
 destcityname STRING,
 deststate STRING,
 deststatefips STRING,
 deststatename STRING,
 destwac INT,
 crsdeptime STRING,
 deptime STRING,
 depdelay DECIMAL(5,2),
 depdelayminutes DECIMAL(5,2),
 depdel15 DECIMAL(5,2),
 departuredelaygroups INT,
 deptimeblk STRING,
 taxiout DECIMAL(5,2),
 wheelsoff STRING,
 wheelson STRING,
 taxiin DECIMAL(5,2),
 crsarrtime STRING,
 arrtime STRING,
 arrdelay STRING,
 arrdelayminutes DECIMAL(5,2),
 arrdel15 DECIMAL(5,2),
 arrivaldelaygroups DECIMAL(5,2),
 arrtimeblk STRING,
 cancelled DECIMAL(5,2),
 cancellationcode STRING,
 diverted DECIMAL(6,2),
 crselapsedtime DECIMAL(6,2),
 actualelapsedtime DECIMAL(6,2),
 airtime DECIMAL(6,2),
 flights DECIMAL(6,2),
 distance DECIMAL(6,2),
 distancegroup INT,
 carrierdelay INT,
 weatherdelay INT,
 nasdelay INT,
 securitydelay INT,
 lateaircraftdelay INT,
 firstdeptime INT,
 totaladdgtime INT,
 longestaddgtime INT,
 divairportlandings INT,
 divreacheddest INT,
 divactualelapsedtime DECIMAL(38,0),
 divarrdelay INT,
 divdistance INT,
 div1airport INT,
 div1airportid INT,
 div1airportseqid INT,
 div1wheelson INT,
 div1totalgtime INT,
 div1longestgtime INT,
 div1wheelsoff INT,
 div1tailnum INT,
 div2airport INT,
 div2airportid INT,
 div2airportseqid INT,
 div2wheelson INT,
 div2totalgtime INT,
 div2longestgtime INT,
 div2wheelsoff INT,
 div2tailnum INT,
 div3airport INT,
 div3airportid INT,
 div3airportseqid INT,
 div3wheelson INT,
 div3totalgtime INT,
 div3longestgtime INT,
 div3wheelsoff INT,
 div3tailnum INT,
 div4airport INT,
 div4airportid INT,
 div4airportseqid INT,
 div4wheelson INT,
 div4totalgtime INT,
 div4longestgtime INT,
 div4wheelsoff INT,
 div4tailnum INT,
 div5airport INT,
 div5airportid INT,
 div5airportseqid INT,
 div5wheelson INT,
 div5totalgtime INT,
 div5longestgtime INT,
 div5wheelsoff INT,
 div5tailnum INT,
 None INT)
ROW FORMAT DELIMITED FIELDS TERMINATED BY ','
LOCATION 's3://bjss-nyc-dev/onTimeData/'
TBLPROPERTIES ("skip.header.line.count"="1");
CREATE EXTERNAL TABLE IF NOT EXISTS airline_id (Code STRING, Description  STRING) 
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' 
LOCATION  's3://bjss-nyc-dev/otherdata/airline/'
TBLPROPERTIES ("skip.header.line.count"="1");
CREATE EXTERNAL TABLE IF NOT EXISTS carrier (Code STRING, Description  STRING) 
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' 
LOCATION  's3://bjss-nyc-dev/otherdata/carrier/'
TBLPROPERTIES ("skip.header.line.count"="1");
CREATE EXTERNAL TABLE IF NOT EXISTS months (Code STRING, Description  STRING) 
ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' 
LOCATION  's3://bjss-nyc-dev/otherdata/months/'
TBLPROPERTIES ("skip.header.line.count"="1");
CREATE EXTERNAL TABLE IF NOT EXISTS historicalTemp (Location STRING, Date DATE, Temp INT) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' LOCATION  's3://bjss-nyc-dev/historicaltemp/';