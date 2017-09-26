/*
 * Copyright (C) 2016 SeqWare
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
package io.seqware.admin;

import ca.on.oicr.gsi.provenance.model.AnalysisProvenance;
import ca.on.oicr.gsi.provenance.model.IusLimsKey;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import io.seqware.admin.RelinkWorkflowRunIusAndProcessingsToLimsKey.Summary;
import static io.seqware.pipeline.SqwKeys.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import javax.naming.NamingException;
import net.sourceforge.seqware.common.business.AnalysisProvenanceService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml", "testApplicationContext.xml"})
public class RelinkWorkflowRunIusAndProcessingsToLimsKeyIT {

    private static BasicTestDatabaseCreator basicTestDatabaseCreator;
    private static BasicDataSource dataSource;
    private static Map<String, String> settings;

    @Autowired
    RelinkWorkflowRunIusAndProcessingsToLimsKey relinker;

    @Autowired
    @Qualifier("analysisProvenanceService")
    AnalysisProvenanceService aprs;

    @Autowired
    @Qualifier("sampleService")
    SampleService sampleService;

    @Autowired
    @Qualifier("laneService")
    LaneService laneService;

    @Autowired
    @Qualifier("IUSService")
    IUSService iusService;

    @Autowired
    @Qualifier("workflowService")
    WorkflowService workflowService;

    @Autowired
    @Qualifier("workflowRunService")
    WorkflowRunService workflowRunService;

    @Autowired
    @Qualifier("processingService")
    ProcessingService processingService;

    @Autowired
    @Qualifier("fileService")
    FileService fileService;

    @Autowired
    @Qualifier("registrationService")
    RegistrationService registrationService;

    public RelinkWorkflowRunIusAndProcessingsToLimsKeyIT() {
        Logger.getLogger(RelinkWorkflowRunIusAndProcessingsToLimsKey.class).setLevel(Level.DEBUG);
    }

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
        settings = new HashMap<>();
        settings.put(BASIC_TEST_DB_NAME.getSettingKey(), "testing_"
                + DateTime.now().toString(DateTimeFormat.forPattern("MMdd_HHmmss")) + "_"
                + RandomStringUtils.randomAlphabetic(4).toLowerCase());
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

    @Before
    public void resetDatabase() throws SQLException {
        basicTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void defaultData() throws IOException {
        assertEquals(26, aprs.list().size());
        relinker.setTimestamp(DateTime.now().toDate());
        Summary summary = relinker.run();
        assertEquals(4, summary.getWorkflowRunsAnalyzed());
        assertEquals(4, summary.getWorkflowRunsAlreadyLinked());
        assertEquals(0, summary.getWorkflowRunsToBeRelinked());
        assertEquals(0, summary.getWorkflowRunsRelinked());
        assertEquals(0, summary.getWorkflowRunsErrorState());
        assertEquals(26, aprs.list().size());
    }

    @Test
    public void workflowRunLinkedToOnlyIus() throws IOException {

        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";

        IUS ius = new IUS();
        ius.setSample(sampleService.findByID(10));
        ius.setLane(laneService.findByID(12));
        iusService.insert(ius);

        WorkflowRun workflowRun = createTestWorkflowRun(expectedWorkflowName);
        createTestOutputFile(expectedProcessingAlgorithm, expectedFilePath, workflowRun);

        //link workflow run and ius
        workflowRun.setIus(ImmutableSortedSet.of(ius));
        workflowRunService.update(workflowRun);

        //relink workflow run
        assertEquals(0, getAnalysisProvenanceForWorkflowRun(workflowRun).size());
        relinker.setTimestamp(DateTime.now().toDate());
        relinker.setDoRelinking(true);
        Summary summary = relinker.run();
        assertEquals(5, summary.getWorkflowRunsAnalyzed());
        assertEquals(4, summary.getWorkflowRunsAlreadyLinked());
        assertEquals(1, summary.getWorkflowRunsToBeRelinked());
        assertEquals(1, summary.getWorkflowRunsRelinked());
        assertEquals(0, summary.getWorkflowRunsErrorState());

        List<AnalysisProvenanceDto> dtos = getAnalysisProvenanceForWorkflowRun(workflowRun);
        assertEquals(1, dtos.size());
        assertEquals(ius.getSwAccession().toString(), Iterables.getOnlyElement(dtos.get(0).getIusLimsKeys()).getLimsKey().getId());
    }

    @Test
    public void workflowRunLinkedToIusAndProcessing() throws IOException {

        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";

        IUS ius1 = new IUS();
        ius1.setSample(sampleService.findByID(10));
        ius1.setLane(laneService.findByID(12));
        iusService.insert(ius1);

        IUS ius2 = new IUS();
        ius2.setSample(sampleService.findByID(11));
        ius2.setLane(laneService.findByID(12));
        iusService.insert(ius2);

        WorkflowRun workflowRun = createTestWorkflowRun(expectedWorkflowName);
        Processing processing1 = createTestOutputFile(expectedProcessingAlgorithm, expectedFilePath, workflowRun);
        Processing processing2 = createTestOutputFile(expectedProcessingAlgorithm, expectedFilePath, workflowRun);

        //link workflow run and ius
        workflowRun.setIus(ImmutableSortedSet.of(ius1, ius2));
        workflowRunService.update(workflowRun);
        ius1.setProcessings(ImmutableSortedSet.of(processing1));
        iusService.update(ius1);
        ius2.setProcessings(ImmutableSortedSet.of(processing2));
        iusService.update(ius2);

        //relink workflow run
        assertEquals(0, getAnalysisProvenanceForWorkflowRun(workflowRun).size());
        relinker.setTimestamp(DateTime.now().toDate());
        relinker.setDoRelinking(true);
        Summary summary = relinker.run();
        assertEquals(5, summary.getWorkflowRunsAnalyzed());
        assertEquals(4, summary.getWorkflowRunsAlreadyLinked());
        assertEquals(1, summary.getWorkflowRunsToBeRelinked());
        assertEquals(1, summary.getWorkflowRunsRelinked());
        assertEquals(0, summary.getWorkflowRunsErrorState());

        List<AnalysisProvenanceDto> dtos = getAnalysisProvenanceForWorkflowRun(workflowRun);
        assertEquals(2, dtos.size());
        for (AnalysisProvenanceDto dto : dtos) {
            assertEquals(1, dto.getIusLimsKeys().size());
        }
    }

    @Test
    public void workflowRunLinkedToIusAndIncorrectProcessing() throws IOException {

        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";

        IUS ius1 = new IUS();
        ius1.setSample(sampleService.findByID(10));
        ius1.setLane(laneService.findByID(12));
        ius1 = iusService.findBySWAccession(iusService.insert(ius1));

        IUS ius2 = new IUS();
        ius2.setSample(sampleService.findByID(11));
        ius2.setLane(laneService.findByID(12));
        ius2 = iusService.findBySWAccession(iusService.insert(ius2));

        WorkflowRun workflowRun = createTestWorkflowRun(expectedWorkflowName);
        Processing processing = createTestOutputFile(expectedProcessingAlgorithm, expectedFilePath, workflowRun);

        //link workflow run and ius
        workflowRun.setIus(ImmutableSortedSet.of(ius1));
        workflowRunService.update(workflowRun);
        ius2.setProcessings(ImmutableSortedSet.of(processing));
        iusService.update(ius2);

        //relink workflow run
        assertEquals(0, getAnalysisProvenanceForWorkflowRun(workflowRun).size());
        relinker.setTimestamp(DateTime.now().toDate());
        relinker.setDoRelinking(true);
        Summary summary = relinker.run();
        assertEquals(5, summary.getWorkflowRunsAnalyzed());
        assertEquals(4, summary.getWorkflowRunsAlreadyLinked());
        assertEquals(0, summary.getWorkflowRunsToBeRelinked());
        assertEquals(0, summary.getWorkflowRunsRelinked());
        assertEquals(1, summary.getWorkflowRunsErrorState());

        List<AnalysisProvenanceDto> dtos = getAnalysisProvenanceForWorkflowRun(workflowRun);
        assertEquals(0, dtos.size());
        for (AnalysisProvenanceDto dto : dtos) {
            assertEquals(1, dto.getIusLimsKeys().size());
        }
    }

    @Test
    public void expectedLinkingOfIusAndLanes() throws IOException {

        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";

        IUS ius = new IUS();
        ius.setSample(sampleService.findByID(10));
        ius.setLane(laneService.findByID(12));
        iusService.insert(ius);

        Lane lane = laneService.findByID(12);

        WorkflowRun workflowRun = createTestWorkflowRun(expectedWorkflowName);
        //only linked to ius
        Processing processing1 = createTestOutputFile(expectedProcessingAlgorithm, expectedFilePath, workflowRun);
        //linked to ius and lane
        Processing processing2 = createTestOutputFile(expectedProcessingAlgorithm, expectedFilePath, workflowRun);
        //only linked to lane
        Processing processing3 = createTestOutputFile(expectedProcessingAlgorithm, expectedFilePath, workflowRun);

        //link workflow run and ius
        workflowRun.setIus(ImmutableSortedSet.of(ius));
        workflowRun.setLanes(ImmutableSortedSet.of(lane));
        workflowRunService.update(workflowRun);
        ius.setProcessings(ImmutableSortedSet.of(processing1, processing2));
        iusService.update(ius);
        lane.setProcessings(ImmutableSortedSet.of(processing1, processing2, processing3));
        laneService.update(lane);

        //relink workflow run
        assertEquals(0, getAnalysisProvenanceForWorkflowRun(workflowRun).size());
        relinker.setTimestamp(DateTime.now().toDate());
        relinker.setDoRelinking(true);
        Summary summary = relinker.run();
        assertEquals(5, summary.getWorkflowRunsAnalyzed());
        assertEquals(4, summary.getWorkflowRunsAlreadyLinked());
        assertEquals(1, summary.getWorkflowRunsToBeRelinked());
        assertEquals(1, summary.getWorkflowRunsRelinked());
        assertEquals(0, summary.getWorkflowRunsErrorState());

        List<AnalysisProvenanceDto> dtos = getAnalysisProvenanceForWorkflowRun(workflowRun);
        Map<Integer, AnalysisProvenance> analysisProvenanceByProcessingSwid = new HashMap<>();
        assertEquals(3, dtos.size());
        for (AnalysisProvenanceDto dto : dtos) {
            analysisProvenanceByProcessingSwid.put(dto.getProcessingId(), dto);

            //all APs should only have one IUS-LimsKey
            assertEquals(1, dto.getIusLimsKeys().size());
        }

        //processing1 should be linked to the IUS
        IusLimsKey ilk1 = Iterables.getOnlyElement(analysisProvenanceByProcessingSwid.get(processing1.getSwAccession()).getIusLimsKeys());
        assertEquals(ius.getSwAccession().toString(), ilk1.getLimsKey().getId());

        //processing2 should only be linked to the IUS (not both IUS and Lane)
        IusLimsKey ilk2 = Iterables.getOnlyElement(analysisProvenanceByProcessingSwid.get(processing2.getSwAccession()).getIusLimsKeys());
        assertEquals(ius.getSwAccession().toString(), ilk2.getLimsKey().getId());

        //processing3 should be linked to the Lane
        IusLimsKey ilk3 = Iterables.getOnlyElement(analysisProvenanceByProcessingSwid.get(processing3.getSwAccession()).getIusLimsKeys());
        assertEquals(lane.getSwAccession().toString(), ilk3.getLimsKey().getId());

        //ilk1 and ilk2 should be the same
        assertTrue(ilk1.equals(ilk2));
    }

    @Test
    public void malformedLinkingOfIusAndLanes() throws IOException {

        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";

        IUS ius = new IUS();
        ius.setSample(sampleService.findByID(10));
        ius.setLane(laneService.findByID(12));
        iusService.insert(ius);

        WorkflowRun workflowRun = createTestWorkflowRun(expectedWorkflowName);
        //only linked to ius
        Processing processing = createTestOutputFile(expectedProcessingAlgorithm, expectedFilePath, workflowRun);

        //different
        Lane correctLane = laneService.findByID(12);
        Lane incorrectLane = laneService.findByID(13);

        //link workflow run and ius
        workflowRun.setIus(ImmutableSortedSet.of(ius));
        workflowRun.setLanes(ImmutableSortedSet.of(correctLane)); //workflow run linked to correct lane
        workflowRunService.update(workflowRun);
        ius.setProcessings(ImmutableSortedSet.of(processing));
        iusService.update(ius);
        incorrectLane.setProcessings(ImmutableSortedSet.of(processing));
        laneService.update(incorrectLane); //processing linked to incorrect lane

        //relink workflow run
        assertEquals(0, getAnalysisProvenanceForWorkflowRun(workflowRun).size());
        relinker.setTimestamp(DateTime.now().toDate());
        relinker.setDoRelinking(true);
        Summary summary = relinker.run();
        assertEquals(5, summary.getWorkflowRunsAnalyzed());
        assertEquals(4, summary.getWorkflowRunsAlreadyLinked());
        assertEquals(0, summary.getWorkflowRunsToBeRelinked());
        assertEquals(0, summary.getWorkflowRunsRelinked());
        assertEquals(1, summary.getWorkflowRunsErrorState());
    }

    @Test
    public void commandLine() throws IOException, IllegalStateException, NamingException {

        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";

        IUS ius = new IUS();
        ius.setSample(sampleService.findByID(10));
        ius.setLane(laneService.findByID(12));
        iusService.insert(ius);

        WorkflowRun workflowRun = createTestWorkflowRun(expectedWorkflowName);
        createTestOutputFile(expectedProcessingAlgorithm, expectedFilePath, workflowRun);

        //link workflow run and ius
        workflowRun.setIus(ImmutableSortedSet.of(ius));
        workflowRunService.update(workflowRun);

        //dry run relink workflow run
        assertEquals(0, getAnalysisProvenanceForWorkflowRun(workflowRun).size());
        RelinkWorkflowRunIusAndProcessingsToLimsKey.main(Arrays.asList(
                "--user", settings.get(BASIC_TEST_DB_USER.getSettingKey()),
                "--password", settings.get(BASIC_TEST_DB_PASSWORD.getSettingKey()),
                "--host", settings.get(BASIC_TEST_DB_HOST.getSettingKey()),
                "--port", settings.get(BASIC_TEST_DB_PORT.getSettingKey()),
                "--db-name", settings.get(BASIC_TEST_DB_NAME.getSettingKey()),
                "--timestamp", "2016-01-01T00:00:01Z"
        ).toArray(new String[0]));
        List<AnalysisProvenanceDto> dtos = getAnalysisProvenanceForWorkflowRun(workflowRun);
        assertEquals(0, dtos.size());

        //relink workflow run
        assertEquals(0, getAnalysisProvenanceForWorkflowRun(workflowRun).size());
        RelinkWorkflowRunIusAndProcessingsToLimsKey.main(Arrays.asList(
                "--user", settings.get(BASIC_TEST_DB_USER.getSettingKey()),
                "--password", settings.get(BASIC_TEST_DB_PASSWORD.getSettingKey()),
                "--host", settings.get(BASIC_TEST_DB_HOST.getSettingKey()),
                "--port", settings.get(BASIC_TEST_DB_PORT.getSettingKey()),
                "--db-name", settings.get(BASIC_TEST_DB_NAME.getSettingKey()),
                "--timestamp", "2016-01-01T00:00:01Z",
                "--do-relinking"
        ).toArray(new String[0]));
        dtos = getAnalysisProvenanceForWorkflowRun(workflowRun);
        assertEquals(1, dtos.size());
        assertEquals(ius.getSwAccession().toString(), Iterables.getOnlyElement(dtos.get(0).getIusLimsKeys()).getLimsKey().getId());
    }

    @Test
    public void dryRun() throws IOException {

        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";

        IUS ius = new IUS();
        ius.setSample(sampleService.findByID(10));
        ius.setLane(laneService.findByID(12));
        iusService.insert(ius);

        WorkflowRun workflowRun = createTestWorkflowRun(expectedWorkflowName);
        createTestOutputFile(expectedProcessingAlgorithm, expectedFilePath, workflowRun);

        //link workflow run and ius
        workflowRun.setIus(ImmutableSortedSet.of(ius));
        workflowRunService.update(workflowRun);

        List<AnalysisProvenanceDto> dtos;
        Summary summary;

        //preconditions
        assertEquals(0, getAnalysisProvenanceForWorkflowRun(workflowRun).size());

        //dry run relink workflow run
        relinker.setTimestamp(DateTime.now().toDate());
        relinker.setDoRelinking(false);
        summary = relinker.run();
        assertEquals(5, summary.getWorkflowRunsAnalyzed());
        assertEquals(4, summary.getWorkflowRunsAlreadyLinked());
        assertEquals(1, summary.getWorkflowRunsToBeRelinked());
        assertEquals(0, summary.getWorkflowRunsRelinked());
        assertEquals(0, summary.getWorkflowRunsErrorState());
        dtos = getAnalysisProvenanceForWorkflowRun(workflowRun);
        assertEquals(0, dtos.size());

        //relink workflow run
        relinker.setTimestamp(DateTime.now().toDate());
        relinker.setDoRelinking(true);
        summary = relinker.run();
        assertEquals(5, summary.getWorkflowRunsAnalyzed());
        assertEquals(4, summary.getWorkflowRunsAlreadyLinked());
        assertEquals(1, summary.getWorkflowRunsToBeRelinked());
        assertEquals(1, summary.getWorkflowRunsRelinked());
        assertEquals(0, summary.getWorkflowRunsErrorState());
        dtos = getAnalysisProvenanceForWorkflowRun(workflowRun);
        assertEquals(1, dtos.size());
        assertEquals(ius.getSwAccession().toString(), Iterables.getOnlyElement(dtos.get(0).getIusLimsKeys()).getLimsKey().getId());
    }

    private WorkflowRun createTestWorkflowRun(String workflowName) {
        Workflow workflow = new Workflow();
        workflow.setName(workflowName);
        workflowService.insert(workflow);

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflow(workflow);
        workflowRun.setIus(new TreeSet<IUS>());
        workflowRun.setProcessings(new TreeSet<Processing>());
        workflowRunService.insert(workflowRun);

        return workflowRun;
    }

    private Processing createTestOutputFile(String processingAlgorithm, String filePath, WorkflowRun workflowRun) {
        Processing processing = new Processing();
        processing.setAlgorithm(processingAlgorithm);
        processing.setWorkflowRun(workflowRun);
        processing.setFiles(new HashSet<File>());
        processingService.insert(processing);

        File file = new File();
        file.setProcessings(ImmutableSortedSet.of(processing));
        file.setFilePath(filePath);
        fileService.insert(file);

        return processing;
    }

    private List<AnalysisProvenanceDto> getAnalysisProvenanceForWorkflowRun(WorkflowRun workflowRun) {
        List<AnalysisProvenanceDto> aps = new ArrayList<>();
        for (AnalysisProvenanceDto dto : aprs.list()) {
            if (workflowRun.getSwAccession().equals(dto.getWorkflowRunId())) {
                aps.add(dto);
            }
        }
        return aps;
    }
}
