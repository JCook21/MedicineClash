import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.empty;

public class PatientTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testClashNeverReturnsNull() {
        Patient patient = new Patient();
        Assert.assertNotNull(patient.clash(Arrays.asList("Aspirin")));
    }

    @Test
    public void testClashWithTwoMedicines() {
        Patient patient = new Patient();
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
        Patient patient = new Patient();
        Assert.assertTrue(patient.clash(Arrays.asList("Tylenol", "Aspirin")).isEmpty());
    }

    @Test
    public void testEmptyMedicineListReturnsNothing() {
        Patient patient = new Patient();
        patient.addMedicine(new Medicine(("Advil")));
        Assert.assertTrue(patient.clash(new ArrayList<String>()).isEmpty());
    }

    @Test
    public void testSingleMedicineListReturnsNothing() {
        Patient patient = new Patient();
        patient.addMedicine(new Medicine(("Advil")));
        Assert.assertTrue(patient.clash(Arrays.asList("Advil")).isEmpty());
    }

    @Test
    public void testSinglePatientSingleMedicine(){
        Patient patient = new Patient();
        patient.addMedicine(new Medicine("Aspirin"));
        Assert.assertTrue(patient.clash(Arrays.asList("Tylenol", "Aspirin")).isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSinglePatientNullMedicineList(){
        Patient patient = new Patient();
        patient.addMedicine(new Medicine("Aspirin"));
        patient.clash(null);
    }

   @Test
    public void testSinglePatientWithEmptyStringMedicineList()
    {
        Patient patient = new Patient();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Medicine Names contains Empty String.");
        patient.clash(List.of(""));
    }
}
