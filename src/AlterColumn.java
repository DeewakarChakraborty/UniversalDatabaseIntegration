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

public class AlterColumn extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		Statement   statement = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String	error_message	= null;
		boolean error_occured	= false;

		String message		= null;
		String tableName	= null;
		String columnName	= null;
		String columnType	= null;
		String columnSize	= null;

		String primary	= null;
		String unique	= null;
		String notnull	= null;

		String	primaryKey	= null;
		boolean keyChanged	= false;
		boolean keyExisted	= false;

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

			tableName	= req.getParameter("table_name").trim();
			columnName	= req.getParameter("column_name").trim();
			columnType	= req.getParameter("column_type").trim();
			columnSize	= req.getParameter("column_size").trim();
			primaryKey	= req.getParameter("primary_key").trim();
			primary		= req.getParameter("primary");
			unique		= req.getParameter("unique");
			notnull		= req.getParameter("notnull");

			String query =	"alter table " + tableName + " modify " + 
							columnName + " " + columnType;
			if(!columnSize.equals("0")) query += "(" + columnSize + ")";
			
			if(primaryKey.length() > 1)	keyExisted = true;

			if(unique != null && notnull != null)	primary = "1";

			if(primary != null) {
				if(primaryKey.indexOf("," + columnName + ",") == -1) {
					primaryKey += columnName + ",";
					keyChanged = true;
				}
			}
			else if(unique != null)		query += " unique";
			else if(notnull != null)	query += " not null";
				
			if(primary == null && primaryKey.indexOf("," + columnName + ",") != -1) {
				primaryKey = primaryKey.replaceFirst("," + columnName + "," , ",");
				keyChanged = true;
			}
			
			try {
				Class.forName(driver);
				connection	= DriverManager.getConnection(url,userid,pass);
				statement	= connection.createStatement();
				
				if(keyExisted && keyChanged) {
					statement.executeUpdate("alter table " + tableName + " drop primary key");
				}
				statement.executeUpdate(query);
				
				if(keyChanged && primaryKey.length() > 1) {
					primaryKey = primaryKey.substring(1,primaryKey.length()-1);
					query = "alter table " + tableName + " add primary key(" + primaryKey + ")";
					statement.executeUpdate(query);
				}

				message	= columnName + " column is successfully altered.";
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