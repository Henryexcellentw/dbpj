package UI;

import Entity.User;
import InterfaceImplementation.UserInterface;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.SQLException;

import static UI.AdministratorPage.showAdministratorPage;
import static UI.ShopOwnerPage.showShopOwnerPage;
import static UI.UserPage.showUserPage;

public class LoginPage {
    public static void LoginPage(Stage stage){
        Label labelId = new Label("ID:");
        Label labelPassword = new Label("Password:");

        User user = new User();

        // 创建文本框和密码框
        TextField textFieldId = new TextField();
        PasswordField passwordField = new PasswordField();

        // 创建登录按钮
        Button loginButton = new Button("Login");
        String[] UserType = {"用户","商家","管理员"};
        loginButton.setOnAction(e -> {
            try {
                int result = UserInterface.login(textFieldId.getText(), passwordField.getText(), user);
                if(result==-1){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("错误");
                    alert.setHeaderText("登录失败");
                    alert.setContentText("请检查您输入的用户id或密码");
                    alert.show();
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("成功");
                    alert.setHeaderText("登录成功");
                    alert.setContentText("欢迎您，"+user.getName()+",您的身份是"+UserType[user.getRole()]);
                    alert.show();
                    switch(user.getRole()){
                        case 2:showAdministratorPage(stage);break;
                        case 0:showUserPage(stage,user);break;
                        case 1:
                            ShopOwnerPage shopOwnerPage = new ShopOwnerPage(user);
                            shopOwnerPage.showShopOwnerPage(stage);
                            break;
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        // 使用 GridPane 布局
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER); // 设置 GridPane 居中

        // 添加组件到布局
        gridPane.add(labelId, 0, 0);
        gridPane.add(textFieldId, 1, 0);
        gridPane.add(labelPassword, 0, 1);
        gridPane.add(passwordField, 1, 1);
        gridPane.add(loginButton, 1, 2);

        // 设置场景和舞台
        Scene scene = new Scene(gridPane, 300, 200);
        stage.setScene(scene);
        stage.setTitle("数据库Pj登录界面");
        stage.show();
    }
}
