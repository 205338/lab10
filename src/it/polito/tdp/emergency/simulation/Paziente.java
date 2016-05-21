//////////////////////////////////////////////////////////////////-*-java-*-//
//             // Classroom code for "Tecniche di Programmazione"           //
//   #####     // (!) Giovanni Squillero <giovanni.squillero@polito.it>     //
//  ######     //                                                           //
//  ###   \    // Copying and distribution of this file, with or without    //
//   ##G  c\   // modification, are permitted in any medium without royalty //
//   #     _\  // provided this notice is preserved.                        //
//   |   _/    // This file is offered as-is, without any warranty.         //
//   |  _/     //                                                           //
//             // See: http://bit.ly/tecn-progr                             //
//////////////////////////////////////////////////////////////////////////////

package it.polito.tdp.emergency.simulation;

public class Paziente implements Comparable<Paziente> {
	public enum StatoPaziente {
		ROSSO, GIALLO, VERDE, BIANCO, IN_CURA, SALVO, NERO
	};

	private int id;
	private StatoPaziente stato;

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Paziente other = (Paziente) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public StatoPaziente getStato() {
		return stato;
	}

	public void setStato(StatoPaziente stato) {
		this.stato = stato;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Paziente [id=" + id + ", stato=" + stato + "]";
	}

	public Paziente(int id, StatoPaziente stato) {
		super();
		this.id = id;
		this.stato = stato;
	}

	@Override
	public int compareTo(Paziente arg0) {
		return Integer.compare(this.getStato().ordinal(), arg0.getStato().ordinal());
	}

}
