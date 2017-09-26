/*
 * Copyright (C) 2011 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.common.business.impl;

import static io.seqware.pipeline.SqwKeys.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.naming.NamingException;
import junit.framework.TestCase;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

/**
 * <p>
 * HibernateTestSuite class.
 * </p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 * @since 0.13.3
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    net.sourceforge.seqware.common.business.impl.AnalysisProvenanceServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.SampleProvenanceServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.LaneProvenanceServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.IusServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.LimsKeyServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.LaneServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.ProcessingServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.StudyServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.SampleServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.SequencerRunServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.ExperimentServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.FileServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.WorkflowRunServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.SampleReportServiceImplTest.class,
    net.sourceforge.seqware.database.QueryTest.class,
    net.sourceforge.seqware.common.hibernate.WorkflowRunReportTest.class
})
public class HibernateTestSuite extends TestCase {

    private static BasicTestDatabaseCreator basicTestDatabaseCreator;
    private static BasicDataSource dataSource;

    /**
     * <p>
     * setUpClass.
     * </p>
     *
     * @throws java.lang.Exception
     *                             if any.
     */
    @BeforeClass
    public static void setUpClass() throws Exception {
        Map<String, String> settings = new HashMap<>();
        settings.put(BASIC_TEST_DB_NAME.getSettingKey(), "testing_" + 
                DateTime.now().toString(DateTimeFormat.forPattern("MMdd_HHmmss")) + "_" + 
                RandomStringUtils.randomAlphabetic(4).toLowerCase());
        settings.put(BASIC_TEST_DB_PORT.getSettingKey(), System.getProperty("seqware_meta_db_port"));
        settings.put("POSTGRE_USER", System.getProperty("seqware_meta_db_user"));
        settings.put("POSTGRE_PASSWORD", System.getProperty("seqware_meta_db_password"));
        settings.put(BASIC_TEST_DB_USER.getSettingKey(), System.getProperty("seqware_meta_db_user"));
        settings.put(BASIC_TEST_DB_PASSWORD.getSettingKey(), System.getProperty("seqware_meta_db_password"));
        settings.put(BASIC_TEST_DB_HOST.getSettingKey(), System.getProperty("seqware_meta_db_host"));

        basicTestDatabaseCreator = new BasicTestDatabaseCreator(settings);
        basicTestDatabaseCreator.createNewDatabase(true);

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://" + settings.get(BASIC_TEST_DB_HOST.getSettingKey()) + ":"
                + settings.get(BASIC_TEST_DB_PORT.getSettingKey()) + "/" + settings.get(BASIC_TEST_DB_NAME.getSettingKey()));
        dataSource.setUsername(settings.get(BASIC_TEST_DB_USER.getSettingKey()));
        dataSource.setPassword(settings.get(BASIC_TEST_DB_PASSWORD.getSettingKey()));
        dataSource.setValidationQuery("SELECT version()");

        //bind the test data source to the seqware metadb JNDI name
        SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        builder.bind("java:comp/env/jdbc/SeqWareMetaDB", dataSource);
        try {
            builder.activate();
        } catch (IllegalStateException | NamingException ex) {
            throw ex;
        }

        //load the sample report table
        dataSource.getConnection().createStatement().execute("SELECT fill_sample_report();");
    }

    /**
     * <p>
     * tearDownClass.
     * </p>
     *
     * @throws java.sql.SQLException
     */
    @AfterClass
    public static void tearDownClass() throws SQLException {
        dataSource.close();
        basicTestDatabaseCreator.dropDatabase();
    }

    /**
     * <p>
     * setUp.
     * </p>
     *
     * @throws java.lang.Exception
     *                             if any.
     */
    @Before
    @Override
    public void setUp() throws Exception {
    }

    /**
     * <p>
     * tearDown.
     * </p>
     *
     * @throws java.lang.Exception
     *                             if any.
     */
    @After
    @Override
    public void tearDown() throws Exception {
    }
}
