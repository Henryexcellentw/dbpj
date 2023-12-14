package UI;

import Entity.Shop;
import Entity.User;
import InterfaceImplementation.ShopInterface;
import InterfaceImplementation.UserInterface;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

import java.util.ArrayList;

/**
 * @Description：
 * @Author Huangzisu
 * @date 2023-12-07
 **/
public class ShopOwnerPage {
    private static User user;
    public ShopOwnerPage(User user) {
        this.user = user;
//        this.user = UserInterface.getUserById(10000002);
    }
    public static void showShopOwnerPage(Stage stage) {
        // 创建布局
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);

        // 添加组件
        Utils.addButton(gridPane, "查看拥有商店信息", () -> showShopInfoPopup(stage));

        // 设置场景和舞台
        Scene scene = new Scene(gridPane, 300, 200);
        stage.setScene(scene);
        stage.setTitle("商家界面");
        stage.show();
    }

    public static void showShopInfoPopup(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("商家信息");
        alert.setHeaderText("商家信息");

        // 获取商家信息
        ArrayList<Shop> shops = ShopInterface.getShopsByOwnerId(user.getId());
        if (shops.size() == 0) {
            alert.setContentText("您还没有商家");
            alert.showAndWait();
            return;
        }

        // 创建选择商店的 ChoiceBox
        ChoiceBox<Shop> shopChoiceBox = new ChoiceBox<>();
        shopChoiceBox.getItems().addAll(shops);
        shopChoiceBox.setConverter(new StringConverter<Shop>() {
            @Override
            public String toString(Shop shop) {
                return shop.getName(); // 显示商店的名称
            }

            @Override
            public Shop fromString(String string) {
                return null;
            }
        });
        shopChoiceBox.getSelectionModel().selectFirst(); // 默认选择第一个商店

        // 显示商家信息
        StringProperty contentText = new SimpleStringProperty();
        contentText.set("选择商家: \n" +
                "ID: " + shopChoiceBox.getValue().getId() + "\n" +
                "商家名称: " + shopChoiceBox.getValue().getName() + "\n" +
                "地址: " + shopChoiceBox.getValue().getAddress() + "\n" +
                "拥有者 ID: " + shopChoiceBox.getValue().getOwnerId() + "\n\n");

        // 当用户选择不同商店时更新内容
        shopChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // 清空之前的内容
            contentText.set("选择商家: \n" +
                    "ID: " + newValue.getId() + "\n" +
                    "商家名称: " + newValue.getName() + "\n" +
                    "地址: " + newValue.getAddress() + "\n" +
                    "拥有者 ID: " + newValue.getOwnerId() + "\n\n");
        });


        // 将 ChoiceBox 添加到布局中
        VBox vBox = new VBox();

        // 使用Text组件，将其textProperty绑定到contentText
        Text textContent = new Text();
        textContent.textProperty().bind(contentText);
        vBox.getChildren().addAll(shopChoiceBox, textContent);

        // 添加按钮“进入商店管理”
        Button enterShopManagementButton = new Button("进入商店管理");
        enterShopManagementButton.setOnAction(event -> {
            Shop selectedShop = shopChoiceBox.getValue();
            if (selectedShop != null) {
                ShopManagementPage shopManagementPage = new ShopManagementPage(selectedShop);
                shopManagementPage.showShopManagementPage(selectedShop, stage);
            }
        });
        vBox.getChildren().add(enterShopManagementButton);

        alert.getDialogPane().setContent(vBox);


        alert.showAndWait();
    }
}
