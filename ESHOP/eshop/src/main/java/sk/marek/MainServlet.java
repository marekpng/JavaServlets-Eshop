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
import java.sql.Statement;

/**
 * Servlet implementation class MainServlet
 */

public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con = null;
	String errorMessage = "";
	Guard g;

	
	private String URL = "jdbc:mysql://localhost/eshop";
    private String login = "root";
    private String pwd = ""; 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServlet() {
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
           con = DriverManager.getConnection(
                 URL,login,pwd);
        } catch (Exception e) { errorMessage = e.getMessage();}
    }

    protected Connection dajSpojenie(HttpServletRequest request) {
        try {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	  response.setContentType("text/html;charset=UTF-8");
	  PrintWriter out = response.getWriter();
	  out.print("<head> <link rel='stylesheet' href='style.css'> </head>");
	  try {
	   
		  
		  if (con == null) {
          	out.println(errorMessage);
          	return; 
          	}
		  
		  
	     String operacia = request.getParameter("operacia");

     
	     if (operacia == null) { 
         	HttpSession session = request.getSession();
         	if(session.getAttribute("ID") != null) {//KED OTOVRIM LEN TAK bez odoslania formulara tak este skontroluje ci existuje session ak hej tak vypise ak neexistuje tak neopravneny pristup
         		createHeader(out, request);
         		createBody(out, request);

         	} else {
	            	zobrazNeopravnenyPristup(out);
	            	return; 
         	}
         	}
	     
	     if (operacia.equals("login")) { 
         	OverUsera(out, request); 
     	}
	     
	     int id = getLogedUser(request, out);
	     
	     if (id == 0 ) {
	    	 return;
	     }
	     
	    
	     if (operacia.equals("logout")) { 
	    	 odhlas(out, request); 
	    	 return; 
	    	 }
	     if (operacia.equals("nakup")) {

	    	 pridajDoKosika(id, out, request, response);
	    	 }

	     
	     createHeader(out, request);

	     createBody(out, request);

	  } catch (Exception e) {}

	}

	
	


	protected void zobrazNeopravnenyPristup(PrintWriter out) {
        out.println("Nemáš právo tu byť...");
    }

	protected void OverUsera(PrintWriter out, HttpServletRequest request) {
	        try {
	            String meno = request.getParameter("login");
	            String heslo = request.getParameter("pwd");
	            Statement stmt = con.createStatement();
	            String sql = "SELECT MAX(id) AS iid, COUNT(id) AS pocet FROM users "+
	                    " WHERE login = '"+meno+"' AND passwd = '"+heslo+"'";
	            ResultSet rs = stmt.executeQuery(sql);
	            rs.next();
	            HttpSession session = request.getSession();
	            if (rs.getInt("pocet") == 1) {
	              sql = "SELECT id, meno, priezvisko, zlava, je_admin FROM users WHERE login = '"+meno+"'"; 
	              rs = stmt.executeQuery(sql); 
	              rs.next(); 
	              session.setAttribute("ID", rs.getInt("id")); 
	              session.setAttribute("zlava", rs.getInt("zlava"));
	              session.setAttribute("meno", rs.getString("meno"));
	              session.setAttribute("priezvisko", rs.getString("priezvisko"));
	              session.setAttribute("je_admin", rs.getInt("je_admin"));
	            } else { 
	              out.println("Prihlasovacie údaje nie sú v poriadku.");
	              session.invalidate(); 
	            }
	            rs.close();
	            stmt.close();
	           } catch (Exception ex) { out.println(ex.getMessage()); }
	           


			  
	}


	private void odhlas(PrintWriter out, HttpServletRequest request) {
		 out.println("dakujeme za nakup");
		 HttpSession ses = request.getSession();
	 	ses.invalidate();
	 }
	 
	 private void pridajDoKosika(int id_user, PrintWriter out, HttpServletRequest request, HttpServletResponse response) throws IOException {
		   int idTovaru = Integer.parseInt(request.getParameter("productId"));
		   double cena = Double.parseDouble(request.getParameter("productPrice"));
		   System.out.println("Som v meteode pridaj do kosika");

	

		   PreparedStatement pstmt = null;
		   ResultSet rs = null;
		   HttpSession ses = request.getSession();
	       Integer idPouzivatela = (Integer)(ses.getAttribute("ID"));
	       System.out.println("ID POUZIVATELA JE " + idPouzivatela);
		   try {
		       
		        
		        String sql = "SELECT count(ID) AS pocet FROM kosik WHERE ID_pouzivatela = ? AND ID_tovaru = ?";
		        pstmt = con.prepareStatement(sql);
		        pstmt.setInt(1, idPouzivatela);
		        pstmt.setInt(2, idTovaru);
		        rs = pstmt.executeQuery();

		        int pocet = 0;
		        if (rs.next()) {
		            pocet = rs.getInt("pocet");
		        }

		        if (pocet == 0) {
		            
		            sql = "INSERT INTO kosik (ID_pouzivatela, ID_tovaru, cena, ks) VALUES (?, ?, ?, ?)";
		            pstmt = con.prepareStatement(sql);
		            pstmt.setInt(1, idPouzivatela);
		            pstmt.setInt(2, idTovaru);
		            pstmt.setDouble(3, cena);
		            pstmt.setInt(4, 1);
		            pstmt.executeUpdate();
		        } else {
		           
		            sql = "UPDATE kosik SET ks = ks + ?, cena = ? WHERE ID_pouzivatela = ? AND ID_tovaru = ?";
		            pstmt = con.prepareStatement(sql);
		            pstmt.setInt(1, 1); 
		            pstmt.setDouble(2, cena);
		            pstmt.setInt(3, idPouzivatela);
		            pstmt.setInt(4, idTovaru);
		            pstmt.executeUpdate();
		        }
		        
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		   response.sendRedirect("MainServlet");
		   
		   
		
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
		    out.println("<a href='ZoznamObjednavok'>Zoznam objednávok</a>");

		   
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


	private void createBody(PrintWriter out, HttpServletRequest request) {
	    HttpSession ses = request.getSession();
	    out.print("<h3>PRODUKTY</h3>");
	    Integer zlava = (Integer) ses.getAttribute("zlava");

	    try {
	        Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery("select * from sklad");
	        
	    
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
	            out.println("<p>Cena: " + zaokruhlenaCena + " €</p>");
	            out.println("<p>Na sklade: " + kusyNaSklade + " ks</p>");
	            
	            out.println("<form method='get' action='DetailProduktu'>");
	            out.println("<input type='hidden' name='productId' value='" + id + "'>");
	            out.println("<button type='submit'>Detail produktu</button>");
	            out.println("</form>");
	            
	            
	            
	            out.println("<form  method='post' action='MainServlet'>");
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
	        
	    	
	    }
	}
	



	private int getLogedUser(HttpServletRequest request, PrintWriter out) {
		// TODO Auto-generated method stub
		   HttpSession ses = request.getSession();
		   int id = (Integer)ses.getAttribute("ID");
		   if (id == 0) {
		       out.println("Neprihlásený user");
		       zobrazNeopravnenyPristup(out);
		   }
		   return id;

	}


	private boolean badOperation(String operacia, PrintWriter out) {
		// TODO Auto-generated method stub
		if (operacia == null) {
		       zobrazNeopravnenyPristup(out);
		       return true;
		   }
		   return false;

	}


	private boolean badConnection(PrintWriter out) {
		// TODO Auto-generated method stub
		if (errorMessage.length() > 0) {
	        out.println(errorMessage);
	        return true;
	    }
	    return false;

	}

}
