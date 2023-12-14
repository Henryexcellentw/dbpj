package UI;

import Entity.DetailedCommodity;
import Entity.Platform;
import Entity.Shop;
import InterfaceImplementation.CommodityInterface;
import InterfaceImplementation.PlatformInterface;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * @Description：
 * @Author Huangzisu
 * @date 2023-12-07
 **/
public class ShopManagementPage {
    private Shop shop;
    public ShopManagementPage(Shop shop){
        this.shop = shop;
    }
    public void showShopManagementPage(Shop shop, Stage primaryStage) {
        // 创建商店管理页面的布局
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.setVgap(10);

        // 创建商店管理页面的场景
        Scene scene = new Scene(root, 300, 200);

        // 创建商店管理页面的舞台
        Stage shopManagementStage = new Stage();
        shopManagementStage.setTitle("商店管理页面 - " + shop.getName());
        shopManagementStage.setScene(scene);

        // 增加按钮
        Utils.addButton(root, "发布新商品", () -> showReleaseNewCommodityPopup(shopManagementStage));
        Utils.addButton(root, "管理商品", () -> showAllCommoditiesPopup(shopManagementStage));

        // 设置商店管理页面的 Modality 和 Owner
        shopManagementStage.initModality(Modality.APPLICATION_MODAL);
        shopManagementStage.initOwner(primaryStage);

        shopManagementStage.show();
    }

    public void showReleaseNewCommodityPopup(Stage primaryStage) {
        // 创建发布商品页面的布局
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.setVgap(10);

        // 添加标签和文本框/选择框
        Label nameLabel = new Label("商品名称:");
        TextField nameTextField = new TextField();

        Label categoryLabel = new Label("商品类别:");
        TextField categoryTextField = new TextField();

        Label originLabel = new Label("商品产地:");
        TextField originTextField = new TextField();

        Label descriptionLabel = new Label("商品描述:");
        TextField descriptionTextField = new TextField();

        Label produceDateLabel = new Label("生产日期:");
        DatePicker produceDatePicker = new DatePicker();

        Label priceLabel = new Label("商品价格:");
        TextField priceTextField = new TextField();

        Label platformLabel = new Label("选择平台:");
        ChoiceBox<Platform> platformChoiceBox = new ChoiceBox<>();

        // 获取所有平台
        ArrayList<Platform> platforms = PlatformInterface.getAllPlatforms();

        // 添加平台选项到ChoiceBox
        platformChoiceBox.getItems().addAll(platforms);
        platformChoiceBox.setConverter(new StringConverter<Platform>() {
            @Override
            public String toString(Platform platform) {
                return platform.getName();
            }

            @Override
            public Platform fromString(String string) {
                return null;
            }
        });
        platformChoiceBox.getSelectionModel().selectFirst(); // 默认选择第一个平台

        // 添加所有控件到布局中
        root.add(nameLabel, 0, 0);
        root.add(nameTextField, 1, 0);
        root.add(categoryLabel, 0, 1);
        root.add(categoryTextField, 1, 1);
        root.add(originLabel, 0, 2);
        root.add(originTextField, 1, 2);
        root.add(descriptionLabel, 0, 3);
        root.add(descriptionTextField, 1, 3);
        root.add(produceDateLabel, 0, 4);
        root.add(produceDatePicker, 1, 4);
        root.add(priceLabel, 0, 5);
        root.add(priceTextField, 1, 5);
        root.add(platformLabel, 0, 6);
        root.add(platformChoiceBox, 1, 6);

        // 创建发布商品页面的场景
        Scene scene = new Scene(root, 400, 300);

        // 创建发布商品页面的舞台
        Stage releaseCommodityStage = new Stage();
        releaseCommodityStage.setTitle("发布新商品");
        releaseCommodityStage.setScene(scene);

        // 设置发布商品页面的 Modality 和 Owner
        releaseCommodityStage.initModality(Modality.APPLICATION_MODAL);
        releaseCommodityStage.initOwner(primaryStage);

        // 添加确认按钮
        Button confirmButton = new Button("确认发布");
        confirmButton.setOnAction(event -> {
            // 获取用户输入的信息
            String name = nameTextField.getText();
            String category = categoryTextField.getText();
            String origin = originTextField.getText();
            String description = descriptionTextField.getText();
            String produceDate = produceDatePicker.getValue().toString();
            Platform selectedPlatform = platformChoiceBox.getValue();
            Integer platformId = selectedPlatform.getId();
            String priceText = priceTextField.getText();
            try {
                // 尝试将输入的字符串转换为数字
                Double test = Double.parseDouble(priceText);
                // 检查价格是否为正数
                if (test < 0) {
                    Utils.alertDoubleInput(primaryStage);
                }
            } catch (NumberFormatException e) {
                Utils.alertDoubleInput(primaryStage);
            }
            Double newPrice = Double.parseDouble(priceText);

            // 调用发布商品的函数
            Integer result = CommodityInterface.releaseNewCommodity(name, category, description, produceDate, origin, shop.getId(), newPrice, platformId);

            Utils.alertReleaseResult(releaseCommodityStage, result);

            // 关闭发布商品页面
            releaseCommodityStage.close();
        });

        // 将确认按钮添加到布局中
        root.add(confirmButton, 1, 7);

        // 显示发布商品页面
        releaseCommodityStage.showAndWait();
    }

    public void showAllCommoditiesPopup(Stage primaryStage) {
        // 获取当前商店下的所有商品
        ArrayList<DetailedCommodity> detailedCommodities = CommodityInterface.getAllCommoditiesByShopId(shop.getId());

        // 创建表格形式的布局
        TableView<DetailedCommodity> tableView = new TableView<>();

        // 创建表格的列
        TableColumn<DetailedCommodity, String> nameColumn = new TableColumn<>("名称");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<DetailedCommodity, String> categoryColumn = new TableColumn<>("类别");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<DetailedCommodity, String> originColumn = new TableColumn<>("产地");
        originColumn.setCellValueFactory(new PropertyValueFactory<>("origin"));

        TableColumn<DetailedCommodity, String> descriptionColumn = new TableColumn<>("描述");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<DetailedCommodity, String> produceDateColumn = new TableColumn<>("生产日期");
        produceDateColumn.setCellValueFactory(new PropertyValueFactory<>("productionDate"));

        TableColumn<DetailedCommodity, Integer> priceColumn = new TableColumn<>("价格");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<DetailedCommodity, String> platformColumn = new TableColumn<>("平台");
        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platform"));

        // 将列添加到表格
        tableView.getColumns().addAll(nameColumn, categoryColumn, originColumn,
                descriptionColumn, produceDateColumn, priceColumn, platformColumn);

        // 将商品数据添加到表格
        tableView.getItems().addAll(detailedCommodities);

        // 创建弹出窗口
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);

        // 为表格的行设置双击事件
        tableView.setRowFactory(tv -> {
            TableRow<DetailedCommodity> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    // 获取选中的商品
                    DetailedCommodity selectedCommodity = row.getItem();

                    // 显示商品详细信息弹窗
                    showDetailedCommodityPopup(selectedCommodity);
                }
            });
            return row;
        });

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // 添加表格到布局
        vBox.getChildren().add(tableView);

        // 创建确认按钮
        Button confirmButton = new Button("确定");
        confirmButton.setOnAction(event -> popupStage.close());

        // 添加确认按钮到布局
        vBox.getChildren().add(confirmButton);

        // 设置布局到场景
        Scene scene = new Scene(vBox);
        popupStage.setScene(scene);

        // 设置弹出窗口标题
        popupStage.setTitle("所有商品");

        // 显示弹出窗口
        popupStage.showAndWait();
    }

    public void showDetailedCommodityPopup(DetailedCommodity selectedCommodity) {
        // 创建弹窗
        Stage detailedPopupStage = new Stage();
        detailedPopupStage.initModality(Modality.APPLICATION_MODAL);

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // 创建标签显示商品详细信息
        Label detailedInfoLabel = new Label("商品详细信息:\n" +
                "名称: " + selectedCommodity.getName() + "\n" +
                "类别: " + selectedCommodity.getCategory() + "\n" +
                "产地: " + selectedCommodity.getOrigin() + "\n" +
                "描述: " + selectedCommodity.getDescription() + "\n" +
                "生产日期: " + selectedCommodity.getProductionDate() + "\n" +
                "价格: " + selectedCommodity.getPrice() + "\n" +
                "平台: " + selectedCommodity.getPlatform());

        // 创建按钮，用于执行不同的操作
        Button modifyInfoButton = new Button("修改商品信息");
        modifyInfoButton.setOnAction(event -> {
            showModifyCommodityInfoPage(selectedCommodity, detailedPopupStage);
        });

        Button modifyPriceButton = new Button("修改商品价格");
        modifyPriceButton.setOnAction(event -> {
            showModifyCommodityPricePage(selectedCommodity, detailedPopupStage);
        });

        // 将标签和按钮添加到布局
        vBox.getChildren().addAll(detailedInfoLabel, modifyInfoButton, modifyPriceButton);

        // 创建场景和设置弹窗标题
        Scene detailedPopupScene = new Scene(vBox);
        detailedPopupStage.setScene(detailedPopupScene);
        detailedPopupStage.setTitle("商品详情");

        // 显示弹窗
        detailedPopupStage.showAndWait();
    }

    public static void showModifyCommodityInfoPage(DetailedCommodity selectedCommodity, Stage primaryStage) {
        // 创建修改商品信息页面的布局
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.setVgap(10);

        // 添加标签和文本框
        Label nameLabel = new Label("商品名称:");
        TextField nameTextField = new TextField(selectedCommodity.getName());

        Label categoryLabel = new Label("商品类别:");
        TextField categoryTextField = new TextField(selectedCommodity.getCategory());

        Label originLabel = new Label("商品产地:");
        TextField originTextField = new TextField(selectedCommodity.getOrigin());

        Label descriptionLabel = new Label("商品描述:");
        TextField descriptionTextField = new TextField(selectedCommodity.getDescription());

        Label productionDateLabel = new Label("商品生产日期:");
        DatePicker productionDatePicker = new DatePicker();
        productionDatePicker.setValue(LocalDate.parse(selectedCommodity.getProductionDate()));

        // 添加所有控件到布局中
        root.add(nameLabel, 0, 0);
        root.add(nameTextField, 1, 0);
        root.add(categoryLabel, 0, 1);
        root.add(categoryTextField, 1, 1);
        root.add(originLabel, 0, 2);
        root.add(originTextField, 1, 2);
        root.add(descriptionLabel, 0, 3);
        root.add(descriptionTextField, 1, 3);
        root.add(productionDateLabel, 0, 4);
        root.add(productionDatePicker, 1, 4);

        // 创建确认按钮
        Button confirmButton = new Button("确认修改");
        confirmButton.setOnAction(event -> {
            // 获取用户输入的修改信息
            String newName = nameTextField.getText();
            String newCategory = categoryTextField.getText();
            String newOrigin = originTextField.getText();
            String newDescription = descriptionTextField.getText();
            String newProductionDate = productionDatePicker.getValue().toString();

            // 调用修改商品信息的函数
            Integer result = CommodityInterface.updateCommodityInfo(selectedCommodity.getId(), newName, newCategory, newDescription, newProductionDate, newOrigin);
            Utils.alertUpdateResult(primaryStage, result);
            // 关闭修改商品信息页面
            primaryStage.close();
        });

        // 将确认按钮添加到布局中
        root.add(confirmButton, 1, 5);

        // 创建修改商品信息页面的场景
        Scene scene = new Scene(root, 400, 200);

        // 创建修改商品信息页面的舞台
        Stage modifyInfoStage = new Stage();
        modifyInfoStage.setTitle("修改商品信息 - " + selectedCommodity.getName());
        modifyInfoStage.setScene(scene);

        // 设置修改商品信息页面的 Modality 和 Owner
        modifyInfoStage.initModality(Modality.APPLICATION_MODAL);
        modifyInfoStage.initOwner(primaryStage);

        // 显示修改商品信息页面
        modifyInfoStage.showAndWait();
    }

    public static void showModifyCommodityPricePage(DetailedCommodity selectedCommodity, Stage primaryStage) {
        // 创建修改商品价格页面的布局
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.setVgap(10);

        // 添加标签和文本框
        Label originalPriceLabel = new Label("原商品价格:");
        Label originalPriceValueLabel = new Label(String.valueOf(selectedCommodity.getPrice()));

        Label newPriceLabel = new Label("新商品价格:");
        TextField newPriceTextField = new TextField();

        // 添加所有控件到布局中
        root.add(originalPriceLabel, 0, 0);
        root.add(originalPriceValueLabel, 1, 0);
        root.add(newPriceLabel, 0, 1);
        root.add(newPriceTextField, 1, 1);

        // 创建确认按钮
        Button confirmButton = new Button("确认修改");
        confirmButton.setOnAction(event -> {
            // 获取用户输入的新价格
            String newPriceText = newPriceTextField.getText();

            try {
                // 尝试将输入的字符串转换为数字
                Double newPrice = Double.parseDouble(newPriceText);

                // 检查价格是否为正数
                if (newPrice > 0) {
                    Integer result = CommodityInterface.updateCommodityPrice(selectedCommodity.getId(), newPrice);
                    Utils.alertUpdateResult(primaryStage, result);
                    // 关闭修改商品价格页面
                    primaryStage.close();
                } else {
                    Utils.alertDoubleInput(primaryStage);
                }
            } catch (NumberFormatException e) {
                // 提示用户输入有效的数字
                Utils.alertDoubleInput(primaryStage);
            }
        });

        // 将确认按钮添加到布局中
        root.add(confirmButton, 1, 2);

        // 创建修改商品价格页面的场景
        Scene scene = new Scene(root, 400, 200);

        // 创建修改商品价格页面的舞台
        Stage modifyPriceStage = new Stage();
        modifyPriceStage.setTitle("修改商品价格 - " + selectedCommodity.getName());
        modifyPriceStage.setScene(scene);

        // 设置修改商品价格页面的 Modality 和 Owner
        modifyPriceStage.initModality(Modality.APPLICATION_MODAL);
        modifyPriceStage.initOwner(primaryStage);

        // 显示修改商品价格页面
        modifyPriceStage.showAndWait();
    }

}
