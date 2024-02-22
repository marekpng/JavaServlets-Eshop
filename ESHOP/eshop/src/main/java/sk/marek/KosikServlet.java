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
import java.sql.Statement;
import java.time.LocalDate;

import org.apache.jasper.tagplugins.jstl.core.Catch;

/**
 * Servlet implementation class KosikServlet
 */
public class KosikServlet extends HttpServlet {
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
    public KosikServlet() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/html;charset=UTF-8");
		

	    HttpSession ses = request.getSession();

	    Connection con = dajSpojenie(request);
	    if(con == null) {
	    	out.print("<p>nemas tu co robit/nemam spojenie na databazu con</p> ");
	    }
	    
	    createHeader(out,request);

	    
	    
	    
	    try {
	        int id = (int)ses.getAttribute("ID");
	        PreparedStatement pstmt = con.prepareStatement(
	            "SELECT s.ID as id_tovaru, s.nazov AS nazov_tovaru, s.cena, s.image, k.ks FROM kosik k INNER JOIN sklad s ON k.ID_tovaru = s.ID WHERE k.ID_pouzivatela = ?");
	        pstmt.setInt(1, id); 
	        ResultSet rs = pstmt.executeQuery();
	        Integer zlava = (Integer) ses.getAttribute("zlava");
	        out.println("<div class='products-container'>");
	        while (rs.next()) {
	            String nazov = rs.getString("nazov_tovaru");
	            int idProduktu = rs.getInt("id_tovaru");

	            String image = rs.getString("image");
	            int ks = rs.getInt("ks");

	            double aktCena = rs.getDouble("cena")*(100-zlava)/100;
	            double zaokruhlenaCena = Math.round(aktCena * 100.0) / 100.0;
	            out.println("<div class='product-box'>");
	            out.println("<img src='" + image + "' alt='Product Image'>");
	            out.println("<div class='product-info'>");
	            out.println("<h3>" + nazov + "</h3>");
	            out.println("<p>Cena: " + zaokruhlenaCena + " €</p>");
	            out.println("<p>Počet kusov: " + ks + "</p>");
	            
	         
	            out.println("<form method='post' action='KosikServlet'>");
	            out.println("<input type='hidden' name='operacia' value='zvysit'>");
	            out.println("<input type='hidden' name='productId' value='" + idProduktu + "'>");
	            out.println("<button type='submit'>+</button>");
	            out.println("</form>");

	          
	            out.println("<form method='post' action='KosikServlet'>");
	            out.println("<input type='hidden' name='operacia' value='znizit'>");
	            out.println("<input type='hidden' name='productId' value='" + idProduktu + "'>");
	            out.println("<button type='submit'>-</button>");
	            out.println("</form>");

	            
	            out.println("<span>Počet kusov: " + ks + "</span>");
	            
	            
	            
	            out.println("</div>");
	            out.println("</div>");
	        }
	        out.println("</div>");
	        rs.close();
	        pstmt.close();
	        
//	        out.println("<form method='post' action='KosikServlet'>");
//            out.println("<input type='hidden' name='operacia' value='vykonatObjednavku'>");
//
//            out.println("<button type='submit'>Vykonat objednavku</button>");
//            out.println("</form>");
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    
	    
	    
	    try {
            System.out.println("ID uzivatel je " +  (int)ses.getAttribute("ID"));
            String sql = "SELECT COUNT(k.ID_tovaru) as pocet_produktov FROM kosik k INNER JOIN sklad s ON k.ID_tovaru = s.ID WHERE k.ID_pouzivatela = ?";
            
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, (int)ses.getAttribute("ID")); 
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int pocetProduktov = rs.getInt("pocet_produktov");

                if (pocetProduktov > 0) {
                    
                    out.println("<form method='post' action='KosikServlet'>");
                    out.println("<input type='hidden' name='operacia' value='vykonatObjednavku'>");
                    out.println("<button type='submit'>Vykonat objednavku</button>");
                    out.println("</form>");
                } else {
                    
                    out.println("<h3>Košík je prázdny</h3>");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
	    
	    
	    String operacia = request.getParameter("operacia");
	    if(operacia.equals("zvysit")) {

	    	int idTovaru = Integer.parseInt(request.getParameter("productId"));
	    	
	    	zvysitMnozstvo(out, request, idTovaru, response);
	    }
	    if(operacia.equals("znizit")) {
	    	
	    	try {
	    		int idTovaru = Integer.parseInt(request.getParameter("productId"));
				znizitMnozstvo(con, idTovaru, ses, response);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    }
	    if(operacia.equals("vykonatObjednavku")) {
	    	vykonajObjednavku(request, response);
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

	

	private void vykonajObjednavku(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		HttpSession ses = request.getSession();
        int idPouzivatela = (int)ses.getAttribute("ID");
        Connection con = dajSpojenie(request);
        int zlava = (int) ses.getAttribute("zlava");
        try {
        	synchronized (this) {
        		if(suProduktyDostupne(idPouzivatela, con)) {
		            
		            PreparedStatement pstmtZoznam = con.prepareStatement(
		                "INSERT INTO obj_zoznam (obj_cislo, datum_objednavky, ID_pouzivatela, suma, stav) VALUES (?, ?, ?, ?, ?)",
		                Statement.RETURN_GENERATED_KEYS);
		            pstmtZoznam.setString(1, generujObjCislo()); 
		            pstmtZoznam.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
		            pstmtZoznam.setInt(3, idPouzivatela);
		            pstmtZoznam.setDouble(4, vypocitajSumu(idPouzivatela, con,zlava)); 
		            pstmtZoznam.setString(5, "spracuva sa");
		            pstmtZoznam.executeUpdate();
		            
		            
		         
	                PreparedStatement pstmtAktualizujSklad = con.prepareStatement(
	        		        "UPDATE sklad s INNER JOIN kosik k ON s.ID = k.ID_tovaru " +
	        		        "SET s.ks = s.ks - k.ks WHERE k.ID_pouzivatela = ?");
	        		    pstmtAktualizujSklad.setInt(1, idPouzivatela);
	        		    pstmtAktualizujSklad.executeUpdate();
	        		   
	                System.out.println("SKLAD AKTUALIZOVANY");
		            
		            
		            
		            ResultSet rsKeys = pstmtZoznam.getGeneratedKeys();
			            if (rsKeys.next()) {
			                int idObjednavky = rsKeys.getInt(1);
			
			                
			                PreparedStatement pstmtPolozky = con.prepareStatement(
			                    "INSERT INTO obj_polozky (ID_objednavky, ID_tovaru, cena, ks) SELECT ?, ID_tovaru, cena, ks FROM kosik WHERE ID_pouzivatela = ?");
			                pstmtPolozky.setInt(1, idObjednavky);
			                pstmtPolozky.setInt(2, idPouzivatela);
			                pstmtPolozky.executeUpdate();
			
			                
			                PreparedStatement pstmtVymazKosik = con.prepareStatement("DELETE FROM kosik WHERE ID_pouzivatela = ?");
			                pstmtVymazKosik.setInt(1, idPouzivatela);
			                pstmtVymazKosik.executeUpdate();
			                
			                
			                
			                
			                
			                response.sendRedirect("MainServlet");
			            }
			            
		        	  } else {
		        		  PrintWriter out = response.getWriter();
		        		  out.print("<p> nedostatok produktov na sklade");
		        	  }
        		}
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public void aktualizujSklad(Connection con, int idPouzivatela) throws SQLException {
		System.out.println("SOM V METODE AKTUALIZUJ SKLAD");
		PreparedStatement pstmtAktualizujSklad = con.prepareStatement(
		        "UPDATE sklad s INNER JOIN kosik k ON s.ID = k.ID_tovaru " +
		        "SET s.ks = s.ks - k.ks WHERE k.ID_pouzivatela = ?");
		    pstmtAktualizujSklad.setInt(1, idPouzivatela);
		    pstmtAktualizujSklad.executeUpdate();
		    pstmtAktualizujSklad.close();
	}
	
	private boolean suProduktyDostupne(int idPouzivatela, Connection con) throws SQLException {
	    
	    PreparedStatement pstmt = con.prepareStatement(
	        "SELECT k.ID_tovaru, k.ks, s.ks AS ks_na_sklade FROM kosik k JOIN sklad s ON k.ID_tovaru = s.ID WHERE k.ID_pouzivatela = ?");
	    pstmt.setInt(1, idPouzivatela);
	    ResultSet rs = pstmt.executeQuery();

	    while (rs.next()) {
	        int ksVKosiku = rs.getInt("ks");
	        int ksNaSklade = rs.getInt("ks_na_sklade");

	        if (ksVKosiku > ksNaSklade) {
	            
	            return false;
	        }
	    }

	    rs.close();
	    pstmt.close();
	    return true;
	}


	private double vypocitajSumu(int idPouzivatela, Connection con, int zlava) throws SQLException {
	    double suma = 0;
	    PreparedStatement pstmt = con.prepareStatement(
	        "SELECT SUM(cena * ks) AS total FROM kosik WHERE ID_pouzivatela = ?");
	    pstmt.setInt(1, idPouzivatela);
	    ResultSet rs = pstmt.executeQuery();
	    
	    if (rs.next()) {
	    	System.out.print("Suma je rs.next= " + rs.getDouble("total"));
	        suma = rs.getDouble("total");
	    }

	    rs.close();
	    pstmt.close();
	    

	    System.out.print("Suma je= " + suma);
	    return suma ;
	}


	private String generujObjCislo() {
	    return "OBJ" + System.currentTimeMillis();
	}


	private void zvysitMnozstvo(PrintWriter out, HttpServletRequest request, int idProduktu, HttpServletResponse response ) {
		// TODO Auto-generated method stub
		try {
		HttpSession ses = request.getSession();
		Connection con = (Connection) ses.getAttribute("spojenie");
		int idPouzivatela = (int) ses.getAttribute("ID");
		PreparedStatement pstmtSklad = con.prepareStatement("SELECT ks FROM sklad WHERE ID = ?");
	    pstmtSklad.setInt(1, idProduktu);
	    ResultSet rsSklad = pstmtSklad.executeQuery();
	   

	    if (rsSklad.next()) {
	        int ksNaSklade = rsSklad.getInt("ks");

	       
	        PreparedStatement pstmtKosik = con.prepareStatement("SELECT ks FROM kosik WHERE ID_tovaru = ? AND ID_pouzivatela = ?");
	        pstmtKosik.setInt(1, idProduktu);
	        pstmtKosik.setInt(2, idPouzivatela);
	        ResultSet rsKosik = pstmtKosik.executeQuery();

	        if (rsKosik.next()) {
	            int ksVKosiku = rsKosik.getInt("ks");

	           
	            if (ksVKosiku < ksNaSklade) {
	                PreparedStatement pstmtUpdate = con.prepareStatement("UPDATE kosik SET ks = ks + 1 WHERE ID_tovaru = ? AND ID_pouzivatela = ?");
	                pstmtUpdate.setInt(1, idProduktu);
	                pstmtUpdate.setInt(2, idPouzivatela);
	                pstmtUpdate.executeUpdate();
	                pstmtUpdate.close();
	            } else {
	            	out.print("Viac nie je na sklade !");
	            }
	        }

	        pstmtKosik.close();
	        rsKosik.close();
	    }

	    pstmtSklad.close();
	    rsSklad.close();
	    response.sendRedirect("KosikServlet");
		} catch (Exception e) {
			out.print("Nieco sa stalo zle v zvysitMnozstvo metode");
			// TODO: handle exception
		}
		
	}
	
	
	private void znizitMnozstvo(Connection con, int idProduktu, HttpSession ses, HttpServletResponse response) throws SQLException, IOException {
		int idPouzivatela = (int) ses.getAttribute("ID");
	    
	    PreparedStatement pstmtKosik = con.prepareStatement("SELECT ks FROM kosik WHERE ID_tovaru = ? AND ID_pouzivatela = ?");
	    pstmtKosik.setInt(1, idProduktu);
	    pstmtKosik.setInt(2, idPouzivatela);
	    ResultSet rsKosik = pstmtKosik.executeQuery();

	    if (rsKosik.next()) {
	        int ksVKosiku = rsKosik.getInt("ks");

	      
	        if (ksVKosiku > 1) {
	            PreparedStatement pstmtUpdate = con.prepareStatement("UPDATE kosik SET ks = ks - 1 WHERE ID_tovaru = ? AND ID_pouzivatela = ?");
	            pstmtUpdate.setInt(1, idProduktu);
	            pstmtUpdate.setInt(2, idPouzivatela);
	            pstmtUpdate.executeUpdate();
	            pstmtUpdate.close();
	        } else {
	            PreparedStatement pstmtDelete = con.prepareStatement("DELETE FROM kosik WHERE ID_tovaru = ? AND ID_pouzivatela = ?");
	            pstmtDelete.setInt(1, idProduktu);
	            pstmtDelete.setInt(2, idPouzivatela);
	            pstmtDelete.executeUpdate();
	            pstmtDelete.close();
	        }
	    }

	    pstmtKosik.close();
	    rsKosik.close();
	    response.sendRedirect("KosikServlet");
	}
	


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
