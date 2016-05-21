package it.polito.tdp.emergency.simulation;

public class Doctor implements Comparable<Doctor> {

	public enum StatoTurno {
		AT_WORK, AT_REST
	}

	private int id;
	private StatoTurno stato;

	public Doctor(int id, StatoTurno stato) {
		super();
		this.id = id;
		this.stato = stato;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public StatoTurno getStato() {
		return stato;
	}

	public void setStato(StatoTurno stato) {
		this.stato = stato;
	}

	@Override
	public String toString() {
		return "Doctor [id=" + id + ", stato=" + stato + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Doctor other = (Doctor) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(Doctor arg0) {
		return Integer.compare(this.getStato().ordinal(), arg0.getStato().ordinal());
	}

}
