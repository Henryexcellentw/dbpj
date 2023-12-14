package InterfaceImplementation;

import Entity.DetailedCommodity;
import Entity.Shop;
import Entity.User;
import SqlOperation.SqlConnection;

import java.sql.*;
import java.util.ArrayList;

public class UserInterface {
    public static int login(String id, String password, User user) throws SQLException, ClassNotFoundException {
        Connection con = SqlConnection.getConnection();
        PreparedStatement stmt = con.prepareStatement("select * from user where id = ? and password = ?");
        stmt.setString(1, id);
        stmt.setString(2, password);
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            user.setId(resultSet.getInt("id"));
            user.setName(resultSet.getString("name"));
            user.setAge(resultSet.getInt("age"));
            user.setGender(resultSet.getString("gender"));
            user.setPhoneNumber(resultSet.getString("phoneNumber"));
            user.setRole(resultSet.getInt("role"));
            stmt.close();
            con.close();
            return 1;
        }
        else {
            stmt.close();
            con.close();
            return -1;
        }
    }


    public static Integer updateUserInfo(Integer id, String name, Integer age, String phoneNumber, String gender,
                                         String password) {
        Integer result = 0;
        try{
            Connection con = SqlConnection.getConnection();
            String sql = "UPDATE user SET name = ?, age = ?, phoneNumber = ?, gender = ?, password = ? WHERE id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1,name);
            pstmt.setInt(2,age);
            pstmt.setString(3,phoneNumber);
            pstmt.setString(4, gender);
            pstmt.setString(5, password);
            pstmt.setInt(6,id);
            result = pstmt.executeUpdate();
            pstmt.close();
            con.close();
            if(result != 1)  return -1;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    public static User getUserById(Integer id){
        User user = null;
        try{
            Connection con = SqlConnection.getConnection();
            String sql = "SELECT * FROM user WHERE id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet result = pstmt.executeQuery();
            while(result.next()){
                user = new User(result.getInt("id"), result.getString("name"), result.getInt("age"),
                        result.getString("phoneNumber"), result.getInt("role"), result.getString("gender"));
            }
            pstmt.close();
            con.close();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return user;
    }

    public static Integer insertNewUser(String name, Integer age, String phoneNumber, Integer role, String password, String gender){
        Integer generatedId = 0;
        try{
            Connection con = SqlConnection.getConnection();
            String sql = "INSERT INTO user(name, age, phoneNumber, role, password, gender) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, phoneNumber);
            pstmt.setInt(4, role);
            pstmt.setString(5, password);
            pstmt.setString(6, gender);
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getInt(1);
            }
            pstmt.close();
            con.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return generatedId;
    }

    public static Integer deleteUser(Integer id){
        Integer result = -1;
        User user = getUserById(id);
        if(user == null)    return -1;
        Integer role = user.getRole();
        Connection con = null;
        try{
            con = SqlConnection.getConnection();
            con.setAutoCommit(false);
            Integer resultMessage = -1;
            Integer resultCollection = -1;
            Integer resultShop = -1;
            // 根据用户身份删除相应表中数据
            if (role == 0){ // 普通用户
                resultMessage = MessageInterface.deleteMessageByUserId(con, id);
                resultCollection = CollectionInterface.deleteCollectionByUserId(con, id);
                if(resultMessage == -1 || resultCollection == -1){
                    con.rollback();
                    con.close();
                    return -1;
                }
            }else if(role == 1){ // 商家
                resultShop = ShopInterface.deleteShopByOwnerId(con, id);
                if(resultShop == -1){
                    con.rollback();
                    con.close();
                    return -1;
                }
            }
            String sql = "DELETE FROM user WHERE id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, id);
            result = pstmt.executeUpdate();
            pstmt.close();
            if(result < 1){
                return -1;
            }
            con.commit();
        }catch (Exception e){
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
        return 1;
    }

    public static ArrayList<User> getAllUserNotAdministrator() {
        ArrayList<User> result = new ArrayList<>();
        try {
            Connection conn = SqlConnection.getConnection();

            // 修改 SQL 查询语句
            String sql = "SELECT * FROM user WHERE role <> 2";

            try (PreparedStatement ptmt = conn.prepareStatement(sql)) {
                ResultSet rs = ptmt.executeQuery();
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("phoneNumber"),
                            rs.getInt("role"),
                            rs.getString("gender"),
                            rs.getString("password")
                    );
                    result.add(user);
                }
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
