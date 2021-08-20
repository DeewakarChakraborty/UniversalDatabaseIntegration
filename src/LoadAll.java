import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.io.PrintWriter;
import java.io.IOException;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoadAll extends HttpServlet
{
	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		DatabaseMetaData dbMetaData = null;
		ResultSet resultSet = null;
        PrintWriter wri=res.getWriter();     
		boolean error_occured = false;
		String  error_message = null;

		String genericTerms = ",USER NAME,USERNAME,DATABASE,DATA BASE,SCHEMA,";
		String schemaTerm   = null;
		int	   schemaValue	= 0;

		String schema = null;
		String dbProductName = null;
		String dbProductVersion = null;

		String driver = req.getParameter("driver");
		String url    = req.getParameter("url");
		String userid = req.getParameter("userid");
		String pass   = req.getParameter("pass");

		res.setContentType("text/html");
		PrintWriter writer = res.getWriter();

		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(url,userid,pass);	
			dbMetaData = connection.getMetaData();				
		}
		catch(Exception e) {
			error_occured = true;
			error_message = e.toString();
			wri.println("UserName and password do not match..!!!!!!!!!!!!");
		}

		if(!error_occured) {
			try {
				dbProductName	 = dbMetaData.getDatabaseProductName();
				dbProductVersion = dbMetaData.getDatabaseProductVersion();

				schemaTerm  = dbMetaData.getSchemaTerm();
				if(genericTerms.indexOf(","+schemaTerm.toUpperCase()+",") != -1) schemaValue = 1;
				else {
					schemaTerm  = dbMetaData.getCatalogTerm();
					schemaValue = 0;
				}

				String pname = dbProductName.toUpperCase();
				if(pname.indexOf("ORACLE") != -1)		schema = userid.trim().toUpperCase();
				else if(pname.indexOf("MYSQL") != -1)	
						schema = url.substring(url.lastIndexOf("/")+1, url.length());
				else if(pname.indexOf("ACCESS") !=-1)	{
					resultSet = dbMetaData.getCatalogs();
					if(resultSet.next()) {
						schema = resultSet.getString(1);
						schema = schema.substring(schema.lastIndexOf("\\")+1,schema.length());			
					}
				}
				
				HttpSession session = req.getSession(true);
				session.setMaxInactiveInterval(900);
				session.setAttribute("driver",driver);
				session.setAttribute("url",url);
				session.setAttribute("userid",userid);
				session.setAttribute("pass",pass);
				session.setAttribute("dbProductName",dbProductName);
				session.setAttribute("dbProductVersion",dbProductVersion);
				session.setAttribute("schemaTerm",schemaTerm);
				session.setAttribute("schemaValue",new Integer(schemaValue));
				session.setAttribute("schema",schema);
			}
			catch(Exception e) {
				error_occured = true;
				error_message = e.toString();
			}
			try {
				connection.close();
				if(resultSet!=null)
					resultSet.close();
			}
			catch(Exception e)	{	e.printStackTrace();	}
		}
		
		if(error_occured) {
			writer.println("<HTML>");
			writer.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
							error_message + "'\"");
			writer.println("</BODY>");
			writer.println("</HTML>");
		}
		else writer.println("<HTML><HEAD>" +
							"<FRAMESET COLS='20%,80%' BORDER=0>" +
							"<FRAME NAME='left'  SRC='ListDB'>" +
							"<FRAME NAME='right' SRC='DescDB'>" +
							"</FRAMESET>" +
							"</HEAD></HTML>");
		writer.close();
	}	
}