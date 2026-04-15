package db01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestZipcode01 {

	// 연결 문자열 : Connection String
	private static String driver = "oracle.jdbc.OracleDriver";
	private static String dburl  = "jdbc:oracle:thin:@localhost:1521:xe";
	private static String dbuid  = "sky";
	private static String dbpwd  = "1234";
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		Connection conn = DriverManager.getConnection(dburl, dbuid, dbpwd);
		
		Statement stmt = conn.createStatement(); // statement : 명령문
		String sql = "select count(zipcode) cnt from zipcode";
		
		ResultSet rs = stmt.executeQuery(sql);
		
		rs.next();
		
		System.out.println(rs.getInt("cnt")); // 조회된것의 cnt 라는 값을 출력
		
		stmt.close();
		conn.close();
		
	}

}
