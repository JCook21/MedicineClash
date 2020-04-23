import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class PatientTest {

    @Test
    public void testClashNeverReturnsNull() {
        Patient patient = new Patient();
        Assert.assertNotNull(patient.clash(Arrays.asList("Aspirin")));
    }
}
