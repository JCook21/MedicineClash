import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class PatientTest {

    @Test
    public void testClashNeverReturnsNull() {
        Patient patient = new Patient();
        Assert.assertNotNull(patient.clash(Arrays.asList("Aspirin")));
    }

    @Test
    public void testClashOneDate(){
        Patient patient = new Patient();
        Assert.assertEquals(1, patient.clash(Arrays.asList("Tylenol")).size());

    }
}
