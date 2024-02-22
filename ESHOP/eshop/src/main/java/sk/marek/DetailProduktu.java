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
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Servlet implementation class DetailProduktu
 */
public class DetailProduktu extends HttpServlet {
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
    public DetailProduktu() {
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
	    createBody(out,request);
	}
	
	private void createBody(PrintWriter out, HttpServletRequest request) {
	    HttpSession ses = request.getSession();
	    out.print("<h3>PRODUKTY</h3>");
	    Integer zlava = (Integer) ses.getAttribute("zlava");
	    Connection con = dajSpojenie(request);
	    try {
	        Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery("select * from sklad where id = " + request.getParameter("productId"));
	        
	        out.println("<div class='products-container'>");

	        while (rs.next()) {
	            int id = rs.getInt("ID");
	            String nazov = rs.getString("nazov");
	            int kusyNaSklade = rs.getInt("ks");
	            String image = rs.getString("image");
	            double aktCena = rs.getDouble("cena")*(100-zlava)/100;
	            double zaokruhlenaCena = Math.round(aktCena * 100.0) / 100.0;
	            
	            out.println("<div class='product-box'>");
	            out.println("<img src='" + image + "' alt='Product Image'>");
	            out.println("<div class='product-info'>");
	            out.println("<h3>" + nazov + "</h3>");
	            out.println("<p>Popis: " + rs.getString("popis")+ " </p>");
	            out.println("<p>Cena: " + zaokruhlenaCena + " €</p>");
	            out.println("<p>Na sklade: " + kusyNaSklade + " ks</p>");
	            
	            
	            out.println("<form method='post' action='MainServlet'>");
	            out.println("<input type='hidden' name='operacia' value='nakup'>");
	            out.println("<input type='hidden' name='productId' value='" + id + "'>");
	            out.println("<input type='hidden' name='productName' value='" + nazov + "'>");
	            out.println("<input type='hidden' name='productPrice' value='" + zaokruhlenaCena + "'>");
	            out.println("<input type='number' name='quantity' value='1' min='1' max='" + kusyNaSklade + "'>");
	            out.println("<button type='submit'>Pridať do košíka</button>");
	            out.println("</form>");
	            
	            

	            out.println("</div>"); 
	            out.println("</div>"); 
	        }

	        out.println("</div>"); 
	        stmt.close();
	    } catch (Exception e) {
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
