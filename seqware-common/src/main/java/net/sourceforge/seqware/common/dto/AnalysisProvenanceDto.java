/*
 * Copyright (C) 2016 SeqWare
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
package net.sourceforge.seqware.common.dto;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import ca.on.oicr.gsi.provenance.model.AnalysisProvenance;
import ca.on.oicr.gsi.provenance.model.IusLimsKey;
import net.sourceforge.seqware.common.model.adapters.DateTimeAdapter;
import net.sourceforge.seqware.common.model.adapters.IntegerSortedSet;
import net.sourceforge.seqware.common.model.adapters.IusLimsKeyAdapter;
import net.sourceforge.seqware.common.model.adapters.SortedMapOfSortedSetAdapter;

/**
 *
 * @author mlaszloffy
 */
@XmlRootElement(name="dto")
public class AnalysisProvenanceDto implements AnalysisProvenance {

    protected String workflowName;
    protected String workflowVersion;
    protected Integer workflowId;
    protected SortedMap<String, SortedSet<String>> workflowAttributes;
    protected String workflowRunName;
    protected String workflowRunStatus;
    protected Integer workflowRunId;
    protected SortedMap<String, SortedSet<String>> workflowRunAttributes;
    protected SortedSet<Integer> workflowRunInputFileIds;
    protected String processingAlgorithm;
    protected Integer processingId;
    protected String processingStatus;
    protected SortedMap<String, SortedSet<String>> processingAttributes;
    protected String fileMetaType;
    protected Integer fileId;
    protected String filePath;
    protected String fileMd5sum;
    protected String fileSize;
    protected String fileDescription;
    protected SortedMap<String, SortedSet<String>> fileAttributes;
    protected Boolean skip;
    protected ZonedDateTime lastModified;
    protected Set<IusLimsKey> iusLimsKeys;
    protected SortedMap<String, SortedSet<String>> iusAttributes;

    
    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    
    public String getWorkflowVersion() {
        return workflowVersion;
    }

    public void setWorkflowVersion(String workflowVersion) {
        this.workflowVersion = workflowVersion;
    }

    
    public Integer getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Integer workflowId) {
        this.workflowId = workflowId;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    
    public SortedMap<String, SortedSet<String>> getWorkflowAttributes() {
        return workflowAttributes;
    }

    public void setWorkflowAttributes(SortedMap<String, SortedSet<String>> workflowAttributes) {
        this.workflowAttributes = workflowAttributes;
    }

    
    public String getWorkflowRunName() {
        return workflowRunName;
    }

    public void setWorkflowRunName(String workflowRunName) {
        this.workflowRunName = workflowRunName;
    }

    
    public String getWorkflowRunStatus() {
        return workflowRunStatus;
    }

    public void setWorkflowRunStatus(String workflowRunStatus) {
        this.workflowRunStatus = workflowRunStatus;
    }

    
    public Integer getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(Integer workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    
    public SortedMap<String, SortedSet<String>> getWorkflowRunAttributes() {
        return workflowRunAttributes;
    }

    public void setWorkflowRunAttributes(SortedMap<String, SortedSet<String>> workflowRunAttributes) {
        this.workflowRunAttributes = workflowRunAttributes;
    }

    @XmlJavaTypeAdapter(IntegerSortedSet.class)
    
    public SortedSet<Integer> getWorkflowRunInputFileIds() {
        return workflowRunInputFileIds;
    }

    public void setWorkflowRunInputFileIds(SortedSet<Integer> workflowRunInputFileIds) {
        this.workflowRunInputFileIds = workflowRunInputFileIds;
    }

    
    public String getProcessingAlgorithm() {
        return processingAlgorithm;
    }

    public void setProcessingAlgorithm(String processingAlgorithm) {
        this.processingAlgorithm = processingAlgorithm;
    }

    
    public Integer getProcessingId() {
        return processingId;
    }

    public void setProcessingId(Integer processingId) {
        this.processingId = processingId;
    }

    
    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    
    public SortedMap<String, SortedSet<String>> getProcessingAttributes() {
        return processingAttributes;
    }

    public void setProcessingAttributes(SortedMap<String, SortedSet<String>> processingAttributes) {
        this.processingAttributes = processingAttributes;
    }

    
    public String getFileMetaType() {
        return fileMetaType;
    }

    public void setFileMetaType(String fileMetaType) {
        this.fileMetaType = fileMetaType;
    }

    
    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    
    public String getFileMd5sum() {
        return fileMd5sum;
    }

    public void setFileMd5sum(String fileMd5sum) {
        this.fileMd5sum = fileMd5sum;
    }

    
    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    
    public String getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(String fileDescription) {
        this.fileDescription = fileDescription;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    
    public SortedMap<String, SortedSet<String>> getFileAttributes() {
        return fileAttributes;
    }

    public void setFileAttributes(SortedMap<String, SortedSet<String>> fileAttributes) {
        this.fileAttributes = fileAttributes;
    }

    
    public Boolean getSkip() {
        return skip;
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    @XmlElementWrapper(name = "iusLimsKeys")
    @XmlElement(name = "iusLimsKey")
    @XmlJavaTypeAdapter(IusLimsKeyAdapter.class)
    
    public Set<IusLimsKey> getIusLimsKeys() {
        return iusLimsKeys;
    }

    public void setIusLimsKeys(Set<IusLimsKey> keys) {
        this.iusLimsKeys = keys;
    }

    @XmlJavaTypeAdapter(SortedMapOfSortedSetAdapter.class)
    
    public SortedMap<String, SortedSet<String>> getIusAttributes() {
        return iusAttributes;
    }

    public void setIusAttributes(SortedMap<String, SortedSet<String>> iusAttributes) {
        this.iusAttributes = iusAttributes;
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileAttributes == null) ? 0 : fileAttributes.hashCode());
		result = prime * result + ((fileDescription == null) ? 0 : fileDescription.hashCode());
		result = prime * result + ((fileId == null) ? 0 : fileId.hashCode());
		result = prime * result + ((fileMd5sum == null) ? 0 : fileMd5sum.hashCode());
		result = prime * result + ((fileMetaType == null) ? 0 : fileMetaType.hashCode());
		result = prime * result + ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result + ((fileSize == null) ? 0 : fileSize.hashCode());
		result = prime * result + ((iusAttributes == null) ? 0 : iusAttributes.hashCode());
		result = prime * result + ((iusLimsKeys == null) ? 0 : iusLimsKeys.hashCode());
		result = prime * result + ((lastModified == null) ? 0 : lastModified.toInstant().hashCode());
		result = prime * result + ((processingAlgorithm == null) ? 0 : processingAlgorithm.hashCode());
		result = prime * result + ((processingAttributes == null) ? 0 : processingAttributes.hashCode());
		result = prime * result + ((processingId == null) ? 0 : processingId.hashCode());
		result = prime * result + ((processingStatus == null) ? 0 : processingStatus.hashCode());
		result = prime * result + ((skip == null) ? 0 : skip.hashCode());
		result = prime * result + ((workflowAttributes == null) ? 0 : workflowAttributes.hashCode());
		result = prime * result + ((workflowId == null) ? 0 : workflowId.hashCode());
		result = prime * result + ((workflowName == null) ? 0 : workflowName.hashCode());
		result = prime * result + ((workflowRunAttributes == null) ? 0 : workflowRunAttributes.hashCode());
		result = prime * result + ((workflowRunId == null) ? 0 : workflowRunId.hashCode());
		result = prime * result + ((workflowRunInputFileIds == null) ? 0 : workflowRunInputFileIds.hashCode());
		result = prime * result + ((workflowRunName == null) ? 0 : workflowRunName.hashCode());
		result = prime * result + ((workflowRunStatus == null) ? 0 : workflowRunStatus.hashCode());
		result = prime * result + ((workflowVersion == null) ? 0 : workflowVersion.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		AnalysisProvenanceDto other = (AnalysisProvenanceDto) obj;
		if (fileAttributes == null) {
			if (other.fileAttributes != null)
				return false;
		} else if (!fileAttributes.equals(other.fileAttributes))
			return false;
		if (fileDescription == null) {
			if (other.fileDescription != null)
				return false;
		} else if (!fileDescription.equals(other.fileDescription))
			return false;
		if (fileId == null) {
			if (other.fileId != null)
				return false;
		} else if (!fileId.equals(other.fileId))
			return false;
		if (fileMd5sum == null) {
			if (other.fileMd5sum != null)
				return false;
		} else if (!fileMd5sum.equals(other.fileMd5sum))
			return false;
		if (fileMetaType == null) {
			if (other.fileMetaType != null)
				return false;
		} else if (!fileMetaType.equals(other.fileMetaType))
			return false;
		if (filePath == null) {
			if (other.filePath != null)
				return false;
		} else if (!filePath.equals(other.filePath))
			return false;
		if (fileSize == null) {
			if (other.fileSize != null)
				return false;
		} else if (!fileSize.equals(other.fileSize))
			return false;
		if (iusAttributes == null) {
			if (other.iusAttributes != null)
				return false;
		} else if (!iusAttributes.equals(other.iusAttributes))
			return false;
		if (iusLimsKeys == null) {
			if (other.iusLimsKeys != null)
				return false;
		} else if (!iusLimsKeys.equals(other.iusLimsKeys))
			return false;
		if (lastModified == null) {
			if (other.lastModified != null)
				return false;
		} else if (!lastModified.toInstant().equals(other.lastModified == null ? null : other.lastModified.toInstant()))
			return false;
		if (processingAlgorithm == null) {
			if (other.processingAlgorithm != null)
				return false;
		} else if (!processingAlgorithm.equals(other.processingAlgorithm))
			return false;
		if (processingAttributes == null) {
			if (other.processingAttributes != null)
				return false;
		} else if (!processingAttributes.equals(other.processingAttributes))
			return false;
		if (processingId == null) {
			if (other.processingId != null)
				return false;
		} else if (!processingId.equals(other.processingId))
			return false;
		if (processingStatus == null) {
			if (other.processingStatus != null)
				return false;
		} else if (!processingStatus.equals(other.processingStatus))
			return false;
		if (skip == null) {
			if (other.skip != null)
				return false;
		} else if (!skip.equals(other.skip))
			return false;
		if (workflowAttributes == null) {
			if (other.workflowAttributes != null)
				return false;
		} else if (!workflowAttributes.equals(other.workflowAttributes))
			return false;
		if (workflowId == null) {
			if (other.workflowId != null)
				return false;
		} else if (!workflowId.equals(other.workflowId))
			return false;
		if (workflowName == null) {
			if (other.workflowName != null)
				return false;
		} else if (!workflowName.equals(other.workflowName))
			return false;
		if (workflowRunAttributes == null) {
			if (other.workflowRunAttributes != null)
				return false;
		} else if (!workflowRunAttributes.equals(other.workflowRunAttributes))
			return false;
		if (workflowRunId == null) {
			if (other.workflowRunId != null)
				return false;
		} else if (!workflowRunId.equals(other.workflowRunId))
			return false;
		if (workflowRunInputFileIds == null) {
			if (other.workflowRunInputFileIds != null)
				return false;
		} else if (!workflowRunInputFileIds.equals(other.workflowRunInputFileIds))
			return false;
		if (workflowRunName == null) {
			if (other.workflowRunName != null)
				return false;
		} else if (!workflowRunName.equals(other.workflowRunName))
			return false;
		if (workflowRunStatus == null) {
			if (other.workflowRunStatus != null)
				return false;
		} else if (!workflowRunStatus.equals(other.workflowRunStatus))
			return false;
		if (workflowVersion == null) {
			if (other.workflowVersion != null)
				return false;
		} else if (!workflowVersion.equals(other.workflowVersion))
			return false;
		return true;
	}

	public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
