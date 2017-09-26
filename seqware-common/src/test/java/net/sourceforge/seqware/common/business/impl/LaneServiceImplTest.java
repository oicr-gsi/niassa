package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import java.util.Set;
import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.WorkflowRun;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * LaneServiceImplTest class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class LaneServiceImplTest extends AbstractTestCase {

    @Autowired
    LaneService laneService;

    /**
     * <p>
     * testAssociatedWorkflowRuns.
     * </p>
     */
    @Test
    public void testAssociatedWorkflowRuns() {
        Lane lane = laneService.findByID(16);
        Set<WorkflowRun> workflowRuns = lane.getWorkflowRuns();
        System.out.print(workflowRuns.size());
        assertEquals(0, workflowRuns.size());
    }

    /**
     * <p>
     * testFindByCriteria.
     * </p>
     */
    @Test
    public void testFindByCriteria() {
        // List<Lane> found = laneService.findByCriteria("_LMP", true);
        // assertEquals(9, found.size());

        // Case Sensitive
        List<Lane> found = laneService.findByCriteria("_lAne", true);
        assertEquals(0, found.size());

        found = laneService.findByCriteria("_lAne", false);
        assertEquals(8, found.size());

        // By SWID
        found = laneService.findByCriteria("4707", false);
        assertEquals(1, found.size());

        // By Description
        // found = laneService.findByCriteria("{", false);
        // assertEquals(1, found.size());
    }
}
