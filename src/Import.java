import java.lang.Class;
import java.lang.String;
import java.lang.Exception;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

import java.util.List;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileUploadException;

public class Import extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) 
				throws ServletException, IOException	{

		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;
		
		String dbProductName	= null;
		String dbProductVersion = null;

		String schemaTerm	= null;
		String schema		= null;

		String message = null;

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
			schema			 = session.getAttribute("schema").toString();

			message	= req.getParameter("message");


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
							"<img name=pic5 src='pics/import2.jpg' border=0 " +
							"width=80 height=26 align=absbottom>"+
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
							"</TD></TR></TABLE><BR>");
			writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%>"); 
			writer.println("<TR><TH width=27% id=common_hed>Database Product Name</TH>"); 
			writer.println("<TD width=73% id=common_data>" + dbProductName + "</TD></TR>");
			writer.println("<TR><TH width=27% id=common_hed>Database Product Version</TH>"); 
			writer.println("<TD width=73% id=common_data>" + dbProductVersion + "</TD></TR>");
			writer.println("<TR><TH width=27% id=common_hed>Displayed " + schemaTerm + "</TH>");
			writer.println("<TD width=73% id=common_data>" + schema + "</TD></TR>");
			writer.println("</TABLE><BR>");

			if(message != null) {
				writer.println("<HR WIDTH=100%>");
				writer.println("<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=4 BORDER=0 WIDTH=100%");
				writer.println("<TR>");
				writer.println("<TH id=insert_norm_msg>" + message + "</TH>");
				writer.println("</TR>");
				writer.println("</TABLE>");
				writer.println("<HR WIDTH=100%>");
			}

			writer.println(	"<FORM NAME=import_form METHOD=post ACTION='Import' " +
							" encType=multipart/form-data >");
			writer.println(	"<TABLE ALIGN=center CELLSPACING=1 CELLPADDING=2 BORDER=0 WIDTH=100% " +
							" STYLE='border-style:double;border-width:1px;border-color:black'>");
			writer.println("<TR><TH COLSPAN=3 id=common_th>IMPORT FROM SQL FILE ...</TH></TR>");
			writer.println(	"<TR><TH WIDTH=20% id=common_hed STYLE='font-weight:bold;" +
							"text-align:center'>File Name</TH>");
			writer.println(	"<TD WIDTH=60% ALIGN=center STYLE='background:#c5d6df'>" +
							"<INPUT TYPE=file NAME=file_name STYLE='background:azure;width:400px;" +
							"height:23px'></TD>");
			writer.println( "<TD ALIGN=center STYLE='background:#f5f5f5'>" +
							" <IMG NAME=pic23 SRC='pics/import11.jpg' " +
							" BORDER=0 WIDTH=70 HEIGHT=24 onMouseDown='putOn(this,23)' " +
							" onMouseUp='putOff(this,23)' onMouseOut='putOff(this,23)' " +
							" onClick='submitImportForm(document.import_form)' STYLE='cursor:hand'></TD>");
			writer.println("</TR></TABLE></FORM>");				
		}
		writer.close();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException	{
		Connection connection = null;
		Statement  statement  = null;
	
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;

		String query  = null;

		String  error_message = null;
		boolean error_occured = false;

		boolean	tableCreated = false;

		DiskFileUpload upload	= null;
		BufferedReader queries	= null;
        List items				= null;

		HttpSession session	  = req.getSession(false);

		if(session == null) {
			res.setContentType("text/html");
			PrintWriter writer = res.getWriter();

			writer.println("<HTML>");
			writer.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
							"Sorry Your session expired. Please Login Again.'\"");
			writer.println("</BODY>");
			writer.println("</HTML>");

			writer.close();
		}
		else {
			driver	= session.getAttribute("driver").toString();
			url		= session.getAttribute("url").toString();
			userid	= session.getAttribute("userid").toString();		
			pass	= session.getAttribute("pass").toString();

			try {
				upload = new DiskFileUpload();			
		        items  = upload.parseRequest(req);
				Iterator iter = items.iterator();
		        while (iter.hasNext()) {
			        FileItem item = (FileItem) iter.next();
					if(!item.isFormField())	{
						queries = new BufferedReader(new InputStreamReader(item.getInputStream()));
					}
				}
			}
			catch(Exception e)	{
				error_occured = true;
				error_message = e.toString();
			}

			if(!error_occured)	{
				try {
					Class.forName(driver);
					connection	= DriverManager.getConnection(url,userid,pass);
					statement	= connection.createStatement();
				}
				catch(Exception e) {
					error_occured = true;
					error_message = e.toString();
				}
			}

			if(error_occured) res.sendRedirect("Import?message=" + error_message);
			else {
				try {
					while(true) {
						query = queries.readLine();
						if(query == null)	break;
						else {
							statement.execute(query);
							if(query.toUpperCase().startsWith("CREATE")) tableCreated = true;
						}
					}
				}	
				catch(Exception e) {
					error_occured = true;
					error_message = e.toString();
				}
			}

			try {
				connection.close();
				statement.close();
			}
			catch(Exception e) { }

			if(error_occured) {
				if(tableCreated) {
					res.setContentType("text/html");
					PrintWriter writer = res.getWriter();
				
					writer.println("<HTML>");
					writer.println("<HEAD>");
					writer.println("<META NAME='Author' CONTENT='Vamsi'>");
					writer.println("<SCRIPT LANGUAGE='javascript'>");
					writer.println("function loadPages() { ");
					writer.println("	window.parent.left.location.href='ListDB'; ");
					writer.println(	"	window.parent.right.location.href='Import?message=" +
									error_message + "'; ");
					writer.println("} ");
					writer.println("</SCRIPT>");
					writer.println("</HEAD>");
					writer.println("<BODY onLoad='loadPages()' BGCOLOR=#ffffff>");
					writer.println("</BODY></HTML>");
					writer.close();
				}				
				else	res.sendRedirect("Import?message=" + error_message);
			}
			else {
				if(tableCreated) {
					res.setContentType("text/html");
					PrintWriter writer = res.getWriter();
				
					writer.println("<HTML>");
					writer.println("<HEAD>");
					writer.println("<META NAME='Author' CONTENT='Vamsi'>");
					writer.println("<SCRIPT LANGUAGE='javascript'>");
					writer.println("function loadPages() { ");
					writer.println("	window.parent.left.location.href='ListDB'; ");
					writer.println(	"	window.parent.right.location.href='Import?message=" +
									"Import Succeeded'; ");
					writer.println("} ");
					writer.println("</SCRIPT>");
					writer.println("</HEAD>");
					writer.println("<BODY onLoad='loadPages()' BGCOLOR=#ffffff>");
					writer.println("</BODY></HTML>");
					writer.close();
				}
				else	res.sendRedirect("Import?message=Import succeeded");
			}
		}
	}
}