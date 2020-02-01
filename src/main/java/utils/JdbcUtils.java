package utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcUtils {

    /**c3p0连接池被创建时，会自动从src下加载c3p0-config.xml*/
    private static DataSource dataSource = new ComboPooledDataSource();

    /**
     * 获取连接
     */
    public static Connection getConnection() throws Exception {

        //获取连接
        Connection connection = dataSource.getConnection();
        return connection;
    }

    /**
     * 释放资源
     */
    public static void close(ResultSet resultSet, Statement statement, Connection connection){
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
