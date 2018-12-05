package sample;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import sample.Db;

public class LoginController {
    @FXML
    private PasswordField passField;

    @FXML
    private TextField accNumField;

    @FXML
    private Label loginFailMsg;

    @FXML
    private void initialize() throws IOException{
        Db.connect();
    }

    public void hanldleLogin(ActionEvent event) throws IOException {
        //check if user exists in db
        if(Db.findUser(accNumField.getText(), passField.getText())) {
            Parent loadScene = FXMLLoader.load(getClass().getResource("Profile.fxml"));
            Scene profileScene = new Scene(loadScene);
            //add the css file
            profileScene.getStylesheets().add(getClass().getResource("../Styles/main.css").toExternalForm());
            //get the stage information
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(profileScene);
            window.show();
        }
        else
        {
            loginFailMsg.setVisible(true);
        }
    }
}
