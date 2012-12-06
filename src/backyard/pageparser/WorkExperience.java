package backyard.pageparser;

import java.util.ArrayList;

import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;

public class WorkExperience {
	public String company, title, period;

	public static ArrayList<WorkExperience> getWorkExperience(NodeList body) {
		ArrayList<WorkExperience> toReturn = new ArrayList<WorkExperience>();
		try {
			NodeList experienceList = body.extractAllNodesThatMatch(new StringFilter("Experience"), true);
			if (experienceList.size() != 0) {
				experienceList = experienceList.elementAt(0).getParent().getParent().getParent().getParent()
						.getChildren();
				experienceList = experienceList.extractAllNodesThatMatch(new NodeClassFilter(TextNode.class), true);
				NodeList trimmedList = new NodeList();
				// Remove all the Text nodes with \n or only whitespace
				for (int i = 0; i < experienceList.size(); ++i) {
					if (!experienceList.elementAt(i).toHtml().trim().isEmpty())
						trimmedList.add(experienceList.elementAt(i));
				}
				for (int i = 4; i < trimmedList.size(); i += 3) {
					WorkExperience toAdd = new WorkExperience();
					toAdd.company = trimmedList.elementAt(i).toHtml();
					toAdd.title = trimmedList.elementAt(i + 1).toHtml();
					toAdd.period = trimmedList.elementAt(i + 2).toHtml();
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
		return "Company: '" + this.company + "', Title: '" + this.title + "', Period: '" + this.period + "'";
	}

}
