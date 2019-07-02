import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import twitter4j.GeoLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TweetRepository {
    private static final String TABLE_NAME = "Tweets";
    private static final String TABLE_NAME_BY_COUNTRY = TABLE_NAME + "ByCountry";
    private Session session;

    public TweetRepository(Session session) {
        this.session = session;
    }

    public void createTable() {
        System.out.println("createTableTweets – init");

        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(TABLE_NAME).append("(")
                .append("id uuid PRIMARY KEY, ")
                .append("tweettext text, ")
                .append("username text, ")
                .append("datesent date, ")
                .append("country text, ")
                .append("source text, ")
                .append("istruncated boolean, ")
                .append("latitude double, ")
                .append("longitude double, ")
                .append("isfavorited boolean, ")
                .append("isretweeted boolean, ")
                .append("contributors list<bigint>);");
        final String query = sb.toString();

        System.out.println(query);

        System.out.println("createTableTweets – end");
        session.execute(query);
    }

    public void createTableTweetsByCountry() {
        System.out.println("createTableTweetsByCountry – init");

        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(TABLE_NAME_BY_COUNTRY).append("(")
                .append("id uuid,")
                .append("tweettext text, ")
                .append("username text, ")
                .append("datesent date, ")
                .append("country text,")
                .append("source text, ")
                .append("istruncated boolean, ")
                .append("latitude double, ")
                .append("longitude double, ")
                .append("isfavorited boolean, ")
                .append("isretweeted boolean, ")
                .append("contributors list<bigint>, PRIMARY KEY ((id, country)));");
        final String query = sb.toString();

        System.out.println(query);

        System.out.println("createTableTweetByCountry – end");
        session.execute(query);
    };

    public void insertTweet(Tweet tweet) {
        System.out.println("insertTweet – init");

        StringBuilder sb = new StringBuilder("INSERT INTO ")
                .append(TABLE_NAME).append("(id, tweettext, username, datesent, country, source" +
                        ", istruncated, latitude, longitude, isfavorited, isretweeted, contributors) ")
                .append("VALUES (").append(tweet.getID()).append(", '")
                .append(tweet.getUsername()).append("', '")
                .append(tweet.getTweetText()).append("', '")
                .append(tweet.getDateSent()).append("', '")
                .append(tweet.getCountry()).append("', '")
                .append(tweet.getSource()).append("', ")
                .append(tweet.isTruncated()).append(", ")
                .append(tweet.getGeoLocation().getLatitude()).append(", ")
                .append(tweet.getGeoLocation().getLongitude()).append(", ")
                .append(tweet.isFavorited()).append(", ")
                .append(tweet.isRetweeted()).append(", ")
                .append(tweet.getContributors()).append(");");
        final String query = sb.toString();
        session.execute(query);

        System.out.println(query);

        System.out.println("insertTweet – end");
        session.execute(query);
    }

    public void insertTweetByCountry(Tweet tweet) {
        System.out.println("insertTweetByCountry – init");

        StringBuilder sb = new StringBuilder("INSERT INTO ")
                .append(TABLE_NAME_BY_COUNTRY).append("(id, username, tweettext, datesent, country, source" +
                        ", istruncated, latitude, longitude, isfavorited, isretweeted, contributors) ")
                .append("VALUES (").append(tweet.getID()).append(", '")
                .append(tweet.getUsername()).append("', '")
                .append(tweet.getTweetText()).append("', '")
                .append(tweet.getDateSent()).append("', '")
                .append(tweet.getCountry()).append("', '")
                .append(tweet.getSource()).append("', ")
                .append(tweet.isTruncated()).append(", ")
                .append(tweet.getGeoLocation().getLatitude()).append(", ")
                .append(tweet.getGeoLocation().getLongitude()).append(", ")
                .append(tweet.isFavorited()).append(", ")
                .append(tweet.isRetweeted()).append(", ")
                .append(tweet.getContributors()).append(");");
        final String query = sb.toString();
        session.execute(query);

        System.out.println(query);

        System.out.println("insertTweetByCountry – end");
        session.execute(query);

    }

    public List<Tweet> selectAll() {
        System.out.println("selectAll – init");

        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME);

        final String query = sb.toString();
        System.out.println(query);
        ResultSet rs = session.execute(query);
        List<Tweet> tweets = new ArrayList<Tweet>();
        for (Row r : rs) {
            GeoLocation geo = new GeoLocation(r.getDouble("latitude"), r.getDouble("longitude"));
            Tweet tt = new Tweet(r.getLong("id"), r.getString("username"),
                    r.getString("tweettext"), r.getDate("datesent"), r.getString("country"),
                    r.getString("source"), r.getBool("istruncated"), geo,
                    r.getBool("isfavorited"), r.getBool("isretweeted"), null);
            System.out.println(tt.getCountry() + "  " + tt.getID() + " @" + tt.getUsername() + ":" + " " + tt.getTweetText());
            tweets.add(tt);
        }
        System.out.println("selectAll – end");
        return tweets;
    }

    public List<Tweet> selectAllByCountry() {
        System.out.println("selectAllyCountry – init");

        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME_BY_COUNTRY);

        final String query = sb.toString();
        System.out.println(query);
        ResultSet rs = session.execute(query);
        List<Tweet> tweets = new ArrayList<Tweet>();
        for (Row r : rs) {
            GeoLocation geo = new GeoLocation(r.getDouble("latitude"), r.getDouble("longitude"));
            Tweet tt = new Tweet(r.getLong("id"), r.getString("username"),
                    r.getString("tweettext"), r.getDate("datesent"), r.getString("country"),
                    r.getString("source"), r.getBool("istruncated"), geo,
                    r.getBool("isfavorited"), r.getBool("isretweeted"), null);
            System.out.println(tt.getCountry() + "  " + tt.getID() + " @" + tt.getUsername() + ":" + " " + tt.getTweetText());
            tweets.add(tt);
        }
        System.out.println("selectAllyCountry – end");
        return tweets;
    }

    public Tweet selectTweetByCountry(String country) {
        System.out.println("selectTweetByCountry – init");

        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME_BY_COUNTRY)
                .append(" WHERE country = '").append(country).append("' ALLOW FILTERING;");

        final String query = sb.toString();
        ResultSet rs = session.execute(query);
        System.out.println(query);
        List<Tweet> tweets = new ArrayList<Tweet>();
        for (Row r : rs) {
            GeoLocation geo = new GeoLocation(r.getDouble("latitude"), r.getDouble("longitude"));
            Tweet tt = new Tweet(r.getLong("id"), r.getString("username"),
                    r.getString("tweettext"), r.getDate("datesent"), r.getString("country"),
                    r.getString("source"), r.getBool("istruncated"), geo,
                    r.getBool("isfavorited"), r.getBool("isretweeted"), null);
            System.out.println(tt.getCountry() + "  " + tt.getID() + " @" + tt.getUsername() + ":" + " " + tt.getTweetText());
            tweets.add(tt);
        }
        System.out.println("selectTweetByCountry – end");
        return tweets.get(0);
    }

    public void deleteTweet(UUID id) {
        System.out.println("deleteTweet – init");
        StringBuilder sb = new StringBuilder("DELETE FROM ")
                .append(TABLE_NAME).append(" WHERE id = ")
                .append(id).append(";");
        final String query = sb.toString();
        System.out.println(query);
        session.execute(query);
        System.out.println("deleteTweet – end");
    }

    public void deleteTweetByCountry(String country, UUID id) {
        System.out.println("deleteTweetByCountry – init");
        StringBuilder sb = new StringBuilder("DELETE FROM ")
                .append(TABLE_NAME_BY_COUNTRY).append(" WHERE country = '")
                .append(country).append("' AND id =").append(id).append(";");
        final String query = sb.toString();
        session.execute(query);
        System.out.println(query);
        System.out.println("deleteTweetByCountry – end");
    }

    public void deleteTable(String tableName) {
        System.out.println("deleteTable – init");

        StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS ").append(tableName);

        final String query = sb.toString();
        System.out.println(query);
        session.execute(query);
        System.out.println("deleteTable – end");
    }
}
