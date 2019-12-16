import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.util.List;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Iterator;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;

public class EditLobs extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		ResultSet  resultSet  = null;
		DatabaseMetaData dbMetaData = null;
		PreparedStatement statement = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm = null;
		String schema	  = null;
		int schemaValue	  = 0;

		String dbProductName    = null;
		String dbProductVersion = null;

		String  error_message = null;
		boolean error_occured = false;

		String tableName  = null;
		String sortColumn = null;

		String rowids = null;
		int    currId = 0;
		int	   rowid  = 0; 

		StringTokenizer rowIds = null;

		int sortOrder	= 0;
		int columnCount	= 0;
		int updatedCount= 0;

		Vector colNames   = new Vector();
		Vector colTypes   = new Vector();
		Vector typeName   = new Vector();
		Vector colSizes   = new Vector();
		Vector decimals   = new Vector();
		Vector Nullable	  = new Vector();
		Vector prevData	  = new Vector();
		Vector currData	  = new Vector();
		Vector dataStatus = new Vector();
		Vector cnames	  = new Vector();

		boolean rowOk = true;

		DiskFileUpload upload = null;			
        List items			  = null;

		HttpSession session	  = req.getSession(false);

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

			try {
				Class.forName(driver);
				connection	= DriverManager.getConnection(url,userid,pass);
				dbMetaData	= connection.getMetaData();

				upload = new DiskFileUpload();			
		        items  = upload.parseRequest(req);
			}
			catch(Exception e)	{
				error_occured = true;
				error_message = e.toString();
			}

			if(!error_occured)	{
				int index = 0;
				Iterator iter = items.iterator();

		        while (iter.hasNext()) {
			        FileItem item = (FileItem) iter.next();
					String fname = item.getFieldName();					
					
					if(item.isFormField())	{
						String value = item.getString().trim();
						if(fname.equals("row_ids"))			rowids = value;
						else if(fname.equals("sort_column"))sortColumn = value;
						else if(fname.equals("sort_order"))	sortOrder = Integer.parseInt(value);
						else if(fname.equals("curr_id"))	currId = Integer.parseInt(value);
						else if(fname.equals("table_name"))	{
							tableName = value;
							try {
								if(schemaValue == 0) 
									 resultSet = dbMetaData.getColumns(schema,null,tableName,null);
								else resultSet = dbMetaData.getColumns(null,schema,tableName,null);

								while(resultSet.next()) {
									colNames.add(resultSet.getString(4));
									colTypes.add(new Integer(resultSet.getString(5)));
									typeName.add(resultSet.getString(6));
									colSizes.add(new Long(resultSet.getLong(7)));
									decimals.add(new Integer(resultSet.getInt(9)));
									Nullable.add(new Integer(resultSet.getInt(11)));
								}
								if(resultSet != null) resultSet.close();
							}
							catch(Exception e)	{
								error_occured = true;
								error_message = e.toString();
							}
						}
						else {
							index		 = Integer.parseInt(fname);
							int type	 = Integer.parseInt(colTypes.elementAt(index).toString());
							int nullable = Integer.parseInt(Nullable.elementAt(index).toString());
							boolean isOk = true;

							switch(type) {
								case Types.BIT		:	isOk = Validate.isBit(value);		break;
								case Types.SMALLINT :
								case Types.TINYINT	:	isOk = Validate.isShort(value);		break;
								case Types.INTEGER	:	isOk = Validate.isInteger(value);	break;
								case Types.BIGINT	:	isOk = Validate.isLong(value);		break;
								case Types.NUMERIC	:
								case Types.DECIMAL	:	isOk = Validate.isBigDecimal(value);break;
								case Types.REAL		:	isOk = Validate.isFloat(value);		break;
								case Types.DOUBLE	:
								case Types.FLOAT	:	isOk = Validate.isDouble(value);	break;
								case Types.DATE		:
								case Types.TIME		:
								case Types.TIMESTAMP:	isOk = Validate.isDate(value);		break;
								case Types.CHAR		:
								case Types.VARCHAR	:	isOk = true;						break;
							}

							if((value.equals("") || value.equalsIgnoreCase("null")) && nullable != 0) {
								isOk  = true;
								value = "null";
							}
							if(!isOk)  {
								rowOk = false;
								dataStatus.add("false");
							}
							else dataStatus.add("true");

							cnames.add(colNames.elementAt(index));
							
							Vector data = new Vector();
							data.add(value);
							currData.add(data);
						}
					}
					else {
						index		= Integer.parseInt(fname);
						int type	= Integer.parseInt(colTypes.elementAt(index).toString());
						int nullable= Integer.parseInt(Nullable.elementAt(index).toString());
						boolean isOk= true;
						
						switch(type) {
							case Types.LONGVARBINARY:
							case Types.LONGVARCHAR	:
							case Types.VARBINARY	:
							case Types.BINARY		:
							case Types.BLOB			:
							case Types.CLOB			:	isOk = true;	break;
							default					:	isOk = false;
						}

						try {
							Vector lobData = new Vector();
							long size = item.getSize();
							if(size > 0) {
								lobData.add(item.getName());
								lobData.add(new Long(size));
								lobData.add(item.getInputStream());
								currData.add(lobData);	
								isOk = true;
							}
							else throw new IOException();
						}
						catch(IOException e) {	
							Vector data = new Vector();
							data.add("null");
							currData.add(data);
							if(nullable != 0) isOk = true;
							else			  isOk = false;
						}

						if(!isOk)  {
							rowOk = false;
							dataStatus.add("false");
						}
						else dataStatus.add("true");

						cnames.add(colNames.elementAt(index));
					}
				}
				
				if(rowOk) {
					try {
						String query = "select * from " + tableName;
						if(!sortColumn.equalsIgnoreCase("none")) {
							query += " order by " + sortColumn;
							if(sortOrder == 0)	query += " asc";
							else				query += " desc";
						}

						statement = connection.prepareStatement(query);
						resultSet = statement.executeQuery();

						int count = 0;
						while(true) {
							if(count == currId) break;
							resultSet.next();
							count ++;
						}
						
						columnCount	= colNames.size();
						for(int i=0;i<columnCount;i++) {
							int type = Integer.parseInt(colTypes.elementAt(i).toString());
							switch(type) {
								case Types.LONGVARBINARY:
								case Types.LONGVARCHAR	:
								case Types.VARBINARY	:
								case Types.BINARY		:
								case Types.BLOB			:
								case Types.CLOB			:	prevData.add("");	break;
								default	:	String data = null;
											try { data = resultSet.getString(i+1); }
											catch (Exception e) { prevData.add("$DATA_READ_ERROR$"); }
											prevData.add((data == null) ? "null" : data);
							}
						}

						if(resultSet != null) resultSet.close();
						if(statement != null) statement.close();
					}
					catch(Exception e) {}

					columnCount	= cnames.size();
					String setClause	= "";
					String whereClause	= "";
					Vector lobIndex = new Vector();

					for(int i=0;i<columnCount;i++)	{
						String cname    = cnames.elementAt(i).toString();
						String colname  = colNames.elementAt(i).toString();
						String currdata = ((Vector) currData.elementAt(i)).elementAt(0).toString();
						String prevdata = prevData.elementAt(i).toString();
						int type = Integer.parseInt(colTypes.elementAt(i).toString());

						switch(type) {
							case Types.BIT			:	
							case Types.SMALLINT		:
							case Types.TINYINT		:	
							case Types.INTEGER		:	
							case Types.BIGINT		:	
							case Types.NUMERIC		:
							case Types.DECIMAL		:	
							case Types.REAL			:	
							case Types.DOUBLE		:
							case Types.FLOAT:	if(currdata.equalsIgnoreCase("null")) 
													 setClause	+= cname + "= null , ";
												else setClause	+= cname + "=" + currdata + " , ";
												if(prevdata.equalsIgnoreCase("null"))
													 whereClause += colname + " IS null and ";
												else whereClause += colname + "=" + prevdata + " and ";
												break;
							
							case Types.DATE			:
							case Types.TIME			:
							case Types.TIMESTAMP	:	
							case Types.CHAR			:
							case Types.VARCHAR:	if(currdata.equalsIgnoreCase("null")) 
													 setClause	+= cname + "= null , ";
												else setClause	+= cname + "='" + currdata + "' , ";
												if(prevdata.equalsIgnoreCase("null"))
													 whereClause += colname + " IS null and ";
												else whereClause += colname + "='" + prevdata + "' and ";
												break;

							case Types.BLOB			:
							case Types.CLOB			:
							case Types.BINARY		:
							case Types.VARBINARY	:
							case Types.LONGVARCHAR	:
							case Types.LONGVARBINARY:	if(!currdata.equalsIgnoreCase("null")) {
															setClause += cname + "=? , ";
															lobIndex.add(new Integer(i));
														}
						}
					}

					setClause	= setClause.substring(0,setClause.length()-2);
					whereClause = whereClause.substring(0,whereClause.length()-4);
					String query =	"update " + tableName + " set " + setClause + 
									" where " + whereClause;
					try {
						statement = connection.prepareStatement(query);
						for(int i=0;i<lobIndex.size();i++)	{
							int lobindex   = Integer.parseInt(lobIndex.elementAt(i).toString());
							Vector lobData = (Vector) currData.elementAt(lobindex);
							int size	   = Integer.parseInt(lobData.elementAt(1).toString());
							InputStream is = (InputStream) lobData.elementAt(2);
							statement.setBinaryStream(i+1,is,size);
						}
						updatedCount = statement.executeUpdate();
					}
					catch(SQLException se)	{	updatedCount = -1;	}
				}
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

				if(updatedCount == 1) {
					writer.println("<HR WIDTH=100%>");
					writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
					writer.println("<TR>");
					writer.println("<TH id=insert_norm_msg>1 Row Updated Successfully</TH>");
					writer.println("</TR>");
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%>");
				}
				else if(updatedCount == -1) {
					writer.println("<HR WIDTH=100%>");
					writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
					writer.println("<TR>");
					writer.println("<TH id=insert_err_msg>ERROR OCCURED DURING QUERY EXECUTION</TH>");
					writer.println("</TR>");
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%>");
				}
				else if(updatedCount == 0) {
					writer.println("<HR WIDTH=100%>");
					writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
					writer.println("<TR>");
					writer.println("<TH id=insert_err_msg>INVALID DATA FOUND IN FORM FIELDS</TH>");
					writer.println("</TR>");
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%>");
				}

				if(updatedCount != 1) {
					writer.println(	"<FORM NAME=edit_form ACTION='EditLobs' " +
									" METHOD=post ENCTYPE='multipart/form-data'>");
					writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
					writer.println("<INPUT TYPE=hidden NAME=sort_column VALUE=" + sortColumn + ">");
					writer.println("<INPUT TYPE=hidden NAME=sort_order VALUE=" + sortOrder + ">");
					writer.println("<INPUT TYPE=hidden NAME=row_ids VALUE=\"" + rowids + "\">");
					writer.println("<INPUT TYPE=hidden NAME=curr_id VALUE=" + currId + ">");

					writer.println(	"<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=1 BORDER=0 " +
									"style='border-style:double;border-width:1px;border-color:black'>");
					writer.println("<TR>");
					writer.println("<TH id=insert_th WIDTH=25%>COLUMN NAME</TH>");
					writer.println("<TH id=insert_th WIDTH=25%>DATA TYPE</TH>");
					writer.println("<TH id=insert_th WIDTH=50% COLSPAN=2>VALUE</TH>");
					writer.println("</TR>");
					
					columnCount = colNames.size();
					for(int i=0;i<columnCount;i++)	{
						String colName	= colNames.elementAt(i).toString();
						String typename = typeName.elementAt(i).toString();
						long maxlength  = Long.parseLong(colSizes.elementAt(i).toString());
						int	 colType	= Integer.parseInt(colTypes.elementAt(i).toString());
						int  decimal	= Integer.parseInt(decimals.elementAt(i).toString());					
	
						writer.println("<TR>");
						writer.println(	"<TD id=insert_td STYLE='font-weight:bold'>" + 
										colName + "</TD>");
						writer.print(	"<TD id=insert_td>" + typename + "[" + maxlength);
						
						if(decimal > 0)	writer.println("," + decimal + "]</TD>");
						else			writer.println("]</TD>");
				
						String inputType = "text";
						String data = ((Vector) currData.elementAt(i)).elementAt(0).toString();
						switch(colType) {
							case Types.LONGVARBINARY:
							case Types.LONGVARCHAR	:
							case Types.VARBINARY	:
							case Types.BINARY		:
							case Types.BLOB			:
							case Types.CLOB	:	inputType = "file";	break;
							default			:	inputType = "text";
						}
					
						String style = null;

						if(rowOk) style = "insert_norm_inp";
						else {
							String status = dataStatus.elementAt(i).toString();
							if(status.equals("true"))	style = "insert_norm_inp";
							else						style = "insert_error_inp";
						}

						if(columnCount == i+1) {
							writer.println("<TD WIDTH=45% id=insert_td>"); 
							writer.println(	"<INPUT TYPE=" + inputType + " id=" + style +
											" STYLE='WIDTH=100%' VALUE='" + data + 
											"' NAME=" + i + " MAXLENGTH=" + maxlength + "></TD>");
							writer.println("<TD WIDTH=5%><IMG SRC='pics/go1.jpg' " +
										   "NAME=go1 WIDTH=30 HEIGHT=30 BORDER=0 STYLE='cursor:hand' " +
										   "onMouseOut='putOff(this,13)' onMouseUp='putOff(this,13)' " +
										   "onMouseDown='putOn(this,13)' " +
										   "onClick='edit_form.submit()'></TD>");
						}
						else {
							writer.println(	"<TD COLSPAN=2 id=insert_td><INPUT TYPE=" + inputType + 
											" id=" + style + " STYLE='WIDTH=100%' VALUE='" + data + 
											"' NAME=" + i + " MAXLENGTH=" + maxlength + "></TD>");
						}
						writer.println("</TR>");						
					}				
					writer.println("</TABLE><BR>");
					writer.println("</FORM>");
				}	
				else rowids = rowids.replaceFirst(currId + "," , "");
				
				rowIds = new StringTokenizer(rowids,",");
				if(rowIds.hasMoreTokens()) {
					try {
						String query = "select * from " + tableName;
						if(!sortColumn.equalsIgnoreCase("none")) {
							query += " order by " + sortColumn;
							if(sortOrder == 0)	query += " asc";
							else				query += " desc";
						}
						statement = connection.prepareStatement(query);
						resultSet = statement.executeQuery();
					}
					catch(Exception e) {
						error_occured = true;
						error_message = e.toString();
					}
				}
				
				if(error_occured) writer.println("<H3>" + error_message + "</H3></BODY></HTML>");
				else {
					while(rowIds.hasMoreTokens()) {
						try {
							int count = rowid;
							rowid = Integer.parseInt(rowIds.nextToken());
							while(true) {
								if(count == rowid) break;
								resultSet.next();
								count ++;
							}	
							if(rowid == currId) continue;
						}	catch(Exception e)	{ }
	
						writer.println(	"<FORM METHOD=post ENCTYPE='multipart/form-data' " +
										" NAME='edit_form" + rowid + "' ACTION='EditLobs'>");
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

						columnCount = colNames.size();
						for(int i=0;i<columnCount;i++)	{
							String colName	= colNames.elementAt(i).toString();
							String typename = typeName.elementAt(i).toString();
							long maxlength  = Long.parseLong(colSizes.elementAt(i).toString());
							int	 colType	= Integer.parseInt(colTypes.elementAt(i).toString());
							int  decimal	= Integer.parseInt(decimals.elementAt(i).toString());					
	
							writer.println("<TR>");
							writer.println(	"<TD id=insert_td STYLE='font-weight:bold'>" + 
											colName + "</TD>");
							writer.print(	"<TD id=insert_td>" + typename + "[" + maxlength);
							
							if(decimal > 0)	writer.println("," + decimal + "]</TD>");
							else			writer.println("]</TD>");
					
							String inputType = "text";
							String data		 = "";

							switch(colType) {
								case Types.LONGVARBINARY:
								case Types.LONGVARCHAR	:
								case Types.VARBINARY	:
								case Types.BINARY		:
								case Types.BLOB			:
								case Types.CLOB	:	inputType = "file";	break;
								default			:	inputType = "text";	
													try { data = resultSet.getString(i+1); }
													catch (Exception e) { data = "DATA_READ_ERROR"; }
							}
								
							if(columnCount == i+1) {
								writer.println("<TD WIDTH=45% id=insert_td>"); 
								writer.println(	"<INPUT TYPE=" + inputType + " id=insert_norm_inp " +
												" STYLE='WIDTH=100%' VALUE='" + data + 
												"' NAME=" + i + " MAXLENGTH=" + maxlength + "></TD>");
								writer.println("<TD WIDTH=5%><IMG SRC='pics/go1.jpg' " +
											"NAME=go1 WIDTH=30 HEIGHT=30 BORDER=0 STYLE='cursor:hand' " +
											"onMouseOut='putOff(this,13)' onMouseUp='putOff(this,13)' " +
											"onMouseDown='putOn(this,13)' " +
											"onClick='edit_form" + rowid +".submit()'></TD>");
							}
							else {
								writer.println(	"<TD COLSPAN=2 id=insert_td><INPUT TYPE=" + inputType + 
												" id=insert_norm_inp STYLE='WIDTH=100%' VALUE='" + data + 
												"' NAME=" + i + " MAXLENGTH=" + maxlength + "></TD>");
							}
							writer.println("</TR>");
						}
						writer.println("</TABLE><BR>");
						writer.println("</FORM>");					
					}
				}
				writer.println("</BODY></HTML>");
			}
		}
		writer.close();
	}
}