package UI;

import Entity.*;
import InterfaceImplementation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.DatePicker;
import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;


/**
 * @Description：
 * @Author Huangzisu
 * @date 2023-12-06
 **/
public class AdministratorPage {
    public static void showAdministratorPage(Stage stage) {
        // 创建布局
        GridPane gridPane = new GridPane();
        gridPane.setVgap(15);
        gridPane.setHgap(15);
        gridPane.setAlignment(Pos.CENTER);

        // 添加组件
        Utils.addButton(gridPane, "查询最受欢迎的商品", () -> showMostPopularCommodity(stage));
        Utils.addButton(gridPane, "查看所有用户信息", () -> showAllUsersPopup(stage));
        Utils.addButton(gridPane, "查看所有商店信息", () -> showAllShopsPopup(stage));
        Utils.addButton(gridPane, "查看所有商品信息", () -> showAllCommoditiesPage(stage));
        Utils.addButton(gridPane, "查看所有平台信息", () -> showAllPlatformsPage(stage));
        Utils.addButton(gridPane, "新增用户", () -> showInsertUserPopup(stage));
        Utils.addButton(gridPane, "新增商店", () -> showInsertShopPopup(stage));
        Utils.addButton(gridPane, "新增平台", () -> showInsertPlatformPopup(stage));

        // 设置场景和舞台
        Scene scene = new Scene(gridPane, 300, 300);
        stage.setScene(scene);
        stage.setTitle("管理员界面");
        stage.show();
    }

    public static void showUpdatePriceHistoryPopup(Price price) {
        Stage updatePopupStage = new Stage();
        updatePopupStage.initModality(Modality.APPLICATION_MODAL);
        updatePopupStage.setTitle("更新价格历史");

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // 添加标签、文本框和 Spinner 控件
        Label priceLabel = new Label("价格:");
        TextField priceTextField = new TextField(Double.toString(price.getPrice()));

        String timeString = price.getTime(); // 这里获取 String 类型的时间
        // 将 String 转换为 LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 你的时间格式
        LocalDateTime timestamp = LocalDateTime.parse(timeString, formatter);

        Label timeLabel = new Label("日期:");
        // 创建日期选择器
        DatePicker datePicker = new DatePicker();
        Label hourLabel = new Label("小时:");
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, timestamp.getHour(), 1);
        Label minuteLabel = new Label("分钟:");
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, timestamp.getMinute(), 1);

        // 创建确认按钮
        Button confirmButton = new Button("确认更新");
        confirmButton.setOnAction(event -> {
            // 获取用户输入的价格和时间
            Double updatedPrice = 0.0;
            try{
                updatedPrice = Double.parseDouble(priceTextField.getText());
            }catch (Exception e){
                Utils.alertIsDouble();
                updatePopupStage.close();
            }
            LocalDate updatedDate = datePicker.getValue();
            int updatedHour = hourSpinner.getValue();
            int updatedMinute = minuteSpinner.getValue();

            // 构建更新的 LocalDateTime 对象
            LocalDateTime updatedDateTime = LocalDateTime.of(updatedDate, LocalTime.of(updatedHour, updatedMinute));

            // 调用更新价格历史的操作
            Integer result = PriceInterface.updatePrice(price.getCId(), updatedPrice,Timestamp.valueOf(price.getTime()) , Timestamp.valueOf(updatedDateTime));

            // 显示更新结果
            Utils.alertUpdateResult(updatePopupStage, result);

            // 关闭更新价格历史窗口
            updatePopupStage.close();
        });

        vBox.getChildren().addAll(priceLabel, priceTextField, timeLabel, datePicker, hourLabel, hourSpinner, minuteLabel, minuteSpinner, confirmButton);

        Scene updatePopupScene = new Scene(vBox);
        updatePopupStage.setScene(updatePopupScene);

        updatePopupStage.showAndWait();
    }
    public static void showDeletePriceHistoryPopup(Price price, Stage primaryStage){
        // 创建弹出窗口
        Stage deletePopupStage = new Stage();
        deletePopupStage.initModality(Modality.APPLICATION_MODAL);
        deletePopupStage.initOwner(primaryStage);
        deletePopupStage.setTitle("删除价格历史");

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // 创建确认按钮
        Button confirmButton = new Button("确认删除");
        confirmButton.setOnAction(event -> {
            // 获取用户输入的ID
            try {
                // 显示确认弹窗
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.initOwner(primaryStage);
                alert.setTitle("确认删除");
                alert.setHeaderText("确认删除价格历史？");
                alert.setContentText("您确定要删除商品价格历史吗？");

                // 显示确认弹窗并等待用户响应
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // 用户点击确认，执行删除用户的操作
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date parsedDate = new Date();
                        try{
                            parsedDate = dateFormat.parse(price.getTime());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Integer result =PriceInterface.deletePrice(price.getCId(), new Timestamp(parsedDate.getTime()));

                        // 显示删除结果
                        Utils.alertDeleteResult(primaryStage, result);
                    }
                });
            } catch (NumberFormatException e) {
            }
        });

        // 将标签、文本框和按钮添加到布局
        vBox.getChildren().addAll(confirmButton);

        // 创建场景和设置弹窗标题
        Scene deletePopupScene = new Scene(vBox);
        deletePopupStage.setScene(deletePopupScene);

        // 显示弹窗
        deletePopupStage.showAndWait();
    }
    public static void showPriceHistoryDetailsPopup(Price price, Stage primaryStage){
        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // 创建按钮
        Button updateButton = new Button("修改价格历史");
        Button deleteButton = new Button("删除价格历史");

        // 设置按钮点击事件
        updateButton.setOnAction(e -> showUpdatePriceHistoryPopup(price));
        deleteButton.setOnAction(e -> showDeletePriceHistoryPopup(price, primaryStage));
        // 将按钮添加到布局
        vBox.getChildren().addAll(updateButton, deleteButton);

        // 创建新的Stage
        Stage detailsStage = new Stage();
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.setTitle("价格历史管理");
        detailsStage.initOwner(primaryStage);

        // 创建场景
        Scene scene = new Scene(vBox, 200, 100);
        detailsStage.setScene(scene);

        // 显示弹出窗口
        detailsStage.showAndWait();
    }
    public static void showPriceHistoryPopup(DetailedCommodity selectedCommodity){
        // 创建弹出窗口
        Stage priceHistoryPopupStage = new Stage();
        priceHistoryPopupStage.initModality(Modality.APPLICATION_MODAL);
        priceHistoryPopupStage.setTitle("价格历史");

        // 创建 TableView
        TableView<Price> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 创建 TableColumn
        TableColumn<Price, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("cId"));
        TableColumn<Price, Double> priceColumn = new TableColumn<>("价格");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Price, String> timeColumn = new TableColumn<>("时间");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        // 将列添加到 TableView
        tableView.getColumns().addAll(idColumn,priceColumn, timeColumn);
        // 设置鼠标点击事件
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // 判断是否单击
                Price selectedPrice = tableView.getSelectionModel().getSelectedItem();
                if (selectedPrice != null) {
                    // 单击事件处理
                    showPriceHistoryDetailsPopup(selectedPrice, priceHistoryPopupStage);
                }
            }
        });

        // 创建选择框（ChoiceBox）
        ChoiceBox<String> filterChoiceBox = new ChoiceBox<>();
        filterChoiceBox.getItems().addAll("所有日期", "近一星期", "近一月", "近一年");
        filterChoiceBox.setValue("所有日期"); // 默认选择"所有日期"

        // 创建查询按钮
        Button queryButton = new Button("查询");
        queryButton.setOnAction(event -> {
            // 获取选定的日期范围
            String selectedOption = filterChoiceBox.getValue();
            int option = Utils.convertOptionToValue(selectedOption);
            // 获取价格历史数据
            try{
                ArrayList<Price> priceHistory = PriceInterface.getHistoryPrice(selectedCommodity.getId(), option);
                // 将价格历史数据添加到 TableView
                ObservableList<Price> data = FXCollections.observableArrayList(priceHistory);
                tableView.setItems(data);
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        // 创建布局
        VBox vBox = new VBox(filterChoiceBox, queryButton, tableView);

        // 创建场景和设置弹窗标题
        Scene priceHistoryPopupScene = new Scene(vBox, 400, 300);
        priceHistoryPopupStage.setScene(priceHistoryPopupScene);

        // 显示弹窗
        priceHistoryPopupStage.showAndWait();
    }
    public static void showCommodityDetailsPopup(DetailedCommodity selectedCommodity) {
        // 创建布局
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        // 创建弹窗
        Stage commodityDetailsPopupStage = new Stage();
        commodityDetailsPopupStage.initModality(Modality.APPLICATION_MODAL);

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        Utils.addButton(gridPane, "修改商品信息", () -> showUpdateCommodityPopup(selectedCommodity, commodityDetailsPopupStage));
        Utils.addButton(gridPane, "删除商品", () -> showDeleteCommodityPopup(selectedCommodity, commodityDetailsPopupStage));
        Utils.addButton(gridPane, "查看价格历史", () -> showPriceHistoryPopup(selectedCommodity));
        // 创建标签显示商品详细信息
        Label commodityDetailsLabel = new Label("商品详细信息:\n" +
                "ID: " + selectedCommodity.getId() + "\n" +
                "名称: " + selectedCommodity.getName() + "\n" +
                "类别: " + selectedCommodity.getCategory() + "\n" +
                "产地: " + selectedCommodity.getOrigin() + "\n" +
                "描述: " + selectedCommodity.getDescription() + "\n" +
                "生产日期: " + selectedCommodity.getProductionDate() + "\n" +
                "价格: " + selectedCommodity.getPrice() + "\n" +
                "商店: " + selectedCommodity.getShop() + "\n" +
                "平台: " + selectedCommodity.getPlatform() + "\n" +
                "商店地址: " + selectedCommodity.getShopAddress());

        // 将标签添加到布局
        vBox.getChildren().addAll(commodityDetailsLabel, gridPane);

        // 创建场景和设置弹窗标题
        Scene commodityDetailsPopupScene = new Scene(vBox);
        commodityDetailsPopupStage.setScene(commodityDetailsPopupScene);
        commodityDetailsPopupStage.setTitle("商品详情");

        // 显示弹窗
        commodityDetailsPopupStage.showAndWait();
    }
    public static void showAllCommoditiesPage(Stage primaryStage) {
        // 示例商品数据
        ArrayList<DetailedCommodity> detailedCommodities = CommodityInterface.getAllCommodity();
        System.out.println(detailedCommodities.size());
        for(DetailedCommodity item: detailedCommodities){
            if(item.getId() == 10001006)System.out.println("ok");
        }

        // 创建表格形式的布局
        TableView<DetailedCommodity> tableView = new TableView<>();

        // 创建表格的列
        TableColumn<DetailedCommodity, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<DetailedCommodity, String> nameColumn = new TableColumn<>("名称");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<DetailedCommodity, String> categoryColumn = new TableColumn<>("类别");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<DetailedCommodity, Integer> priceColumn = new TableColumn<>("价格");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<DetailedCommodity, String> shopColumn = new TableColumn<>("商店");
        shopColumn.setCellValueFactory(new PropertyValueFactory<>("shop"));

        TableColumn<DetailedCommodity, String> platformColumn = new TableColumn<>("平台");
        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platform"));

        // 添加列到表格
        tableView.getColumns().addAll(idColumn, nameColumn, categoryColumn, priceColumn, shopColumn, platformColumn);

        // 添加商品数据到表格
        tableView.setItems(FXCollections.observableArrayList(detailedCommodities));

        // 监听表格的选中事件
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // 在选中事件中弹出新页面显示商品详细信息
                showCommodityDetailsPopup(newSelection);
            }
        });

        // 创建搜索框
        TextField searchField = new TextField();
        Button searchButton = new Button("搜索");
        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText();
            ArrayList<DetailedCommodity> searchResults = CommodityInterface.searchDetailedCommodity(searchTerm);
            tableView.setItems(FXCollections.observableArrayList(searchResults));
        });
        // 创建弹出窗口
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        vBox.getChildren().add(tableView);

        // 设置布局到场景
        Scene scene = new Scene(vBox, 650, 500);
        popupStage.setScene(scene);

        // 将搜索框和按钮添加到布局
        vBox.getChildren().addAll(searchField, searchButton);

        // 创建确认按钮
        Button confirmButton = new Button("确定");
        confirmButton.setOnAction(event -> popupStage.close());

        // 添加确认按钮到布局
        vBox.getChildren().add(confirmButton);

        // 设置弹出窗口标题
        popupStage.setTitle("所有商品信息");

        // 显示弹出窗口
        popupStage.showAndWait();
    }
    private static void showPlatformDetailsPage(Platform selectedPlatform, Stage primaryStage) {
        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // 创建按钮
        Button updateButton = new Button("修改平台信息");
        Button deleteButton = new Button("删除平台");

        // 设置按钮点击事件
        updateButton.setOnAction(e -> showUpdatePlatformPopup(selectedPlatform));
        deleteButton.setOnAction(e -> showDeletePlatformPopup(selectedPlatform));

        // 将按钮添加到布局
        vBox.getChildren().addAll(updateButton, deleteButton);

        // 创建新的Stage
        Stage detailsStage = new Stage();
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.setTitle("平台详细信息");
        detailsStage.initOwner(primaryStage);

        // 创建场景
        Scene scene = new Scene(vBox);
        detailsStage.setScene(scene);

        // 显示弹出窗口
        detailsStage.showAndWait();
    }
    public static void showDeletePlatformPopup(Platform selectedPlatform) {
        // 创建弹出窗口
        Stage deletePlatformStage = new Stage();
        deletePlatformStage.initModality(Modality.APPLICATION_MODAL);
        deletePlatformStage.setTitle("删除平台");

        // 创建标签显示提示信息
        Label deletePlatformLabel = new Label("确定删除平台 " + selectedPlatform.getName() + " 吗？");

        // 创建确认按钮
        Button confirmButton = new Button("确认删除");

        // 添加确认按钮点击事件
        confirmButton.setOnAction(e -> {
            Integer result = PlatformInterface.deletePlatform(selectedPlatform.getId());
            Utils.alertDeleteResult(deletePlatformStage, result);
            deletePlatformStage.close();
        });

        // 使用 VBox 布局
        VBox deletePlatformVBox = new VBox();
        deletePlatformVBox.setSpacing(10);
        deletePlatformVBox.setAlignment(Pos.CENTER);

        // 添加组件到删除平台的弹出窗口布局
        deletePlatformVBox.getChildren().addAll(deletePlatformLabel, confirmButton);

        // 设置弹出窗口的场景
        Scene deletePlatformScene = new Scene(deletePlatformVBox, 300, 150);
        deletePlatformStage.setScene(deletePlatformScene);

        // 显示弹出窗口
        deletePlatformStage.showAndWait();
    }
    public static void showUpdatePlatformPopup(Platform selectedPlatform) {
        // 创建弹出窗口
        Stage updatePlatformStage = new Stage();
        updatePlatformStage.initModality(Modality.APPLICATION_MODAL);
        updatePlatformStage.setTitle("修改平台信息");

        // 创建文本框和标签
        Label labelPlatformName = new Label("平台名称:");
        TextField textFieldPlatformName = new TextField(selectedPlatform.getName());

        // 创建确认按钮
        Button confirmButton = new Button("确认修改");

        // 添加确认按钮点击事件
        confirmButton.setOnAction(e -> {
            // 获取用户输入的平台名称
            String platformName = textFieldPlatformName.getText();
            Integer result = PlatformInterface.updatePlatformInfo(selectedPlatform.getId(), platformName);
            Utils.alertUpdateResult(updatePlatformStage, result);
            // 关闭弹出窗口
            updatePlatformStage.close();
        });

        // 使用 GridPane 布局
        GridPane updatePlatformGridPane = new GridPane();
        updatePlatformGridPane.setVgap(10);
        updatePlatformGridPane.setHgap(10);
        updatePlatformGridPane.setAlignment(Pos.CENTER);

        // 添加组件到修改平台信息的弹出窗口布局
        updatePlatformGridPane.add(labelPlatformName, 0, 0);
        updatePlatformGridPane.add(textFieldPlatformName, 1, 0);
        updatePlatformGridPane.add(confirmButton, 1, 1);

        // 设置弹出窗口的场景
        Scene updatePlatformScene = new Scene(updatePlatformGridPane, 300, 150);
        updatePlatformStage.setScene(updatePlatformScene);

        // 显示弹出窗口
        updatePlatformStage.showAndWait();
    }
    public static void showAllPlatformsPage(Stage primaryStage) {
        // 示例平台数据
        ArrayList<Platform> platforms = PlatformInterface.getAllPlatforms();

        // 创建表格形式的布局
        TableView<Platform> tableView = new TableView<>();

        // 创建表格的列
        TableColumn<Platform, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Platform, String> nameColumn = new TableColumn<>("名称");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // 添加列到表格
        tableView.getColumns().addAll(idColumn, nameColumn);

        // 添加平台数据到表格
        ObservableList<Platform> observablePlatforms = FXCollections.observableArrayList(platforms);
        tableView.setItems(observablePlatforms);
        // 监听表格的选中事件
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // 在选中事件中弹出新页面显示平台详细信息
                showPlatformDetailsPage(newSelection, primaryStage);
            }
        });

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // 添加表格到布局
        vBox.getChildren().add(tableView);
        // 创建新的Stage
        Stage platformStage = new Stage();
        platformStage.initModality(Modality.APPLICATION_MODAL);
        platformStage.setTitle("所有平台信息");
        platformStage.initOwner(primaryStage);

        // 创建确认按钮
        Button confirmButton = new Button("确定");
        confirmButton.setOnAction(event -> {platformStage.close();});

        // 添加确认按钮到布局
        vBox.getChildren().add(confirmButton);

        // 创建场景
        Scene scene = new Scene(vBox);
        platformStage.setScene(scene);

        // 显示弹出窗口
        platformStage.showAndWait();
    }
    private static void showUpdateUserPopup(User selectedUser, Stage stage) {
        // 创建弹出窗口
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(stage);

        // 创建文本框和标签
        Label labelUserName = new Label("用户姓名:");
        Label labelUserAge = new Label("用户年龄:");
        Label labelUserGender = new Label("用户性别:");
        Label labelUserPhone = new Label("用户电话:");
        Label labelUserPassword = new Label("用户密码:");

        TextField textFieldUserName = new TextField();
        TextField textFieldUserAge = new TextField();
        ChoiceBox<String> genderChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("Male", "Female"));
        TextField textFieldUserPhone = new TextField();
        TextField passwordField = new TextField();
        genderChoiceBox.setValue(selectedUser.getGender());
        textFieldUserName.setText(selectedUser.getName());
        textFieldUserAge.setText(String.valueOf(selectedUser.getAge()));
        textFieldUserPhone.setText(selectedUser.getPhoneNumber());
        passwordField.setText(selectedUser.getPassword());
        // 创建确认按钮
        Button confirmButton = new Button("确认修改");
        // 添加确认按钮点击事件
        confirmButton.setOnAction(e -> {
            // 获取用户输入的信息
            String userName = textFieldUserName.getText();
            String userAge = textFieldUserAge.getText();
            String userGender = genderChoiceBox.getValue();
            String userPassword = passwordField.getText();
            String userPhone = textFieldUserPhone.getText();

            // 在此处执行修改用户信息的逻辑，你可以调用相应的方法或写逻辑代码
            Integer result = UserInterface.updateUserInfo(selectedUser.getId(), userName, Integer.parseInt(userAge),
                    userPhone, userGender, userPassword);
            Utils.alertUpdateResult(popupStage, result);
            // 关闭弹出窗口
            popupStage.close();
        });

        // 使用 GridPane 布局
        GridPane popupGridPane = new GridPane();
        popupGridPane.setVgap(10);
        popupGridPane.setHgap(10);
        popupGridPane.setAlignment(Pos.CENTER);

        // 添加组件到弹出窗口布局
        popupGridPane.add(labelUserName, 0, 1);
        popupGridPane.add(textFieldUserName, 1, 1);
        popupGridPane.add(labelUserAge, 0, 2);
        popupGridPane.add(textFieldUserAge, 1, 2);
        popupGridPane.add(labelUserGender, 0, 3);
        popupGridPane.add(genderChoiceBox, 1, 3);
        popupGridPane.add(labelUserPhone, 0, 4);
        popupGridPane.add(textFieldUserPhone, 1, 4);
        popupGridPane.add(labelUserPassword, 0, 5);
        popupGridPane.add(passwordField, 1, 5);
        popupGridPane.add(confirmButton, 1, 6);

        // 设置弹出窗口的场景
        Scene popupScene = new Scene(popupGridPane, 300, 250);
        popupStage.setScene(popupScene);
        popupStage.setTitle("管理员修改用户信息");
        popupStage.showAndWait();
    }

    private static void showUpdateShopPopup(Shop shop, Stage stage) {
        // 创建弹出窗口
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(stage);

        // 创建文本框和标签
        Label labelShopName = new Label("商家名称:");
        Label labelShopLocation = new Label("商家地址:");

        TextField textFieldShopName = new TextField();
        TextField textFieldShopLocation = new TextField();
        textFieldShopName.setText(shop.getName());
        textFieldShopLocation.setText(shop.getAddress());

        // 创建确认按钮
        Button confirmButton = new Button("确认修改");

        // 添加确认按钮点击事件
        confirmButton.setOnAction(e -> {
            try {
                String shopName = textFieldShopName.getText();
                String shopLocation = textFieldShopLocation.getText();

                // 在此处执行修改商家信息的逻辑，你可以调用相应的方法或写逻辑代码
                int result = ShopInterface.updateShopInfo(shop.getId(), shopName, shopLocation);

                // 显示更新结果的消息窗口
                Utils.alertUpdateResult(popupStage, result);
                popupStage.close();
            } catch (Exception ex) {
                ex.printStackTrace(); // 这里可以根据实际情况处理异常
            }
        });

        // 使用 GridPane 布局
        GridPane popupGridPane = new GridPane();
        popupGridPane.setVgap(10);
        popupGridPane.setHgap(10);
        popupGridPane.setAlignment(Pos.CENTER);

        // 添加组件到弹出窗口布局
        popupGridPane.add(labelShopName, 0, 1);
        popupGridPane.add(textFieldShopName, 1, 1);
        popupGridPane.add(labelShopLocation, 0, 3);
        popupGridPane.add(textFieldShopLocation, 1, 3);
        popupGridPane.add(confirmButton, 1, 5);

        // 设置弹出窗口的场景
        Scene popupScene = new Scene(popupGridPane, 300, 250);
        popupStage.setScene(popupScene);
        popupStage.setTitle("管理员修改商家信息");
        popupStage.showAndWait();
    }

    private static void showUpdateCommodityPopup(DetailedCommodity selectedCommodity, Stage stage) {
        // 创建弹出窗口
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(stage);

        // 创建文本框和标签
        Label labelCommodityName = new Label("商品名称:");
        Label labelCommodityDescription = new Label("商品描述:");
        Label labelCommodityCategory = new Label("商品类别:");
        Label labelCommodityOrigin = new Label("商品产地:");
        Label labelCommodityProductionDate = new Label("商品生产日期:");
        Label labelShopName = new Label("商家名称:");
        Label labelPlatformName = new Label("平台名称:");

        TextField textFieldCommodityName = new TextField();
        TextField textFieldCommodityDescription = new TextField();
        TextField textFieldCommodityCategory = new TextField();
        TextField textFieldCommodityOrigin = new TextField();
        // 使用 DatePicker 选择日期
        DatePicker datePickerCommodityProductionDate = new DatePicker();
        TextField textFieldShopName = new TextField();
        TextField textFieldPlatformName = new TextField();
        textFieldPlatformName.setText(selectedCommodity.getPlatform());
        textFieldShopName.setText(selectedCommodity.getShop());
        textFieldCommodityOrigin.setText(selectedCommodity.getOrigin());
        textFieldCommodityCategory.setText(selectedCommodity.getCategory());
        textFieldCommodityDescription.setText(selectedCommodity.getDescription());
        textFieldCommodityName.setText(selectedCommodity.getName());
        datePickerCommodityProductionDate.setValue(LocalDate.parse(selectedCommodity.getProductionDate()));

        // 创建确认按钮
        Button confirmButton = new Button("确认修改");

        // 添加确认按钮点击事件
        confirmButton.setOnAction(e -> {
            try {
                // 获取商品输入的信息
                String commodityName = textFieldCommodityName.getText();
                String commodityDescription = textFieldCommodityDescription.getText();
                String commodityCategory = textFieldCommodityCategory.getText();
                String commodityOrigin = textFieldCommodityOrigin.getText();
                // 获取选择的日期
                String commodityProductionDate = datePickerCommodityProductionDate.getValue().toString();
                String shopName = textFieldShopName.getText();
                String platformName = textFieldPlatformName.getText();

                // 在此处执行修改商品信息的逻辑，你可以调用相应的方法或写逻辑代码
                int result = CommodityInterface.administratorUpdateCommodityInfo(
                        selectedCommodity.getId(),
                        commodityName,
                        commodityCategory,
                        commodityDescription,
                        commodityProductionDate,
                        commodityOrigin,
                        shopName,
                        platformName
                );

                // 显示更新结果的消息窗口
                Utils.alertUpdateResult(stage, result);
            } catch (Exception ex) {
                ex.printStackTrace(); // 这里可以根据实际情况处理异常
            }
        });

        // 使用 GridPane 布局
        GridPane popupGridPane = new GridPane();
        popupGridPane.setVgap(10);
        popupGridPane.setHgap(10);
        popupGridPane.setAlignment(Pos.CENTER);

        // 添加组件到弹出窗口布局
        popupGridPane.add(labelCommodityName, 0, 1);
        popupGridPane.add(textFieldCommodityName, 1, 1);
        popupGridPane.add(labelCommodityDescription, 0, 2);
        popupGridPane.add(textFieldCommodityDescription, 1, 2);
        popupGridPane.add(labelCommodityCategory, 0, 3);
        popupGridPane.add(textFieldCommodityCategory, 1, 3);
        popupGridPane.add(labelCommodityOrigin, 0, 4);
        popupGridPane.add(textFieldCommodityOrigin, 1, 4);
        popupGridPane.add(labelCommodityProductionDate, 0, 5);
        popupGridPane.add(datePickerCommodityProductionDate, 1, 5);
        popupGridPane.add(labelShopName, 0, 6);
        popupGridPane.add(textFieldShopName, 1, 6);
        popupGridPane.add(labelPlatformName, 0, 7);
        popupGridPane.add(textFieldPlatformName, 1, 7);
        popupGridPane.add(confirmButton, 1, 8);

        // 设置弹出窗口的场景
        Scene popupScene = new Scene(popupGridPane, 400, 350);
        popupStage.setScene(popupScene);
        popupStage.setTitle("管理员修改商品信息");
        popupStage.showAndWait();
    }

    public static void showMostPopularCommodity(Stage stage) {
        try {
            // 在此处执行查询最受欢迎商品的逻辑，返回商品信息的结果
            Integer mostPopularCommodityId = CollectionInterface.getMostPopularCommodityId();
            if(mostPopularCommodityId == -1){
                Utils.alertQueryFailure(stage);
                return;
            }
            DetailedCommodity commodity = CommodityInterface.getDetailedCommodityInfo(mostPopularCommodityId);
            if(commodity == null){
                Utils.alertQueryFailure(stage);
                return;
            }
            // 创建弹出窗口
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.initOwner(stage);
            alert.setTitle("最受欢迎的商品");
            alert.setHeaderText("最受欢迎的商品信息");
            StringBuilder contentText = new StringBuilder();
            contentText.append("ID: ").append(commodity.getId()).append("\n");
            contentText.append("名称: ").append(commodity.getName()).append("\n");
            contentText.append("价格: ").append(commodity.getPrice()).append("\n");
            contentText.append("商家: ").append(commodity.getShop()).append("\n");
            contentText.append("平台: ").append(commodity.getPlatform()).append("\n");
            contentText.append("类别: ").append(commodity.getCategory()).append("\n");
            contentText.append("描述: ").append(commodity.getDescription()).append("\n");
            contentText.append("生产日期: ").append(commodity.getProductionDate()).append("\n");
            contentText.append("产地: ").append(commodity.getOrigin()).append("\n\n");
            alert.setContentText(contentText.toString());
            alert.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace(); // 这里可以根据实际情况处理异常
        }
    }

    public static void showInsertUserPopup(Stage primaryStage) {
        // 创建新增用户窗口的布局
        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.setVgap(10);

        // 添加标签和文本框
        Label nameLabel = new Label("姓名:");
        TextField nameTextField = new TextField();

        Label ageLabel = new Label("年龄:");
        TextField ageTextField = new TextField();

        Label phoneNumberLabel = new Label("电话号码:");
        TextField phoneNumberTextField = new TextField();

        Label roleLabel = new Label("身份:");
        TextField roleTextField = new TextField();

        Label passwordLabel = new Label("密码:");
        TextField passwordTextField = new TextField();

        Label genderLabel = new Label("性别:");
        TextField genderTextField = new TextField();

        // 添加所有控件到布局中
        root.add(nameLabel, 0, 0);
        root.add(nameTextField, 1, 0);
        root.add(ageLabel, 0, 1);
        root.add(ageTextField, 1, 1);
        root.add(phoneNumberLabel, 0, 2);
        root.add(phoneNumberTextField, 1, 2);
        root.add(roleLabel, 0, 3);
        root.add(roleTextField, 1, 3);
        root.add(passwordLabel, 0, 4);
        root.add(passwordTextField, 1, 4);
        root.add(genderLabel, 0, 5);
        root.add(genderTextField, 1, 5);

        // 创建确认按钮
        Button confirmButton = new Button("确认新增");
        confirmButton.setOnAction(event -> {
            // 获取用户输入的信息
            String name = nameTextField.getText();
            String age = ageTextField.getText();
            String phoneNumber = phoneNumberTextField.getText();
            Utils.alertIsInt(age);
            Integer ageInt = Integer.parseInt(age);
            Utils.alertIsInt(phoneNumber);
            String role = roleTextField.getText();
            String password = passwordTextField.getText();
            String gender = genderTextField.getText();
            Integer roleInt = 0;
            if(role.equals("商家")){
                roleInt = 1;
            } else if(role.equals("管理员")){
                roleInt = 2;
            }

            // 调用新增用户的函数
            Integer userId = UserInterface.insertNewUser(name, ageInt, phoneNumber, roleInt, password, gender);

            // 显示用户新增结果的弹窗
            Utils.alertInsertUserResult(userId);

            // 关闭新增用户窗口
            primaryStage.close();
        });

        // 将确认按钮添加到布局中
        root.add(confirmButton, 1, 6);

        // 创建新增用户窗口的场景
        Scene scene = new Scene(root, 400, 300);

        // 创建新增用户窗口的舞台
        Stage insertUserStage = new Stage();
        insertUserStage.setTitle("新增用户");
        insertUserStage.setScene(scene);

        // 设置新增用户窗口的 Modality 和 Owner
        insertUserStage.initModality(Modality.APPLICATION_MODAL);
        insertUserStage.initOwner(primaryStage);

        // 显示新增用户窗口
        insertUserStage.showAndWait();
    }

    public static void showUserDetailsPopup(User selectedUser) {
        // 创建布局
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);
        // 创建弹窗
        Stage userDetailsPopupStage = new Stage();
        userDetailsPopupStage.initModality(Modality.APPLICATION_MODAL);

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        Utils.addButton(gridPane, "修改用户信息", () -> showUpdateUserPopup(selectedUser, userDetailsPopupStage));
        Utils.addButton(gridPane, "删除用户", () -> showDeleteUserPopup(selectedUser, userDetailsPopupStage));
        // 创建标签显示用户详细信息
        String role = "";
        if(selectedUser.getRole() == 0){
            role = "普通用户";
        }else{
            role = "商家";
        }
        Label userDetailsLabel = new Label("用户详细信息:\n" +
                "ID: " + selectedUser.getId() + "\n" +
                "姓名: " + selectedUser.getName() + "\n" +
                "年龄: " + selectedUser.getAge() + "\n" +
                "电话号码: " + selectedUser.getPhoneNumber() + "\n" +
                "角色: " + role + "\n" +
                "性别: " + selectedUser.getGender() + "\n" +
                "密码: " + selectedUser.getPassword());

        // 将标签添加到布局
        vBox.getChildren().addAll(userDetailsLabel);

        // 创建场景和设置弹窗标题
        Scene userDetailsPopupScene = new Scene(new VBox(gridPane, vBox));
        userDetailsPopupStage.setScene(userDetailsPopupScene);
        userDetailsPopupStage.setTitle("用户详情");

        // 显示弹窗
        userDetailsPopupStage.showAndWait();
    }

    public static void showAllUsersPopup(Stage primaryStage) {
        // 获取所有非管理员用户信息
        ArrayList<User> users = UserInterface.getAllUserNotAdministrator();

        // 创建表格形式的布局
        TableView<User> tableView = new TableView<>();

        // 创建表格的列
        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<User, String> nameColumn = new TableColumn<>("姓名");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, Integer> ageColumn = new TableColumn<>("年龄");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<User, String> phoneNumberColumn = new TableColumn<>("电话号码");
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        TableColumn<User, Integer> roleColumn = new TableColumn<>("角色");
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<User, String> genderColumn = new TableColumn<>("性别");
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<User, String> passwordColumn = new TableColumn<>("密码");
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

        // 将列添加到表格
        tableView.getColumns().addAll(idColumn, nameColumn, ageColumn, phoneNumberColumn, roleColumn, genderColumn, passwordColumn);

        // 将用户数据添加到表格
        ObservableList<User> observableUsers = FXCollections.observableArrayList(users);
        tableView.setItems(observableUsers);

        // 监听表格的选中事件
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // 在选中事件中弹出新页面显示用户详细信息
                showUserDetailsPopup(newSelection);
            }
        });

        // 创建弹出窗口
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);

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
        popupStage.setTitle("所有用户信息");

        // 显示弹出窗口
        popupStage.showAndWait();
    }
    private static void showShopDetailsPopup(Shop selectedShop) {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.CENTER);
        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        // 创建场景和设置弹窗标题
        Stage shopDetailsPopupStage = new Stage();
        shopDetailsPopupStage.initModality(Modality.APPLICATION_MODAL);
        shopDetailsPopupStage.setTitle("商家详情");

        // 创建标签显示商家详细信息
        Label shopDetailsLabel = new Label("商家详细信息:\n" +
                "ID: " + selectedShop.getId() + "\n" +
                "商家名称: " + selectedShop.getName() + "\n" +
                "商家地址: " + selectedShop.getAddress() + "\n" +
                "拥有者ID: " + selectedShop.getOwnerId());

        // 将标签添加到布局
        vBox.getChildren().addAll(shopDetailsLabel);
        Utils.addButton(gridPane, "删除商店", () -> showDeleteShopPopup(selectedShop, shopDetailsPopupStage));
        Utils.addButton(gridPane, "修改商家信息", () -> showUpdateShopPopup(selectedShop, shopDetailsPopupStage));
        Scene shopDetailsPopupScene = new Scene(new VBox(gridPane, vBox));
        shopDetailsPopupStage.setScene(shopDetailsPopupScene);

        // 显示弹窗
        shopDetailsPopupStage.showAndWait();
    }
    public static void showAllShopsPopup(Stage primaryStage) {
        // 获取所有商家信息
        ArrayList<Shop> shops = ShopInterface.getAllShops();

        // 创建表格形式的布局
        TableView<Shop> tableView = new TableView<>();

        // 创建表格的列
        TableColumn<Shop, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Shop, String> nameColumn = new TableColumn<>("商家名称");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Shop, String> addressColumn = new TableColumn<>("商家地址");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Shop, Integer> ownerColumn = new TableColumn<>("拥有者ID");
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerId"));

        // 将列添加到表格
        tableView.getColumns().addAll(idColumn, nameColumn, addressColumn, ownerColumn);

        // 将商家数据添加到表格
        ObservableList<Shop> observableShops = FXCollections.observableArrayList(shops);
        tableView.setItems(observableShops);

        // 监听表格的选中事件
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // 在选中事件中弹出新页面显示商家详细信息
                showShopDetailsPopup(newSelection);
            }
        });

        // 创建弹出窗口
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(5));

        // 添加表格到布局
        vBox.getChildren().add(tableView);

        // 创建确认按钮
        Button confirmButton = new Button("确定");
        confirmButton.setOnAction(event -> popupStage.close());

        // 添加确认按钮到布局
        vBox.getChildren().add(confirmButton);

        // 设置布局到场景
        Scene scene = new Scene(vBox, 750, 400);
        popupStage.setScene(scene);


        // 设置弹出窗口标题
        popupStage.setTitle("所有商家信息");

        // 显示弹出窗口
        popupStage.showAndWait();
    }

    public static void showDeleteUserPopup(User selectedUser, Stage primaryStage) {
        // 创建弹出窗口
        Stage deletePopupStage = new Stage();
        deletePopupStage.initModality(Modality.APPLICATION_MODAL);
        deletePopupStage.initOwner(primaryStage);
        deletePopupStage.setTitle("删除用户");

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // 创建确认按钮
        Button confirmButton = new Button("确认删除");
        confirmButton.setOnAction(event -> {
            // 获取用户输入的ID
            try {
                // 显示确认弹窗
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.initOwner(primaryStage);
                alert.setTitle("确认删除");
                alert.setHeaderText("确认删除用户？");
                alert.setContentText("您确定要删除用户吗？");

                // 显示确认弹窗并等待用户响应
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // 用户点击确认，执行删除用户的操作
                        Integer result = UserInterface.deleteUser(selectedUser.getId());

                        // 显示删除结果
                        Utils.alertDeleteResult(primaryStage, result);
                    }
                });
            } catch (NumberFormatException e) {
            }
        });

        // 将标签、文本框和按钮添加到布局
        vBox.getChildren().addAll(confirmButton);

        // 创建场景和设置弹窗标题
        Scene deletePopupScene = new Scene(vBox);
        deletePopupStage.setScene(deletePopupScene);

        // 显示弹窗
        deletePopupStage.showAndWait();
    }
    public static void showDeleteCommodityPopup(DetailedCommodity selectedCommodity, Stage primaryStage){
        // 创建弹出窗口
        Stage deletePopupStage = new Stage();
        deletePopupStage.initModality(Modality.APPLICATION_MODAL);
        deletePopupStage.initOwner(primaryStage);
        deletePopupStage.setTitle("删除商品");

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // 创建确认按钮
        Button confirmButton = new Button("确认删除");
        confirmButton.setOnAction(event -> {
            // 获取用户输入的ID
            try {
                // 显示确认弹窗
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.initOwner(primaryStage);
                alert.setTitle("确认删除");
                alert.setHeaderText("确认删除商品？");
                alert.setContentText("您确定要删除商品吗？");

                // 显示确认弹窗并等待用户响应
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // 用户点击确认，执行删除用户的操作
                        Integer result = CommodityInterface.deleteCommodityById(selectedCommodity.getId());
                        // 显示删除结果
                        Utils.alertDeleteResult(primaryStage, result);
                    }
                });
            } catch (NumberFormatException e) {
            }
        });

        // 将标签、文本框和按钮添加到布局
        vBox.getChildren().addAll(confirmButton);

        // 创建场景和设置弹窗标题
        Scene deletePopupScene = new Scene(vBox);
        deletePopupStage.setScene(deletePopupScene);

        // 显示弹窗
        deletePopupStage.showAndWait();
    }

    public static void showDeleteShopPopup(Shop shop, Stage primaryStage) {
        // 创建弹出窗口
        Stage deletePopupStage = new Stage();
        deletePopupStage.initModality(Modality.APPLICATION_MODAL);
        deletePopupStage.initOwner(primaryStage);
        deletePopupStage.setTitle("删除商店");

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // 创建确认按钮
        Button confirmButton = new Button("确认删除");
        confirmButton.setOnAction(event -> {
            // 获取用户输入的商店ID
            try {
                // 创建确认弹窗
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.initOwner(primaryStage);
                alert.setTitle("确认删除");
                alert.setHeaderText("确认删除商店？");
                alert.setContentText("您确定要删除商店吗？");

                // 显示确认弹窗并等待用户响应
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // 用户点击确认，执行删除商店的操作
                        Integer result = ShopInterface.deleteShopById(shop.getId());

                        // 显示删除结果
                        Utils.alertDeleteResult(primaryStage, result);
                    }
                });
            } catch (NumberFormatException e) {
            }
        });

        // 将标签、文本框和按钮添加到布局
        vBox.getChildren().addAll(confirmButton);

        // 创建场景和设置弹窗标题
        Scene deletePopupScene = new Scene(vBox);
        deletePopupStage.setScene(deletePopupScene);
        deletePopupStage.setTitle("删除商店");

        // 显示弹窗
        deletePopupStage.showAndWait();
    }
    public static void showInsertShopPopup(Stage primaryStage) {
        // 创建弹出窗口
        Stage insertPopupStage = new Stage();
        insertPopupStage.initModality(Modality.APPLICATION_MODAL);
        insertPopupStage.initOwner(primaryStage);
        insertPopupStage.setTitle("新增商店");

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // 添加标签和文本框
        Label nameLabel = new Label("商店名称:");
        TextField nameTextField = new TextField();

        Label addressLabel = new Label("商店地址:");
        TextField addressTextField = new TextField();

        Label ownerIdLabel = new Label("拥有者ID:");
        TextField ownerIdTextField = new TextField();

        // 创建确认按钮
        Button confirmButton = new Button("确认新增");
        confirmButton.setOnAction(event -> {
            // 获取用户输入的商店信息
            String name = nameTextField.getText();
            String address = addressTextField.getText();
            try {
                Integer ownerId = Integer.parseInt(ownerIdTextField.getText());

                // 调用新增商店的操作
                Integer result = ShopInterface.insertNewShop(name, address, ownerId);

                // 显示新增结果
                Utils.alertInsertShopResult(result);

                // 关闭新增商店窗口
                insertPopupStage.close();
            } catch (NumberFormatException e) {
                // 处理非法输入，弹窗提示用户输入正确的拥有者ID
                Utils.alertIsInt(ownerIdTextField.getText());
            }
        });

        // 将标签、文本框和按钮添加到布局
        vBox.getChildren().addAll(nameLabel, nameTextField, addressLabel, addressTextField, ownerIdLabel, ownerIdTextField, confirmButton);

        // 创建场景和设置弹窗标题
        Scene insertPopupScene = new Scene(vBox);
        insertPopupStage.setScene(insertPopupScene);
        insertPopupStage.setTitle("新增商店");

        // 显示弹窗
        insertPopupStage.showAndWait();
    }
    public static void showInsertPlatformPopup(Stage primaryStage){
        // 创建弹出窗口
        Stage insertPopupStage = new Stage();
        insertPopupStage.initModality(Modality.APPLICATION_MODAL);
        insertPopupStage.initOwner(primaryStage);
        insertPopupStage.setTitle("新增平台");

        // 创建布局
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        // 添加标签和文本框
        Label nameLabel = new Label("平台名称:");
        TextField nameTextField = new TextField();

        // 创建确认按钮
        Button confirmButton = new Button("确认新增");
        confirmButton.setOnAction(event -> {
            // 获取用户输入的商店信息
            String name = nameTextField.getText();
            // 调用新增平添的操作
            Integer result = PlatformInterface.insertNewPlatform(name);
            // 显示新增结果
            Utils.alertInsertShopResult(result);
            // 关闭新增商店窗口
            insertPopupStage.close();
        });

        // 将标签、文本框和按钮添加到布局
        vBox.getChildren().addAll(nameLabel, nameTextField,confirmButton);

        // 创建场景和设置弹窗标题
        Scene insertPopupScene = new Scene(vBox);
        insertPopupStage.setScene(insertPopupScene);

        // 显示弹窗
        insertPopupStage.showAndWait();
    }
}
