package sqlfx;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        TextField passwordField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Button submitButton = new Button("Submit");

        VBox vbox = new VBox();
        vbox.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, emailLabel, emailField, submitButton);

        Scene scene = new Scene(vbox, 300, 200);
        stage.setScene(scene);
        stage.show();

        submitButton.setOnAction(e -> {
            try {
                Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/shawn/UserInformation.db");

                String username = usernameField.getText();
                String password = passwordField.getText();
                String email = emailField.getText();

                String insertQuery = "INSERT INTO accounts (username, password, email) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                preparedStatement.executeUpdate();

                connection.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }
}
