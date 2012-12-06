package test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlparser.util.ParserException;
import org.junit.Before;
import org.junit.Test;

import backyard.pageparser.BackyardPage;

public class BackyardPageTester {
	static String pageRaviprak = "";

	@Before
	public void setup() throws IOException {
		pageRaviprak = FileUtils.readFileToString(new File("src/test/test_data/raviprak.html"));
	}

	@Test
	public void testParsing() throws ParserException {
		assertTrue("Read an empty backyard page for the greatest Y! ever (raviprak)", pageRaviprak.length() != 0);
		BackyardPage myPage = new BackyardPage(pageRaviprak);
		checkRaviprak(myPage);
	}

	private void checkRaviprak(BackyardPage raviprak) {
		check("Work Phone", raviprak.workPhone, "+1 (217) 255-4422");
		check("Mobile Phone", raviprak.mobilePhone, "+1 (650) 492-0984");
		check("Home Phone", raviprak.homePhone, "NA");
		check("Pager", raviprak.pagerPhone, "NA");
		check("Pager email", raviprak.pagerEmail, "NA");

		check("Username", raviprak.username, "raviprak");
		check("Email", raviprak.email, "raviprak@yahoo-inc.com");

		check("Messenger ID", raviprak.yMessengerID, "ravihoo@ymail.com");
		check("Location", raviprak.location, "US - Champaign");
		check("Birthday", raviprak.birthday, "NA");
		check("Start Date", raviprak.startDate, "Jan 04, 2010");
		check("Preferred method of contact", raviprak.contactMethod, "Work Phone");
		check("Title", raviprak.title, "Technical Yahoo. Hadoop core (HDFS+YARN) development");
		check("Name", raviprak.name, "Ravi Prakash");
		check("Legal Name", raviprak.legalName, "Ravi Prakash");
		check("Local Time", raviprak.localTime, "Fri Feb 22 15:46:20 2013 (Timezone: America/Chicago)");
		check("Secure ID", raviprak.secureID, "Yes");
		check("Mailing Address", raviprak.mailingAddress, "2021 S First St Suite 110, Champaign, IL, 61820, US");
		check("Mail Stop", raviprak.mailstop, "NA");
		check("Reports to", raviprak.reportsTo, "holder");
		check("Dept/Function ", raviprak.deptFunction, "710718 - 100 Hadoop - US - Engineering Function");

		checkReports(raviprak.directReports, "");

		check("Education Institution", raviprak.education.get(0).institution, "GGS Indraprastha University");
		check("Education Major", raviprak.education.get(0).major, "Computer Science &amp; Engineering");
		check("Education Degree", raviprak.education.get(0).degree, "B.Tech");
		check("Education Period", raviprak.education.get(0).period, "May 2003-2007");
		check("Education Institution", raviprak.education.get(1).institution,
				"University of Southern California (USC), Los Angeles");
		check("Education Major", raviprak.education.get(1).major, "Computer Science");
		check("Education Degree", raviprak.education.get(1).degree, "MS");
		check("Education Period", raviprak.education.get(1).period, "2007-2008");

		check("Experience Company", raviprak.workExperience.get(0).company, "University of Southern California");
		check("Experience Title", raviprak.workExperience.get(0).title, "Programmer Analyst");
		check("Experience Period", raviprak.workExperience.get(0).period, "2008-2009");
		check("Experience Company", raviprak.workExperience.get(1).company, "Motorola");
		check("Experience Title", raviprak.workExperience.get(1).title, "Intern");
		check("Experience Period", raviprak.workExperience.get(1).period, "June - Aug 2008");

		check("Goals Link", raviprak.goalsLink,
				"https://docs.google.com/a/yahoo-inc.com/document/d/17LQ4GMsUpkAICs8BzvXWiLzIOG0-MslJBG7INtbCA5o/edit");

		check("Bravo from", raviprak.bravos.get(0).from, "Antonio Hernandez Aguila");
		check("Bravo type", raviprak.bravos.get(0).type, "Show the Way");
		check("Bravo text", raviprak.bravos.get(0).text,
				"Ravi always communicates his ideas clearly and concisely. He is able to organize and explain very complex topics in easy-to-understand terms and is careful to tailor them to his audience. Working with Ravi on the A29 Project always a pleasure.");
	}

	private void check(String tag, String value, String correctValue) {
		assertTrue(tag + " parsed incorrectly as '" + value + "' instead of correctly as '" + correctValue + "'",
				value != null && value.equals(correctValue));
	}

	private void checkReports(ArrayList<String> directReports, String correctValue) {
		Collections.sort(directReports);
		assertTrue(
				"Direct Reports parsed incorrectly as '" + StringUtils.join(directReports.toArray(), ',')
						+ "' instead of correctly as '" + correctValue + "'",
				correctValue.equals(StringUtils.join(directReports.toArray(), ',')));
	}

}
