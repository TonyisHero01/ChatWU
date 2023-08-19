package cz.cuni.mff.java.chatwu.dao;

import cz.cuni.mff.java.chatwu.dto.UserDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    // information of database connection
    private static final String DB_URL = "jdbc:oracle:thin:@tirpitz.ms.mff.cuni.cz:1510/vyuka";
    private static final String USER = "u76824974"; // database username
    private static final String PASS = "u76824974dbs22"; // database password

    // this function returns a database connection
    private Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public void createTable() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ALL_TABLES WHERE TABLE_NAME=UPPER('t_user')";
        String sql2 = "create table T_USER\n" +
                "(\n" +
                "    NAME     VARCHAR2(50) not null\n" +
                "        primary key,\n" +
                "    PASSWORD VARCHAR2(50)\n" +
                ")";
        try (Connection conn = getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == 0) {
                    PreparedStatement preparedStatement = conn.prepareStatement(sql2);
                    preparedStatement.executeQuery();
                }
            }
        }
    }

    public void addUser(UserDto userDto) throws SQLException {
        System.out.println(userDto.getName());
        System.out.println(userDto.getPassword());
        String sql = "INSERT INTO t_user (NAME, PASSWORD) VALUES (?, ?)";

        try (Connection conn = getDBConnection(); // 获取数据库连接
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userDto.getName());
            pstmt.setString(2, userDto.getPassword());
            pstmt.executeUpdate();
        }
    }

    public List<UserDto> getUserList() throws SQLException {
        // SQL query
        String sql = "SELECT NAME, PASSWORD FROM t_user ORDER BY NAME";

        try (Connection conn = getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            List<UserDto> userList = new ArrayList<>(); // create an array list for saving results of searching
            while (rs.next()) { // iterate results
                UserDto user = new UserDto(); // 创建一个 Map，用于存放单个用户的信息 save information of one user
                user.setName(rs.getString("NAME")); // get NAME from result and save in user
                user.setPassword(rs.getString("PASSWORD")); // get PASSWORD from result and save in user
                userList.add(user); // add this user info to the userList
            }
            return userList;
        }
    }

    // search user by username and password in database
    public List<UserDto> getUser(UserDto userDto) throws SQLException {
        String sql = "SELECT * FROM t_user WHERE NAME = ? AND PASSWORD = ?";

        try (Connection conn = getDBConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userDto.getName());
            pstmt.setString(2, userDto.getPassword());
            ResultSet rs = pstmt.executeQuery();

            List<UserDto> userList = new ArrayList<>();
            while (rs.next()) {
                UserDto user = new UserDto();
                user.setName(rs.getString("NAME"));
                user.setPassword(rs.getString("PASSWORD"));
                userList.add(user);
            }
            return userList;
        }
    }
}
