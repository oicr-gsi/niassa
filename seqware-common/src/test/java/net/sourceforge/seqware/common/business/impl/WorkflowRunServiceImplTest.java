package net.sourceforge.seqware.common.business.impl;

import io.seqware.common.model.WorkflowRunStatus;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.WorkflowRun;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * <p>
 * WorkflowRunServiceImplTest class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class WorkflowRunServiceImplTest extends AbstractTestCase {

    @Autowired
    WorkflowRunService workflowRunService;

    @Autowired
    LaneService laneService;

    @Autowired
    @Qualifier("IUSService")
    IUSService iusService;

    /**
     * <p>
     * testParentLanes.
     * </p>
     */
    @Test
    public void testParentLanes() {
        WorkflowRun wfRun = workflowRunService.findByID(22);

        SortedSet<Lane> lanes = wfRun.getLanes();
        assertNotNull(lanes);

        System.out.print(lanes.size());

        // Let's try to add new lane to wfRun
        Lane lane = laneService.findByID(3);
        lanes.add(lane);
        wfRun.setLanes(lanes);
        workflowRunService.update(wfRun);

        wfRun = workflowRunService.findByID(22);
        lanes = wfRun.getLanes();
        System.out.print(lanes.size());
        assertEquals(1, lanes.size());
    }

    /**
     * <p>
     * testParentIus.
     * </p>
     */
    @Test
    public void testParentIus() {
        WorkflowRun wfRun = workflowRunService.findByID(22);

        SortedSet<IUS> ius = wfRun.getIus();
        assertNotNull(ius);
        assertEquals(0, ius.size());

        // Get some IUS
        IUS someIus = iusService.findByID(4);
        ius.add(someIus);
        wfRun.setIus(ius);
        workflowRunService.update(wfRun);

        wfRun = workflowRunService.findByID(22);
        ius = wfRun.getIus();
        assertNotNull(ius);
        assertEquals(1, ius.size());
    }

    /**
     * <p>
     * testAttachNewlyCreatedWorkflowRun.
     * </p>
     */
    @Test
    public void testAttachNewlyCreatedWorkflowRun() {
        // Suppose we created or get WorkflowRun object which is hibernate outbound
        WorkflowRun createdWorkflowRun = new WorkflowRun();
        createdWorkflowRun.setWorkflowRunId(22);
        createdWorkflowRun.setIniFile("newIniFile"); // <-- ini file has been
        // updated
        createdWorkflowRun.setStatus(WorkflowRunStatus.completed);
        createdWorkflowRun.setStatusCmd("newCommand"); // <-- command has been
        // updated
        createdWorkflowRun.setSeqwareRevision("2305M");
        createdWorkflowRun.setSwAccession(64);

        createdWorkflowRun.setCreateTimestamp(new Date());
        createdWorkflowRun.setUpdateTimestamp(new Date());

        workflowRunService.updateDetached(createdWorkflowRun);
    }

    /**
     * <p>
     * testFindByCriteria.
     * </p>
     */
    @Test
    public void testFindByCriteria() {
        List<WorkflowRun> found = workflowRunService.findByCriteria("NC_001807", false);
        assertEquals(4, found.size());

        // Case sensitive
        found = workflowRunService.findByCriteria("ExomesOrHg19Tumour", true);
        assertEquals(1, found.size());

        found = workflowRunService.findByCriteria("exomesOrHg19Tumour", true);
        assertEquals(0, found.size());

        // SWID
        found = workflowRunService.findByCriteria("2862", true);
        assertEquals(1, found.size());
    }

}
