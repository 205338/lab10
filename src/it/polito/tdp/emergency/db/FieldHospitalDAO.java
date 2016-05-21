////////////////////////////////////////////////////////////////////////////////
//             //                                                             //
//   #####     // Field hospital simulator                                    //
//  ######     // (!) 2013 Giovanni Squillero <giovanni.squillero@polito.it>  //
//  ###   \    //                                                             //
//   ##G  c\   // Field Hospital DAO                                          //
//   #     _\  // Test with MariaDB 10 on win                                 //
//   |   _/    //                                                             //
//   |  _/     //                                                             //
//             // 03FYZ - Tecniche di programmazione 2012-13                  //
////////////////////////////////////////////////////////////////////////////////

package it.polito.tdp.emergency.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Queue;

import it.polito.tdp.emergency.simulation.Evento;
import it.polito.tdp.emergency.simulation.Paziente;


public class FieldHospitalDAO {

	public void getEvents(Queue<Evento> listaEventi, Map<Integer, Paziente> pazienti) {		
		
		try {
			Connection conn = DBConnect.getInstance().getConnection();
			String sql = "SELECT * FROM arrivals";
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();
			
			boolean first = true;
			long t0 = 0;
			while (rs.next()) {
				
				if (first) {
					t0 =  (long)( ((System.currentTimeMillis() - rs.getTimestamp("timestamp").getTime())/1000)/60 );
					first = false;
				}
//				long time = (long)(rs.getTimestamp("timestamp").getTime()/(1.67*Math.pow(10, -5))) - t0;
				long time = (long)( t0 - ((System.currentTimeMillis() - rs.getTimestamp("timestamp").getTime())/1000)/60 );
				int idPatient = rs.getInt("patient");
				
				Evento e = new Evento(time, Evento.TipoEvento.PAZIENTE_ARRIVA, idPatient);
				listaEventi.add(e);
				
				switch (rs.getString("triage")) {
				case "Red":
					pazienti.put(idPatient, new Paziente(idPatient, Paziente.StatoPaziente.ROSSO));
					break;
				case "Green":
					pazienti.put(idPatient, new Paziente(idPatient, Paziente.StatoPaziente.VERDE));
					break;
				case "Yellow":
					pazienti.put(idPatient, new Paziente(idPatient, Paziente.StatoPaziente.GIALLO));
					break;
				case "White":
					pazienti.put(idPatient, new Paziente(idPatient, Paziente.StatoPaziente.BIANCO));
					break;
				default:
					break;
				}
				
			}
			
		} catch (SQLException e) {
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
				
	}
	
}
