import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.IOException;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;

import java.util.Vector;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExportDB extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) 
				throws ServletException, IOException {

		Connection connection = null;
		DatabaseMetaData dbMetaData = null;
		ResultSet resultSet = null;
		
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;
		
		String dbProductName	= null;
		String dbProductVersion = null;

		String schemaTerm	= null;
		String schema		= null;
		int	   schemaValue	= 0;

		String types[] = { "TABLE" };

		Vector tableNames = new Vector();
		Vector colNames	  = new Vector();	

		String tableName = null;
		String message	 = null;

		boolean error_occured = false;
		String  error_message = null;

		HttpSession session = req.getSession(false);
		PrintWriter writer  = res.getWriter();

		if(session == null) {
			writer.println("<HTML>");
			writer.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
							"Sorry Your session expired. Please Login Again.'\"");
			writer.println("</BODY>");
			writer.println("</HTML>");
		}
		else {
			driver = session.getAttribute("driver").toString();
			url    = session.getAttribute("url").toString();
			userid = session.getAttribute("userid").toString();		
			pass   = session.getAttribute("pass").toString();

			dbProductName	 = session.getAttribute("dbProductName").toString();
			dbProductVersion = session.getAttribute("dbProductVersion").toString();
			schemaTerm		 = session.getAttribute("schemaTerm").toString();
			schemaValue		 = Integer.parseInt(session.getAttribute("schemaValue").toString());
			schema			 = session.getAttribute("schema").toString();

			tableName = req.getParameter("table_name");
			message	  = req.getParameter("message");

			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url,userid,pass);	
				dbMetaData = connection.getMetaData();		
			}
			catch(Exception e) {
				error_occured = true;
				error_message = e.toString();
			}

			if(!error_occured) {
				try {
					if(schemaValue == 1) resultSet = dbMetaData.getTables(null,schema,null,types);
					else				 resultSet = dbMetaData.getTables(schema,null,null,types);

					while(resultSet.next()) tableNames.add(resultSet.getString(3).trim());
					if(resultSet != null) resultSet.close();

					if(tableName != null) {
						if(schemaValue == 0) resultSet = dbMetaData.getColumns(schema,null,tableName,null);
						else				 resultSet = dbMetaData.getColumns(null,schema,tableName,null);
	
						while(resultSet.next()) colNames.add(resultSet.getString(4));
						if(resultSet != null) resultSet.close();
					}
				}
				catch(Exception e) {
					error_occured = true;
					error_message = e.toString();
				}
				try {
					connection.close();
					resultSet.close();
				}
				catch(Exception e)	{	e.printStackTrace();	}
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
							"<A HREF='DescDB'>" +
							"<img onMouseOver='putOn(this,1)' onMouseOut='putOff(this,1)' " +
							" name='pic1' src='pics/structure1.jpg' " +
							" border=0 width=80 height=26 align=absbottom></A>" +
							"<A HREF='DBProperties'>" +
							"<img onMouseOver='putOn(this,2)' onMouseOut='putOff(this,2)' " +
							" name='pic2' src='pics/properties1.jpg' border=0 " +
							" width=80 height=26 align=absbottom></A>" +
							"<A HREF='DBQuery'>" +
							"<img onMouseOver='putOn(this,4)' onMouseOut='putOff(this,4)' " +
							"name=pic4 src='pics/sql1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='Import'>" +
							"<img onMouseOver='putOn(this,5)' onMouseOut='putOff(this,5)' " +
							"name=pic5 src='pics/import1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>"+
							"<img name=pic7 src='pics/export2.jpg' border=0 " +
							"width=80 height=26 align=absbottom>" +
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

			if(error_occured) writer.println("<H3>" + error_message + "</H3></BODY></HTML>");
			else {
				if(message != null) {
					writer.println("<HR WIDTH=100%>");
					writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
					writer.println("<TR>");
					writer.println("<TH id=insert_norm_msg>" + message + "</TH>");
					writer.println("</TR>");
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%>");
				}

				writer.println("<FORM NAME=export_form METHOD=post ACTION='ExportDB'>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=2 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH COLSPAN=2 id=common_th>EXPORT TABLE ...</TH></TR>");
				
				writer.println("<TR><TD WIDTH=50%>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=3 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH id=common_hed STYLE='font-weight:bold'>TABLE NAME</TH>");
				writer.println("<TD STYLE='background:#c5d6df;text-align:center'>");
				writer.println(	"<SELECT NAME=table_name STYLE='background:azure;width:180px' " +
								" onChange='callExportDB(this)'>");
				writer.println("<OPTION VALUE=0> ------- Select a Table ------- </OPTION>");
				for(int i=0;i<tableNames.size();i++) {
					String name = tableNames.elementAt(i).toString();
					writer.print("<OPTION VALUE='" + name + "' ");
					if(tableName != null && tableName.equals(name)) writer.print(" SELECTED>");
					else											writer.print(">");
					writer.println(name + "</OPTION>");
				}
				writer.println("</SELECT>");
				writer.println("</TD></TR>");
				writer.println("<TR><TD COLSPAN=2 STYLE='background:#c5d6df;text-align:center'>");
				writer.println("<SELECT NAME=column_names MULTIPLE STYLE='width:100%;height:150px'>");
				for(int i=0;i<colNames.size();i++) {
					String name = colNames.elementAt(i).toString();
					writer.println("<OPTION VALUE='" + name + "' SELECTED>" + name + "</OPTION>");
				}
				writer.println("</SELECT></TD></TR>");
				writer.println("</TABLE>");
				writer.println("</TD>");

				writer.println("<TD WIDTH=50%>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println(	"<TR><TH id=common_hed STYLE='font-weight:bold;text-align:center' " +
								" COLSPAN=3>EXPORT OPTIONS</TH></TR>");
				writer.println("<TR><TH id=common_hed STYLE='font-weight:bold'>FORMAT</TH>");
				writer.println("<TD COLSPAN=2 STYLE='background:#c5d6df;text-align:center' >");
				writer.println("<SELECT NAME=format STYLE='background:azure;width:180px'>");
				writer.println("<OPTION VALUE='sql'>SQL FILE</OPTION>");
				writer.println("<OPTION VALUE='html'>HTML TABLE</OPTION>");
				writer.println("<OPTION VALUE='csv'>CSV - Excel</OPTION>");
				writer.println("</SELECT>");
				writer.println("</TD></TR>");
				writer.println("<TR><TH id=common_hed STYLE='font-weight:bold'>CONDITION</TH>");
				writer.println("<TD COLSPAN=2 STYLE='background:#c5d6df;text-align:center'>");
				writer.println("<INPUT TYPE=text NAME=condition STYLE='background:azure;width:180px'>");
				writer.println("</TD></TR>");
				writer.println("<TR><TD COLSPAN=3 id=common_td STYLE='text-align:center'>");
				writer.println("Condition must be a valid WHERE clause.<BR>");
				writer.println("( Ex: empno between 110 and 240 )<BR>");
				writer.println("( Ex: name like '%kumar%' )<BR>");
				writer.println("( Ex: salary > 8000 )");
				writer.println("</TD></TR>");
				writer.println("<TR>");
				writer.println("<TD id=common_data STYLE='text-align:center;font-weight:bold'>");
				writer.println("<INPUT TYPE=radio NAME=export VALUE=0>Structure</TD>");
				writer.println("<TD id=common_data STYLE='text-align:center;font-weight:bold'>");
				writer.println("<INPUT TYPE=radio NAME=export VALUE=1 CHECKED>Data</TD>");
				writer.println("<TD id=common_data STYLE='text-align:center;font-weight:bold'>");
				writer.println("<INPUT TYPE=radio NAME=export VALUE=-1>Both</TD>");
				writer.println("</TR>");
				writer.println("</TABLE>");
				writer.println("</TD></TR>");

				writer.println("<TR>");
				writer.println(	"<TD ALIGN=right STYLE='background:#f5f5f5'>" +
								" <IMG NAME=pic11 SRC='pics/reset1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,11)' " +
								" onMouseUp='putOff(this,11)' onMouseOut='putOff(this,11)' " +
								" onClick='document.export_form.reset()' STYLE='cursor:hand'></TD>");
				writer.println( "<TD ALIGN=left STYLE='background:#f5f5f5'>" +
								" <IMG NAME=pic22 SRC='pics/export11.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,22)' " +
								" onMouseUp='putOff(this,22)' onMouseOut='putOff(this,22)' " +
								" onClick='submitExportForm(document.export_form)' STYLE='cursor:hand'></TD>");
				writer.println("</TR></TABLE></FORM>");				
			}
		}
		writer.close();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
System.out.println("inside dopost");		
		Connection connection = null;
		Statement  statement  = null;
		ResultSet  resultSet  = null;
		DatabaseMetaData dbMetaData = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm	= null;
		String schema		= null;
		int	   schemaValue	= 0;

		String dbProductName    = null;
		String dbProductVersion = null;

		String  error_message = null;
		boolean error_occured = false;

		String tableName = null;
		String format	 = null;
		String condition = null;
		int	   export	 = 1;

		String primaryKey = ",";
		String foreignKey = ",";
		String references = ",";

		String[] colNames = null;
		String[] typeName = null;
		int	  [] colTypes = null;
		int	  [] colSizes = null;
		int	  [] decimals = null;
		int	  [] Nullable = null;

		int columnCount = 0;

		HttpSession session = req.getSession(false);

		if(session == null) {
			res.setContentType("text/html");
			PrintWriter writer  = res.getWriter();

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
			schemaValue		= Integer.parseInt(session.getAttribute("schemaValue").toString());
			schema			= session.getAttribute("schema").toString();
	
			tableName	= req.getParameter("table_name");
			colNames	= req.getParameterValues("column_names");
			format		= req.getParameter("format");
			condition	= req.getParameter("condition");
			export		= Integer.parseInt(req.getParameter("export"));
			System.out.println("HELLO"+export);
			columnCount = colNames.length;

			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url,userid,pass);
			}
			catch(Exception e) {
				error_occured = true;
				error_message = e.toString();
			}
			
			if(!error_occured) {
				try {				
					if(export != 1)	{
						dbMetaData = connection.getMetaData();

						if(schemaValue == 0) 
								resultSet = dbMetaData.getColumns(schema,null,tableName,null);
						else	resultSet = dbMetaData.getColumns(null,schema,tableName,null);
	
						colTypes = new int	 [columnCount];
						colSizes = new int	 [columnCount];
						decimals = new int	 [columnCount];						
						Nullable = new int	 [columnCount];
						typeName = new String[columnCount];

						while(resultSet.next()) {
							String cname = resultSet.getString(4);
				
							for(int i=0;i<columnCount;i++) {
								if(cname.equals(colNames[i].trim())) {
									colTypes[i] = resultSet.getInt(5);
									typeName[i] = resultSet.getString(6);
									colSizes[i] = resultSet.getInt(7);
									decimals[i] = resultSet.getInt(9);
									Nullable[i] = resultSet.getInt(11);
								}
							}
						}
						if(resultSet != null) resultSet.close();

						try {
							if(schemaValue == 0) 
									resultSet = dbMetaData.getPrimaryKeys(schema,null,tableName);
							else	resultSet = dbMetaData.getPrimaryKeys(null,schema,tableName);
						
							while(resultSet.next()) {
								String pKey		= resultSet.getString(4);
								boolean exists	= false;

								for(int i=0;i<columnCount;i++) {
									if(pKey.equals(colNames[i]))	exists = true;
								}

								if(exists)	primaryKey += pKey + ",";
							}
							if(resultSet != null) resultSet.close();
						}
						catch(Exception e)	{	e.printStackTrace();	}
	
						try {
							if(schemaValue == 0) 
								 resultSet = dbMetaData.getCrossReference(	schema,	null, null,
																			schema,	null, tableName);
							else resultSet = dbMetaData.getCrossReference(	null, schema, null,
																			null, schema, tableName);
	
							while(resultSet.next()) {
								String refTable	 = resultSet.getString(3);
								String refColumn = resultSet.getString(4);
								String fKey		 = resultSet.getString(8);
								boolean exists	 = false;

								for(int i=0;i<columnCount;i++) {
									if(fKey.equals(colNames[i]))	exists = true;
								}

								if(exists) {
									references += refTable + "(" + refColumn + "),";
									foreignKey += fKey + ",";
								}
							}
							if(resultSet != null) resultSet.close();
						}
						catch(Exception e)	{	e.printStackTrace();	}
					}

					if(export != 0) {
						statement = connection.createStatement();

						String query = "select ";
						for(int i=0;i<columnCount;i++) query += colNames[i] + ",";
						query = query.substring(0,query.length()-1) + " from " + tableName;
						if(condition != null && condition.trim().length()>1) 
							query += " where " + condition;

						resultSet = statement.executeQuery(query);
					}
				}
				catch(Exception e) {
					error_occured = true;
					error_message = e.toString();
				}
			}

			if(error_occured)	res.sendRedirect(	"ExportDB?table_name=" + tableName + 
													"&message=" + error_message);
			else {
				String filename = schema + "_" + tableName;
				if(export == 0)			filename += "_structure." + format;
				else if(export == 1)	filename += "_data." + format;
				else					filename += "." + format;
				res.setContentType("application/html");
				res.setHeader(	"Content-Disposition", "attachment;filename=" + filename);
				PrintStream writer = new PrintStream(res.getOutputStream());
				System.out.println("downloading start");
				if(format.equalsIgnoreCase("html")) {
					writer.println("<HTML><HEAD>");
					writer.println("<STYLE>");
					writer.println(	"#hed { " +
									"	background:#336699; " +
									"	color:#ffffff; " +
									"	text-align:center; " +
									"	text-transform:uppercase; " +
									"	font-family:tahoma; " +
									"	font-weight:bold; " +
									"	font-size:14; " +
									"} " +
									"#sub_hed { " +
									"	background:#abcdef; " +
									"	color:black; " +
									"	text-align:center; " +
									"	text-transform:capitalize; " +
									"	font-family:tahoma; " +
									"	font-weight:bold; " +
									"	font-size:13; " +
									"} " +
									"#data { " +
									"	background:#c5d6df; " +
									"	color:black; " +
									"	text-align:center; " +
									"	text-transform:lowercase; " +
									"	font-family:tahoma; " +
									"	font-weight:normal; " +
									"	font-size:12; " +
									"} ");
					writer.println("</STYLE>");
					writer.println("<BODY BGCOLOR=#ffffff>");

					if(export != 1)	{
						writer.println("<TABLE WIDTH=100% BORDER=0 CELLSPACING=1 CELLPADDING=4>");
						writer.println(	"<TR><TH id=hed COLSPAN=5>" + tableName + 
										" Table Structure</TH></TR>");
						writer.println("<TR>");
						writer.println("<TH id=sub_hed>Column Name</TH>");
						writer.println("<TH id=sub_hed>Type</TH>");
						writer.println("<TH id=sub_hed>Size</TH>");
						writer.println("<TH id=sub_hed>Nullable</TH>");
						writer.println("<TH id=sub_hed>Key</TH>");
						writer.println("</TR>");
						
						for(int i=0;i<columnCount;i++) {
							writer.println("<TR>");
							writer.println("<TD id=data>" + colNames[i] + "</TD>");
							writer.println("<TD id=data>" + typeName[i] + "</TD>");

							writer.print("<TD id=data>");
							if(decimals[i] <= 0)		writer.println(colSizes[i] + "</TD>");
							else	writer.println(colSizes[i] + "," + decimals[i] + "</TD>");

							if(Nullable[i] == 0)	writer.println("<TD id=data> no </TD>");
							else					writer.println("<TD id=data> yes </TD>");

							String key = "";
							if(primaryKey.indexOf("," + colNames[i] + ",") != -1) {
								key = " Primary Key ";
								if(foreignKey.indexOf("," + colNames[i] + ",") != -1) 
									key += ", Foriegn Key";
							}
							else if(foreignKey.indexOf("," + colNames[i] + ",") != -1)
									key += " Foriegn Key ";
							else	key += " - ";
							writer.println("<TD id=data>" + key + "</TD>");

							writer.println("</TR>");
						}
						writer.println("</TABLE><BR>");
					}

					if(export != 0) {
						writer.println("<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0>");
						writer.println(	"<TR><TH COLSPAN=" + columnCount + " id=hed>" + 
										tableName + " Table Data</TH></TR>");

						writer.println("<TR>");
						for(int i=0;i<columnCount;i++) 
							writer.println("<TH id=sub_hed>" + colNames[i] + "</TH>");
						writer.println("</TR>");
						
						try {
							while(resultSet.next()) {
								writer.println("<TR>");

								for(int i=0;i<columnCount;i++) {
									String data = null;

									switch(colTypes[i]) {
										case Types.LONGVARBINARY:
										case Types.LONGVARCHAR	:
										case Types.VARBINARY	:
										case Types.BINARY		:
										case Types.BLOB			:
										case Types.CLOB			:	data = "$LARGE_OBJECT$"; break;
										default					:	data = resultSet.getString(i+1);
									}

									writer.println("<TD id=data>" + data + "</TD>");
								}

								writer.println("</TR>");
							}
						}
						catch(Exception e) {}

						writer.println("</TABLE>");
					}
					writer.println("</BODY></HTML>");
				}

				if(format.equalsIgnoreCase("csv")) {
					if(export != 1)	{
						writer.println();
						writer.println("---------- " + tableName + " Table Structure ----------");
						writer.println();
						writer.println("Column Name,Type,Size,Nullable,Key");
						writer.println();
						
						for(int i=0;i<columnCount;i++) {
							writer.print(colNames[i] + "," + typeName[i] + ","); 

							if(decimals[i] <= 0)	writer.print(colSizes[i] + ",");
							else	writer.print(colSizes[i] + "." + decimals[i] + ",");

							if(Nullable[i] == 0)	writer.print("no");
							else					writer.print("yes");

							String key = "";
							if(primaryKey.indexOf("," + colNames[i] + ",") != -1) {
								key = " Primary Key ";
								if(foreignKey.indexOf("," + colNames[i] + ",") != -1) 
									key += "- Foriegn Key";
							}
							else if(foreignKey.indexOf("," + colNames[i] + ",") != -1)
									key += " Foriegn Key ";
							else	key += " - ";
							writer.println("," + key);
						}
						writer.println();
					}

					if(export != 0) {
						writer.println();
						writer.println("---------- " + tableName + " Table Data ----------");
						writer.println();

						for(int i=0;i<columnCount;i++)	writer.print(colNames[i] + ",");
						writer.println();
						
						try {
							while(resultSet.next()) {
								for(int i=0;i<columnCount;i++) {
									String data = null;

									switch(colTypes[i]) {
										case Types.LONGVARBINARY:
										case Types.LONGVARCHAR	:
										case Types.VARBINARY	:
										case Types.BINARY		:
										case Types.BLOB			:
										case Types.CLOB			:	data = "$LARGE_OBJECT$"; break;
										default					:	data = resultSet.getString(i+1);
																	if(data == null) data = "null";
									}

									writer.print(data.replaceAll(","," ") + ",");
								}
								writer.println();
							}
						}
						catch(Exception e) {}
					}
				}

				if(format.equalsIgnoreCase("sql")) {
					if(export != 1)	{
						String query = "create table " + tableName + " (";
					
						for(int i=0;i<columnCount;i++) {
							query += colNames[i] + " " + typeName[i];
							
							if(colSizes[i] <= 2000)	{
								query += "(" + colSizes[i];
								if(decimals[i] > 0)	query += "," + decimals[i] + ")";
								else				query += ")";
							}

							if(Nullable[i] == 0)	query += " not null";
							query += ", ";
						}
						
						query = query.substring(0,query.length()-2);

						if(primaryKey.length() >1) {
							primaryKey = primaryKey.substring(1,primaryKey.length()-1);
							query += ", primary key(" + primaryKey + ")";
						}

						if(foreignKey.length() > 1)	{
							StringTokenizer fk = new StringTokenizer(foreignKey,",");
							StringTokenizer rf = new StringTokenizer(references,",");

							while(fk.hasMoreTokens()) query +=	", foreign key(" + fk.nextToken() + 
																") references "  + rf.nextToken();
						}

						query += ")";

						writer.println(query);
					}

					if(export != 0) {						
						try {
							System.out.println("both working");
							while(resultSet.next()) {
								int lobCount = 0;
								String query = "insert into " + tableName + " (";
								for(int i=0;i<columnCount;i++)	{
									switch(colTypes[i]) {
										case Types.LONGVARBINARY:
										case Types.LONGVARCHAR	:
										case Types.VARBINARY	:
										case Types.BINARY		:
										case Types.BLOB			:
										case Types.CLOB			:	lobCount++;	break;
										default					:	query += colNames[i] + ",";
																	break;
									}
									
								}
								query = query.substring(0,query.length()-1) + ") values(";
System.out.println(query);
								for(int i=0;i<columnCount;i++) {
									String data = resultSet.getString(i+1);

									switch(colTypes[i]) {
										case Types.BIT		:	
										case Types.SMALLINT :
										case Types.TINYINT	:	
										case Types.INTEGER	:	
										case Types.BIGINT	:	
										case Types.NUMERIC	:
										case Types.DECIMAL	:	
										case Types.REAL		:	
										case Types.DOUBLE	:
										case Types.FLOAT	:	if(data == null) query += "null,";
																else	query += data + ",";
																break;

										case Types.DATE		:	
										case Types.TIME		:
										case Types.TIMESTAMP:	
										case Types.CHAR		:
										case Types.VARCHAR	:	if(data == null) query += "null,";
																else	query += "'" + data + "',";
																break;

										case Types.LONGVARBINARY:
										case Types.LONGVARCHAR	:
										case Types.VARBINARY	:
										case Types.BINARY		:
										case Types.BLOB			:
										case Types.CLOB			:	break;
									}
								}

								query = query.substring(0,query.length()-1) + ")";
								if(lobCount < columnCount)	writer.println(query);
							}
						}
						catch(Exception e) {}
					}
				}
				writer.close();
			}
		}
	}
}