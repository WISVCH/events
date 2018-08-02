package ch.wisv.events.core.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.RegistrationInvalidException;
import ch.wisv.events.core.model.registration.Address;
import ch.wisv.events.core.model.registration.Gender;
import ch.wisv.events.core.model.registration.Permissions;
import ch.wisv.events.core.model.registration.Profile;
import ch.wisv.events.core.model.registration.Registration;
import ch.wisv.events.core.model.registration.Study;
import ch.wisv.events.core.model.registration.StudyDetails;
import ch.wisv.events.core.repository.registration.AddressRepository;
import ch.wisv.events.core.repository.registration.PermissionsRepository;
import ch.wisv.events.core.repository.registration.ProfileRepository;
import ch.wisv.events.core.repository.registration.RegistrationRepository;
import ch.wisv.events.core.repository.registration.StudyRepository;
import ch.wisv.events.core.service.registration.RegistrationService;
import ch.wisv.events.core.service.registration.RegistrationServiceImpl;
import java.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RegistrationService test.
 */
public class RegistrationServiceTest extends ServiceTest {

    private RegistrationService registrationService;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private PermissionsRepository permissionsRepository;

    private Registration registration;

    @Before
    public void setUp() {
        registrationService = new RegistrationServiceImpl(
                registrationRepository,
                profileRepository,
                addressRepository,
                studyRepository,
                permissionsRepository
        );

        registration = new Registration();

        registration.setDateOfSigning(LocalDate.now());
        registration.setSigned(true);

        registration.getProfile().setInitials("J");
        registration.getProfile().setFirstName("John");
        registration.getProfile().setSurname("Travolta");
        registration.getProfile().setEmail("john@travol.ta");
        registration.getProfile().setPhoneNumber("+31612345678");
        registration.getProfile().setGender(Gender.OTHER);
        registration.getProfile().setDateOfBirth(LocalDate.now());
        registration.getProfile().getAddress().setStreetName("Hollywood");
        registration.getProfile().getAddress().setHouseNumber("3");
        registration.getProfile().getAddress().setZipCode("1234AB");
        registration.getProfile().getAddress().setCity("Amsterdam");
        registration.getProfile().setIceContactName("Carly Simon");
        registration.getProfile().setIceContactPhone("+31687654321");

        registration.getStudyDetails().setStudy(Study.BS_CSE);
        registration.getStudyDetails().setFirstStudyYear(2018);
        registration.getStudyDetails().setStudentNumber("1234567");
        registration.getStudyDetails().setNetId("jtravolta");
    }

    @After
    public void tearDown() {
        registrationService = null;
    }

    @Test
    public void testCreateNull() throws Exception {
        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Registration can not be null!");

        registrationService.create(null);
    }

    @Test
    public void testCreateCreatedAtNull() throws Exception {
        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Registration should contain a created at timestamp.");

        registration.setCreatedAt(null);

        registrationService.create(registration);
    }

    @Test
    public void testCreateProfileNull() throws Exception {
        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Profile can not be null!");

        registration.setProfile(null);

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidInitials() throws Exception {
        registration.getProfile().setInitials(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Initials is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidFirstName() throws Exception {
        registration.getProfile().setFirstName(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("First name is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidSurname() throws Exception {
        registration.getProfile().setSurname(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Surname is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidEmail() throws Exception {
        registration.getProfile().setEmail(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("E-mail is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidPhoneNumber() throws Exception {
        registration.getProfile().setPhoneNumber(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Phone number is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidGender() throws Exception {
        registration.getProfile().setGender(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Gender is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidDateOfBirth() throws Exception {
        registration.getProfile().setDateOfBirth(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Date of Birth is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidAddress() throws Exception {
        registration.getProfile().setAddress(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Address can not be null!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidAddressStreetName() throws Exception {
        registration.getProfile().getAddress().setStreetName(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Street name is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidAddressHouseNumber() throws Exception {
        registration.getProfile().getAddress().setHouseNumber(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("House number is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidAddressZipCode() throws Exception {
        registration.getProfile().getAddress().setZipCode(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Zip code is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidAddressCity() throws Exception {
        registration.getProfile().getAddress().setCity(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("City is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidIceContactName() throws Exception {
        registration.getProfile().setIceContactName(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("ICE contact name is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidIceContactPhone() throws Exception {
        registration.getProfile().setIceContactPhone(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("ICE contact phone number is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidPermissions() throws Exception {
        registration.setPermissions(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Permissions can not be null!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidStudy() throws Exception {
        registration.setStudyDetails(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("StudyDetails can not be null!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidStudyName() throws Exception {
        registration.getStudyDetails().setStudy(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("I am studying is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidStudyFirstYear() throws Exception {
        registration.getStudyDetails().setFirstStudyYear(0);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("First year of studyDetails is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidStudyStudentNumber() throws Exception {
        registration.getStudyDetails().setStudentNumber(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Student number is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidStudyNetId() throws Exception {
        registration.getStudyDetails().setNetId(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("NetID is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidDateOfSigning() throws Exception {
        registration.setDateOfSigning(null);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Date of signing is empty, but a required field, please fill in this field!");

        registrationService.create(registration);
    }

    @Test
    public void testCreateInvalidSigned() throws Exception {
        registration.setSigned(false);

        thrown.expect(RegistrationInvalidException.class);
        thrown.expectMessage("Please sign your registration!");

        registrationService.create(registration);
    }

    @Test
    public void testCreate() throws Exception {
        when(addressRepository.saveAndFlush(any(Address.class))).thenReturn(new Address());
        when(profileRepository.saveAndFlush(any(Profile.class))).thenReturn(new Profile());
        when(studyRepository.saveAndFlush(any(StudyDetails.class))).thenReturn(new StudyDetails());
        when(permissionsRepository.saveAndFlush(any(Permissions.class))).thenReturn(new Permissions());
        when(registrationRepository.saveAndFlush(any(Registration.class))).thenReturn(new Registration());

        registrationService.create(registration);

        verify(addressRepository, times(1)).saveAndFlush(any(Address.class));
        verify(profileRepository, times(1)).saveAndFlush(any(Profile.class));
        verify(studyRepository, times(1)).saveAndFlush(any(StudyDetails.class));
        verify(permissionsRepository, times(1)).saveAndFlush(any(Permissions.class));
        verify(registrationRepository, times(1)).saveAndFlush(any(Registration.class));
    }
}