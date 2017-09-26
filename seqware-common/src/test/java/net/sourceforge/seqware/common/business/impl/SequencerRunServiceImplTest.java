package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.SequencerRun;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * SequencerRunServiceImplTest class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class SequencerRunServiceImplTest extends AbstractTestCase {

    @Autowired
    SequencerRunService sequencerRunService;

    /**
     * <p>
     * testFindByCriteria.
     * </p>
     */
    @Test
    public void testFindByCriteria() {
        List<SequencerRun> found = sequencerRunService.findByCriteria("srk", false);
        assertEquals(2, found.size());

        // Case sensitive
        found = sequencerRunService.findByCriteria("Run", true);
        assertEquals(0, found.size());

        found = sequencerRunService.findByCriteria("Run", false);
        assertEquals(2, found.size());

        // SWID
        found = sequencerRunService.findByCriteria("47150", false);
        assertEquals(1, found.size());
    }
}
