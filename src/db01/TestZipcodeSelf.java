package db01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sun.net.httpserver.Authenticator.Result;

public class TestZipcodeSelf {
	
	private static String driver = "oracle.jdbc.OracleDriver";             
	private static String url    = "jdbc:oracle:thin:@127.0.0.1:1521:xe";
	private static String dbuid  = "hr";
	private static String dbpwd  = "1234"; 

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName( driver );
		Connection conn = DriverManager.getConnection(url, dbuid, dbpwd);
		
		// sql 명령문
		Statement  stmt = conn.createStatement();
		String sql = 
				  "SELECT DEPARTMENT_ID AS 부서번호, "
				  + "     first_name || ' ' || last_name AS 이름, "
				  + "     phone_number AS 전화번호 "
				+ "FROM   EMPLOYEES ";
		System.out.println( sql );
		
		ResultSet  rs   = stmt.executeQuery(sql);
		while( rs.next() != false ) {
			System.out.print(rs.getString("부서번호") + ", ");
			System.out.print(rs.getString("이름") + ", ");
			System.out.print(rs.getString("전화번호") );
			System.out.println();
		}
		

		rs.close();
		stmt.close();
		conn.close();
	}

}
