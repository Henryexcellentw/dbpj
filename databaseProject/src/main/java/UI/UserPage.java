package UI;

import Entity.*;
import Entity.Collection;
import InterfaceImplementation.CollectionInterface;
import InterfaceImplementation.CommodityInterface;
import InterfaceImplementation.PriceInterface;
import SqlOperation.SqlConnection;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static InterfaceImplementation.CollectionInterface.*;
import static InterfaceImplementation.CommodityInterface.getDetailedCommodityInfo;
import static InterfaceImplementation.CommodityInterface.searchCommodity;
import static InterfaceImplementation.MessageInterface.getMessageByUserId;
import static InterfaceImplementation.PriceInterface.getHistoryPrice;

public class UserPage {
    public static void showUserPage(Stage stage, User user){
        Button btnUserInfo = new Button("用户信息查询");
        Button btnSearchProduct = new Button("商品搜索");
        Button btnViewMessages = new Button("查看消息列表");
        Button btnCollection = new Button("查看收藏商品");
        Button btnCollectionAnalysis = new Button("收藏分析");
        Button btnPriceDifferenceAnalysis = new Button("价格差异分析");

        // 为按钮添加事件处理（示例）
        btnUserInfo.setOnAction(event -> showUserInfo(user));
        btnSearchProduct.setOnAction(event -> showSearchProductPage(user));
        btnViewMessages.setOnAction(event->showMessageList(user.getId()));
        btnCollectionAnalysis.setOnAction(event->showCollectionAnalysis(user,stage));
        btnPriceDifferenceAnalysis.setOnAction(event -> showPriceDifferenceExtreme());
        btnCollection.setOnAction(event-> {
            try {
                showCollectionPage(user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        // 其他按钮类似...

        // 创建布局
        VBox layout = new VBox(20); // 10是按钮之间的间距
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(btnUserInfo, btnSearchProduct, btnViewMessages,btnCollection, btnCollectionAnalysis,
                btnPriceDifferenceAnalysis);

        // 设置舞台和场景
        Scene scene = new Scene(layout, 400, 300);
        stage.setScene(scene);
        stage.setTitle("用户页面");
        stage.show();
    }

    private static void showCollectionPage(User user) throws SQLException, ClassNotFoundException {
        Stage stage = new Stage();
        TableView<Collection> table = new TableView<>();

        TableColumn<Collection, String> nameColumn = new TableColumn<>("商品名");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Collection, String> shopColumn = new TableColumn<>("店铺");
        shopColumn.setCellValueFactory(new PropertyValueFactory<>("shopName"));

        TableColumn<Collection, String> platformColumn = new TableColumn<>("平台");
        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platformName"));

        TableColumn<Collection, Double> floorPriceColumn = new TableColumn<>("底价");
        floorPriceColumn.setCellValueFactory(new PropertyValueFactory<>("floorPrice"));

        table.getColumns().addAll(nameColumn, shopColumn, platformColumn, floorPriceColumn);

        ArrayList<Collection> collections = getCollectionList(user.getId());
        table.setItems(FXCollections.observableArrayList(collections));

        // 行点击事件
        table.setRowFactory(tv -> {
            TableRow<Collection> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    Collection selectedCollection = row.getItem();
                    showCollectionEditDialog(selectedCollection,user);
                }
            });
            return row;
        });

        Scene scene = new Scene(new VBox(table), 500, 400);
        stage.setScene(scene);
        stage.setTitle("收藏列表");
        stage.show();
    }
    private static void showCollectionEditDialog(Collection collection, User user) {
        Stage dialogStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField floorPriceField = new TextField(String.valueOf(collection.getFloorPrice()));
        floorPriceField.setPromptText("修改底价");

        Button updateButton = new Button("确认修改");
        updateButton.setOnAction(event -> {
            try {
                double newFloorPrice = Double.parseDouble(floorPriceField.getText());
                int result = changeFloorPrice(user.getId(), collection.getC_id(), newFloorPrice);
                if (result == 1) {
                    showAlert("修改成功", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("修改失败", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                showAlert("请输入有效的底价", Alert.AlertType.ERROR);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        Button deleteButton = new Button("删除收藏");
        deleteButton.setOnAction(event -> {
            int result = 0;
            try {
                result = deleteSpecificCollection(user.getId(), collection.getC_id());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (result == 1) {
                showAlert("删除成功", Alert.AlertType.INFORMATION);
            } else {
                showAlert("删除失败", Alert.AlertType.ERROR);
            }
        });

        layout.getChildren().addAll(new Label("修改底价:"), floorPriceField, updateButton, deleteButton);

        Scene scene = new Scene(layout, 300, 200);
        dialogStage.setScene(scene);
        dialogStage.setTitle("编辑收藏");
        dialogStage.show();
    }

    private static void showUserInfo(User user) {
        String[] UserType = {"用户","商家","管理员"};
        Stage infoStage = new Stage();
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        layout.getChildren().addAll(
                new Label("ID: " + user.getId()),
                new Label("Name: " + user.getName()),
                new Label("Age: " + user.getAge()),
                new Label("Gender: " + user.getGender()),
                new Label("Phone Number: " + user.getPhoneNumber()),
                new Label("Role: " + UserType[user.getRole()])
        );

        Scene scene = new Scene(layout,300,200);
        infoStage.setScene(scene);
        infoStage.setTitle("用户信息");
        infoStage.show();
    }

    private static void showSearchProductPage(User user){
        Stage searchStage = new Stage();
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

            // 输入框
        TextField searchField = new TextField("请输入商品名称");

            // 确认按钮
        Button searchButton = new Button("搜索");
        searchButton.setOnAction(event -> {
            ArrayList<Commodity> commodities = null; // 假设currentUser是当前用户对象
            try {
                commodities = searchCommodity(searchField.getText());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            showCommodityResults(commodities,user);
        });

        layout.getChildren().addAll(searchField, searchButton);

        Scene scene = new Scene(layout, 400, 300);
        searchStage.setScene(scene);
        searchStage.setTitle("商品搜索");
        searchStage.show();
        }

    private static void showCommodityResults(ArrayList<Commodity> commodities, User user) {
        Stage resultStage = new Stage();
        TableView<Commodity> table = new TableView<>();

        // 商品名称列
        TableColumn<Commodity, String> nameColumn = new TableColumn<>("商品名称");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // 商家列
        TableColumn<Commodity, String> shopColumn = new TableColumn<>("商家");
        shopColumn.setCellValueFactory(new PropertyValueFactory<>("shop"));

        // 平台列
        TableColumn<Commodity, String> platformColumn = new TableColumn<>("平台");
        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platform"));

        // 价格列
        TableColumn<Commodity, Double> priceColumn = new TableColumn<>("价格");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        table.getColumns().addAll(nameColumn, shopColumn, platformColumn, priceColumn);
        table.setItems(FXCollections.observableArrayList(commodities));

        // 行点击事件
        table.setRowFactory(tv -> {
            TableRow<Commodity> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    Commodity clickedRow = row.getItem();
                    try {
                        showDetailedCommodityInfo(getDetailedCommodityInfo(clickedRow.getId()),user);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return row;
        });

        Scene scene = new Scene(new VBox(table), 500, 400);
        resultStage.setScene(scene);
        resultStage.setTitle("搜索结果");
        resultStage.show();
    }
    private static void showDetailedCommodityInfo(DetailedCommodity commodity,User user) {
        Stage infoStage = new Stage();
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        Button historyPriceButton = new Button("查看历史价格");
        Button collectButton = new Button("收藏该商品");
        Button comparePriceButton = new Button("比较价格");
        historyPriceButton.setOnAction(event -> showHistoryPriceOptions(commodity.getId()));
        collectButton.setOnAction(event -> showCollectCommodityDialog(commodity.getId(), user));
        comparePriceButton.setOnAction(event -> showComparePriceAmongShopsAndPlatforms(commodity,infoStage));
        layout.getChildren().addAll(
                new Label("商品名称: " + commodity.getName()),
                new Label("商家: " + commodity.getShop()),
                new Label("平台: " + commodity.getPlatform()),
                new Label("价格: " + commodity.getPrice()),
                new Label("产地: " + commodity.getOrigin()),
                new Label("类别: " + commodity.getCategory()),
                new Label("描述: " + commodity.getDescription()),
                new Label("生产日期: " + commodity.getProductionDate()),
                new Label("商家地址: " + commodity.getShopAddress()),
                historyPriceButton,
                collectButton,
                comparePriceButton
        );

        Scene scene = new Scene(layout, 300, 350);
        infoStage.setScene(scene);
        infoStage.setTitle("商品详细信息");
        infoStage.show();
    }
    private static void showHistoryPriceOptions(Integer cId) {
        Stage optionsStage = new Stage();
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        ComboBox<String> timeOptions = new ComboBox<>();
        timeOptions.getItems().addAll("所有时间", "近一周", "近一个月", "近一年");
        timeOptions.getSelectionModel().selectFirst();

        Button searchButton = new Button("查询");
        searchButton.setOnAction(event -> {
            try {
                showHistoryPriceChart(cId, timeOptions.getSelectionModel().getSelectedIndex());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        layout.getChildren().addAll(new Label("选择时间范围"), timeOptions, searchButton);

        Scene scene = new Scene(layout, 300, 200);
        optionsStage.setScene(scene);
        optionsStage.setTitle("选择时间范围");
        optionsStage.show();
    }
    private static void showHistoryPriceChart(Integer cId, int option) throws SQLException, ClassNotFoundException {
        Stage chartStage = new Stage();

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("日期");
        yAxis.setLabel("价格");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("历史价格");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("价格变化");

        ArrayList<Price> prices = getHistoryPrice(cId, option);
        Price minPrice = Collections.min(prices, Comparator.comparingDouble(Price::getPrice));

        for (Price price : prices) {

            XYChart.Data<String, Number> data = new XYChart.Data<>(formatDate(price.getTime()), price.getPrice());
            if (price.getPrice() == minPrice.getPrice()) {
                data.setNode(new Circle(5)); // 突出显示最低价格
            }
            series.getData().add(data);
        }

        lineChart.getData().add(series);

        Scene scene = new Scene(lineChart, 800, 600);
        chartStage.setScene(scene);
        chartStage.setTitle("历史价格图表");
        chartStage.show();
    }
    private static String formatDate(String dateTimeStr) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return dateTime.toLocalDate().toString();
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return dateTimeStr;
        }
    }

    private static void showMessageList(int userId) {
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        ListView<String> listView = new ListView<>();
        ArrayList<Message> messages = getMessageByUserId(userId);

        for (Message message : messages) {
            String displayText = "时间: " + message.getTime().toString() + "\n内容: " + message.getContent();
            listView.getItems().add(displayText);
        }

        layout.getChildren().add(listView);

        Scene scene = new Scene(layout, 400, 600);
        stage.setScene(scene);
        stage.setTitle("消息列表");
        stage.show();
    }
    private static void showCollectCommodityDialog(int cId, User user) {
        Stage dialogStage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField floorPriceField = new TextField();
        floorPriceField.setPromptText("输入底价");

        Button submitButton = new Button("确认");
        submitButton.setOnAction(event -> {
            try {
                double floorPrice = Double.parseDouble(floorPriceField.getText());
                if (floorPrice > 0) {
                    int result = addCollectionCommodity(cId, user, floorPrice);
                    if (result == 1) {
                        showAlert("收藏成功", Alert.AlertType.INFORMATION);
                    } else {
                        showAlert("收藏失败", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("底价必须大于0", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                showAlert("请输入有效的底价", Alert.AlertType.ERROR);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        layout.getChildren().addAll(new Label("请输入底价："), floorPriceField, submitButton);

        Scene scene = new Scene(layout, 300, 200);
        dialogStage.setScene(scene);
        dialogStage.setTitle("收藏商品");
        dialogStage.show();
    }
    private static void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType, message);
        alert.showAndWait();
    }

    private static void showCollectionAnalysis(User user, Stage primaryStage){
        // 获取收藏的商品详细信息
        ArrayList<DetailedCommodity> collectionCommodities = getCollectionDetailedCommoditiesByUserId(user.getId());
        System.out.println(collectionCommodities.size());
        // 统计每种类别的商品数量
        Map<String, Integer> categoryCountMap = new HashMap<>();
        for (DetailedCommodity commodity : collectionCommodities) {
            String category = commodity.getCategory();
            categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
        }

        // 创建饼图数据
        PieChart.Data[] pieChartData = categoryCountMap.entrySet().stream()
                .map(entry -> new PieChart.Data(entry.getKey() + ": " + entry.getValue(), entry.getValue()))
                .toArray(PieChart.Data[]::new);

        // 创建饼图
        PieChart pieChart = new PieChart(FXCollections.observableArrayList(pieChartData));

        // 创建布局
        VBox vbox = new VBox(pieChart);
        vbox.setPadding(new Insets(10));

        // 创建场景
        Scene scene = new Scene(vbox, 400, 400);

        // 设置舞台标题
        primaryStage.setTitle("收藏分析");

        // 设置场景到舞台
        primaryStage.setScene(scene);

        // 显示舞台
        primaryStage.show();
    }

    private static void showComparePriceAmongShopsAndPlatforms(DetailedCommodity commodity, Stage primaryStage){
        // 获取商品详细信息
        ArrayList<DetailedCommodity> commodityDetailsList = CommodityInterface.getCommodityByName(commodity.getName());
        System.out.println(commodityDetailsList.size());

        // 创建柱状图
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("价格比较");

        // 创建数据系列
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("商品价格");

        // 添加数据点到系列
        for (int i = 0; i < commodityDetailsList.size(); i++) {
            DetailedCommodity commodityData = commodityDetailsList.get(i);
            // 使用商家/平台的名称作为横坐标
            String x = commodityData.getShop() + "/" + commodityData.getPlatform();
            series.getData().add(new XYChart.Data<>(x, commodityData.getPrice()));
        }

        // 将系列添加到柱状图
        barChart.getData().add(series);

        // 设置布局
        VBox vbox = new VBox(barChart);
        vbox.setPadding(new Insets(10));

        // 创建场景
        Scene scene = new Scene(vbox, 600, 400);

        // 根据价格排序商品详细信息列表
        if (commodityDetailsList.size() != 0) {
            Collections.sort(commodityDetailsList, Comparator.comparingDouble(DetailedCommodity::getPrice));
            Text highestPriceText = new Text("最高价格: $" + commodityDetailsList.get(commodityDetailsList.size() - 1).getPrice()
            + " (" + commodityDetailsList.get(commodityDetailsList.size() - 1).getShop() + "/" + commodityDetailsList.get(commodityDetailsList.size() - 1).getPlatform() + ")");
            Text lowestPriceText = new Text("最低价格: $" + commodityDetailsList.get(0).getPrice()
            + " (" + commodityDetailsList.get(0).getShop() + "/" + commodityDetailsList.get(0).getPlatform() + ")");

            // 获取场景的根元素
            Pane root = (Pane) scene.getRoot();

            // 设置文本的布局位置，这里假设你要将它们放在场景的左上角
            highestPriceText.setLayoutX(10);
            highestPriceText.setLayoutY(20);

            lowestPriceText.setLayoutX(10);
            lowestPriceText.setLayoutY(40);

            // 添加 Text 元素到场景的根元素上
            root.getChildren().addAll(highestPriceText, lowestPriceText);
        }

        // 设置舞台标题
        primaryStage.setTitle("价格比较");

        // 设置场景到舞台
        primaryStage.setScene(scene);

        // 显示舞台
        primaryStage.show();
    }

    public static void showPriceDifferenceExtreme() {
        // 创建舞台
        Stage stage = new Stage();
        stage.setTitle("价格差异分析");

        // 创建布局
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        // 创建选择框
        ChoiceBox<String> timeRangeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList("所有日期", "近一星期", "近一月", "近一年"));
        timeRangeChoiceBox.setValue("所有日期");

        // 创建按钮
        Button analyzeButton = new Button("分析");
        analyzeButton.setOnAction(event -> {
            // 获取选择的时间范围
            int option = Utils.convertOptionToValue(timeRangeChoiceBox.getValue());

            // 获取所有商品信息
            ArrayList<DetailedCommodity> allCommodities = CommodityInterface.getAllCommodity();

            // 分析价格差异
            analyzePriceDifference(allCommodities, option);
        });

        // 添加组件到布局
        vbox.getChildren().addAll(new Label("选择时间范围:"), timeRangeChoiceBox, analyzeButton);

        // 创建场景
        Scene scene = new Scene(vbox, 300, 200);

        // 设置舞台场景
        stage.setScene(scene);

        // 显示舞台
        stage.show();
    }

    public static void analyzePriceDifference(ArrayList<DetailedCommodity> commodities, int option) {
        if (commodities == null || commodities.isEmpty()) {
            System.out.println("商品列表为空");
            return;
        }

        DetailedCommodity maxDifferenceCommodity = null;
        DetailedCommodity minDifferenceCommodity = null;
        double maxDifference = Double.MIN_VALUE;
        double minDifference = Double.MAX_VALUE;

        for (DetailedCommodity commodity : commodities) {
            try{
                ArrayList<Price> priceHistory = PriceInterface.getHistoryPrice(commodity.getId(), option);
                // 计算价格差异
                if (priceHistory != null && priceHistory.size() > 1) {
                    double maxPrice = Collections.max(priceHistory, Comparator.comparingDouble(Price::getPrice)).getPrice();
                    double minPrice = Collections.min(priceHistory, Comparator.comparingDouble(Price::getPrice)).getPrice();
                    double difference = maxPrice - minPrice;

                    // 更新最大和最小价格差异商品
                    if (difference > maxDifference) {
                        maxDifference = difference;
                        maxDifferenceCommodity = commodity;
                    }

                    if (difference < minDifference) {
                        minDifference = difference;
                        minDifferenceCommodity = commodity;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        // 在页面中显示最大价格差异的商品和最小价格差异的商品
        if (maxDifferenceCommodity != null && minDifferenceCommodity != null) {
            Utils.showPopup("最大价格差异的商品：" + maxDifferenceCommodity.getName() + "，差异金额：" + maxDifference +
                    "\n最小价格差异的商品：" + minDifferenceCommodity.getName() + "，差异金额：" + minDifference);
        } else {
            Utils.showPopup("无法计算价格差异");
        }
    }

}
