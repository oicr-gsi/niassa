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
package net.sourceforge.seqware.webservice.resources.queries;

import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreatorWrapper;
import net.sourceforge.seqware.common.util.testtools.JndiDatasourceCreator;
import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author mtaschuk
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    net.sourceforge.seqware.webservice.resources.queries.AnalysisProvenanceResourceTest.class,
    net.sourceforge.seqware.webservice.resources.queries.SampleProvenanceResourceTest.class,
    net.sourceforge.seqware.webservice.resources.queries.LaneProvenanceResourceTest.class,
    net.sourceforge.seqware.webservice.resources.queries.WorkflowRunReportResourceTest.class
})
public class WSResourceTestSuite {

    protected SessionFactory sessionFactory = null;
    private static BasicTestDatabaseCreator dbCreator;

    @BeforeClass
    public static void setUpClass() throws Exception {
        dbCreator = BasicTestDatabaseCreator.getFromSystemProperties();
        dbCreator.resetDatabaseWithUsers();
        JndiDatasourceCreator.create();
        // SeqWareWebServiceMain.main(null);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        // SeqWareWebServiceMain.stop();
        BasicTestDatabaseCreatorWrapper.dropDatabase();
    }
    // @Before
    // public void setUp() {
    // sessionFactory = BeanFactory.getSessionFactoryBean();
    // Session session = SessionFactoryUtils.getSession(sessionFactory, true);
    // TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
    // }
    //
    // @After
    // public void tearDown() {
    // SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
    // SessionFactoryUtils.closeSession(sessionHolder.getSession());
    // }
}
