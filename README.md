# SeminarPigHive

## I. Installation de l'environnement de travail sur Ubuntu (Hadoop, Pig et Hive)
source : https://hadoop.apache.org/docs/r3.0.0/hadoop-project-dist/hadoop-common/SingleCluster.html

### a. pré-requis
```shell
sudo apt-get install ssh
sudo apt-get install pdsh
sudo apt-get install rsync
sudo apt-get --no-install-recommends install maven
```
### b. Téléchargements des sources
```shell
cd ~/Download || cd ~/Downloads || cd ~/Téléchargements

wget http://mirrors.standaloneinstaller.com/apache/hadoop/common/hadoop-2.9.0/hadoop-2.9.0.tar.gz #(350 M => 6 min à 1 MB/s)
wget http://apache.mindstudios.com/pig/pig-0.17.0/pig-0.17.0.tar.gz                               #(220 M => 4 min à 1 MB/s)
wget http://apache.claz.org/hive/stable/apache-hive-1.2.2-bin.tar.gz                              #(87 M => 1,5 min à 1 MB/s)
wget https://donnees.roulez-eco.fr/opendata/instantane -O PrixCarburants_instantane.xml.zip       #(1 M)
wget http://central.maven.org/maven2/org/apache/pig/piggybank/0.17.0/piggybank-0.17.0.jar         #(1 M)
```
### c. Décompression des sources dans le répertoire ~/hadoop
```shell
mkdir ~/hadoop
sudo mkdir /user
sudo chown -R $USER:$USER /user
mkdir -p  /user/hive/warehouse/


chmod -R 777  /user/hive

tar -xzvf hadoop-2.9.0.tar.gz -C ~/hadoop && rm hadoop-2.9.0.tar.gz
tar -xzvf pig-0.17.0.tar.gz -C ~/hadoop && rm pig-0.17.0.tar.gz
tar -xzvf apache-hive-1.2.2-bin.tar.gz -C ~/hadoop && rm apache-hive-1.2.2-bin.tar.gz
unzip PrixCarburants_instantane.xml.zip -d ~/hadoop && rm PrixCarburants_instantane.xml.zip
mv piggybank-0.17.0.jar ~/hadoop/
```
### d. Création des variables d'environnement
 * Vérifier la varaiable ***$JAVA_HOME*** :
```shell
echo $JAVA_HOME
```

 * Si ***$JAVA_HOME*** est non renseigné :
```shell
ls JAVA_HOME=/usr/lib/jvm/
export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_102/jre
```

 * Création des variables pour Hadoop, Pig et Hive :
```shell
export HADOOP_HOME=~/hadoop/hadoop-2.9.0
export PIG_HOME=~/hadoop/pig-0.17.0
export HIVE_HOME=~/hadoop/apache-hive-1.2.2-bin
export HCAT_HOME=~/hadoop/apache-hive-1.2.2-bin/hcatalog
export HIVE_CONF_DIR=$HIVE_HOME/conf
export HIVE_AUX_JARS_PATH=$HIVE_HOME/hcatalog/share/hcatalog/hive-hcatalog-core.jar
```

### e. Création des applicatifs
```shell
cd ~/hadoop

echo "$PIG_HOME/bin/pig -Dpig.additional.jars=$PIG_HOME/../piggybank-0.17.0.jar:$HIVE_HOME/lib/datanucleus-*.jar:$HIVE_HOME/lib/derby-10.10.2.0.jar -x local -useHCatalog" > pig
echo "$HIVE_HOME/bin/hive" > hive

chmod +x pig
chmod +x hive


mv $HADOOP_HOME/etc/hadoop/hadoop-env.sh $HADOOP_HOME/etc/hadoop/initial_hadoop-env.sh
JAVA_HOME_ESCAPED=$(echo $JAVA_HOME | sed -e "s/\//\\\\\//g")
cat $HADOOP_HOME/etc/hadoop/initial_hadoop-env.sh | sed -e "s/.*export JAVA_HOME.*= *$/export JAVA_HOME=$JAVA_HOME_ESCAPED/g" > $HADOOP_HOME/etc/hadoop/hadoop-env.sh

ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa
cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys
chmod 0600 ~/.ssh/authorized_keys
```

### f. Get Started
#### Start Hive
```
sh ~/hadoop/hive
```
#### Start Pig
```
sh ~/hadoop/pig
```
