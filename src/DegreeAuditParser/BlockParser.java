package DegreeAuditParser;

import org.jsoup.nodes.Element;

public class BlockParser {

	private BlockParser() {}

	public static String parseBlockId(Element elem) {
		// <a name="Block-RA000000">
		return elem.attr("name").replace("Block-", "");
	}

	public static String parseBlockName(Element elem) {
		// <a name="BlockTitle-XXXXXXXXX">
		return elem.attr("name").replace("BlockTitle-", "").replace("%20", " ");
	}

}
