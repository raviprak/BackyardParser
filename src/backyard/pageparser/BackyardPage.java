package backyard.pageparser;

import java.util.ArrayList;

import org.htmlparser.*;
import org.htmlparser.filters.*;
import org.htmlparser.util.*;
import org.htmlparser.tags.*;

public class BackyardPage {
	public String username, title, name, legalName, localTime, deptFunction;
	public String workPhone, mobilePhone, homePhone, pagerPhone, pagerEmail;
	public String yMessengerID, email, location, contactMethod, mailstop;
	public String birthday, startDate, secureID, mailingAddress, reportsTo;
	public String goalsLink;
	public ArrayList<String> directReports;
	public ArrayList<Bravo> bravos;
	public ArrayList<Education> education;
	public ArrayList<WorkExperience> workExperience;

	public BackyardPage(String page) throws ParserException {
		parsePage(page);
	}

	/**
	 * Given the HTML text of the page, parses out the data
	 * 
	 * @param page
	 *            the HTML text to parse
	 * @throws ParserException
	 */
	public void parsePage(String page) throws ParserException {
		Parser parser = Parser.createParser(page, null);
		NodeList nodeList = parser.parse(null);
		NodeList body = nodeList.extractAllNodesThatMatch(new TagNameFilter("BODY"), true);

		this.workPhone = getNextTdElement(body, "Work:");
		this.mobilePhone = getNextTdElement(body, "Mobile:");
		this.homePhone = getNextTdElement(body, "Home:");
		this.pagerPhone = getNextTdElement(body, "Pager:");
		this.pagerEmail = getNextTdElement(body, "Pager email:");

		this.username = getNextTdElement(body, "User ID:");
		this.email = getNextTdElement(body, "Email:");

		this.yMessengerID = getNextTdElement(body, "Y! Messenger ID:");

		this.location = getNextTdElement(body, "Location:");

		this.contactMethod = getEnclosingString(page, "Preferred method of contact: ", "</td>");

		this.birthday = getNextTdElement(body, "Birthday:");
		this.startDate = getNextTdElement(body, "Start Date:");

		this.title = getEnclosingString(page, "<h3 class=\"title\">", "</h3>");
		;

		this.directReports = getDirectReports(body);

		this.name = getEnclosingString(page, "<h4 class=\"name\">", "</h4>");
		this.legalName = getNextTdElement(body, "Legal Name:");
		this.localTime = getNextTdElement(body, "Local Time:").replaceAll("\n", "");

		this.secureID = getNextTdElement(body, "Secure ID:");
		this.mailingAddress = getNextTdElement(body, "Mailing Address:");
		this.mailstop = getNextTdElement(body, "Mail Stop:");
		this.deptFunction = getNextTdElement(body, "Dept/Funct.:");

		this.reportsTo = getReportsTo(body);

		this.education = Education.getEducation(body);

		this.workExperience = WorkExperience.getWorkExperience(body);

		this.goalsLink = getGoalsLink(body);

		this.bravos = Bravo.getBravos(body);
	}

	private String getReportsTo(NodeList body) {
		try {
			String plainString = body.extractAllNodesThatMatch(new StringFilter("Reports To:", true), true).elementAt(0)
					.getParent().getParent().toHtml();
			final String URL = "tools/g/employee/profile?user_id=";
			int beginIndex = plainString.indexOf(URL);
			if (beginIndex != -1) {
				beginIndex += URL.length();
				int endIndex = plainString.indexOf("\">", beginIndex);
				return plainString.substring(beginIndex, endIndex);
			}
		} catch (NullPointerException npe) {
			npe.printStackTrace(System.err);
		}
		return "NA";
	}

	public static String getEnclosingString(String page, String tagString, String endTag) {
		int beginIndex = page.indexOf(tagString) + tagString.length();
		if (beginIndex == -1)
			return "NA";
		int endIndex = page.indexOf(endTag, beginIndex);
		if (endIndex == -1)
			endIndex = page.length();
		return page.substring(beginIndex, endIndex);
	}

	private String getNextTdElement(NodeList body, String tag) {
		try {
			Node element = body.extractAllNodesThatMatch(new StringFilter(tag, true), true).elementAt(0).getParent()
					.getParent().getChildren().extractAllNodesThatMatch(new NodeClassFilter(TableColumn.class), true)
					.elementAt(1);
			NodeList WorkPhoneTexts = element.getChildren()
					.extractAllNodesThatMatch(new NodeClassFilter(org.htmlparser.nodes.TextNode.class), true);
			for (int i = 0; i < WorkPhoneTexts.size(); ++i)
				if (!WorkPhoneTexts.elementAt(i).toPlainTextString().trim().isEmpty())
					return WorkPhoneTexts.elementAt(i).toPlainTextString().trim();
		} catch (NullPointerException npe) {
			npe.printStackTrace(System.err);
		}
		return "NA";
	}

	private ArrayList<String> getDirectReports(NodeList body) {
		ArrayList<String> toReturn = new ArrayList<String>();

		NodeList reportsList = body.extractAllNodesThatMatch(new StringFilter("Direct Reports"), true);
		if (reportsList.size() != 0) {
			reportsList = reportsList.elementAt(0).getParent().getParent().getChildren()
					.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class), true);
		}

		for (int i = 0; i < reportsList.size(); i++) {
			String plainString = reportsList.elementAt(i).toHtml();
			final String URL = "/tools/g/employee/profile?user_id=";
			int beginIndex = plainString.indexOf(URL);
			if (beginIndex != -1) {
				beginIndex += URL.length();
				int endIndex = plainString.indexOf("\'>", beginIndex);
				toReturn.add(plainString.substring(beginIndex, endIndex));
			}
		}
		return toReturn;
	}

	private String getGoalsLink(NodeList body) {
		NodeList goalsNodeList = body.extractAllNodesThatMatch(new StringFilter("View my goals", true), true);
		if (goalsNodeList.size() > 0) {
			String goalsLink = goalsNodeList.elementAt(0).getParent().toHtml();
			int beginIndex = goalsLink.indexOf("href=\'") + "href=\'".length();
			if (beginIndex == -1)
				return "";
			int endIndex = goalsLink.indexOf("\'", beginIndex + 1);
			if (beginIndex > 0 && endIndex > 0)
				return goalsLink.substring(beginIndex, endIndex);
		}
		return "";
	}
}
