package InterfaceImplementation;

import Entity.Commodity;
import Entity.DetailedCommodity;
import Entity.Platform;
import SqlOperation.SqlConnection;
import UI.Utils;

import java.sql.*;
import java.util.ArrayList;

/**
 * @Description：
 * @Author Huangzisu
 * @date 2023-12-06
 **/
public class PlatformInterface {
    public static Platform getPlatformByName(String name){
        Platform platform = null;
        try{
            Connection conn = SqlConnection.getConnection();
            String sql = "select * from platform where name = ? LIMIT 1";
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ptmt.setString(1, name);
            ResultSet rs = ptmt.executeQuery();
            while (rs.next()) {
                 platform = new Platform(rs.getInt("id"), rs.getString("name"));
            }
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return platform;
    }

    public static ArrayList<Platform> getAllPlatforms() {
        ArrayList<Platform> platforms = new ArrayList<>();
        try {
            Connection conn = SqlConnection.getConnection();
            String sql = "SELECT * FROM platform";
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ResultSet rs = ptmt.executeQuery();
            while (rs.next()) {
                Platform platform = new Platform(rs.getInt("id"), rs.getString("name"));
                platforms.add(platform);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return platforms;
    }
    public static Integer updatePlatformInfo(Integer id, String name) {
        int result = -1;
        try {
            Connection con = SqlConnection.getConnection();
            // 更新平台信息
            String sql = "UPDATE platform SET name = ? WHERE id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, id);
                result = pstmt.executeUpdate();
            }
            if (result < 1) {
                return -1; // 更新失败
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // 更新失败
        }
        return 1; // 更新成功
    }
    public static Integer deletePlatform(Integer id) {
        int result = -1;
        ArrayList<DetailedCommodity> commodities = CommodityInterface.getAllCommoditiesByPlatformId(id);
        Connection con = null;
        try {
            con = SqlConnection.getConnection();
            con.setAutoCommit(false);
            for(DetailedCommodity commodity: commodities){
                Integer resultCommodity = CommodityInterface.deleteCommodityById(con, commodity.getId());
                if(resultCommodity == -1){
                    con.rollback();
                    con.close();
                    return -1;
                }
            }
            // 删除平台
            String sql = "DELETE FROM platform WHERE id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                result = pstmt.executeUpdate();
            }
            if (result < 1) {
                return -1; // 删除失败
            }
            con.commit();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            try{
                con.rollback();
            }catch (Exception e1){
                e1.printStackTrace();
                return -1;
            }
            return -1;
        }finally {
            try{
                con.close();
            }catch (Exception e){
                e.printStackTrace();
                return -1;
            }
        }
        return 1; // 删除成功
    }
    public static Integer insertNewPlatform(String name) {
        Integer generatedId = 0;
        try {
            Connection con = SqlConnection.getConnection();
            // 插入新平台
            String insertPlatformSql = "INSERT INTO platform (name) VALUES (?)";
            // 获取自动生成的主键值
            try (PreparedStatement insertPlatformStmt = con.prepareStatement(insertPlatformSql, Statement.RETURN_GENERATED_KEYS)) {
                insertPlatformStmt.setString(1, name);
                Integer result = insertPlatformStmt.executeUpdate();
                if (result > 0) {
                    // 获取自动生成的主键值
                    ResultSet generatedKeys = insertPlatformStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedId;
    }
}
