import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class TweetSerializer implements Serializer<Tweet> {
    ObjectMapper mapper = new ObjectMapper();

    public byte[] serialize(String s, Tweet tt) {
        try {
            return mapper.writeValueAsBytes(tt);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void configure(Map<String, ?> map, boolean b) {}

    public void close() {}
}
