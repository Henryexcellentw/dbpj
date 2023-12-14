package InterfaceImplementation;

import Entity.Collection;
import Entity.DetailedCommodity;
import Entity.User;
import SqlOperation.SqlConnection;

import java.sql.*;
import java.util.ArrayList;

public class CollectionInterface {

    public static int addCollectionCommodity(int cId, User user,double floorPrice) throws SQLException, ClassNotFoundException {
        int returnValue = 0;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        if (user.getRole() == 0) {
            try {
                con = SqlConnection.getConnection();
                con.setAutoCommit(false); // 关闭自动提交
                String sqlInsert = "INSERT INTO collection (u_id, c_id,floorprice) VALUES (?, ?,?)";
                pstmt = con.prepareStatement(sqlInsert);
                pstmt.setInt(1, user.getId());
                pstmt.setInt(2, cId);
                pstmt.setDouble(3,floorPrice);
                pstmt.executeUpdate();
                con.commit(); // 提交事务
                returnValue = 1;
            }
            catch (SQLException e) {
                if (con != null) {
                    con.rollback(); // 发生异常，回滚事务
                    return -1;
                }
                throw e;
            } finally {
                if (resultSet != null) resultSet.close();
                if (pstmt != null) pstmt.close();
                if (con != null) {
                    con.setAutoCommit(true); // 恢复自动提交
                    con.close();
                }
            }
        } else {
            returnValue = -1;
        }

        return returnValue;
    }

    public static Integer getMostPopularCommodityId(){
        Integer resultId = -1;
        try{
            Connection conn = SqlConnection.getConnection();

            String sql = "SELECT c_id, COUNT(c_id) AS count\n" +
                    "FROM collection\n" +
                    "GROUP BY c_id\n" +
                    "ORDER BY count DESC\n" +
                    "LIMIT 1;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet resultSet = pstmt.executeQuery();
            if(resultSet.next()){
                resultId = resultSet.getInt("c_id");
            }
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultId;
    }

    public static Integer deleteCollectionByUserId(Connection con, Integer userId){
        Integer result = -1;
        try{
            String sql = "DELETE FROM collection WHERE u_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,userId);
            result = pstmt.executeUpdate();
            pstmt.close();
            if(result < 0)  return -1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 1;
    }
    public static Integer deleteCollectionByCommodityId(Connection con, Integer commodityId){
        Integer result = -1;
        try{
            String sql = "DELETE FROM collection WHERE c_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,commodityId);
            result = pstmt.executeUpdate();
            pstmt.close();
            if(result < 0)  return -1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        System.out.println(commodityId);
        return 1;
    }

    public static Integer deleteSpecificCollection(Integer userId, Integer commodityId) throws SQLException, ClassNotFoundException {
        Connection con = SqlConnection.getConnection();
        Integer result = -1;
        try{
            con.setAutoCommit(false);
            String sql = "DELETE FROM collection WHERE u_id = ? AND c_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,userId);
            pstmt.setInt(2,commodityId);
            result = pstmt.executeUpdate();
            pstmt.close();
            con.commit();
            con.setAutoCommit(true);
            if(result < 0)  return -1;
        }catch (Exception e){
            e.printStackTrace();
            con.rollback();
            con.setAutoCommit(true);
            return -1;
        }finally {
            con.close();
        }
        return 1;
    }

    public static Integer changeFloorPrice(Integer userId, Integer commodityId, Double floorPrice) throws SQLException, ClassNotFoundException {
        Connection con = SqlConnection.getConnection();
        Integer result = -1;
        try{
            con.setAutoCommit(false);
            String sql = "UPDATE collection SET floorprice = ? WHERE u_id = ? AND c_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDouble(1,floorPrice);
            pstmt.setInt(2,userId);
            pstmt.setInt(3,commodityId);
            result = pstmt.executeUpdate();
            pstmt.close();
            con.commit();
            if(result < 0)  return -1;
        }catch (Exception e){
            e.printStackTrace();
            con.rollback();
            con.setAutoCommit(true);
            return -1;
        }finally {
            con.close();
        }
        return 1;
    }

    public static ArrayList<Collection> getCollectionList(int userId) throws SQLException, ClassNotFoundException {
        Connection conn = SqlConnection.getConnection();
        ArrayList<Collection> collectionList = new ArrayList<>();
        String sql = "SELECT t1.u_id, t1.c_id, t1.floorprice, t2.name, t3.name as shopName, t4.name as platformName " +
                "FROM collection t1 " +
                "INNER JOIN commodity t2 ON t1.c_id = t2.id " +
                "INNER JOIN shop t3 ON t2.s_id = t3.id " +
                "INNER JOIN platform t4 ON t4.id = t2.p_id " +
                "WHERE t1.u_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Collection collection = new Collection(rs.getInt("u_id"), rs.getInt("c_id"), rs.getDouble("floorprice"), rs.getString("name"), rs.getString("shopName"), rs.getString("platformName"));
            collectionList.add(collection);
        }
        rs.close();
        ps.close();
        conn.close();
        return collectionList;
    }

    public static ArrayList<DetailedCommodity> getCollectionDetailedCommoditiesByUserId(Integer u_id) {
        ArrayList<DetailedCommodity> collections = new ArrayList<>();
        ArrayList<Integer> commodityIds = new ArrayList<>();
        try{
            Connection conn = SqlConnection.getConnection();
            // 使用PreparedStatement来防止SQL注入攻击
            String getCollectionsSql = "SELECT c_id FROM collection WHERE u_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(getCollectionsSql);
            pstmt.setInt(1, u_id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                commodityIds.add(rs.getInt("c_id"));
            }
            pstmt.close();
            conn.close();
            for(Integer c_id: commodityIds){
                collections.add(CommodityInterface.getDetailedCommodityInfo(c_id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return collections;
    }


}
