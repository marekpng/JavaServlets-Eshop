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
 * Servlet implementation class DetailObjednavky
 */
public class DetailObjednavky extends HttpServlet {
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
    public DetailObjednavky() {
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
	    int idObjednavky = Integer.parseInt(request.getParameter("idObjednavky"));
	    if(con == null) {
	    	out.print("<p>nemas tu co robit/nemam spojenie na databazu con</p> ");
	    }
	    
	    createHeader(out,request);
	    
	    
	    try {
            PreparedStatement pstmt = con.prepareStatement(
                "SELECT o.ID_tovaru, o.cena, o.ks, s.nazov, s.image FROM obj_polozky o " +
                "INNER JOIN sklad s ON o.ID_tovaru = s.ID WHERE o.ID_objednavky = ?");
            pstmt.setInt(1, idObjednavky);
            ResultSet rs = pstmt.executeQuery();

           
            out.println("<html><body>");
            out.println("<h2>Detaily objednávky " + idObjednavky + "</h2>");
            out.println("<div class='products-container'>");

            
            while (rs.next()) {
                out.println("<div class='product-box'>");
                out.println("<img src='" + rs.getString("image") + "' alt='Product Image' style='width:100px; height:100px;'>");
                out.println("<div class='product-info'>");
                out.println("<h3>" + rs.getString("nazov") + "</h3>");
                out.println("<p>Cena: " + (rs.getDouble("cena") * rs.getInt("ks")) + " €</p>");
                out.println("<p>Počet kusov: " + rs.getInt("ks") + "</p>");
                out.println("</div>");
                out.println("</div>");
            }

            out.println("</div>");
            out.println("<a href='ZoznamObjednavok'>Spat na zoznam objednavok</a>");
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
