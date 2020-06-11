import java.time.LocalDate;
import java.util.Date;

public class Prescription {

    private LocalDate dispenseDate;
    private int daysSupply = 30;

    public LocalDate getDispenseDate() {
        return dispenseDate;
    }

    public void setDispenseDate(LocalDate dispenseDate) {
        this.dispenseDate = dispenseDate;
    }

    public int getDaysSupply() {
        return daysSupply;
    }

    public void setDaysSupply(int daysSupply) {
        this.daysSupply = daysSupply;
    }

    public Prescription(LocalDate dispenseDate, int daysSupply) {
        this.dispenseDate = dispenseDate;
        this.daysSupply = daysSupply;
    }

}
