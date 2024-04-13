package client.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import client.fx.gui.*;
import project.networking.protocol.ProjectServicesProxy;
import project.services.IProjectServices;

import java.io.IOException;
import java.util.Properties;

public class StartClient extends Application {

    private static int defaultPort=55555;

    @Override
    public void start(Stage stage) throws Exception {
        Properties clientProperties = new Properties();

        try {
            clientProperties.load(StartClient.class.getResourceAsStream("/project_client.properties"));
        } catch (IOException e) {
            return;
        }

        String serverIP = clientProperties.getProperty("project.server.host");
        int chatServerPort=defaultPort;

        try {
            chatServerPort = Integer.parseInt(clientProperties.getProperty("project.server.port"));
        } catch (NumberFormatException e) {
            System.err.println("Wrong  Port Number"+ e.getMessage());
            System.err.println("Using default port "+defaultPort);
        }

        System.out.println("Using server on host: " + serverIP);
        System.out.println("Using server on port: " + chatServerPort);

        IProjectServices server = new ProjectServicesProxy(serverIP, chatServerPort);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = fxmlLoader.load();

        LoginController ctrl = fxmlLoader.getController();
        ctrl.setServer(server);

        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/mainView.fxml"));
        Parent mainRoot = mainLoader.load();

        MainViewController projectCtrl = mainLoader.getController();
        projectCtrl.setServer(server);

        ctrl.setProjectCtrl(projectCtrl);
        ctrl.setMainParent(mainRoot);

        Scene scene = new Scene(root);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}