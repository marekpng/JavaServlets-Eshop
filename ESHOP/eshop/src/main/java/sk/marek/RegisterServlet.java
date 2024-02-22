package sk.marek;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Servlet implementation class RegisterServlet
 */
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String errorMessage = "";
	Guard g;
	Connection c;
	
	private String URL = "jdbc:mysql://localhost/eshop";
    private String login = "root";
    private String pwd = "";   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
 
    /**
  	 * @see Servlet#init(ServletConfig)
  	 */
    @Override
    public void init() throws ServletException {
        super.init();
        try {
           Class.forName("com.mysql.cj.jdbc.Driver");
           c = DriverManager.getConnection(
                 URL,login,pwd);
        } catch (Exception e) { errorMessage = e.getMessage();}
    }

    
    
    
    protected Connection dajSpojenie(HttpServletRequest request) {
        try {
         HttpSession session = request.getSession();
          Connection c = (Connection)session.getAttribute("spojenie"); 
          if (c == null) { 
        	  Class.forName("com.mysql.cj.jdbc.Driver");
            c = DriverManager.getConnection(URL, login, pwd);
            session.setAttribute("spojenie", c); 
            g = new Guard(c);
          } 
          return c; 
        } catch(Exception e) {return null;}     
      }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 response.setContentType("text/html;charset=UTF-8");
		  PrintWriter out = response.getWriter();
		  out.print("<head> <link rel='stylesheet' href='style.css'> </head>");
		 
		String operacia = request.getParameter("operacia");
		if(operacia.equals("registrovat")) {
			zobrazFormular(out);
		}
		if(operacia.equals("vykonajRegistraciu")) {
			vykonajRegistraciu(request, response);
		}
	}

	private void vykonajRegistraciu(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		
		 String login = request.getParameter("login");
	        String passwd = request.getParameter("passwd");
	        String adresa = request.getParameter("adresa");
	        
	        Connection con = dajSpojenie(request);
	        
			
	            
	        try {
	        	
        	
	            PreparedStatement pstmt = con.prepareStatement("SELECT COUNT(*) FROM users WHERE login = ? OR adresa = ?");
	            pstmt.setString(1, login);
	            pstmt.setString(2, adresa);

	            ResultSet rs = pstmt.executeQuery();
	            if (rs.next() && rs.getInt(1) > 0) {
	            	out.print("<p>takyto pouzivatel uz existuje skus znova</p> ");
	                
	            } else {
	            	System.out.print("Som pred setom do databazky");
	            	 PreparedStatement pstmtAddUser = con.prepareStatement(
	            		        "INSERT INTO users (login, passwd, adresa, zlava, meno, priezvisko, poznamky, je_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
	            		    pstmtAddUser.setString(1, login);
	            		    pstmtAddUser.setString(2, passwd);
	            		    pstmtAddUser.setString(3, adresa);
	            		    pstmtAddUser.setInt(4, 0); 
	            		    pstmtAddUser.setString(5, request.getParameter("meno"));
	            		    pstmtAddUser.setString(6, request.getParameter("priezvisko"));
	            		    pstmtAddUser.setString(7, request.getParameter("poznamky"));
	            		    pstmtAddUser.setInt(8, 0);
	            		    System.out.print("tu som sa dostal setol som to");
	            		   
	            		    pstmtAddUser.executeUpdate();
	            		    pstmtAddUser.close();

	            		    
	            		    response.sendRedirect("/eshop");
	              
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	}


	private void zobrazFormular(PrintWriter out) {
		// TODO Auto-generated method stub
		out.println("<form action='RegisterServlet' method='post'>");
		out.println("    <label for='login'>Login (email):</label>");
		out.println("    <input type='email' id='login' name='login' required><br>");

		out.println("    <label for='passwd'>Heslo:</label>");
		out.println("    <input type='password' id='passwd' name='passwd' required><br>");

		out.println("    <label for='adresa'>Adresa:</label>");
		out.println("    <input type='text' id='adresa' name='adresa' required><br>");

		out.println("    <label for='meno'>Meno:</label>");
		out.println("    <input type='text' id='meno' name='meno' required><br>");

		out.println("    <label for='priezvisko'>Priezvisko:</label>");
		out.println("    <input type='text' id='priezvisko' name='priezvisko' required><br>");

		out.println("    <label for='poznamky'>Poznámky:</label>");
		out.println("    <textarea id='poznamky' name='poznamky'></textarea><br>");
		out.println("<input type='hidden' name='operacia' value='vykonajRegistraciu' >");
		out.println("    <input type='submit' value='Registrovať'>");
		out.println("</form>");
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
