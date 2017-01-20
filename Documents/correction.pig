
%default dir 'file:///home/corentin/Bureau/test'

REGISTER /home/corentin/Bureau/elephant-bird-pig-4.1.jar;
REGISTER /home/corentin/Bureau/elephant-bird-core-4.1.jar;
REGISTER /home/corentin/Bureau/elephant-bird-hadoop-compat-4.1.jar;
REGISTER /home/corentin/Bureau/json-simple-1.1.jar;
REGISTER /home/corentin/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-api-1.7.10.jar
REGISTER /home/corentin/hadoop/hadoop-2.7.2/share/hadoop/common/lib/slf4j-log4j12-1.7.10.jar

REGISTER /home/corentin/hadoop/apache-hive-1.2.1-bin/hcatalog/share/hcatalog/hive-hcatalog-core-1.2.1.jar;
REGISTER /home/corentin/hadoop/apache-hive-1.2.1-bin/hcatalog/share/hcatalog/hive-hcatalog-pig-adapter-1.2.1.jar
REGISTER /home/corentin/hadoop/apache-hive-1.2.1-bin/hcatalog/share/hcatalog/hive-hcatalog-server-extensions-1.2.1.jar
REGISTER /home/corentin/hadoop/apache-hive-1.2.1-bin/hcatalog/share/hcatalog/hive-hcatalog-streaming-1.2.1.jar


A = LOAD '$dir/staging/*' USING com.twitter.elephantbird.pig.load.JsonLoader('-nestedLoad') as (event: map[]);

B = FOREACH A GENERATE
	(chararray)event#'userId' as user_id,
	ToDate((chararray)event#'ts') as ts,
	(chararray)event#'eventName' as event_name,
	(map[])event#'event' as event;

SPLIT B INTO
	SUBSCR_A IF event_name=='subcription',
	UNSUBS_A IF event_name=='unsubscription',
	CONNEC_A IF event_name=='connection',
	DISCON_A IF event_name=='disconnection',
	DISCUS_A IF event_name=='discussion_start',
	DISCUE_A IF event_name=='discussion_end',
	FLOWER_A IF event_name=='flower';

SUBSCR_B = FOREACH SUBSCR_A GENERATE
	user_id,
	ts,
	(chararray)event#'firstname' as firstname,
	(chararray)event#'lastname' as lastname,
	(int)event#'age' as age,
	(int)event#'sexe' as sexe,
	(chararray)event#'city' as city,
	(chararray)event#'geopoint' as geopoint;

CONNEC_B = FOREACH CONNEC_A GENERATE 
	user_id,
	ts;

STORE SUBSCR_B INTO 'subscription' using PigStorage(';');
STORE CONNEC_B INTO 'connection' using PigStorage(';');

A = LOAD 'subscription' using PigStorage(';') as (user_id: chararray,ts: datetime,firstname: chararray,lastname: chararray,age: int,sexe: int,city: chararray,geopoint: chararray);
B = GROUP A BY (GetYear(ts), GetMonth(ts), GetDay(ts));
C = FOREACH B GENERATE group.$0 as y, group.$1 as m, group.$2 as d, SIZE(A);
Dump C;


A = LOAD 'connection' using PigStorage(';') as (user_id: chararray,ts: datetime);
B = LOAD 'subscription' using PigStorage(';') as (user_id: chararray,ts: datetime,firstname: chararray,lastname: chararray,age: int,sexe: int,city: chararray,geopoint: chararray);
C = JOIN A BY user_id, B BY user_id;

D = GROUP C BY (B::sexe,A::user_id);

E = FOREACH D GENERATE group.$0 as sexe, group.$1 as user_id, SIZE(C) as num_connection;
F = GROUP E BY sexe;
G = FOREACH F GENERATE group as sexe,SIZE(E) as num_user, AVG(E.num_connection);






