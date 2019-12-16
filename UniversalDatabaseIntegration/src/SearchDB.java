import java.util.Vector;
import java.lang.String;
import java.lang.Exception;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchDB extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		ResultSet  resultSet  = null;
		DatabaseMetaData dbMetaData = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm	= null;
		String schema		= null;
		int	   schemaValue	= 0;

		String types[] = { "TABLE" };

		String  error_message = null;
		boolean error_occured = false;

		String dbProductName    = null;
		String dbProductVersion = null;

		Vector tableNames = new Vector();

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

			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url,userid,pass);
				dbMetaData = connection.getMetaData();

				if(schemaValue == 0)	resultSet = dbMetaData.getTables(schema,null,null,types);
				else					resultSet = dbMetaData.getTables(null,schema,null,types);

				while(resultSet.next()) tableNames.add(resultSet.getString(3));
				if(resultSet != null)	resultSet.close();
			}
			catch(Exception e) {
				error_occured = true;
				error_message = e.toString();
			}

			try {
				if(resultSet != null) resultSet.close();
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
							"<A HREF='Import'>" +
							"<img onMouseOver='putOn(this,5)' onMouseOut='putOff(this,5)' " +
							"name=pic5 src='pics/import1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>"+
							"<A HREF='ExportDB'>" +
							"<img onMouseOver='putOn(this,7)' onMouseOut='putOff(this,7)' " +
							"name=pic7 src='pics/export1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='DBOperations'>" +
							"<img onMouseOver='putOn(this,8)' onMouseOut='putOff(this,8)' "+
							"name=pic8 src='pics/operations1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<img name=pic9 src='pics/search2.jpg' border=0 " +
							"width=80 height=26 align=absbottom></TD></TR>" +
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

			if(!error_occured) {
				writer.println("<FORM NAME=search_form METHOD=post ACTION='SearchDB'>");

				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=2 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH COLSPAN=3 id=common_th>SEARCH DATABASE ...</TH></TR>");
				writer.println(	"<TR><TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
								" KEYWORD </TH>");
				writer.println("<TD id=common_td COLSPAN=2>");
				writer.println("<INPUT TYPE=text NAME=keyword STYLE='background:azure;width:100%'>");
				writer.println("</TD></TR>");
				
				writer.println("<TR><TD COLSPAN=2 WIDTH=50%>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=3 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println(	"<TR><TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
								"LOOK IN TABLE(S)</TH></TR>");
				
				writer.println("<TR><TD STYLE='background:#c5d6df;text-align:center'>");
				writer.println("<SELECT NAME=table_names MULTIPLE STYLE='width:100%;height:136px'>");
				for(int i=0;i<tableNames.size();i++) {
					String name = tableNames.elementAt(i).toString();
					writer.println("<OPTION VALUE='" + name + "' SELECTED>" + name + "</OPTION>");
				}
				writer.println("</SELECT></TD></TR>");
				
				writer.println("</TABLE>");
				writer.println("</TD>");

				writer.println("<TD WIDTH=50%>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println(	"<TR><TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
								"SEARCH OPTIONS</TH></TR>");
				writer.println(	"<TR><TD id=common_td STYLE='font-weight:bold;text-indent:100px'>" +
								"<INPUT TYPE=checkbox NAME=match_case VALUE=1> Match Case </TD></TR>");
				writer.println(	"<TR><TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
								" LOOK FOR </TH></TR>");
				writer.println(	"<TR><TD id=common_td STYLE='font-weight:bold;text-indent:100px'>" +
								"<INPUT TYPE=radio NAME=search VALUE=0> Column Names Only </TD></TR>");
				writer.println(	"<TR><TD id=common_td STYLE='font-weight:bold;text-indent:100px'>" +
								"<INPUT TYPE=radio NAME=search VALUE=1 CHECKED> Data Only </TD></TR>");
				writer.println(	"<TR><TD id=common_td STYLE='font-weight:bold;text-indent:100px'>" +
								"<INPUT TYPE=radio NAME=search VALUE=2> Both </TD></TR>");
				writer.println("</TABLE></TD></TR>");

				writer.println("<TR><TD STYLE='background:#f5f5f5'></TD>");
				writer.println(	"<TD ALIGN=right STYLE='background:#f5f5f5'>" +
								" <IMG NAME=pic11 SRC='pics/reset1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,11)' " +
								" onMouseUp='putOff(this,11)' onMouseOut='putOff(this,11)' " +
								" onClick='document.search_form.reset()' STYLE='cursor:hand'></TD>");
				writer.println( "<TD ALIGN=left STYLE='background:#f5f5f5'>" +
								" <IMG NAME=pic24 SRC='pics/search11.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,24)' " +
								" onMouseUp='putOff(this,24)' onMouseOut='putOff(this,24)' " +
								" onClick='submitSearchForm(document.search_form)' " +
								" STYLE='cursor:hand'></TD>");
				writer.println("</TR></TABLE></FORM>");

				writer.println("</BODY></HTML>");
			}
			else	writer.println("<H3>" + error_message + "</H3></BODY></HTML>");
		}
		writer.close();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {

		Connection connection = null;
		ResultSet  resultSet  = null;
		Statement  statement  = null;
		DatabaseMetaData dbMetaData = null;
		ResultSetMetaData metaData	= null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm = null;
		String schema	  = null;
		int	   schemaValue= 0;

		String types[] = { "TABLE" };

		Vector tableNames = new Vector();

		String  error_message = null;
		boolean error_occured = false;

		String dbProductName    = null;
		String dbProductVersion = null;

		String[] selectedTables = null;
		String	 keyword		= null;
		boolean	 matchCase		= false;
		int		 search			= 0;

		int	   matchCount	  = 0;
		Vector matchedColumns = new Vector();
		Vector matchedData	  = new Vector();

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
			
			keyword			= req.getParameter("keyword").trim();
			selectedTables	= req.getParameterValues("table_names");
			matchCase		= (req.getParameter("match_case") == null) ? false : true;
			search			= Integer.parseInt(req.getParameter("search"));

			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url,userid,pass);
				statement  = connection.createStatement();
				dbMetaData = connection.getMetaData();

				if(schemaValue == 0)	resultSet = dbMetaData.getTables(schema,null,null,types);
				else					resultSet = dbMetaData.getTables(null,schema,null,types);

				while(resultSet.next()) tableNames.add(resultSet.getString(3));
				if(resultSet != null)	resultSet.close();

				if(search != 1) {
					if(schemaValue == 0) resultSet = dbMetaData.getColumns(schema,null,null,null);
					else				 resultSet = dbMetaData.getColumns(null,schema,null,null);

					while(resultSet.next()) {
						String tabName = resultSet.getString(3);
						String colName = resultSet.getString(4);

						for(int i=0;i<selectedTables.length;i++) {
							if(tabName.equals(selectedTables[i])) {
								if(!matchCase && colName.equalsIgnoreCase(keyword)) 
									matchedColumns.add(tabName);
								else if(matchCase && colName.equals(keyword)) 
									matchedColumns.add(tabName);
								break;
							}
						}
					}
					if(resultSet != null)	resultSet.close();
				}

				if(search != 0) {
					String newKey = (matchCase) ? keyword : keyword.toUpperCase();
					for(int i=0;i<selectedTables.length;i++) {
						String query = "select * from " + selectedTables[i];

						try	{	
							if(resultSet != null)	resultSet.close();
							resultSet = statement.executeQuery(query);
							metaData  = resultSet.getMetaData();
							int columnCount = metaData.getColumnCount();
							
							while(resultSet.next()) {
								for(int x=0;x<columnCount;x++) {
									String data		= null;
									String colName	= metaData.getColumnName(x+1);
									int colType		= metaData.getColumnType(x+1);
									
									switch(colType) {
										case Types.LONGVARBINARY:
										case Types.LONGVARCHAR	:
										case Types.VARBINARY	:
										case Types.BINARY		:
										case Types.BLOB			:
										case Types.CLOB			:	break;
										default					:	data = resultSet.getString(x+1);
									}

									if(data == null)	continue;
									
									if(!matchCase)	data = data.toUpperCase();
									if(data.indexOf(newKey) != -1) {
										Vector temp = new Vector();
										temp.add(data);
										temp.add(colName);
										temp.add(selectedTables[i]);
										matchedData.add(temp);
									}
								}
							}
						}
						catch(Exception e) {}
					}
				}
			}
			catch(Exception e) {
				error_occured = true;
				error_message = e.toString();
			}

			try {
				connection.close();
				statement.close();
				resultSet.close();
			}
			catch(Exception e) {}

			matchCount = matchedColumns.size() + matchedData.size();

			writer.println("<HTML>");
			writer.println("<HEAD>");
			writer.println("<META NAME='Author' CONTENT='Vamsi'>");
			writer.println("<LINK REL='stylesheet' TYPE='text/css' HREF='styles.css'>");
			writer.println( "<SCRIPT LANGUAGE='javascript' TYPE='text/javascript' " +
							" SRC='script.js'></SCRIPT>");
			writer.println("</HEAD>");
			writer.println(	"<BODY onLoad='loadImages()' BGCOLOR=#ffffff " +
							" link=blue alink=blue vlink=blue>");
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
							"<A HREF='Import'>" +
							"<img onMouseOver='putOn(this,5)' onMouseOut='putOff(this,5)' " +
							"name=pic5 src='pics/import1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>"+
							"<A HREF='ExportDB'>" +
							"<img onMouseOver='putOn(this,7)' onMouseOut='putOff(this,7)' " +
							"name=pic7 src='pics/export1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<A HREF='DBOperations'>" +
							"<img onMouseOver='putOn(this,8)' onMouseOut='putOff(this,8)' "+
							"name=pic8 src='pics/operations1.jpg' border=0 " +
							"width=80 height=26 align=absbottom></A>" +
							"<img name=pic9 src='pics/search2.jpg' border=0 " +
							"width=80 height=26 align=absbottom></TD></TR>" +
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

			if(matchCount > 0) {
				writer.println("<HR WIDTH=100%>");
				writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
				writer.println("<TR>");
				writer.println("<TH id=search_norm_msg>" + matchCount + " MATCH(S) FOUND </TH>");
				writer.println("</TR>");
				writer.println("</TABLE>");
				writer.println("<HR WIDTH=100%><BR>");

				writer.println("<TABLE WIDTH=100% BORDER=0 CELLSPACING=1 CELLPADDING=4 ALIGN=center>");
				writer.println("<TR><TH id=common_th>SEARCH  RESULTS</TH></TR>");
				
				for(int i=0;i<matchedColumns.size();i++) {
					String tabName = matchedColumns.elementAt(i).toString();
					writer.println("<TR><TD id=search_td>");
					writer.println(	"\"<SPAN STYLE='background:yellow'>" + keyword + "</SPAN>\"" +
									" is one of the columns of the table " +
									"<A HREF='DescTab?table_name=" + tabName + "'>" +
									tabName + "</A>");
					writer.println("</TD></TR>");
				}

				for(int i=0;i<matchedData.size();i++) {
					Vector temp		= (Vector) matchedData.elementAt(i);
					String data		= temp.elementAt(0).toString();
					String colName	= temp.elementAt(1).toString();
					String tabName	= temp.elementAt(2).toString();

					writer.println("<TR><TD id=search_td>");
					writer.println(	"\"<SPAN STYLE='background:yellow'>" + keyword + "</SPAN>\"" +
									" is part of <A HREF='DescTab?table_name=" + tabName + 
									"'>" + tabName + "</A> table's <SPAN STYLE='color:brown'>" + 
									colName + "</SPAN> column data.<BR>" +
									"<SPAN STYLE='text-transform:upperCase'>" +
									"ACTUAL DATA :: </SPAN>[ <SPAN STYLE='background:ivory'>" + 
									data + "</SPAN> ]");
					writer.println("</TD></TR>");
				}

				writer.println("</TABLE><BR>");
			}
			else {
				String message = " NO MATCH FOUND WITH \"" + keyword + "\"";
				if(error_occured)	message = error_message;

				writer.println("<HR WIDTH=100%>");
				writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
				writer.println("<TR>");
				writer.println("<TH id=search_norm_msg>" + message + "</TH>");
				writer.println("</TR>");
				writer.println("</TABLE>");
				writer.println("<HR WIDTH=100%>");
			}

			writer.println("<FORM NAME=search_form METHOD=post ACTION='SearchDB'>");

			writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=2 BORDER=0 WIDTH=100% " +
							" STYLE='border-style:double;border-width:1px;border-color:black'>");
			writer.println("<TR><TH COLSPAN=3 id=common_th>SEARCH DATABASE ...</TH></TR>");
			writer.println(	"<TR><TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
							" KEYWORD </TH>");
			writer.println("<TD id=common_td COLSPAN=2>");
			writer.println(	"<INPUT TYPE=text NAME=keyword STYLE='background:azure;width:100%' " +
							" VALUE=\"" + keyword + "\">");
			writer.println("</TD></TR>");
			
			writer.println("<TR><TD COLSPAN=2 WIDTH=50%>");
			writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=3 BORDER=0 WIDTH=100% " +
							" STYLE='border-style:double;border-width:1px;border-color:black'>");
			writer.println(	"<TR><TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
							"LOOK IN TABLE(S)</TH></TR>");
			
			writer.println("<TR><TD STYLE='background:#c5d6df;text-align:center'>");
			writer.println("<SELECT NAME=table_names MULTIPLE STYLE='width:100%;height:136px'>");
			for(int i=0;i<tableNames.size();i++) {
				String name = tableNames.elementAt(i).toString();
				writer.println("<OPTION VALUE='" + name + "' SELECTED>" + name + "</OPTION>");
			}
			writer.println("</SELECT></TD></TR>");
			
			writer.println("</TABLE>");
			writer.println("</TD>");

			writer.println("<TD WIDTH=50%>");
			writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100% " +
							" STYLE='border-style:double;border-width:1px;border-color:black'>");
			writer.println(	"<TR><TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
							"SEARCH OPTIONS</TH></TR>");
			writer.println(	"<TR><TD id=common_td STYLE='font-weight:bold;text-indent:100px'>" +
							"<INPUT TYPE=checkbox NAME=match_case VALUE=1> Match Case </TD></TR>");
			writer.println(	"<TR><TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
							" LOOK FOR </TH></TR>");
			writer.println(	"<TR><TD id=common_td STYLE='font-weight:bold;text-indent:100px'>" +
							"<INPUT TYPE=radio NAME=search VALUE=0> Column Names Only </TD></TR>");
			writer.println(	"<TR><TD id=common_td STYLE='font-weight:bold;text-indent:100px'>" +
							"<INPUT TYPE=radio NAME=search VALUE=1 CHECKED> Data Only </TD></TR>");
			writer.println(	"<TR><TD id=common_td STYLE='font-weight:bold;text-indent:100px'>" +
							"<INPUT TYPE=radio NAME=search VALUE=2> Both </TD></TR>");
			writer.println("</TABLE></TD></TR>");

			writer.println("<TR><TD STYLE='background:#f5f5f5'></TD>");
			writer.println(	"<TD ALIGN=right STYLE='background:#f5f5f5'>" +
							" <IMG NAME=pic11 SRC='pics/reset1.jpg' " +
							" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,11)' " +
							" onMouseUp='putOff(this,11)' onMouseOut='putOff(this,11)' " +
							" onClick='document.search_form.reset()' STYLE='cursor:hand'></TD>");
			writer.println( "<TD ALIGN=left STYLE='background:#f5f5f5'>" +
							" <IMG NAME=pic24 SRC='pics/search11.jpg' " +
							" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,24)' " +
							" onMouseUp='putOff(this,24)' onMouseOut='putOff(this,24)' " +
							" onClick='submitSearchForm(document.search_form)' " +
							" STYLE='cursor:hand'></TD>");
			writer.println("</TR></TABLE></FORM>");

			writer.println("</BODY></HTML>");
		}
		writer.close();
	}
}