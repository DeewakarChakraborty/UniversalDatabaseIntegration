import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.util.Vector;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoadLob extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		ResultSet   resultSet = null;
		Statement   statement = null;

		OutputStream out = null;
		InputStream  in	 = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String  error_message = null;
		boolean error_occured = false;

		String tableName	= null;
		String columnName	= null;
		String contentType	= null;
		String sortColumn	= null;

		int rowIndex	= 0;
		int sortOrder	= 0;

		HttpSession session = req.getSession(false);

		if(session != null) {
			driver			= session.getAttribute("driver").toString();
			url				= session.getAttribute("url").toString();
			userid			= session.getAttribute("userid").toString();		
			pass			= session.getAttribute("pass").toString();
			tableName		= req.getParameter("tname");
			columnName		= req.getParameter("cname");
			contentType		= req.getParameter("ctype");
			sortColumn		= req.getParameter("scolumn");
			rowIndex		= Integer.parseInt(req.getParameter("index"));
			sortOrder		= Integer.parseInt(req.getParameter("sorder"));

			res.setContentType(contentType);
			if(contentType.equalsIgnoreCase("application")) {
				res.setHeader(	"Content-Disposition", "attachment;filename=" + 
								tableName + "_" + columnName + "_" + rowIndex);
			}
			out = res.getOutputStream();
			
			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url,userid,pass);
				statement  = connection.createStatement();				
			}
			catch(Exception e)	{
				error_occured = true;
				error_message = e.toString();				
			}

			if(!error_occured)	{
				String query = "select " + columnName + " from " + tableName;
				if(!sortColumn.equalsIgnoreCase("NONE")) {
					query += " order by " + sortColumn;
					if(sortOrder == 0)	query += " asc";
					else				query += " desc";
				}

				try {
					resultSet = statement.executeQuery(query);
					int index = 1;

					while(resultSet.next())	{
						if(index == rowIndex)	{
							in = resultSet.getBinaryStream(1);
							byte[] buffer = new byte[1];
							while(in.read(buffer) != -1) out.write(buffer);
							in.close();
							break;
						}
						else index++;
					}
				}
				catch(Exception e)	{	e.printStackTrace();	}
			}
			out.close();
		}
	}
}