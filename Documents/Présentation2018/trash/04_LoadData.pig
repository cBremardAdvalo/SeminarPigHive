ID_RELATION_A = LOAD 'data/store_id_relation.csv' USING PigStorage(',') AS (air_store_id: chararray,hpg_store_id: chararray);
ID_RELATION = FILTER ID_RELATION_A BY air_store_id!='air_store_id';


AIR_RESERVE_A = LOAD 'data/air_reserve.csv' USING PigStorage(',') AS (air_store_id: chararray,visit_datetime: chararray,reserve_datetime: chararray,reserve_visitors: int);
AIR_RESERVE_B = FILTER AIR_RESERVE_A BY air_store_id!='air_store_id';
AIR_RESERVE_C = JOIN AIR_RESERVE_B BY (air_store_id) LEFT, ID_RELATION BY (air_store_id);
AIR_RESERVE = FOREACH AIR_RESERVE_C GENERATE
          AIR_RESERVE_B::air_store_id as air_store_id,
          ID_RELATION::hpg_store_id as hpg_store_id,
          ToDate(AIR_RESERVE_B::reserve_datetime,'YYYY-MM-DD HH:mm:ss')  as reserve_datetime,
          ToDate(AIR_RESERVE_B::visit_datetime,'YYYY-MM-DD HH:mm:ss') as visit_datetime,
          AIR_RESERVE_B::reserve_visitors as reserve_visitors;

HPG_RESERVE_A = LOAD 'data/hpg_reserve.csv' USING PigStorage(',') AS (hpg_store_id: chararray,visit_datetime: chararray,reserve_datetime: chararray,reserve_visitors: int);
HPG_RESERVE_B = FILTER HPG_RESERVE_A BY hpg_store_id!='hpg_store_id';
HPG_RESERVE_C = JOIN HPG_RESERVE_B BY (hpg_store_id) LEFT, ID_RELATION BY (hpg_store_id);
HPG_RESERVE = FOREACH HPG_RESERVE_C GENERATE
          ID_RELATION::air_store_id as air_store_id,
          HPG_RESERVE_B::hpg_store_id as hpg_store_id,
          ToDate(HPG_RESERVE_B::reserve_datetime,'YYYY-MM-DD HH:mm:ss')  as reserve_datetime,
          ToDate(HPG_RESERVE_B::visit_datetime,'YYYY-MM-DD HH:mm:ss') as visit_datetime,
          HPG_RESERVE_B::reserve_visitors as reserve_visitors;

RESERVE = UNION AIR_RESERVE, HPG_RESERVE;

STORE RESERVE INTO 'restaurant.reserve' USING org.apache.hive.hcatalog.pig.HCatStorer();










AIR_INFO_A = LOAD 'data/air_store_info.csv' USING PigStorage(',') AS (air_store_id: chararray,air_genre_name: chararray,air_area_name: chararray,latitude: double,longitude: double);
AIR_INFO_B = FILTER AIR_INFO_A BY air_store_id!='air_store_id';
AIR_INFO_C = JOIN AIR_INFO_B BY (air_store_id) LEFT, ID_RELATION BY (air_store_id);
AIR_INFO = FOREACH AIR_INFO_C GENERATE
          AIR_INFO_B::air_store_id as air_store_id,
          ID_RELATION::hpg_store_id as hpg_store_id,
          AIR_INFO_B::air_genre_name  as genre_name,
          AIR_INFO_B::air_area_name as area_name,
          AIR_INFO_B::latitude as latitude,
          AIR_INFO_B::longitude as longitude;

HPG_INFO_A = LOAD 'data/hpg_reserve.csv' USING PigStorage(',') AS (hpg_store_id: chararray,hpg_genre_name: chararray,hpg_area_name: chararray,latitude: double,longitude: double);
HPG_INFO_B = FILTER HPG_INFO_A BY hpg_store_id!='hpg_store_id';
HPG_INFO_C = JOIN HPG_INFO_B BY (hpg_store_id) LEFT, ID_RELATION BY (hpg_store_id);
HPG_INFO = FOREACH HPG_INFO_C GENERATE
          ID_RELATION::air_store_id as air_store_id,
          HPG_INFO_B::hpg_store_id as hpg_store_id,
          HPG_INFO_B::hpg_genre_name  as genre_name,
          HPG_INFO_B::hpg_area_name as area_name,
          HPG_INFO_B::latitude as latitude,
          HPG_INFO_B::longitude as longitude;

INFO = UNION AIR_INFO, HPG_INFO;

STORE INFO INTO 'restaurant.info' USING org.apache.hive.hcatalog.pig.HCatStorer();
