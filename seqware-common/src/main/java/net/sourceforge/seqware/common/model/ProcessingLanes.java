package net.sourceforge.seqware.common.model;

/**
 * ProcessingLanes generated by hbm2java
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingLanes implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    private int processingLanesId;
    private Processing processing;
    private Lane lane;
    private String description;
    private String label;
    private String url;
    private Integer swAccession;

    /**
     * <p>
     * Constructor for ProcessingLanes.
     * </p>
     */
    public ProcessingLanes() {
    }

    /**
     * <p>
     * Constructor for ProcessingLanes.
     * </p>
     * 
     * @param processingLanesId
     *            a int.
     * @param processing
     *            a {@link net.sourceforge.seqware.common.model.Processing} object.
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public ProcessingLanes(int processingLanesId, Processing processing, Lane lane) {
        this.processingLanesId = processingLanesId;
        this.processing = processing;
        this.lane = lane;
    }

    /**
     * <p>
     * Constructor for ProcessingLanes.
     * </p>
     * 
     * @param processingLanesId
     *            a int.
     * @param processing
     *            a {@link net.sourceforge.seqware.common.model.Processing} object.
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     * @param description
     *            a {@link java.lang.String} object.
     * @param label
     *            a {@link java.lang.String} object.
     * @param url
     *            a {@link java.lang.String} object.
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     */
    public ProcessingLanes(int processingLanesId, Processing processing, Lane lane, String description, String label, String url,
            Integer swAccession) {
        this.processingLanesId = processingLanesId;
        this.processing = processing;
        this.lane = lane;
        this.description = description;
        this.label = label;
        this.url = url;
        this.swAccession = swAccession;
    }

    /**
     * <p>
     * Getter for the field <code>processingLanesId</code>.
     * </p>
     * 
     * @return a int.
     */
    public int getProcessingLanesId() {
        return this.processingLanesId;
    }

    /**
     * <p>
     * Setter for the field <code>processingLanesId</code>.
     * </p>
     * 
     * @param processingLanesId
     *            a int.
     */
    public void setProcessingLanesId(int processingLanesId) {
        this.processingLanesId = processingLanesId;
    }

    /**
     * <p>
     * Getter for the field <code>processing</code>.
     * </p>
     * 
     * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
     */
    public Processing getProcessing() {
        return this.processing;
    }

    /**
     * <p>
     * Setter for the field <code>processing</code>.
     * </p>
     * 
     * @param processing
     *            a {@link net.sourceforge.seqware.common.model.Processing} object.
     */
    public void setProcessing(Processing processing) {
        this.processing = processing;
    }

    /**
     * <p>
     * Getter for the field <code>lane</code>.
     * </p>
     * 
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Lane getLane() {
        return this.lane;
    }

    /**
     * <p>
     * Setter for the field <code>lane</code>.
     * </p>
     * 
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public void setLane(Lane lane) {
        this.lane = lane;
    }

    /**
     * <p>
     * Getter for the field <code>description</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return this.description;
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
     * Getter for the field <code>label</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * <p>
     * Setter for the field <code>label</code>.
     * </p>
     * 
     * @param label
     *            a {@link java.lang.String} object.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * <p>
     * Getter for the field <code>url</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * <p>
     * Setter for the field <code>url</code>.
     * </p>
     * 
     * @param url
     *            a {@link java.lang.String} object.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * <p>
     * Getter for the field <code>swAccession</code>.
     * </p>
     * 
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getSwAccession() {
        return this.swAccession;
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

}
