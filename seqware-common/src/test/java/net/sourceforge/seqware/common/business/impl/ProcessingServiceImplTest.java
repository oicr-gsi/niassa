package net.sourceforge.seqware.common.business.impl;

import io.seqware.common.model.ProcessingStatus;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.RegistrationDTO;
import net.sourceforge.seqware.common.util.Log;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * ProcessingServiceImplTest class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class ProcessingServiceImplTest extends AbstractTestCase {

    @Autowired
    ProcessingService processingService;

    @Autowired
    RegistrationService registrationService;

    /**
     * <p>
     * testProcessingWithFiles.
     * </p>
     */
    @Test
    public void testProcessingWithFiles() {
        Log.info("Processing without files");
        Processing processing = processingService.findByID(775);
        assertEquals(2, processing.getFiles().size());
    }

    /**
     * <p>
     * testProcessingWithoutFiles.
     * </p>
     */
    @Test
    public void testProcessingWithoutFiles() {
        Log.info("Processing without files");
        Processing processing = processingService.findByID(3);
        assertEquals(0, processing.getFiles().size());
    }

    /**
     * <p>
     * testInsertProcessing.
     * </p>
     */
    @Test
    public void testInsertProcessing() {
        RegistrationDTO regDto = registrationService.findByEmailAddressAndPassword("admin@admin.com", "admin");

        Processing newProcessing = new Processing();
        newProcessing.setOwner(regDto);
        newProcessing.setFiles(new HashSet<File>());
        newProcessing.setStatus(ProcessingStatus.success);
        newProcessing.setExitStatus(0);
        newProcessing.setProcessExitStatus(0);
        newProcessing.setRunStartTimestamp(null);
        newProcessing.setRunStopTimestamp(null);
        newProcessing.setAlgorithm("upload");
        newProcessing.setCreateTimestamp(new Date());
        processingService.insert(newProcessing);
    }

    /**
     * <p>
     * testFindByCriteria.
     * </p>
     */
    @Test
    public void testFindByCriteria() {
        // find using SWID number
        List<Processing> found = processingService.findByCriteria("3819", false);
        assertEquals(1, found.size());

        // Case sens
        found = processingService.findByCriteria("simple", true);
        assertEquals(1567, found.size());

        found = processingService.findByCriteria("SIMPLE", true);
        assertEquals(0, found.size());
    }
}
