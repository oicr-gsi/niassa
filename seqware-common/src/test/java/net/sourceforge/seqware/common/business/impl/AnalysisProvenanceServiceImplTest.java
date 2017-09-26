/*
 * Copyright (C) 2015 SeqWare
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
package net.sourceforge.seqware.common.business.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.PersistenceException;

import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import ca.on.oicr.gsi.provenance.FileProvenanceFilter;
import net.sourceforge.seqware.common.AbstractTestCase;
import net.sourceforge.seqware.common.business.AnalysisProvenanceService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LimsKeyService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.dto.IusLimsKeyDto;
import net.sourceforge.seqware.common.dto.LimsKeyDto;
import net.sourceforge.seqware.common.err.DataIntegrityException;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceServiceImplTest extends AbstractTestCase {

    @Autowired
    @Qualifier("analysisProvenanceService")
    AnalysisProvenanceService aprs;

    @Autowired
    @Qualifier("limsKeyService")
    LimsKeyService limsKeyService;

    @Autowired
    @Qualifier("IUSService")
    IUSService iusService;

    @Autowired
    @Qualifier("workflowService")
    WorkflowService workflowService;

    @Autowired
    @Qualifier("workflowRunService")
    WorkflowRunService workflowRunService;

    @Autowired
    @Qualifier("processingService")
    ProcessingService processingService;

    @Autowired
    @Qualifier("fileService")
    FileService fileService;

    @Autowired
    SessionFactory sessionFactory;

    private final int baseExpectedCount = 26;

    @Test
    public void getAllRecords() {
        //+ 20 IUS without workflow runs 
        //+ 3 files attached to workflow run
        //+ 3 workflow runs without files
        //= 25 expected records
        assertEquals(baseExpectedCount, aprs.list().size());
    }

    @Test
    public void singleRecordTest() {
        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";
        String expectedFileMetaType = "my-file/meta-type";
        String expectedProvider = "seqware";
        String expectedId = "1_1_1";
        String expectedVersion = "2dc238bf1d7e1f6b6a110bb9592be4e7b83ee8a144c20fb0632144b66b3735cf";
        ZonedDateTime expectedLastModified = ZonedDateTime.parse("2016-01-01T00:00:00Z");

        LimsKey limsKey = new LimsKey();
        limsKey.setProvider(expectedProvider);
        limsKey.setId(expectedId);
        limsKey.setVersion(expectedVersion);
        limsKey.setLastModified(expectedLastModified);
        limsKeyService.insert(limsKey);

        IUS ius = new IUS();
        ius.setLimsKey(limsKey);
        iusService.insert(ius);

        Workflow workflow = new Workflow();
        workflow.setName(expectedWorkflowName);
        workflowService.insert(workflow);
        Integer expectedWorkflowSwid = workflow.getSwAccession();

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflow(workflow);
        workflowRun.setIus(ImmutableSortedSet.of(ius));
        workflowRun.setProcessings(new TreeSet<Processing>());
        workflowRunService.insert(workflowRun);
        Integer expectedWorkflowRunSwid = workflowRun.getSwAccession();

        Processing processing = new Processing();
        processing.setAlgorithm(expectedProcessingAlgorithm);
        processing.setWorkflowRun(workflowRun);
        processing.setFiles(new HashSet<File>());
        processingService.insert(processing);

        File file = new File();
        file.setProcessings(ImmutableSortedSet.of(processing));
        file.setFilePath(expectedFilePath);
        file.setMetaType(expectedFileMetaType);
        fileService.insert(file);
        Integer expectedFileSwid = file.getSwAccession();

        //original + 1 new file
        assertEquals(baseExpectedCount + 1, aprs.list().size());

        assertEquals(1, aprs.findForIus(ius).size());
        AnalysisProvenanceDto ap = Iterables.getOnlyElement(aprs.findForIus(ius));
        assertEquals(expectedWorkflowName, ap.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, ap.getProcessingAlgorithm());
        assertEquals(expectedFilePath, ap.getFilePath());

        assertEquals(1, ap.getIusLimsKeys().size());
        IusLimsKeyDto ilk = Iterables.getOnlyElement(ap.getIusLimsKeys());
        assertEquals(ius.getSwAccession(), ilk.getIusSWID());

        LimsKeyDto lk = ilk.getLimsKey();
        assertEquals(expectedId, lk.getId());
        assertEquals(expectedLastModified.toInstant(), lk.getLastModified().toInstant());
        assertEquals(expectedProvider, lk.getProvider());
        assertEquals(expectedVersion, lk.getVersion());

        Map<FileProvenanceFilter, Set<String>> filters = new HashMap<>();
        List<AnalysisProvenanceDto> aps;
        AnalysisProvenanceDto dto;

        filters.clear();
        filters.put(FileProvenanceFilter.workflow, ImmutableSet.of(expectedWorkflowSwid.toString()));
        aps = aprs.list(filters);
        assertEquals(1, aps.size());
        dto = Iterables.getOnlyElement(aps);
        assertEquals(expectedWorkflowName, dto.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, dto.getProcessingAlgorithm());
        assertEquals(expectedFilePath, dto.getFilePath());

        filters.clear();
        filters.put(FileProvenanceFilter.file, ImmutableSet.of(expectedFileSwid.toString()));
        aps = aprs.list(filters);
        assertEquals(1, aps.size());
        dto = Iterables.getOnlyElement(aps);
        assertEquals(expectedWorkflowName, dto.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, dto.getProcessingAlgorithm());
        assertEquals(expectedFilePath, dto.getFilePath());

        filters.clear();
        filters.put(FileProvenanceFilter.file_meta_type, ImmutableSet.of(expectedFileMetaType));
        aps = aprs.list(filters);
        assertEquals(1, aps.size());
        dto = Iterables.getOnlyElement(aps);
        assertEquals(expectedWorkflowName, dto.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, dto.getProcessingAlgorithm());
        assertEquals(expectedFilePath, dto.getFilePath());

        filters.clear();
        filters.put(FileProvenanceFilter.workflow_run, ImmutableSet.of(expectedWorkflowRunSwid.toString()));
        aps = aprs.list(filters);
        assertEquals(1, aps.size());
        dto = Iterables.getOnlyElement(aps);
        assertEquals(expectedWorkflowName, dto.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, dto.getProcessingAlgorithm());
        assertEquals(expectedFilePath, dto.getFilePath());

        filters.clear();
        filters.put(FileProvenanceFilter.workflow_run, ImmutableSet.of(expectedWorkflowRunSwid.toString()));
        filters.put(FileProvenanceFilter.file_meta_type, ImmutableSet.of(expectedFileMetaType));
        filters.put(FileProvenanceFilter.file, ImmutableSet.of(expectedFileSwid.toString()));
        filters.put(FileProvenanceFilter.workflow, ImmutableSet.of(expectedWorkflowSwid.toString()));
        aps = aprs.list(filters);
        assertEquals(1, aps.size());
        dto = Iterables.getOnlyElement(aps);
        assertEquals(expectedWorkflowName, dto.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, dto.getProcessingAlgorithm());
        assertEquals(expectedFilePath, dto.getFilePath());
    }

    @Test
    public void multipleInputIusTest() {
        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";
        String expectedProvider = "seqware";
        String expectedId = "1_1_1";
        String expectedVersion = "2dc238bf1d7e1f6b6a110bb9592be4e7b83ee8a144c20fb0632144b66b3735cf";
        ZonedDateTime expectedLastModified = ZonedDateTime.parse("2016-01-01T00:00:00Z");

        //first IusLimsKey
        LimsKey limsKey1 = new LimsKey();
        limsKey1.setProvider(expectedProvider);
        limsKey1.setId(expectedId);
        limsKey1.setVersion(expectedVersion);
        limsKey1.setLastModified(expectedLastModified);
        limsKeyService.insert(limsKey1);

        IUS ius1 = new IUS();
        ius1.setLimsKey(limsKey1);
        iusService.insert(ius1);

        //second IusLimsKey
        LimsKey limsKey2 = new LimsKey();
        limsKey2.setProvider(expectedProvider);
        limsKey2.setId(expectedId);
        limsKey2.setVersion(expectedVersion);
        limsKey2.setLastModified(expectedLastModified);
        limsKeyService.insert(limsKey2);

        IUS ius2 = new IUS();
        ius2.setLimsKey(limsKey2);
        iusService.insert(ius2);

        Workflow workflow = new Workflow();
        workflow.setName(expectedWorkflowName);
        workflowService.insert(workflow);

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflow(workflow);
        workflowRun.setIus(ImmutableSortedSet.of(ius1, ius2));
        workflowRun.setProcessings(new TreeSet<Processing>());
        workflowRunService.insert(workflowRun);

        Processing processing = new Processing();
        processing.setAlgorithm(expectedProcessingAlgorithm);
        processing.setWorkflowRun(workflowRun);
        processing.setFiles(new HashSet<File>());
        processingService.insert(processing);

        File file = new File();
        file.setProcessings(ImmutableSortedSet.of(processing));
        file.setFilePath(expectedFilePath);
        fileService.insert(file);

        assertEquals(baseExpectedCount + 1, aprs.list().size());

        assertEquals(1, aprs.findForIus(ius1).size());
        AnalysisProvenanceDto ap = Iterables.getOnlyElement(aprs.findForIus(ius2));
        assertEquals(expectedWorkflowName, ap.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, ap.getProcessingAlgorithm());
        assertEquals(expectedFilePath, ap.getFilePath());

        assertEquals(2, ap.getIusLimsKeys().size());
        for (IusLimsKeyDto ilk : ap.getIusLimsKeys()) {
            LimsKeyDto lk = ilk.getLimsKey();
            assertEquals(expectedId, lk.getId());
            assertEquals(expectedLastModified.toInstant(), lk.getLastModified().toInstant());
            assertEquals(expectedProvider, lk.getProvider());
            assertEquals(expectedVersion, lk.getVersion());
        }

        //skip one of the IUS - all files for the workflow run should be skipped
        ius2.setSkip(Boolean.TRUE);
        iusService.update(ius2);
        assertEquals(true, Iterables.getOnlyElement(aprs.findForIus(ius1)).getSkip());
        assertEquals(true, Iterables.getOnlyElement(aprs.findForIus(ius2)).getSkip());
    }

    @Test
    public void iusLinkedToProcessingTest() {
        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";
        String expectedProvider = "seqware";
        String expectedId = "1_1_1";
        String expectedVersion = "2dc238bf1d7e1f6b6a110bb9592be4e7b83ee8a144c20fb0632144b66b3735cf";
        ZonedDateTime expectedLastModified = ZonedDateTime.parse("2016-01-01T00:00:00Z");

        //first IusLimsKey
        LimsKey limsKey1 = new LimsKey();
        limsKey1.setProvider(expectedProvider);
        limsKey1.setId(expectedId);
        limsKey1.setVersion(expectedVersion);
        limsKey1.setLastModified(expectedLastModified);
        limsKeyService.insert(limsKey1);

        IUS ius1 = new IUS();
        ius1.setLimsKey(limsKey1);
        iusService.insert(ius1);

        //second IusLimsKey
        LimsKey limsKey2 = new LimsKey();
        limsKey2.setProvider(expectedProvider);
        limsKey2.setId(expectedId);
        limsKey2.setVersion(expectedVersion);
        limsKey2.setLastModified(expectedLastModified);
        limsKeyService.insert(limsKey2);

        IUS ius2 = new IUS();
        ius2.setLimsKey(limsKey2);
        iusService.insert(ius2);

        Workflow workflow = new Workflow();
        workflow.setName(expectedWorkflowName);
        workflowService.insert(workflow);

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflow(workflow);
        workflowRun.setIus(ImmutableSortedSet.of(ius1, ius2));
        workflowRun.setProcessings(new TreeSet<Processing>());
        workflowRunService.insert(workflowRun);

        //first processing+file - linked only to IUS1
        Processing processing1 = new Processing();
        processing1.setAlgorithm(expectedProcessingAlgorithm);
        processing1.setWorkflowRun(workflowRun);
        processing1.setFiles(new HashSet<File>());
        processingService.insert(processing1);
        ius1.getProcessings().add(processing1);
        iusService.update(ius1);

        File file1 = new File();
        file1.setProcessings(ImmutableSortedSet.of(processing1));
        file1.setFilePath(expectedFilePath);
        fileService.insert(file1);

        //second processing+file - linked only to IUS2
        Processing processing2 = new Processing();
        processing2.setAlgorithm(expectedProcessingAlgorithm);
        processing2.setWorkflowRun(workflowRun);
        processing2.setFiles(new HashSet<File>());
        processingService.insert(processing2);
        ius2.getProcessings().add(processing2);
        iusService.update(ius2);

        File file2 = new File();
        file2.setProcessings(ImmutableSortedSet.of(processing2));
        file2.setFilePath(expectedFilePath);
        fileService.insert(file2);

        assertEquals(baseExpectedCount + 2, aprs.list().size());

        assertEquals(1, aprs.findForIus(ius1).size());
        AnalysisProvenanceDto ap1 = Iterables.getOnlyElement(aprs.findForIus(ius1));
        assertEquals(expectedWorkflowName, ap1.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, ap1.getProcessingAlgorithm());
        assertEquals(expectedFilePath, ap1.getFilePath());

        assertEquals(1, ap1.getIusLimsKeys().size());
        IusLimsKeyDto ilk1 = Iterables.getOnlyElement(ap1.getIusLimsKeys());
        assertEquals(ius1.getSwAccession(), ilk1.getIusSWID());

        LimsKeyDto lk1 = ilk1.getLimsKey();
        assertEquals(expectedId, lk1.getId());
        assertEquals(expectedLastModified.toInstant(), lk1.getLastModified().toInstant());
        assertEquals(expectedProvider, lk1.getProvider());
        assertEquals(expectedVersion, lk1.getVersion());

        assertEquals(1, aprs.findForIus(ius2).size());
        AnalysisProvenanceDto ap2 = Iterables.getOnlyElement(aprs.findForIus(ius2));
        assertEquals(expectedWorkflowName, ap2.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, ap2.getProcessingAlgorithm());
        assertEquals(expectedFilePath, ap2.getFilePath());

        assertEquals(1, ap2.getIusLimsKeys().size());
        IusLimsKeyDto ilk2 = Iterables.getOnlyElement(ap2.getIusLimsKeys());
        assertEquals(ius2.getSwAccession(), ilk2.getIusSWID());

        LimsKeyDto lk2 = ilk2.getLimsKey();
        assertEquals(expectedId, lk2.getId());
        assertEquals(expectedLastModified.toInstant(), lk2.getLastModified().toInstant());
        assertEquals(expectedProvider, lk2.getProvider());
        assertEquals(expectedVersion, lk2.getVersion());

        //skip one of the IUS - the processing is linked to IUS so only one file should be skipped
        ius2.setSkip(Boolean.TRUE);
        iusService.update(ius2);
        assertEquals(false, Iterables.getOnlyElement(aprs.findForIus(ius1)).getSkip());
        assertEquals(true, Iterables.getOnlyElement(aprs.findForIus(ius2)).getSkip());
    }

    @Test
    public void noFilesYetTest() {
        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";
        String expectedProvider = "seqware";
        String expectedId = "1_1_1";
        String expectedVersion = "2dc238bf1d7e1f6b6a110bb9592be4e7b83ee8a144c20fb0632144b66b3735cf";
        ZonedDateTime expectedLastModified = ZonedDateTime.parse("2016-01-01T00:00:00Z");

        //first IusLimsKey
        LimsKey limsKey1 = new LimsKey();
        limsKey1.setProvider(expectedProvider);
        limsKey1.setId(expectedId);
        limsKey1.setVersion(expectedVersion);
        limsKey1.setLastModified(expectedLastModified);
        limsKeyService.insert(limsKey1);

        IUS ius1 = new IUS();
        ius1.setLimsKey(limsKey1);
        iusService.insert(ius1);

        //second IusLimsKey
        LimsKey limsKey2 = new LimsKey();
        limsKey2.setProvider(expectedProvider);
        limsKey2.setId(expectedId);
        limsKey2.setVersion(expectedVersion);
        limsKey2.setLastModified(expectedLastModified);
        limsKeyService.insert(limsKey2);

        IUS ius2 = new IUS();
        ius2.setLimsKey(limsKey2);
        iusService.insert(ius2);

        Workflow workflow = new Workflow();
        workflow.setName(expectedWorkflowName);
        workflowService.insert(workflow);

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflow(workflow);
        workflowRun.setIus(ImmutableSortedSet.of(ius1, ius2));
        workflowRun.setProcessings(new TreeSet<Processing>());
        workflowRun.setOffspringProcessings(new TreeSet<Processing>());
        workflowRunService.insert(workflowRun);

        Processing p1 = new Processing();
        p1.setAlgorithm("start");
        p1.setWorkflowRun(workflowRun);
        processingService.insert(p1);

        Processing p2 = new Processing();
        p2.setAlgorithm("pfi");
        p2.setWorkflowRunByAncestorWorkflowRunId(workflowRun);
        processingService.insert(p2);
        p1.setChildren(ImmutableSortedSet.of(p2));
        processingService.update(p1);

        Processing p3 = new Processing();
        p3.setAlgorithm(expectedProcessingAlgorithm);
        p3.setWorkflowRunByAncestorWorkflowRunId(workflowRun);
        processingService.insert(p3);
        p2.setChildren(ImmutableSortedSet.of(p3));
        processingService.update(p2);

        //no file yet... should have a record with workflow run info though
        assertEquals(baseExpectedCount + 1, aprs.list().size());

        assertEquals(1, aprs.findForIus(ius1).size());
        assertEquals(1, aprs.findForIus(ius2).size());
        AnalysisProvenanceDto apBeforeAddingFile = Iterables.getOnlyElement(aprs.findForIus(ius1));
        assertEquals(expectedWorkflowName, apBeforeAddingFile.getWorkflowName());
        assertNull(apBeforeAddingFile.getProcessingAlgorithm());
        assertNull(apBeforeAddingFile.getFilePath());

        assertEquals(2, apBeforeAddingFile.getIusLimsKeys().size());
        for (IusLimsKeyDto ilk : apBeforeAddingFile.getIusLimsKeys()) {
            LimsKeyDto lk = ilk.getLimsKey();
            assertEquals(expectedId, lk.getId());
            assertEquals(expectedLastModified.toInstant(), lk.getLastModified().toInstant());
            assertEquals(expectedProvider, lk.getProvider());
            assertEquals(expectedVersion, lk.getVersion());
        }

        //add a file
        File file = new File();
        file.setProcessings(ImmutableSortedSet.of(p3));
        file.setFilePath(expectedFilePath);
        fileService.insert(file);

        //previous record should now have processing + file information
        assertEquals(baseExpectedCount + 1, aprs.list().size());

        assertEquals(1, aprs.findForIus(ius1).size());
        assertEquals(1, aprs.findForIus(ius2).size());
        AnalysisProvenanceDto apAfterAddingFile = Iterables.getOnlyElement(aprs.findForIus(ius1));
        assertEquals(expectedWorkflowName, apAfterAddingFile.getWorkflowName());
        assertEquals(expectedProcessingAlgorithm, apAfterAddingFile.getProcessingAlgorithm());
        assertEquals(expectedFilePath, apAfterAddingFile.getFilePath());

        assertEquals(2, apAfterAddingFile.getIusLimsKeys().size());
        for (IusLimsKeyDto ilk : apAfterAddingFile.getIusLimsKeys()) {
            LimsKeyDto lk = ilk.getLimsKey();
            assertEquals(expectedId, lk.getId());
            assertEquals(expectedLastModified.toInstant(), lk.getLastModified().toInstant());
            assertEquals(expectedProvider, lk.getProvider());
            assertEquals(expectedVersion, lk.getVersion());
        }
    }

    @Test
    public void attributesAndDeletionTest() {
        String expectedWorkflowName = "test_workflow";
        String expectedProcessingAlgorithm = "test_algorithm";
        String expectedFilePath = "/tmp/file.out";
        String expectedProvider = "seqware";
        String expectedId = "1_1_1";
        String expectedVersion = "2dc238bf1d7e1f6b6a110bb9592be4e7b83ee8a144c20fb0632144b66b3735cf";
        ZonedDateTime expectedLastModified = ZonedDateTime.parse("2016-01-01T00:00:00Z");
        String expectedTag1 = "testTag1";
        String expectedValue1 = "testValue1";
        String expectedTag2 = "testTag2";
        String expectedValue2 = "testValue2";
        String expectedIusTag = "iusTag";
        String expectedIusValue = "iusValue";

        LimsKey limsKey = new LimsKey();
        limsKey.setProvider(expectedProvider);
        limsKey.setId(expectedId);
        limsKey.setVersion(expectedVersion);
        limsKey.setLastModified(expectedLastModified);
        limsKeyService.insert(limsKey);

        IUS ius = new IUS();
        ius.setLimsKey(limsKey);
        iusService.insert(ius);

        IUSAttribute ia = new IUSAttribute();
        ia.setTag(expectedIusTag);
        ia.setValue(expectedIusValue);
        ia.setIus(ius);
        ius.getIusAttributes().add(ia);

        Workflow workflow = new Workflow();
        workflow.setName(expectedWorkflowName);
        workflowService.insert(workflow);

        WorkflowRun workflowRun = new WorkflowRun();
        workflowRun.setWorkflow(workflow);
        workflowRun.setIus(ImmutableSortedSet.of(ius));
        workflowRun.setProcessings(new TreeSet<Processing>());
        workflowRunService.insert(workflowRun);

        Processing processing = new Processing();
        processing.setAlgorithm(expectedProcessingAlgorithm);
        processing.setWorkflowRun(workflowRun);
        processing.setFiles(new HashSet<File>());
        processingService.insert(processing);

        FileAttribute fa1 = new FileAttribute();
        fa1.setTag(expectedTag1);
        fa1.setValue(expectedValue1);

        FileAttribute fa2 = new FileAttribute();
        fa2.setTag(expectedTag2);
        fa2.setValue(expectedValue2);

        File file = new File();
        file.setProcessings(ImmutableSortedSet.of(processing));
        file.setFilePath(expectedFilePath);
        file.setFileAttributes(ImmutableSortedSet.of(fa1, fa2));
        fa1.setFile(file);
        fa2.setFile(file);
        fileService.insert(file);

        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        //original 25 + 1 new file
        assertEquals(baseExpectedCount + 1, aprs.list().size());

        List<AnalysisProvenanceDto> aps = aprs.findForIus(ius);
        assertEquals(1, aps.size());
        AnalysisProvenanceDto ap = Iterables.getOnlyElement(aps);
        assertEquals(Sets.newHashSet(expectedValue1), ap.getFileAttributes().get(expectedTag1));
        assertEquals(Sets.newHashSet(expectedValue2), ap.getFileAttributes().get(expectedTag2));
        assertEquals(Sets.newHashSet(expectedIusValue), ap.getIusAttributes().get(expectedIusTag));

        //delete the ius and lims key
        IUS iusToDelete = iusService.findBySWAccession(ius.getSwAccession());
        Integer limsKeySwid = iusToDelete.getLimsKey().getSwAccession();
        iusToDelete.setLimsKey(null);
        iusToDelete.getWorkflowRuns().clear();
        iusToDelete.getProcessings().clear();
        iusService.update(iusToDelete);
        try {
            iusService.delete(iusToDelete);
        } catch (DataIntegrityException ex) {
            throw new RuntimeException(ex);
        }

        LimsKey limsKeyToDelete = limsKeyService.findBySWAccession(limsKeySwid);
        try {
            limsKeyService.delete(limsKeyToDelete);
        } catch (PersistenceException ex) {
            throw new RuntimeException(ex);
        }

        assertNull(iusService.findBySWAccession(ius.getSwAccession()));
        assertNull(limsKeyService.findBySWAccession(limsKeySwid));
        assertNotNull(workflowRunService.findBySWAccession(workflowRun.getSwAccession()));
        assertNotNull(workflowService.findBySWAccession(workflow.getSwAccession()));
        assertNotNull(processingService.findBySWAccession(processing.getSwAccession()));
        assertNotNull(fileService.findBySWAccession(file.getSwAccession()));

        assertEquals(baseExpectedCount, aprs.list().size());
    }
}
