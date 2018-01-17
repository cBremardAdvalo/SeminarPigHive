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
unzip PrixCarburants_instantane.zip -d ~/hadoop && rm PrixCarburants_instantane.zip
mv piggybank-0.17.0.jar ~/hadoop/
```
### d. Création des variables d'environnement
 * Vérifier la varaiable ***$JAVA_HOME*** :
```shell
echo $JAVA_HOME
```

 * Si ***$JAVA_HOME*** est non renseigné :
```shell
ls /usr/lib/jvm/
export JAVA_HOME=/usr/lib/jvm/default-6java/jre
```

### e. Création des applicatifs
```shell
# Go to workspace
cd ~/hadoop

# Export variables
export HADOOP_HOME=~/hadoop/hadoop-2.9.0
export PIG_HOME=~/hadoop/pig-0.17.0
export HIVE_HOME=~/hadoop/apache-hive-1.2.2-bin
export HCAT_HOME=~/hadoop/apache-hive-1.2.2-bin/hcatalog
export HIVE_CONF_DIR=$HIVE_HOME/conf
export HIVE_AUX_JARS_PATH=$HIVE_HOME/hcatalog/share/hcatalog/hive-hcatalog-core.jar

# Create shell script to run Pig
echo "export HADOOP_HOME=~/hadoop/hadoop-2.9.0" > pig
echo "export PIG_HOME=~/hadoop/pig-0.17.0" >> pig
echo "export HIVE_HOME=~/hadoop/apache-hive-1.2.2-bin" >> pig
echo "export HCAT_HOME=~/hadoop/apache-hive-1.2.2-bin/hcatalog" >> pig
echo "export HIVE_CONF_DIR=$HIVE_HOME/conf" >> pig
echo "export HIVE_AUX_JARS_PATH=$HIVE_HOME/hcatalog/share/hcatalog/hive-hcatalog-core.jar" >> pig
echo "$PIG_HOME/bin/pig -Dpig.additional.jars=$PIG_HOME/../piggybank-0.17.0.jar:$HIVE_HOME/lib/datanucleus-*.jar:$HIVE_HOME/lib/derby-10.10.2.0.jar -x local -useHCatalog" >> pig
chmod +x pig

# Create shell script to run Hive
echo "export HADOOP_HOME=~/hadoop/hadoop-2.9.0" > hive
echo "export PIG_HOME=~/hadoop/pig-0.17.0" >> hive
echo "export HIVE_HOME=~/hadoop/apache-hive-1.2.2-bin" >> hive
echo "export HCAT_HOME=~/hadoop/apache-hive-1.2.2-bin/hcatalog" >> hive
echo "export HIVE_CONF_DIR=$HIVE_HOME/conf" >> hive
echo "export HIVE_AUX_JARS_PATH=$HIVE_HOME/hcatalog/share/hcatalog/hive-hcatalog-core.jar" >> hive
echo "$HIVE_HOME/bin/hive" >> hive
chmod +x hive

# Update run file "hadoop-env.sh" with variable "JAVA_HOME"
mv $HADOOP_HOME/etc/hadoop/hadoop-env.sh $HADOOP_HOME/etc/hadoop/initial_hadoop-env.sh
JAVA_HOME_ESCAPED=$(echo $JAVA_HOME | sed -e "s/\//\\\\\//g")
cat $HADOOP_HOME/etc/hadoop/initial_hadoop-env.sh | sed -e "s/.*export JAVA_HOME.*= *$/export JAVA_HOME=$JAVA_HOME_ESCAPED/g" > $HADOOP_HOME/etc/hadoop/hadoop-env.sh

# Generate ssh key
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


<!--
```shell
mkdir -p ~/hadoop/hue
cd ~/hadoop/hue
wget https://www.dropbox.com/s/auwpqygqgdvu1wj/hue-4.1.0.tgz?dl=0 -O hue-4.1.0.tgz       #(46 M)

tar -xvzf hue-4.1.0.tgz

cd hue-4.1.0/

sudo mkdir -p /usr/local/hue
sudo chown -R $USER:$USER /usr/local/hue

sudo apt-get install git ant gcc g++ libffi-dev libkrb5-dev libmysqlclient-dev libsasl2-dev libsasl2-modules-gssapi-mit libsqlite3-dev libssl-dev libxml2-dev libxslt-dev make maven libldap2-dev python-dev python-setuptools libgmp3-dev
sudo apt-get install libldap2-dev
# sudo apt-get remove python-cffi-backend
sudo apt-get upgrade python3-cffi-backend

make install

/usr/local/hue/build/env/bin/supervisor
``` --> 
