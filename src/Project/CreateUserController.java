package Project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import java.io.File;
import java.io.IOException;

public class CreateUserController implements Initializable {

    @FXML
    private Button cancelButton;
    @FXML
    private Button acceptButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private PasswordField repasswordTextField;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passLabel;
    @FXML
    private Label repassLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public static void openWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(CreateUserController.class.getResource("CreateUser.fxml"));
        Parent root = (Parent) loader.load();
        CreateUserController controller = (CreateUserController) loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        Image image = new Image(new File("Images/Logo2.png").toURI().toString());
        stage.getIcons().add(image);
        stage.setTitle("Create New User");
        stage.setResizable(false);
        stage.show();
    }
    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void acceptButtonOnAction(ActionEvent event) throws Exception {
        if (usernameTextField.getText().equals("")){
            usernameLabel.setText("This field can not be empty.");
        }else if(passwordTextField.getText().equals("")){
            usernameLabel.setText("");
            passLabel.setText("This field can not be empty.");
        }else if(!repasswordTextField.getText().equals(passwordTextField.getText())){
            passLabel.setText("");
            repassLabel.setText("Password do not match!");
        }else{
            createFile(usernameTextField.getText(), passwordTextField.getText());
        }
    }

    public void createFile(String name, String pass) throws Exception {
        try {
            Path path = Paths.get("database/"+name);
            if (Files.exists(path)){
                System.out.println("This user already exists.");
            }else{
                File folder = new File("database/"+name);
                if (folder.mkdir()){
                    File myObj = new File("database/"+name+"/"+name+".csv");
                    if (myObj.createNewFile()) {
                        System.out.println("Database created: " + myObj.getName());

                        AESFile.encrypt(name, pass);

                        Stage stage = (Stage) acceptButton.getScene().getWindow();
                        stage.close();
                    }
                }

            }

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}
