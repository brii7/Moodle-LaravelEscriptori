package net.brian.MoodleNotes;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import javafx.scene.control.ListView;

import javafx.scene.control.ComboBox;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MoodleNotesControllerController {
	@FXML
	private Button connectar;
	@FXML
	private Button buscar;
	@FXML
	private ComboBox<String> selectorCurs = new ComboBox<String>();
	@FXML
	private ComboBox<String> selectorUF = new ComboBox<String>();
	@FXML
	private ListView<String> llistaTasques = new ListView<String>();
	@FXML
	private ListView<String> llistaNotes = new ListView<String>();
	@FXML
	private TextArea events;
	@FXML
	private TextField buscador;
	
	
	
	Alert alert = new Alert(AlertType.WARNING);
	Connection connexio;
	String idalumne;
	String nomalumne;
	String cursactual;
	String nomcursactual;
	String ufactual;
	String nomufactual;
	ArrayList<String> tasques = new ArrayList<String>();
	ArrayList<String> notes = new ArrayList<String>();

	
	
	
	// Event Listener on Button[#connectar].onAction
	@FXML
	public void connectar(ActionEvent event) {

		// Inicialitzem la connexió a la base de dades, si no funciona, exception.
    	try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connexio = DriverManager.getConnection("jdbc:mysql://localhost/moodle?serverTimezone=GMT&useSSL=false", "root", "");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	events.setText("Connexió a la BDD exitosa!");
		
		
	}
	// Event Listener on Button[#buscar].onAction
	@FXML
	public void buscarAlumne(ActionEvent event) throws SQLException {
		String buscat = buscador.getText();
		nomalumne = buscat;
		String query = "SELECT id, name from users WHERE name='"+nomalumne+"'";
		ResultSet resultat = consultarDB(connexio, query);

		while(resultat.next()){
			
			idalumne = resultat.getString("id");
			
			String queryCurs = "SELECT curs_id from curs_user WHERE user_id='"+idalumne+"'";
			ResultSet resultatCurs = consultarDB(connexio, queryCurs);
			
			while(resultatCurs.next()){
			
				cursactual = resultatCurs.getString("curs_id");
				String queryNomCurs = "SELECT name from cursos WHERE id='"+cursactual+"'";
				ResultSet resultatNomCurs = consultarDB(connexio, queryNomCurs);
				
				while(resultatNomCurs.next()){
					nomcursactual = resultatNomCurs.getString("name");
					selectorCurs.getItems().add(nomcursactual);
					
				}
			}
		}
	}
	// Event Listener on ComboBox[#selectorCurs].onAction
	@FXML
	public void seleccionarCurs(ActionEvent event) throws SQLException {
		
		
		String cursseleccionat = selectorCurs.getValue();
		nomcursactual = cursseleccionat;
		String querycursid = "SELECT id from cursos WHERE name=\""+cursseleccionat+"\"";
		ResultSet resultatCursSeleccionat = consultarDB(connexio, querycursid);
		
		while (resultatCursSeleccionat.next()){
			
			cursactual = resultatCursSeleccionat.getString("id");
			String queryuf = "SELECT name from unitatsformatives WHERE course_id='"+cursactual+"'";
			ResultSet resultatqueryuf = consultarDB(connexio, queryuf);
			
			while(resultatqueryuf.next()){
				
				nomufactual = resultatqueryuf.getString("name");
				selectorUF.getItems().add(nomufactual);

			}
		}
	}
	// Event Listener on ComboBox[#selectorUF].onAction
	@FXML
	public void seleccionarUF(ActionEvent event) throws SQLException {
		
		String cursseleccionat = selectorCurs.getValue();
		nomcursactual = cursseleccionat;
		String querycursid = "SELECT id from cursos WHERE name=\""+cursseleccionat+"\"";
		ResultSet resultatCursSeleccionat = consultarDB(connexio, querycursid);
		
		while (resultatCursSeleccionat.next()){
			
			cursactual = resultatCursSeleccionat.getString("id");
			
			String ufseleccionada = selectorUF.getValue();
			String queryufseleccionada = "SELECT id from unitatsformatives WHERE name=\""+ufseleccionada+"\"";
			ResultSet resultatUFSeleccionada = consultarDB(connexio, queryufseleccionada);
			
			while(resultatUFSeleccionada.next()){
				
				ufactual = resultatUFSeleccionada.getString("id");
				
			}
		}
		
		String queryidtasques = "SELECT task_id,nota from user_task WHERE uf_id=\""+ufactual+"\" AND course_id=\""+cursactual+"\" AND user_id=\""+idalumne+"\"";
		ResultSet resultatqueryidtasques = consultarDB(connexio, queryidtasques);
		while(resultatqueryidtasques.next()){
			
			String nota = resultatqueryidtasques.getString("nota");
			notes.add(nota);
			String tascaactual = resultatqueryidtasques.getString("task_id");
			String querytascaactual = "SELECT name from tasks WHERE id=\""+tascaactual+"\"";
			ResultSet resultatquerynomtasca = consultarDB(connexio, querytascaactual);
			
			while(resultatquerynomtasca.next()){
				
				String nomtasca = resultatquerynomtasca.getString("name");
				tasques.add(nomtasca);
				
			}
		}
		ObservableList<String> tasquesobs = FXCollections.observableArrayList();
		for(String tasca: tasques){
			tasquesobs.add(tasca);
		}
		tasques.clear();
		
		llistaTasques.setItems(tasquesobs);
		
		ObservableList<String> notesobs = FXCollections.observableArrayList();
		for(String nota: notes){
			notesobs.add(nota);
		}
		notes.clear();
		llistaNotes.setItems(notesobs);
		
		
	}
	
public static ResultSet consultarDB(Connection connexio, String query){
    	
    	Statement consulta = null;
    	ResultSet resultat = null;
    	try {
			consulta = connexio.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			resultat = consulta.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultat;
    }
}
