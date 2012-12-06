package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import backistics.pageparser.BackyardPage;

public class BackyardPageTester 
{
	static String page = "";
	
	@Before
	public void setup() 
	{
		try
		{
//			URLConnection urlConnection = new URL("http://backyard.yahoo.com/tools/g/employee/profile?user_id=raviprak").openConnection();
//			BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream() ) );
			
			FileInputStream fis = new FileInputStream("src/test/test_data/raviprak.html");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line;
			while( (line = br.readLine() ) != null )
			{
				page += line;
			}
			br.close();
		} catch(IOException ioe) { System.out.println("CWD: " + System.getProperty("user.dir")); ioe.printStackTrace();}
	}
	
	@Test
	public void testParsing()
	{
		assertTrue ( "Read an empty backyard page for the greatest Y! ever (raviprak)", page.length() != 0 ) ;
		BackyardPage myPage = new BackyardPage();
		myPage.parsePage(page);
		assertTrue ( "WorkPhone parsed incorrectly as " + myPage.workPhone , myPage.workPhone != null && myPage.workPhone.compareTo("12172554422") == 0 );
		assertTrue ( "Mobile Phone parsed incorrectly as " + myPage.mobilePhone , myPage.mobilePhone != null && myPage.mobilePhone.compareTo("16504920984") == 0 );
		assertTrue ( "Email parsed incorrectly as " + myPage.email, myPage.email != null && myPage.email.compareTo("raviprak@yahoo-inc.com" ) == 0 );
		assertTrue ( "Location parsed incorrectly as " + myPage.location, myPage.location != null && myPage.location.compareTo("<td class=\"rc\"><a href=\"http://backyard.yahoo.com/locations/offices/info/US-Champaign.html\">US - Champaign</a> - CPN011209</td>" ) == 0 );
		assertTrue ( "Preferred Contact Method parsed incorrectly as " + myPage.contactMethod, myPage.contactMethod != null && myPage.contactMethod.compareTo("Preferred method of contact: Work Phone") == 0 );
		assertTrue ( "Birthday parsed incorrectly as " + myPage.birthday, myPage.birthday != null && myPage.birthday.compareTo("NA") == 0 );
		assertTrue ( "Start date parsed incorrectly as " + myPage.startDate, myPage.startDate != null && myPage.startDate.compareTo("Jan 04, 2010") == 0 );
		System.out.println("Hello World");
	}

}
