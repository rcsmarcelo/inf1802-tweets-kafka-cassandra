import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.datastax.driver.core.*;
import com.datastax.driver.core.utils.UUIDs;
import twitter4j.GeoLocation;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemoWithDeserializer {

    private static Logger logger = LoggerFactory.getLogger(ConsumerDemoWithDeserializer.class.getName());

    public static void main(String[] args){

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
        consumer.subscribe(Collections.singleton("meu_topico"));

        initCassandra();

        // Ler as mensagens
        while (true) {  // Apenas como demo, usaremos um loop infinito
            ConsumerRecords<String, Tweet> poll = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord record : poll) {
                Tweet tt = Tweet.class.cast(record);
                logger.info(record.topic() + " - " + record.partition() + " - " + record.value());
            }
        }
    }

    private static void initCassandra() {
        System.out.println("Sup");
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

        } finally {
            if (cluster != null) cluster.close();
        }
    }
}
