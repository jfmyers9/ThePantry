package cs169.project.thepantry;

import java.util.ArrayList;

public class IngredientChild {
	
	private String name;
	private String group;
	
	public IngredientChild(String name, String group) {
		this.name = name;
		this.group = group;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setGroup(String group) {
		this.group = group;
	}
	
	public String getGroup() {
		return group;
	}

}