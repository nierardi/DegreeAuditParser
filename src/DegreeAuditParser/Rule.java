package DegreeAuditParser;

import java.util.ArrayList;

public class Rule {

	private String ruleName;
	private RuleStatus status;
	private String otherText;
	private ArrayList<Course> courses;

	public Rule(String ruleName, RuleStatus status, ArrayList<Course> courses, String otherText) {
		this.ruleName = ruleName;
		this.status = status;
		this.courses = courses;
		this.otherText = otherText;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public RuleStatus getStatus() {
		return status;
	}

	public void setStatus(RuleStatus status) {
		this.status = status;
	}

	public ArrayList<Course> getCourses() {
		return courses;
	}

	public void setCourses(ArrayList<Course> courses) {
		this.courses = courses;
	}

	public String toString() {
		String result = "Rule name: " + ruleName + ", status: " + status + ", courses -> ";
		for (Course course : courses) {
			result += "\n\t\t" + course;
		}
		if (!otherText.equals("")) {
			result += "none, description: " + otherText;
		}
		return result;
	}

}
