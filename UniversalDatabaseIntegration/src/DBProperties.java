import java.lang.String;
import java.lang.Exception;

import java.io.PrintWriter;
import java.io.IOException;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;

import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DBProperties extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse res) 
				throws ServletException, IOException {

		Connection connection = null;
		DatabaseMetaData dm	  = null;
		ResultSet resultSet   = null;
		
		String driver = null;
		String url    = null;
		String userid = null;
		String pass	  = null;
		
		String dbProductName= null;
		String schemaTerm	= null;
		String schema		= null;
		int	   schemaValue	= 0;

		boolean error_occured = false;
		String  error_message = null;

		HttpSession session = req.getSession(false);
		PrintWriter out		= res.getWriter();

		if(session == null) {
			out.println("<HTML>");
			out.println(	"<BODY onLoad=\"window.parent.location.href='Login?message=" +
							"Sorry Your session expired. Please Login Again.'\"");
			out.println("</BODY>");
			out.println("</HTML>");
		}
		else {
			driver = session.getAttribute("driver").toString();
			url    = session.getAttribute("url").toString();
			userid = session.getAttribute("userid").toString();		
			pass   = session.getAttribute("pass").toString();

			dbProductName = session.getAttribute("dbProductName").toString();
			schemaTerm	  = session.getAttribute("schemaTerm").toString();
			schemaValue	  = Integer.parseInt(session.getAttribute("schemaValue").toString());
			schema		  = session.getAttribute("schema").toString();

			try {
				Class.forName(driver);
				connection = DriverManager.getConnection(url,userid,pass);	
				dm = connection.getMetaData();		
			}
			catch(Exception e) {
				error_occured = true;
				error_message = e.toString();
			}

			if(!error_occured) {
				try {
					out.println("<HTML>");
					out.println("<HEAD>");
					out.println("<META NAME='Author' CONTENT='Vamsi'>");
					out.println("<LINK REL='stylesheet' TYPE='text/css' HREF='styles.css'>");
					out.println("<SCRIPT LANGUAGE='javascript' TYPE='text/javascript' " +
								" SRC='script.js'></SCRIPT>");
					out.println("</HEAD>");
					out.println("<BODY onLoad='loadImages()' BGCOLOR=#ffffff " +
								" link=black alink=black vlink=black>");
					out.println("<TABLE BORDER=0 CELLSPACING=0 CELLPADDING=0>");
					out.println("<TR><TD>&nbsp&nbsp&nbsp&nbsp" +
								"<A HREF='DescDB'>" +
								"<img onMouseOver='putOn(this,1)' onMouseOut='putOff(this,1)' " +
								"name=pic1 src='pics/structure1.jpg' border=0 " +
								"width=80 height=26 align=absbottom></A>" +
								"<img name='pic2' src='pics/properties2.jpg' border=0 " +
								" width=80 height=26 align=absbottom>" +
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
					
					out.println("<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0>");
					out.println("<TR><TH COLSPAN=2 id=props_hed>Properties of " + schema + 
								" " + schemaTerm + "</TH>");
					out.println("<TR><TH id=props_sidehed WIDTH=45%>Database Product Name" +
								"<TD id=props_td WIDTH=55%>" + dm.getDatabaseProductName());
					out.println("<TR><TH id=props_sidehed>Database Product Version" +
								"<TD id=props_td>" + dm.getDatabaseProductVersion());
					out.println("<TR><TH id=props_sidehed>Driver Name" +
								"<TD id=props_td>" + dm.getDriverName());
					out.println("<TR><TH id=props_sidehed>Driver Version" +
								"<TD id=props_td>" +	dm.getDriverVersion());
					if(schemaValue == 0) {
						out.println("<TR><TH id=props_sidehed>Catalog Term" +
									"<TD id=props_td>" + dm.getCatalogTerm());
						out.println("<TR><TH id=props_sidehed>Catalog Separator" +
									"<TD id=props_td>" + dm.getCatalogSeparator());
						out.println("<TR><TH id=props_sidehed>Max CatalogName Length" +
									"<TD id=props_td>" + dm.getMaxCatalogNameLength());
						out.println("<TR><TH id=props_sidehed>Supports Catalogs In Data Manipulation" +
									"<TD id=props_td>" + dm.supportsCatalogsInDataManipulation());
						out.println("<TR><TH id=props_sidehed>Supports Catalogs In Index Definitions" +
									"<TD id=props_td>" + dm.supportsCatalogsInIndexDefinitions());
						out.println("<TR><TH id=props_sidehed>Supports Catalogs In Privilege Definitions" +
									"<TD id=props_td>" + dm.supportsCatalogsInPrivilegeDefinitions());
						out.println("<TR><TH id=props_sidehed>Supports Catalogs In Procedure Calls" +
									"<TD id=props_td>" + dm.supportsCatalogsInProcedureCalls());
						out.println("<TR><TH id=props_sidehed>Supports Catalogs In Table Definitions" +
									"<TD id=props_td>" + dm.supportsCatalogsInTableDefinitions());
					}
					else {
						out.println("<TR><TH id=props_sidehed>SchemaTerm" +
									"<TD id=props_td>"+dm.getSchemaTerm());
						out.println("<TR><TH id=props_sidehed>Max SchemaName Length" +
									"<TD id=props_td>" + dm.getMaxSchemaNameLength());
						out.println("<TR><TH id=props_sidehed>Supports Schemas In Data Manipulation" +
									"<TD id=props_td>" + dm.supportsSchemasInDataManipulation());
						out.println("<TR><TH id=props_sidehed>Supports Schemas In Index Definitions" +
									"<TD id=props_td>" + dm.supportsSchemasInIndexDefinitions());
						out.println("<TR><TH id=props_sidehed>Supports Schemas In Privilege Definitions" +
									"<TD id=props_td>" + dm.supportsSchemasInPrivilegeDefinitions());
						out.println("<TR><TH id=props_sidehed>Supports Schemas In Procedure Calls" +
									"<TD id=props_td>" + dm.supportsSchemasInProcedureCalls());
						out.println("<TR><TH id=props_sidehed>Supports Schemas In Table Definitions" +
									"<TD id=props_td>" + dm.supportsSchemasInTableDefinitions());
					}
					out.println("<TR><TH id=props_sidehed>ProcedureTerm" +
								"<TD id=props_td>" + dm.getProcedureTerm());
					out.println("<TR><TD COLSPAN=2><BR>");
					
					out.println("<TR><TH COLSPAN=2 id=props_hed>Naming Conventions</TH></TR>");
					out.println("<TR><TH id=props_sidehed>Max UserName Length" +
								"<TD id=props_td>" + dm.getMaxUserNameLength());
					out.println("<TR><TH id=props_sidehed>Max TableName Length" +
								"<TD id=props_td>" + dm.getMaxTableNameLength());
					out.println("<TR><TH id=props_sidehed>Max ColumnName Length" +
								"<TD id=props_td>" + dm.getMaxColumnNameLength());
					out.println("<TR><TH id=props_sidehed>Max ProcedureName Length" +
								"<TD id=props_td>" + dm.getMaxProcedureNameLength());
					out.println("<TR><TH id=props_sidehed>Max CursorName Length" +
								"<TD id=props_td>" + dm.getMaxCursorNameLength());
					out.println("<TR><TH id=props_sidehed>Max Index Length" +
								"<TD id=props_td>" + dm.getMaxIndexLength());
					out.println("<TR><TH id=props_sidehed>Max Statement Length" +
								"<TD id=props_td>" + dm.getMaxStatementLength());
					out.println("<TR><TH id=props_sidehed>Max Binary Literal Length" +
								"<TD id=props_td>" + dm.getMaxBinaryLiteralLength());
					out.println("<TR><TH id=props_sidehed>Max CharLiteral Length" +
								"<TD id=props_td>" + dm.getMaxCharLiteralLength());
					out.println("<TR><TH id=props_sidehed>Extra Name Characters" +
								"<TD id=props_td>" + dm.getExtraNameCharacters());
					out.println("<TR><TD COLSPAN=2><BR>");

					out.println("<TR><TH COLSPAN=2 id=props_hed>Supported functions</TH></TR>");
					out.println("<TR><TH id=props_sidehed>Numeric Functions" +
								"<TD id=props_td>" + dm.getNumericFunctions().replaceAll(",",", "));
					out.println("<TR><TH id=props_sidehed>String Functions" +
								"<TD id=props_td>" + dm.getStringFunctions().replaceAll(",",", "));
					out.println("<TR><TH id=props_sidehed>System Functions" +
								"<TD id=props_td>" + dm.getSystemFunctions().replaceAll(",",", "));
					out.println("<TR><TH id=props_sidehed>TimeDate Functions" +
								"<TD id=props_td>" + dm.getTimeDateFunctions().replaceAll(",",", "));
					out.println("<TR><TH id=props_sidehed>SQL Keywords" +
								"<TD id=props_td>" + dm.getSQLKeywords().replaceAll(",",", "));
					out.println("</TABLE><BR>");

					out.println("<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0>");
					out.println("<TR><TH id=props_hed COLSPAN=7>SUPPORTED DATA TYPES</TH></TR>");
					out.println("<TR><TH id=props_subhed>NAME</TH>");
					out.println("<TH id=props_subhed>Precision</TH>");
					out.println("<TH id=props_subhed>Nullable</TH>");
					out.println("<TH id=props_subhed>Case Sensitive</TH>");
					out.println("<TH id=props_subhed>Searchable</TH>");
					out.println("<TH id=props_subhed>Signed</TH>");
					out.println("<TH id=props_subhed>Radix</TH></TR>");
				
					resultSet = dm.getTypeInfo();
					while(resultSet.next()) {
						out.println("<TR>");
						out.println("<TD id=props_td>" + resultSet.getString(1) + "</TD>");
						out.println("<TD id=props_td style='text-align:right'>" + 
									resultSet.getString(3) + "</TD>");

						out.println("<TD id=props_td style='text-align:center'>");
						if(resultSet.getBoolean(7)) out.println("yes</TD>");
						else						out.println("no</TD>");
						
						out.println("<TD id=props_td style='text-align:center'>");
						if(resultSet.getBoolean(8))	out.println("yes</TD>");
						else						out.println("no</TD>");

						out.println("<TD id=props_td style='text-align:center'>");
						if(resultSet.getShort(9) == 0)	out.println("no</TD>");
						else							out.println("yes</TD>");
						
						out.println("<TD id=props_td style='text-align:center'>");
						if(resultSet.getBoolean(10))	out.println("no</TD>");
						else							out.println("yes</TD>");
						
						out.print("<TD id=props_td style='text-align:center'>"); 
						try {	out.print(resultSet.getString(18));	}
						catch(Exception e)	{	out.println("-");	}
						out.println("</TD></TR>");
					}
					out.println("</TABLE><BR>");

					out.println("<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=4 BORDER=0>");
					out.println("<TR><TH COLSPAN=2 id=props_hed>Other Properties</TH></TR>");
					out.println("<TR><TH id=props_sidehed WIDTH=45%>Default Transaction Isolation" +
								"<TD id=props_td WIDTH=55%>" + dm.getDefaultTransactionIsolation());
					out.println("<TR><TH id=props_sidehed>Max Columns In GroupBy" +
								"<TD id=props_td>" + dm.getMaxColumnsInGroupBy());
					out.println("<TR><TH id=props_sidehed>Max Columns In OrderBy" +
								"<TD id=props_td>" + dm.getMaxColumnsInOrderBy());
					out.println("<TR><TH id=props_sidehed>Max Columns In Select" +
								"<TD id=props_td>" + dm.getMaxColumnsInSelect());
					out.println("<TR><TH id=props_sidehed>Max Columns In Table" +
								"<TD id=props_td>" + dm.getMaxColumnsInTable());
					out.println("<TR><TH id=props_sidehed>Max Connections" +
								"<TD id=props_td>" + dm.getMaxConnections());
					out.println("<TR><TH id=props_sidehed>Max RowSize" +
								"<TD id=props_td>" + dm.getMaxRowSize());
					out.println("<TR><TH id=props_sidehed>Max Statements" +
								"<TD id=props_td>" + dm.getMaxStatements());
					out.println("<TR><TH id=props_sidehed>Max Tables In Select" +
								"<TD id=props_td>" + dm.getMaxTablesInSelect());
					out.println("<TR><TH id=props_sidehed>Data Definition Causes Transaction Commit" +
								"<TD id=props_td>" + dm.dataDefinitionCausesTransactionCommit());
					out.println("<TR><TH id=props_sidehed>Data Definition Ignored In Transactions" +
								"<TD id=props_td>" + dm.dataDefinitionIgnoredInTransactions());
					out.println("<TR><TH id=props_sidehed>Does Max Row Size Include Blobs" +
								"<TD id=props_td>" + dm.doesMaxRowSizeIncludeBlobs());
					out.println("<TR><TH id=props_sidehed>Is Catalog At Start" +
								"<TD id=props_td>"+dm.isCatalogAtStart());
					out.println("<TR><TH id=props_sidehed>Is ReadOnly<TD id=props_td>"+dm.isReadOnly());
					out.println("<TR><TH id=props_sidehed>Null Plus NonNull Is Null" +
								"<TD id=props_td>" + dm.nullPlusNonNullIsNull());
					out.println("<TR><TH id=props_sidehed>Nulls Are Sorted At End" +
								"<TD id=props_td>" + dm.nullsAreSortedAtEnd());
					out.println("<TR><TH id=props_sidehed>Nulls Are Sorted At Start " +
								"<TD id=props_td>" + dm.nullsAreSortedAtStart());
					out.println("<TR><TH id=props_sidehed>Nulls Are Sorted High" +
								"<TD id=props_td>" + dm.nullsAreSortedHigh());
					out.println("<TR><TH id=props_sidehed>Nulls Are Sorted Low" +
								"<TD id=props_td>"+dm.nullsAreSortedLow());
					out.println("<TR><TH id=props_sidehed>Stores LowerCase Identifiers" + 
								"<TD id=props_td>" + dm.storesLowerCaseIdentifiers());
					out.println("<TR><TH id=props_sidehed>Stores LowerCase Quoted Identifiers" +
								"<TD id=props_td>" + dm.storesLowerCaseQuotedIdentifiers());
					out.println("<TR><TH id=props_sidehed>Stores MixedCase Identifiers" +
								"<TD id=props_td>" + dm.storesMixedCaseIdentifiers());
					out.println("<TR><TH id=props_sidehed>Stores MixedCase Quoted Identifiers" +
								"<TD id=props_td>" + dm.storesMixedCaseQuotedIdentifiers());
					out.println("<TR><TH id=props_sidehed>Stores UpperCase Identifiers" +
								"<TD id=props_td>" + dm.storesUpperCaseIdentifiers());
					out.println("<TR><TH id=props_sidehed>Stores UpperCase Quoted Identifiers" +
								"<TD id=props_td>" + dm.storesUpperCaseQuotedIdentifiers());
					out.println("<TR><TH id=props_sidehed>Supports ANSI92 Entry Level SQL" +
								"<TD id=props_td>" + dm.supportsANSI92EntryLevelSQL());
					out.println("<TR><TH id=props_sidehed>Supports ANSI92 Full SQL" +
								"<TD id=props_td>" + dm.supportsANSI92FullSQL());
					out.println("<TR><TH id=props_sidehed>Supports ANSI92 Intermediate SQL" +
								"<TD id=props_td>" + dm.supportsANSI92IntermediateSQL());
					out.println("<TR><TH id=props_sidehed>Supports Alter Table With Add Column" +
								"<TD id=props_td>" + dm.supportsAlterTableWithAddColumn());
					out.println("<TR><TH id=props_sidehed>Supports Alter Table With Drop Column" +
								"<TD id=props_td>" + dm.supportsAlterTableWithDropColumn());
					out.println("<TR><TH id=props_sidehed>Supports Column Aliasing" +
								"<TD id=props_td>" + dm.supportsColumnAliasing());
					out.println("<TR><TH id=props_sidehed>Supports Convert" +
								"<TD id=props_td>" + dm.supportsConvert());
					out.println("<TR><TH id=props_sidehed>Supports Core SQL Grammar" +
								"<TD id=props_td>" + dm.supportsCoreSQLGrammar());
					out.println("<TR><TH id=props_sidehed>Supports Correlated Subqueries" +
								"<TD id=props_td>" + dm.supportsCorrelatedSubqueries());
					out.println("<TR><TH id=props_sidehed>Supports DDL And DML Transactions" +
								"<TD id=props_td>" + 
								dm.supportsDataDefinitionAndDataManipulationTransactions());
					out.println("<TR><TH id=props_sidehed>Supports DML Transactions Only" +
								"<TD id=props_td>" + dm.supportsDataManipulationTransactionsOnly());
					out.println("<TR><TH id=props_sidehed>Supports Different Table Correlation Names" + 
								"<TD id=props_td>" + dm.supportsDifferentTableCorrelationNames());
					out.println("<TR><TH id=props_sidehed>Supports Expressions In OrderBy" +
								"<TD id=props_td>" + dm.supportsExpressionsInOrderBy());
					out.println("<TR><TH id=props_sidehed>Supports Extended SQL Grammar" +
								"<TD id=props_td>" + dm.supportsExtendedSQLGrammar());
					out.println("<TR><TH id=props_sidehed>Supports Full Outer Joins" +
								"<TD id=props_td>" + dm.supportsFullOuterJoins());
					out.println("<TR><TH id=props_sidehed>Supports GroupBy" +
								"<TD id=props_td>" + dm.supportsGroupBy());
					out.println("<TR><TH id=props_sidehed>Supports GroupBy Beyond Select" +
								"<TD id=props_td>" + dm.supportsGroupByBeyondSelect());
					out.println("<TR><TH id=props_sidehed>Supports GroupBy Unrelated" +
								"<TD id=props_td>" + dm.supportsGroupByUnrelated());
					out.println("<TR><TH id=props_sidehed>Supports Integrity Enhancement Facility" +
								"<TD id=props_td>" + dm.supportsIntegrityEnhancementFacility());
					out.println("<TR><TH id=props_sidehed>Supports Like Escape Clause" +
								"<TD id=props_td>" + dm.supportsLikeEscapeClause());
					out.println("<TR><TH id=props_sidehed>Supports Limited Outer Joins" +
								"<TD id=props_td>" + dm.supportsLimitedOuterJoins());
					out.println("<TR><TH id=props_sidehed>Supports Minimum SQL Grammar" +
								"<TD id=props_td>" + dm.supportsMinimumSQLGrammar());
					out.println("<TR><TH id=props_sidehed>Supports MixedCase Identifiers" +
								"<TD id=props_td>" + dm.supportsMixedCaseIdentifiers());
					out.println("<TR><TH id=props_sidehed>Supports MixedCase Quoted Identifiers" +
								"<TD id=props_td>" + dm.supportsMixedCaseQuotedIdentifiers());
					out.println("<TR><TH id=props_sidehed>Supports Multiple ResultSets" +
								"<TD id=props_td>" + dm.supportsMultipleResultSets());
					out.println("<TR><TH id=props_sidehed>Supports Multiple Transactions" +
								"<TD id=props_td>" + dm.supportsMultipleTransactions());
					out.println("<TR><TH id=props_sidehed>Supports NonNullable Columns" +
								"<TD id=props_td>" + dm.supportsNonNullableColumns());
					out.println("<TR><TH id=props_sidehed>Supports Open Cursors Across Commit" +
								"<TD id=props_td>" + dm.supportsOpenCursorsAcrossCommit());
					out.println("<TR><TH id=props_sidehed>Supports Open Cursors Across Rollback" +
								"<TD id=props_td>" + dm.supportsOpenCursorsAcrossRollback());
					out.println("<TR><TH id=props_sidehed>Supports Open Statements Across Commit" +
								"<TD id=props_td>" + dm.supportsOpenStatementsAcrossCommit());
					out.println("<TR><TH id=props_sidehed>Supports Open Statements Across Rollback" +
								"<TD id=props_td>" + dm.supportsOpenStatementsAcrossRollback());
					out.println("<TR><TH id=props_sidehed>Supports OrderBy Unrelated" +
								"<TD id=props_td>" + dm.supportsOrderByUnrelated());
					out.println("<TR><TH id=props_sidehed>Supports Outer Joins" +
								"<TD id=props_td>" + dm.supportsOuterJoins());
					out.println("<TR><TH id=props_sidehed>Supports Positioned Delete" +
								"<TD id=props_td>" + dm.supportsPositionedDelete());
					out.println("<TR><TH id=props_sidehed>Supports Positioned Update" +
								"<TD id=props_td>" + dm.supportsPositionedUpdate());
					out.println("<TR><TH id=props_sidehed>Supports Select For Update" +
								"<TD id=props_td>" + dm.supportsSelectForUpdate());
					out.println("<TR><TH id=props_sidehed>Supports Stored Procedures" +
								"<TD id=props_td>" + dm.supportsStoredProcedures());
					out.println("<TR><TH id=props_sidehed>Supports Subqueries In Comparisons" +
								"<TD id=props_td>" + dm.supportsSubqueriesInComparisons());
					out.println("<TR><TH id=props_sidehed>Supports Subqueries In Exists" +
								"<TD id=props_td>" + dm.supportsSubqueriesInExists());
					out.println("<TR><TH id=props_sidehed>Supports Subqueries In Ins" +
								"<TD id=props_td>" + dm.supportsSubqueriesInIns());
					out.println("<TR><TH id=props_sidehed>Supports Subqueries In Quantifieds" +
								"<TD id=props_td>" + dm.supportsSubqueriesInQuantifieds());
					out.println("<TR><TH id=props_sidehed>Supports Table Correlation Names" +
								"<TD id=props_td>" + dm.supportsTableCorrelationNames());
					out.println("<TR><TH id=props_sidehed>Supports Transactions" +
								"<TD id=props_td>" + dm.supportsTransactions());
					out.println("<TR><TH id=props_sidehed>Supports Union" +
								"<TD id=props_td>"+dm.supportsUnion());
					out.println("<TR><TH id=props_sidehed>Supports UnionAll" +
								"<TD id=props_td>" + dm.supportsUnionAll());
					out.println("<TR><TH id=props_sidehed>Uses Local File Per Table" +
								"<TD id=props_td>" + dm.usesLocalFilePerTable());
					out.println("<TR><TH id=props_sidehed>Uses Local Files" +
								"<TD id=props_td>" + dm.usesLocalFiles());
					out.println("<TR><TH id=props_sidehed>Identifier Quote String" +
								"<TD id=props_td>" + dm.getIdentifierQuoteString());
					out.println("<TR><TH id=props_sidehed>Search String Escape" +
								"<TD id=props_td>" + dm.getSearchStringEscape());
					out.println("</TABLE></BODY></HTML>");

					out.close();
				}
				catch(Exception e)	{	e.printStackTrace();	}

				try	{	
					if(resultSet != null)	resultSet.close();
					connection.close();	
				}	
				catch(Exception e)	{	e.printStackTrace();	}
			}
		}
		out.close();
	}
}