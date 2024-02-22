package sk.marek;

import jakarta.servlet.ServletConfig;
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
 * Servlet implementation class ZoznamObjednavok
 */
public class ZoznamObjednavok extends HttpServlet {
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
    public ZoznamObjednavok() {
        super();
        // TODO Auto-generated constructor stub
    }

    
    protected Connection dajSpojenie(HttpServletRequest request) {
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
         HttpSession session = request.getSession();
          Connection c = (Connection)session.getAttribute("spojenie"); 
          if (c == null) { 
            c = DriverManager.getConnection(URL, login, pwd);
            session.setAttribute("spojenie", c); // zapis do session
            g = new Guard(c);
          } 
          return c; 
        } catch(Exception e) {return null;}     
      }
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		response.setContentType("text/html;charset=UTF-8");
		out.print("<head> <link rel='stylesheet' href='main.css'> </head>");

	    HttpSession ses = request.getSession();

	    Connection con = dajSpojenie(request);
	    if(con == null) {
	    	out.print("<p>nemas tu co robit/nemam spojenie na databazu con</p> ");
	    }
	    
	    createHeader(out,request);
	    
	    int idPouzivatela = (int) ses.getAttribute("ID");
	    try {
            
            PreparedStatement pstmt = con.prepareStatement(
                "SELECT * FROM obj_zoznam WHERE ID_pouzivatela = ?");
            pstmt.setInt(1, idPouzivatela); 
            ResultSet rs = pstmt.executeQuery();

            
            out.println("<html><body>");
            out.println("<h2>Objednávky zákazníka s ID " +idPouzivatela + " </h2>");
            
            out.println("<table border='1'><tr><th>Číslo objednávky</th><th>Dátum</th><th>Suma</th><th>Stav</th><th>Detail Objednavky</th></tr>");

            
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getString("obj_cislo") + "</td>");
                out.println("<td>" + rs.getDate("datum_objednavky") + "</td>");
                out.println("<td>" + rs.getDouble("suma") + "</td>");
                out.println("<td>" + rs.getString("stav") + "</td>");
                out.println("<td><a href='DetailObjednavky?idObjednavky=" + rs.getString("ID") + "'>" + rs.getString("obj_cislo") + "</a></td>");
                out.println("</tr>");
            }

            out.println("</table>");
          
            out.println("</body></html>");

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	


	private void createHeader(PrintWriter out, HttpServletRequest request){
	    HttpSession ses = request.getSession();
	    String vypis = (String)ses.getAttribute("meno") + " " + (String)ses.getAttribute("priezvisko");
	    int je_admin = (int) ses.getAttribute("je_admin");

	    out.print("<head>");
	    out.print("<link rel='stylesheet' type='text/css' href='style.css'>");  
	    out.print("</head>");
	    out.print("<body>");

	    out.println("<div class='navbar'>");
	    if(je_admin == 1) {
	        out.println("<a href='AdminZoznamObjednavok'>Admin panel</a>");
	    }
	    
	    out.println("<a href='MainServlet'>Domov</a>");
	    
	    out.println("<a href='KosikServlet'>Košík</a>");
	    
	    out.println("<div style='align-self: flex-end;'>");
	    out.println("<span>"+vypis+"</span>"); 
	    
	    
	    out.println("<form action='MainServlet' method='post'>");
	    out.println("<input type='hidden' name='operacia' value='logout'>");
	    out.println("<input type='submit' value='Odhlásiť'>");
	    out.println("</form>");
	    out.println("</div>");

	    out.println("</div>");
	    out.print("</body>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
