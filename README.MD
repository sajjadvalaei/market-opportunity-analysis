Market Opportunity Analysis

Hello, this is Manual running guide:

1. First, you need to start zookeeper server and kafka server as https://kafka.apache.org/quickstart illustrated..

$ sudo bin/zookeeper-server-start.sh config/zookeeper.properties

$ sudo bin/kafka-server-start.sh config/server.properties

2. It's recommended to use maven to download and setup dependencies properly.

3. Copy resources/log4j.properties to target/classes if it's not there.(and
don't change the name of the project directory).
Or you can copy that wherever kafka finds that.
Otherwise, you won't be able to see any log of my code.

4. Set up an appropriate socks proxy (I'm using tor proxy which default is localhost:9150)

5. **run fetcher.Sender**: 
You can see sending data in System.out.
Or check data consuming by another consumer from command line. 
With this command:$ bin/kafka-console-consumer.sh --topic candlestick --from-beginning --bootstrap-server localhost:9092

6. **run notifier.Main**:
You can see consumed data in System.out.
And also in you can check mysql database.

7. **run api.APIHandler**:
You can see the data with link http://localhost:4567/notifications.

Hope you enjoy that. 

Thank you for being generous with your time. 
