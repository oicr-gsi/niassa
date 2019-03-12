/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.common.util;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * <p>
 * Log class.
 * </p>
 *
 * @author yongliang
 * @version $Id: $Id
 * @deprecated as of 2.0.4 this class has been deprecated. Use slf4j directly instead.
 */
@Deprecated
public class Log {
    private static final Logger LOGGER = LoggerFactory.getLogger(Log.class);
    private static boolean verbose;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * See {@link org.apache.log4j.Logger#debug(Object)}.
     *
     * @param message
     *            the message to log.
     */
    public static void trace(final Object message) {
        LOGGER.error(message.toString());
    }

    /**
     * See {@link org.apache.log4j.Logger#debug(Object,Throwable)}.
     *
     * @param message
     *            the message to log.
     * @param t
     *            the error stack trace.
     */
    public static void trace(final Object message, final Throwable t) {
        LOGGER.error(message.toString(), t);
    }

    /**
     * See {@link org.apache.log4j.Logger#debug(Object)}.
     *
     * @param message
     *            the message to log.
     */
    public static void debug(final Object message) {
        LOGGER.debug(message.toString());
    }

    /**
     * See {@link org.apache.log4j.Logger#debug(Object,Throwable)}.
     *
     * @param message
     *            the message to log.
     * @param t
     *            the error stack trace.
     */
    public static void debug(final Object message, final Throwable t) {
        LOGGER.debug(message.toString(), t);
    }

    /**
     * See {@link org.apache.log4j.Logger#info(Object)}.
     *
     * @param message
     *            the message to log.
     */
    public static void info(final Object message) {
        LOGGER.info(message.toString());
    }

    /**
     * See {@link org.apache.log4j.Logger#info(Object,Throwable)}.
     *
     * @param message
     *            the message to log.
     * @param t
     *            the error stack trace.
     */
    public static void info(final Object message, final Throwable t) {
        LOGGER.info(message.toString(), t);
    }

    /**
     * See {@link org.apache.log4j.Logger#warn(Object)}.
     *
     * @param message
     *            the message to log.
     */
    public static void warn(final Object message) {
        LOGGER.warn(message.toString());
    }

    /**
     * See {@link org.apache.log4j.Logger#warn(Object,Throwable)}.
     *
     * @param message
     *            the message to log.
     * @param t
     *            the error stack trace.
     */
    public static void warn(final Object message, final Throwable t) {
        LOGGER.warn(message.toString(), t);
    }

    /**
     * See {@link org.apache.log4j.Logger#error(Object)}.
     *
     * @param message
     *            the message to log.
     */
    public static void error(final Object message) {
        LOGGER.error(message.toString());
    }

    /**
     * See {@link org.apache.log4j.Logger#error(Object,Throwable)}.
     *
     * @param message
     *            the message to log.
     * @param t
     *            the error stack trace.
     */
    public static void error(final Object message, final Throwable t) {
        LOGGER.error(message.toString(), t);
    }

    /**
     * See {@link org.apache.log4j.Logger#fatal(Object)}.
     *
     * @param message
     *            the message to log.
     */
    public static void fatal(final Object message) {
        LOGGER.error(message.toString());
    }

    /**
     * See {@link org.apache.log4j.Logger#fatal(Object,Throwable)}.
     *
     * @param message
     *            the message to log.
     * @param t
     *            the error stack trace.
     */
    public static void fatal(final Object message, final Throwable t) {
        LOGGER.error(message.toString(), t);
    }

    /**
     * <p>
     * stdout.
     * </p>
     *
     * @param message
     *            a {@link java.lang.String} object.
     */
    public static void stdout(final String message) {
        System.out.println(message);
    }

    /**
     * Output to stdout with the time pre-pended
     *
     * @param message
     *            a {@link java.lang.String} object.
     */
    public static void stdoutWithTime(final String message) {
        outputWithTime(message.toString(), System.out);
    }

    public static void stderrWithTime(final String message) {
        outputWithTime(message.toString(), System.err);
    }

    private static void outputWithTime(final String message, PrintStream stream) {
        // get current date time with Date()
        Date date = new Date();
        stream.print("[" + DATE_FORMAT.format(date) + "] | ");
        stream.println(message.toString());
    }

    /**
     * <p>
     * stderr.
     * </p>
     *
     * @param message
     *            a {@link java.lang.String} object.
     */
    public static void stderr(final String message) {
        System.err.println(message.toString());
    }

    /**
     * override the log4j.properties
     *
     * @param b
     *            a boolean.
     */
    @Deprecated
    public static void setVerbose(boolean b) {
    }

    // Private means that this class is a static singleton.
    private Log() {
    }

    /**
     * Info about the LOGGER caller
     */
    private static class CallInfo {

        public String className;
        public String methodName;

        public CallInfo() {
        }

        public CallInfo(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }
    }

    /**
     * @return the className of the class actually logging the message
     */
    private static String getCallerClassName() {
        final int level = 5;
        return getCallerClassName(level);
    }

    /**
     * @return the className of the class actually logging the message
     */
    private static String getCallerClassName(final int level) {
        CallInfo ci = getCallerInformations(level);
        return ci.className;
    }

    /**
     * Examine stack trace to get caller
     *
     * @param level
     *            method stack depth
     * @return who called the LOGGER
     */
    private static CallInfo getCallerInformations(int level) {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StackTraceElement caller = callStack[level];
        return new CallInfo(caller.getClassName(), caller.getMethodName());
    }

    private static void setVerboseLogger(Logger LOGGER) {
    }
}
