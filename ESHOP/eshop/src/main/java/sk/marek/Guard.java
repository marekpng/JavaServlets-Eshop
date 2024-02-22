package sk.marek;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;

import java.io.IOException;
import java.sql.Connection;

/**
 * Servlet implementation class Guard
 */
public class Guard implements HttpSessionBindingListener {
	  Connection connection; // premenna pamatajuca si nase spojenie
	  
	 // nacitanie spojenia v konstruktore
	 public Guard(Connection c) {
	    connection = c; 
	 }

	 @Override
	 public void valueBound(HttpSessionBindingEvent event) { /*nic*/}

	 @Override
	 public void valueUnbound(HttpSessionBindingEvent event) {
	    try { 
	       if (connection != null) connection.close(); // uvolnenie
	    } catch (Exception e) { }
	  }            
	}
