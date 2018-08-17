package ch.wisv.events.core.service.registration;

import ch.wisv.events.core.exception.normal.RegistrationInvalidException;
import ch.wisv.events.core.model.registration.Address;
import ch.wisv.events.core.model.registration.Permissions;
import ch.wisv.events.core.model.registration.Profile;
import ch.wisv.events.core.model.registration.Registration;
import ch.wisv.events.core.model.registration.StudyDetails;
import ch.wisv.events.core.repository.registration.AddressRepository;
import ch.wisv.events.core.repository.registration.PermissionsRepository;
import ch.wisv.events.core.repository.registration.ProfileRepository;
import ch.wisv.events.core.repository.registration.RegistrationRepository;
import ch.wisv.events.core.repository.registration.StudyRepository;
import org.springframework.stereotype.Service;

/**
 * RegistrationService class.
 */
@Service
public class RegistrationServiceImpl implements RegistrationService {

    /** RegistrationRepository. */
    private final RegistrationRepository registrationRepository;

    /** RegistrationRepository. */
    private final ProfileRepository profileRepository;

    /** AddressRepository. */
    private final AddressRepository addressRepository;

    /** StudyRepository. */
    private final StudyRepository studyRepository;

    /** PermissionsRepository. */
    private final PermissionsRepository permissionsRepository;

    /**
     * RegistrationServiceImpl constructor.
     *
     * @param registrationRepository of type RegistrationRepository
     * @param profileRepository      of type ProfileRepository
     * @param addressRepository      of type AddressRepository
     * @param studyRepository        of type StudyRepository
     * @param permissionsRepository  of type PermissionsRepository
     */
    public RegistrationServiceImpl(
            RegistrationRepository registrationRepository,
            ProfileRepository profileRepository,
            AddressRepository addressRepository,
            StudyRepository studyRepository,
            PermissionsRepository permissionsRepository
    ) {
        this.registrationRepository = registrationRepository;
        this.profileRepository = profileRepository;
        this.addressRepository = addressRepository;
        this.studyRepository = studyRepository;
        this.permissionsRepository = permissionsRepository;
    }

    /**
     * Add a new registration.
     *
     * @param registration registration model
     *
     * @throws RegistrationInvalidException when Registration object is invalid.
     */
    @Override
    public void create(Registration registration) throws RegistrationInvalidException {
        this.assertIsValidRegistration(registration);

        addressRepository.saveAndFlush(registration.getProfile().getAddress());
        profileRepository.saveAndFlush(registration.getProfile());
        studyRepository.saveAndFlush(registration.getStudyDetails());
        permissionsRepository.saveAndFlush(registration.getPermissions());
        registrationRepository.saveAndFlush(registration);
    }

    /**
     * Assert if Address is valid.
     *
     * @param address of type Address
     *
     * @throws RegistrationInvalidException when Address is invalid.
     */
    private void assertIsValidAddress(Address address) throws RegistrationInvalidException {
        if (address == null) {
            throw new RegistrationInvalidException("Address can not be null!");
        }

        if (address.getStreetName() == null || address.getStreetName().equals("")) {
            throw new RegistrationInvalidException("Street name is empty, but a required field, please fill in this field!");
        }

        if (address.getHouseNumber() == null || address.getHouseNumber().equals("")) {
            throw new RegistrationInvalidException("House number is empty, but a required field, please fill in this field!");
        }

        if (address.getZipCode() == null || address.getZipCode().equals("")) {
            throw new RegistrationInvalidException("Zip code is empty, but a required field, please fill in this field!");
        }

        if (address.getCity() == null || address.getCity().equals("")) {
            throw new RegistrationInvalidException("City is empty, but a required field, please fill in this field!");
        }
    }

    /**
     * Assert is permissions is valid.
     *
     * @param permissions of type Permissions
     *
     * @throws RegistrationInvalidException when Permissions object is invalid.
     */
    private void assertIsValidPermissions(Permissions permissions) throws RegistrationInvalidException {
        if (permissions == null) {
            throw new RegistrationInvalidException("Permissions can not be null!");
        }
    }

    /**
     * Assert if Profile is valid.
     *
     * @param profile of type Profile
     *
     * @throws RegistrationInvalidException when Profile is invalid.
     */
    private void assertIsValidProfile(Profile profile) throws RegistrationInvalidException {
        if (profile == null) {
            throw new RegistrationInvalidException("Profile can not be null!");
        }

        if (profile.getInitials() == null || profile.getInitials().equals("")) {
            throw new RegistrationInvalidException("Initials is empty, but a required field, please fill in this field!");
        }

        if (profile.getFirstName() == null || profile.getFirstName().equals("")) {
            throw new RegistrationInvalidException("First name is empty, but a required field, please fill in this field!");
        }

        if (profile.getSurname() == null || profile.getSurname().equals("")) {
            throw new RegistrationInvalidException("Surname is empty, but a required field, please fill in this field!");
        }

        if (profile.getEmail() == null || profile.getEmail().equals("")) {
            throw new RegistrationInvalidException("E-mail is empty, but a required field, please fill in this field!");
        }

        if (profile.getPhoneNumber() == null || profile.getPhoneNumber().equals("")) {
            throw new RegistrationInvalidException("Phone number is empty, but a required field, please fill in this field!");
        }

        if (profile.getGender() == null) {
            throw new RegistrationInvalidException("Gender is empty, but a required field, please fill in this field!");
        }

        if (profile.getDateOfBirth() == null) {
            throw new RegistrationInvalidException("Date of Birth is empty, but a required field, please fill in this field!");
        }

        this.assertIsValidAddress(profile.getAddress());

        if (profile.getIceContactName() == null || profile.getIceContactName().equals("")) {
            throw new RegistrationInvalidException("ICE contact name is empty, but a required field, please fill in this field!");
        }

        if (profile.getIceContactPhone() == null || profile.getIceContactPhone().equals("")) {
            throw new RegistrationInvalidException("ICE contact phone number is empty, but a required field, please fill in this field!");
        }
    }

    /**
     * Assert is registration is valid.
     *
     * @param registration of type Registration
     *
     * @throws RegistrationInvalidException when Registration object is invalid.
     */
    private void assertIsValidRegistration(Registration registration) throws RegistrationInvalidException {
        if (registration == null) {
            throw new RegistrationInvalidException("Registration can not be null!");
        }

        if (registration.getCreatedAt() == null) {
            throw new RegistrationInvalidException("Registration should contain a created at timestamp.");
        }

        this.assertIsValidProfile(registration.getProfile());

        this.assertIsValidPermissions(registration.getPermissions());

        this.assertIsValidStudy(registration.getStudyDetails());

        if (registration.getDateOfSigning() == null) {
            throw new RegistrationInvalidException("Date of signing is empty, but a required field, please fill in this field!");
        }

        if (!registration.isSigned()) {
            throw new RegistrationInvalidException("Please sign your registration!");
        }
    }

    /**
     * Assert is studyDetails is valid.
     *
     * @param studyDetails of type StudyDetails
     *
     * @throws RegistrationInvalidException when StudyDetails object is invalid.
     */
    private void assertIsValidStudy(StudyDetails studyDetails) throws RegistrationInvalidException {
        if (studyDetails == null) {
            throw new RegistrationInvalidException("StudyDetails can not be null!");
        }

        if (studyDetails.getStudy() == null || studyDetails.getStudy().equals("")) {
            throw new RegistrationInvalidException("I am studying is empty, but a required field, please fill in this field!");
        }

        if (studyDetails.getFirstStudyYear() == 0) {
            throw new RegistrationInvalidException("First year of studyDetails is empty, but a required field, please fill in this field!");
        }

        if (studyDetails.getStudentNumber() == null || studyDetails.getStudentNumber().equals("")) {
            throw new RegistrationInvalidException("Student number is empty, but a required field, please fill in this field!");
        }

        if (studyDetails.getNetId() == null || studyDetails.getNetId().equals("")) {
            throw new RegistrationInvalidException("NetID is empty, but a required field, please fill in this field!");
        }
    }
}
