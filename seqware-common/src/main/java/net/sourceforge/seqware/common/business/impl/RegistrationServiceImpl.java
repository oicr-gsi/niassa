package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.dao.RegistrationDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.RegistrationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * RegistrationServiceImpl class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
@Transactional(rollbackFor=Exception.class)
public class RegistrationServiceImpl implements RegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    private RegistrationDAO registrationDAO = null;

    /**
     * <p>
     * Constructor for RegistrationServiceImpl.
     * </p>
     */
    public RegistrationServiceImpl() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * Sets a private member variable with an instance of an implementation of RegistrationDAO. This method is called by the Spring
     * framework at run time.
     *
     * @see RegistrationDAO
     */
    @Override
    public void setRegistrationDAO(RegistrationDAO registrationDAO) {
        LOGGER.error("RegistrationServiceImpl.setRegistrationDAO: SETTING REGDAO HERE: " + registrationDAO + " " + this);
        this.registrationDAO = registrationDAO;
    }

    /* Inserts an instance of Registration into the database. */
    /**
     * {@inheritDoc}
     *
     * @param registrationDTO
     */
    @Override
    public void insert(RegistrationDTO registrationDTO) {
        Registration registration = this.populateRegistration(registrationDTO);
        registration.setCreateTimestamp(new Date());
        registrationDAO.insert(registration);
    }

    /* Updates an instance of Registration in the database. */
    /**
     * {@inheritDoc}
     *
     * @param registrationDTO
     */
    @Override
    public void update(RegistrationDTO registrationDTO) {
        Registration registration = this.populateRegistration(registrationDTO);
        registrationDAO.update(registration);
    }

    private String replace(String str, String pattern, String replace) {
        int start = 0;
        int end = 0;
        if (replace == null) replace = "";
        StringBuilder result = new StringBuilder();
        while ((end = str.indexOf(pattern, start)) >= 0) {
            result.append(str.substring(start, end));
            result.append(replace);
            start = end + pattern.length();
        }
        result.append(str.substring(start));
        str = result.toString();
        return str;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of Registration in the database by the Registration emailAddress, and copies the Registration properties to an
     * instance of RegistrationDTO.
     */
    @Override
    public RegistrationDTO findByEmailAddress(String emailAddress) {

        RegistrationDTO registrationDTO = null;
        if (emailAddress != null) {
            if (registrationDAO == null) {
                LOGGER.error("RegistrationServiceImpl.findByEmailAddress regDAW IS NULL! " + registrationDAO + " " + this);
                return null;
            }
            try {
                Registration registration = registrationDAO.findByEmailAddress(emailAddress.trim().toLowerCase());
                if (registration != null) {
                    registrationDTO = this.populateRegistrationDTO(registration);
                }
            } catch (Exception exception) {
                LOGGER.error("RegistrationServiceImpl.findByEmailAddress: EXCEPTION: " + exception.getMessage());
                LOGGER.debug("RegistrationServiceImpl.findByEmailAddress: Cannot find Registration by email address " + emailAddress);
            }
        }
        return registrationDTO;
    }

    /**
     * {@inheritDoc}
     *
     * Finds an instance of Registration in the database by the Registration emailAddress and password, and copies the Registration
     * properties to an instance of RegistrationDTO.
     */
    @Override
    public RegistrationDTO findByEmailAddressAndPassword(String emailAddress, String password) {
        RegistrationDTO registrationDTO = null;
        if (emailAddress != null && password != null) {
            try {
                Registration registration = registrationDAO.findByEmailAddressAndPassword(emailAddress.trim().toLowerCase(), password);
                if (registration != null) {
                    registrationDTO = this.populateRegistrationDTO(registration);
                }
            } catch (Exception exception) {
                LOGGER.debug("RegistrationServiceImpl.findByEmailAddressAndPassword: Cannot find Registration by email address " + emailAddress + " and password " + password);
            }
        }
        return registrationDTO;
    }

    /**
     * {@inheritDoc}
     *
     * Determines if an email address has already been used.
     */
    @Override
    public boolean hasEmailAddressBeenUsed(String email) {
        /**
         * We do not want to check if an email address has been used if the user is updating an existing registration and has not changed
         * the emailAddress.
         */
        return (findByEmailAddress(email.trim().toLowerCase()) != null);
    }

    /**
     * Copies the properties of an instance of RegistrationDTO to an instance of Registration.
     */
    private Registration populateRegistration(RegistrationDTO registrationDTO) {
        Registration registration = registrationDTO.getDomainObject();
        if (registration == null) {
            registration = new Registration();
            registrationDTO.setDomainObject(registration);
        }
        try {
            registrationDTO.setEmailAddress(registrationDTO.getEmailAddress().trim().toLowerCase());
            registrationDTO.setConfirmEmailAddress(registrationDTO.getConfirmEmailAddress().trim().toLowerCase());
            registrationDTO.setCreateTimestamp(registration.getCreateTimestamp());
            registrationDTO.setUpdateTimestamp(registration.getUpdateTimestamp());
            registrationDTO.setRegistrationId(registration.getRegistrationId());
            BeanUtils.copyProperties(registrationDTO, registration);
        } catch (Exception exception) {
            LOGGER.error("RegistrationServiceImpl.populateRegistration: Error copying RegistrationDTO to Registration.");
        }
        return registration;
    }

    /**
     * Copies the properties of an instance of Registration to an instance of RegistrationDTO.
     */
    private RegistrationDTO populateRegistrationDTO(Registration registration) {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        try {
            BeanUtils.copyProperties(registration, registrationDTO);
            registrationDTO.setConfirmEmailAddress(registration.getEmailAddress());
            registrationDTO.setConfirmPassword(registration.getPassword());
            registrationDTO.setDomainObject(registration);
        } catch (Exception exception) {
            LOGGER.error("RegistrationServiceImpl.populateRegistrationDTO: EXCEPTION2: " + exception.getMessage());
            LOGGER.error("RegistrationServiceImpl.populateRegistrationDTO: Error copying Registration to RegistrationDTO.");
        }
        return registrationDTO;
    }

    /** {@inheritDoc} */
    @Override
    public Registration updateDetached(Registration registration) {
        return registrationDAO.updateDetached(registration);
    }
}

// ex:sw=4:ts=4:
