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
    private Medicine ibuprofen, tylenol, aspirin;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void testSetup()
    {
        patient = new Patient();
        ibuprofen = new Medicine("Ibuprofen");
        tylenol = new Medicine("Tylenol");
        aspirin = new Medicine("Aspirin");
    }

    @Test
    public void testClashNeverReturnsNull() {
        Assert.assertNotNull(patient.clash(Collections.singletonList(aspirin.getName())));
    }

    @Test
    public void testClashWithTwoMedicines() {
        Prescription tylenolPrescription = new Prescription(LocalDate.now(), 5);

        aspirin.addPrescription(tylenolPrescription);
        tylenol.addPrescription(tylenolPrescription);

        addMedicineToPatient(tylenol, aspirin);

        assertThat(patient.clash(Arrays.asList(tylenol.getName(), aspirin.getName())), is(not(empty())));
    }

    @Test
    public void testSinglePatientNoMedicine(){
        Assert.assertTrue(patient.clash(Arrays.asList(tylenol.getName(), aspirin.getName())).isEmpty());
    }

    @Test
    public void testEmptyMedicineListReturnsNothing() {
        patient.addMedicine(new Medicine(("Advil")));
        Assert.assertTrue(patient.clash(new ArrayList<String>()).isEmpty());
    }

    @Test
    public void testSingleMedicineListReturnsNothing() {
        patient.addMedicine(new Medicine(("Advil")));
        Assert.assertTrue(patient.clash(Collections.singletonList("Advil")).isEmpty());
    }

    @Test
    public void testSinglePatientSingleMedicine(){
        addMedicineToPatient(aspirin);
        Assert.assertTrue(patient.clash(Arrays.asList(tylenol.getName(), aspirin.getName())).isEmpty());
    }

    @Test
    public void testSinglePatientNullMedicineList(){
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Medicine Names is null.");
        patient.addMedicine(new Medicine(aspirin.getName()));
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
        patient.clash(Collections.singletonList(aspirin.getName()), -1);
    }

    @Test
    public void testZeroDaysBackReturnsEmptyCollection()
    {
        addMedicineToPatient(ibuprofen, tylenol);
        Assert.assertTrue(patient.clash(List.of(aspirin.getName(), "Twinrix"), 0).isEmpty());
    }

    @Test
    public void testOneDayBackWithNoPrescriptionsReturnsEmptyCollection() {
        addMedicineToPatient(ibuprofen, tylenol);

        Assert.assertTrue(patient.clash(List.of(aspirin.getName(), "Twinrix"), 1).isEmpty());
    }

    @Test
    public void testTwoMedicinesWithSamePrescriptionReturnsClash() {

        Prescription commonPrescription = new Prescription(LocalDate.now(), 1);
        ibuprofen.addPrescription(commonPrescription);
        tylenol.addPrescription(commonPrescription);

        addMedicineToPatient(ibuprofen, tylenol, aspirin);

        Collection<LocalDate> clash = patient.clash(List.of(tylenol.getName(), ibuprofen.getName()), 1);
        Assert.assertTrue(!clash.isEmpty());
    }

    @Test
    public void testTwoMedicinesWithSamePrescriptionReturnsClashWithCorrectDates() {


        LocalDate yesterday = LocalDate.now().minusDays(1);
        Prescription commonPrescription = new Prescription(yesterday, 1);
        ibuprofen.addPrescription(commonPrescription);
        tylenol.addPrescription(commonPrescription);

        addMedicineToPatient(ibuprofen, tylenol, aspirin);

        Collection<LocalDate> clash = patient.clash(List.of(tylenol.getName(), ibuprofen.getName()), 10);
        Assert.assertTrue(clash.contains(yesterday) && clash.size() == 1);
    }

    @Test
    public void testMultipleMedicinesWithTwoDiffPrescriptionsReturnsNoClash() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Prescription commonPrescription = new Prescription(yesterday, 10);
        Prescription rarePrescription = new Prescription(yesterday.minusDays(5), 1);

        ibuprofen.addPrescription(commonPrescription);
        tylenol.addPrescription(commonPrescription);
        aspirin.addPrescription(rarePrescription);

        addMedicineToPatient(ibuprofen, tylenol, aspirin);

        Collection<LocalDate> clash = patient.clash(List.of(tylenol.getName(), ibuprofen.getName(), aspirin.getName()), 10);
        Assert.assertTrue(clash.isEmpty());
    }


    @Test
    public void testClashHappensOutsideTheWindow() {
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        Prescription commonPrescription = new Prescription(tenDaysAgo, 2);
        Prescription prescriptionOutsideWindow = new Prescription(tenDaysAgo, 4);

        ibuprofen.addPrescription(commonPrescription);
        tylenol.addPrescription(prescriptionOutsideWindow);

        addMedicineToPatient(ibuprofen, tylenol);

        Collection<LocalDate> clash = patient.clash(List.of(tylenol.getName(), ibuprofen.getName()), 7);
        Assert.assertTrue(clash.isEmpty());
    }

    @Test
    public void testClashWithinWindow_AccurateDates() {
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        Prescription commonPrescription = new Prescription(tenDaysAgo, 20);

        ibuprofen.addPrescription(commonPrescription);
        tylenol.addPrescription(commonPrescription);

        addMedicineToPatient(ibuprofen, tylenol);

        Collection<LocalDate> clash = patient.clash(List.of(tylenol.getName(), ibuprofen.getName()), 10);

        List<LocalDate> expectedDates = new ArrayList<>();

        for(LocalDate currentDate = tenDaysAgo; currentDate.isBefore(LocalDate.now()) || currentDate.isEqual(LocalDate.now()); currentDate = currentDate.plusDays(1)){
            expectedDates.add(currentDate);
        }

        Assert.assertEquals(expectedDates.size(), clash.size());
        Assert.assertTrue(clash.containsAll(expectedDates));
    }

    @Test
    public void testClash_NonOverlappingPrescriptions_WithinWindow_NoClash() {
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        LocalDate fiveDaysAgo = LocalDate.now().minusDays(5);

        Prescription prescription1 = new Prescription(tenDaysAgo, 3);
        Prescription prescription2 = new Prescription(fiveDaysAgo, 2);

        ibuprofen.addPrescription(prescription1);
        tylenol.addPrescription(prescription2);

        addMedicineToPatient(ibuprofen, tylenol);

        Collection<LocalDate> clash = patient.clash(List.of(tylenol.getName(), ibuprofen.getName()), 10);

        Assert.assertEquals(0, clash.size());
    }

    // presc have clashes
    // presc1_ten_days_ago___five_days_ago___presc1_end_2_days_ago__today
    // presc2_eight_days_ago___five_days_ago___presc2_end_3_days_ago__today
    @Test
    public void testClash_PrescriptionStartsBeforeWindow_PrescriptionEndsWithinWindow_AccurateDates() {
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        LocalDate eightDaysAgo = LocalDate.now().minusDays(8);

        Prescription prescription1 = new Prescription(tenDaysAgo, 8);
        Prescription prescription2 = new Prescription(eightDaysAgo, 5);

        ibuprofen.addPrescription(prescription1);
        tylenol.addPrescription(prescription2);

        addMedicineToPatient(ibuprofen, tylenol);

        Collection<LocalDate> clash = patient.clash(List.of(tylenol.getName(), ibuprofen.getName()), 10);

        Assert.assertEquals(5, clash.size());
    }


    @Test
    public void testClash_Prescription_daysSupplyIsEqualToClashingDates() {
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);

        Prescription prescription1 = new Prescription(tenDaysAgo, 8);
        Prescription prescription2 = new Prescription(tenDaysAgo, 8);

        ibuprofen.addPrescription(prescription1);
        tylenol.addPrescription(prescription2);

        addMedicineToPatient(ibuprofen, tylenol);

        Collection<LocalDate> clash = patient.clash(List.of(tylenol.getName(), ibuprofen.getName()), 10);

        Assert.assertEquals(8, clash.size());
    }


    @Test
    public void testClash_PrescriptionWithLargeNumbers_AccurateDates() {
        LocalDate tenYearsAgo = LocalDate.now().minusYears(10);
        LocalDate eightYearsAgo = LocalDate.now().minusYears(8);

        Prescription prescription1 = new Prescription(tenYearsAgo, 8 * 365);
        Prescription prescription2 = new Prescription(eightYearsAgo, 5 * 365);

        ibuprofen.addPrescription(prescription1);
        tylenol.addPrescription(prescription2);

        addMedicineToPatient(ibuprofen, tylenol);

        Collection<LocalDate> clash = patient.clash(List.of(tylenol.getName(), ibuprofen.getName()), 10 * 365);

        Assert.assertEquals(5 * 365, clash.size());
    }

    @Test
    public void testClashOneMedicineMultiplePrescriptionsAndClashOutsideWindow() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        LocalDate fiveDaysAgo = LocalDate.now().minusDays(5);

        Prescription prescription1 = new Prescription(thirtyDaysAgo, 10);
        Prescription prescription2 = new Prescription(tenDaysAgo, 5);
        Prescription prescription3 = new Prescription(fiveDaysAgo, 2);

        ibuprofen.addPrescription(prescription1);
        tylenol.addPrescription(prescription1);

        ibuprofen.addPrescription(prescription2);
        tylenol.addPrescription(prescription3);

        addMedicineToPatient(ibuprofen, tylenol);

        Collection<LocalDate> clash = patient.clash(List.of(tylenol.getName(), ibuprofen.getName()), 7);

        Assert.assertEquals(0, clash.size());
    }

    @Test
    public void testClash_Prescription_multipleClashesWithinWindow() {
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        LocalDate twentyDaysAgo = LocalDate.now().minusDays(20);

        Prescription prescription1 = new Prescription(tenDaysAgo, 2);
        Prescription prescription2 = new Prescription(twentyDaysAgo, 2);

        ibuprofen.addPrescription(prescription1);
        ibuprofen.addPrescription(prescription2);

        tylenol.addPrescription(prescription1);
        tylenol.addPrescription(prescription2);

        addMedicineToPatient(ibuprofen, tylenol);

        Collection<LocalDate> clash = patient.clash(List.of(tylenol.getName(), ibuprofen.getName()), 25);

        Assert.assertEquals(4, clash.size());
    }


    private void addMedicineToPatient(Medicine... listOfMedicines) {

        for (Medicine meds : listOfMedicines)
        {
            patient.addMedicine(meds);
        }

    }
}
