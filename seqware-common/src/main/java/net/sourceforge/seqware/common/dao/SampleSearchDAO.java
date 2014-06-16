package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.SampleSearch;

/**
 * <p>
 * SampleSearchDAO interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface SampleSearchDAO {

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public List<SampleSearch> list();

    /**
     * <p>
     * findById.
     * </p>
     * 
     * @param id
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.SampleSearch} object.
     */
    public SampleSearch findById(Integer id);

    /**
     * <p>
     * create.
     * </p>
     * 
     * @param sampleSearch
     *            a {@link net.sourceforge.seqware.common.model.SampleSearch} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer create(SampleSearch sampleSearch);

}
