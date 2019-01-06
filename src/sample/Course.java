package sample;

public class Course {

	private String prefix;
	private int num;
	private String desc;

	public Course(String prefix, int num, String desc) {
		this.prefix = prefix;
		this.num = num;
		this.desc = desc;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String toString() {
		return "Course: " + prefix + " " + num + ", desc: " + desc;
	}

}
