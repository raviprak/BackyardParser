package backistics.pageparser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hard coded strings used in parsing.
 * @author raviprak
 *
 */
interface someAssumptions
{
	String URLprefix  = "http://backyard.yahoo.com/tools/g/employee/profile?user_id=";
	
	String reportsRegex = "tools/g/employee/profile\\?user_id=\\S*'>";
	String startDirectReportsSection = "<!-- START: direct reports -->";
	String endDirectReportsSection = "<!-- END: direct reports -->";

	String PhoneNumberRegex = "\\d{3,}+"; 		//Assuming anything with 3 consecutive digits in the subsection is a phone number. Could be better
	String EmailRegex = "mailto:\\S{1,}\\.com\"";
	String LocationRegex = "<td class=\"rc\">.+</td>";
	String contactMethodRegex = "Preferred method of contact:.+?</td></tr>";
	String birthdayRegex = "\\d\\d-\\S{1,} |NA";
	String startDateRegex = "\">\\S{2,}? \\d{2}, \\d{4}";
	
	String WorkPhoneTag = ">Work:</td>"; 
	String MobilePhoneTag = ">Mobile:</td>";
	String HomePhoneTag = ">Home:</td>";
	String PagerTag = ">Pager:</td>";
	String PagerEmailTag = ">Pager email:</td>";
	String YIdTag = "Y! ID:</td>";
	String EmailTag = "Email:</td>";
	String LocationTag = "Location:</td>";
	String SecureIDTag = "Secure ID:</td>";
	String contactMethodTag = "Preferred method of contact:";
	String endProfileTag = "<!-- END: profile -->";
	String birthdayTag = "<td class=\"lc\">Birthday:</td>";
	String legalNameTag = "<td class=\"lc\">Legal Name:</td>";
	String startDateTag = "Start Date:";
	String endOrganisationTag = "<!-- END: organizational information -->";
	String bravosBegin = "<!-- START: my bravos -->";
	String bravosEnd = "<!-- END: my bravos -->";
}

/**
 * Abstracts a single user's backyard page
 * @author raviprak
 *
 */
public class BackyardPage 
{
	public String username;
	public String workPhone, mobilePhone, homePhone, pagerPhone;
	public String email, location, contactMethod;
	public String birthday, startDate;
	public ArrayList<String> directReports;
	public ArrayList<Bravo> bravos;
	
	/**
	 * Given a the HTML text of the page, parses out the data
	 * @param page the HTML text to parse
	 */
	public void parsePage(String page)
	{
		
		workPhone = getMatchFromSubsection(page, someAssumptions.WorkPhoneTag, someAssumptions.MobilePhoneTag, someAssumptions.PhoneNumberRegex);
		mobilePhone = getMatchFromSubsection(page, someAssumptions.MobilePhoneTag, someAssumptions.HomePhoneTag, someAssumptions.PhoneNumberRegex);
		homePhone = getMatchFromSubsection(page, someAssumptions.HomePhoneTag, someAssumptions.PagerTag, someAssumptions.PhoneNumberRegex);
		pagerPhone = getMatchFromSubsection(page, someAssumptions.PagerTag, someAssumptions.PagerEmailTag, someAssumptions.PhoneNumberRegex);

		email = getMatchFromSubsection(page, someAssumptions.EmailTag, someAssumptions.LocationTag, someAssumptions.EmailRegex);
		email = (email != null) ? email.substring("mailto:".length(), email.length()-1) : null;
		if(email != null && email.endsWith("yahoo-inc.com") )
			username = email.substring(0, "@yahoo-inc.com".length() );

		location = getMatchFromSubsection(page, someAssumptions.LocationTag, someAssumptions.SecureIDTag, someAssumptions.LocationRegex );

		contactMethod = getMatchFromSubsection(page, someAssumptions.contactMethodTag, someAssumptions.endProfileTag, someAssumptions.contactMethodRegex);
		contactMethod = (contactMethod != null) ? contactMethod.substring(0, contactMethod.length() - "</td></tr>".length()) : null;
		
		birthday = getMatchFromSubsection(page, someAssumptions.birthdayTag, someAssumptions.legalNameTag, someAssumptions.birthdayRegex);
		
		startDate = getMatchFromSubsection(page, someAssumptions.startDateTag, someAssumptions.endOrganisationTag, someAssumptions.startDateRegex);
		startDate = (startDate != null) ? startDate.substring(2) : null;
		
		directReports = directReports(page);
		
		String bravoSection = page.substring( page.indexOf(someAssumptions.bravosBegin), page.indexOf(someAssumptions.bravosEnd) );
		bravos = Bravo.getBravos(bravoSection);
	}

	/**
	 * Returns the usernames of the direct reports in a page
	 * @param page the HTML text from which direct reports will be parsed
	 * @return An ArrayList of Strings containing the usernames of the direct reports 
	 */
	private ArrayList<String> directReports(String page)
	{
		ArrayList<String> reports = new ArrayList<String>();
		String reportsSection =  page.substring(page.indexOf(someAssumptions.startDirectReportsSection), page.indexOf(someAssumptions.endDirectReportsSection) );
		Pattern reportsPattern = Pattern.compile( someAssumptions.reportsRegex );
		
		Matcher matcher = reportsPattern.matcher(reportsSection);
		while( matcher.find() )
			reports.add( matcher.group().substring(someAssumptions.reportsRegex.length() - 6, matcher.group().length() - 2 ) );
		
		return reports;
	}
	
	/**
	 * Gets the string from page which is between start and end and matches regex
	 * @param page the HTML text
	 * @param start the string which denotes where to start matching from
	 * @param end the string which denotes where to stop matching 
	 * @param regex The regular expression to match
	 * @return the matched string
	 */
	private String getMatchFromSubsection(String page, String start, String end, String regex)
	{
		String sectionToSearch = page;
		if(start != null && end != null)
			sectionToSearch = page.substring( page.indexOf(start), page.indexOf(end) );
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sectionToSearch);
		if( true == matcher.find() )
			return matcher.group();
		else
			return null;
	}
	
}
