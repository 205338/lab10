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

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import it.polito.tdp.emergency.db.FieldHospitalDAO;

public class Core {

	/* ----------------- VARIABLES ----------------- */

	int pazientiSalvati = 0;
	int pazientiPersi = 0;

	int idDottore = 0;
	Queue<Evento> listaEventi = new PriorityQueue<Evento>();
	Map<Integer, Paziente> pazienti = new HashMap<Integer, Paziente>();
	Map<Integer, Doctor> dottori = new HashMap<Integer, Doctor>();
	int assistenteDisponibile;

	int mediciDisponibili = 0;
	long oreDiSfasamento = 0;
	Queue<Paziente> pazientiInAttesaUrgente = new PriorityQueue<Paziente>();
	Queue<Paziente> pazientiInAttesa = new PriorityQueue<Paziente>();
	Queue<Doctor> dottoriInAttesa = new PriorityQueue<Doctor>();

	FieldHospitalDAO fieldHospitalDAO = new FieldHospitalDAO();

	/* ----------------- CONSTRUCTOR AND METHODS ----------------- */

	public Core() {
		getEvents(listaEventi, pazienti);
	}

	public void passo() {
		Evento e = listaEventi.remove();

		switch (e.getTipo()) {

		case PAZIENTE_ARRIVA:
			System.out.println("Arrivo paziente:" + e);
			if (pazienti.get(e.getDato()).getStato() == Paziente.StatoPaziente.ROSSO)
				pazientiInAttesaUrgente.add(pazienti.get(e.getDato()));
			else
				pazientiInAttesa.add(pazienti.get(e.getDato()));

			switch (pazienti.get(e.getDato()).getStato()) {
			case BIANCO:
				break;
			case ROSSO:
				this.aggiungiEvento(new Evento(e.getTempo() + 1 * 60, Evento.TipoEvento.PAZIENTE_MUORE, e.getDato()));
				break;
			case GIALLO:
				this.aggiungiEvento(new Evento(e.getTempo() + 6 * 60, Evento.TipoEvento.PAZIENTE_MUORE, e.getDato()));
				break;
			case VERDE:
				this.aggiungiEvento(new Evento(e.getTempo() + 12 * 60, Evento.TipoEvento.PAZIENTE_MUORE, e.getDato()));
				break;
			default:
				System.err.println("Panik!");
			}
			break;

		case PAZIENTE_GUARISCE:
			if (pazienti.get(e.getDato()).getStato() != Paziente.StatoPaziente.NERO) {
				if (assistenteDisponibile == pazienti.get(e.getDato()).getId()) {
					// is the patient of the assistent
					assistenteDisponibile = 0;
				} else {
					++mediciDisponibili;
				}
				System.out.println("Paziente salvato: " + e);
				pazienti.get(e.getDato()).setStato(Paziente.StatoPaziente.SALVO);
				++pazientiSalvati;
			}
			break;

		case PAZIENTE_MUORE:
			if (pazienti.get(e.getDato()).getStato() == Paziente.StatoPaziente.SALVO) {
				// System.out.println("Paziente gia' salvato: " + e);
			} else {
				++pazientiPersi;
				if (pazienti.get(e.getDato()).getStato() == Paziente.StatoPaziente.IN_CURA) {
					if (assistenteDisponibile == pazienti.get(e.getDato()).getId()) {
						assistenteDisponibile = 0;
					} else {
						++mediciDisponibili;
					}
					pazienti.get(e.getDato()).setStato(Paziente.StatoPaziente.NERO);
					System.out.println("Paziente morto sotto i ferri: " + e);
				} else {
					System.out.println("Paziente morto: " + e);
				}
			}
			break;

		case DOCTOR_INIZIA_TURNO:

			++mediciDisponibili;
			this.aggiungiEvento(new Evento(e.getTempo() + 8 * 60, Evento.TipoEvento.DOCTOR_FINE_TURNO, e.getDato()));
			dottori.get(e.getDato()).setStato(Doctor.StatoTurno.AT_WORK);

			break;

		case DOCTOR_FINE_TURNO:

			--mediciDisponibili;
			this.aggiungiEvento(new Evento(e.getTempo() + 16 * 60, Evento.TipoEvento.DOCTOR_INIZIA_TURNO, e.getDato()));
			dottori.get(e.getDato()).setStato(Doctor.StatoTurno.AT_REST);

			break;

		default:
			System.err.println("Panik!");
		}
		long currentTime = e.getTempo();
		while (cura(currentTime))
			;
	}

	protected boolean cura(long adesso) {
		if (pazientiInAttesa.isEmpty() && pazientiInAttesaUrgente.isEmpty())
			return false;

		if (assistenteDisponibile != 0 && mediciDisponibili == 0)
			return false;

		// cura rosso
		if (mediciDisponibili > 0) {
			if (!pazientiInAttesaUrgente.isEmpty()) {
				Paziente pu = pazientiInAttesaUrgente.remove();
				if (pu.getStato() != Paziente.StatoPaziente.NERO) {
					--mediciDisponibili;
					setInizioCura(pu, adesso);
					return true;
						
				}
			}
		}

		if (!pazientiInAttesa.isEmpty()) {

			Paziente p = pazientiInAttesa.remove();

			if (p.getStato() != Paziente.StatoPaziente.NERO) {
				if (assistenteDisponibile == 0) {
					assistenteDisponibile = p.getId();
				} else {
					--mediciDisponibili;
				}
				setInizioCura(p, adesso);
				return true;
			}

		}
		return true; // TODO TRUE?????
	}

	public void simula() {
		setTurniMedici();
		assistenteDisponibile = 0;
		while (!listaEventi.isEmpty()) {
			passo();
		}
	}

	/* ----------------- GETTERS AND SETTERS ----------------- */
	
	
	private void setInizioCura(Paziente p, long adesso) {
		pazienti.get(p.getId()).setStato(Paziente.StatoPaziente.IN_CURA);
		aggiungiEvento(new Evento(adesso + 30, Evento.TipoEvento.PAZIENTE_GUARISCE, p.getId()));
		System.out.println("\tInizio a curare: " + p);
	}

	private void setTurniMedici() {
		Evento e = listaEventi.element();
		long t0 = e.getTempo();

		for (int i = 0; i < dottori.size(); i++) {
			this.aggiungiEvento(new Evento(t0 + i * 120, Evento.TipoEvento.DOCTOR_INIZIA_TURNO, -(i+1)));
		}
	}

	public void getEvents(Queue<Evento> listaEventi, Map<Integer, Paziente> pazienti) {
		listaEventi.clear();
		pazienti.clear();
		fieldHospitalDAO.getEvents(listaEventi, pazienti);
	}

	public void aggiungiDottore(String nomeDottore) {
		idDottore--;
		dottori.put(idDottore, new Doctor(idDottore, Doctor.StatoTurno.AT_REST));
	}

	public int getPazientiSalvati() {
		return pazientiSalvati;
	}

	public int getPazientiPersi() {
		return pazientiPersi;
	}

	public int getMediciDisponibili() {
		return mediciDisponibili;
	}

	public void setMediciDisponibili(int mediciDisponibili) {
		this.mediciDisponibili = mediciDisponibili;
	}

	public void aggiungiEvento(Evento e) {
		listaEventi.add(e);
	}

	public void aggiungiPaziente(Paziente p) {
		pazienti.put(p.getId(), p);
	}

	public void setOreDiSfasamento(long oreDiSfasamento) {
		this.oreDiSfasamento = oreDiSfasamento;
	}
}
