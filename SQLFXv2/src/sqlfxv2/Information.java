package sqlfxv2;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.*;

public class Information {

    private static final String DB_URL = "jdbc:sqlite:C:/Users/shawn/UserInformation.db";


    private String firstName;
    private String lastName;
    private String studentID;
    private String username;
    private String password;
    private String email;


    public Information(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;

    }

    public void showForm() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(false);

            // Query the Accounts table for the logged-in user's accountID
            String selectAccountQuery = "SELECT id FROM accounts WHERE username = ?";
            PreparedStatement selectAccountStatement = connection.prepareStatement(selectAccountQuery);
            selectAccountStatement.setString(1, username);
            ResultSet accountResultSet = selectAccountStatement.executeQuery();

            if (accountResultSet.next()) {
                int accountID = accountResultSet.getInt("id");

                // Query the Students table for the student's information
                String selectStudentQuery = "SELECT * FROM Students WHERE accountID = ?";
                PreparedStatement selectStudentStatement = connection.prepareStatement(selectStudentQuery);
                selectStudentStatement.setInt(1, accountID);
                ResultSet studentResultSet = selectStudentStatement.executeQuery();

                VBox vbox = new VBox();

                if (studentResultSet.next()) {
                    // Information already exists, show it to the user
                    String firstName = studentResultSet.getString("firstName");
                    String lastName = studentResultSet.getString("lastName");
                    String studentID = studentResultSet.getString("studentID");

                    Label firstNameLabel = new Label("First Name:");
                    TextField firstNameField = new TextField(firstName); // reuse the existing TextField
                    firstNameField.setEditable(false); // make the TextField read-only

                    Label lastNameLabel = new Label("Last Name:");
                    TextField lastNameField = new TextField(lastName); // reuse the existing TextField
                    lastNameField.setEditable(false); // make the TextField read-only

                    Label studentIDLabel = new Label("Student ID:");
                    TextField studentIDField = new TextField(studentID); // reuse the existing TextField
                    studentIDField.setEditable(false); // make the TextField read-only

                    vbox.getChildren().addAll(firstNameLabel, firstNameField, lastNameLabel, lastNameField, studentIDLabel, studentIDField);
                } else {
                    // Information does not exist, prompt user to enter it
                    Label firstNameLabel = new Label("First Name:");
                    TextField firstNameField = new TextField();
                    firstNameField.setEditable(true);

                    Label lastNameLabel = new Label("Last Name:");
                    TextField lastNameField = new TextField();
                    lastNameField.setEditable(true);

                    Label studentIDLabel = new Label("Student ID:");
                    TextField studentIDField = new TextField();
                    studentIDField.setEditable(true);

                    // Save button to save the entered information to the database
                    Button saveButton = new Button("Save");
                    saveButton.setOnAction(event -> {
                        try {
                            // Check if information already exists in the database
                            String selectQuery = "SELECT * FROM Students WHERE studentID = ? AND firstName = ? AND lastName = ?";
                            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                            preparedStatement.setString(1, studentIDField.getText());
                            preparedStatement.setString(2, firstNameField.getText());
                            preparedStatement.setString(3, lastNameField.getText());
                            ResultSet resultSet = preparedStatement.executeQuery();

                            if (resultSet.next()) {
                                // Information already exists, update it in the database
                                String updateQuery = "UPDATE Students SET firstName = ?, lastName = ? WHERE studentID = ?";
                                preparedStatement = connection.prepareStatement(updateQuery);
                                preparedStatement.setString(1, firstNameField.getText());
                                preparedStatement.setString(2, lastNameField.getText());
                                preparedStatement.setString(3, studentIDField.getText());
                                preparedStatement.executeUpdate();
                            } else {
                                // Information does not exist, insert it into the database
                                String insertQuery = "INSERT INTO Students (firstName, lastName, studentID, accountID) VALUES (?, ?, ?, ?)";
                                preparedStatement = connection.prepareStatement(insertQuery);
                                preparedStatement.setString(1, firstNameField.getText());
                                preparedStatement.setString(2, lastNameField.getText());
                                preparedStatement.setString(3, studentIDField.getText());
                                preparedStatement.setInt(4, accountID);
                                preparedStatement.executeUpdate();
                            }

                            // Clear the text fields after the information has been saved
                            firstNameField.setText("");
                            lastNameField.setText("");
                            studentIDField.setText("");


                            connection.commit();
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });

                    vbox.getChildren().addAll(firstNameLabel, firstNameField, lastNameLabel, lastNameField, studentIDLabel, studentIDField, saveButton);

                }

                Scene scene = new Scene(vbox, 400, 300);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}