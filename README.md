# SeminarPigHive

## I. Installation de l'environnement de travail sur Ubuntu (Hadoop, Pig et Hive)
source : https://hadoop.apache.org/docs/r2.7.2/hadoop-project-dist/hadoop-common/SingleCluster.html

### a. pré-requis
```
sudo apt-get install ssh
sudo apt-get install rsync
sudo apt-get --no-install-recommends install maven
```
### b. Téléchargements des sources
```
cd ~/Download || cd ~/Downloads || cd ~/Téléchargements

wget http://apache.crihan.fr/dist/hadoop/common/stable2/hadoop-2.7.2.tar.gz
wget http://apache.mindstudios.com/pig/pig-0.15.0/pig-0.15.0.tar.gz
wget http://apache.claz.org/hive/stable/apache-hive-1.2.1-bin.tar.gz
```
### c. Décompression des sources dans le répertoire ~/hadoop
```
mkdir ~/hadoop

tar -xzvf hadoop-2.7.2.tar.gz -C ~/hadoop && rm hadoop-2.7.2.tar.gz
tar -xzvf pig-0.15.0.tar.gz -C ~/hadoop && rm pig-0.15.0.tar.gz
tar -xzvf apache-hive-1.2.1-bin.tar.gz -C ~/hadoop && rm apache-hive-1.2.1-bin.tar.gz
```
### d. Création des variables d'environnement
```
export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_102/jre
export HADOOP_HOME=~/hadoop/hadoop-2.7.2
export PIG_HOME=~/hadoop/pig-0.15.0
export HIVE_HOME=~/hadoop/apache-hive-1.2.1-bin
export HCAT_HOME=~/hadoop/apache-hive-1.2.1-bin/hcatalog
```
### e. Création des applicatifs
```
cd ~/hadoop

echo "$PIG_HOME/bin/pig -x local -useHCatalog" > pig
echo "$HIVE_HOME/bin/hive" > hive
echo "$HADOOP_HOME/bin/hdfs namenode -format" > cleanDFS
echo "$HADOOP_HOME/sbin/start-dfs.sh" > startDFS
echo "$HADOOP_HOME/sbin/stop-dfs.sh" > stopDFS

chmod +x pig
chmod +x hive
chmod +x cleanDFS
chmod +x startDFS
chmod +x stopDFS

sed -i 's@export JAVA_HOME=${JAVA_HOME}@export JAVA_HOME='"$JAVA_HOME"'@' $HADOOP_HOME/etc/hadoop/hadoop-env.sh
ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa
cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys
chmod 0600 ~/.ssh/authorized_keys
```

### f. Get Started
#### Start HDFS
```
sh cleanDFS
sh startDFS
```
#### Start Hive
```
sh hive
```
#### Start Pig
```
sh pig
```
#### STOP HDFS
```
sh stopDFS
```

