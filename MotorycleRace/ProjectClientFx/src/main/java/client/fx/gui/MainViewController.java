package client.fx.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import project.model.Organizer;
import project.model.Participant;
import project.model.Race;
import project.networking.dto.ParticipantDto;
import project.networking.dto.RacesDto;
import project.services.IProjectObserver;
import project.services.IProjectServices;
import project.services.ProjectException;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController implements Initializable, IProjectObserver {
    ObservableList<RacesDto> model = FXCollections.observableArrayList();
    ObservableList<ParticipantDto> modelPart = FXCollections.observableArrayList();
    private IProjectServices server;
    private Organizer loggedOrganizer;
    private List<RacesDto> selectedRaces;
    private List<ParticipantDto> selectedParticipants;
    private RacesDto selectedRacesDto;
    @FXML
    private TextField participantName, participantMotor, participantTeam;
    @FXML
    private TableView<RacesDto> RacesTable;
    @FXML
    private TableColumn<RacesDto, String> motorColumn;
    @FXML
    private TableColumn<RacesDto, String> nrColumn;
    @FXML
    private TableView<ParticipantDto> participantsTable;
    @FXML
    private TableColumn<ParticipantDto, String> nameColumn;
    @FXML
    private TableColumn<ParticipantDto, String> motorColumn2;
    @FXML
    private TableColumn<ParticipantDto, String> teamColumn;
    @FXML
    public TableColumn<ParticipantDto, String> raceNrColumn;
    @FXML
    private TextField teamSearchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button addButton;
    @FXML
    private Button logoutButton;

    public MainViewController() {}

    public MainViewController(IProjectServices server) {
        this.server = server;
    }

    public void setServer(IProjectServices server) throws ProjectException {
        this.server = server;
    }

    public void setLoggedOrganizer(Organizer loggedOrganizer) {
        this.loggedOrganizer =loggedOrganizer;
    }

    private void populateTable() throws ProjectException  {
        Race[] races = server.getAllRaces(loggedOrganizer, this);
        List<Race> raceList = Arrays.stream(races).toList();
        model.setAll(raceList.stream().map(RacesDto::new).toList());
        selectedRaces = raceList.stream().map(RacesDto::new).toList();
    }

    public void initializeFields() {
        motorColumn.setCellValueFactory(new PropertyValueFactory<>("Motor"));
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("Participants"));
        RacesTable.setItems(model);
    }

    public void populateTablePart()throws ProjectException  {
        Participant[] participants = server.getAllParticipants(loggedOrganizer, this);
        List<Participant> participantList = Arrays.stream(participants).toList();
        modelPart.setAll(participantList.stream().map(ParticipantDto::new).toList());
        selectedParticipants = participantList.stream().map(ParticipantDto::new).toList();
    }

    public void initializer() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        motorColumn2.setCellValueFactory(new PropertyValueFactory<>("motor"));
        teamColumn.setCellValueFactory(new PropertyValueFactory<>("team"));
        raceNrColumn.setCellValueFactory(new PropertyValueFactory<>("race_id"));
        participantsTable.setItems(modelPart);
    }

    @FXML
    private void searchByTeam() throws ProjectException {
        String teamName = teamSearchField.getText().trim().toLowerCase();
        Participant participant = new Participant(teamName);

        Participant[] participants = server.getParticipantsForTeam(participant,this);
        List<Participant> participantList = Arrays.stream(participants).toList();
        modelPart.setAll(participantList.stream().map(ParticipantDto::new).toList());
        selectedParticipants = participantList.stream().map(ParticipantDto::new).toList();
    }

    private boolean checkString(String s){
        return s != null && !s.isEmpty();
    }

    @FXML
    public void addParticipant(ActionEvent actionEvent) {
        RacesDto selectedRace = RacesTable.getSelectionModel().getSelectedItem();

        String name=participantName.getText();
        String motor=participantMotor.getText();
        String team=participantTeam.getText();

        if (selectedRace == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No race selected");
            alert.setContentText("Please select a race to add participant");
            alert.showAndWait();
            return;
        }

        int id = server.getLowestId(this);
        Participant participant = new Participant(id,selectedRace.getId(),name, motor,team);

        try {
            server.addParticipant(participant,this);
        } catch (ProjectException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add failed");
            alert.setContentText("Add failed");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Add successful");
        alert.setContentText("Add successful");
        alert.showAndWait();
    }

    @FXML
    public void clearFieldsParticipant(ActionEvent actionEvent) {
        participantName.setText("");
        participantMotor.setText("");
        participantTeam.setText("");
    }

    private void showNotification(String message, Alert.AlertType type){
        Alert alert=new Alert(type);
        alert.setTitle("Notification");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void logout1(ActionEvent mouseEvent) {
        try {
            server.logout(loggedOrganizer, this);
            closeWindow();
        } catch (ProjectException e) {
            System.out.println("Logout error " + e);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
    }

    public void logout() {
        try {
            server.logout(loggedOrganizer, this);
            closeWindow();
        } catch (ProjectException e) {
            System.out.println("Logout error " + e);
        }
    }

    public void clearButtonClicked() throws ProjectException {
        initializeFields();
        populateTable();
        initializer();
        populateTablePart();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void ticketBuyed() throws ProjectException {

    }
}