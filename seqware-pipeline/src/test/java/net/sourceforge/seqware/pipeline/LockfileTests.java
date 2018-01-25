package net.sourceforge.seqware.pipeline;

import java.io.File;

import net.sourceforge.seqware.common.util.filetools.lock.LockingFileTools;
import net.sourceforge.seqware.common.util.processtools.ProcessTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * LockfileTests class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class LockfileTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(LockfileTests.class);

    /**
     * <p>
     * main.
     * </p>
     * 
     * @param args
     *            an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        String outdir = args[0];
        String type = args[1];

        // the lockAndAppend method actually tries 20 times internally, no need to loop here
        for (int i = 0; i < 1; i++) {

            int maxTries = 10;
            for (int j = 0; j < maxTries; j++) {
                // Break on success
                if (LockingFileTools.lockAndAppend(new File(outdir + "/tmp_" + i + ".txt"), i + System.getProperty("line.separator"))) {
                    LOGGER.info("Wrote lock file: " + outdir + "/tmp_" + i + ".txt");
                    break;
                }
                // Sleep if going to try again
                else if (j < maxTries) {
                    LOGGER.info("Sleeping for lock file: " + outdir + "/tmp_" + i + ".txt");
                    ProcessTools.sleep(2);
                }
                // Return error if failed on last try
                else {
                    LOGGER.info("Failed to open lock file for: " + outdir + "/tmp_" + i + ".txt");
                    System.exit(1);
                }
            }
        }
    }

}
