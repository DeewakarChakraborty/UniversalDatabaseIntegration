import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.util.Vector;
import java.util.StringTokenizer;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AlterTab extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		ResultSet  resultSet  = null;
		Statement  statement  = null;
		DatabaseMetaData dbMetaData = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm = null;
		String schema	  = null;
		int	   schemaValue= 0;

		String  error_message = null;
		boolean error_occured = false;

		boolean pkExists	= false;
		String	pkColumn	= null;
		String	primaryKey	= ",";
		Vector	primaryKeys	= new Vector();

		String tableName		= null;
		String dbProductName    = null;
		String dbProductVersion = null;

		String[] columnNames = null;
		String[] columnTypes = null;
		String[] columnSizes = null;
		String[] columnPk	 = null;
		String[] columnUn	 = null;
		String[] columnNn	 = null;

		String[] prevColumnNames  = null;
		String[] prevColumnIndex  = null;
		String[] prevColumnAction = null;

		Vector alterQueries	= new Vector();
		Vector errorIndex	= new Vector();
		Vector errorMessage = new Vector();

		Vector typeNames = null;

		PrintWriter writer = null;

		HttpSession session = req.getSession(false);

		if(session == null) {
			res.setContentType("text/html");
			writer  = res.getWriter();

			writer.println("<HTML>");
			writer.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
							"Sorry Your session expired. Please Login Again.'\"");
			writer.println("</BODY>");
			writer.println("</HTML>");

			writer.close();
		}
		else {
			driver			= session.getAttribute("driver").toString();
			url				= session.getAttribute("url").toString();
			userid			= session.getAttribute("userid").toString();		
			pass			= session.getAttribute("pass").toString();
			dbProductName	= session.getAttribute("dbProductName").toString();
			dbProductVersion= session.getAttribute("dbProductVersion").toString();
			schemaTerm		= session.getAttribute("schemaTerm").toString();
			schema			= session.getAttribute("schema").toString();
			schemaValue		= Integer.parseInt(session.getAttribute("schemaValue").toString());
			
			pkColumn	= req.getParameter("pk_column");
			tableName	= req.getParameter("table_name");
			columnNames	= req.getParameterValues("column_names");
			columnTypes	= req.getParameterValues("column_types");
			columnSizes	= req.getParameterValues("column_sizes");
			columnPk	= req.getParameterValues("column_pk");
			columnUn	= req.getParameterValues("column_un");
			columnNn	= req.getParameterValues("column_nn");

			prevColumnNames = req.getParameterValues("prev_column_names");
			prevColumnIndex = req.getParameterValues("prev_column_index");
			prevColumnAction= req.getParameterValues("prev_column_action");

			if(!pkColumn.equals(","))	pkExists = true;
			
			for(int i=0;i<prevColumnNames.length;i++) {
				String prevName	= prevColumnNames[i].trim();
				int prevAction	= Integer.parseInt(prevColumnAction[i]);
				int prevIndex	= Integer.parseInt(prevColumnIndex[i]);
				
				if(prevAction == -1) alterQueries.add(	"alter table " + tableName + 
														" drop column " + prevName	);
				if(prevIndex == -1 && prevAction == -1) {
					pkColumn = pkColumn.replaceFirst("," + prevName + "," , ",");
				}
			}
			
			for(int i=0;i<columnNames.length;i++) {

				String colname = columnNames[i].trim();
				String coltype = columnTypes[i].trim();
				String colsize = columnSizes[i].trim();

				int primary	= Integer.parseInt(columnPk[i]);
				int unique	= Integer.parseInt(columnUn[i]);
				int notnull = Integer.parseInt(columnNn[i]);

				boolean prevFound  = false;
				int		prevAction = 0;
				
				for(int x=0;x<prevColumnNames.length;x++) {
					String prevName	= prevColumnNames[x].trim();
					int prevIndex	= Integer.parseInt(prevColumnIndex[x]);
					prevAction	= Integer.parseInt(prevColumnAction[x]);

					if(prevName.equals(colname) && prevIndex == i) {
						prevFound = true;
						break;
					}
				}

				if(prevFound) {
					if(prevAction == 1) {
						String modify = "alter table " + tableName + 
										" modify " + colname + " " + coltype;

						if(pkColumn.indexOf("," + colname + ",") != -1 && primary != 1) {
							pkColumn = pkColumn.replaceFirst("," + colname + "," , ",");
						}

						if(colsize.length()>0 && !colsize.equals("0")) modify += "(" + colsize + ")";
		
						if(primary == 1)					 primaryKey += colname + ",";
						else if(unique == 1 && notnull == 1) primaryKey += colname + ",";
						else if(unique  == 1)				 modify += " unique";
						else if(notnull == 1)				 modify += " not null";
	
						alterQueries.add(modify);
					}
				}
				else {
					String add = "alter table " + tableName + " add " + colname + " " + coltype;
				
					if(colsize.length() > 0 && !colsize.equals("0")) add += "(" + colsize + ")";
					
					if(primary == 1)					 primaryKey += colname + ",";
					else if(unique == 1 && notnull == 1) primaryKey += colname + ",";
					else if(unique  == 1)				 add += " unique";
					else if(notnull == 1)				 add += " not null";
					
					alterQueries.add(add);
				}
			}

			if(alterQueries.size() > 0) {
				StringTokenizer st = new StringTokenizer(pkColumn,",");
				while(st.hasMoreTokens())	primaryKeys.add(st.nextToken());
		
				st = new StringTokenizer(primaryKey,",");
				while(st.hasMoreTokens()) {
					String  value	= st.nextToken();
					boolean visited = false;
					
					for(int i=0;i<primaryKeys.size();i++) {
						if(value.equals(primaryKeys.elementAt(i).toString())) {
							visited = true;
							break;
						}
					}
	
					if(!visited)	primaryKeys.add(value);
				}
			}

			if(primaryKeys.size() > 0) {
				primaryKey = new String();
				for(int i=0;i<primaryKeys.size();i++) {
					primaryKey += primaryKeys.elementAt(i).toString() + ",";
				}

				primaryKey = primaryKey.substring(0,primaryKey.length()-1);
				
				if(pkExists) alterQueries.add("alter table " + tableName + " drop primary key");
				alterQueries.add("alter table " + tableName + " add primary key(" + primaryKey + ")");
			}
					
			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url,userid,pass);
				statement  = connection.createStatement();
			}
			catch(Exception e) {
				error_occured = true;
				error_message = e.toString();
			}

			if(!error_occured) {
				for(int i=0;i<alterQueries.size();i++) {
					try	{	statement.executeUpdate(alterQueries.elementAt(i).toString());	}
					catch(Exception e)	{	
						errorIndex.add(new Integer(i));
						errorMessage.add(e.toString());
					}
				}

				if(errorIndex.size() == 0) {
					String message = "'" + tableName + "' Table Altered successfully.";
					res.sendRedirect("DBOperations?message=" + message);
				}
				else {
					try {
						dbMetaData = connection.getMetaData();
						resultSet  = dbMetaData.getTypeInfo();
						typeNames  = new Vector();
						while(resultSet.next())	typeNames.add(resultSet.getString(1));
					} 
					catch(Exception e) { 
						error_occured = true;
						error_message = e.toString();
					}
				}

				try {
					if(resultSet  != null) resultSet.close();
					if(statement  != null) statement.close();
					if(connection != null) connection.close();
				}
				catch(Exception e)	{	e.printStackTrace();	}
			}

			if(error_occured || errorIndex.size() > 0) {
				res.setContentType("text/html");
				writer = res.getWriter();

				writer.println("<HTML>");
				writer.println("<HEAD>");
				writer.println("<META NAME='Author' CONTENT='Vamsi'>");
				writer.println("<LINK REL='stylesheet' TYPE='text/css' HREF='styles.css'>");
				writer.println( "<SCRIPT LANGUAGE='javascript' TYPE='text/javascript' " +
								" SRC='script.js'></SCRIPT>");
				writer.println("</HEAD>");
				writer.println(	"<BODY onLoad='loadImages()' BGCOLOR=#ffffff " +
								" link=black alink=black vlink=black>");
				writer.println("<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0>");
				writer.println(	"<TR><TD>&nbsp&nbsp&nbsp&nbsp" +
								"<A HREF='DescDB'>" +
								"<img onMouseOver='putOn(this,1)' onMouseOut='putOff(this,1)' " +
								" name='pic1' src='pics/structure1.jpg' border=0 " +
								" width=80 height=26 align=absbottom></A>" +
								"<A HREF='DBProperties'>" +
								"<img onMouseOver='putOn(this,2)' onMouseOut='putOff(this,2)' " +
								" name='pic2' src='pics/properties1.jpg' border=0 " +
								" width=80 height=26 align=absbottom></A>" +
								"<A HREF='DBQuery'>" +
								"<img onMouseOver='putOn(this,4)' onMouseOut='putOff(this,4)' " +
								"name=pic4 src='pics/sql1.jpg' border=0 " +
								"width=80 height=26 align=absbottom></A>" +
								"<A HREF=''>" +
								"<img onMouseOver='putOn(this,5)' onMouseOut='putOff(this,5)' " +
								"name=pic5 src='pics/1.jpg' border=0 " +
								"width=80 height=26 align=absbottom></A>"+
								"<A HREF='ExportDB'>" +
								"<img onMouseOver='putOn(this,7)' onMouseOut='putOff(this,7)' " +
								"name=pic7 src='pics/export1.jpg' border=0 " +
								"width=80 height=26 align=absbottom></A>" +
								"<A HREF='DBOperations'>" +
								"<img onMouseOver='putOn(this,8)' onMouseOut='putOff(this,8)' " +
								"name=pic8 src='pics/operations1.jpg' border=0 " +
								"width=80 height=26 align=absbottom></A>" +
								"<A HREF='SearchDB'>" + 
								"<img onMouseOver='putOn(this,9)' onMouseOut='putOff(this,9)' "+
								"name=pic9 src='pics/search1.jpg' border=0 " +
								"width=80 height=26 align=absbottom></A></TD></TR>" +
								"<TR><TD vAlign=top><IMG SRC='pics/bar.jpg' " +
								" ALIGN=absTop BORDER=0 WIDTH=590 HEIGHT=13>" +
								"</TD></TR></TABLE><BR>");
				writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%>"); 
				writer.println("<TR><TH width=27% id=common_hed>Database Product Name</TH>"); 
				writer.println("<TD width=73% id=common_data>" + dbProductName + "</TD></TR>");
				writer.println("<TR><TH width=27% id=common_hed>Database Product Version</TH>"); 
				writer.println("<TD width=73% id=common_data>" + dbProductVersion + "</TD></TR>");
				writer.println("<TR><TH width=27% id=common_hed>Displayed " + schemaTerm + "</TH>");
				writer.println("<TD width=73% id=common_data>" + schema + "</TD></TR>");
				writer.println("</TABLE><BR>");
			}
			
			if(error_occured)	{
				writer.println("<H3>" + error_message + "</H3></BODY></HTML>");
				writer.close();
			}
			else if(errorIndex.size() > 0) {
				writer.println("<HR WIDTH=100%>");
				writer.println("<TABLE ALIGN=center WIDTH=100% BORDER=0 CELLSPACING=1 CELLPADDING=4>");
				writer.println(	"<TR><TD id=insert_err_msg>The Following Queries Caused " +
								"Corresponding Errors, During Execution</TD></TR>"); 
				for(int i=0;i<errorIndex.size();i++) {
					int index = Integer.parseInt(errorIndex.elementAt(i).toString());
					String query = alterQueries.elementAt(index).toString();
					String error = errorMessage.elementAt(i).toString();
					writer.println("<TR><TD id=common_hed>" + query + "</TD></TR>");
					writer.println("<TR><TD id=common_data>" + error + "</TD></TR>");
				}
				writer.println("</TABLE>");
				writer.println("<HR WIDTH=100%>");

				writer.println("</BODY></HTML>");
				writer.close();
			}
		}
	}
}