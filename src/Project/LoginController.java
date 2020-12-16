package Project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button cancelButtonLogin;
    @FXML
    private Button loginButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextFieldLogin;
    @FXML
    private PasswordField passwordTextFieldLogin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
    }

    public void createUser(ActionEvent event) throws IOException {
        CreateUserController.openWindow();
    }

    public void loginButtonOnAction(ActionEvent event) throws Exception {
        if (!usernameTextFieldLogin.getText().equals("")&&!passwordTextFieldLogin.getText().equals("")) {
            String username = usernameTextFieldLogin.getText();
            String password = passwordTextFieldLogin.getText();
            File f = new File("database/"+username+"/"+username+".encrypted");
            if (f.exists() && !f.isDirectory()) {
                if (AESFile.decrypt(username,password).equals("Decrypted")) {
                    InfoController.openWindow(username,password);
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.close();
                }
                else {
                    loginMessageLabel.setText("Wrong password!");
                }
            } else {
                loginMessageLabel.setText("That username does not exist or password is incorrect");
            }
        }else{
            loginMessageLabel.setText("Enter Username and Password.");
        }
    }

    public void cancelButtonOnAction(ActionEvent event){
        Stage stage = (Stage) cancelButtonLogin.getScene().getWindow();
        stage.close();
    }



}
