package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Workflow;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkflowServiceImplTest extends AbstractTestCase {

    @Autowired
    WorkflowService workflowService;

    @Test
    public void testList() {
        List<Workflow> workflows = workflowService.list();
        assertEquals(20, workflows.size());
    }

    @Test
    public void testGetWorkflowParamDefaults() {
        Workflow workflow = workflowService.findBySWAccession(4767);
        assertEquals(25, workflow.getParameterDefaults().size());
    }
}
