package db01;

import java.sql.Connection;
import java.sql.DriverManager;
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
				  "SELECT DEPARTMENT_ID , first_name || ' ' || last_name, phone_number "
				+ "FROM   EMPLOYEES ";
		System.out.println( sql );
		
		Result rs = stmt.executeQuery(sql);

		
		stmt.close();
		conn.close();
	}

}
