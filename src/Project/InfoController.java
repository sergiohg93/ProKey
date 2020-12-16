package Project;

import java.io.FileReader;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;

import java.util.*;

public class InfoController implements Initializable {
    private static List<String[]> data;
    private static String username;
    private static String password;

    @FXML
    private ListView<String> itemsListView;
    @FXML
    private TextField usernameTextField, passwordTextField, nameTextField;
    @FXML
    private TextArea notesTextField;
    @FXML
    private Button lockButton, saveButton, cancelButton, deleteButton, informationEditButton, genPassButton;
    @FXML
    private ProgressBar passwordIndicatorBar;
    @FXML
    private Label passwordStrengthLabel, numOfCharLabel;
    @FXML
    private Slider passGenSlider;
    @FXML
    private CheckBox includeUppercaseBox, includeDigitBox, includeSymbolsBox;
    @FXML
    private MenuButton settingsButton;


    int buttonPressed = -1;
    int selectedItem = 0;

    public void handleMouseClick(javafx.scene.input.MouseEvent mouseEvent) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i)[0].equals(itemsListView.getSelectionModel().getSelectedItem())){
                selectedItem = i;
                setData(i);
                informationEditButton.setDisable(false);
                setPasswordBar();
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            data = readData("database/test/test.csv");
        } catch (Exception e) {
            System.out.println("File empty!");
            e.printStackTrace();
        }
        if (data != null) {
            //System.out.println(Arrays.deepToString(data.toArray()));
            for (String[] datum : data) {
                itemsListView.getItems().add(datum[0]);
            }
        }else {
            System.out.println("data null");
        }

        settingsButton.getItems().clear();
        MenuItem m1 = new MenuItem("Dark Theme");
        MenuItem m2 = new MenuItem("Light Theme");


        m1.setOnAction(event1);
        m2.setOnAction(event2);

        settingsButton.getItems().add(m1);
        settingsButton.getItems().add(m2);

    }

    EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e)
        {
            try {
                openWindow("DarkTheme.css");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    };
    EventHandler<ActionEvent> event2 = new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e)
        {
            try {
                openWindow("Style.css");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    };


    public static void openWindow(String user, String pass) throws IOException {
        FXMLLoader loader = new FXMLLoader(InfoController.class.getResource("Info.fxml"));
        Parent root = (Parent) loader.load();
        InfoController controller = (InfoController) loader.getController();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(InfoController.class.getResource("Style.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        Image image = new Image(new File("src/Project/Images/Logo2.png").toURI().toString());
        stage.getIcons().add(image);
        stage.setTitle("Pro Key");
        stage.setResizable(false);
        stage.show();
        username = user;
        password = pass;
    }

    public void reloadData(){
        itemsListView.getItems().clear();
        for (int i = 0; i < data.size(); i++) {
            itemsListView.getItems().add(data.get(i)[0]);
        }
    }

    public void setPasswordBar(){
        passwordIndicatorBar.setProgress(evaluatePassword(passwordTextField.getText()));
        if (passwordIndicatorBar.getProgress() < 0.33){
            passwordStrengthLabel.setText("Weak");
            passwordStrengthLabel.setTextFill(Color.rgb(210, 0, 0));
        }else if (passwordIndicatorBar.getProgress() < 0.71) {
            passwordStrengthLabel.setText("Moderate");
            passwordStrengthLabel.setTextFill(Color.rgb(0, 0, 210));
        }else if (passwordIndicatorBar.getProgress() <= 1) {
            passwordStrengthLabel.setText("Strong");
            passwordStrengthLabel.setTextFill(Color.rgb(0, 210, 0));
        }
    }

    public void setData(int i){
        try {
            nameTextField.setText(data.get(i)[0]);
        }catch (ArrayIndexOutOfBoundsException e){
            nameTextField.setText("");
        }
        try {
            usernameTextField.setText(data.get(i)[1]);
        }catch (ArrayIndexOutOfBoundsException e){
            usernameTextField.setText("");
        }
        try {
            passwordTextField.setText(data.get(i)[2]);
        }catch (ArrayIndexOutOfBoundsException e){
            passwordTextField.setText("");
        }
        try {
            notesTextField.setText(data.get(i)[3]);
        }catch (ArrayIndexOutOfBoundsException e){
            notesTextField.setText("");
        }
        setPasswordBar();
    }

    public void setEnable(){
        nameTextField.setEditable(true);
        usernameTextField.setEditable(true);
        passwordTextField.setEditable(true);
        notesTextField.setEditable(true);
        saveButton.setDisable(false);
        cancelButton.setDisable(false);
        genPassButton.setDisable(false);

    }

    public void setDisable(){
        nameTextField.setEditable(false);
        usernameTextField.setEditable(false);
        passwordTextField.setEditable(false);
        notesTextField.setEditable(false);
        saveButton.setDisable(true);
        cancelButton.setDisable(true);
        deleteButton.setVisible(false);
        genPassButton.setDisable(true);
    }

    public void clearFields(){
        nameTextField.setText("");
        usernameTextField.setText("");
        passwordTextField.setText("");
        notesTextField.setText("");
        passwordIndicatorBar.setProgress(-1);
        passwordStrengthLabel.setText("");
    }

    public void cancelButtonAction(ActionEvent event) throws Exception{
        setDisable();
        reloadData();
        itemsListView.getSelectionModel().select(selectedItem);
        setData(selectedItem);
    }

    public void deleteButtonAction(ActionEvent event) throws Exception{
        data.remove(selectedItem);
        reloadData();
        clearFields();
        setDisable();
        informationEditButton.setDisable(true);
    }

    public void saveButtonAction(ActionEvent event) throws Exception {
        setDisable();
        if (buttonPressed == 1) {
            String[] newItem = {nameTextField.getText(), usernameTextField.getText(), passwordTextField.getText(), notesTextField.getText()};
            data.add(newItem);
            reloadData();
            itemsListView.requestFocus();
            itemsListView.getSelectionModel().select(data.size()-1);
            passwordIndicatorBar.setProgress(evaluatePassword(passwordTextField.getText()));
        }
        if (buttonPressed == 2){
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i)[0].equals(itemsListView.getSelectionModel().getSelectedItem())) {
                    try {
                        data.get(i)[0] = nameTextField.getText();
                    }catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("Nothing to save in the name field");
                    }
                    try {
                        data.get(i)[1] = usernameTextField.getText();
                    }catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Nothing to save in the username field");
                    }
                    try {
                        data.get(i)[2] = passwordTextField.getText();
                    }catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("Nothing to save in the password field");
                    }
                    try {
                        data.get(i)[3] = notesTextField.getText();
                    }catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("Nothing to save in the notes field");
                    }
                }
            }
            reloadData();
            itemsListView.requestFocus();
            itemsListView.getSelectionModel().select(selectedItem);
            setPasswordBar();
        }
    }

    public void ItemAddButton(ActionEvent event) throws Exception {
        buttonPressed = 1;
        clearFields();
        setEnable();
        nameTextField.requestFocus();
    }

    public void informationEditButton(ActionEvent event) throws Exception {
        buttonPressed = 2;
        setEnable();
        deleteButton.setVisible(true);
    }

    public void lockButton(ActionEvent event) throws Exception {
        writeData(data);
        AESFile.encrypt(username, password);
        Stage stage = (Stage) lockButton.getScene().getWindow();
        stage.close();
    }

    public double evaluatePassword(String itemPassword){

        boolean hasNumber = false , hasSymbol = false;

        for (int i = 0; i < itemPassword.length(); i++){
            if (itemPassword.charAt(i) >= (char)48 && itemPassword.charAt(i) <= (char)57)
                hasNumber = true;
            else if (itemPassword.charAt(i) >= (char)33 && itemPassword.charAt(i) <= (char)47)
                hasSymbol = true;
            else if (itemPassword.charAt(i) >= (char)58 && itemPassword.charAt(i) <= (char)64)
                hasSymbol = true;
            else if (itemPassword.charAt(i) >= (char)91 && itemPassword.charAt(i) <= (char)96)
                hasSymbol = true;
        }
        if (itemPassword.equals(""))
            return 0;

        if (itemPassword.length() >= 9 && !itemPassword.equals(itemPassword.toLowerCase()) && hasSymbol)
            return 1;
        else if (itemPassword.length() >= 9 && !itemPassword.equals(itemPassword.toLowerCase()) && hasNumber)
            return 0.85;
        else if (itemPassword.length() >= 9 && !itemPassword.equals(itemPassword.toLowerCase()))
            return 0.70;
        else if (itemPassword.length() >= 9)
            return 0.56;
        else if (itemPassword.length() <= 8 && !itemPassword.equals(itemPassword.toLowerCase()))
            return 0.28;
        else if (itemPassword.length() <= 8)
            return 0.14;
        else
            return 0.0;

    }

    private String passwordGenerator(double length, boolean useUpper, boolean useDigits, boolean useSymbols){

        final String lower = "abcdefghijklmnopqrstuvwxyz";
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String digits = "0123456789";
        final String symbols = "!@#$%&*()_+-=[]|,./?><";

        StringBuilder password = new StringBuilder((int)length);
        Random random = new Random(System.nanoTime());

        List<String> charCategories = new ArrayList<>(4);
        charCategories.add(lower);
        if (useUpper) {
            charCategories.add(upper);
        }
        if (useDigits) {
            charCategories.add(digits);
        }
        if (useSymbols) {
            charCategories.add(symbols);
        }

        // Build the password.
        for (int i = 0; i < length; i++) {
            String charCategory = charCategories.get(random.nextInt(charCategories.size()));
            int position = random.nextInt(charCategory.length());
            password.append(charCategory.charAt(position));
        }
        return new String(password);
    }

    public void openWindow(String Stylesheet) throws Exception {
        FXMLLoader loader = new FXMLLoader(InfoController.class.getResource("Info.fxml"));
        Parent root = (Parent) loader.load();
        InfoController controller = (InfoController) loader.getController();
        Scene scene = new Scene(root);
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource(Stylesheet).toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        Image image = new Image(new File("src/Project/Images/Logo2.png").toURI().toString());
        stage.getIcons().add(image);
        stage.setTitle("Pro Key");
        stage.setResizable(false);
        writeData(data);
        Stage stage1 = (Stage) lockButton.getScene().getWindow();
        stage1.close();
        stage.show();
    }


    public void GeneratePasswordAction(ActionEvent event){
        passwordTextField.setText(passwordGenerator(passGenSlider.getValue(),includeUppercaseBox.isSelected(),includeDigitBox.isSelected(),includeSymbolsBox.isSelected()));
    }

    public void passLengthDragAction(){
        numOfCharLabel.setText(String.valueOf((int)passGenSlider.getValue()));
    }

    public List<String[]> readData(String path) throws IOException {
        BufferedReader csvReader = new BufferedReader(new FileReader(path));
        String row;
        List<String[]> data = new ArrayList<>();
        while ((row = csvReader.readLine()) != null) {
            data.add(row.split(","));
        }
        csvReader.close();
        return data;
    }

    public void writeData(List<String[]> stringArray) throws Exception {
        FileWriter writer = new FileWriter("database/"+username+"/"+username+".csv");
        for (String[] i : stringArray){
            for (String x :  i){
                writer.write(String.valueOf(x)+",");
            }
            writer.write("\n");
        }
        writer.flush();
        writer.close();
    }

}