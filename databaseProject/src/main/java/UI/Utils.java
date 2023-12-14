package UI;

import Entity.DetailedCommodity;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @Description：
 * @Author Huangzisu
 * @date 2023-12-07
 **/
public class Utils {
    public static void alertQueryFailure(Stage stage){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);
        alert.setTitle("失败");
        alert.setContentText("查询失败");
        alert.showAndWait();
    }
    public static void alertUpdateResult(Stage stage, int result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);

        if (result == 1) {
            alert.setTitle("成功");
            alert.setHeaderText("信息已成功更新");
            alert.setContentText("信息已经成功更新到数据库。");
        } else {
            alert.setTitle("失败");
            alert.setHeaderText("更新信息失败");
            alert.setContentText("更新信息时发生错误。");
        }
        alert.showAndWait();
    }
    public static void alertDeleteResult(Stage stage, int result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);

        if (result == 1) {
            alert.setTitle("成功");
            alert.setContentText("删除成功！");
        } else {
            alert.setTitle("失败");
            alert.setContentText("删除失败！");
        }
        alert.showAndWait();
    }
    public static void alertReleaseResult(Stage stage, int result){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(stage);

        if (result == 1) {
            alert.setTitle("成功");
            alert.setContentText("商品发布成功。");
        } else {
            alert.setTitle("失败");
            alert.setContentText("商品发布失败。");
        }
        alert.showAndWait();
    }
    public static void alertDoubleInput(Stage stage){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText("价格输入错误");
        alert.setContentText("请输入一个大于0的正数作为商品价格！");
        alert.showAndWait();
    }
    public static void alertInsertUserResult(Integer userId) {
        // 创建用户新增结果的弹窗
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("新增用户结果");

        if (userId != 0) {
            // 插入成功，显示用户新增的id
            alert.setHeaderText("用户新增成功");
            alert.setContentText("新增用户的ID为: " + userId);
        } else {
            // 插入失败
            alert.setHeaderText("用户新增失败");
            alert.setContentText("请检查输入信息并重试");
        }

        // 显示弹窗
        alert.showAndWait();
    }
    public static void alertInsertShopResult(Integer shopId) {
        // 创建用户新增结果的弹窗
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("新增商店结果");

        if (shopId != 0) {
            // 插入成功，显示用户新增的id
            alert.setHeaderText("商店新增成功");
            alert.setContentText("新增商店的ID为: " + shopId);
        } else {
            // 插入失败
            alert.setHeaderText("商店新增失败");
            alert.setContentText("请检查输入信息并重试");
        }
        // 显示弹窗
        alert.showAndWait();
    }
    public static void alertInsertPlatformResult(Integer shopId) {
        // 创建用户新增结果的弹窗
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("新增平台结果");

        if (shopId != 0) {
            // 插入成功，显示用户新增的id
            alert.setHeaderText("平台新增成功");
            alert.setContentText("新增平台的ID为: " + shopId);
        } else {
            // 插入失败
            alert.setHeaderText("平台新增失败");
            alert.setContentText("请检查输入信息并重试");
        }
        // 显示弹窗
        alert.showAndWait();
    }
    public static void alertIsInt(String numberStr){
        try{
            Integer test = Integer.parseInt(numberStr);
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("警告");
            alert.setContentText("请输入正确数字");
            alert.showAndWait();
        }
    }
    public static void alertIsDouble(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setContentText("请输入正确数字");
        alert.showAndWait();
    }
    public static void addButton(GridPane gridPane, String buttonText, Runnable action) {
        Button button = new Button(buttonText);
        button.setOnAction(event -> action.run());
        gridPane.add(button, 1, gridPane.getRowCount());
    }
    public static int convertOptionToValue(String selectedOption) {
        // 将选项转换为对应的值
        switch (selectedOption) {
            case "所有日期":
                return 0;
            case "近一星期":
                return 1;
            case "近一月":
                return 2;
            case "近一年":
                return 3;
            default:
                return 0; // 默认为所有日期
        }
    }
    public static void showPopup(String message) {
        // 创建布局
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        // 添加信息到布局
        Label label = new Label(message);
        vbox.getChildren().add(label);

        // 创建场景
        Scene scene = new Scene(vbox, 400, 200);

        // 创建舞台
        Stage stage = new Stage();
        stage.setScene(scene);

        // 设置舞台标题
        stage.setTitle("价格差异信息");

        // 设置弹出窗口模态，使其在关闭前禁止与其他窗口交互
        stage.initModality(Modality.APPLICATION_MODAL);

        // 显示舞台
        stage.showAndWait();
    }
}
