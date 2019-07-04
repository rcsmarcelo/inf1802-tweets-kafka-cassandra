import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.Serializable;
import java.util.Properties;

public class Stream implements LifecycleManager, Serializable {
    private static String _consumerKey = System.getenv().get("CONSUMER_KEY");
    private static String _consumerSecret = System.getenv().get("CONSUMER_SECRET");
    private static String _accessToken = System.getenv().get("TWITTER_ACCESS_TOKEN");
    private static String _accessTokenSecret = System.getenv().get("ACCESS_TOKEN_SECRET");

    private TwitterStream Ts;
    private StatusListener Listener;
    private KafkaProducer<String, Tweet> Producer;

    public static TwitterStream getTwitterStreamInstance() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(_consumerKey)
                .setOAuthConsumerSecret(_consumerSecret)
                .setOAuthAccessToken(_accessToken)
                .setOAuthAccessTokenSecret(_accessTokenSecret);
        TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
        return tf.getInstance();
    }

    private void configProducer() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TweetSerializer.class.getName());

        Producer = new KafkaProducer<String, Tweet>(properties);
    }

    public void start() {
        Ts = getTwitterStreamInstance();
        configProducer();
        Listener = new StatusListener() {
            public void onStatus(Status status) {
                Tweet tt;
                if (status.getGeoLocation() != null)
                    tt = new Tweet(status.getId(), status.getUser().getName(), status.getText(), status.getCreatedAt(),
                            status.getPlace().getCountry(), status.getSource(), status.isTruncated(), status.getGeoLocation(),
                            status.isFavorited(), status.isRetweeted(), status.getContributors().toString());
                else {
                    GeoLocation geo = new GeoLocation(0,0);
                    tt = new Tweet(status.getId(), status.getUser().getName(), status.getText(), status.getCreatedAt(),
                            status.getPlace().getCountry(), status.getSource(), status.isTruncated(), geo,
                            status.isFavorited(), status.isRetweeted(), status.getContributors().toString());
                }
                System.out.println("@" + tt.getUsername() + ":" + " " + tt.getTweetText());
                ProducerRecord<String, Tweet> Record = new ProducerRecord<String, Tweet>
                        ("tweets-input", tt);
                Producer.send(Record, new Callback() {
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if (e == null) {
                            System.out.println("Success");
                        } else
                            System.out.println("Error: " + e);
                    }
                });
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
            public void onTrackLimitationNotice(int i) {}
            public void onScrubGeo(long l, long l1) {}
            public void onStallWarning(StallWarning stallWarning) {}
            public void onException(Exception e) {}
        };
        Ts.addListener(Listener);

        String terms = "trump";
        FilterQuery query = new FilterQuery();
        query.track(terms.split(","));
        Ts.filter(query);

    }

    public void stop() {
        Ts.shutdown();
        Producer.close();
    }
}
