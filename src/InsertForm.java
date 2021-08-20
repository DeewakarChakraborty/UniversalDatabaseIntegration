import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.util.Vector;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InsertForm extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		ResultSet  resultSet  = null;
		DatabaseMetaData dbMetaData = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm = null;
		String schema	  = null;
		int schemaValue	  = 0;

		final int rowCount = 12;

		String dbProductName    = null;
		String dbProductVersion = null;

		String  error_message = null;
		boolean error_occured = false;

		boolean lobExists = false;

		String tableName = null;

		Vector colNames = new Vector();
		Vector colTypes = new Vector();
		Vector typeName = new Vector();
		Vector colSizes = new Vector();
		Vector decimals = new Vector();

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
			schemaValue		= Integer.parseInt(session.getAttribute("schemaValue").toString());
			schema			= session.getAttribute("schema").toString();
			tableName		= req.getParameter("table_name");

			try {
				Class.forName(driver);
				connection	= DriverManager.getConnection(url,userid,pass);
				dbMetaData	= connection.getMetaData();
			}
			catch(Exception e) {
				error_occured = true;
				error_message = e.toString();
			}

			if(!error_occured) {
				try {
					if(schemaValue == 0) resultSet = dbMetaData.getColumns(schema,null,tableName,null);
					else				 resultSet = dbMetaData.getColumns(null,schema,tableName,null);

					while(resultSet.next()) {
						colNames.add(resultSet.getString(4));
						int type = resultSet.getInt(5);

						if(!lobExists)	{
							switch(type) {
								case Types.LONGVARBINARY:
								case Types.LONGVARCHAR	:
								case Types.VARBINARY	:
								case Types.BINARY		:
								case Types.BLOB			:
								case Types.CLOB			:	lobExists = true;
							}
						}
						
						colTypes.add(new Integer(type));
						typeName.add(resultSet.getString(6));
						colSizes.add(new Long(resultSet.getLong(7)));
						decimals.add(new Integer(resultSet.getInt(9)));
					}
				}
				catch(Exception e)	{
					error_occured = true;
					error_message = e.toString();
				}

				try {
					if(resultSet != null) resultSet.close();
					connection.close();
				}
				catch(Exception e)	{	e.printStackTrace();	}
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
								"<img name=pic6 src='pics/insert2.jpg' border=0 " +
								"width=80 height=26 align=absbottom>"+
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

				if(lobExists) {
					writer.println(	"<FORM NAME=insert_form ACTION='InsertLobs' " +
									" METHOD=post ENCTYPE='multipart/form-data'>");
					writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
				
					writer.println(	"<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=1 BORDER=0 " +
									"style='border-style:double;border-width:1px;border-color:black'>");
					writer.println("<TR>");
					writer.println("<TH id=insert_th WIDTH=25%>COLUMN NAME</TH>");
					writer.println("<TH id=insert_th WIDTH=25%>DATA TYPE</TH>");
					writer.println("<TH id=insert_th WIDTH=50%>VALUE</TH>");
					writer.println("</TR>");
					for(int i=0;i<colNames.size();i++)	{
						String colName	= colNames.elementAt(i).toString();
						String typename = typeName.elementAt(i).toString();
						long maxlength  = Long.parseLong(colSizes.elementAt(i).toString());
						int	 colType	= Integer.parseInt(colTypes.elementAt(i).toString());
						int  decimal	= Integer.parseInt(decimals.elementAt(i).toString());					
	
						writer.println("<TR>");
						writer.println(	"<TD id=insert_td STYLE='font-weight:bold'>" + 
										colName + "</TH>");
						writer.print(	"<TD id=insert_td >" + typename + "[" + maxlength);
						
						if(decimal > 0)	writer.println("," + decimal + "]</TD>");
						else			writer.println("]</TD>");
				
						String inputType = "text";
						switch(colType) {
							case Types.LONGVARBINARY:
							case Types.LONGVARCHAR	:
							case Types.VARBINARY	:
							case Types.BINARY		:
							case Types.BLOB			:
							case Types.CLOB			:	inputType = "file";	break;
							default					:	inputType = "text";	break;
						}
							
						writer.println(	"<TD id=insert_td><INPUT TYPE=" + inputType + 
										" id=insert_norm_inp VALUE='' STYLE='WIDTH=100%' " +
										"NAME=" + i + " MAXLENGTH=" + maxlength +	"></TD></TR>");
					}
					writer.println("</TABLE><BR>");
					writer.println("</FORM>");
				}
				else {
					writer.println("<FORM NAME=insert_form METHOD=post ACTION='InsertNorm'>");
					writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
					writer.println("<INPUT TYPE=hidden NAME=row_count  VALUE=" + rowCount  + ">");
				
					writer.println("<DIV id=common_div ALIGN=center>");
					writer.println("<TABLE ALIGN=left CELLSPACING=1 CELLPADDING=1 BORDER=0>");
					writer.println("<TR>");
					for(int i=0;i<colNames.size();i++)	{
						writer.println("<TH id=insert_th>" + colNames.elementAt(i) + "</TH>");
					}
					writer.println("</TR>");
				
					writer.println("<TR>");
					for(int i=0;i<colTypes.size();i++)	{
						writer.print("<TD id=insert_td>" + typeName.elementAt(i) +
									 "[" + colSizes.elementAt(i));
						
						int decimal = Integer.parseInt(decimals.elementAt(i).toString());
						if(decimal > 0)	writer.println("," + decimal + "]</TD>");
						else			writer.println("]</TD>");
					}
					writer.println("</TR>");
				
					for(int i=0;i<rowCount;i++)	{
						writer.println("<TR>");
						for(int j=0;j<colNames.size();j++)	{
							long maxlength = Long.parseLong(colSizes.elementAt(j).toString()); 
	
							writer.println(	"<TD id=insert_td>" +
											"<INPUT TYPE=text id=insert_norm_inp VALUE='' NAME='row" + 
											i + "data' MAXLENGTH=" + maxlength + "></TD>");
						}
					}
					writer.println("</TR></TABLE></DIV><BR>");
					writer.println("</FORM>");
				}
				
				writer.println("<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0>");
				writer.println("<TR>");
				writer.println(	"<TD WIDTH=20% ALIGN=left><IMG SRC='pics/go1.jpg' " +
								"NAME=go1 WIDTH=30 HEIGHT=30 BORDER=0 STYLE='cursor:hand' " +
								"onMouseOut='putOff(this,13)' onMouseUp='putOff(this,13)' " +
								"onMouseDown='putOn(this,13)' onClick='insert_form.submit()'>" +
								"</TD>");
				writer.println("<TD WIDTH=60%></TD>");
				writer.println(	"<TD WIDTH=20% ALIGN=right><IMG SRC='pics/go1.jpg' " +
								"NAME=go2 WIDTH=30 HEIGHT=30 BORDER=0 STYLE='cursor:hand' " +
								"onMouseOut='putOff(this,13)' onMouseUp='putOff(this,13)' " +
								"onMouseDown='putOn(this,13)' onClick='insert_form.submit()'>" +
								"</TD>");
				writer.println("</TR>");
				writer.println("</TABLE>");

				writer.println("</BODY></HTML>");
			}
		}
		writer.close();
	}
}