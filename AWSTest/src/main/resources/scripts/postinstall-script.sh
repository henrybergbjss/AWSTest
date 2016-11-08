#!/bin/sh
#install rabbitmq
cat <<EOF > /etc/apt/sources.list.d/rabbitmq.list
deb http://www.rabbitmq.com/debian/ testing main
EOF

curl https://www.rabbitmq.com/rabbitmq-release-signing-key.asc -o /tmp/rabbitmq-release-signing-key.asc
apt-key add /tmp/rabbitmq-release-signing-key.asc
rm /tmp/rabbitmq-release-signing-key.asc

apt-get -qy update
apt-get -qy install rabbitmq-server

#rabbitmq config
/usr/sbin/rabbitmq-plugins/rabbitmq-plugins enable rabbitmq_management
/usr/sbin/rabbitmqctl stop
/usr/sbin/rabbitmq-server -detached
rabbitmqctl add_user admin admin
rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"
rabbitmqctl set_user_tags admin administrator

#install postgresql
apt-get install -y postgresql
apt-get install -y pgAdmin3

wget https://s3.amazonaws.com/elastic.snaplogic.com/snaplogic-sidekick-4.mrc260-x86_64.deb
mv snaplogic-sidekick-4.mrc260-x86_64.deb /var/cache/apt/archives/
dpkg -i /var/cache/apt/archives/snaplogic-sidekick-4.mrc260-x86_64.deb
apt-get -fy install

sudo cat <<EOF>/opt/snaplogic/etc/keys.properties
cc.admin_uri = https://elastic.snaplogic.com:443
cc.username = cc+rollsroyceenterprise@snaplogic.com
cc.api_key = JDJhJDEwJHpyOFRnV3k3bEFOc3A1TUltUXNRYU9pWnRqdXlqbzZDVmpyR1NxRXNCaE5ROXRwbUlJU2Qu
cc.ssl_verify = True
EOF

sed -i /opt/snaplogic/etc/global.properties -e 's/jcc.subscriber_id.*/jcc\.subscriber_id\ \=\ RollsRoyceEnterprise/'
sed -i /opt/snaplogic/etc/global.properties -e 's/jcc.environment.*/jcc\.environment\ \=\ gbjss/'
ln -s /opt/snaplogic/bin/jcc.sh /etc/init.d/snaplex
update-rc.d -f snaplex start 60 2 3 4 5 . stop 99 2 3 4 5 .
/etc/inid.d/snaplex start

cd /tmp
curl https://amazon-ssm-us-east-1.s3.amazonaws.com/latest/debian_amd64/amazon-ssm-agent.deb -o amazon-ssm-agent.deb
dpkg -i amazon-ssm-agent.deb