package db03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class TestTUser {
	// db 연결문자열
	private static String  driver = "oracle.jdbc.OracleDriver";
	private static String  url    = "jdbc:oracle:thin:@localhost:1521:xe";
	private static String  dbuid  = "sky";
	private static String  dbpwd  = "1234";
	
	static Scanner   in      =   new Scanner( System.in );
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// CRUD 예제, Create, Read, Update, Delete
		do {
			// 화면춣력
			//System.out.print("\033[H\033[2J"); // \033[H: 커서를 화면 왼쪽 상단(Home)으로 이동
			//System.out.flush();                // \033[2J: 화면 전체를 지움
			
			System.out.println("========================");
			System.out.println("       회원 정보        ");
			System.out.println("========================");
			System.out.println("1. 회원 목록");
			System.out.println("2. 회원 조회");
			System.out.println("3. 회원 추가");
			System.out.println("4. 회원 수정");
			System.out.println("5. 회원 삭제");
			System.out.println("Q. 종료");

			System.out.println("선택:");
			String    choice  =   in.nextLine();
			
			TUserDTO   tuser = null;
			
			switch( choice ) {
			case  "1":  // 회원 목록 
				
				break;
			case  "2":  // 회원 조회 (아이디)
				System.out.println("조회할 아이디를 입력하세요");
				String     uid    =  in.nextLine();
				tuser             =  getTUser( uid );
				// System.out.println( tuser.toString() );
				display( tuser );
				break;
			case  "3":  // 회원 추가
				tuser             =  inputData();
				int        aftcnt = addTUser( tuser );
				System.out.println(aftcnt + "건 저장되었습니다");
				break;
			case  "4":  // 회원 수정
				break;
			case  "5":  // 회원 삭제  
				break;
			case  "q":  // 종료
				System.out.println("프로그램을 종료합니다");
				System.exit(0);
				break;
			}			
			
		} while( true );  // 무한반복 : 무한루프		
		

	}

	// 입력받은 아이디로 한줄을 db 에서 조회한다
	private static TUserDTO getTUser(String uid) 
			throws ClassNotFoundException, SQLException {
		Class.forName( driver );
		Connection          conn   =  DriverManager.getConnection(url, dbuid, dbpwd);
		String              sql    =  " SELECT * FROM TUSER  WHERE  USERID = ?";
		//String              sql    =  " SELECT * FROM TUSER  WHERE  UPPER(USERID) = UPPER(?)"; 소문자로 입력하여도 대문자로 입력받게 만드는 방법 
		PreparedStatement   pstmt  =  conn.prepareStatement( sql );
		pstmt.setString(1, uid );
		
		TUserDTO  tuser = null;
		
		ResultSet           rs     =  pstmt.executeQuery();
		if( rs.next() ) {  // 해당자료가 있다
			String  userid    = rs.getString("userid");
			String  username  = rs.getString("username");
			String  email     = rs.getString("email");
			
			tuser             =  new TUserDTO(userid, username, email)  ;
			
		} else { // 해당자료가 없다 : primary key
			
		}		
		
		pstmt.close();
		conn.close();
		
		return tuser;
	}

	// TUser 한줄을 출력한다
	private static void display(TUserDTO tuser) {
		
		if( tuser == null ) 
			System.out.println("조회한 자료가 없습니다");
		else {
			String msg = String.format("%s %s %s",
				tuser.getUserid(), tuser.getUsername(), tuser.getEmail());
			System.out.println( msg );
		}
	}

	// db 에 insert 한다
	private static int addTUser(TUserDTO tuser) throws SQLException, ClassNotFoundException {
		
		Class.forName(driver);		
		Connection          conn   =  DriverManager.getConnection(url, dbuid, dbpwd);
		
		String              sql    =  "";
		sql   +=  " INSERT  INTO   TUSER  VALUES (?, ?, ?)"; 
		PreparedStatement   pstmt  =  conn.prepareStatement( sql );
		pstmt.setString(1, tuser.getUserid());
		pstmt.setString(2, tuser.getUsername());
		pstmt.setString(3, tuser.getEmail());		
		
		int                 aftcnt =  pstmt.executeUpdate();		
		
		pstmt.close();
		conn.close();		
		return              aftcnt; 
		
	}

	// 데이터를 키보드로 입력받는다
	private static TUserDTO inputData() {
		System.out.println("아이디:");
		String   userid     =  in.nextLine();
		System.out.println("이름:");
		String   username   =  in.nextLine();
		System.out.println("이메일:");
		String   email      =  in.nextLine();
		
		TUserDTO  tuser     =  new TUserDTO(userid, username, email);
		return    tuser;		
	}

}










