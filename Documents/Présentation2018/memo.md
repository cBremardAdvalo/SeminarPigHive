# Pig
## Keywords
Afficher l'historique des commandes
```pig
HISTORY;
```

Afficher le schéma d'un alias
```pig
DESCRIBE alias;
```

Afficher un alias
```pig
DUMP alias;
```

Quitter le terminal
```pig
QUIT;
```


## Les loaders
### Loader xml
```pig
REGISTER piggybank.jar;
A = LOAD 'PrixCarburants_instantane.xml' USING org.apache.pig.piggybank.storage.XMLLoader('pdv') as (pdv:chararray);
```

### Loader csv
```pig
A = LOAD 'data/ratings.csv' USING PigStorage(',') as (user_id: long,movie_id: long,rating: double,timestamp: long);
```

### Loader json
```pig
REGISTER /shared/prod/libraries/elephant-bird-pig-4.5.jar;
REGISTER /shared/prod/libraries/elephant-bird-core-4.5.jar;
REGISTER /shared/prod/libraries/elephant-bird-hadoop-compat-4.5.jar;
A = LOAD 'fichier.json' USING com.twitter.elephantbird.pig.load.JsonLoader('-nestedLoad') AS (json:map []);
```

### Loader hive (avec optin -useHCatalog au lancement de pig)
```pig
A = LOAD 'db.table' USING org.apache.hive.hcatalog.pig.HCatLoader();
```

### Loader Hbase
```pig
REGISTER /usr/hdp/current/hbase-client/lib/hbase-client.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-common.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-hadoop2-compat.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-hadoop-compat.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-it.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-prefix-tree.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-protocol.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-server.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-thrift.jar;
REGISTER /usr/hdp/current/hadoop-client/client/htrace-core.jar;
REGISTER /usr/hdp/current/hbase-master/lib/zookeeper.jar;

A = LOAD 'hbase://table' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('cf:user_id,cf:movie_id,cf:rating,cf:timestamp', '-loadKey true') as (rowkey: chararray,user_id: long,movie_id: long,rating: double,timestamp: long);
```

## Les storers
### Storer csv
```pig
STORE A INTO 'data' USING PigStorage('\u001F');
```

### Storer json
```pig
STORE A INTO 'data' USING JsonStorage();
```

### Storer hive (avec optin -useHCatalog au lancement de pig)
```pig
STORE A INTO 'db.table' USING org.apache.hive.hcatalog.pig.HCatStorer();
```

### Storer Hbase
```pig
REGISTER /usr/hdp/current/hbase-client/lib/hbase-client.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-common.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-hadoop2-compat.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-hadoop-compat.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-it.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-prefix-tree.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-protocol.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-server.jar;
REGISTER /usr/hdp/current/hbase-client/lib/hbase-thrift.jar;
REGISTER /usr/hdp/current/hadoop-client/client/htrace-core.jar;
REGISTER /usr/hdp/current/hbase-master/lib/zookeeper.jar;

B = FOREACH A GENERATE rowkey, user_id, movie_id, rating, timestamp;
STORE B INTO 'hbase://table' USING org.apache.pig.backend.hadoop.hbase.HBaseStorage('cf:user_id,cf:movie_id,cf:rating,cf:timestamp');

```
