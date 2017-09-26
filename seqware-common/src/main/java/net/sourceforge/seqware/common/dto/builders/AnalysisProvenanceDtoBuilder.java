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
package net.sourceforge.seqware.common.dto.builders;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.BeanUtils;

import net.sourceforge.seqware.common.dto.AnalysisProvenanceDto;
import net.sourceforge.seqware.common.dto.IusLimsKeyDto;
import net.sourceforge.seqware.common.dto.LimsKeyDto;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.LimsKey;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowAttribute;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;

/**
 *
 * @author mlaszloffy
 */
public class AnalysisProvenanceDtoBuilder {

    private Workflow workflow;
    private WorkflowRun workflowRun;
    private Processing processing;
    private File file;
    private Boolean skip = false;
    private final Set<IUS> iuses = new HashSet<>();

    public AnalysisProvenanceDtoBuilder setWorkflow(Workflow workflow) {
        this.workflow = workflow;
        return this;
    }

    public AnalysisProvenanceDtoBuilder setWorkflowRun(WorkflowRun workflowRun) {
        this.workflowRun = workflowRun;
        return this;
    }

    public AnalysisProvenanceDtoBuilder setProcessing(Processing processing) {
        this.processing = processing;
        return this;
    }

    public AnalysisProvenanceDtoBuilder setFile(File file) {
        this.file = file;
        return this;
    }

    
    public Boolean getSkip() {
        return skip;
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    
    public String getWorkflowName() {
        if (workflow == null) {
            return null;
        } else {
            return workflow.getName();
        }
    }

    
    public String getWorkflowVersion() {
        if (workflow == null) {
            return null;
        } else {
            return workflow.getVersion();
        }
    }

    
    public Integer getWorkflowId() {
        if (workflow == null) {
            return null;
        } else {
            return workflow.getSwAccession();
        }
    }

    
    public SortedMap<String, SortedSet<String>> getWorkflowAttributes() {
        if (workflow == null) {
            return Collections.emptySortedMap();
        } else {
            SortedMap<String, SortedSet<String>> map = new TreeMap<>();
            for (WorkflowAttribute attr : workflow.getWorkflowAttributes()) {
                SortedSet<String> values = map.get(attr.getTag());
                if (values == null) {
                    values = new TreeSet<>();
                    map.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
            return Collections.unmodifiableSortedMap(map);
        }
    }

    
    public String getWorkflowRunName() {
        if (workflowRun == null) {
            return null;
        } else {
            return workflowRun.getName();
        }
    }

    
    public String getWorkflowRunStatus() {
        if (workflowRun == null || workflowRun.getStatus() == null) {
            return null;
        } else {
            return workflowRun.getStatus().toString();
        }
    }

    
    public Integer getWorkflowRunId() {
        if (workflowRun == null) {
            return null;
        } else {
            return workflowRun.getSwAccession();
        }
    }

    
    public SortedMap<String, SortedSet<String>> getWorkflowRunAttributes() {
        if (workflowRun == null) {
            return Collections.emptySortedMap();
        } else {
            SortedMap<String, SortedSet<String>> map = new TreeMap<>();
            for (WorkflowRunAttribute attr : workflowRun.getWorkflowRunAttributes()) {
                SortedSet<String> values = map.get(attr.getTag());
                if (values == null) {
                    values = new TreeSet<>();
                    map.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
            return Collections.unmodifiableSortedMap(map);
        }
    }

    
    public SortedSet<Integer> getWorkflowRunInputFileIds() {
        if (workflowRun == null || workflowRun.getInputFileAccessions() == null) {
            return Collections.emptySortedSet();
        } else {
            return Collections.unmodifiableSortedSet(new TreeSet<>(workflowRun.getInputFileAccessions()));
        }
    }

    
    public String getProcessingAlgorithm() {
        if (processing == null) {
            return null;
        } else {
            return processing.getAlgorithm();
        }
    }

    
    public Integer getProcessingId() {
        if (processing == null) {
            return null;
        } else {
            return processing.getSwAccession();
        }
    }

    
    public SortedMap<String, SortedSet<String>> getProcessingAttributes() {
        if (processing == null) {
            return Collections.emptySortedMap();
        } else {
            SortedMap<String, SortedSet<String>> map = new TreeMap<>();
            for (ProcessingAttribute attr : processing.getProcessingAttributes()) {
                SortedSet<String> values = map.get(attr.getTag());
                if (values == null) {
                    values = new TreeSet<>();
                    map.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
            return Collections.unmodifiableSortedMap(map);
        }
    }

    
    public String getProcessingStatus() {
        if (processing == null || processing.getStatus() == null) {
            return null;
        } else {
            return processing.getStatus().toString();
        }
    }

    
    public String getFileMetaType() {
        if (file == null) {
            return null;
        } else {
            return file.getMetaType();
        }
    }

    
    public Integer getFileId() {
        if (file == null) {
            return null;
        } else {
            return file.getSwAccession();
        }
    }

    
    public SortedMap<String, SortedSet<String>> getFileAttributes() {
        if (file == null) {
            return Collections.emptySortedMap();
        } else {
            SortedMap<String, SortedSet<String>> map = new TreeMap<>();
            for (FileAttribute attr : file.getFileAttributes()) {
                SortedSet<String> values = map.get(attr.getTag());
                if (values == null) {
                    values = new TreeSet<>();
                    map.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
            return Collections.unmodifiableSortedMap(map);
        }
    }

    
    public String getFilePath() {
        if (file == null) {
            return null;
        } else {
            return file.getFilePath();
        }
    }

    
    public String getFileMd5sum() {
        if (file == null) {
            return null;
        } else {
            return file.getMd5sum();
        }
    }

    
    public String getFileSize() {
        if (file == null) {
            return null;
        }
        if (file.getSize() == null) {
            return null;
        }
        return file.getSize().toString();
    }

    
    public String getFileDescription() {
        if (file == null) {
            return null;
        } else {
            return file.getDescription();
        }
    }

    
    public DateTime getLastModified() {
        if (processing == null) {
            return null;
        } else if (processing.getUpdateTimestamp() != null) {
            return new DateTime(processing.getUpdateTimestamp()).toDateTime(DateTimeZone.UTC);
        } else if (processing.getCreateTimestamp() != null) {
            return new DateTime(processing.getCreateTimestamp()).toDateTime(DateTimeZone.UTC);
        } else {
            return null;
        }
    }

    public void addIus(IUS ius) {
        this.iuses.add(ius);
    }

    public void addIuses(Set<IUS> iuses) {
        this.iuses.addAll(iuses);
    }

    
    public SortedMap<String, SortedSet<String>> getIusAttributes() {
        SortedMap<String, SortedSet<String>> map = new TreeMap<>();
        for (IUS ius : iuses) {
            for (IUSAttribute attr : ius.getIusAttributes()) {
                SortedSet<String> values = map.get(attr.getTag());
                if (values == null) {
                    values = new TreeSet<>();
                    map.put(attr.getTag(), values);
                }
                values.add(attr.getValue());
            }
        }
        return Collections.unmodifiableSortedMap(map);
    }

    
    public Set<IusLimsKeyDto> getIusLimsKeys() {
        Set<IusLimsKeyDto> iusLimsKeys = new HashSet<>();
        for (IUS ius : iuses) {
            IusLimsKeyDto ilk = new IusLimsKeyDto();
            ilk.setIusSWID(ius.getSwAccession());
            LimsKey lk = ius.getLimsKey();
            LimsKeyDto lkDto = new LimsKeyDto();
            lkDto.setId(lk.getId());
            lkDto.setVersion(lk.getVersion());
            lkDto.setLastModified(lk.getLastModified());
            lkDto.setProvider(lk.getProvider());
            ilk.setLimsKey(lkDto);
            iusLimsKeys.add(ilk);
        }
        return iusLimsKeys;
    }

    public AnalysisProvenanceDto build() {
        AnalysisProvenanceDto dto = new AnalysisProvenanceDto();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

}
