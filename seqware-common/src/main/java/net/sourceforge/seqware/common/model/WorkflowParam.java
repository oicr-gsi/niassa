package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import net.sourceforge.seqware.common.util.jsontools.JsonUtil;

@Deprecated
public class WorkflowParam implements Serializable, Comparable<WorkflowParam> {

    private static final long serialVersionUID = 1L;
    private Integer workflowParamId;
    private Workflow workflow;
    private String type;
    private String key;
    private String defaultValue;
    private Boolean display;
    private String fileMetaType;
    private String displayName;
    private String value;
    private String displayValue;
    // none persistents(for Summary Launch Workflow Page)
    private Sample sample;
    private List<File> files;

    /**
     * <p>
     * Constructor for WorkflowParam.
     * </p>
     */
    public WorkflowParam() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @param that
     */
    @Override
    public int compareTo(WorkflowParam that) {
        if (that == null) {
            return -1;
        }

        if (Objects.equals(that.getWorkflowParamId(), this.getWorkflowParamId())) // when both names are null
        {
            return 0;
        }

        if (that.getWorkflowParamId() == null) {
            return -1; // when only the other name is null
        }
        return (that.getWorkflowParamId().compareTo(this.getWorkflowParamId()));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("swAccession", getWorkflowParamId()).toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @param other
     */
    @Override
    public boolean equals(Object other) {
        if ((this == other)) {
            return true;
        }
        if (!(other instanceof WorkflowParam)) {
            return false;
        }
        WorkflowParam castOther = (WorkflowParam) other;
        return new EqualsBuilder().append(this.getWorkflowParamId(), castOther.getWorkflowParamId()).isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getWorkflowParamId()).toHashCode();
    }

    /**
     * <p>
     * Getter for the field <code>workflowParamId</code>.
     * </p>
     * 
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getWorkflowParamId() {
        return workflowParamId;
    }

    /**
     * <p>
     * Setter for the field <code>workflowParamId</code>.
     * </p>
     * 
     * @param workflowParamId
     *            a {@link java.lang.Integer} object.
     */
    public void setWorkflowParamId(Integer workflowParamId) {
        this.workflowParamId = workflowParamId;
    }

    /**
     * <p>
     * Getter for the field <code>workflow</code>.
     * </p>
     * 
     * @return a {@link net.sourceforge.seqware.common.model.Workflow} object.
     */
    public Workflow getWorkflow() {
        return workflow;
    }

    /**
     * <p>
     * Setter for the field <code>workflow</code>.
     * </p>
     * 
     * @param workflow
     *            a {@link net.sourceforge.seqware.common.model.Workflow} object.
     */
    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    /**
     * <p>
     * Getter for the field <code>type</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getType() {
        return type;
    }

    /**
     * <p>
     * getJsonEscapeType.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeType() {
        return JsonUtil.forJSON(type);
    }

    /**
     * <p>
     * Setter for the field <code>type</code>.
     * </p>
     * 
     * @param type
     *            a {@link java.lang.String} object.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <p>
     * Getter for the field <code>key</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getKey() {
        return key;
    }

    /**
     * <p>
     * getJsonEscapeKey.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeKey() {
        return JsonUtil.forJSON(key);
    }

    /**
     * <p>
     * Setter for the field <code>key</code>.
     * </p>
     * 
     * @param key
     *            a {@link java.lang.String} object.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * <p>
     * getJsonEscapeDefaultValue.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeDefaultValue() {
        return JsonUtil.forJSON(defaultValue);
    }

    /**
     * <p>
     * Getter for the field <code>defaultValue</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * <p>
     * Setter for the field <code>defaultValue</code>.
     * </p>
     * 
     * @param defaultValue
     *            a {@link java.lang.String} object.
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * <p>
     * isDisplay.
     * </p>
     * 
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean isDisplay() {
        return display;
    }

    /**
     * <p>
     * Getter for the field <code>display</code>.
     * </p>
     * 
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getDisplay() {
        return display;
    }

    /**
     * <p>
     * Setter for the field <code>display</code>.
     * </p>
     * 
     * @param display
     *            a {@link java.lang.Boolean} object.
     */
    public void setDisplay(Boolean display) {
        this.display = display;
    }

    /**
     * <p>
     * Getter for the field <code>fileMetaType</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getFileMetaType() {
        return fileMetaType;
    }

    /**
     * <p>
     * Setter for the field <code>fileMetaType</code>.
     * </p>
     * 
     * @param fileMetaType
     *            a {@link java.lang.String} object.
     */
    public void setFileMetaType(String fileMetaType) {
        this.fileMetaType = fileMetaType;
    }

    /**
     * <p>
     * getJsonEscapeDisplayName.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeDisplayName() {
        return JsonUtil.forJSON(displayName);
    }

    /**
     * <p>
     * Getter for the field <code>displayName</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * <p>
     * Setter for the field <code>displayName</code>.
     * </p>
     * 
     * @param displayName
     *            a {@link java.lang.String} object.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * <p>
     * getJsonEscapeValue.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeValue() {
        return JsonUtil.forJSON(value);
    }

    /**
     * <p>
     * Getter for the field <code>value</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getValue() {
        return value;
    }

    /**
     * <p>
     * Setter for the field <code>value</code>.
     * </p>
     * 
     * @param value
     *            a {@link java.lang.String} object.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * <p>
     * Getter for the field <code>displayValue</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     * <p>
     * Setter for the field <code>displayValue</code>.
     * </p>
     * 
     * @param displayValue
     *            a {@link java.lang.String} object.
     */
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    /**
     * <p>
     * Getter for the field <code>sample</code>.
     * </p>
     * 
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * <p>
     * Setter for the field <code>sample</code>.
     * </p>
     * 
     * @param sample
     *            a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

    /**
     * <p>
     * Getter for the field <code>files</code>.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * <p>
     * Setter for the field <code>files</code>.
     * </p>
     * 
     * @param files
     *            a {@link java.util.List} object.
     */
    public void setFiles(List<File> files) {
        this.files = files;
    }


}
