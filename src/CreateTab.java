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

public class CreateTab extends HttpServlet {
	
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

		String schemaTerm	= null;
		String schema		= null;

		String  error_message = null;
		boolean error_occured = false;
		boolean tableCreated  = false;

		int columnCount	= 0;

		String tableName		= null;
		String dbProductName    = null;
		String dbProductVersion = null;

		String[] columnNames = null;
		String[] columnTypes = null;
		String[] columnSizes = null;
		String[] columnPk	 = null;
		String[] columnUn	 = null;
		String[] columnNn	 = null;

		Vector typeNames = null;
		
		PrintWriter writer = null;

		HttpSession session = req.getSession(false);

		if(session == null) {
			res.setContentType("text/html");
			writer = res.getWriter();

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

			tableName	= req.getParameter("table_name");
			columnNames	= req.getParameterValues("column_names");
			columnTypes	= req.getParameterValues("column_types");			
			columnSizes	= req.getParameterValues("column_sizes");
			columnPk	= req.getParameterValues("column_pk");
			columnUn	= req.getParameterValues("column_un");
			columnNn	= req.getParameterValues("column_nn");
			
			String primaryKey = "";
			String query = "create table " + tableName + " ( ";
			columnCount = columnNames.length;

			for(int i=0;i<columnCount;i++) {
				String colname = columnNames[i];
				String coltype = columnTypes[i];
				String colsize = columnSizes[i];

				int primary = Integer.parseInt(columnPk[i]);
				int unique	= Integer.parseInt(columnUn[i]);
				int notnull = Integer.parseInt(columnNn[i]);
				
				query += colname + " " + coltype;
				
				if(colsize.length() > 0 && !colsize.equals("0"))	query += "(" + colsize + ")";

				if(primary == 1)					 primaryKey += colname + ",";
				else if(unique == 1 && notnull == 1) primaryKey += colname + ",";
				else if(unique  == 1)				 query += " unique";
				else if(notnull == 1)				 query += " not null";
				query += ",";
			}
			
			if(primaryKey.length() > 0)	{
				primaryKey	= primaryKey.substring(0,primaryKey.length()-1);
				query += "primary key(" + primaryKey + "))";
			}
			else query = query.substring(0,query.length()-1) + " )";

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
				try {	
					statement.execute(query);
					tableCreated = true;
				}
				catch(Exception e)	{	
					tableCreated = false;
					error_message = e.toString();
				}

				if(tableCreated) {
					String message = tableName + " table Created Successfully.";
					res.setContentType("text/html");
					writer = res.getWriter();

					writer.println("<HTML>");
					writer.println("<HEAD>");
					writer.println("<META NAME='Author' CONTENT='Vamsi'>");
					writer.println("<SCRIPT LANGUAGE='javascript'>");
					writer.println("function loadPages() { ");
					writer.println("	window.parent.left.location.href='ListDB'; ");
					writer.println( "	window.parent.right.location.href=" +
									"'DBOperations?message=" + message + "'; ");
					writer.println("} ");
					writer.println("</SCRIPT>");
					writer.println("</HEAD>");
					writer.println("<BODY onLoad='loadPages()' BGCOLOR=#ffffff>");
					writer.println("</BODY></HTML>");
					writer.close();					
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
			
			if(error_occured || !tableCreated) {
				res.setContentType("text/html");
				writer = res.getWriter();

				writer.println("<HTML>");
				writer.println("<HEAD>");
				writer.println("<META NAME='Author' CONTENT='Vamsi'>");
				writer.println("<LINK REL='stylesheet' TYPE='text/css' HREF='styles.css'>");
				writer.println( "<SCRIPT LANGUAGE='javascript' TYPE='text/javascript' " +
								" SRC='script.js'></SCRIPT>");
				writer.println("</HEAD>");
				writer.println("<BODY onLoad='loadImages()' BGCOLOR=#ffffff " +
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

			if(error_occured) {
				writer.println("<H3>" + error_message + "</H3></BODY></HTML>");
				writer.close();
			}
			else if(!tableCreated) {
				writer.println("<HR WIDTH=100%>");
				writer.println("<TABLE ALIGN=center WIDTH=100% BORDER=0 CELLSPACING=1 CELLPADDING=4>");
				writer.println("<TR><TD id=insert_err_msg>Syntax Error, Recheck the data</TD></TR>"); 
				writer.println("<TR><TD id=common_hed>" + query + "</TD></TR>");
				writer.println("<TR><TD id=common_hed>" + error_message + "</TD></TR>");
				writer.println("</TABLE>");
				writer.println("<HR WIDTH=100%>");

				writer.println("<FORM NAME=create_form METHOD=post ACTION='CreateTab'>");
				writer.println(	"<SELECT NAME=column_types STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px' MULTIPLE>");
				for(int i=0;i<columnCount;i++) writer.println("<OPTION VALUE=" + columnTypes[i] + ">");
				writer.println("</SELECT>");

				writer.println(	"<SELECT NAME=column_sizes STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px' MULTIPLE>");
				for(int i=0;i<columnCount;i++) writer.println("<OPTION VALUE=" + columnSizes[i] + ">");
				writer.println("</SELECT>");
				
				writer.println(	"<SELECT NAME=column_pk STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px' MULTIPLE>");
				for(int i=0;i<columnCount;i++) writer.println("<OPTION VALUE=" + columnPk[i] + ">");
				writer.println("</SELECT>");

				writer.println(	"<SELECT NAME=column_un STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px' MULTIPLE>");
				for(int i=0;i<columnCount;i++) writer.println("<OPTION VALUE=" + columnUn[i] + ">");
				writer.println("</SELECT>");
				
				writer.println(	"<SELECT NAME=column_nn STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px' MULTIPLE>");
				for(int i=0;i<columnCount;i++) writer.println("<OPTION VALUE=" + columnNn[i] + ">");
				writer.println("</SELECT>");								

				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH COLSPAN=2 id=common_th>CREATE TABLE ...</TH></TR>");
				
				writer.println("<TR><TD WIDTH=50%>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=3 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH id=common_hed STYLE='font-weight:bold'>TABLE NAME</TH>");
				writer.println("<TD STYLE='background:#c5d6df;text-align:center'>");
				writer.println(	"<INPUT TYPE=text NAME=table_name VALUE=\"" + tableName + "\" " +
								" STYLE='background:azure;width:180px'>");
				writer.println("</TD></TR>");
				writer.println("<TR><TD COLSPAN=2 STYLE='background:#c5d6df;text-align:center'>");
				writer.println(	"<SELECT NAME=column_names MULTIPLE STYLE='width:100%;height:150px' " +
								" onChange='loadValues(document.create_form,this)'>");
				for(int i=0;i<columnCount;i++)
					writer.println(	"<OPTION VALUE=\"" + columnNames[i] + "\">" + 
									columnNames[i] + "</OPTION>");
				writer.println("</SELECT></TD></TR>");
				writer.println("</TABLE>");
				writer.println("</TD>");

				writer.println("<TD WIDTH=50%>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println(	"<TR><TH id=common_hed STYLE='font-weight:bold;text-align:center' " +
								" COLSPAN=3>FIELD PROPERFIES</TH></TR>");
				writer.println("<TR><TH id=common_hed STYLE='font-weight:bold'>Field Name</TH>");
				writer.println("<TD COLSPAN=2 STYLE='background:#c5d6df;text-align:center' >");
				writer.println("<INPUT TYPE=text NAME=field_name STYLE='background:azure;width:180px'"+
							   " onFocus=\"isEmpty(document.create_form.table_name)\">"); 
				writer.println("</TD></TR>");
				writer.println("<TR><TH id=common_hed STYLE='font-weight:bold'>Field Type</TH>");
				writer.println("<TD COLSPAN=2 STYLE='background:#c5d6df;text-align:center' >");
				writer.println(	"<SELECT STYLE='background:azure;width:180px' NAME=field_type " +
								" onClick=\"isEmpty(document.create_form.field_name)\">");
				
				for(int i=0;i<typeNames.size();i++) {
					String type = typeNames.elementAt(i).toString();
					writer.println("<OPTION VALUE='" + type + "'>" + type + "</OPTION>");
				}
				writer.println("</SELECT>");

				writer.println("</TD></TR>");
				writer.println("<TR><TH id=common_hed STYLE='font-weight:bold'>Field Size</TH>");
				writer.println("<TD COLSPAN=2 STYLE='background:#c5d6df;text-align:center' >");
				writer.println("<INPUT TYPE=text STYLE='background:azure;width:180px' NAME=field_size>");
				writer.println("</TD></TR>");
				writer.println("<TR>");
				writer.println("<TD id=common_data STYLE='text-align:center;font-weight:bold'>");
				writer.println("<INPUT TYPE=checkbox NAME=primary>  Primary </TD>");
				writer.println("<TD id=common_data STYLE='text-align:center;font-weight:bold'>");
				writer.println("<INPUT TYPE=checkbox NAME=unique>  Unique </TD>");
				writer.println("<TD id=common_data STYLE='text-align:center;font-weight:bold'>");
				writer.println("<INPUT TYPE=checkbox NAME=notnull>  NotNull </TD>");
				writer.println("</TR>");
				writer.println("<TR>");
				writer.println(	"<TD WIDTH=34% STYLE='background:#f5f5f5;text-align:center'>" +
								"<IMG NAME=pic14 SRC='pics/add1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,14)' " +
								" onMouseUp='putOff(this,14)' onMouseOut='putOff(this,14)' " +
								" onClick='addField(document.create_form)' STYLE='cursor:hand'></TD>");
				writer.println(	"<TD WIDTH=33% STYLE='background:#f5f5f5;text-align:center'>" +
								"<IMG NAME=pic15 SRC='pics/change1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,15)' " +
								" onMouseUp='putOff(this,15)' onMouseOut='putOff(this,15)' " +
								" onClick='changeField(document.create_form)' STYLE='cursor:hand'></TD>");
				writer.println(	"<TD WIDTH=33% STYLE='background:#f5f5f5;text-align:center'>" +
								"<IMG NAME=pic16 SRC='pics/remove1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,16)' " +
								" onMouseUp='putOff(this,16)' onMouseOut='putOff(this,16)' " +
								" onClick='removeField(document.create_form)' STYLE='cursor:hand'></TD>");
				writer.println("</TR>");
				writer.println("</TABLE>");
				writer.println("</TD></TR>");

				writer.println("<TR>");
				writer.println(	"<TD ALIGN=right><IMG NAME=pic11 SRC='pics/reset1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,11)' " +
								" onMouseUp='putOff(this,11)' onMouseOut='putOff(this,11)' " +
								" onClick='document.create_form.reset()' STYLE='cursor:hand'></TD>");
				writer.println( "<TD ALIGN=left><IMG NAME=pic17 SRC='pics/create1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,17)' " +
								" onMouseUp='putOff(this,17)' onMouseOut='putOff(this,17)' " +
								" onClick='submitForm(document.create_form)' STYLE='cursor:hand'></TD>");
				writer.println("</TABLE>");
				writer.println("</BODY></HTML>");
				writer.close();
			}
		}
	}
}