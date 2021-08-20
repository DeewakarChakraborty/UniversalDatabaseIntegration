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

public class DescDB extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
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

		String types[] = { "TABLE" };

		String dbProductName    = null;
		String dbProductVersion = null;

		Vector tableNames  = new Vector();
		Vector tableTypes  = new Vector();
		Vector columnCount = new Vector();
		Vector recordCount = new Vector();

		String	message		  = null;
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
			message			= req.getParameter("message");
			
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
					if(schemaValue == 0)	resultSet = dbMetaData.getTables(schema,null,null,types);
					else					resultSet = dbMetaData.getTables(null,schema,null,types);

					while(resultSet.next()) {
						ResultSet rs;
						int colCount = 0;
						int rowCount = 0;
						
						String tname = resultSet.getString(3);
						String ttype = resultSet.getString(4);
						tableNames.add(tname);
						tableTypes.add(ttype);
																	
						if(schemaValue == 0)	rs = dbMetaData.getColumns(schema,null,tname,null);
						else					rs = dbMetaData.getColumns(null,schema,tname,null);

						while(rs.next()) colCount++;
						columnCount.add(new Integer(colCount));

						if(rs != null)	rs.close();

						rs = statement.executeQuery("select count(*) from " + tname );
						if(rs.next())	rowCount = rs.getInt(1);
						recordCount.add(new Integer(rowCount));

						if(rs != null)	rs.close();
					}
				}
				catch(Exception e) {
					error_occured = true;
					error_message = e.toString();
				}

				try {
					resultSet.close();
					connection.close();
				}	
				catch(Exception e) { e.printStackTrace(); }
			}
	
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
								"<img name='pic1' src='pics/structure2.jpg' " +
								" border=0 width=80 height=26 align=absbottom>" +
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
								"<img onMouseOver='putOn(this,8)' onMouseOut='putOff(this,8)' " +
								"name=pic8 src='pics/operations1.jpg' border=0 " +
								"width=80 height=26 align=absbottom></A>" +
								"<A HREF='SearchDB'>" + 
								"<img onMouseOver='putOn(this,9)' onMouseOut='putOff(this,9)' "+
								"name=pic9 src='pics/search1.jpg' border=0 " +
								"width=80 height=26 align=absbottom></A></TD></TR>" +
								"<TR><TD vAlign=top><IMG SRC='pics/bar.jpg' " +
								" ALIGN=absTop BORDER=0 WIDTH=590 HEIGHT=13>" +
								"</TD>" +
                                                                "<td><pre>      <a href='dbsearch.jsp'>DBSEARCH</a></pre></td></TR></TABLE><BR>");
				writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%>"); 
				writer.println("<TR><TH width=27% id=common_hed>Database Product Name</TH>"); 
				writer.println("<TD width=73% id=common_data>" + dbProductName + "</TD></TR>");
				writer.println("<TR><TH width=27% id=common_hed>Database Product Version</TH>"); 
				writer.println("<TD width=73% id=common_data>" + dbProductVersion + "</TD></TR>");
				writer.println("<TR><TH width=27% id=common_hed>Displayed " + schemaTerm + "</TH>");
				writer.println("<TD width=73% id=common_data>" + schema + "</TD></TR>");
				writer.println("</TABLE><BR>");

				if(message != null)	{
					writer.println("<HR WIDTH=100%>");
					writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
					writer.println("<TR>");
					writer.println(	"<TH id=insert_norm_msg>" + message + "</TH>");
					writer.println("</TR>");
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%><BR>");
				}

				writer.println("<TABLE WIDTH=100% ALIGN=center CELLSPACING=1 CELLPADDING=3 BORDER=0>");
				writer.println("<TR><TH id=common_th WIDTH=35%>Table Name</TH>");
				writer.println("<TH id=common_th WIDTH=10%>Type</TH>");
				writer.println("<TH id=common_th WIDTH=9%>Fields</TH>");
				writer.println("<TH id=common_th WIDTH=9%>Records</TH>");
				writer.println("<TH id=common_th COLSPAN=8 WIDTH=37%>Action</TH></TR>"); 

				for(int i=0;i<tableNames.size();i++) {
					String tname = tableNames.elementAt(i).toString();
					writer.println("<TR>");
					writer.println(	"<TD id=common_td><A HREF='DescTab?table_name=" + tname + 
									"'>" + tname + "</A></TD>");
					writer.println(	"<TD id=common_td style='text-align:center'>" + 
									tableTypes.elementAt(i).toString() + "</TD>");
					writer.println(	"<TD id=common_td style='text-align:right'>" + 
									columnCount.elementAt(i).toString() + "</TD>");
					writer.println(	"<TD id=common_td style='text-align:right'>" + 
									recordCount.elementAt(i).toString() + "</TD>");
					writer.println(	"<TD id=common_img>" +
									"<A HREF='BrowseForm?table_name=" + tname + "'>" +
									"<IMG WIDTH=12 HEIGHT=13 BORDER=0 ALT=browse " +
									"SRC='pics/button_browse.png'></A></TD>");
					writer.println(	"<TD id=common_img>" +
									"<A HREF='SearchTab?table_name=" + tname + "'>" +
									"<IMG WIDTH=14 HEIGHT=13 BORDER=0 ALT=search " +
									"SRC='pics/button_search.png'></A></TD>");
					writer.println(	"<TD id=common_img>" +
									"<A HREF='InsertForm?table_name=" + tname + "'>" +
									"<IMG WIDTH=18 HEIGHT=13 BORDER=0 ALT=insert " +
									"SRC='pics/button_insert.png'></A></TD>");
					writer.println(	"<TD id=common_img>" +
									"<A HREF='ExportTab?table_name=" + tname + "'>" +
									"<IMG WIDTH=13 HEIGHT=13 BORDER=0 ALT=export " +
									"SRC='pics/button_export.png'></A></TD>");
					writer.println(	"<TD id=common_img>" +
									"<A HREF='DBOperations?tname=" + 
									tname + "&command=alter#alter'>" +
									"<IMG WIDTH=12 HEIGHT=13 BORDER=0 ALT=alter " +
									"SRC='pics/button_edit.png'></A></TD>");
					writer.println(	"<TD id=common_img>" +
									"<A HREF='DBOperations?tname=" + 
									tname + "&command=rename#rename'>" +
									"<IMG WIDTH=11 HEIGHT=13 BORDER=0 ALT=rename " +
									"SRC='pics/button_rename.png'></A></TD>");
					writer.println( "<TD id=common_img><IMG WIDTH=11 HEIGHT=13 BORDER=0 ALT=drop " +
									" SRC='pics/button_drop.png' STYLE='cursor:hand' " +
									" onClick=callDropTab('" + tname + "')></TD>");
					writer.println(	"<TD id=common_img><IMG WIDTH=11 HEIGHT=13 BORDER=0 ALT=empty " +
									" SRC='pics/button_empty.png' STYLE='cursor:hand' " +
									" onClick=callEmptyTab('" + tname + "')></TD>");
					writer.println(	"</TR>");
				}
				
				if(tableNames.size() < 1) {
					writer.println("<TR><TD id=common_td COLSPAN=10><CENTER>EMPTY</CENTER></TD></TR>");
				}

				writer.println("</TABLE></BODY></HTML>");
			}
		}
		writer.close();
	}
}