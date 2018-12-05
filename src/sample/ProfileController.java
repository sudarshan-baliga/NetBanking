package sample;

import com.mysql.cj.util.StringUtils;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import sample.Db;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import sample.Db;

import java.io.IOException;

public class ProfileController {
    @FXML
    private Label hmFname, hmLname, hmAccNum, hmbranch, hmIfsc, currentBalance, currentTime, sendMoneyBal, errSendMoney, sussMsgSendMoney;
    @FXML
    private TextField sendAccNo, sendAmount;
    @FXML
    private VBox transTable;

    @FXML
    private void initialize() throws IOException {
        configureProfileTab();
    }

    private void hideAllTabs(MouseEvent event) {
        Scene scene = ((Control) event.getSource()).getScene();
        //i couldnt find how to hide elements with class so bruteforcing
        scene.lookup("#profileTab").setVisible(false);
        scene.lookup("#balanceTab").setVisible(false);
        scene.lookup("#sendMoneyTab").setVisible(false);
        scene.lookup("#transactionsTab").setVisible(false);
    }

    private void configureProfileTab() {
        hmFname.setText(Db.fname);
        hmLname.setText(Db.lname);
        hmAccNum.setText(Db.accNum);
        hmbranch.setText(Db.blocation);
        hmIfsc.setText(Db.ifsc);
    }

    private void configureBalanceTab() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        currentBalance.setText("Rs." + Db.balance);
        currentTime.setText(dtf.format(LocalDateTime.now()));
    }

    private void configureSendMoneyTab() {
        sendMoneyBal.setText(Db.getBalance());
    }

    private void configureTransactionTab() {
        String transactions[] = Db.getTransactions();
        Label label[] = new Label[10];
        transTable.getChildren().clear();

        for(int i = 0; i < transactions.length; i++){
            label[i] = new Label(transactions[i]);
            label[i].getStyleClass().add("transaction");
            transTable.getChildren().add((label[i]));
        }
    }

    public void hanldleTabClick(MouseEvent event) throws IOException {
        String id = ((Control) event.getSource()).getId();
        Scene scene = ((Control) event.getSource()).getScene();
        switch (id) {
            case "profileLabel":
                hideAllTabs(event);
                Node tab = scene.lookup("#profileTab");
                configureProfileTab();
                tab.setVisible(true);
                break;
            case "balanceLabel":
                hideAllTabs(event);
                Node tab2 = scene.lookup("#balanceTab");
                configureBalanceTab();
                tab2.setVisible(true);
                break;
            case "sendMoneyLabel":
                hideAllTabs(event);
                Node tab3 = scene.lookup("#sendMoneyTab");
                configureSendMoneyTab();
                tab3.setVisible(true);
                break;
            case "transactionsLabel":
                hideAllTabs(event);
                Node tab4 = scene.lookup("#transactionsTab");
                configureTransactionTab();
                tab4.setVisible(true);
                break;
            default:
                System.out.println("lable not found executing default");
                break;
        }
    }

    public void handleLogout(ActionEvent event) throws IOException {
        Parent loadScene = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene profileScene = new Scene(loadScene);
        //add the css file
        profileScene.getStylesheets().add(getClass().getResource("../Styles/main.css").toExternalForm());
        //get the stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(profileScene);
        window.show();
    }

    public void handleSendMoney() {
        int result;
        result = Db.sendMoney(sendAccNo.getText(), sendAmount.getText());
        System.out.println(result);
        switch (result) {
            case 1:
                configureSendMoneyTab();
                errSendMoney.setText("");
                sussMsgSendMoney.setText("Success");
                break;
            case 2:
                sussMsgSendMoney.setText("");
                errSendMoney.setText("Not enough balance");
                break;
            case 3:
                sussMsgSendMoney.setText("");
                errSendMoney.setText("You cannot use your'e own account number");
                break;
            case 4:
                sussMsgSendMoney.setText("");
                errSendMoney.setText("User not found");
                break;
            case 5:
                sussMsgSendMoney.setText("");
                errSendMoney.setText("Enter valid amount");
                break;
            default:
                sussMsgSendMoney.setText("");
                errSendMoney.setText("Internal error");
        }
    }
}

