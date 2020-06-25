import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

		if (!allMedicinesHavePrescriptions(medicineNames)) {
			return Collections.emptyList();
		}

		List<Medicine> filteredMedicines = this.medicines.stream()
				.filter(medicine -> medicineNames.contains(medicine.getName()))
				.collect(Collectors.toList());

		Set<Medicine> medsWithinDaysBack = new HashSet<>();
		for (Medicine meds : filteredMedicines)
		{
			for(Prescription pres : meds.getPrescriptions())
			{
				if (pres.isWithinWindow(daysBack))
				{
					medsWithinDaysBack.add(meds);
				}
			}
		}

		//iterate over the set, and identify the dates where the medicines have clashes.
		// med1 : p1, p2
		// med2 : p3, p4
		// p : start, end
		// timeline: s1..e1
		// timeline: s1..s2...e2...e1 | s1...s2..e1..e2 | s1..e1...s2...e2

		// MAP: date -> Presc
		List<LocalDate> clashingDates = new ArrayList<LocalDate>();
		Map<LocalDate, Integer> datePrescriptionMap = new HashMap<>();
		for (Medicine medicine : medsWithinDaysBack) {
			for (Prescription prescription : medicine.getPrescriptions()) {
				LocalDate currDate = prescription.getDispenseDate();
				LocalDate endDate = prescription.getDispenseDate().plusDays(prescription.getDaysSupply());
				for ( ; currDate.isBefore(endDate) || currDate.isEqual(endDate); currDate = currDate.plusDays(1)) {
					int currCount = datePrescriptionMap.get(currDate) != null ? datePrescriptionMap.get(currDate) : 0;
					datePrescriptionMap.put(currDate, ++currCount);
					if(currCount >= filteredMedicines.size()) {
						clashingDates.add(currDate);
					}
				}
			}
		}

		Map<Medicine, Collection<Prescription>> myMap = new HashMap<>();

		for (Medicine med: filteredMedicines) {
			myMap.put(med, med.getPrescriptions());
		}


		return Collections.singletonList(LocalDate.now());
	}



	private boolean allMedicinesHavePrescriptions(Collection<String> medicineNames) {

		long filteredMedicineCount = this.medicines.stream()
				.filter(medicine -> medicineNames.contains(medicine.getName()))
				.count();

		if (filteredMedicineCount == 0)
			return false;

		return this.medicines.stream()
				.filter(medicine -> medicineNames.contains(medicine.getName()))
				.allMatch(Medicine::hasPrescriptions);
	}

	private void validateInputs(Collection<String> medicineNames, int daysBack) {
		assert daysBack >= 0 : "daysBack cannot be negative.";

		assert medicineNames != null : "Medicine Names is null.";

		assert !medicineNames.contains("") : "Medicine Names contains Empty String.";

		assert medicineNames.stream().noneMatch(Objects::isNull) : "Medicine Names contains a NULL value.";
	}
}