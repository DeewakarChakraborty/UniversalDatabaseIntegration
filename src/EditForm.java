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
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EditForm extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		ResultSet   resultSet = null;
		Statement   statement = null;
		ResultSetMetaData metaData  = null;
	
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

		String[] colNames = null;
		String[] typeName = null;
		
		int[] colTypes = null;
		int[] colSizes = null;
		int[] decimals = null;
		int[] nullable = null;

		String[] rowIds = null;
		String rowids	= "";
		String tableName  = null;
		String sortColumn = null;

		int	sortOrder		 = 0;
		int columnCount		 = 0;
		int largeColumnCount = 0;

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
			sortOrder = Integer.parseInt(req.getParameter("sort_order"));
			largeColumnCount = Integer.parseInt(req.getParameter("large_column_count"));

			for(int x=0;x<rowIds.length;x++) rowids += rowIds[x].trim() + ",";

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

				colNames = new String[columnCount];
				typeName = new String[columnCount];
				colTypes = new int[columnCount];
				colSizes = new int[columnCount];
				decimals = new int[columnCount];
				nullable = new int[columnCount];

				for(int i=0;i<columnCount;i++) {
					colNames[i] = metaData.getColumnName(i+1);
					typeName[i] = metaData.getColumnTypeName(i+1);
					colTypes[i] = metaData.getColumnType(i+1);
					colSizes[i] = metaData.getColumnDisplaySize(i+1);
					decimals[i] = metaData.getPrecision(i+1);
					nullable[i] = metaData.isNullable(i+1);
				}
			}
			catch(Exception e) {
				error_occured = true;
				error_message = e.toString();
			}

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
				if(largeColumnCount == 0) {
					writer.println("<FORM NAME=edit_form METHOD=post ACTION='EditNorm'>");
					writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
					writer.println("<INPUT TYPE=hidden NAME=row_count  VALUE=" + rowIds.length + ">");
					
					writer.println("<DIV id=common_div ALIGN=center>");
					writer.println("<TABLE ALIGN=left CELLSPACING=1 CELLPADDING=1 BORDER=0>");
					writer.println("<TR>");
					for(int i=0;i<columnCount;i++)	{
						writer.println("<TH id=insert_th>" + colNames[i] + "</TH>");
					}
					writer.println("</TR>");
				
					writer.println("<TR>");
					for(int i=0;i<columnCount;i++)	{
						writer.print("<TD id=insert_td>" + typeName[i] +
									 "[" + colSizes[i]);
						
						if(decimals[i] > 0)	writer.println("," + decimals[i] + "]</TD>");
						else				writer.println("]</TD>");
					}
					writer.println("</TR>");
				
					int rowid = 0;
					for(int i=0;i<rowIds.length;i++)	{
						try {	
							int count = rowid;
							rowid = Integer.parseInt(rowIds[i]);
							while(true) {
								if(count == rowid) break;
								resultSet.next();
								count ++;
							}	
						}	catch(Exception e)	{ }

						writer.println("<TR>");						
						for(int j=0;j<columnCount;j++)	{
							String data = null;
							try {	data = resultSet.getString(j+1);	}
							catch(Exception e)	{	data = "$DATA_READ_ERROR$"; }

							writer.println(	"<TD id=insert_td>" +
											"<INPUT TYPE=hidden NAME='row_" + i + "_prev_data' " +
											" VALUE=\"" + data + "\">" +
											"<INPUT TYPE=text id=insert_norm_inp NAME='row_" + i + 
											"_curr_data' MAXLENGTH='" + colSizes[j] + "' " + 
											"VALUE=\"" + data + "\"></TD>");
						}	
					}
					writer.println("</TR></TABLE></DIV><BR>");
					writer.println("</FORM>");

					writer.println("<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0>");
					writer.println("<TR>");
					writer.println(	"<TD WIDTH=20% ALIGN=left><IMG SRC='pics/go1.jpg' " +
									"NAME=go1 WIDTH=30 HEIGHT=30 BORDER=0 STYLE='cursor:hand' " +
									"onMouseOut='putOff(this,13)' onMouseUp='putOff(this,13)' " +
									"onMouseDown='putOn(this,13)' onClick='edit_form.submit()'>" +
									"</TD>");
					writer.println("<TD WIDTH=60%></TD>");
					writer.println(	"<TD WIDTH=20% ALIGN=right><IMG SRC='pics/go1.jpg' " +
									"NAME=go2 WIDTH=30 HEIGHT=30 BORDER=0 STYLE='cursor:hand' " +
									"onMouseOut='putOff(this,13)' onMouseUp='putOff(this,13)' " +
									"onMouseDown='putOn(this,13)' onClick='edit_form.submit()'>" +
									"</TD>");
					writer.println("</TR>");
					writer.println("</TABLE>");
				}
				else {
					int rowid = 0;
					for(int i=0;i<rowIds.length;i++) {
						try {	
							int count = rowid;
							rowid = Integer.parseInt(rowIds[i]);
							while(true) {
								if(count == rowid) break;
								resultSet.next();
								count ++;
							}	
						}	catch(Exception e)	{ }

						writer.println(	"<FORM METHOD=post ENCTYPE='multipart/form-data' " +
										" NAME='edit_form" + i + "' ACTION='EditLobs'>");
						writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
						writer.println("<INPUT TYPE=hidden NAME=sort_column VALUE=" + sortColumn + ">");
						writer.println("<INPUT TYPE=hidden NAME=sort_order VALUE=" + sortOrder + ">");
						writer.println("<INPUT TYPE=hidden NAME=row_ids VALUE=\"" + rowids + "\">");
						writer.println("<INPUT TYPE=hidden NAME=curr_id VALUE=" + rowid + ">");

						writer.println(	"<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=1 BORDER=0 " +
										"style='border-style:double;border-width:1px;border-color:black'>");
						writer.println("<TR>");
						writer.println("<TH id=insert_th WIDTH=25%>COLUMN NAME</TH>");
						writer.println("<TH id=insert_th WIDTH=25%>DATA TYPE</TH>");
						writer.println("<TH id=insert_th WIDTH=50% COLSPAN=2>VALUE</TH>");
						writer.println("</TR>");

						for(int j=0;j<columnCount;j++)	{
							writer.println("<TR>");
							writer.println( "<TH id=insert_td STYLE='font-weight:bold'>" + 
											colNames[j] + "</TH>");
							writer.print("<TD id=insert_td >" + typeName[j] + "[" + colSizes[j]);
						
							if(decimals[j] > 0)	writer.println("," + decimals[j] + "]</TD>");
							else				writer.println("]</TD>");
				
							String inputType = "text";
							String data		 = "";
							switch(colTypes[j]) {
								case Types.LONGVARBINARY:
								case Types.LONGVARCHAR	:
								case Types.VARBINARY	:
								case Types.BINARY		:
								case Types.BLOB			:
								case Types.CLOB	:	inputType = "file";	break;
								default			:	inputType = "text";	
													try { data = resultSet.getString(j+1); }
													catch (Exception e) { data = "$DATA_READ_ERROR$"; }
							}
							
							if(columnCount == j+1) {
								writer.println("<TD WIDTH=45% id=insert_td>"); 
								writer.println(	"<INPUT TYPE=" + inputType + " id=insert_norm_inp " +
												" STYLE='WIDTH=100%' VALUE='" + data + 
												"' NAME=" + j + " MAXLENGTH=" + colSizes[j] + "></TD>");
								writer.println("<TD WIDTH=5%><IMG SRC='pics/go1.jpg' " +
										"NAME=go1 WIDTH=30 HEIGHT=30 BORDER=0 STYLE='cursor:hand' " +
										"onMouseOut='putOff(this,13)' onMouseUp='putOff(this,13)' " +
										"onMouseDown='putOn(this,13)' " +
										"onClick='edit_form" + i +".submit()'></TD>");
							}
							else {
								writer.println("<TD COLSPAN=2 id=insert_td><INPUT TYPE=" + inputType + " id=insert_norm_inp " +
												" STYLE='WIDTH=100%' VALUE='" + data + "' NAME=" + j + 
												" MAXLENGTH=" + colSizes[j] + "></TD>");
							}
							writer.println("</TR>");
						}
						writer.println("</TABLE><BR>");
						writer.println("</FORM>");
					}
				}
			}

			try {
				if(resultSet != null) resultSet.close();
				connection.close();
			}
			catch(Exception e)	{	e.printStackTrace();	}
		}
		writer.close();
	}
}