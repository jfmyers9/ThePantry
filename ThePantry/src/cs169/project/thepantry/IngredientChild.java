package cs169.project.thepantry;

import java.util.ArrayList;

public class IngredientChild {
	
	private String name;
	private String group;
	private String image;
	private boolean common;
	private boolean selected;
	
	public IngredientChild(String name, String group) {
		this.name = name;
		this.group = group;
		this.common = false;
		this.selected = false;
	}
	public IngredientChild(String name) {
		this.name = name;
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
	
	public void setImage(String image) {
		this.image = image;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setCommon(boolean common) {
		this.common = common;
	}
	
	public boolean isCommon() {
		return common;
	}
	
	public String toString() {
		return name;
	}
	
	
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof IngredientChild) {
			result = name.equals(((IngredientChild)o).getName());
		}
		return result;
	}

}