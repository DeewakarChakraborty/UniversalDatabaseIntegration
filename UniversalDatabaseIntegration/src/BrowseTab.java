import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.util.Vector;

import java.io.Reader;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BrowseTab extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {
		
		Connection connection = null;
		ResultSet   resultSet = null;
		Statement   statement = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String schemaTerm = null;
		String schema	  = null;

		String dbProductName    = null;
		String dbProductVersion = null;

		String  error_message = null;
		boolean error_occured = false;

		Vector largeColumnNames = null;
		Vector largeColumnTypes = null;
		
		String[] selectedColumns = null;
		String tableName	= null;
		String sortColumn	= null;

		int rowCount		= 0;
		int recordsPerPage  = 0;
		int startIndex		= 0;
		int displayMode		= 0;
		int headRepCount	= 0;
		int sortOrder		= 0;
		int largeColumnCount= 0;

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
			schema			= session.getAttribute("schema").toString();

			rowCount		= Integer.parseInt(req.getParameter("row_count"));

			try {	recordsPerPage = Integer.parseInt(req.getParameter("records_per_page"));	}
			catch (NumberFormatException e)	{	recordsPerPage = 11;	}

			try	{	startIndex = Integer.parseInt(req.getParameter("start_index"));	}
			catch (NumberFormatException e)	{	startIndex = 1;	}

			displayMode		= Integer.parseInt(req.getParameter("display_mode"));

			try {	headRepCount = Integer.parseInt(req.getParameter("head_rep_count"));	}
			catch (NumberFormatException e)	{	headRepCount = 11;	}

			sortOrder		= Integer.parseInt(req.getParameter("sort_order"));
			largeColumnCount= Integer.parseInt(req.getParameter("large_column_count"));
			
			tableName		= req.getParameter("table_name");
			sortColumn		= req.getParameter("sort_column");
			selectedColumns	= req.getParameterValues("selected_columns");

			if(largeColumnCount > 0)	{
				largeColumnNames = new Vector();
				largeColumnTypes = new Vector();

				for(int i=0;i<largeColumnCount;i++)	{
					largeColumnNames.add(req.getParameter("large_column_name" + i));
					largeColumnTypes.add(req.getParameter("large_column_type" + i));
				}
			}
			
			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url,userid,pass);
				statement  = connection.createStatement();
			}
			catch(Exception e) {
				error_occured = true;
				error_message = e.toString();
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
								"<img name=pic3 src='pics/browse2.jpg' border=0 " +
								"width=80 height=26 align=absbottom>" +
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
				writer.println("<TR><TH width=27% id=common_hed>Total number of Records" + "</TH>");
				writer.println("<TD width=73% id=common_data>" + rowCount + "</TD></TR>");
				writer.println("</TABLE><BR>");			

				String query = "select ";
				for(int i=0;i<selectedColumns.length;i++)	query += selectedColumns[i] + ",";
				query = query.substring(0,query.length()-1);
				query += " from " + tableName;
				if(!sortColumn.equalsIgnoreCase("NONE")) {
					query += " order by " + sortColumn;
					if(sortOrder == 0)	query += " asc";
					else				query += " desc";
				}

				try {
					resultSet = statement.executeQuery(query);

					writer.println("<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0>");
					writer.println("<TR>");
					writer.println("<TH id=browse_hed WIDTH=36%>Operations on Selected Records</TH>");
					writer.println(	"<TH id=browse_hed WIDTH=4%>" +
									"<IMG SRC='pics/button_edit.png' WIDTH=12 HEIGHT=13 " +
									" BORDER=0 ALT='Edit' STYLE='cursor:hand' " +
									" onClick='submitEditForm(action_form,action_form.row_ids)'></TH>");
					writer.println(	"<TH id=browse_hed WIDTH=4%>" +
									"<IMG SRC='pics/button_drop.png' WIDTH=12 HEIGHT=13 " +
									" BORDER=0 ALT='Delete' STYLE='cursor:hand' " +
									" onClick='submitBDropForm(action_form,action_form.row_ids)'></TH>");
					writer.println("<TH STYLE='background:white' WIDTH=1%></TH>");
					if(startIndex > 1) {
						writer.println("<TH id=browse_hed WIDTH=5%><IMG BORDER=0 WIDTH=12 HEIGHT=12 " +
									   "SRC='pics/prev.gif' onClick='prev_form.submit()' " +
									   " style='cursor:hand' ALT='Previous " + recordsPerPage +
									   " record(s)'></TH>");
						
						writer.println("<FORM NAME=prev_form METHOD=post ACTION='BrowseTab'>");
						writer.println("<INPUT TYPE=hidden NAME=row_count VALUE='" + rowCount + "'>");
						writer.println(	"<INPUT TYPE=hidden NAME=records_per_page VALUE='" + 
										recordsPerPage + "'>");
						
						if(startIndex > recordsPerPage)	{
							writer.println(	"<INPUT TYPE=hidden NAME=start_index VALUE='" + 
											(startIndex - recordsPerPage) + "'>");
						}
						else writer.println("<INPUT TYPE=hidden NAME=start_index VALUE='1'>");

						writer.println("<INPUT TYPE=hidden NAME=display_mode VALUE='"+displayMode+"'>");
						writer.println(	"<INPUT TYPE=hidden NAME=head_rep_count VALUE='" +
										headRepCount + "'>");
						writer.println("<INPUT TYPE=hidden NAME=sort_column VALUE='"+sortColumn + "'>");
						writer.println("<INPUT TYPE=hidden NAME=sort_order VALUE='" + sortOrder + "'>");
						writer.println("<INPUT TYPE=hidden NAME=table_name VALUE='" + tableName + "'>");
						writer.println(	"<INPUT TYPE=hidden NAME=large_column_count VALUE='" + 
										largeColumnCount + "'>");

						for(int i=0;i<selectedColumns.length;i++)	{
							writer.println(	"<INPUT TYPE=hidden NAME=selected_columns VALUE='" + 
											selectedColumns[i] + "'>");
						}
						if(largeColumnCount > 0) {
							for(int i=0;i<largeColumnCount;i++) {
								writer.println(	"<INPUT TYPE=hidden NAME='large_column_name" + i +
												"' VALUE='" + largeColumnNames.elementAt(i) + "'>");
								writer.println(	"<INPUT TYPE=hidden NAME='large_column_type" + i +
												"' VALUE='" + largeColumnTypes.elementAt(i) + "'>");
							}
						}
						writer.println("</FORM>");
					}
					else writer.println("<TH id=browse_hed WIDTH=5%></TH>");

					writer.print("<TH id=browse_hed WIDTH=45%>Displaying Records From " + startIndex + " - "); 
					if((startIndex + recordsPerPage -1) > rowCount)	writer.println(rowCount + "</TH>");
					else writer.println((startIndex + recordsPerPage - 1) + "</TH>");
					
					if((startIndex + recordsPerPage - 1) < rowCount) {
						writer.println(	"<TH id=browse_hed WIDTH=5%><IMG BORDER=0 WIDTH=12 HEIGHT=12 " +
										" SRC='pics/next.gif' style='cursor:hand' " +
										" onClick='next_form.submit()' ALT='Next " + recordsPerPage +
										" record(s)'></TH>");
						writer.println("<FORM NAME=next_form METHOD=post ACTION='BrowseTab'>");
						writer.println("<INPUT TYPE=hidden NAME=row_count VALUE='" + rowCount + "'>");
						writer.println(	"<INPUT TYPE=hidden NAME=records_per_page VALUE='" + 
										recordsPerPage + "'>");
						writer.println(	"<INPUT TYPE=hidden NAME=start_index VALUE='" + 
										(startIndex + recordsPerPage) + "'>");
						writer.println("<INPUT TYPE=hidden NAME=display_mode VALUE='"+displayMode+"'>");
						writer.println(	"<INPUT TYPE=hidden NAME=head_rep_count VALUE='" + 
										headRepCount + "'>");
						writer.println("<INPUT TYPE=hidden NAME=sort_column VALUE='"+sortColumn + "'>");
						writer.println("<INPUT TYPE=hidden NAME=sort_order VALUE='" + sortOrder + "'>");
						writer.println("<INPUT TYPE=hidden NAME=table_name VALUE='" + tableName + "'>");
						writer.println(	"<INPUT TYPE=hidden NAME=large_column_count VALUE='" +
										largeColumnCount + "'>");
						for(int i=0;i<selectedColumns.length;i++)	{
							writer.println(	"<INPUT TYPE=hidden NAME=selected_columns VALUE='" + 
											selectedColumns[i] + "'>");
						}
						if(largeColumnCount > 0) {
							for(int i=0;i<largeColumnCount;i++) {
								writer.println(	"<INPUT TYPE=hidden NAME='large_column_name" + i +
												"' VALUE='" + largeColumnNames.elementAt(i) + "'>");
								writer.println(	"<INPUT TYPE=hidden NAME='large_column_type" + i +
												"' VALUE='" + largeColumnTypes.elementAt(i) + "'>");
							}
						}
						writer.println("</FORM>");
					}					
					else writer.println("<TH id=browse_hed WIDTH=5%></TH>");
					writer.println("</TABLE><BR>");

					int retrievedCount  = 1;
					int headerCount		= 1;
					int recordCount		= 1;
					
					writer.println("<DIV id=common_div ALIGN=center>");
					writer.println("<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0>");
					writer.println("<FORM NAME=action_form METHOD=post>");
					writer.println("<INPUT TYPE=hidden NAME=table_name VALUE=" + tableName + ">");
					writer.println("<INPUT TYPE=hidden NAME=sort_column VALUE=" + sortColumn + ">");
					writer.println("<INPUT TYPE=hidden NAME=sort_order VALUE=" + sortOrder + ">");
					writer.println("<INPUT TYPE=hidden NAME=large_column_count VALUE=" + largeColumnCount + ">");
					while(resultSet.next()) {
						if(startIndex == recordCount) {
							if(displayMode == 0) {
								if(headerCount == 1) {
									writer.println("<TR>");
									writer.println("<TH id=browse_th><INPUT TYPE=checkbox NAME=chk_all"+
												   " onClick='setCheckBoxes(this,action_form.row_ids)'>" +
												   "</TH>");
									for(int i=0;i<selectedColumns.length;i++)	{
										writer.println("<TH id=browse_th>"+selectedColumns[i]+"</TH>");
									}
									writer.println("</TR>");
								}
								writer.println("<TR>");
								writer.println(	"<TD id=browse_td>" +
												"<INPUT TYPE=checkbox NAME=row_ids VALUE=" + 
												(startIndex + retrievedCount -1) + "></TD>");
								for(int i=0;i<selectedColumns.length;i++)	{
									if(largeColumnCount > 0) {
										int index = -1;
										for(int j=0;j<largeColumnCount;j++) {
											String largeColName = null;
											largeColName = largeColumnNames.elementAt(j).toString();
											if(selectedColumns[i].equalsIgnoreCase(largeColName)) {
												index = j;
												break;
											}
											else index = -1;
										}
										if(index != -1) {
											String type = largeColumnTypes.elementAt(index).toString();
											if(type.equalsIgnoreCase("NONE")) {
												writer.println(	"<TD id=browse_td>UNKNOWN DATA FORMAT " +
																"<A HREF='LoadLob?tname=" +
																tableName + "&cname=" + 
																selectedColumns[i] +
																"&ctype=application&index=" + 
																(startIndex + retrievedCount - 1) +
																"&scolumn=" + sortColumn + "&sorder=" +
																sortOrder + "'> " +
																"<IMG SRC='pics/load.gif' " +
																" BORDER=0 WIDTH=12 HEIGHT=12></A></TD>");
											}
											else if(type.equalsIgnoreCase("TEXT/PLAIN")) {
												writer.println("<TD id=browse_td>");
												try {
													Reader r = resultSet.getCharacterStream(i+1);
													int ch;
													while((ch=r.read()) != -1) writer.print((char)ch);
													r.close();
												}
												catch(Exception e) { writer.print("$DATA_READ_ERROR$"); }
												writer.println("</TD></TR>");											
											}
											else writer.println("<TD id=browse_td><IMG BORDER=0 " +
																"SRC='LoadLob?tname=" +
																tableName + "&cname=" + 
																selectedColumns[i] +
																"&ctype=" + type + "&index=" + 
																(startIndex + retrievedCount - 1) +
																"&scolumn=" + sortColumn + "&sorder=" +
																sortOrder + "'></TD>");
										}
										else writer.println("<TD id=browse_td>" + 
															resultSet.getString(i+1) + "</TD>");
									}
									else writer.println("<TD id=browse_td>" + 
														resultSet.getString(i+1)+"</TD>");
								}
								writer.println("</TR>");
								if(headerCount == headRepCount)	headerCount = 1;
								else							headerCount++;
							}
							else {
								writer.println("<TR>");
								writer.println(	"<TH id=browse_th COLSPAN=2>" +
												"<INPUT TYPE=checkbox NAME=row_ids VALUE=" + 
												(startIndex + retrievedCount -1) + "> " +
												" RECORD" + (startIndex + retrievedCount - 1) + 
												"</TH></TR>");
								for(int i=0;i<selectedColumns.length;i++)	{
									writer.println(	"<TR><TH id=browse_hed style='text-align:left'>" + 
													selectedColumns[i] + "</TH>");
									if(largeColumnCount > 0) {
										int index = -1;
										for(int j=0;j<largeColumnCount;j++) {
											String largeColName = null;
											largeColName = largeColumnNames.elementAt(j).toString();
											if(selectedColumns[i].equalsIgnoreCase(largeColName)) {
												index = j;
												break;
											}
											else index = -1;
										}
										if(index != -1) {
											String type = largeColumnTypes.elementAt(index).toString();
											if(type.equalsIgnoreCase("NONE")) {
												writer.println(	"<TD id=browse_td>UNKNOWN DATA FORMAT " +
																"<A HREF='LoadLob?tname=" +
																tableName + "&cname=" + 
																selectedColumns[i] +
																"&ctype=application&index=" + 
																(startIndex + retrievedCount - 1) +
																"&scolumn=" + sortColumn + "&sorder=" +
																sortOrder + "'> " +
																"<IMG SRC='pics/load.gif' " +
																" BORDER=0 WIDTH=12 HEIGHT=12></A>" +
																"</TD></TR>");
											}
											else if(type.equalsIgnoreCase("TEXT/PLAIN")) {
												writer.println("<TD id=browse_td>");
												try {
													Reader r = resultSet.getCharacterStream(i+1);
													int ch;
													while((ch=r.read()) != -1) writer.print((char)ch);
													r.close();
												}
												catch(Exception e) { writer.print("$DATA_READ_ERROR$"); }
												writer.println("</TD></TR>");
											}
											else writer.println("<TD id=common_td><IMG BORDER=0 " +
																"SRC='LoadLob?tname=" +
																tableName + "&cname=" + 
																selectedColumns[i] +
																"&ctype=" + type + "&index=" + 
																(startIndex + retrievedCount - 1) +
																"&scolumn=" + sortColumn + "&sorder=" +
																sortOrder + "'></TD></TR>");
										}
										else writer.println("<TD id=common_td>" + 
															resultSet.getString(i+1) + "</TD></TR>");
									}
									else writer.println("<TD id=common_td>" + 
														resultSet.getString(i+1) + "</TD></TR>");
								}
							}
							if(retrievedCount >= recordsPerPage) break;
							else								 retrievedCount ++;
						}
						else recordCount++;
					}
					writer.println("</FORM></FORM></TABLE></DIV><BR>");

					writer.println("<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0>");
					writer.println("<TR>");
					writer.println("<TH id=browse_hed WIDTH=36%>Operations on Selected Records</TH>");
					writer.println(	"<TH id=browse_hed WIDTH=4%>" +
									"<IMG SRC='pics/button_edit.png' WIDTH=12 HEIGHT=13 " +
									" BORDER=0 ALT='Edit' STYLE='cursor:hand' " +
									" onClick='submitEditForm(action_form,action_form.row_ids)'></TH>");
					writer.println(	"<TH id=browse_hed WIDTH=4%>" +
									"<IMG SRC='pics/button_drop.png' WIDTH=12 HEIGHT=13 " +
									" BORDER=0 ALT='Delete' STYLE='cursor:hand' " +
									" onClick='submitBDropForm(action_form,action_form.row_ids)'></TH>");
					writer.println("<TH STYLE='background:white' WIDTH=1%></TH>");
					if(startIndex > 1) {
						writer.println("<TH id=browse_hed WIDTH=5%><IMG BORDER=0 WIDTH=12 HEIGHT=12 " +
									   "SRC='pics/prev.gif' onClick='prev_form.submit()' " +
									   " style='cursor:hand' ALT='Previous " + recordsPerPage +
									   " record(s)'></TH>");
					}
					else writer.println("<TH id=browse_hed WIDTH=5%></TH>");

					writer.print("<TH id=browse_hed WIDTH=45%>Displaying Records From " + startIndex + " - "); 
					if((startIndex + recordsPerPage -1) > rowCount)	writer.println(rowCount + "</TH>");
					else writer.println((startIndex + recordsPerPage - 1) + "</TH>");

					if((startIndex + recordsPerPage - 1) < rowCount) {
						writer.println(	"<TH id=browse_hed WIDTH=5%><IMG BORDER=0 WIDTH=12 HEIGHT=12 " +
										" SRC='pics/next.gif' style='cursor:hand' " +
										" onClick='next_form.submit()' ALT='Next " + recordsPerPage +
										" record(s)'></TH>");
					}
					else writer.println("<TH id=browse_hed WIDTH=5%></TH>");
					writer.println("</TR></TABLE>");
					
					writer.println("</BODY></HTML>");

					if(resultSet != null)	resultSet.close();
					connection.close();
				}
				catch(Exception e)	{	e.printStackTrace();	}
			}
		}
		writer.close();
	}
}