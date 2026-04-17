package db03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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
			String    choice    =   in.nextLine();
			
			TUserDTO   tuser    =   null;
			int        aftcnt   =   0;  // 작업결과에 영향을 받는 행(row)을 저장하는 변수 
			
			switch( choice ) {
			// 목록
			case  "1":  // 회원 목록 
				ArrayList<TUserDTO> userList  = getTUserList();
				displayList( userList );
				break;
		    // 조회
			case  "2":  // 회원 조회 (아이디)
				System.out.println("조회할 아이디를 입력하세요");
				String     uid    =  in.nextLine();
				tuser             =  getTUser( uid );
				// System.out.println( tuser.toString() );
				display( tuser );
				break;
			// 추가	
			case  "3":  // 회원 추가
				tuser                =  inputData();
				aftcnt               =  addTUser( tuser );  // 새로운 회원이 DB에 성공적으로 저장이 되면 1, 실패하면 0을 담는다
				System.out.println(aftcnt + "건 저장되었습니다");
				break;
            // 수정				
			case  "4":  // 회원 수정
				System.out.println("수정할 아이디를 입력하세요");
				String     orgUserid =  in.nextLine();     // 검색할 데이터, 변경대상X
				
				System.out.println("수정할 내용를 입력하세요");
				tuser                =  inputUpdateData(); // 수정된 회원이 존재하여 변경에 성공하면 1, 해당 아이디가 없어 실패하면 0이 담깁니다.
				
				aftcnt               =  updateTUser( orgUserid, tuser ); // 기존에 저장된 회원 정보를 새로운 정보로 덮어쓰는(Update) 과정 그 결과를 aftcnt 변수에 담는
				           // 수정하고싶은 식별자 ID (orgUserid), 수정할 새로운 데이터들을 담고 있는 객체(tuser)
				System.out.println(aftcnt + "건 수정되었습니다");
				
				System.out.println("Press Enter Key ....");
				in.nextLine();
				
				break;
			// 삭제	
			case  "5":  // 회원 삭제  
				System.out.println("삭제할 아이디를 입력하세요");
				String orgUserid2  =  in.nextLine(); // 삭제할 오리지널 유저아이디(orgUserid2) 문자열을 입력 받아야하니 문자열 명령어를 
				
				aftcnt             = deleteTUser(orgUserid2); // 삭제된 데이터가 있으면 1, 없으면 0이 담깁니다.
				System.out.println(aftcnt + "건 삭제 되었습니다");
				
				System.out.println("Press Enter Key ....");
				in.nextLine();
				
				break;
				
			case  "q":  // 종료
				System.out.println("프로그램을 종료합니다");
				System.exit(0);
				break;
			}			
			
		} while( true );  // 무한반복 : 무한루프		
		

	}

	//-----------------------------------------------------
 


	// 1. 전체 목록 조회 - db 에서
	private static ArrayList<TUserDTO> getTUserList() 
			throws SQLException, ClassNotFoundException {
		
		Class.forName( driver );
		Connection           conn   =  DriverManager.getConnection(url, dbuid, dbpwd);
		String               sql    =  " SELECT * FROM TUSER ";
		sql                        +=  " ORDER BY USERID ASC ";
		PreparedStatement    pstmt  =  conn.prepareStatement( sql );
		ResultSet            rs     =  pstmt.executeQuery();
		
		ArrayList<TUserDTO>  userList = new ArrayList<>();
		
		while( rs.next()  ) {
			String   userid    =  rs.getString("userid");   
			String   username  =  rs.getString("username");
			String   email     =  rs.getString("email");
			
			TUserDTO  tuser    =  new TUserDTO(userid, username, email); 
			userList.add( tuser );
		}
		
		rs.close();		
		pstmt.close();
		conn.close();
		
		return userList;
	}

	// 2. 입력받은 아이디로 한줄을 db 에서 조회한다
	private static TUserDTO getTUser(String uid) 
			throws ClassNotFoundException, SQLException {
		Class.forName( driver );
		Connection          conn   =  DriverManager.getConnection(url, dbuid, dbpwd);
		String              sql    =  " SELECT * FROM TUSER  WHERE  USERID = ?";
		PreparedStatement   pstmt  =  conn.prepareStatement( sql );
		pstmt.setString(1, uid.toUpperCase() );
		
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

	
	// 3. db 에 insert 한다
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
	
   // 4. 회원 수정
	private static int updateTUser(String orgUserid, TUserDTO tuser)
			throws ClassNotFoundException, SQLException {
		
		Class.forName(driver);		
		Connection          conn   =  DriverManager.getConnection(url, dbuid, dbpwd);
		
		String              sql    =  "";
		sql   +=  "  UPDATE   TUSER ";
		sql	  +=  "  SET     USERNAME = ?,";  // 1번째 물음표
		sql   +=  "          EMAIL    = ? ";  // 2번째 물음표
		sql   +=  "  WHERE   USERID   = ? ";  // 3번째 물음표
		PreparedStatement   pstmt  =  conn.prepareStatement( sql );
		pstmt.setString(1, tuser.getUsername()); // 1번째 물음표
		pstmt.setString(2, tuser.getEmail());    // 2번째 물음표
		pstmt.setString(3, orgUserid);           // 3번째 물음표
		
		int                 aftcnt =  pstmt.executeUpdate();		
		
		pstmt.close();
		conn.close();		
		return              aftcnt; 
	}
	
	
	// 5. 회원 삭제
	private static int deleteTUser(String orgUserid2)
			throws SQLException, ClassNotFoundException {
		Class.forName(driver);		
		Connection          conn   =  DriverManager.getConnection(url, dbuid, dbpwd);
		
		String              sql    =  "";
		sql   +=  "  DELETE FROM TUSER ";
		sql   +=  "  WHERE  USERID = ? ";     // 2번째 물음표
		PreparedStatement   pstmt  =  conn.prepareStatement( sql );
		pstmt.setString(1, orgUserid2); // 1번째 물음표
		
		int                 aftcnt =  pstmt.executeUpdate(); // MYSQL 에도 실행을 해라 는 의미도 된다		
		
		pstmt.close();
		conn.close();		
		return              aftcnt; 
		
	}


	//-------------------------------------------------
	// 추가할 데이터를 키보드로 입력받는다
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
	
	// 수정할 데이터를 입력받는다
	private static TUserDTO inputUpdateData() {
		System.out.println("이름:");
		String   username   =  in.nextLine();
		System.out.println("이메일:");
		String   email      =  in.nextLine();
		
		TUserDTO  tuser     =  new TUserDTO( username, email );
		return    tuser;		
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
		
		System.out.println("Press enter key ....");
		in.nextLine();
	}
	
	// 전체 목록을 출력한다
	private static void displayList(ArrayList<TUserDTO> userList) {
		
		if( userList.size() == 0  ) {
			System.out.println("조회한 자료가 없습니다");
			return ;
		}
		
		String   fmt  = "";
		String   msg  = "";
		for (TUserDTO tuser : userList) {
			String  userid    =  tuser.getUserid();
			String  username  =  tuser.getUsername();
			String  email     =  tuser.getEmail();
			msg  = """
			%s %s %s		
			""".formatted(userid, username, email); // Java template 문자열
			System.out.print( msg );
		}
		
		System.out.println("Press enter key ....");
		in.nextLine();
	}


}










