import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class TweetDeserializer implements Deserializer<Tweet> {
    ObjectMapper mapper = new ObjectMapper();

    public TweetDeserializer() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        mapper.registerModule(javaTimeModule);
    }

    @Override
    public Tweet deserialize(String key, byte[] value) {
        Tweet user = null;
        try {
            user = mapper.readValue(value, Tweet.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void configure(Map<String, ?> map, boolean b) {
    }

    @Override
    public void close() {
    }
}
