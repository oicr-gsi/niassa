/*
 * Copyright (C) 2013 SeqWare
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
package net.sourceforge.seqware.common.util.testtools;

import io.seqware.metadb.util.TestDatabaseCreator;
import io.seqware.pipeline.SqwKeys;
import static io.seqware.pipeline.SqwKeys.BASIC_TEST_DB_HOST;
import static io.seqware.pipeline.SqwKeys.BASIC_TEST_DB_NAME;
import static io.seqware.pipeline.SqwKeys.BASIC_TEST_DB_PASSWORD;
import static io.seqware.pipeline.SqwKeys.BASIC_TEST_DB_PORT;
import static io.seqware.pipeline.SqwKeys.BASIC_TEST_DB_USER;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

/**
 * This class controls configuration of the database used for integration tests.
 * 
 * If these keys are used in your .seqware/settings file, you can redirect your testing DB (which should match the REST URL that you require
 * as well, otherwise you will be resetting a database completely different from your web service)
 * 
 * @author dyuen
 */
public class BasicTestDatabaseCreator extends TestDatabaseCreator {

    private Map<String, String> settings = null;

    public BasicTestDatabaseCreator() {
        try {
            settings = ConfigTools.getSettings();
        } catch (Exception e) {
            Log.fatal("Could not read .seqware/settings, this will likely crash basic integration tests", e);
        }
    }
    
    public BasicTestDatabaseCreator(Map<String, String> settings) {
        this.settings = settings;
    }
    
    public static BasicTestDatabaseCreator getFromSystemProperties() {
        Map<String, String> settings = new HashMap<>();
        settings.put(BASIC_TEST_DB_NAME.getSettingKey(), System.getProperty("seqware_meta_db_name"));
        settings.put(BASIC_TEST_DB_PORT.getSettingKey(), System.getProperty("seqware_meta_db_port"));
        settings.put("POSTGRE_USER", System.getProperty("seqware_meta_db_user"));
        settings.put("POSTGRE_PASSWORD", System.getProperty("seqware_meta_db_password"));
        settings.put(BASIC_TEST_DB_USER.getSettingKey(), System.getProperty("seqware_meta_db_user"));
        settings.put(BASIC_TEST_DB_PASSWORD.getSettingKey(), System.getProperty("seqware_meta_db_password"));
        settings.put(BASIC_TEST_DB_HOST.getSettingKey(), System.getProperty("seqware_meta_db_host"));
        return new BasicTestDatabaseCreator(settings);
    }

    /**
     * @return the SEQWARE_DB
     */
    @Override
    protected String getSEQWARE_DB() {
        if (settings.containsKey(SqwKeys.BASIC_TEST_DB_NAME.getSettingKey())) {
            return settings.get(SqwKeys.BASIC_TEST_DB_NAME.getSettingKey());
        }
        Log.debug("Could not retrieve basic test db, using default from unit tests");
        return super.getSEQWARE_DB();
    }

    /**
     * @return the BASIC_TEST_USERNAME_KEY
     */
    @Override
    protected String getSEQWARE_USER() {
        if (settings.containsKey(SqwKeys.BASIC_TEST_DB_USER.getSettingKey())) {
            return settings.get(SqwKeys.BASIC_TEST_DB_USER.getSettingKey());
        }
        Log.debug("Could not retrieve basic test db username, using default from unit tests");
        return super.getSEQWARE_USER();
    }

    /**
     * @return the BASIC_TEST_PASSWORD_KEY
     */
    @Override
    protected String getSEQWARE_PASSWORD() {
        if (settings.containsKey(SqwKeys.BASIC_TEST_DB_PASSWORD.getSettingKey())) {
            return settings.get(SqwKeys.BASIC_TEST_DB_PASSWORD.getSettingKey());
        }
        Log.debug("Could not retrieve basic test db password, using default from unit tests");
        return super.getSEQWARE_PASSWORD();
    }

    /**
     * @return the DEFAULT_DB_HOST
     */
    @Override
    protected String getDEFAULT_DB_HOST() {
        if (settings.containsKey(SqwKeys.BASIC_TEST_DB_HOST.getSettingKey())) {
            return settings.get(SqwKeys.BASIC_TEST_DB_HOST.getSettingKey());
        }
        Log.debug("Could not retrieve basic test db host, using default from unit tests");
        return super.getDEFAULT_DB_HOST();
    }
    
    /**
     * @return the DEFAULT_DB_PORT
     */
    @Override
    protected String getDEFAULT_DB_PORT() {
        if (settings.containsKey(SqwKeys.BASIC_TEST_DB_PORT.getSettingKey())) {
            return settings.get(SqwKeys.BASIC_TEST_DB_PORT.getSettingKey());
        }
        Log.debug("Could not retrieve basic test db port, using default from unit tests");
        return super.getDEFAULT_DB_PORT();
    }
    
    /**
     * @return the POSTGRE_USER
     */
    @Override
    protected String getPOSTGRE_USER() {
        if (settings.containsKey("POSTGRE_USER")) {
            return settings.get("POSTGRE_USER");
        }
        Log.debug("Could not retrieve postgres user name, using default from unit tests");
        return super.getPOSTGRE_USER();
    }

    /**
     * @return the POSTGRE_PASSWORD
     */
    @Override
    protected String getPOSTGRE_PASSWORD() {
        if (settings.containsKey("POSTGRE_PASSWORD")) {
            return settings.get("POSTGRE_PASSWORD");
        }
        Log.debug("Could not retrieve postgres password, using default from unit tests");
        return super.getPOSTGRE_PASSWORD();
    }

    /**
     * Unfortunately, postgres does not allow the straight dropdb and createdb when tomcat is used (perhaps we leave open a connection)
     */
    public void resetDatabaseWithUsers() {
        basicResetDatabaseWithUsers();
    }
}
