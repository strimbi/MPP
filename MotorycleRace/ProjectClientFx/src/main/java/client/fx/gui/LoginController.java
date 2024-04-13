package client.fx.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.model.Organizer;
import project.services.IProjectServices;
import project.services.ProjectException;

public class LoginController {
    private IProjectServices server;
    private MainViewController projectCtrl;
    private Organizer crtOrganizer;
    public Label addedText;
    public TextField emailText;
    public PasswordField passwordText;
    Parent mainParent;

    public void setProjectCtrl(MainViewController projectCtrl) {
        this.projectCtrl = projectCtrl;
    }

    public void setServer(IProjectServices server) {
        this.server = server;
    }

    public void setMainParent(Parent mainParent) {
        this.mainParent = mainParent;
    }


    public void onLoginButtonClick() {
        String username = emailText.getText();
        String password = passwordText.getText();
        crtOrganizer = new Organizer(username, "", password);

        try {
            server.login(crtOrganizer, projectCtrl);
            projectCtrl.setLoggedOrganizer(crtOrganizer);
            Stage stage = new Stage();
            //stage.setTitle("Main view for " + crtOrganizer.getID());
            stage.setScene(new Scene(mainParent));

            stage.setOnCloseRequest(event -> {
                projectCtrl.logout();
                System.exit(0);
            });
            stage.show();
            projectCtrl.clearButtonClicked();
            closeWindow();
            //login.this.dispose();

        } catch (ProjectException e) {
            addedText.setText("Credentials are invalid");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) addedText.getScene().getWindow();
        stage.close();
    }
}