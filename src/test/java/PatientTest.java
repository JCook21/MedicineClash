import org.junit.*;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.empty;

public class PatientTest {

    private Patient patient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void testSetup()
    {
        patient = new Patient();
    }

    @Test
    public void testClashNeverReturnsNull() {
        Assert.assertNotNull(patient.clash(Arrays.asList("Aspirin")));
    }

    @Test
    public void testClashWithTwoMedicines() {
        Medicine tylenolMedicine = new Medicine("Tylenol");
        Medicine aspirinMedicine = new Medicine("Aspirin");
        Prescription tylenolPrescription = new Prescription(LocalDate.now(), 5);

        aspirinMedicine.addPrescription(tylenolPrescription);
        tylenolMedicine.addPrescription(tylenolPrescription);

        patient.addMedicine(tylenolMedicine);
        patient.addMedicine(aspirinMedicine);

        assertThat(patient.clash(Arrays.asList("Tylenol", "Aspirin")), is(not(empty())));
    }

    @Test
    public void testSinglePatientNoMedicine(){
        Assert.assertTrue(patient.clash(Arrays.asList("Tylenol", "Aspirin")).isEmpty());
    }

    @Test
    public void testEmptyMedicineListReturnsNothing() {
        patient.addMedicine(new Medicine(("Advil")));
        Assert.assertTrue(patient.clash(new ArrayList<String>()).isEmpty());
    }

    @Test
    public void testSingleMedicineListReturnsNothing() {
        patient.addMedicine(new Medicine(("Advil")));
        Assert.assertTrue(patient.clash(Arrays.asList("Advil")).isEmpty());
    }

    @Test
    public void testSinglePatientSingleMedicine(){
        patient.addMedicine(new Medicine("Aspirin"));
        Assert.assertTrue(patient.clash(Arrays.asList("Tylenol", "Aspirin")).isEmpty());
    }

    @Test
    public void testSinglePatientNullMedicineList(){
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Medicine Names is null.");
        patient.addMedicine(new Medicine("Aspirin"));
        patient.clash(null);
    }

   @Test
    public void testSinglePatientWithEmptyStringMedicineList()
    {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Medicine Names contains Empty String.");
        patient.clash(Collections.singletonList(""));
    }

    @Test
    public void testSinglePatientWithNullInMedicineList()
    {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Medicine Names contains a NULL value.");
        patient.clash(Collections.singletonList(null));
    }

    @Test
    public void testNegativeDaysBack()
    {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("daysBack cannot be negative.");
        patient.clash(Collections.singletonList("Aspirin"), -1);
    }

    @Test
    public void testZeroDaysBackReturnsEmptyCollection()
    {
        patient.addMedicine(new Medicine("Ibuprofen"));
        patient.addMedicine(new Medicine("Tylenol"));
        Assert.assertTrue(patient.clash(List.of("Aspirin", "Twinrix"), 0).isEmpty());
    }

    @Test
    public void testOneDayBackWithNoPrescriptionsReturnsEmptyCollection() {
        patient.addMedicine(new Medicine("Ibuprofen"));
        patient.addMedicine(new Medicine("Tylenol"));

        Assert.assertTrue(patient.clash(List.of("Aspirin", "Twinrix"), 1).isEmpty());
    }

    @Test
    public void testTwoMedicinesWithSamePrescriptionReturnsClash() {
        Medicine ibuprofen = new Medicine("Ibuprofen");
        Medicine tylenol = new Medicine("Tylenol");
        Medicine aspirin = new Medicine("Aspirin");

        Prescription commonPrescription = new Prescription(LocalDate.now(), 1);
        ibuprofen.addPrescription(commonPrescription);
        tylenol.addPrescription(commonPrescription);

        patient.addMedicine(ibuprofen);
        patient.addMedicine(tylenol);
        patient.addMedicine(aspirin);

        Collection<LocalDate> clash = patient.clash(List.of("Tylenol", "Ibuprofen"), 1);
        Assert.assertTrue(!clash.isEmpty());
    }

    @Test
    public void testTwoMedicinesWithSamePrescriptionReturnsClashWithCorrectDates() {
        Medicine ibuprofen = new Medicine("Ibuprofen");
        Medicine tylenol = new Medicine("Tylenol");
        Medicine aspirin = new Medicine("Aspirin");

        LocalDate yesterday = LocalDate.now().minusDays(1);
        Prescription commonPrescription = new Prescription(yesterday, 1);
        ibuprofen.addPrescription(commonPrescription);
        tylenol.addPrescription(commonPrescription);

        patient.addMedicine(ibuprofen);
        patient.addMedicine(tylenol);
        patient.addMedicine(aspirin);

        Collection<LocalDate> clash = patient.clash(List.of("Tylenol", "Ibuprofen"), 10);
        Assert.assertTrue(clash.contains(yesterday));
    }

    @Test
    public void testMultipleMedicinesWithTwoDiffPrescriptionsReturnsNoClash() {
        Medicine ibuprofen = new Medicine("Ibuprofen");
        Medicine tylenol = new Medicine("Tylenol");
        Medicine aspirin = new Medicine("Aspirin");

        LocalDate yesterday = LocalDate.now().minusDays(1);
        Prescription commonPrescription = new Prescription(yesterday, 10);
        Prescription rarePrescription = new Prescription(yesterday.minusDays(5), 1);

        ibuprofen.addPrescription(commonPrescription);
        tylenol.addPrescription(commonPrescription);
        aspirin.addPrescription(rarePrescription);

        patient.addMedicine(ibuprofen);
        patient.addMedicine(tylenol);
        patient.addMedicine(aspirin);

        Collection<LocalDate> clash = patient.clash(List.of("Tylenol", "Ibuprofen", "Aspirin"), 10);
        Assert.assertTrue(clash.isEmpty());
    }

    @Test
    public void testClashHappensOutsideTheWindow() {
        Medicine ibuprofen = new Medicine("Ibuprofen");
        Medicine tylenol = new Medicine("Tylenol");

        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        Prescription commonPrescription = new Prescription(tenDaysAgo, 2);
        Prescription prescriptionOutsideWindow = new Prescription(tenDaysAgo, 4);

        ibuprofen.addPrescription(commonPrescription);
        tylenol.addPrescription(prescriptionOutsideWindow);

        patient.addMedicine(ibuprofen);
        patient.addMedicine(tylenol);

        Collection<LocalDate> clash = patient.clash(List.of("Tylenol", "Ibuprofen"), 7);
        Assert.assertTrue(clash.isEmpty());
    }


}
