package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnUtils {
	public static Connection getMySQLConnection() throws SQLException, ClassNotFoundException {
		String hostName = "localhost";

		String dbName = "porttal_news";
		String userName = "root";
		String password = "root";
		return getMySQLConnection(hostName, dbName, userName, password);
	}

	public static Connection getMySQLConnection(String hostName, String dbName, String userName, String password)
			throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		String connectionURL = "jdbc:mysql://" + hostName + ":3306/" + dbName
				+ "?useUnicode=true&characterEncoding=utf-8";
		Connection conn = DriverManager.getConnection(connectionURL, userName, password);
		return conn;
	}

	public static boolean checkTitleExit(String md5_title) throws ClassNotFoundException, SQLException {
		boolean return_ck = false;
		Connection connection = getMySQLConnection();
		Statement statement = connection.createStatement();
		String sql = "SELECT `id`, `fullname`, `price`, `phone`, `title`, `description`, `city`, `status`, "
				+ "`address`, `time_post`, `url`, `page_id`, `deleted_at`, `created_at`, `updated_at`,"
				+ "`md5_title` FROM `porttal_news`.`tbl_news` where md5_title =  '" + md5_title + "' ;";
		ResultSet rs = statement.executeQuery(sql);
		if (!rs.next()) {
			return_ck = false;
		} else {
			return_ck = true;
		}
		connection.close();
		return return_ck;

	}

	public static int insertTableNews(String fullname, String price, String phone, String title, String description,
			String city, String address, int time_post, String url, int page_id, int created_at, String md5_title) {
		String sql = "";
		try {
			Connection connection = getMySQLConnection();
			Statement statement = connection.createStatement();
//			if(page_id == 4) {
//				description = Helper.escapeHTML(description);			
//				}
			sql = "INSERT INTO `porttal_news`.`tbl_news` ( `fullname`, `price`, `phone`, `title`, `description`,"
					+ " `city`, `address`, `time_post`, `url`, `page_id`, `created_at`, `md5_title` ) " + " VALUES ('"
					+ fullname + "', '" + price + "', '" + phone + "', '" + Helper.convertWord(title) + "', '"
					+ Helper.convertWord(description) + "', '" + city + "'," + " '" + Helper.convertWord(address) + "', '"
					+ time_post + "', '" + url + "', '" + page_id + "', '" + created_at + "','" + md5_title + "' )";
//			System.out.println(sql);
			int rowCount = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = statement.getGeneratedKeys();
			int key = 0;
			if (rs.next()) {
				// Retrieve the auto generated key(s).
				key = rs.getInt(1);
			}
			statement.close();
			connection.close();
			return key;
		} catch (Exception e) {
			// TODO: handle exception
			
			e.printStackTrace();
		}
		return 0;

	}
	
}