package db03;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
		do { // 무한루프 시작 do~while 문
			// 화면출력
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
			String    choice  =   in.nextLine(); // 사용자가 키보드로 입력한 메뉴번호 읽어와라
			
			TUserDTO   tuser = null;
			
			switch( choice ) {
			case  "1":  // 회원 목록 
				ArrayList<TUserDTO> userList = getTUserList(); // 데이터가 여러개일 경우 ArrayList 를 사용한다
				displayList(userList);
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

	// 전체 목록을 출력한다
	private static void displayList(ArrayList<TUserDTO> userList) {
		if( userList.size() == 0 ) {
			System.out.println("조회한 자료가 없습니다");
			return;
		}
		
		String fmt = "";
		String msg = "";
		for (TUserDTO tuser : userList) {
			String userid = tuser.getUserid();
			String username = tuser.getUsername();
			String email = tuser.getEmail();
			// """ ... """ : java 15의 텍스트 블록, 여러 줄 문자열을 편하게 작성
			msg = """
					%s %s %s
					""".formatted(userid, username, email); // java template 문
                        // %s 자리에 변수들을 순서대로 채워 넣는다
			System.out.print(msg);
		}
		
		System.out.println("Press enter key ....");
		in.nextLine();
	}
	
	// 1. 전체 목록 조회 - db에서
	private static ArrayList<TUserDTO> getTUserList()
			throws ClassNotFoundException, SQLException {
		// 오라클 드라이버 로드
		Class.forName( driver );
		// 연결 : DB에 주소/아이디/비번 접속해서 통로 conn 만들어
		Connection          conn   =  DriverManager.getConnection(url, dbuid, dbpwd); // DB 연결
		// SQL 준비 : 모든 회원 정보를 아이디 순으로 가져오는 명령서를 준비
		String              sql    =  " SELECT * FROM TUSER ";
		                    sql   +=  " ORDER BY USERID ASC "; // USERID 를 오름차순으로 정리
        // PreparedStatement : SQL을 미리 컴파일해서 실행 준비를 마침		                   
		PreparedStatement   pstmt  =  conn.prepareStatement( sql ); // 자바에서 제공하는 sql문장을 실행하기 위해 사용
		// 실행 및 결과 수신: select 문 실행결과를 rs에 담는다
		ResultSet            rs    = pstmt.executeQuery(); // pstmt.executeQuery(); 는 준비된 SQL 문장(SELECT)를 데이터베이스에 실행하라는 명령어
		
		// 결과 담기
		ArrayList<TUserDTO> userList = new ArrayList<>(); // 배열 생성 담을그릇 생성
		
		// rs.next() 는 '다음줄이 있는가?' 를 확인하고 커서를 내린다.
		while ( rs.next( ) ) {
			// 현재 커서가 있늕 ㅜㄹ의 데이터를 컬럼명으로 읽어온다.
			String userid   = rs.getString("userid");
			String username = rs.getString("username");
			String email    = rs.getString("email");
			// 읽어온 데이터로 객체를 생성해 리스트에 차곡차곡 쌓는다
			TUserDTO tuser = new TUserDTO(userid, username, email);
			userList.add(tuser);
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
		// " SELECT * FROM TUSER  WHERE  USERID = ?" 여기서 '?' 는 변수를 뜻한다
		String              sql    =  " SELECT * FROM TUSER  WHERE  USERID = ?";
		//String              sql    =  " SELECT * FROM TUSER  WHERE  UPPER(USERID) = UPPER(?)"; 소문자로 입력하여도 대문자로 입력받게 만드는 방법
		
		PreparedStatement   pstmt  =  conn.prepareStatement( sql );
		// pstmt.setSting(순서, 값)을 사용해 첫번째 '?' 자리에 데이터를 채운다
		// uid.toUpperCase()는 사용자가 소문자로 써도 DB에서 대문자로 찾을수 있게 
		pstmt.setString(1, uid.toUpperCase() );
		
		TUserDTO  tuser = null;
		
		ResultSet           rs     =  pstmt.executeQuery();
		// 조회가 된다면 (if 문을) 그렇지않으면 null 인상태로 반환
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

	// 3. db 에 insert 한다
	private static int addTUser(TUserDTO tuser) throws SQLException, ClassNotFoundException {
		
		Class.forName(driver);		
		Connection          conn   =  DriverManager.getConnection(url, dbuid, dbpwd);
		
		String              sql    =  "";
		// (?, ?, ?) = '?' 는 데이터가 들어갈 빈칸 이다. 즉 3개의 값을 넣겟다는 준비
		sql   +=  " INSERT  INTO   TUSER  VALUES (?, ?, ?)";
		
		PreparedStatement   pstmt  =  conn.prepareStatement( sql );
		// 자바 객체(DTO)에 들어있는 값들을 SQL의 '?'에 순서대로 매칭
		pstmt.setString(1, tuser.getUserid());   // 아이디 꺼내기
		pstmt.setString(2, tuser.getUsername()); // 이름 꺼내기
		pstmt.setString(3, tuser.getEmail());	// 이메일 꺼내기
		
		// executeUpdate() 는 select 와 달리 데이터를 '건드리는' 작업은 update 를 사용
		// 결과로 ' 성공한 행의 개수' 를 숫자로 돌려받는다
		int  aftcnt =  pstmt.executeUpdate();	 // 다채웟다면 채워진 명령서를 db로 발송한다	
		
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










