package InterfaceImplementation;


import SqlOperation.SqlConnection;

import java.sql.*;

import Entity.Price;

import java.util.ArrayList;

import static InterfaceImplementation.CommodityInterface.getCommodityPriceHistory;


/**
 * @Description：
 * @Author Huangzisu
 * @date 2023-12-02
 **/
public class PriceInterface {
    public static Integer insertPrice(Connection conn, Integer cId, Double price) {
        Integer result = -1;
        try{
            String sql = "insert into price(c_id, price, time) values(?, ?, current_timestamp)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cId);
            ps.setDouble(2, price);
            result =  ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<Price> getHistoryPrice(Integer cId,int option) throws SQLException, ClassNotFoundException {
        ArrayList<Price> priceArrayList;
        Connection con = SqlConnection.getConnection();
        priceArrayList = getCommodityPriceHistory(con,cId,option);
        return priceArrayList;
    }

    public static Integer deletePriceByCommodityId(Connection con, Integer id){
        Integer result = -1;
        try{
            String sql = "DELETE FROM price WHERE c_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,id);
            result = pstmt.executeUpdate();
            pstmt.close();
            if(result < 0)  return -1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 1;
    }
    public static Integer updatePrice(Integer c_id, Double price, Timestamp time, Timestamp newTime){
        try (Connection connection = SqlConnection.getConnection()) {
            String sql = "UPDATE price SET price = ?, time = ? WHERE c_id = ? AND time = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setDouble(1, price);
                preparedStatement.setTimestamp(2, newTime);
                preparedStatement.setInt(3, c_id);
                preparedStatement.setTimestamp(4, time);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected < 0) {
                    return -1;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }
    public static Integer deletePrice(Integer c_id, Timestamp time){
        Integer rowsAffected = -1;
        try  {
            Connection conn = SqlConnection.getConnection();
            String sql = "DELETE FROM price WHERE c_id = ? AND time = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, c_id);
                pstmt.setTimestamp(2, time);
                rowsAffected = pstmt.executeUpdate();
                if(rowsAffected < 1){
                    return -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

}
