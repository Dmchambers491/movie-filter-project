package com.cognixia.jump.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MovieFilterServlet extends HttpServlet {

	private static final long serialVersionUID = -3388035267701508368L;
	
	private Connection conn;
	private PreparedStatement pstmt;
	
	@Override
	public void init() {
		conn = ConnectionManager.getConnection();
		
		try {
			pstmt = conn.prepareStatement("select title, description from film where rental_rate=? and rating=? limit ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		
		doGet(request, response);
		
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		String rating = req.getParameter("ratings");
		float rental_rate = Float.parseFloat(req.getParameter("rental"));
		int size = Integer.parseInt(req.getParameter("movies"));
		
		Map<String, String> films = new HashMap<String, String>();
		
		String title = null;
		String description = null;
		
		try {
			
			pstmt.setFloat(1, rental_rate);
			pstmt.setString(2, rating);
			pstmt.setInt(3, size);
			
			ResultSet rs = pstmt.executeQuery();
			
			res.setContentType("text/html");
			
			PrintWriter pw = res.getWriter();
			
			pw.println("<html>");
			
			pw.println("<header><title>Movies</title></header>");
			
			pw.println("<body>");
			
			pw.println("<h1 style='text-align:center;text-decoration:underline'>Movie List</h1>");
			
			while(rs.next()) {
				title = rs.getString("title");
				description = rs.getString("description");
				
				films.put(title, description);
				
				
				pw.println("<h3 style='color:blue;text-decoration:underline'>" + title + "</h3>");
				pw.println("<h3 style='font-style:italic'>" + description + "</h3>");
				pw.println("</br>");
				
			}
			
			pw.println("</body>");
			
			pw.println("</html>");
			
			if(films.size() > 0) {
				pw.println("<h2 style='color:red'>Number of movies: " + films.size() + "</h2>");
			}else {
				pw.println("<h2 style='color:red'>No films found</h2>");
			}
			
			rs.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void destroy() {
		
		try {
			pstmt.close();
			conn.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}

}
