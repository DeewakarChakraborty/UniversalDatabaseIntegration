import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.util.Vector;

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

public class BrowseForm extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		DatabaseMetaData dbMetaData = null;
		ResultSet resultSet = null;
		Statement statement = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm	= null;
		String schema		= null;
		int	   schemaValue	= 0;

		String dbProductName    = null;
		String dbProductVersion = null;

		Vector colNames = new Vector();
		Vector colTypes = new Vector();
		Vector colSizes	= new Vector();
		int	   rowCount = 0;

		String tableName = null;

		Vector largeColNames = new Vector();
		final String largeObjects = ",TEXT,LONGTEXT,MEDIUMTEXT,BLOB,MEDIUMBLOB," +
									"LONGBLOB,RAW,LONG RAW,LONG,CLOB,LONGBINARY,LONGCHAR,";

		String  error_message = null;
		boolean error_occured = false;

		HttpSession session = req.getSession(false);
		res.setContentType("text/html");
		PrintWriter writer  = res.getWriter();

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
			schemaValue		= Integer.parseInt(session.getAttribute("schemaValue").toString());
			schema			= session.getAttribute("schema").toString();

			tableName = req.getParameter("table_name");			

			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url,userid,pass);
				dbMetaData = connection.getMetaData();
				statement  = connection.createStatement();
			}
			catch(Exception e) {
				error_occured = true;
				error_message = e.toString();
			}
			
			if(!error_occured) {
				try {
					if(schemaValue == 0)resultSet = dbMetaData.getColumns(schema,null,tableName,null);
					else				resultSet = dbMetaData.getColumns(null,schema,tableName,null);

					while(resultSet.next()) {
						colNames.add(resultSet.getString(4));
						colTypes.add(resultSet.getString(6));

						String size = resultSet.getString(7);
						int decimalDigits = resultSet.getInt(9);
						if(decimalDigits > 0)	size += "," + decimalDigits;
						colSizes.add(size);
					}
					if(resultSet != null) resultSet.close();

					resultSet = statement.executeQuery("select count(*) from " + tableName);
					if(resultSet.next())	rowCount = resultSet.getInt(1);

					if(resultSet != null)	resultSet.close();
				}
				catch(Exception e) {
					error_occured = true;
					error_message = e.toString();
				}
			}

			try {
				if(resultSet != null)	resultSet.close();
				connection.close();
			}
			catch(Exception e)	{	e.printStackTrace();	}

			if(error_occured) {
				writer.println("<HTML><HEAD><H3>" + error_message + "</H3></HEAD></HTML>");
			}
			else {
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
							"<img name=pic3 src='pics/browse2.jpg' border=0 " +
							"width=80 height=26 align=absbottom>" +
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
				writer.println("<TR><TH width=27% id=common_hed>Total number of Records" + "</TH>");
				writer.println("<TD width=73% id=common_data>" + rowCount + "</TD></TR>");
				writer.println("</TABLE><BR>");

				if(rowCount > 0)	{
					writer.println("<FORM NAME=browseform METHOD=post ACTION='BrowseTab'>");
					writer.println("<INPUT TYPE=hidden NAME=table_name VALUE='" + tableName + "'>");
					writer.println("<INPUT TYPE=hidden NAME=row_count  VALUE='" + rowCount + "'>");
					writer.println(	"<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0 " +
									" style='border-style:double; border-width:1px; " +
									" border-color:black'>");
					writer.println("<TR><TH id=common_th COLSPAN=2>TABLE DISPLAY SETTINGS</TH></TR>");
					writer.println("<TR><TD WIDTH=55%>");
					writer.println(	"<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0 " +
									" style='border-style:double; border-width:1px; " +
									" border-color:black'>");
					writer.println("<TR><TH id=common_hed>No.of Records Per Page</TH>");
					writer.println(	"<TD id=common_td><INPUT NAME=records_per_page TYPE=text" +
									" style='background:azure;width:145px' VALUE=11>");
					writer.println("</TD></TR>");
					writer.println("<TR><TH id=common_hed>Starting From Record</TH>");
					writer.println(	"<TD id=common_td><INPUT NAME=start_index TYPE=text " +
									"style='background:azure;width:145px' VALUE=1>");
					writer.println("</TD></TR>");
					writer.println("<TR><TH id=common_hed>Display Mode</TH>");
					writer.println(	"<TD id=common_td><SELECT NAME=display_mode " +
									"style='width:145px; background:azure'>");
					writer.println("<OPTION VALUE=0 SELECTED>Horizontal</OPTION>");
					writer.println("<OPTION VALUE=1>Vertical</OPTION>");
					writer.println("</SELECT></TD></TR>");
					writer.println("<TR><TH id=common_hed>Header Repeat Count</TH>");
					writer.println(	"<TD id=common_td><INPUT NAME=head_rep_count TYPE=text " +
									"style='background:azure;width:145px' VALUE=11>");
					writer.println("</TD></TR>");
					writer.println("<TR><TH id=common_hed>Sort By</TH>");
					writer.println(	"<TD id=common_td><SELECT NAME=sort_column " +
									"style='width:145px; background:azure'>");
					writer.println("<OPTION VALUE=none>----------</OPTION>");
					for(int i=0;i<colNames.size();i++) {
						writer.println(	"<OPTION VALUE=" + colNames.elementAt(i).toString() + " >" +
										colNames.elementAt(i).toString() + "</OPTION>");
					}
					writer.println("</SELECT></TD></TR>");
					writer.println("<TR><TH id=common_hed>Order</TH>");
					writer.println(	"<TD id=common_td><SELECT NAME=sort_order " +
									"style='width:145px; background:azure'>");
					writer.println("<OPTION VALUE=0 SELECTED>----------</OPTION>");
					writer.println("<OPTION VALUE=0>Ascending</OPTION>");
					writer.println("<OPTION VALUE=1>Descending</OPTION>");
					writer.println("</SELECT></TD></TR>");
					writer.println("</TABLE>");
					writer.println("<TD WIDTH=45%>");
					writer.println(	"<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0 " +
									" style='border-style:double; border-width:1px; " +
									" border-color:black'> ");
					writer.println(	"<TR><TH id=common_hed style='text-align:center;font-weight:bold'>" +
									"Select the Columns to be Displayed</TH></TR>");
					writer.println(	"<TR><TD id=common_td><SELECT NAME=selected_columns MULTIPLE " +
									" style='width:250px;height:152px' >");
					for(int i=0;i<colNames.size();i++)	{
						writer.println(	"<OPTION style='background:azure; text-transform:lowercase' " +
										"VALUE=" + colNames.elementAt(i).toString() + " SELECTED>" +
										colNames.elementAt(i) + " ( " + colTypes.elementAt(i) + "[" +
										colSizes.elementAt(i) + "] )</OPTION>");
					}
					writer.println("</SELECT></TABLE></TD></TR>");
				
					for(int i=0;i<colTypes.size();i++) {
						String ctype = "," + colTypes.elementAt(i).toString().toUpperCase() + ",";
						if(largeObjects.indexOf(ctype) != -1) largeColNames.add(colNames.elementAt(i));
					}

					writer.println(	"<INPUT TYPE=hidden NAME=large_column_count VALUE=" + 
									largeColNames.size() + ">");
					if(largeColNames.size()>0) {
						writer.println("<TR><TD COLSPAN=2>");
						writer.println("<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0>");
						writer.println(	"<TR><TH id=common_hed style='font-weight:bold;text-align:center' " +
										" COLSPAN=2>Choose the Content-type of the " +
										"following Column(s)</TH></TR>");
						writer.println(	"<TR><TH id=common_hed style='font-weight:bold;text-align:center'>" +
										"Column Name</TH>");
						writer.println(	"<TH id=common_hed style='font-weight:bold;text-align:center'>" +
										"Content Type</TH></TR>");
						for(int i=0;i<largeColNames.size();i++) {
							writer.println(	"<TR><TD id=common_td style='text-align:center' WIDTH=50%>" + 
											largeColNames.elementAt(i) + "</TD>");
							writer.println(	"<INPUT TYPE=hidden NAME='large_column_name" + i + 
											"' VALUE='" + largeColNames.elementAt(i) + "' >");
							writer.println(	"<TD id=common_td style='text-align:center'>" +
											"<SELECT NAME='large_column_type" + i + 
											"' style='width:145px; background:azure'>");
							writer.println("<OPTION VALUE=none>----------</OPTION>");
							writer.println("<OPTION VALUE=text/plain>text/plain</OPTION>");
							writer.println("<OPTION VALUE=image/gif>image/gif</OPTION>");
							writer.println("<OPTION VALUE=image/jpg>image/jpg</OPTION>");
							writer.println("</SELECT></TD></TR>");
						}
						writer.println("</TABLE></TD></TR>");
					}

					writer.println("<TR>");
					writer.println(	"<TD ALIGN=right>" +
									"<IMG NAME=pic11 SRC='pics/reset1.jpg' " +
									" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,11)' " +
									" onMouseUp='putOff(this,11)' onMouseOut='putOff(this,11)' " +
									" onClick='browseform.reset()' STYLE='cursor:hand'></TD>");

					writer.println(	"<TD ALIGN=left>" +
									"<IMG NAME=pic12 SRC='pics/fetch1.jpg' " +
									" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,12)' " +
									" onMouseUp='putOff(this,12)' onMouseOut='putOff(this,12)' " +
									" onClick='browseform.submit()' STYLE='cursor:hand'></TD>");

					writer.println("</TR></TABLE></FORM></BODY></HTML>");
				}
			}
		}
		writer.close();
	}
}