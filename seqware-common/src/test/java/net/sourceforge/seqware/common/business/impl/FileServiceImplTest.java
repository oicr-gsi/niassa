package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.model.File;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * FileServiceImplTest class.
 * </p>
 *
 * @author Oleg Lopatin
 * @version $Id: $Id
 * @since 0.13.3
 */
public class FileServiceImplTest extends AbstractTestCase {

    @Autowired
    FileService fileService;

    /**
     * <p>
     * testFindByCriteria.
     * </p>
     */
    @Test
    public void testFindByCriteria() {
        // No results by path matching
        List<File> foundFiles = fileService.findByCriteria("https", false);
        assertEquals(0, foundFiles.size());

        foundFiles = fileService.findByCriteria("ABC015068", true);
        assertEquals(11, foundFiles.size());

        // Case insensitive search
        foundFiles = fileService.findByCriteria("abc015068", true);
        assertEquals(0, foundFiles.size());

        foundFiles = fileService.findByCriteria("abc015068", false);
        assertEquals(11, foundFiles.size());

        // Test SW Accession
        foundFiles = fileService.findByCriteria("1963", false);
        assertEquals(1, foundFiles.size());
    }
}
