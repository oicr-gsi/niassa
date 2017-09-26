/*
 * Copyright (C) 2017 SeqWare
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
package io.seqware.cli.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.ImmutableMap;

import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

/**
 *
 * @author mlaszloffy
 */
@PrepareForTest({net.sourceforge.seqware.common.util.configtools.ConfigTools.class})
@RunWith(PowerMockRunner.class)
public class CreateIusLimsKeyTest {

    @Before
    public void init() {
        PowerMockito.mockStatic(ConfigTools.class);
        PowerMockito.when(ConfigTools.getSettings()).thenReturn(ImmutableMap.of("SW_METADATA_METHOD", "inmemory"));
    }

    @Test
    public void createTest() {
        String id = "1";
        String provider = "seqware";
        String version = "426e0b848d77d7b3d778187d006056ee406221ca567ce2844ca403dc834ff8f1";
        String lastModified = "2017-01-01T01:01:01Z";

        Integer iusSwid = io.seqware.cli.Main.createIusLimsKey(Arrays.asList("--id", id, "--provider", provider, "--version", version,
                "--last-modified", lastModified));
        IUS ius = MetadataFactory.getInMemory().getIUS(iusSwid);
        assertNotNull(ius);
        assertNotNull(ius.getLimsKey());

        LimsKey lk = ius.getLimsKey();
        assertEquals(lk.getId(), id);
        assertEquals(lk.getProvider(), provider);
        assertEquals(lk.getVersion(), version);
        assertEquals(lk.getLastModified(), ZonedDateTime.parse(lastModified));
    }

    @Test
    public void createCliTest() {
        run("--id", "1", "--provider", "seqware", "--version", "426e0b848d77d7b3d778187d006056ee406221ca567ce2844ca403dc834ff8f1", "--last-modified", "2017-01-01T01:01:01Z");
    }

    @Test(expected = RuntimeException.class)
    public void malformedDateCliTest() {
        run("--id", "1", "--provider", "seqware", "--version", "426e0b848d77d7b3d778187d006056ee406221ca567ce2844ca403dc834ff8f1", "--last-modified", "2017-01-01 ???");
    }

    @Test(expected = RuntimeException.class)
    public void missingCliArgs() {
        run("--id", "1", "--provider", "seqware", "--version", "426e0b848d77d7b3d778187d006056ee406221ca567ce2844ca403dc834ff8f1");
    }

    private void run(String... args) {
        List<String> args2 = new ArrayList<>();
        args2.add("create");
        args2.add("ius-lims-key");
        args2.addAll(Arrays.asList(args));
        io.seqware.cli.Main.main(args2.toArray(new String[0]));
    }

}
