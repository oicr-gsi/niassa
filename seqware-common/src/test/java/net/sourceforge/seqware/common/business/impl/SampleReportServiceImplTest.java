package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.SampleReportService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.SampleReportRow;
import net.sourceforge.seqware.common.model.SequencerRun;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * SampleReportServiceImplTest class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class SampleReportServiceImplTest extends AbstractTestCase {

    @Autowired
    SampleReportService sampleReportService;

    @Autowired
    SequencerRunService sequencerRunService;
    
    /**
     * <p>
     * testGetRowsForSequencerRun.
     * </p>
     */
    @Test
    public void testGetRowsForSequencerRun() {
        SequencerRun sr = sequencerRunService.findByID(1);
        List<SampleReportRow> reportRow = sampleReportService.getRowsForSequencerRun(sr);
        assertEquals(11, reportRow.size());
    }

    /**
     * <p>
     * testGetRowsWithSequencerRuns.
     * </p>
     */
    @Test
    public void testGetRowsWithSequencerRuns() {
        List<SampleReportRow> reportRows = sampleReportService.getRowsWithSequencerRuns();
        assertEquals(52, reportRows.size());
    }

}
