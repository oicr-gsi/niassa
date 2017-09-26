package net.sourceforge.seqware.common.util.testtools;

import io.seqware.pipeline.SqwKeys;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * <p>
 * JndiDatasourceCreator class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class JndiDatasourceCreator {
    
    private static final String JNDI_NAME = "SeqWareMetaDB";

    /**
     * <p>
     * create.
     * </p>
     * 
     * @throws java.lang.Exception
     *             if any.
     */
    public static void create() throws Exception {
        try {
            if (isJNDIExist("java:comp/env/jdbc/" + JNDI_NAME)) return;
            final SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();

            String username = System.getProperty("seqware_meta_db_user", "seqware");
            String password = System.getProperty("seqware_meta_db_password", "seqware");
            String host = System.getProperty("seqware_meta_db_host", "localhost");
            String port = System.getProperty("seqware_meta_db_port", "5432");
            String database = System.getProperty("seqware_meta_db_name", "test_seqware_meta_db");
            
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName("org.postgresql.Driver");
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setRemoveAbandoned(true);
            ds.setRemoveAbandonedTimeout(30);
            ds.setLogAbandoned(true);
            ds.setUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
            
            Map<String, String> settings = new HashMap<>();
            try {
                settings = ConfigTools.getSettings();
            } catch (Exception e) {
                Log.stderr("Error reading settings file: " + e.getMessage());
            }
            if (settings.containsKey(SqwKeys.BASIC_TEST_DB_HOST.getSettingKey())) {
                ds.setUsername(settings.get(SqwKeys.BASIC_TEST_DB_USER.getSettingKey()));
                ds.setPassword(settings.get(SqwKeys.BASIC_TEST_DB_PASSWORD.getSettingKey()));
                ds.setUrl("jdbc:postgresql://" + settings.get(SqwKeys.BASIC_TEST_DB_HOST.getSettingKey())
                        + ":" + settings.get(SqwKeys.BASIC_TEST_DB_PORT.getSettingKey()) + "/"
                        + settings.get(SqwKeys.BASIC_TEST_DB_NAME.getSettingKey()));
            }

            // PGPoolingDataSource ds = new PGPoolingDataSource();
            // ds.setServerName("localhost");
            // ds.setPortNumber(5432);
            // ds.setDatabaseName("test_seqware_meta_db");
            // ds.setUser(username);
            // ds.setPassword(password);

            builder.bind("java:comp/env/jdbc/" + JNDI_NAME, ds);
            builder.activate();
        } catch (NamingException ex) {
            ex.printStackTrace();
        }
    }

    private static boolean isJNDIExist(String name) throws NamingException {
        try {
            InitialContext context = new InitialContext();
            return context.lookup(name) != null;
        } catch (NoInitialContextException e) {
            return false;
        }
    }
}
