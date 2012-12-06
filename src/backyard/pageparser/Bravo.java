package backyard.pageparser;

import java.util.ArrayList;

import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;

public class Bravo {
	public String from, type, text;

	public static ArrayList<Bravo> getBravos(NodeList body) {
		ArrayList<Bravo> toRet = new ArrayList<Bravo>();
		try {
			NodeList bravoList = body.extractAllNodesThatMatch(new StringFilter("My Bravos"), true).elementAt(0)
					.getParent().getParent().getParent().getParent().getParent().getChildren();
			bravoList = bravoList.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class), true);
			NodeList trimmedList = new NodeList();
			for (int i = 0; i < bravoList.size(); ++i) {
				if (bravoList.elementAt(i).toHtml().contains("/tools/o/bravo/awarditem.php?id=")) {
					trimmedList.add(bravoList.elementAt(i));

					String bravoString = bravoList.elementAt(i).toHtml();

					Bravo toAdd = new Bravo();
					toAdd.from = BackyardPage.getEnclosingString(bravoString, "Given by:", "</div>");
					toAdd.type = BackyardPage.getEnclosingString(bravoString, "div class='bravoname'>", "</div>");
					toAdd.text = BackyardPage.getEnclosingString(bravoString, "div class='bravoreason'>", "</div>");
					toRet.add(toAdd);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return toRet;
	}

}
