package backyard.pageparser;

import java.util.ArrayList;

import org.htmlparser.filters.*;
import org.htmlparser.util.NodeList;
import org.htmlparser.nodes.*;

public class Education {
	public String institution, major, degree, period;

	public static ArrayList<Education> getEducation(NodeList body) {
		ArrayList<Education> toReturn = new ArrayList<Education>();

		try {
			NodeList educationList = body.extractAllNodesThatMatch(new StringFilter("Education"), true);
			if (educationList.size() != 0) {
				educationList = educationList.elementAt(0).getParent().getParent().getParent().getParent()
						.getChildren();
				educationList = educationList.extractAllNodesThatMatch(new NodeClassFilter(TextNode.class), true);
				NodeList trimmedList = new NodeList();
				// Remove all the Text nodes with \n or only whitespace
				for (int i = 0; i < educationList.size(); ++i) {
					if (!educationList.elementAt(i).toHtml().trim().isEmpty())
						trimmedList.add(educationList.elementAt(i));
				}
				for (int i = 5; i < trimmedList.size(); i += 4) {
					Education toAdd = new Education();
					toAdd.institution = trimmedList.elementAt(i).toHtml();
					toAdd.major = trimmedList.elementAt(i + 1).toHtml();
					toAdd.degree = trimmedList.elementAt(i + 2).toHtml();
					toAdd.period = trimmedList.elementAt(i + 3).toHtml();
					toReturn.add(toAdd);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return toReturn;
	}

	@Override
	public String toString() {
		return "Institution: '" + this.institution + "', Major: '" + this.major + "', Degree: '" + this.degree
				+ "', Period: '" + this.period + "'";
	}

}
