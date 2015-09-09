#!/bin/bash


sudo service mongodb stop
sudo service mysql stop
sudo service couchdb stop
sudo service couchbase-server stop
sudo service mysql stop
sudo service zookeeper stop
sudo service redis-server stop
sudo service rabbitmq-server stop
sudo service memcached stop
sudo service postgresql stop

sudo echo "wrapper.java.maxmemory=1750" >>/var/lib/neo4j/conf/neo4j-wrapper.conf 
sudo echo "wrapper.java.initmemory=256" >>/var/lib/neo4j/conf/neo4j-wrapper.conf 
sudo sed -i.bak 's/auth_enabled=true/auth_enabled=false/' /var/lib/neo4j/conf/neo4j-server.properties
sudo echo "dbms.pagecache.memory=100m" >>/var/lib/neo4j/conf/neo4j-server.properties
sudo nohup service neo4j-service start