package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ShareStudyDAO;
import net.sourceforge.seqware.common.model.ShareStudy;

/**
 * <p>
 * ShareStudyService interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareStudyService {
    /** Constant <code>NAME="shareStudyService"</code> */
    String NAME = "shareStudyService";

    /**
     * <p>
     * setShareStudyDAO.
     * </p>
     * 
     * @param shareStudyDAO
     *            a {@link net.sourceforge.seqware.common.dao.ShareStudyDAO} object.
     */
    void setShareStudyDAO(ShareStudyDAO shareStudyDAO);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param shareStudy
     *            a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
     */
    void insert(ShareStudy shareStudy);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param shareStudy
     *            a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
     */
    void update(ShareStudy shareStudy);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param shareStudy
     *            a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
     */
    void delete(ShareStudy shareStudy);

    /**
     * <p>
     * findByID.
     * </p>
     * 
     * @param id
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
     */
    ShareStudy findByID(Integer id);

    /**
     * <p>
     * findByStudyIdAndRegistrationId.
     * </p>
     * 
     * @param studyId
     *            a {@link java.lang.Integer} object.
     * @param registrationId
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
     */
    ShareStudy findByStudyIdAndRegistrationId(Integer studyId, Integer registrationId);

    /**
     * <p>
     * findBySWAccession.
     * </p>
     * 
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
     */
    ShareStudy findBySWAccession(Integer swAccession);

    /**
     * <p>
     * isExistsShare.
     * </p>
     * 
     * @param studyId
     *            a {@link java.lang.Integer} object.
     * @param registrationId
     *            a {@link java.lang.Integer} object.
     * @return a boolean.
     */
    boolean isExistsShare(Integer studyId, Integer registrationId);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param shareStudy
     *            a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
     * @return a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
     */
    ShareStudy updateDetached(ShareStudy shareStudy);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<ShareStudy> list();

}
