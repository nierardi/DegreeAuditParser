package sample;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Block {

	private String blockId;
	private String blockTitle;
	private Element blockData;
	private int totalCredits;
	private int creditsComplete;
	private int creditsNeeded;
	private ArrayList<Rule> blockRules;

	public Block(String blockId, String blockTitle, Element blockHTML) {
		this.blockId = blockId;
		this.blockTitle = blockTitle;
		this.blockData = blockHTML;
		blockRules = new ArrayList<>();

		Elements ruleElems = RuleParser.parseRuleElements(blockData);
		for (Element elem : ruleElems) {
			blockRules.add(RuleParser.parseRule(elem));
			System.out.println("parsed a rule successfully");
		}

	}

	public String toString() {
		String result = "Block: " + blockTitle + ", ID: " + blockId + ", rules -> ";
		for (Rule rule : blockRules) {
			result += "\n\t" + rule.toString();
		}
		return result;
	}

}
