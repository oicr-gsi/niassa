package net.sourceforge.seqware.common.model;

//default package
//Generated 09.12.2011 15:01:20 by Hibernate Tools 3.2.4.GA

import java.util.Date;

/**
 * ShareSample generated by hbm2java
 */
public class ShareSample implements java.io.Serializable {

  private static final long serialVersionUID = 1L;
  private int shareSampleId;
  private Sample sample;
  private Registration registration;
  private Boolean active;
  private Integer swAccession;
  private Date createTstmp;
  private Date updateTstmp;

  public ShareSample() {
  }

  public ShareSample(int shareSampleId, Sample sample, Registration registration, Date createTstmp) {
    this.shareSampleId = shareSampleId;
    this.sample = sample;
    this.registration = registration;
    this.createTstmp = createTstmp;
  }

  public ShareSample(int shareSampleId, Sample sample, Registration registration, Boolean active, Integer swAccession,
      Date createTstmp, Date updateTstmp) {
    this.shareSampleId = shareSampleId;
    this.sample = sample;
    this.registration = registration;
    this.active = active;
    this.swAccession = swAccession;
    this.createTstmp = createTstmp;
    this.updateTstmp = updateTstmp;
  }

  public int getShareSampleId() {
    return this.shareSampleId;
  }

  public void setShareSampleId(int shareSampleId) {
    this.shareSampleId = shareSampleId;
  }

  public Sample getSample() {
    return this.sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  public Registration getRegistration() {
    return this.registration;
  }

  public void setRegistration(Registration registration) {
    this.registration = registration;
  }

  public Boolean getActive() {
    return this.active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Integer getSwAccession() {
    return this.swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public Date getCreateTstmp() {
    return this.createTstmp;
  }

  public void setCreateTstmp(Date createTstmp) {
    this.createTstmp = createTstmp;
  }

  public Date getUpdateTstmp() {
    return this.updateTstmp;
  }

  public void setUpdateTstmp(Date updateTstmp) {
    this.updateTstmp = updateTstmp;
  }

}