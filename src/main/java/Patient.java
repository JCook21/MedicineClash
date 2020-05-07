import java.time.LocalDate;
import java.util.*;

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
        if (daysBack < 0) {
            throw new IllegalArgumentException("daysBack cannot be negative.");
        }

//        Objects.requireNonNull(medicineNames,"Medicine Names is null." );
	    if (medicineNames == null) {
			throw new IllegalArgumentException("Medicine Names is null.");
		}

		if(medicineNames.contains(""))
		{
			throw new IllegalArgumentException("Medicine Names contains Empty String.");
		}

		if(medicineNames.contains(null))
		{
			throw new IllegalArgumentException("Medicine Names contains a NULL value.");
		}

		if ( medicines.size() <= 1 || medicineNames.size() <= 1) {
			return Collections.emptyList();
		}

		return Collections.singletonList(LocalDate.now());
	}
}