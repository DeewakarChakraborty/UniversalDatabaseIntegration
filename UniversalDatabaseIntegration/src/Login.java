import java.lang.String;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Login extends HttpServlet
{
	public void doGet(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {

		String message = req.getParameter("message");

		res.setContentType("text/html");
		PrintWriter writer = res.getWriter();

		writer.println("<HTML>");
		writer.println("<HEAD>");
		writer.println("<TITLE> Myadmin </TITLE>");
		writer.println("<META NAME=Author CONTENT=vamsi>");
		writer.println(	"<STYLE>" +
						"#t1 { " +
						"	position:absolute; " +
						"	margin-left:210px; " +
						"	margin-top:50px; " +
						"	width:340px; " +
						"	height:33px; " +
						"	background:#336699; " + 
						"	font-family:Serif; " +
						"	font-weight:bold; " +
						"	font-size:20px; " +
						"	color:#ffffff; " +
						"	text-align:center; " +
						"	border-style:double; " +	
						"	border-color:black; " +
						"	border-width:2px; " +
						"} " +
						"#t2 { " +
						"	position:absolute; " +
						"	margin-left:210px; " +  
						"	margin-top:84px; " +
						"	width:340px; " +
						"	height:180px; " +  
						"	background:#f5f5f5; " +	 
						"	font-family:Tahoma; " +
						"	font-weight:bold; " +
						"	font-size:14px; " +  
						"	color:black; " +
						"	border-style:groove; " +
						"	border-color:black; " +
						"	border-width:1px; " +
						"} " +
						"#msg {" +
						"	background:#abcdef; " +
						"	font-family:Tahoma; " +
						"	font-size:14; " +
						"	font-weight:bold; " +
						"	color:black; " +
						"	text-align:center; " +
						"	text-transform:Capitalize; " +
						"} " +
						"input { " +
						"	height:26px; " +
						"	width:240px; " +
						"	font-size:14px; " +
						"} " +
						"select { 	font-size:16px;	} " +
						"</STYLE>");
		writer.println(	"<SCRIPT LANGUAGE=javascript>" +
						"	var onImages    = new Array(); " +
						"	var offImages   = new Array(); " +
						"function verify() { " +
						"	var ur = document.loginform.url.value; " +
						"	if(ur == null || ur == \"\" || ur == \" \") { " +
						"		alert('URL must be entered.'); " +
						"		document.loginform.url.focus(); " +
						"	} " +
						"	else document.loginform.submit(); " +
						"} " +
						"function isEmpty(id) { " +
						"	if(id.value==null || id.value==\"\" || id.value==\" \") { " +
						"		alert(id.name + \" must be entered.\"); " +
						"		id.focus(); " +
						"	} " +
						"} " +
						"function empty() { " +
						"	document.loginform.reset(); " +
						"	document.loginform.url.value = \"jdbc:odbc:mydsn\"; " +
						"	document.loginform.userid.focus(); " +
						"} " +
						"function down(id) { " +
						"	if(id == 1) document.loginform.pic1.src = onImages[id-1].src; " +
						"	if(id == 2) document.loginform.pic2.src = onImages[id-1].src; " +
						"} " +
						"function up(id) { " +
						"	if(id == 1) document.loginform.pic1.src = offImages[id-1].src; " +
						"	if(id == 2) document.loginform.pic2.src = offImages[id-1].src; " +
						"} " +
						"function load() { " +
						"	onImages[0] = new Image(); " +
						"	onImages[1] = new Image(); " +
						"	onImages[0].src = \"pics/login2.jpg\"; " +
						"	onImages[1].src = \"pics/reset2.jpg\"; " +
						"	offImages[0] = new Image(); " +
						"	offImages[1] = new Image(); " +
						"	offImages[0].src = \"pics/login1.jpg\"; " +
						"	offImages[1].src = \"pics/reset1.jpg\"; " +
						"	document.loginform.url.value = \"jdbc:odbc:mydsn\"; " +
						"	document.loginform.userid.focus(); " +
						"} " +
						"function setURL() { " +
						"	var i = document.loginform.driver.selectedIndex; " +
						
						"	if(i == 0) document.loginform.url.value = \"jdbc:mysql://localhost:3306/edi\"; " +
						"	else if(i == 1) document.loginform.url.value = \"jdbc:oracle:thin:@localhost:1521:xe\"; " +
						"} " +
						"</SCRIPT>");
		writer.println("</HEAD>");
		writer.println("<BODY BGCOLOR=#f5f5f5 onLoad=load()>");
		writer.println("<HR WIDTH=80%>");
		writer.println("<TABLE ALIGN=center CELLPADDING=4 BORDER=0 WIDTH=80%");
		writer.println("<TR>");
		writer.println("<TH id=msg>" + message + "</TH>");
		writer.println("</TR>");
		writer.println("</TABLE>");
		writer.println("<HR WIDTH=80%>");

		writer.println("<FORM NAME=loginform METHOD=post ACTION='LoadAll'>");
		writer.println("<TABLE align=center id=t1><TR><TD>DATABASE LOGIN</TD></TR></TABLE>");
		writer.println("<TABLE align=center id=t2>");
		writer.println("<TR><TD align=center>Driver</TD>");
		writer.println("<TD align=center>");
		writer.println("<SELECT name=driver onChange=setURL()>");
	
		writer.println("<OPTION>com.mysql.jdbc.Driver</OPTION>");
		writer.println("<OPTION>oracle.jdbc.driver.OracleDriver</OPTION>");
		writer.println("</SELECT>");
		writer.println("</TD>");
		writer.println("</TR>");
		writer.println("<TR><TD align=center>URL</TD>");
		writer.println("<TD align=center><INPUT type=text name=url size=32></TD></TR>");
		writer.println("<TR><TD align=center>Username</TD>");
		writer.println(	"<TD align=center><input type=text name=userid size=32 " +
						"onFocus='isEmpty(document.loginform.url)'></TD></TR>");
		writer.println("<TR><TD align=center>Password</TD>");
		writer.println(	"<TD align=center><input type=password name=pass size=32 " +
						"onFocus='isEmpty(document.loginform.url)'></TD></TR>");
		writer.println("<TR><TD align=center colspan=2>");
		writer.println(	"<IMG name=pic1 src='pics/login1.jpg' border=0 " +
						"onMouseOut='up(1)' STYLE='cursor:hand' onMouseDown='down(1)' " +
						"onMouseUp='up(1)' onClick='verify()'>&nbsp&nbsp&nbsp");
		writer.println(	"<img name=pic2 src='pics/reset1.jpg' border=0 " +
						"onMouseOut='up(2)' STYLE='cursor:hand' onMouseDown='down(2)' " +
						"onMouseUp='up(2)' onClick='empty()'>");
		writer.println("</TD></TR>");
		writer.println("</TABLE>");
		writer.println("</FORM>");
		writer.println("</BODY>");
		writer.println("</HTML>");

		writer.close();
	}
}