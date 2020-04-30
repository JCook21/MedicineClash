import java.time.LocalDate;
import java.util.Date;

public class Prescription {

    private LocalDate dispenseDate;
    private int daysSupply = 30;

    public Prescription(LocalDate dispenseDate, int daysSupply) {
        this.dispenseDate = dispenseDate;
        this.daysSupply = daysSupply;
    }

}
