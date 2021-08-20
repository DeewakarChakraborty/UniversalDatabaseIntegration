import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.util.Vector;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EditNorm extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		Statement  statement  = null;
		ResultSet  resultSet  = null;
		DatabaseMetaData dbMetaData = null;
	
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

		String tableName = null;
		
		int columnCount	 = 0;
		int rowCount	 = 0;
		int updatedCount = 0;

		String [][] prevData	= null;
		String [][]	currData	= null;
		boolean[][] dataStatus	= null;

		Vector validIndex	= new Vector();
		Vector invalidIndex = new Vector();
		Vector errorIndex	= new Vector();

		Vector colNames   = new Vector();
		Vector colTypes   = new Vector();
		Vector typeNames  = new Vector();
		Vector colSizes   = new Vector();
		Vector decimals   = new Vector();
		Vector Nullable   = new Vector();

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
			rowCount		= Integer.parseInt(req.getParameter("row_count"));

			try {
				Class.forName(driver);
				connection	= DriverManager.getConnection(url,userid,pass);
				statement   = connection.createStatement();
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
						colTypes.add(new Integer(resultSet.getString(5)));
						typeNames.add(resultSet.getString(6));
						colSizes.add(new Integer(resultSet.getInt(7)));
						decimals.add(new Integer(resultSet.getInt(9)));
						Nullable.add(new Integer(resultSet.getInt(11)));
					}
				}
				catch(Exception e)	{
					error_occured = true;
					error_message = e.toString();
				}
			}

			if(!error_occured)	{
				columnCount = colNames.size();
					
				prevData	= new String [rowCount][columnCount];
				currData	= new String [rowCount][columnCount];
				dataStatus	= new boolean[rowCount][columnCount];

				for(int i=0;i<rowCount;i++) {
					prevData[i] = req.getParameterValues("row_" + i + "_prev_data");
					currData[i] = req.getParameterValues("row_" + i + "_curr_data");

					boolean thisRowOk = true;

					for(int x=0;x<columnCount;x++) {
						String value = currData[i][x].trim();
						boolean isOk = true;

						int type	= Integer.parseInt(colTypes.elementAt(x).toString());
						int nullable= Integer.parseInt(Nullable.elementAt(x).toString());

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
						if(!isOk)	thisRowOk = false;

						dataStatus[i][x] = isOk;
					}

					if(thisRowOk)	validIndex.add(new Integer(i));
					else			invalidIndex.add(new Integer(i));
				}

				for(int x=0;x<validIndex.size();x++)	{
					int i = Integer.parseInt(validIndex.elementAt(x).toString());
					String setClause	= "";
					String whereClause	= "";
					
					for(int j=0;j<columnCount;j++)	{
						String colname  = colNames.elementAt(j).toString();
						String currdata = currData[i][j].trim();
						String prevdata = prevData[i][j].trim();
						int type = Integer.parseInt(colTypes.elementAt(j).toString());

						switch(type) {
							case Types.BIT		:	
							case Types.SMALLINT :
							case Types.TINYINT	:	
							case Types.INTEGER	:	
							case Types.BIGINT	:	
							case Types.NUMERIC	:
							case Types.DECIMAL	:	
							case Types.REAL		:	
							case Types.DOUBLE	:
							case Types.FLOAT:	if(currdata.equalsIgnoreCase("null")) 
													 setClause	+= colname + "= null , ";
												else setClause	+= colname + "=" + currdata + " , ";
												if(prevdata.equalsIgnoreCase("null"))
													 whereClause += colname + " IS null and ";
												else whereClause += colname + "=" + prevdata + " and ";
												break;
							default	:	if(currdata.equalsIgnoreCase("null")) 
											 setClause	+= colname + "= null , ";
										else setClause	+= colname + "='" + currdata + "' , ";
										if(prevdata.equalsIgnoreCase("null"))
											 whereClause += colname + " IS null and ";
										else whereClause += colname + "='" + prevdata + "' and ";
						}
					}

					setClause	= setClause.substring(0,setClause.length()-2);
					whereClause = whereClause.substring(0,whereClause.length()-4);
					String query =	"update " + tableName + " set " + setClause + 
									" where " + whereClause;
					try {
						error_message = query + "###" + statement.executeUpdate(query);
						updatedCount ++;
					}
					catch(SQLException se)	{	errorIndex.add(new Integer(i));	}
				}

				try {
					if(resultSet != null) resultSet.close();
					connection.close();
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
			
			if(error_occured) writer.println("<H3>" + error_message + "</H3></BODY></HTML>");
			else {
				if(updatedCount > 0) {
					writer.println("<HR WIDTH=100%>");
					writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
					writer.println("<TR>");
					writer.println(	"<TH id=insert_norm_msg>" + updatedCount + 
									" row(s) Updated Successfully</TH>");
					writer.println("</TR>");
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%>");
				}

				if(invalidIndex.size() > 0 || errorIndex.size()>0)	{
					writer.println("<HR WIDTH=100%>");
					writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
					writer.println("<TR>");
					writer.println(	"<TH id=insert_err_msg>THE FOLLOWING RECORDS CONTAIN ERRORS. " +
									"CHECK THE VALUES AND TRY AGAIN.</TH>");
					writer.println("</TR>");
					writer.println("</TABLE>");
					writer.println("<HR WIDTH=100%>");

					int rowIndex = 0;
					writer.println("<FORM NAME=edit_form METHOD=post ACTION='EditNorm'>");
					writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
					writer.println("<DIV id=common_div ALIGN=center>");
					writer.println("<TABLE ALIGN=left CELLSPACING=1 CELLPADDING=1 BORDER=0>");

					writer.println("<TR>");
					for(int i=0;i<columnCount;i++)	{
						writer.println("<TH id=insert_th>" + colNames.elementAt(i) + "</TH>");
					}
					writer.println("</TR>");
				
					writer.println("<TR>");
					for(int i=0;i<columnCount;i++)	{
						writer.print("<TD id=insert_td>" + typeNames.elementAt(i) +
									 "[" + colSizes.elementAt(i));
						
						int decimal = Integer.parseInt(decimals.elementAt(i).toString());
						if(decimal > 0)	writer.println("," + decimal + "]</TD>");
						else			writer.println("]</TD>");
					}
					writer.println("</TR>");
										
					if(invalidIndex.size() > 0) {
						writer.println(	"<TR><TH id=insert_err_msg COLSPAN=" + columnCount + ">" +
										"THE FOLLOWING ROWS CONTAIN INVALID DATA" +
										"</TH></TR>");
						
						for(int x=0;x<invalidIndex.size();x++) {
							int i = Integer.parseInt(invalidIndex.elementAt(x).toString());
							writer.println("<TR>");
							for(int j=0;j<columnCount;j++)	{
								int maxlength = Integer.parseInt(colSizes.elementAt(j).toString()); 
								writer.println("<TD id=insert_td>");
								writer.println(	"<INPUT TYPE=hidden NAME='row_" + rowIndex + 
												"_prev_data' VALUE=\"" + prevData[i][j] + "\">");
								writer.print("<INPUT TYPE=text MAXLENGTH=" + maxlength + " NAME='row_" +
											 rowIndex + "_curr_data' VALUE=\"" + currData[i][j] + "\"");
								if(dataStatus[i][j])	writer.println(" id=insert_norm_inp>");
								else					writer.println(" id=insert_error_inp>");
								writer.println("</TD>");
							}
							rowIndex++;
							writer.println("</TR>");
						}					
					}

					if(errorIndex.size() > 0) {
						writer.println("<TR><TH id=insert_err_msg COLSPAN=" + columnCount + ">" +
									   "THE FOLLOWING ROWS CAUSED ERRORS WHILE UPDATING THE TABLE"+
									   "</TH></TR>");
						
						for(int x=0;x<errorIndex.size();x++) {
							int i = Integer.parseInt(errorIndex.elementAt(x).toString());
							writer.println("<TR>");
							for(int j=0;j<columnCount;j++)	{
								int maxlength = Integer.parseInt(colSizes.elementAt(j).toString()); 
								writer.println("<TD id=insert_td>");
								writer.println(	"<INPUT TYPE=hidden NAME='row_" + rowIndex + 
												"_prev_data' VALUE=\"" + prevData[i][j] + "\">");
								writer.print("<INPUT TYPE=text MAXLENGTH=" + maxlength + 
											 " NAME='row_" + rowIndex + "_curr_data' " + 
											 " id=insert_error_inp VALUE=\"" + currData[i][j] + "\">");
								writer.println("</TD>");
							}
							rowIndex++;
							writer.println("</TR>");
						}
					}
					writer.println("</TABLE></DIV><BR>");
					writer.println("<INPUT TYPE=hidden NAME=row_count VALUE=" + rowIndex + ">");
					writer.println("</FORM>");

					writer.println("<TABLE WIDTH=100% ALIGN=center CELLSPACING=0 CELLPADDING=0 BORDER=0>");
					writer.println("<TR>");
					writer.println(	"<TD WIDTH=20% ALIGN=right><IMG SRC='pics/go1.jpg' " +
									"NAME=go1 WIDTH=30 HEIGHT=30 BORDER=0 STYLE='cursor:hand' " +
									"onMouseOut='putOff(this,13)' onMouseUp='putOff(this,13)' " +
									"onMouseDown='putOn(this,13)' onClick='edit_form.submit()'>" +
									"</TD>");
					writer.println("<TD WIDTH=60%></TD>");
					writer.println(	"<TD WIDTH=20% ALIGN=left><IMG SRC='pics/go1.jpg' " +
									"NAME=go2 WIDTH=30 HEIGHT=30 BORDER=0 STYLE='cursor:hand' " +
									"onMouseOut='putOff(this,13)' onMouseUp='putOff(this,13)' " +
									"onMouseDown='putOn(this,13)' onClick='edit_form.submit()'>" +
									"</TD>");
					writer.println("</TR>");
					writer.println("</TABLE>");
				}
				writer.println("</BODY></HTML>");
			}
		}
		writer.close();
	}
}