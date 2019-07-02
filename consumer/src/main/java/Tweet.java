import java.util.Date;

public class Tweet {
    private long        ID;
    private String      Username;
    private String      TweetText;
    private Date        DateSent;
    private String      Country;
    private String      Source;
    private boolean     Truncated;
    private twitter4j.GeoLocation GeoLocation;
    private boolean     Favorited;
    private boolean     Retweeted;
    private long[]      Contributors;

    public Tweet() {}

    public Tweet(long id, String username, String tttext, Date date, String country, String source,
                 boolean truncated, twitter4j.GeoLocation geolocation, boolean favorited, boolean retweeted,
                 long[] contributors) {
        ID = id;
        Username = username;
        TweetText = tttext;
        DateSent = date;
        Country = country;
        Source = source;
        Truncated = truncated;
        GeoLocation = geolocation;
        Favorited = favorited;
        Retweeted = retweeted;
        Contributors = contributors;
    }

    public void setUsername(String username) {
        this.Username = username;
    }

    public void setTweetText(String tweetText) {
        this.TweetText = tweetText;
    }

    public void setDateSent(Date dateSent) {
        this.DateSent = dateSent;
    }

    public void setID(long id) {
        this.ID = id;
    }

    public void setSource(String source) {
        Source = source;
    }

    public void setTruncated(boolean truncated) {
        Truncated = truncated;
    }

    public void setGeoLocation(twitter4j.GeoLocation geoLocation) {
        GeoLocation = geoLocation;
    }

    public void setFavorited(boolean favorited) {
        Favorited = favorited;
    }

    public void setRetweeted(boolean retweeted) {
        Retweeted = retweeted;
    }

    public void setCountry(String country) { Country = country; }

    public void setContributors(long[] contributors) { Contributors = contributors; }

    public String getUsername() {
        return Username;
    }

    public Date getDateSent() {
        return DateSent;
    }

    public String getCountry() { return Country; }

    public String getTweetText() {
        return TweetText;
    }

    public long getID () {
        return ID;
    }

    public String getSource() {
        return Source;
    }

    public boolean isTruncated() {
        return Truncated;
    }

    public twitter4j.GeoLocation getGeoLocation() {
        return GeoLocation;
    }

    public boolean isFavorited() {
        return Favorited;
    }

    public boolean isRetweeted() {
        return Retweeted;
    }

    public long[] getContributors() { return Contributors; }

    public String toString() { return "@" + this.Username +
            ": " + this.TweetText; }
}