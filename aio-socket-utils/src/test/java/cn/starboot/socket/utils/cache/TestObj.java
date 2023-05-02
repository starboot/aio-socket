package cn.starboot.socket.utils.cache;

public class TestObj {

	private String name;

	private String sex;

	private String test;

	public TestObj(String name, String sex, String test) {
		this.name = name;
		this.sex = sex;
		this.test = test;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	@Override
	public String toString() {
		return "TestObj{" +
				"name='" + name + '\'' +
				", sex='" + sex + '\'' +
				", test='" + test + '\'' +
				'}';
	}
}
