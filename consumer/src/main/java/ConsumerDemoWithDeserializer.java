import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.core.*;
import java.io.Serializable;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemoWithDeserializer implements LifecycleManager, Serializable {

    private static Logger logger = LoggerFactory.getLogger(ConsumerDemoWithDeserializer.class.getName());
    private boolean isConsuming = false;
    private Thread ConsumerThread;
    private TweetRepository tr;

    public void start() {
        tr = initCassandra();

        // Criar as propriedades do consumidor
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TweetDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "consumer_demo");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Criar o consumidor
        KafkaConsumer<String ,Tweet> consumer = new KafkaConsumer<>(properties);

        // Subscrever o consumidor para o nosso(s) tópico(s)
        consumer.subscribe(Collections.singleton("tweets-input"));

        //start control variable
        isConsuming = true;

        ConsumerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(isConsuming)
                {
                    ConsumerRecords<String, Tweet> poll = consumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord record : poll) {
                        Tweet tt = Tweet.class.cast(record.value());
                        System.out.println(tt.getGeoLocation());
                        try {
                            tr.insertTweet(tt);
                            tr.insertTweetByCountry(tt);
                        } catch (Exception e) {
                            System.out.println(e);
                            break;
                        }
                        logger.info(record.topic() + " - " + record.partition() + " - " + record.value());
                    }
                }
            }
        });
        ConsumerThread.start();
    }

    private static TweetRepository initCassandra() {
        Cluster cluster = null;
        try {
            cluster = Cluster.builder()
                    .addContactPoint("localhost")
                    .build();
            Session session = cluster.connect();
            ResultSet rs = session.execute("select release_version from system.local");
            Row row = rs.one();
            System.out.println(row.getString("release_version"));

            KeyspaceRepository sr = new KeyspaceRepository(session);
            sr.createKeyspace("tweets", "SimpleStrategy", 1);
            System.out.println("Creating Repository...\n");

            sr.useKeyspace("tweets");
            System.out.println("Using repository tweets...\n");

            TweetRepository tr = new TweetRepository(session);
            tr.createTable();
            System.out.println("Creating table Tweets...\n");

            tr.createTableTweetsByCountry();
            System.out.println("Creating table TweetsByUser...\n");

            return tr;

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public void stop() {
        isConsuming = false;
        tr.selectAll();
        System.out.println();
        tr.selectAllByCountry();
        System.out.println();
        tr.deleteTweetByCountry("United States");
        System.out.println();
        tr.selectAllByCountry();
        System.out.println();
        //tr.deleteTable("tweets");
        //tr.deleteTable("tweetsByCountry");
    }
}
