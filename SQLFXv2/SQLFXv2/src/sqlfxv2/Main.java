package sqlfxv2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class Main extends Application {

    private static final String DB_URL = "jdbc:sqlite:C:/Users/shawn/UserInformation.db";

    private enum Mode {
        LOGIN, REGISTER
    }

    private Mode mode;

    private TextField usernameField;
    private PasswordField passwordField;
    private String id;

    public static void main(String[] args) {
        launch(args);
    }

    private void showInformationForm(String username, String password, String email) {
        Information infoForm = new Information(username, password, email);
        infoForm.showForm();
    }

    //Creates the UI For Log in or Register
    @Override
    public void start(Stage stage) throws Exception {
        Label titleLabel = new Label("Information System Management");

        Button loginButton = new Button("Log in");
        Button registerButton = new Button("Register");

        VBox vbox = new VBox();
        vbox.getChildren().addAll(titleLabel, loginButton, registerButton);

        Scene scene = new Scene(vbox, 300, 200);
        stage.setScene(scene);
        stage.show();

        loginButton.setOnAction(e -> {
            mode = Mode.LOGIN;
            showLoginForm();
        });

        registerButton.setOnAction(e -> {
            mode = Mode.REGISTER;
            showRegistrationForm();
        });
    }

    //Checking the Login to move into Information UI
    private void handleLogin() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String username = usernameField.getText();
            String password = passwordField.getText();

            String selectQuery = "SELECT * FROM accounts WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login Successful");
                alert.setContentText("Welcome, " + username + "!");
                alert.showAndWait();

                showInformationForm(resultSet.getString("username"), resultSet.getString("password"), resultSet.getString("email"));
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setContentText("Invalid username or password.");
                alert.showAndWait();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }



    //JavaFX to show the Login UI
    private void showLoginForm() {
        Label usernameLabel = new Label("Username:");
        usernameField = new TextField(); // assign the value to the instance variable

        Label passwordLabel = new Label("Password:");
        passwordField = new PasswordField(); // assign the value to the instance variable

        Button submitButton = new Button("Submit");

        VBox vbox = new VBox();
        vbox.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, submitButton);

        Scene scene = new Scene(vbox, 300, 200);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        submitButton.setOnAction(e -> {
            try {
                Connection connection = DriverManager.getConnection(DB_URL);

                String username = usernameField.getText();
                String password = passwordField.getText();

                String selectQuery = "SELECT * FROM accounts WHERE username = ? AND password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Login Success");
                    alert.setContentText("You have successfully logged in.");
                    alert.showAndWait();
                    handleLogin();
                    stage.close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Login Failed");
                    alert.setContentText("Invalid username or password.");
                    alert.showAndWait();
                }

                connection.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }

    private void showRegistrationForm() {
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Button submitButton = new Button("Submit");

        VBox vbox = new VBox();
        vbox.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, emailLabel, emailField, submitButton);

        Scene scene = new Scene(vbox, 300, 200);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        submitButton.setOnAction(e -> {
            try {
                Connection connection = DriverManager.getConnection(DB_URL);

                String username = usernameField.getText();
                String password = passwordField.getText();
                String email = emailField.getText();

                String insertQuery = "INSERT INTO accounts (username, password, email) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                int result = preparedStatement.executeUpdate();

                if (result == 1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Registration Success");
                    alert.setContentText("You have successfully registered.");
                    alert.showAndWait();
                    stage.close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Registration Failed");
                    alert.setContentText("Registration failed. Please try again later.");
                    alert.showAndWait();
                }

                connection.close();
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }
}

