package backyard.db;

import java.io.*;
import java.sql.*;

import org.htmlparser.util.ParserException;

import backyard.pageparser.BackyardPage;

public class BackyardParserDatabasePopulator {
	public static void main(String args[]) throws IOException, ParserException, ClassNotFoundException {
		// This table should already exist
		// create table ravi.backyard(username VARCHAR(50),title VARCHAR(50), name
		// VARCHAR(100), legalName VARCHAR(100),
		// deptFunction VARCHAR(30), workPhone VARCHAR(20), mobilePhone VARCHAR(20),
		// homePhone VARCHAR(20), pagerPhone VARCHAR(20),
		// pagerEmail VARCHAR(30), yMessengerID VARCHAR(30), email VARCHAR(30), location
		// VARCHAR(30), contactMethod VARCHAR(20),
		// mailstop VARCHAR(50), birthday VARCHAR(20), startDate VARCHAR(20), secureID
		// VARCHAR(20), mailingAddress VARCHAR(50),
		// reportsTo VARCHAR(50), goalsLink VARCHAR(100), directReport VARCHAR(150) );

		if (args.length != 1) {
			System.out.println("Usage\n java BackyardParserDatabasePopulator fileContainingAllPages");
			System.exit(1);
		}

		BufferedReader file = new BufferedReader(new FileReader(args[0]));
		String onePageWritten;
		while ((onePageWritten = file.readLine()) != null) {
			String user = onePageWritten.substring(0, onePageWritten.indexOf('\u0001'));
			String page = onePageWritten.substring(onePageWritten.lastIndexOf('\u0001'));

			System.out.print("Reading page for " + user);
			BackyardPage bp = new BackyardPage(page);
			System.out.println(". Parsed page of " + bp.name);

			Connection con = null;
			String url = "jdbc:mysql://localhost:3306/";
			String db = "ravi";
			String driver = "com.mysql.jdbc.Driver";

			Class.forName(driver);
			try {
				con = DriverManager.getConnection(url + db, "raviprak", "testPassword");

				PreparedStatement st = con.prepareStatement(
						"INSERT INTO backyard VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				st.setString(1, bp.username);
				st.setString(2, bp.title);
				st.setString(3, bp.name);
				st.setString(4, bp.legalName);
				st.setString(5, bp.deptFunction);
				st.setString(6, bp.workPhone);
				st.setString(7, bp.mobilePhone);
				st.setString(8, bp.homePhone);
				st.setString(9, bp.pagerPhone);
				st.setString(10, bp.pagerEmail);
				st.setString(11, bp.yMessengerID);
				st.setString(12, bp.email);
				st.setString(13, bp.location);
				st.setString(14, bp.contactMethod);
				st.setString(15, bp.mailstop);
				st.setString(16, bp.birthday);
				st.setString(17, bp.startDate);
				st.setString(18, bp.secureID);
				st.setString(19, bp.mailingAddress);
				st.setString(20, bp.reportsTo);
				st.setString(21, bp.goalsLink);
				st.setString(22, "");
				st.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(System.err);
			}
		}
		file.close();
	}
}
