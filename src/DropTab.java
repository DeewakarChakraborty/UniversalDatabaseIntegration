import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DropTab extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		Statement   statement = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String  error_message = null;
		boolean error_occured = false;

		String tableName = null;
		String message	 = null;

		HttpSession session = req.getSession(false);

		if(session == null) {
			res.setContentType("text/html");
			PrintWriter writer = res.getWriter();

			writer.println("<HTML>");
			writer.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
							"Sorry Your session expired. Please Login Again.'\"");
			writer.println("</BODY>");
			writer.println("</HTML>");

			writer.close();
		}
		else {
			driver	= session.getAttribute("driver").toString();
			url		= session.getAttribute("url").toString();
			userid	= session.getAttribute("userid").toString();		
			pass	= session.getAttribute("pass").toString();

			tableName = req.getParameter("table_name");

			String query = "drop table " + tableName;
			
			try {
				Class.forName(driver);
				connection	= DriverManager.getConnection(url,userid,pass);
				statement	= connection.createStatement();
				statement.executeUpdate(query);
				message		= tableName + " table deleted successfully.";
			}
			catch(Exception e)	{
				error_occured = true;
				error_message = e.toString();
			}

			try {
				statement.close();
				connection.close();
			}
			catch(Exception e)	{	e.printStackTrace();	}

			if(!error_occured) {
				res.setContentType("text/html");
				PrintWriter writer = res.getWriter();
				
				writer.println("<HTML>");
				writer.println("<HEAD>");
				writer.println("<META NAME='Author' CONTENT='Vamsi'>");
				writer.println("<SCRIPT LANGUAGE='javascript'>");
				writer.println("function loadPages() { ");
				writer.println("	window.parent.left.location.href='ListDB'; ");
				writer.println(	"	window.parent.right.location.href='DescDB?message=" +
								message + "'; ");
				writer.println("} ");
				writer.println("</SCRIPT>");
				writer.println("</HEAD>");
				writer.println("<BODY onLoad='loadPages()' BGCOLOR=#ffffff>");
				writer.println("</BODY></HTML>");
				writer.close();
			}
			else res.sendRedirect("DBOperations?message=" + error_message);
		}
	}
}