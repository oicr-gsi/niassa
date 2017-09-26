package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import java.util.Set;
import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.LimsKeyService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.err.DataIntegrityException;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import org.hibernate.SessionFactory;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * <p>
 * IusServiceImplTest class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class IusServiceImplTest extends AbstractTestCase {

    @Autowired
    @Qualifier("IUSService")
    IUSService iusService;

    @Autowired
    @Qualifier("registrationService")
    RegistrationService registrationService;

    @Autowired
    @Qualifier("limsKeyService")
    LimsKeyService limsKeyService;

    @Autowired
    @Qualifier("sampleService")
    SampleService sampleService;

    @Autowired
    @Qualifier("laneService")
    LaneService laneService;

    @Autowired
    SessionFactory sessionFactory;

    /**
     * <p>
     * testAssociatedWorkflowRuns.
     * </p>
     */
    @Test
    public void testAssociatedWorkflowRuns() {
        IUS ius = iusService.findByID(4);
        Set<WorkflowRun> iuses = ius.getWorkflowRuns();
        Log.info("Count " + iuses.size());
    }

    /**
     * <p>
     * testFindByCriteria.
     * </p>
     */
    @Test
    public void testFindByCriteria() {
        // List<IUS> found = iusService.findByCriteria("Test IUS 2", true);
        // assertEquals(1, found.size());

        // Case sensitive search
        // Make sure that there is no data with name in lower case
        // found = iusService.findByCriteria("test ius 2", true);
        // assertEquals(0, found.size());
        // Insensitive search
        // found = iusService.findByCriteria("test ius 2", false);
        // assertEquals(1, found.size());
        // Test SW accession
        List<IUS> found = iusService.findByCriteria("4765", false);
        assertEquals(1, found.size());
    }

    @Test
    public void testInsert() {
        IUS ius = new IUS();
        Integer swid = iusService.insert(ius);
        IUS newIus = iusService.findBySWAccession(swid);
        assertNotNull(newIus);
    }

    @Test
    public void testOkayDelete() {
        IUS ius = new IUS();
        Integer swid = iusService.insert(ius);
        IUS newIus = iusService.findBySWAccession(swid);
        assertNotNull(newIus);

        IUSAttribute ia = new IUSAttribute();
        ia.setTag("tag");
        ia.setValue("value");
        ia.setIus(newIus);
        newIus.getIusAttributes().add(ia);

        try {
            iusService.delete(newIus);
        } catch (DataIntegrityException ex) {
            throw new RuntimeException(ex);
        }
        assertNull(iusService.findBySWAccession(swid));
    }

    @Test
    public void testFailDelete() {
        IUS ius = iusService.findBySWAccession(4765);
        String ownerEmailAddress = ius.getOwner().getEmailAddress();
        Integer limsKeySwid = ius.getLimsKey().getSwAccession();
        Integer sampleSwid = ius.getSample().getSwAccession();
        Integer laneSwid = ius.getLane().getSwAccession();

        ius.setSample(null);
        ius.setLane(null);
        ius.setLimsKey(null);
        ius.getWorkflowRuns().clear();
        ius.getProcessings().clear();
        iusService.update(ius);

        ius = iusService.findBySWAccession(4765);
        try {
            iusService.delete(ius);
        } catch (DataIntegrityException ex) {
            throw new RuntimeException(ex);
        }

        sessionFactory.getCurrentSession().flush();
        assertNull(iusService.findBySWAccession(4765));
        assertNotNull(registrationService.findByEmailAddress(ownerEmailAddress));
        assertNotNull(limsKeyService.findBySWAccession(limsKeySwid));
        assertNotNull(sampleService.findBySWAccession(sampleSwid));
        assertNotNull(laneService.findBySWAccession(laneSwid));
    }
}
