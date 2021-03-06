import java.util.ArrayList;
import java.util.Collection;

public class Medicine {

	private Collection<Prescription> prescriptions = new ArrayList<Prescription>();

	private final String name;

	public Medicine(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addPrescription(Prescription prescription) {
		this.prescriptions.add(prescription);
	}

	public Collection<Prescription> getPrescriptions() {
		return this.prescriptions;
	}

	public boolean hasPrescriptions() {
		return  !this.getPrescriptions().isEmpty();
	}
}