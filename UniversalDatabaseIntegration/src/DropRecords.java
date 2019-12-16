import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.util.Vector;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DropRecords extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		ResultSet   resultSet = null;
		Statement   statement = null;
		ResultSetMetaData metaData = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm = null;
		String schema	  = null;

		String dbProductName    = null;
		String dbProductVersion = null;

		String  error_message = null;
		boolean error_occured = false;

		String[][] rowData= null;
		String[] rowIds   = null;
		String[] colNames = null;
		int   [] colTypes = null;

		String tableName  = null;
		String sortColumn = null;

		Vector errorIndex = new Vector();

		int rowid		= 0;
		int	sortOrder	= 0;
		int columnCount	= 0;
		int deletedCount= 0;

		HttpSession session = req.getSession(false);
		res.setContentType("text/html");
		PrintWriter writer = res.getWriter();

		if(session == null) {
			writer.println("<HTML>");
			writer.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
							"Sorry Your session expired. Please Login Again.'\"");
			writer.println("</BODY>");
			writer.println("</HTML>");
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
			
			tableName  = req.getParameter("table_name");
			rowIds     = req.getParameterValues("row_ids");
			sortColumn = req.getParameter("sort_column");
			sortOrder  = Integer.parseInt(req.getParameter("sort_order"));

			String query = "select * from " + tableName;
			if(!sortColumn.equalsIgnoreCase("none")) {
				query += " order by " + sortColumn;
				if(sortOrder == 0)	query += " asc";
				else				query += " desc";
			}

			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url,userid,pass);
				statement  = connection.createStatement();
				resultSet  = statement.executeQuery(query);
				metaData   = resultSet.getMetaData();
				columnCount= metaData.getColumnCount();

				colTypes = new int[columnCount];
				colNames = new String[columnCount];
				rowData  = new String[rowIds.length][columnCount];

				for(int i=0;i<columnCount;i++) {
					colNames[i] = metaData.getColumnName(i+1);
					colTypes[i] = metaData.getColumnType(i+1);
				}

				for(int i=0;i<rowIds.length;i++) {
					try {
						int count = rowid;
						rowid = Integer.parseInt(rowIds[i]);
						while(true) {
							if(count == rowid) break;
							resultSet.next();
							count ++;
						}	
					
						for(int j=0;j<columnCount;j++) {
							switch(colTypes[j]) {
								case Types.BLOB			:
								case Types.CLOB			:
								case Types.BINARY		:
								case Types.VARBINARY	:
								case Types.LONGVARCHAR	:
								case Types.LONGVARBINARY:	rowData[i][j] = "$LOB_DATA$";	break;
								default	:	try	{	rowData[i][j] = resultSet.getString(j+1);	}
											catch(Exception e) { rowData[i][j] = "$DATA_READ_ERROR$"; }
							}
						}
					} catch(Exception e)	{ }					
				}
				if(resultSet != null) resultSet.close();
			}
			catch(Exception e) {
				error_occured = true;
				error_message = e.toString();
			}

			if(!error_occured) {
				for(int i=0;i<rowData.length;i++) {
					String whereClause = "";
					for(int j=0;j<columnCount;j++) {
						String data  = (rowData[i][j] == null) ? "null" : rowData[i][j];
						String cname = colNames[j];
						if(!data.equalsIgnoreCase("$DATA_READ_ERROR$"))
						{
							switch(colTypes[j]) {
								case Types.BIT		:	
								case Types.SMALLINT	:
								case Types.TINYINT	:	
								case Types.INTEGER	:	
								case Types.BIGINT	:	
								case Types.NUMERIC	:
								case Types.DECIMAL	:	
								case Types.REAL		:	
								case Types.DOUBLE	:
								case Types.FLOAT:	if(data.equalsIgnoreCase("null")) 
														 whereClause += cname + " IS null and ";
													else whereClause += cname + "=" + data + " and ";
													break;
								case Types.TIMESTAMP:	
								case Types.VARCHAR	:
								case Types.DATE		:
								case Types.TIME		:
								case Types.CHAR	:	if(data.equalsIgnoreCase("null")) 
														 whereClause += cname + " IS null and ";
													else whereClause += cname + "='" + data + "' and ";
							}
						}
						else {	whereClause = null;	break;	}
					}
					
					if(whereClause == null)	errorIndex.add(new Integer(i));
					else {
						whereClause = whereClause.substring(0,whereClause.length()-4);
						query = "delete from " + tableName + " where " + whereClause;
						try {	
							statement.executeUpdate(query);	
							deletedCount ++;
						}
						catch(Exception e)	{	errorIndex.add(new Integer(i));	}
					}
				}
			}

			try {
				statement.close();
				connection.close();
			}
			catch(Exception e)	{	e.printStackTrace();	}

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
							"<A HREF='DescTab?table_name=" + tableName + "'>" + 
							"<img onMouseOver='putOn(this,1)' onMouseOut='putOff(this,1)' " +
							"name=pic1 src='pics/structure1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='BrowseForm?table_name=" + tableName + "'>" + 
							"<img onMouseOver='putOn(this,3)' onMouseOut='putOff(this,3)' " +
							"name=pic3 src='pics/browse1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='TabQuery?table_name=" + tableName + "'>" + 
							"<img onMouseOver='putOn(this,4)' onMouseOut='putOff(this,4)' " +
							"name=pic4 src='pics/sql1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='InsertForm?table_name=" + tableName + "'>" + 
							"<img onMouseOver='putOn(this,6)' onMouseOut='putOff(this,6)' " +
							"name=pic6 src='pics/insert1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>"+
							"<A HREF='ExportTab?table_name=" + tableName + "'>" +
							"<img onMouseOver='putOn(this,7)' onMouseOut='putOff(this,7)' " +
							"name=pic7 src='pics/export1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='TabOperations?table_name=" + tableName + "'>" +
							"<img onMouseOver='putOn(this,8)' onMouseOut='putOff(this,8)' " +
							"name=pic8 src='pics/operations1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='SearchTab?table_name=" + tableName + "'>" +
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
			writer.println("<TR><TH width=27% id=common_hed>Displayed Table" + "</TH>");
			writer.println("<TD width=73% id=common_data>" + tableName + "</TD></TR>");
			writer.println("</TABLE><BR>");
				
			if(error_occured)	writer.println("<H3>" + error_message + "</H3></BODY></HTML>");
			else {
				if(deletedCount > 0) {
					writer.println("<HR WIDTH=100%>");
					writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
					writer.println("<TR>");
					writer.println(	"<TH id=insert_norm_msg>" + deletedCount +
									" Record(s) Deleted Successfully</TH>");
					writer.println("</TR>");
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%>");
				}

				if(errorIndex.size() > 0) {
					writer.println("<HR WIDTH=100%>");
					writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
					writer.println("<TR>");
					writer.println(	"<TH id=insert_err_msg>" +
									"THE FOLLOWING ROWS CAUSED ERRORS DURING DELETION</TH>");
					writer.println("</TR>");
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%>");

					writer.println("<DIV id=common_div ALIGN=center>");
					writer.println("<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0>");
					writer.println("<TR>");

					for(int i=0;i<columnCount;i++) writer.println(	"<TH id=browse_th>" + colNames[i] + 
																	"</TH>");
					writer.println("</TR>");
					
					for(int i=0;i<errorIndex.size();i++) {
						int r = Integer.parseInt(errorIndex.elementAt(i).toString());
						writer.println("<TR>");
						for(int c=0;c<columnCount;c++)	writer.println(	"<TD browse_td>" + 
																		rowData[r][c] + "</TD>");
						writer.println("</TR>");
					}

					writer.println("</TABLE></DIV>");
				}

				writer.println("</BODY></HTML>");
			}
		}
		writer.close();
	}
}