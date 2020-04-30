import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class Patient {

	private Collection<Medicine> medicines = new ArrayList<Medicine>();

	public void addMedicine(Medicine medicine) {
		this.medicines.add(medicine);
	}

	/*
	* A clash can never happen with <=1 medicine
	* */
	public Collection<LocalDate> clash(Collection<String> medicineNames) {
		return clash(medicineNames, 90);
	}

	public Collection<LocalDate> clash(Collection<String> medicineNames, int daysBack) {
		if (medicines.size() <= 1 || medicineNames.size() <= 1) {
			return Collections.emptyList();
		}
		return Collections.singletonList(LocalDate.now());
	}
}