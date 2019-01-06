package sample;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class RuleParser {

	private RuleParser() {}

	public static Elements parseRuleElements(Element root) {
		Elements list = new Elements();
		// traverse to the tbody containing each rule
		Element rulesBody = root
			.child(0)
			.child(0)
			.child(0)
			.child(0)
			.child(0);

		for (Element elem : rulesBody.children()) {
			if (elem.className().contains("bgLight")) {
				// this is a rule row
				list.add(elem);
			}
		}
		return list;
	}

	public static Rule parseRule(Element ruleRow) {

		try {
			// first process the title (RuleLabelLine)
			// there should only ever be one of these
			Element titleRoot = ruleRow.getElementsByClass("RuleLabelLine").get(0);
			String ruleStatusStr = titleRoot
				.child(0)
				.child(0)
				.child(0)
				.child(0)
				.child(1)
				.attr("alt");

			RuleStatus ruleStatus = null;
			if (ruleStatusStr.equals("Complete")) {
				ruleStatus = RuleStatus.COMPLETE;
			} else if (ruleStatusStr.equals("Complete except for in-progress classes")) {
				ruleStatus = RuleStatus.PARTIAL;
			} else if (ruleStatusStr.equals("Not yet complete")) {
				ruleStatus = RuleStatus.INCOMPLETE;
			}

			String ruleTitle = "";
			if (ruleStatus == RuleStatus.COMPLETE || ruleStatus == RuleStatus.PARTIAL) {
				// RuleLabelTitleNotNeeded <- also for PARTIAL too!!
				ruleTitle = titleRoot
					.child(0)
					.child(0)
					.child(0)
					.getElementsByClass("RuleLabelTitleNotNeeded")
					.text();
			} else {
				// RuleLabelTitleNeeded
				ruleTitle = titleRoot
					.child(0)
					.child(0)
					.child(0)
					.getElementsByClass("RuleLabelTitleNeeded")
					.text();
			}

			// FOR DEBUG
			OutWriter.outputLn("--------------------\nparsing rule with title: " + ruleTitle + ", status: " + ruleStatus);

			// now process the data, if it exists
			Element dataRoot = ruleRow.getElementsByClass("RuleLabelData").get(0);
			ArrayList<Course> courses = new ArrayList<>();
			String ruleDesc = "";
			// we also have to check for the third child here (see below)
			if (dataRoot.child(0).children().size() > 0 /*&&
				dataRoot.child(0).child(0).child(0).children().size() > 0*/) {
				OutWriter.outputLn("there is rule data, processing it");

				// there is rule data
				// this can be either of two things:
				//  - courses possible to take, if status is INCOMPLETE
				//  - courses complete or in progress, if status is PARTIAL or COMPLETE

				// if the rule is PARTIAL/COMPLETE, td at depth 3 has class "RuleLabelData"
				// the third (second?) child may have an extra, empty tr on it before the one
				// with the actual data. to get around this, we make a list of elements and
				// always get the last one
				Elements thirdChildElems = dataRoot.child(0).child(0).children();
				Element thirdChild = thirdChildElems.get(thirdChildElems.size() - 1);

				try {
					if (thirdChild.child(0).hasClass("RuleLabelData")) {

						// this is a course that is already applied
						// we have to process it differently
						OutWriter.outputLn("has RuleLabelData, ignoring for now");
					}

					// if the rule is INCOMPLETE, td at depth 3 has no class
					else {

						Element dataInnerRoot = thirdChild
							.child(0)
							.child(0)
							.child(0);

						// not even sure if we need this, but we'll capture it anyway
						String ruleDataTitle = dataInnerRoot.getElementsByClass("RuleAdviceTitleNew").text();

						// element list of classes that we need
						// this is passed to a helper method
						Element ruleClasses = dataInnerRoot.getElementsByClass("RuleAdviceData").get(0);
						//courses = new ArrayList<Course>();
						OutWriter.outputLn("starting processing of incomplete rule data for rule: " + ruleTitle);

						// check for text-only rule data instead of classes
						if (ruleClasses.children().size() == 0) {
							// no children, get the text
							ruleDesc = ruleClasses.text();
							OutWriter.outputLn("got ruledesc");
						} else {
							courses = parseIncompleteRuleData(ruleClasses);
						}

					}

				} catch (IndexOutOfBoundsException e) {
					OutWriter.outputLn("!!!!!! THIS RULE THREW AN EXCEPTION when attempting to process data");
					OutWriter.outputLn("\texception: " + e.getMessage());
				}

			} /* else {

				there is no rule data, and nothing to process, so rule's courses will remain empty

			} */

			return new Rule(ruleTitle, ruleStatus, courses, ruleDesc);

		} catch (Exception e) {

			// force the caller to handle the exception
			// throw new ParseException("Error occurred while parsing a rule");
			// for now though we need this for debugging
			e.printStackTrace();
			return null;

		}

	}

	// ONLY for parsing incomplete (red, non-applied) courses
	private static ArrayList<Course> parseIncompleteRuleData(Element ruleAdviceDataRoot) {

		ArrayList<Course> courses = new ArrayList<>();

		// at this point, this can be one of two things:
		// - a link to another block, with the block id (Block-RA000000) as href
		//   as direct child of RuleAdviceData
		// - a list of one or more course links separated by "or", beginning with
		//   "X Credits in"
		if (ruleAdviceDataRoot.child(0).hasAttr("href") &&
			ruleAdviceDataRoot.child(0).attr("href").contains("Block-RA")) {

			OutWriter.outputLn("parsed a block-id block");
			// this is a link to another block
			// don't add courses
		} else {
			// this is a list of courses
			OutWriter.outputLn("parsing a course list block");
			String text = ruleAdviceDataRoot.text();
			Elements ruleChildren = ruleAdviceDataRoot.children();
			for (Element elem : ruleChildren) {
				if (elem.hasAttr("href")) {

					// this is a course
					String jsHref = elem.attr("href");
					String courseTitle;
					if (elem.hasAttr("title")) {
						courseTitle = elem.attr("title");
					} else {
						// this is an "@" course, and doesn't have a title
						courseTitle = "";
					}

					// javascript:GetCourseInfo('ART','@');
					// javascript:GetCourseInfo('ART','100');
					// javascript:GetCourseInfo('PHYS','@');
					// javascript:GetCourseInfo('PHYS','222');
					//                           [  ]   [ ]
					// 0----5----1----1----2----2----3----3----
					//           0    5    0    5    0    5
					String prefix = jsHref.substring(26, 30);
					String numStr = jsHref.substring(33, 36);

					int num;
					if (numStr.contains("@")) {
						// this is an "@" course, representing any possible course
						// from that prefix
						num = -1;
					} else {
						try {
							// normal 4-digit prefix course, with 3-digit num
							num = Integer.parseInt(numStr);
						} catch (NumberFormatException e) {
							// prefix is three digits
							prefix = jsHref.substring(26, 29);
							numStr = jsHref.substring(32, 35);
							try {
								// 3-digit prefix course, with 3-digit num
								num = Integer.parseInt(numStr);
							} catch (NumberFormatException e1) {
								// likely a 3-digit prefix "@" course
								num = -1;
							}

						}
					}

					Course cAdd = new Course(prefix, num, courseTitle);
					courses.add(cAdd);
					OutWriter.outputLn("added a course: " + cAdd);

				}
			}

		}

		return courses;

	}



}
