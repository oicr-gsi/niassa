package net.sourceforge.seqware.pipeline.module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import net.sourceforge.seqware.common.module.ReturnValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an empty module mainly for testing purpose. Created by IntelliJ IDEA. User: xiao Date: 7/21/11 Time: 3:53 PM To change this
 * template use File | Settings | File Templates.
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class TestModule extends Module {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestModule.class);
    
    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public String getAlgorithm() {
        return "TestModule";
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue do_run() {
        LOGGER.info("stdout: TestModule.do_run");
        LOGGER.error("stderr: TestModule.do_run");
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue do_test() {
        LOGGER.info("stdout: TestModule.do_test");
        LOGGER.error("stderr: TestModule.do_test");
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue do_verify_input() {
        LOGGER.info("stdout: TestModule.do_verify_input");
        LOGGER.error("stderr: TestModule.do_verify_input");
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue do_verify_parameters() {
        LOGGER.info("stdout: TestModule.do_verify_parameters");
        LOGGER.error("stderr: TestModule.do_verify_parameters");
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue do_verify_output() {
        LOGGER.info("stdout: TestModule.do_verify_output");
        LOGGER.error("stderr: TestModule.do_verify_output");
        return new ReturnValue(ReturnValue.SUCCESS);
    }

    /**
     * <p>
     * main.
     * </p>
     * 
     * @param args
     *            an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        File file = new File(args[0]);
        PrintStream printStream = null;
        PrintStream curOut = System.out;
        try {
            printStream = new PrintStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            LOGGER.error("TestModule.main File not found exception:",e);
        }

        LOGGER.info("1");

        System.setOut(printStream);

        LOGGER.info("2");

        System.setOut(curOut);

        LOGGER.info("3");
    }
}
