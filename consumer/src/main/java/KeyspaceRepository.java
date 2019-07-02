import com.datastax.driver.core.Session;

public class KeyspaceRepository {
    private Session session;

    public KeyspaceRepository(Session session) { this.session = session; }

    public void createKeyspace(String keyspaceName, String repStrat, int replicas) {
        System.out.println("createKeyspace – init");

        StringBuilder sb = new StringBuilder("CREATE KEYSPACE IF NOT EXISTS ")
                .append(keyspaceName).append(" WITH replication = {").append("'class':'")
                .append(repStrat).append("', 'replication_factor':")
                .append(replicas).append("};");
        final String query = sb.toString();
        session.execute(query);
    }

    public void useKeyspace(String keyspace) {
        System.out.println("useKeyspace – init");
        session.execute("USE " + keyspace);
    }

    public void deleteKeyspace(String keyspaceName) {
        System.out.println("deleteKeyspace– init");
        StringBuilder sb = new StringBuilder("DROP KEYSPACE ").append(keyspaceName);

        final String query = sb.toString();
        session.execute(query);
    }
}
