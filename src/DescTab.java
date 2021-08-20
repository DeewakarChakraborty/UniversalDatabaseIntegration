import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.util.Vector;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DescTab extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		DatabaseMetaData dbMetaData = null;
		ResultSet resultSet = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm	= null;
		String schema		= null;
		int	   schemaValue	= 0;

		String primaryKeyColumn = ",";
		String foreignKeyColumn = ",";

		String dbProductName    = null;
		String dbProductVersion = null;

		Vector colNames = new Vector();
		Vector colTypes = new Vector();
		Vector colSizes	= new Vector();
		Vector Nullable = new Vector();

		String  error_message = null;
		boolean error_occured = false;

		String tableName = null;
		String message = null;

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
	
			tableName	= req.getParameter("table_name");
			message		= req.getParameter("message");

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
					if(schemaValue == 0)	resultSet = dbMetaData.getColumns(schema,null,tableName,null);
					else					resultSet = dbMetaData.getColumns(null,schema,tableName,null);

					while(resultSet.next()) {
						
						colNames.add(resultSet.getString(4));
						colTypes.add(resultSet.getString(6));

						String size = resultSet.getString(7);
						int decimalDigits = resultSet.getInt(9);
						if(decimalDigits > 0)	size += " , " + decimalDigits;
						colSizes.add(size);

						int nullable = resultSet.getInt(11);
						if(nullable == 0)	Nullable.add("NO");
						else				Nullable.add("YES");
					}
					if(resultSet != null) resultSet.close();
				}
				catch(Exception e) {
					error_occured = true;
					error_message = e.toString();
				}

				if(!error_occured) {
					try {
						if(schemaValue == 0) resultSet = dbMetaData.getPrimaryKeys(schema,null,tableName);
						else				 resultSet = dbMetaData.getPrimaryKeys(null,schema,tableName);
					
						while(resultSet.next()) primaryKeyColumn += resultSet.getString(4) + ",";
						if(resultSet != null) resultSet.close();
					}
					catch(Exception e)	{	e.printStackTrace();	}

					try {
						if(schemaValue == 0) {
							resultSet = dbMetaData.getCrossReference(schema,null,null,schema,null,tableName);
						}
						else {
							resultSet = dbMetaData.getCrossReference(null,schema,null,null,schema,tableName);
						}

						while(resultSet.next()) foreignKeyColumn += resultSet.getString(8) + ",";
					}
					catch(Exception e)	{	e.printStackTrace();	}
				}

				try {
					if(resultSet != null)	resultSet.close();
					connection.close();
				}	
				catch(Exception e) { e.printStackTrace(); }
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
							"<img name=pic1 src='pics/structure2.jpg' border=0 " +
							"width=80 height=26 align=absbottom>" +
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

			if(error_occured) {
				writer.println("<H3>" + error_message + "</H3></HEAD></HTML>");
			}
			else {
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
				writer.println("<TR><TH id=common_th>Column Name</TH>");
				writer.println("<TH id=common_th>Type</TH>");
				writer.println("<TH id=common_th>Size</TH>");
				writer.println("<TH id=common_th>Nullable</TH>");
				writer.println("<TH id=common_th COLSPAN=4>Action</TH></TR>"); 
				for(int i=0;i<colNames.size();i++) {
					writer.print("<TR><TD id=common_td>");
					String cname = colNames.elementAt(i).toString();
					if(primaryKeyColumn.indexOf("," + cname + ",") != -1) {
						writer.print("<IMG SRC='pics/p_key.png' WIDTH=7 HEIGHT=11>&nbsp");
					}
					if(foreignKeyColumn.indexOf("," + cname + ",") != -1) {
						writer.print("<IMG SRC='pics/f_key.png' WIDTH=7 HEIGHT=11>&nbsp");
					}
					writer.println(cname + "</TD>");
					writer.println(	"<TD id=common_td style='text-align:center'>" + 
									colTypes.elementAt(i) + "</TD>");
					writer.println(	"<TD id=common_td style='text-align:center'>" + 
									colSizes.elementAt(i) + "</TD>");
					writer.println(	"<TD id=common_td style='text-align:center'>" + 
									Nullable.elementAt(i) + "</TD>");
					writer.println(	"<TD id=common_img>" +
									"<A HREF='TabOperations?table_name=" + 
									tableName + "&cname=" + cname + "&command=alter#alter'>" +
									"<IMG WIDTH=12 HEIGHT=13 BORDER=0 ALT=alter " +
									"SRC='pics/button_edit.png'></A></TD>");
					writer.println(	"<TD id=common_img>" +
									"<A HREF='TabOperations?table_name=" + 
									tableName + "&cname=" + cname + "&command=rename#rename'>" +
									"<IMG WIDTH=11 HEIGHT=13 BORDER=0 ALT=rename " +
									"SRC='pics/button_rename.png'></A></TD>");
					writer.println( "<TD id=common_img>" +
									"<A HREF='TabOperations?table_name=" + 
									tableName + "&cname=" + cname + "&command=drop#drop'>" +
									"<IMG WIDTH=11 HEIGHT=13 BORDER=0 ALT=drop " +
									"SRC='pics/button_drop.png'></A></TD>");
					writer.println(	"<TD id=common_img>" +
									"<A HREF='TabOperations?table_name=" + 
									tableName + "&cname=" + cname + "&command=empty#empty'>" +
									"<IMG WIDTH=11 HEIGHT=13 BORDER=0 ALT=empty " +
									" SRC='pics/button_empty.png'></TD>");
					writer.println("</TR>");
				}
				
				if(colNames.size() < 1) {
					writer.println("<TR><TD id=common_td COLSPAN=8><CENTER>EMPTY</CENTER></TD></TR>");
				}
				writer.println("</TABLE></BODY></HTML>");
			}
		}
		writer.close();
	}
}