package net.sourceforge.seqware.common.model;

import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * <p>
 * Workflow class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Workflow extends PermissionsAware
		implements Serializable, Comparable<Workflow>, Annotatable<WorkflowAttribute>, FirstTierModel {
	/**
	 * LEFT OFF WITH: this needs to be finished
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer workflowId;
	private Integer swAccession;
	private String name;
	private String description;
	private String inputAlgorithm;
	private String version;
	private String seqwareVersion;
	private String baseIniFile;
	private String cwd;
	private String command;
	private String template;
	/**
	 * @deprecated this does not seem to be in use and probably should not be
	 */
	@Deprecated
	private String host;
	private String username;
	private String permanentBundleLocation;
	private String workflowClass;
	private String workflowType;
	private String workflowEngine;

	private Date createTimestamp;
	private Date updateTimestamp;

	private boolean isPrivate;
	private boolean isPublic;

	private Registration owner;

	private SortedSet<WorkflowRun> workflowRuns;
	private Set<WorkflowAttribute> workflowAttributes = new TreeSet<>();
	private Map<String, String> parameterDefaults = new TreeMap<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(Workflow.class);
	private SortedSet<WorkflowParam> workflowParams;

	/**
	 * <p>
	 * Constructor for Workflow.
	 * </p>
	 */
	public Workflow() {
		super();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param that
	 */
	@Override
	public int compareTo(Workflow that) {
		if (that == null)
			return -1;

		if (Objects.equals(that.getSwAccession(), this.getSwAccession())) // when both names are
			// null
			return 0;

		if (that.getSwAccession() == null)
			return -1; // when only the other name is null

		return (that.getSwAccession().compareTo(this.getSwAccession()));
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("swAccession", getSwAccession()).toString();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param other
	 */
	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if (!(other instanceof Workflow))
			return false;
		Workflow castOther = (Workflow) other;
		return new EqualsBuilder().append(this.getSwAccession(), castOther.getSwAccession()).isEquals();
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getSwAccession()).toHashCode();
	}

	/**
	 * <p>
	 * Getter for the field <code>cwd</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCwd() {
		return cwd;
	}

	/**
	 * <p>
	 * Setter for the field <code>cwd</code>.
	 * </p>
	 *
	 * @param cwd
	 *            a {@link java.lang.String} object.
	 */
	public void setCwd(String cwd) {
		this.cwd = cwd;
	}

	/**
	 * <p>
	 * Getter for the field <code>command</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * <p>
	 * Setter for the field <code>command</code>.
	 * </p>
	 *
	 * @param command
	 *            a {@link java.lang.String} object.
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * <p>
	 * Getter for the field <code>template</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * <p>
	 * Setter for the field <code>template</code>.
	 * </p>
	 *
	 * @param template
	 *            a {@link java.lang.String} object.
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * <p>
	 * Getter for the field <code>workflowId</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getWorkflowId() {
		return workflowId;
	}

	/**
	 * <p>
	 * Setter for the field <code>workflowId</code>.
	 * </p>
	 *
	 * @param workflowId
	 *            a {@link java.lang.Integer} object.
	 */
	public void setWorkflowId(Integer workflowId) {
		this.workflowId = workflowId;
	}

	/**
	 * <p>
	 * Getter for the field <code>swAccession</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	@Override
	public Integer getSwAccession() {
		return swAccession;
	}

	/**
	 * <p>
	 * Setter for the field <code>swAccession</code>.
	 * </p>
	 *
	 * @param swAccession
	 *            a {@link java.lang.Integer} object.
	 */
	public void setSwAccession(Integer swAccession) {
		this.swAccession = swAccession;
	}

	/**
	 * <p>
	 * Getter for the field <code>name</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>
	 * getJsonEscapeName.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getJsonEscapeName() {
		return JsonUtil.forJSON(name);
	}

	/**
	 * <p>
	 * Setter for the field <code>name</code>.
	 * </p>
	 *
	 * @param name
	 *            a {@link java.lang.String} object.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * <p>
	 * getJsonEscapeDescription.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getJsonEscapeDescription() {
		return JsonUtil.forJSON(description);
	}

	/**
	 * <p>
	 * Getter for the field <code>description</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <p>
	 * Setter for the field <code>description</code>.
	 * </p>
	 *
	 * @param description
	 *            a {@link java.lang.String} object.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * <p>
	 * Getter for the field <code>inputAlgorithm</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getInputAlgorithm() {
		return inputAlgorithm;
	}

	/**
	 * <p>
	 * Setter for the field <code>inputAlgorithm</code>.
	 * </p>
	 *
	 * @param inputAlgorithm
	 *            a {@link java.lang.String} object.
	 */
	public void setInputAlgorithm(String inputAlgorithm) {
		this.inputAlgorithm = inputAlgorithm;
	}

	/**
	 * <p>
	 * Getter for the field <code>version</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * <p>
	 * Setter for the field <code>version</code>.
	 * </p>
	 *
	 * @param version
	 *            a {@link java.lang.String} object.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * <p>
	 * Getter for the field <code>seqwareVersion</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSeqwareVersion() {
		return seqwareVersion;
	}

	/**
	 * <p>
	 * Setter for the field <code>seqwareVersion</code>.
	 * </p>
	 *
	 * @param seqwareVersion
	 *            a {@link java.lang.String} object.
	 */
	public void setSeqwareVersion(String seqwareVersion) {
		this.seqwareVersion = seqwareVersion;
	}

	/**
	 * <p>
	 * Getter for the field <code>baseIniFile</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getBaseIniFile() {
		return baseIniFile;
	}

	/**
	 * <p>
	 * Setter for the field <code>baseIniFile</code>.
	 * </p>
	 *
	 * @param baseIniFile
	 *            a {@link java.lang.String} object.
	 */
	public void setBaseIniFile(String baseIniFile) {
		this.baseIniFile = baseIniFile;
	}

	/**
	 * <p>
	 * Getter for the field <code>host</code>.
	 * </p>
	 *
	 * @deprecated
	 * @return a {@link java.lang.String} object.
	 */
	@Deprecated
	public String getHost() {
		return host;
	}

	/**
	 * <p>
	 * Setter for the field <code>host</code>.
	 * </p>
	 *
	 * @deprecated
	 * @param host
	 *            a {@link java.lang.String} object.
	 */
	@Deprecated
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * <p>
	 * Getter for the field <code>username</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * <p>
	 * Setter for the field <code>username</code>.
	 * </p>
	 *
	 * @param username
	 *            a {@link java.lang.String} object.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * <p>
	 * Getter for the field <code>createTimestamp</code>.
	 * </p>
	 *
	 * @return a {@link java.util.Date} object.
	 */
	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	/**
	 * <p>
	 * Setter for the field <code>createTimestamp</code>.
	 * </p>
	 *
	 * @param createTimestamp
	 *            a {@link java.util.Date} object.
	 */
	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	/**
	 * <p>
	 * Getter for the field <code>updateTimestamp</code>.
	 * </p>
	 *
	 * @return a {@link java.util.Date} object.
	 */
	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}

	/**
	 * <p>
	 * Setter for the field <code>updateTimestamp</code>.
	 * </p>
	 *
	 * @param updateTimestamp
	 *            a {@link java.util.Date} object.
	 */
	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	/**
	 * <p>
	 * isPrivate.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public boolean isPrivate() {
		return isPrivate;
	}

	/**
	 * <p>
	 * getPrivate.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public boolean getPrivate() {
		return isPrivate;
	}

	/**
	 * <p>
	 * setPrivate.
	 * </p>
	 *
	 * @param isPrivate
	 *            a boolean.
	 */
	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	/**
	 * <p>
	 * isPublic.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public boolean isPublic() {
		return isPublic;
	}

	/**
	 * <p>
	 * getPublic.
	 * </p>
	 *
	 * @return a boolean.
	 */
	public boolean getPublic() {
		return isPublic;
	}

	/**
	 * <p>
	 * setPublic.
	 * </p>
	 *
	 * @param isPublic
	 *            a boolean.
	 */
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	/**
	 * <p>
	 * Getter for the field <code>owner</code>.
	 * </p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
	 */
	public Registration getOwner() {
		return owner;
	}

	/**
	 * <p>
	 * Setter for the field <code>owner</code>.
	 * </p>
	 *
	 * @param owner
	 *            a {@link net.sourceforge.seqware.common.model.Registration}
	 *            object.
	 */
	public void setOwner(Registration owner) {
		this.owner = owner;
	}

	/**
	 * <p>
	 * Getter for the field <code>workflowRuns</code>.
	 * </p>
	 *
	 * @return a {@link java.util.SortedSet} object.
	 */
	public SortedSet<WorkflowRun> getWorkflowRuns() {
		return workflowRuns;
	}

	/**
	 * <p>
	 * Setter for the field <code>workflowRuns</code>.
	 * </p>
	 *
	 * @param workflowRuns
	 *            a {@link java.util.SortedSet} object.
	 */
	public void setWorkflowRuns(SortedSet<WorkflowRun> workflowRuns) {
		this.workflowRuns = workflowRuns;
	}

	/**
	 * <p>
	 * getFullName.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFullName() {
		String fullName = "";
		if (name != null) {
			fullName = name;
			if (version != null) {
				fullName = fullName + " " + version;
			}
		}
		return fullName;
	}

	/**
	 * <p>
	 * getJsonEscapeFullName.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getJsonEscapeFullName() {
		return JsonUtil.forJSON(getFullName());
	}

	/**
	 * <p>
	 * Getter for the field <code>permanentBundleLocation</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getPermanentBundleLocation() {
		return permanentBundleLocation;
	}

	public String getWorkflowClass() {
		return workflowClass;
	}

	public void setWorkflowClass(String workflowClass) {
		this.workflowClass = workflowClass;
	}

	public String getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}

	public String getWorkflowEngine() {
		return workflowEngine;
	}

	public void setWorkflowEngine(String workflowEngine) {
		this.workflowEngine = workflowEngine;
	}

	/**
	 * <p>
	 * Setter for the field <code>permanentBundleLocation</code>.
	 * </p>
	 *
	 * @param permanentBundleLocation
	 *            a {@link java.lang.String} object.
	 */
	public void setPermanentBundleLocation(String permanentBundleLocation) {
		this.permanentBundleLocation = permanentBundleLocation;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return
	 */
	@Override
	public boolean givesPermissionInternal(Registration registration, Set<Integer> considered) {
		boolean hasPermission = true;
		if (registration == null) {
			hasPermission = false;
		}

		// else if (registration.equals(owner) || registration.isLIMSAdmin())
		// {
		// hasPermission = true;
		// }
		// else
		// {
		// hasPermission = false;
		// }
		if (!hasPermission) {
			LOGGER.info("Workflow does not give permission");
			throw new SecurityException("User " + registration.getEmailAddress()
					+ " does not have permission to modify aspects of workflow " + this.getName());
		} else {
			LOGGER.info("Workflows are public by default");
		}
		return hasPermission;
	}

	/**
	 * <p>
	 * Getter for the field <code>workflowAttributes</code>.
	 * </p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<WorkflowAttribute> getWorkflowAttributes() {
		return workflowAttributes;
	}

	/**
	 * <p>
	 * Setter for the field <code>workflowAttributes</code>.
	 * </p>
	 *
	 * @param workflowAttributes
	 *            a {@link java.util.Set} object.
	 */
	public void setWorkflowAttributes(Set<WorkflowAttribute> workflowAttributes) {
		this.workflowAttributes = workflowAttributes;
	}

	@Override
	public Set<WorkflowAttribute> getAnnotations() {
		return this.getWorkflowAttributes();
	}

	public Map<String, String> getParameterDefaults() {
		return parameterDefaults;
	}

	public void setParameterDefaults(Map<String, String> parameterDefaults) {
		this.parameterDefaults = parameterDefaults;
	}

	public SortedSet<WorkflowParam> getWorkflowParams() {
		return workflowParams;
	}

	public void setWorkflowParams(SortedSet<WorkflowParam> workflowParams) {
		this.workflowParams = workflowParams;
	}

}
