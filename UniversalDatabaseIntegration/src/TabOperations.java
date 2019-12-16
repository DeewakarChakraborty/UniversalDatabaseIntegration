import java.util.Vector;

import java.lang.String;
import java.lang.Exception;

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

public class TabOperations extends HttpServlet {
	
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

		String  error_message = null;
		boolean error_occured = false;

		String tableName= null;
		String cname	= null;
		String command	= null;
		String message	= null;

		String dbProductName    = null;
		String dbProductVersion = null;

		boolean renameSelected	= false;
		boolean alterSelected	= false;
		boolean dropSelected	= false;
		boolean emptySelected	= false;

		String primaryKeyColumn	= ",";

		String selectedColumnType = "";
		String selectedColumnSize = "";
		int    selectedColumnNull = -1;
		boolean selectedColumnPk  = false;

		Vector typeNames  = new Vector();

		Vector colNames = new Vector();
		Vector colTypes = new Vector();
		Vector colSizes	= new Vector();
		Vector Nullable = new Vector();

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
			message	= req.getParameter("message");
			command	= req.getParameter("command");
			cname	= req.getParameter("cname");
	
			if(command != null && command.equalsIgnoreCase("alter") && cname != null) {
				alterSelected = true;
			}
			if(command != null && command.equalsIgnoreCase("rename") && cname != null) {
				renameSelected = true;
			}
			if(command != null && command.equalsIgnoreCase("drop") && cname != null) {
				dropSelected = true;
			}
			if(command != null && command.equalsIgnoreCase("empty") && cname != null) {
				emptySelected = true;
			}

			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url,userid,pass);
				dbMetaData = connection.getMetaData();
				resultSet  = dbMetaData.getTypeInfo();
				while(resultSet.next())	typeNames.add(resultSet.getString(1));
				if(resultSet != null)	resultSet.close();

				if(schemaValue == 0)	resultSet = dbMetaData.getColumns(schema,null,tableName,null);
				else					resultSet = dbMetaData.getColumns(null,schema,tableName,null);
					
				while(resultSet.next()) {
					colNames.add(resultSet.getString(4));
					colTypes.add(resultSet.getString(6).toUpperCase());

					String size = resultSet.getString(7);
					int decimalDigits = resultSet.getInt(9);
					if(decimalDigits > 0)	size += "," + decimalDigits;
					colSizes.add(size);
	
					Nullable.add(resultSet.getString(11));
				}
				if(resultSet != null) resultSet.close();

				try {
					if(schemaValue == 0) resultSet = dbMetaData.getPrimaryKeys(schema,null,tableName);
					else				 resultSet = dbMetaData.getPrimaryKeys(null,schema,tableName);
				
					while(resultSet.next()) primaryKeyColumn += resultSet.getString(4) + ",";
				}
				catch(Exception e)	{	e.printStackTrace();	}
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
							"<img name=pic8 src='pics/operations2.jpg' border=0 " +
							"width=80 height=26 align=absbottom>" +
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

			if(message != null)	{
				writer.println("<HR WIDTH=100%>");
				writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
				writer.println("<TR>");
				writer.println(	"<TH id=insert_norm_msg>" + message + "</TH>");
				writer.println("</TR>");
				writer.println("</TABLE>");
				writer.println("<HR WIDTH=100%>");
			}

			if(!error_occured) {

				// START OF TEMPERARY STORAGE FORM

				writer.println("<FORM NAME=temp_store>");
			
				writer.println(	"<SELECT NAME=column_types STYLE='visibility:hidden;position:absolute;"+
								" width:0px;height:0px'>");
				for(int i=0;i<colTypes.size();i++) {
					String value = colTypes.elementAt(i).toString();
					writer.println("<OPTION VALUE=\"" + value + "\">"); 
				}				
				writer.println("</SELECT>");
				
				writer.println(	"<SELECT NAME=column_sizes STYLE='visibility:hidden;position:absolute;"+
								" width:0px;height:0px'>");
				for(int i=0;i<colSizes.size();i++) {
					String value = colSizes.elementAt(i).toString();
					writer.println("<OPTION VALUE=\"" + value + "\">");
				}
				writer.println("</SELECT>");			

				writer.println(	"<SELECT NAME=column_pk STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px'>");
				for(int i=0;i<colNames.size();i++) {
					String name = "," + colNames.elementAt(i).toString() + ",";
					if(primaryKeyColumn.indexOf(name) != -1)	writer.println("<OPTION VALUE=1>");
					else										writer.println("<OPTION VALUE=0>");
				}
				writer.println("</SELECT>");

				writer.println(	"<SELECT NAME=column_nn STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px'>");
				for(int i=0;i<Nullable.size();i++) {
					int nullable = Integer.parseInt(Nullable.elementAt(i).toString());
					if(nullable == 0)	writer.println("<OPTION VALUE=1>");
					else				writer.println("<OPTION VALUE=0>");
				}				
				writer.println("</SELECT>");

				writer.println("</FORM>");

				// END OF TEMPERARY STORAGE FORM

				// START OF ALTER COLUMN BLOCK
				
				writer.println("<A NAME='alter'>");
				writer.println("<FORM NAME=alter_form METHOD=post ACTION='AlterColumn'>");
				writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
				writer.println("<INPUT TYPE=hidden NAME=primary_key VALUE=" + primaryKeyColumn + ">");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH COLSPAN=5 id=common_th>ALTER COLUMN ...</TH></TR>");
				writer.println(	"<TR><TH WIDTH=20% id=common_hed " +
								" STYLE='font-weight:bold'>COLUMN NAME</TH>");
				writer.println("<TD WIDTH=30% STYLE='background:#c5d6df;text-align:center'>");
				writer.println(	"<SELECT NAME=column_name STYLE='background:azure;width:180px' " +
								"onChange='loadColData(document.alter_form,document.temp_store,this)'>");
				writer.println("<OPTION VALUE=0> --------- Select a Column --------- </OPTION>");
				for(int i=0;i<colNames.size();i++) {
					String name = colNames.elementAt(i).toString();
					writer.print("<OPTION VALUE=\"" + name + "\" "); 
					if(alterSelected && cname.equals(name))	{
						selectedColumnType = colTypes.elementAt(i).toString();
						selectedColumnSize = colSizes.elementAt(i).toString();
						selectedColumnNull = Integer.parseInt(Nullable.elementAt(i).toString());
						if(primaryKeyColumn.indexOf("," + name + ",") != -1) selectedColumnPk = true;
						writer.print(" SELECTED >");
					}
					else									writer.print(">");
					writer.println(name + "</OPTION>");
				}
				writer.println("</SELECT></TD>");
				writer.println("<TH WIDTH=20% id=common_hed STYLE='font-weight:bold'>COLUMN TYPE</TH>");
				writer.println("<TD WIDTH=30% COLSPAN=2 STYLE='background:#c5d6df;text-align:center'>");
				writer.println("<SELECT STYLE='background:azure;width:180px' NAME=column_type>");
				for(int i=0;i<typeNames.size();i++) {
					String type = typeNames.elementAt(i).toString();
					writer.print("<OPTION VALUE='" + type + "' ");
					if(type.equals(selectedColumnType))	writer.println("SELECTED>" + type + "</OPTION>");
					else								writer.println(">" + type + "</OPTION>");
				}
				writer.println("</SELECT>");
				writer.println("</TD></TR>");
				writer.println("<TR><TH id=common_hed STYLE='font-weight:bold'>COLUMN SIZE</TH>");
				writer.println("<TD STYLE='background:#c5d6df;text-align:center'>");
				writer.println(	"<INPUT TYPE=text STYLE='background:azure;width:180px' " +
								"VALUE='" + selectedColumnSize + "' NAME=column_size>");
				writer.println("</TD>");

				writer.println("<TD id=common_data STYLE='text-align:center;font-weight:bold'>");
				writer.print("<INPUT TYPE=checkbox NAME=primary VALUE=1 ");
				if(selectedColumnPk)	writer.println(" CHECKED> Primary </TD>");
				else					writer.println("> Primary </TD>");
				
				writer.println("<TD id=common_data STYLE='text-align:center;font-weight:bold'>");
				writer.println("<INPUT TYPE=checkbox NAME=unique VALUE=1>  Unique </TD>");

				writer.println("<TD id=common_data STYLE='text-align:center;font-weight:bold'>");
				writer.print("<INPUT TYPE=checkbox NAME=notnull VALUE=1 ");
				if(selectedColumnNull == 0)	writer.println(" CHECKED> Not Null </TD>");
				else						writer.println("> Not Null </TD>");
				
				writer.println("</TR>");
				writer.println("<TR>");
				writer.println(	"<TD COLSPAN=2 ALIGN=right STYLE='background:#f5f5f5'>" +
								" <IMG NAME=pic11 SRC='pics/reset1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,11)' " +
								" onMouseUp='putOff(this,11)' onMouseOut='putOff(this,11)' " +
								" onClick='document.alter_form.reset()' STYLE='cursor:hand'></TD>");
				writer.println( "<TD COLSPAN=3 ALIGN=left STYLE='background:#f5f5f5'>" +
								" <IMG NAME=pic18 SRC='pics/apply1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,18)' " +
								" onMouseUp='putOff(this,18)' onMouseOut='putOff(this,18)' " +
								" onClick='submitAlterColumnForm(document.alter_form,document.temp_store)' " +
								" STYLE='cursor:hand'></TD>");
				writer.println("</TABLE></FORM><BR>");

				// END OF ALTER COLUMN BLOCK				
				
				// START OF RENAME COLUMN BLOCK
				
				writer.println("<A NAME=rename>");
				writer.println("<FORM NAME=rename_form METHOD=post ACTION='RenameColumn'>");
				writer.println("<INPUT TYPE=hidden name=table_name VALUE=" + tableName + ">");
				writer.println("<INPUT TYPE=hidden name=column_spec VALUE=''>");
				writer.println("<INPUT TYPE=hidden name=primary_key VALUE='" + primaryKeyColumn + "'>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println(	"<TR><TH COLSPAN=4 id=common_th>RENAME COLUMN ... " +
								"(Optional Feature)</TH></TR>");
				writer.println("<TR>");
				writer.println(	"<TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
								"OLD NAME</TH>");
				writer.println("<TD STYLE='background:#c5d6df;text-align:center'>");
				writer.println("<SELECT NAME=old_name STYLE='background:azure;width:200px'>");
				writer.println("<OPTION VALUE=0> --------- Select a Column --------- </OPTION>");
				for(int i=0;i<colNames.size();i++) {
					String name = colNames.elementAt(i).toString();
					writer.print("<OPTION VALUE=\"" + name + "\" "); 
					if(renameSelected && cname.equals(name)) writer.print(" SELECTED >");
					else									 writer.print(">");
					writer.println(name + "</OPTION>");
				}
				writer.println("</SELECT></TD>");
				writer.println(	"<TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
								"NEW NAME</TH>");
				writer.println("<TD STYLE='background:#c5d6df;text-align:center'>");
				writer.println("<INPUT TYPE=text NAME=new_name STYLE='background:azure;width:200px'>");
				writer.println("</TD></TR>");
				writer.println("<TR>");
				writer.println(	"<TD ALIGN=right COLSPAN=2 STYLE='background:#f5f5f5'>" +
								"<IMG NAME=pic11 SRC='pics/reset1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,11)' " +
								" onMouseUp='putOff(this,11)' onMouseOut='putOff(this,11)' " +
								" onClick='document.rename_form.reset()' STYLE='cursor:hand'></TD>");
				writer.println( "<TD ALIGN=left COLSPAN=2 STYLE='background:#f5f5f5'>" +
								"<IMG NAME=pic18 SRC='pics/apply1.jpg' STYLE='cursor:hand'" +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,18)' " +
								" onMouseUp='putOff(this,18)' onMouseOut='putOff(this,18)' " +
								" onClick='submitRenameColumnForm(document.rename_form,document.temp_store)' ></TD>");
				writer.println("</TR></TABLE></FORM><BR>");

				// END OF RENAME COLUMN BLOCK
				
				// START OF DROP COLUMN BLOCK
				
				writer.println("<A NAME=drop>");
				writer.println("<FORM NAME=drop_form METHOD=get ACTION='DropColumn'>");
				writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=2 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH COLSPAN=3 id=common_th>DROP COLUMN ...</TH></TR>");
				writer.println("<TR>");
				writer.println(	"<TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
								"COLUMN NAME</TH>");
				writer.println("<TD STYLE='background:#c5d6df;text-align:center'>");
				writer.println("<SELECT NAME=column_name STYLE='background:azure;width:340px'>");
				writer.println(	"<OPTION VALUE=0> --------------------------- " +
								"Select a Column ------------------------ </OPTION>");
				for(int i=0;i<colNames.size();i++) {
					String name = colNames.elementAt(i).toString();
					writer.print("<OPTION VALUE=\"" + name + "\" "); 
					if(dropSelected && cname.equals(name)) writer.print(" SELECTED >");
					else									 writer.print(">");
					writer.println(name + "</OPTION>");
				}
				writer.println("</SELECT></TD>");
				writer.println( "<TD ALIGN=center STYLE='background:#f5f5f5'>" +
								"<IMG NAME=pic19 SRC='pics/drop1.jpg' STYLE='cursor:hand'" +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,19)' " +
								" onMouseUp='putOff(this,19)' onMouseOut='putOff(this,19)' " +
								" onClick='submitDropColumnForm(document.drop_form)' ></TD>");
				writer.println("</TR></TABLE></FORM><BR>");

				// END OF DROP COLUMN BLOCK

				// START OF EMPTY COLUMN BLOCK

				writer.println("<A NAME=empty>");
				writer.println("<FORM NAME=empty_form METHOD=get ACTION='EmptyColumn'>");
				writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
				writer.println("<INPUT TYPE=hidden NAME=primary_key VALUE='" + primaryKeyColumn + "'>");
				writer.println("<INPUT TYPE=hidden NAME=column_spec VALUE=''>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=2 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH COLSPAN=3 id=common_th>EMPTY COLUMN ...</TH></TR>");
				writer.println("<TR>");
				writer.println(	"<TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
								"COLUMN NAME</TH>");
				writer.println("<TD STYLE='background:#c5d6df;text-align:center'>");
				writer.println("<SELECT NAME=column_name STYLE='background:azure;width:340px'>");
				writer.println(	"<OPTION VALUE=0> --------------------------- " +
								"Select a Column ------------------------ </OPTION>");
				for(int i=0;i<colNames.size();i++) {
					String name = colNames.elementAt(i).toString();
					writer.print("<OPTION VALUE=\"" + name + "\" "); 
					if(emptySelected && cname.equals(name)) writer.print(" SELECTED >");
					else									 writer.print(">");
					writer.println(name + "</OPTION>");
				}
				writer.println("</SELECT></TD>");
				writer.println( "<TD ALIGN=center STYLE='background:#f5f5f5'>" +
								"<IMG NAME=pic20 SRC='pics/empty1.jpg' STYLE='cursor:hand'" +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,20)' " +
								" onMouseUp='putOff(this,20)' onMouseOut='putOff(this,20)' " +
								" onClick='submitEmptyColumnForm(document.empty_form,document.temp_store)' ></TD>");
				writer.println("</TR></TABLE></FORM>");

				// END OF EMPTY COLUMN BLOCK

				writer.println("</BODY></HTML>");
			}
			else {
				writer.println("<H3>" + error_message + "</H3>");
				writer.println("</BODY></HTML>");
			}
		}
		writer.close();
	}
}