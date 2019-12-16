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

public class DBOperations extends HttpServlet {
	
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

		String tname	= null;
		String command	= null;
		String message	= null;

		String dbProductName    = null;
		String dbProductVersion = null;

		String primaryKeyColumn = ",";

		boolean renameSelected		= false;
		boolean alterTableSelected	= false;

		Vector typeNames  = new Vector();
		Vector tableNames = new Vector();

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

			message	= req.getParameter("message");
			command	= req.getParameter("command");
			tname	= req.getParameter("tname");
	
			if(command != null && command.equalsIgnoreCase("alter") && tname != null) {
				alterTableSelected = true;
			}
			if(command != null && command.equalsIgnoreCase("rename") && tname != null) {
				renameSelected = true;
			}

			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url,userid,pass);
				dbMetaData = connection.getMetaData();
				resultSet  = dbMetaData.getTypeInfo();
				while(resultSet.next())	typeNames.add(resultSet.getString(1));
				if(resultSet != null)	resultSet.close();

				if(schemaValue == 0)	resultSet = dbMetaData.getTables(schema,null,null,types);
				else					resultSet = dbMetaData.getTables(null,schema,null,types);

				while(resultSet.next()) tableNames.add(resultSet.getString(3));
				if(resultSet != null)	resultSet.close();

				if(alterTableSelected) {
					if(schemaValue == 0)	resultSet = dbMetaData.getColumns(schema,null,tname,null);
					else					resultSet = dbMetaData.getColumns(null,schema,tname,null);
					
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
						if(schemaValue == 0) resultSet = dbMetaData.getPrimaryKeys(schema,null,tname);
						else				 resultSet = dbMetaData.getPrimaryKeys(null,schema,tname);
					
						while(resultSet.next()) primaryKeyColumn += resultSet.getString(4) + ",";
					}
					catch(Exception e)	{	e.printStackTrace();	}
				}
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
							"<img name=pic8 src='pics/operations2.jpg' border=0 " +
							"width=80 height=26 align=absbottom>" +
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

				// START OF CREATE TABLE BLOCK
				
				writer.println("<A NAME='create'>");
				writer.println("<FORM NAME=create_form METHOD=post ACTION='CreateTab'>");
				writer.println(	"<SELECT NAME=column_types STYLE='visibility:hidden;position:absolute;"+
								" width:0px;height:0px' MULTIPLE></SELECT>");
				writer.println(	"<SELECT NAME=column_sizes STYLE='visibility:hidden;position:absolute;"+
								" width:0px;height:0px' MULTIPLE></SELECT>");			
				writer.println(	"<SELECT NAME=column_pk STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px' MULTIPLE></SELECT>");
				writer.println(	"<SELECT NAME=column_un STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px' MULTIPLE></SELECT>");
				writer.println(	"<SELECT NAME=column_nn STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px' MULTIPLE></SELECT>");
				
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=2 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH COLSPAN=2 id=common_th>CREATE TABLE ...</TH></TR>");
				
				writer.println("<TR><TD WIDTH=50%>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=3 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH id=common_hed STYLE='font-weight:bold'>TABLE NAME</TH>");
				writer.println("<TD STYLE='background:#c5d6df;text-align:center'>");
				writer.println("<INPUT TYPE=text NAME=table_name STYLE='background:azure;width:180px'>");
				writer.println("</TD></TR>");
				writer.println("<TR><TD COLSPAN=2 STYLE='background:#c5d6df;text-align:center'>");
				writer.println(	"<SELECT NAME=column_names MULTIPLE STYLE='width:100%;height:150px' " +
								" onChange='loadValues(document.create_form,this)'>");
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
								"<IMG NAME=pic15 SRC='pics/change1.jpg' BORDER=0 " +
								" WIDTH=70 HEIGHT=24 STYLE='cursor:hand' onMouseDown='putOn(this,15)'" +
								" onMouseUp='putOff(this,15)' onMouseOut='putOff(this,15)' " +
								" onClick='changeField(document.create_form)' ></TD>");
				writer.println(	"<TD WIDTH=33% STYLE='background:#f5f5f5;text-align:center'>" +
								"<IMG NAME=pic16 SRC='pics/remove1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,16)' " +
								" onMouseUp='putOff(this,16)' onMouseOut='putOff(this,16)' " +
								" onClick='removeField(document.create_form)' STYLE='cursor:hand'></TD>");
				writer.println("</TR>");
				writer.println("</TABLE>");
				writer.println("</TD></TR>");

				writer.println("<TR>");
				writer.println(	"<TD ALIGN=right STYLE='background:#f5f5f5'>" +
								" <IMG NAME=pic11 SRC='pics/reset1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,11)' " +
								" onMouseUp='putOff(this,11)' onMouseOut='putOff(this,11)' " +
								" onClick='document.create_form.reset()' STYLE='cursor:hand'></TD>");
				writer.println( "<TD ALIGN=left STYLE='background:#f5f5f5'>" +
								" <IMG NAME=pic17 SRC='pics/create1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,17)' " +
								" onMouseUp='putOff(this,17)' onMouseOut='putOff(this,17)' " +
								" onClick='submitForm(document.create_form)' STYLE='cursor:hand'></TD>");
				writer.println("</TABLE></FORM>");

				// END OF CREATE TABLE BLOCK				

				writer.println("<FORM NAME='load_form' method=get ACTION='DBOperations#alter'>");
				writer.println("<INPUT TYPE=hidden NAME=command VALUE=alter>");
				writer.println("<INPUT TYPE=hidden NAME=tname VALUE=''>");
				writer.println("</FORM>");


				// START OF ALTER TABLE BLOCK
				
				writer.println("<A NAME='alter'>");
				writer.println("<FORM NAME=alter_form METHOD=post ACTION='AlterTab'>");
				writer.println("<INPUT TYPE=hidden NAME=pk_column VALUE='" + primaryKeyColumn + "'>");

				writer.println(	"<SELECT NAME=column_types STYLE='visibility:hidden;position:absolute;"+
								" width:0px;height:0px' MULTIPLE>");
				if(alterTableSelected) {
					for(int i=0;i<colTypes.size();i++) {
						writer.println("<OPTION VALUE=\"" + colTypes.elementAt(i).toString() + "\">");
					}
				}
				writer.println("</SELECT>");
				
				writer.println(	"<SELECT NAME=column_sizes STYLE='visibility:hidden;position:absolute;"+
								" width:0px;height:0px' MULTIPLE>");
				if(alterTableSelected) {
					for(int i=0;i<colSizes.size();i++) {
						writer.println("<OPTION VALUE=\"" + colSizes.elementAt(i).toString() + "\">");
					}
				}
				writer.println("</SELECT>");			
				
				writer.println(	"<SELECT NAME=column_pk STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px' MULTIPLE>");
				if(alterTableSelected) {
					for(int i=0;i<colNames.size();i++) {
						String cname = "," + colNames.elementAt(i).toString() + ",";
						if(primaryKeyColumn.indexOf(cname) != -1)	writer.println("<OPTION VALUE=1>");
						else										writer.println("<OPTION VALUE=0>");
					}
				}
				writer.println("</SELECT>");
				
				writer.println(	"<SELECT NAME=column_un STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px' MULTIPLE>");
				if(alterTableSelected) {
					for(int i=0;i<colNames.size();i++)	writer.println("<OPTION VALUE=0>");
				}
				writer.println("</SELECT>");
				
				writer.println(	"<SELECT NAME=column_nn STYLE='visibility:hidden;position:absolute;" +
								" width:0px;height:0px' MULTIPLE>");
				if(alterTableSelected) {
					for(int i=0;i<Nullable.size();i++) {
						int nullable = Integer.parseInt(Nullable.elementAt(i).toString());
						if(nullable == 0)	writer.println("<OPTION VALUE=1>");
						else				writer.println("<OPTION VALUE=0>");
					}
				}
				writer.println("</SELECT>");

				writer.println(	"<SELECT NAME=prev_column_names MULTIPLE " +
								" STYLE='visibility:hidden;position:absolute;width:0px;height:0px'>");
				if(alterTableSelected) {
					for(int i=0;i<colNames.size();i++) {
						String name = colNames.elementAt(i).toString();
						writer.println("<OPTION VALUE=\"" + name + "\">");
					}
				}
				writer.println("</SELECT>");
				
				writer.println(	"<SELECT NAME=prev_column_index MULTIPLE " +
								" STYLE='visibility:hidden;position:absolute;width:0px;height:0px'>");
				if(alterTableSelected) {
					for(int i=0;i<colNames.size();i++) writer.println("<OPTION VALUE=" + i +">");
				}
				writer.println("</SELECT>");

				writer.println(	"<SELECT NAME=prev_column_action MULTIPLE " +
								" STYLE='visibility:hidden;position:absolute;width:0px;height:0px'>");
				if(alterTableSelected) {
					for(int i=0;i<colNames.size();i++) writer.println("<OPTION VALUE=0>");
				}
				writer.println("</SELECT>");

				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=2 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH COLSPAN=2 id=common_th>ALTER TABLE ...</TH></TR>");
				
				writer.println("<TR><TD WIDTH=50%>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=3 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH id=common_hed STYLE='font-weight:bold'>TABLE NAME</TH>");
				writer.println("<TD STYLE='background:#c5d6df;text-align:center'>");
				
				writer.println(	"<SELECT NAME=table_name STYLE='background:azure;width:180px' " +
								" onChange='callLoadForm(this)'>");
				writer.println("<OPTION VALUE=0> ------- Select a Table ------- </OPTION>");
				for(int i=0;i<tableNames.size();i++) {
					String name = tableNames.elementAt(i).toString();
					writer.print("<OPTION VALUE=\"" + name + "\" "); 
					if(alterTableSelected && tname.equals(name)) writer.print(" SELECTED >");
					else										 writer.print(">");
					writer.println(name + "</OPTION>");
				}
				writer.println("</SELECT>");

				writer.println("</TD></TR>");
				writer.println("<TR><TD COLSPAN=2 STYLE='background:#c5d6df;text-align:center'>");
				writer.println(	"<SELECT NAME=column_names MULTIPLE STYLE='width:100%;height:150px' " +
								" onChange='loadValues(document.alter_form,this)'>");
				if(alterTableSelected) {
					for(int i=0;i<colNames.size();i++) {
						String name = colNames.elementAt(i).toString();
						writer.println("<OPTION VALUE=\"" + name + "\">" + name + "</OPTION>");
					}
				}
				writer.println("</SELECT>");
				
				writer.println("</TD></TR>");
				writer.println("</TABLE>");
				writer.println("</TD>");

				writer.println("<TD WIDTH=50%>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println(	"<TR><TH id=common_hed STYLE='font-weight:bold;text-align:center' " +
								" COLSPAN=3>FIELD PROPERFIES</TH></TR>");
				writer.println("<TR><TH id=common_hed STYLE='font-weight:bold'>Field Name</TH>");
				writer.println("<TD COLSPAN=2 STYLE='background:#c5d6df;text-align:center' >");
				writer.println("<INPUT TYPE=text NAME=field_name STYLE='background:azure;width:180px'>");
				writer.println("</TD></TR>");
				writer.println("<TR><TH id=common_hed STYLE='font-weight:bold'>Field Type</TH>");
				writer.println("<TD COLSPAN=2 STYLE='background:#c5d6df;text-align:center' >");
				writer.println("<SELECT STYLE='background:azure;width:180px' NAME=field_type>");
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
								" onClick='addField(document.alter_form)' STYLE='cursor:hand'></TD>");
				writer.println(	"<TD WIDTH=33% STYLE='background:#f5f5f5;text-align:center'>" +
								"<IMG NAME=pic15 SRC='pics/change1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,15)' " +
								" onMouseUp='putOff(this,15)' onMouseOut='putOff(this,15)' " +
								" onClick='changeField(document.alter_form)' STYLE='cursor:hand'></TD>");
				writer.println(	"<TD WIDTH=33% STYLE='background:#f5f5f5;text-align:center'>" +
								"<IMG NAME=pic16 SRC='pics/remove1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,16)' " +
								" onMouseUp='putOff(this,16)' onMouseOut='putOff(this,16)' " +
								" onClick='removeField(document.alter_form)' STYLE='cursor:hand'></TD>");
				writer.println("</TR>");
				writer.println("</TABLE>");
				writer.println("</TD></TR>");

				writer.println("<TR>");
				writer.println(	"<TD ALIGN=right STYLE='background:#f5f5f5'>" +
								" <IMG NAME=pic11 SRC='pics/reset1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,11)' " +
								" onMouseUp='putOff(this,11)' onMouseOut='putOff(this,11)' " +
								" onClick='document.alter_form.field_name.readOnly=false;" +
								" document.alter_form.reset()' STYLE='cursor:hand'></TD>");
				writer.println( "<TD ALIGN=left STYLE='background:#f5f5f5'>" +
								" <IMG NAME=pic18 SRC='pics/apply1.jpg' " +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,18)' " +
								" onMouseUp='putOff(this,18)' onMouseOut='putOff(this,18)' " +
								" onClick='submitForm(document.alter_form)' STYLE='cursor:hand'></TD>");
				writer.println("</TABLE></FORM>");

				// END OF ALTER TABLE BLOCK
				
				// START OF RENAME TABLE BLOCK
				
				writer.println("<A NAME=rename>");
				writer.println("<FORM NAME=rename_form METHOD=post ACTION='RenameTab'>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=2 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH COLSPAN=4 id=common_th>RENAME TABLE ...</TH></TR>");
				writer.println("<TR>");
				writer.println(	"<TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
								"OLD NAME</TH>");
				writer.println("<TD STYLE='background:#c5d6df;text-align:center'>");
				writer.println("<SELECT NAME=old_name STYLE='background:azure;width:200px'>");
				writer.println("<OPTION VALUE=none> ---------- Select a Table ---------- </OPTION>");
				for(int i=0;i<tableNames.size();i++) {
					String name = tableNames.elementAt(i).toString();
					writer.print("<OPTION VALUE=\"" + name + "\" "); 
					if(renameSelected && tname.equals(name)) writer.print(" SELECTED >");
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
								" onClick='document.alter_form.field_name.readOnly=false;" +
								" document.rename_form.reset()' STYLE='cursor:hand'></TD>");
				writer.println( "<TD ALIGN=left COLSPAN=2 STYLE='background:#f5f5f5'>" +
								"<IMG NAME=pic18 SRC='pics/apply1.jpg' STYLE='cursor:hand'" +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,18)' " +
								" onMouseUp='putOff(this,18)' onMouseOut='putOff(this,18)' " +
								" onClick='submitRenameForm(document.rename_form)' ></TD>");
				writer.println("</TR></TABLE></FORM>");

				// END OF RENAME TABLE BLOCK
				
				// START OF DROP TABLE BLOCK
				
				writer.println("<A NAME=drop>");
				writer.println("<FORM NAME=drop_form METHOD=get ACTION='DropTab'>");
				writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=2 BORDER=0 WIDTH=100% " +
								" STYLE='border-style:double;border-width:1px;border-color:black'>");
				writer.println("<TR><TH COLSPAN=3 id=common_th>DROP TABLE ...</TH></TR>");
				writer.println("<TR>");
				writer.println(	"<TH id=common_hed STYLE='font-weight:bold;text-align:center'>" +
								"TABLE NAME</TH>");
				writer.println("<TD STYLE='background:#c5d6df;text-align:center'>");
				writer.println("<SELECT NAME=table_name STYLE='background:azure;width:340px'>");
				writer.println(	"<OPTION VALUE=0> ----------------------------- " +
								"Select a Table -------------------------- </OPTION>");
				for(int i=0;i<tableNames.size();i++) {
					String name = tableNames.elementAt(i).toString();
					writer.print("<OPTION VALUE=\"" + name + "\">" + name + "</OPTION>");
				}
				writer.println("</SELECT></TD>");
				writer.println( "<TD ALIGN=center STYLE='background:#f5f5f5'>" +
								"<IMG NAME=pic19 SRC='pics/drop1.jpg' STYLE='cursor:hand'" +
								" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,19)' " +
								" onMouseUp='putOff(this,19)' onMouseOut='putOff(this,19)' " +
								" onClick='submitDropForm(document.drop_form)' ></TD>");
				writer.println("</TR></TABLE></FORM>");

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