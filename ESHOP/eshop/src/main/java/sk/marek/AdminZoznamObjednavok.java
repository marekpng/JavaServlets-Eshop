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
 * Servlet implementation class AdminZoznamObjednavok
 */
public class AdminZoznamObjednavok extends HttpServlet {
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
    public AdminZoznamObjednavok() {
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
	    if((int)ses.getAttribute("je_admin") == 0) {
	    	out.print("<p>nemas tu co robit niesi admin</p>");
	    	
	    }
	    createHeader(out,request);
	    
	    out.println("<form action='AdminZoznamObjednavok' method='post'>");
 	   out.println("<input type='hidden' name='operacia' value='zobrazObjednavky'>");
 	   out.println("<input type='submit' value='zobraz vsetky objednavky'>");
 	   out.println("</form>");
 	   
 	  out.println("<form action='AdminZoznamObjednavok' method='post'>");
	   out.println("<input type='hidden' name='operacia' value='zobrazUserov'>");
	   out.println("<input type='submit' value='zobraz Userov'>");
	   out.println("</form>");
	    
	    

            
            
            String operacia = request.getParameter("operacia");
            if(operacia.equals("zmenaStavuObjednavok")) {
            	zmenStavObjednavky(request, response);
            }
            if(operacia.equals("zobrazObjednavky")) {
            	zobrazObjednavky(out,request, response, ses);
            }
            if(operacia.equals("zmenStav")) {
            	zmenaStavu(request, response, out);
            }
            if(operacia.equals("odstranObjednavku")) {
            	odstranObjednavku(request, response);
            }
            if(operacia.equals("zobrazUserov")) {
            	zobrazUserov(request,response);
            }
            if(operacia.equals("zmenRolu")) {
            	zmenRolu(request,response);
            }
            if(operacia.equals("detailObjednavky")) {
            	detailObjednavky(request,response);
            }
            

	}
	
	private void detailObjednavky(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

	private void zmenRolu(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		int idPouzivatela = Integer.parseInt(request.getParameter("idPouzivatela"));
        Connection con = dajSpojenie(request); 

        try {
            
            PreparedStatement pstmtGet = con.prepareStatement("SELECT je_admin FROM users WHERE ID = ?");
            pstmtGet.setInt(1, idPouzivatela);
            ResultSet rs = pstmtGet.executeQuery();

            if (rs.next()) {
                int jeAdmin = rs.getInt("je_admin");
                int novyStav = jeAdmin == 1 ? 0 : 1;  

                
                PreparedStatement pstmtUpdate = con.prepareStatement(
                    "UPDATE users SET je_admin = ? WHERE ID = ?");
                pstmtUpdate.setInt(1, novyStav);
                pstmtUpdate.setInt(2, idPouzivatela);
                pstmtUpdate.executeUpdate();
                pstmtUpdate.close();
            }

            rs.close();
            pstmtGet.close();

            response.sendRedirect("AdminZoznamObjednavok");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("ZobrazVsetkychPouzivatelovServlet?error=updateFailed");
        }

	}


	private void zobrazUserov(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Connection con = dajSpojenie(request); 

        try {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM users");
            ResultSet rs = pstmt.executeQuery();

            
            out.println("<html><body>");
            out.println("<h2>Zoznam používateľov</h2>");
            out.println("<table border='1'><tr><th>ID</th><th>Meno</th><th>Priezvisko</th><th>Rola</th><th>Zmeniť</th></tr>");
            String status = "";
            while (rs.next()) {
            	if(rs.getInt("je_admin") == 1) {
            		status = "admin";
            	} else {
            		status = "user";
            	}
                int idPouzivatela = rs.getInt("ID");
                out.println("<tr>");
                out.println("<td>" + idPouzivatela + "</td>");
                out.println("<td>" + rs.getString("meno") + "</td>");
                out.println("<td>" + rs.getString("priezvisko") + "</td>");
                out.println("<td>" + status + "</td>");

                out.println("<td><form action='AdminZoznamObjednavok' method='post'>");
         	   out.println("<input type='hidden' name='operacia' value='zmenRolu'>");
         	  out.println("<input type='hidden' name='idPouzivatela' value='"+ idPouzivatela + "'>");
         	   out.println("<input type='submit' value='zmen rolu'>");
         	   out.println("</form></td>");
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


	private void zobrazObjednavky(PrintWriter out, HttpServletRequest request, HttpServletResponse response, HttpSession ses) {
		// TODO Auto-generated method stub
		int idPouzivatela = (int) ses.getAttribute("ID");
	    Connection con = dajSpojenie(request);
	    try {
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM obj_zoznam");
            ResultSet rs = pstmt.executeQuery();

            
            out.println("<html><body>");
            out.println("<h2>Všetky objednávky</h2>");
            out.println("<table border='1'><tr><th>Číslo objednávky</th><th>Stav</th><th>Detail objednavky</th><th>Zmenit stav</th><th>Odstran objednavku</th></tr>");

            while (rs.next()) {
                int idObjednavky = rs.getInt("ID");
                out.println("<tr>");
                out.println("<td>" + rs.getString("obj_cislo") + "</td>");
                out.println("<td>" + rs.getString("stav") + "</td>");

                out.println("<td><form action='AdminZoznamObjednavok' method='post'>");
          	   out.println("<input type='hidden' name='operacia' value='detailObjednavky'>");
          	  out.println("<input type='hidden' name='idObjednavky' value='"+ idObjednavky + "'>");
          	   out.println("<input type='submit' value='Detail objednavky'>");
          	   out.println("</form></td>");
                
                
                out.println("<td><form action='AdminZoznamObjednavok' method='post'>");
         	   out.println("<input type='hidden' name='operacia' value='zmenaStavuObjednavok'>");
         	  out.println("<input type='hidden' name='idObjednavky' value='"+ idObjednavky + "'>");
         	   out.println("<input type='submit' value='Zmenit stav'>");
         	   out.println("</form></td>");
         	   
         	  out.println("<td><form action='AdminZoznamObjednavok' method='post'>");
        	   out.println("<input type='hidden' name='operacia' value='odstranObjednavku'>");
        	  out.println("<input type='hidden' name='idObjednavky' value='"+ idObjednavky + "'>");
        	   out.println("<input type='submit' value='Odstranit objednavku'>");
        	   out.println("</form></td>");
         	   
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


	private void odstranObjednavku(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		int idObjednavky = Integer.parseInt(request.getParameter("idObjednavky"));
        Connection con = dajSpojenie(request); 

        try {
            con.setAutoCommit(false); 

           
            PreparedStatement pstmtVratNaSklad = con.prepareStatement(
                "UPDATE sklad s INNER JOIN obj_polozky o ON s.ID = o.ID_tovaru " +
                "SET s.ks = s.ks + o.ks WHERE o.ID_objednavky = ?");
            pstmtVratNaSklad.setInt(1, idObjednavky);
            pstmtVratNaSklad.executeUpdate();

            
            PreparedStatement pstmtOdstranPolozky = con.prepareStatement(
                "DELETE FROM obj_polozky WHERE ID_objednavky = ?");
            pstmtOdstranPolozky.setInt(1, idObjednavky);
            pstmtOdstranPolozky.executeUpdate();

            
            PreparedStatement pstmtOdstranObjednavku = con.prepareStatement(
                "DELETE FROM obj_zoznam WHERE ID = ?");
            pstmtOdstranObjednavku.setInt(1, idObjednavky);
            pstmtOdstranObjednavku.executeUpdate();

            con.commit(); 

            response.sendRedirect("AdminZoznamObjednavok");
        }catch (Exception e) {
			// TODO: handle exception
		}
	}


	private void zmenaStavu(HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws IOException {
		// TODO Auto-generated method stub
		int idObjednavky = Integer.parseInt(request.getParameter("idObjednavky"));
	    String novyStav = request.getParameter("novyStav");
	    Connection con = dajSpojenie(request); 
	    
	    try {
	        
	        PreparedStatement pstmt = con.prepareStatement(
	            "UPDATE obj_zoznam SET stav = ? WHERE ID = ?");
	        pstmt.setString(1, novyStav);
	        pstmt.setInt(2, idObjednavky);
	        int affectedRows = pstmt.executeUpdate();

	        if (affectedRows > 0) {
	            
	            response.sendRedirect("AdminZoznamObjednavok");
	        } else {
	            
	        	out.print("<p>nieco sa pokazilo</p>");

	        }

	        pstmt.close();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        response.sendRedirect("ZobrazVsetkyObjednavkyServlet?error=exception");
	    }
	}


	private void zmenStavObjednavky(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		int idObjednavky = Integer.parseInt(request.getParameter("idObjednavky"));
        Connection con = dajSpojenie(request); 

        
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<html><body>");
		out.println("<h2>Zmena stavu objednávky " + idObjednavky + "</h2>");
		out.println("<form action='AdminZoznamObjednavok' method='post'>"); 
		out.println("<input type='hidden' name='operacia' value='zmenStav'>");
		out.println("<input type='hidden' name='idObjednavky' value='" + idObjednavky + "'>");
		out.println("Nový stav: <select name='novyStav'>");
		out.println("<option value='spracovane'>spracované</option>");
		out.println("<option value='odoslane'>odoslané</option>");
		out.println("<option value='zaplatene'>zaplatené</option>");
		out.println("</select><br>");
		out.println("<input type='submit' value='Zmeniť stav'>");
		out.println("</form>");
		out.println("</body></html>");
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
