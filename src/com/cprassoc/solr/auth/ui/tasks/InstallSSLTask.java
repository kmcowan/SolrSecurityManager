/*
  I used a self signed cert that I set up as follows.  (see ssl.txt)
 running the keytool from the $FUSION/apps/jetty/ui/etc directory

keytool -genkeypair -alias fusion-ssl -keyalg RSA -keysize 2048 -keypass secret -storepass secret -validity 9999 -keystore keystore.jks -ext SAN=DNS:localhost,IP:192.168.1.200,IP:127.0.0.1 -dname "CN=host003, OU=lucidworks, O=com, L=Location, ST=CO, C=USA"

1)  The keystore that you are using needs to be in $FUSION/apps/jetty/ui/etc
2) the password generated OBF needs to be set correctly.
3) here is what I set in start.in

jetty.ssl.port=8846
jetty.sslContext.keyStorePath=etc/keystore.jks
jetty.sslContext.trustStorePath=etc/keystore.jks
jetty.sslContext.keyStorePassword=OBF:1yta1t331v8w1v9q1t331ytc
jetty.sslContext.keyManagerPassword=OBF:1yta1t331v8w1v9q1t331ytc
jetty.sslContext.trustStorePassword=OBF:1yta1t331v8w1v9q1t331ytc

4)  I made no changes to fusion.properties because at this point I am only applying ssl to UI.  I include it just for your reference





export FUSION=/opt/fusion
export HOSTNAME=localhost

cd $FUSION/apps/jetty/ui/etc

keytool -genkeypair -alias fusion-ssl -keyalg RSA -keysize 2048 -keypass secret -storepass secret -validity 9999 -keystore keystore.jks -ext SAN=DNS:localhost,IP:192.168.1.200,IP:127.0.0.1 -dname "CN=host003, OU=lucidworks, O=com, L=Location, ST=CO, C=USA"

keytool -importkeystore -srckeystore keystore.jks -destkeystore keystore.p12 -srcstoretype jks -deststoretype pkcs12

cd $FUSION/apps/jetty/ui
java -jar $FUSION/apps/jetty/home/start.jar --add-to-start=https

cd $FUSION
java -cp $FUSION/apps/libs/jetty-util-9.3.8.v20160314.jar org.eclipse.jetty.util.security.Password secret

$FUSION/apps/solr-dist/server/scripts/cloud-scripts/zkcli.sh -zkhost $HOSTNAME:9983 -cmd put /clusterprops.json '{"urlScheme":"https"}'
 */
package com.cprassoc.solr.auth.ui.tasks;

import java.util.TimerTask;

/**
 *
 * @author kevin
 */
public class InstallSSLTask extends TimerTask implements Runnable {

    public void run() {

    }

}
