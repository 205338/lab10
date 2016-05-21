package it.polito.tdp.emergency;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.emergency.simulation.Core;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class EmergencyController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField txtMediciDisponibili;

	@FXML
	private TextField txtOreDiSfasamento;

	int mediciDisponibili = 0;
	long oreDiSfasamento = 0;

	Core core;

	public void setModel(Core model) {
		this.core = model;
	}

	@FXML
	void addDoctor(ActionEvent event) {
		String nomeDottore = txtMediciDisponibili.getText();
		core.aggiungiDottore(nomeDottore);
	}

	@FXML
	void doSimulation(ActionEvent event) {
		oreDiSfasamento = Long.parseLong(txtOreDiSfasamento.getText());
		core.setOreDiSfasamento(oreDiSfasamento);
		core.simula();
	}

	@FXML
	void initialize() {
		assert txtMediciDisponibili != null : "fx:id=\"txtMediciDisponibili\" was not injected: check your FXML file 'Emergency.fxml'.";
		assert txtOreDiSfasamento != null : "fx:id=\"txtOreDiSfasamento\" was not injected: check your FXML file 'Emergency.fxml'.";

	}
}
