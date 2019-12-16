import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DropColumn extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		Statement   statement = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String	error_message	= null;
		boolean error_occured	= false;

		String message	 = null;
		String tableName = null;
		String columnName= null;

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
			columnName= req.getParameter("column_name");

			String query = "alter table " + tableName + " drop column " + columnName;
			
			try {
				Class.forName(driver);
				connection	= DriverManager.getConnection(url,userid,pass);
				statement	= connection.createStatement();
				statement.executeUpdate(query);
				message		= columnName + " column deleted successfully.";
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

			if(error_occured) res.sendRedirect(	"TabOperations?table_name=" + 
												tableName + "&message=" + error_message);
			else res.sendRedirect("DescTab?table_name=" + tableName + "&message=" + message);
		}
	}
}