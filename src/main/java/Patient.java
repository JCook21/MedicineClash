import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Patient {

	private Collection<Medicine> medicines = new ArrayList<>();

	public void addMedicine(Medicine medicine) {
		medicines.add(medicine);
	}

	/*
	* A clash can never happen with <=1 medicine
	* */
	public Collection<LocalDate> clash(Collection<String> medicineNames) {
		return clash(medicineNames, 90);
	}

	public Collection<LocalDate> newClash(Collection<Medicine> medicines, int daysBack){
		var medicineString = medicines.stream().map(medicine -> medicine.getName()).collect(Collectors.toList());
		return clash(medicineString, daysBack);
	}

	public Collection<LocalDate> clash(Collection<String> medicineNames, int daysBack) {

		validateInputs(medicineNames, daysBack);

		if ( medicines.size() <= 1 || medicineNames.size() <= 1 || daysBack == 0) {
			return Collections.emptyList();
		}



		if (!allMedicinesHavePrescriptions()) {
			return Collections.emptyList();
		}

		return Collections.singletonList(LocalDate.now());
	}

	private boolean allMedicinesHavePrescriptions() {
		return this.medicines
				.stream()
				.allMatch(Medicine::hasPrescriptions);
	}

	private void validateInputs(Collection<String> medicineNames, int daysBack) {
		assert daysBack >= 0 : "daysBack cannot be negative.";

		assert medicineNames != null : "Medicine Names is null.";

		assert !medicineNames.contains("") : "Medicine Names contains Empty String.";

		assert medicineNames.stream().noneMatch(Objects::isNull) : "Medicine Names contains a NULL value.";
	}
}