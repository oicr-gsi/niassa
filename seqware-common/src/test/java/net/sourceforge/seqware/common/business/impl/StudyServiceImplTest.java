package net.sourceforge.seqware.common.business.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>
 * StudyServiceImplTest class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class StudyServiceImplTest extends AbstractTestCase {

    @Autowired
    StudyService studyService;

    @Autowired
    SessionFactory sessionFactory;

    // @AfterClass
    // public static void tearDownAfterClass() throws Exception {
    // DatabaseCreator.markDatabaseChanged();
    // }
    
    /**
     * <p>
     * testFindByTitle.
     * </p>
     */
    @Test
    public void testFindByTitle() {
        List<Study> studySensitive = studyService.findByTitle("AbcCo_Exome_Sequencing");
        // Should return one item.
        assertEquals(1, studySensitive.size());

        List<Study> studyInsensitive = studyService.findByTitle("abcco_Exome_Sequencing");
        // Should return one item.
        assertEquals(1, studyInsensitive.size());

        // Look for not existing Title
        List<Study> studyNotExist = studyService.findByTitle("Not Exist");
        // Should return one item.
        assertEquals(0, studyNotExist.size());
    }

    // @Test
    // public void testFindByOwnerId() {
    // Registration r = registrationService.findByEmailAddressAndPassword(email, password);
    // List<Study> studies = ss.findByOwnerID(r.getRegistrationId());
    // Log.info("Count: " + studies.size());
    // // Must be 2 studies for that registrationId
    // assertEquals("Expected 2, got "+studies.size(), 2, studies.size());
    // }
    
    /**
     * <p>
     * testFindBySWAccession.
     * </p>
     */
    @Test
    public void testFindBySWAccession() {
        Study study = studyService.findBySWAccession(120);
        assertNotNull(study);
        assertEquals("AbcCo_Exome_Sequencing", study.getTitle());
    }

    /**
     * <p>
     * testUpdateDetached.
     * </p>
     */
    @Test
    public void testUpdateDetached() {
        Study study = studyService.findByID(12);
        sessionFactory.getCurrentSession().evict(study); //detach object

        Study newStudy = studyService.findByID(12);
        assertFalse(study == newStudy);

        // Update detached object
        study.setTitle("New Title");

        // Let's try to attach object
        Study attachedNewly = studyService.updateDetached(study);
        assertEquals("New Title", attachedNewly.getTitle());

        Study updatedStudy = studyService.findByID(12);
        assertEquals("New Title", updatedStudy.getTitle());
    }

    /**
     * <p>
     * testNoLazyInitializationException.
     * </p>
     */
    @Test
    public void testNoLazyInitializationException() {
        Study study = studyService.findByID(10);
        Set<Processing> processings = study.getProcessings();
        Log.info("Procissings count: " + processings.size());
        Log.info("Owner is " + study.getOwner().getFirstName());
    }

    /**
     * <p>
     * testFindByCriteria.
     * </p>
     */
    @Test
    public void testFindByCriteria() {
        // List<Study> foundStudies = studyService.findByCriteria("HuRef", false);
        // assertEquals(1, foundStudies.size());

        List<Study> foundStudies = studyService.findByCriteria("Human", false);
        assertEquals(1, foundStudies.size());

        // Case sens
        foundStudies = studyService.findByCriteria("human", true);
        assertEquals(0, foundStudies.size());

        foundStudies = studyService.findByCriteria("human", false);
        assertEquals(1, foundStudies.size());

        // SWID
        foundStudies = studyService.findByCriteria("120", false);
        assertEquals(1, foundStudies.size());

        // Title
        foundStudies = studyService.findByCriteria("data", false);
        assertEquals(1, foundStudies.size());
    }

}
