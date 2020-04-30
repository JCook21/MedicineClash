import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class PatientTest {

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

        assertThat(patient.clash(Arrays.asList("Tylenol", "Aspirin")).size(), is(not(0)));
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
}
