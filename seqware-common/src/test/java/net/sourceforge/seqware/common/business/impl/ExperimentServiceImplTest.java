package net.sourceforge.seqware.common.business.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.naming.NamingException;

import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreatorWrapper;

/**
 * <p>
 * ExperimentServiceImplTest class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class ExperimentServiceImplTest extends AbstractTestCase {

    @Autowired
    ExperimentService experimentService;

    @Autowired
    SessionFactory sessionFactory;

    /**
     * <p>
     * tearDown.
     * </p>
     *
     * @throws javax.naming.NamingException
     *                                      if any.
     */
    @AfterClass
    public static void tearDown() throws NamingException {
    }

    /**
     * <p>
     * testFindByTitle.
     * </p>
     */
    @Test
    public void testFindByTitle() {
        Experiment experiment = experimentService.findByTitle("MixExp1");
        assertNotNull(experiment);

        Experiment experimentCaseInsensitive = experimentService.findByTitle("mixexp1");
        assertNotNull(experimentCaseInsensitive);
        assertEquals(experiment.getTitle(), experimentCaseInsensitive.getTitle());
    }

    // @Test
    // public void testFindByOwnerId() {
    // Registration r = registrationService.findByEmailAddressAndPassword(email, password);
    // assertNotNull(r);
    // List<Experiment> experiments = BeanFactory.getExperimentServiceBean().findByOwnerID(r.getRegistrationId());
    // assertNotNull(experiments);
    // Log.info("Count: " + experiments.size());
    // // Must be 3 experiments for that registrationId
    // assertEquals("Expected 3, got "+experiments.size(), 3, experiments.size());
    // }
    /**
     * <p>
     * testFindBySWAccession.
     * </p>
     */
    @Test
    public void testFindBySWAccession() {
        Experiment experiment = experimentService.findBySWAccession(834);
        assertNotNull(experiment);
        assertEquals("Sample_Exome_ABC015068", experiment.getTitle());
    }

    /**
     * <p>
     * testNoLazyInitializationException.
     * </p>
     */
    @Test
    public void testNoLazyInitializationException() {
        Experiment experiment = experimentService.findByID(6);
        Log.info("Owner is " + experiment.getOwner().getFirstName());
    }

    /**
     * <p>
     * testUpdateDetached.
     * </p>
     */
    @Test
    public void testUpdateDetached() {
        Experiment experiment = experimentService.findByID(6);
        sessionFactory.getCurrentSession().evict(experiment); //detach object
        experiment.setTitle("New Title");

        Experiment newExperiment = experimentService.findByID(6);
        assertFalse(experiment == newExperiment);

        // Let's try to attach object
        Experiment attachedNewly = experimentService.updateDetached(experiment);
        assertEquals("New Title", attachedNewly.getTitle());

        Experiment updatedExperiment = experimentService.findByID(6);
        assertEquals("New Title", updatedExperiment.getTitle());
        
        BasicTestDatabaseCreatorWrapper.markDatabaseChanged();
    }

    /**
     * <p>
     * testFindByCriteria.
     * </p>
     */
    @Test
    public void testFindByCriteria() {
        List<Experiment> foundExperiments = experimentService.findByCriteria("Exp", true);
        assertEquals(1, foundExperiments.size());

        foundExperiments = experimentService.findByCriteria("834", true);
        assertEquals(1, foundExperiments.size());

        foundExperiments = experimentService.findByCriteria("(test", true);
        assertEquals(0, foundExperiments.size());

        // Test case insensitive search
        foundExperiments = experimentService.findByCriteria("test", false);
        assertEquals(2, foundExperiments.size());

        // Description
        foundExperiments = experimentService.findByCriteria("genome", false);
        assertEquals(1, foundExperiments.size());
    }

}
