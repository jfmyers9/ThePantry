package cs169.project.thepantry;

import java.util.ArrayList;

public class IngredientChild {
	
	private String name;
	private String group;
	private boolean selected;
	
	public IngredientChild(String name, String group) {
		this.name = name;
		this.group = group;
		this.selected = false;
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
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isSelected() {
		return selected;
	}

}