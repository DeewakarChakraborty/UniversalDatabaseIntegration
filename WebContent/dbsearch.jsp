<%-- 
    Document   : dbsearch
    Created on : Mar 22, 2011, 3:17:58 AM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.sql.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%
                    String name = request.getParameter("cal");
                    String word = request.getParameter("word");
                    System.out.println("hai...............");
                    if (name != null) 
						{
                        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                        Connection con = DriverManager.getConnection("jdbc:odbc:sample");
						System.out.println("conn 1 is created");
                        Statement st=con.createStatement();
                        Statement st1=con.createStatement();

                        
                        %>
                        <table border="1">
                            <tr>
                                <th>
                                    From Oracle
                                </th>
                            </tr>

                        <%
                        ResultSet rs1=st1.executeQuery("select * from tab");
                        while(rs1.next())
                            {
                            String tab=rs1.getString(1);
                            %>
                            <tr>
                                <td>
                                    Table Name <b><%=tab%></b>
                                </td>
                            </tr>
                            <%

                            System.out.println("select "+name+" from "+tab+" where "+name+" like '%"+word+"%'  ");
                        ResultSet rs=st.executeQuery("select "+name+" from "+tab+" where "+name+" like '%"+word+"%' ");
                        while(rs.next())
                            {
                            System.out.println("k");
                            String wrd=rs.getString(1);
                        %>

                            <tr>
                                <td align="center">
                                    <%=wrd%>
                                </td>
                            </tr>
                        
                        <%
                        }
                        }
                        %>

                        </table>
                        <br>
                        <br>
                        <%
                        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                        Connection con1 = DriverManager.getConnection("jdbc:odbc:sample1");
						System.out.println("conn 2 is connected");
                        Statement st2=con1.createStatement();



                        %>
                        <table border="1">
                            <tr>
                                <th>
                                    From Access
                                </th>
                            </tr>

                            <tr>
                                <td>
                                    Table Name <b>sample</b>
                                </td>
                            </tr>

                        <%



                            System.out.println("select "+name+" from sample where "+name+" like '%"+word+"%'  ");
                        ResultSet rs3=st2.executeQuery("select "+name+" from sample where "+name+" like '%"+word+"%' ");
                        while(rs3.next())
                            {
                            System.out.println("k");
                            String wrd=rs3.getString(1);
                        %>

                        <tr>
                                <td align="center">
                                    <%=wrd%>
                                </td>
                            </tr>

                        <%
                        }

                        %>

                        </table>
                        <br>
                        <br>
                        <%
             Class.forName("com.mysql.jdbc.Driver");
            Connection con4=DriverManager.getConnection("jdbc:mysql://localhost/sample","root","root");
			System.out.println("conn3 is connected");
                        Statement st5=con4.createStatement();
                        Statement st6=con4.createStatement();



                        %>
                        <table border="1">
                            <tr>
                                <th colspan="2">
                                    From MySql
                                </th>
                            </tr>
                            
                           
                        <%
                        ResultSet rs5=st5.executeQuery("show tables");
                        while(rs5.next())
                            {
                            String tab=rs5.getString(1);
                                 %>
                            <tr>
                                <td>
                                    Table Name <b><%=tab%></b>
                                </td>
                            </tr>
                            <%

                            System.out.println("select "+name+" from "+tab+" where "+name+" like '%"+word+"%'  ");
                        ResultSet rs6=st6.executeQuery("select "+name+" from "+tab+" where "+name+" like '%"+word+"%' ");
                        while(rs6.next())
                            {
                            System.out.println("k");
                            String wrd=rs6.getString(1);
                        %>

                            <tr>
                                <td align="center">
                                    <%=wrd%>
                                </td>
                            </tr>

                        <%
                        }
                        }

%>
                        </table>

                  
                        <a href="dbsearch.jsp">Back</a>
                        <%
                    }
                    else
                        {

        %>

        <h1>DataBase Search</h1>
        <form action="dbsearch.jsp" method="post">
            <table>
                <tr>
                    <td>
                        Enter Colomn Name:
                    </td>
                    <td>
                        <input type="text" name="cal">
                    </td>
                </tr>
                <tr>
                    <td>
                        Enter Word:
                    </td>
                    <td>
                        <input type="text" name="word">
                    </td>
                </tr>
                <tr>
                    <td>

                    </td>
                    <td>
                        <input type="submit" value="submit">
                    </td>
                </tr>
            </table></form>

        <%
        }
                    %>
                    
    </body>
</html>
