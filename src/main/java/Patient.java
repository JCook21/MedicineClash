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

        Set<Medicine> medsWithinDaysBack = getMedicinesWithinDaysBack(daysBack, filteredMedicines);

        // MAP: date -> Presc
        List<LocalDate> clashingDates = getClashingDates(filteredMedicines, medsWithinDaysBack, daysBack);

		return clashingDates;
	}

    private Set<Medicine> getMedicinesWithinDaysBack(int daysBack, List<Medicine> filteredMedicines) {
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
        return medsWithinDaysBack;
    }

    private List<LocalDate> getClashingDates(List<Medicine> filteredMedicines, Set<Medicine> medsWithinDaysBack, int daysBack) {

	    LocalDate windowStartDate = LocalDate.now().minusDays(daysBack);
	    LocalDate windowEndDate = LocalDate.now();

        List<LocalDate> clashingDates = new ArrayList<LocalDate>();
        Map<LocalDate, Integer> datePrescriptionMap = new HashMap<>();
        for (Medicine medicine : medsWithinDaysBack) {
            for (Prescription prescription : medicine.getPrescriptions()) {

                LocalDate prescriptionDispenseDate = prescription.getDispenseDate();
                //There is a minus 1 below, because the days supply includes the dispense date.
                LocalDate prescriptionEndDate = prescriptionDispenseDate.plusDays(prescription.getDaysSupply() - 1) ;

                LocalDate currDate = prescriptionDispenseDate.isBefore(windowStartDate) ? windowStartDate : prescriptionDispenseDate;
                LocalDate endDate = prescriptionEndDate.isAfter(windowEndDate) ? windowEndDate : prescriptionEndDate;

                for ( ; currDate.isBefore(endDate) || currDate.isEqual(endDate); currDate = currDate.plusDays(1)) {
                    int currCount = datePrescriptionMap.get(currDate) != null ? datePrescriptionMap.get(currDate) : 0;
                    datePrescriptionMap.put(currDate, ++currCount);
                    if(currCount >= filteredMedicines.size()) {
                        clashingDates.add(currDate);
                    }
                }
            }
        }
        return clashingDates;
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