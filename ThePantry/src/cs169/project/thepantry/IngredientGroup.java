package cs169.project.thepantry;

import java.util.ArrayList;


public class IngredientGroup {
	
	private String name;
	private ArrayList<IngredientChild> children;
	
	public IngredientGroup(String name, ArrayList<IngredientChild> children) {
		this.name = name;
		this.children = children;
	}
	
	public String getGroup() {
		return name;
	}
	
	public ArrayList<IngredientChild> getChildren() {
		return children;
	}
	
	public void setGroup(String name) {
		this.name = name;
	}
	
	public void setChildren(ArrayList<IngredientChild> children) {
		this.children = children;
	}
	
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof IngredientGroup) {
			result = name.equals(((IngredientGroup)o).getGroup());
		}else if (o instanceof String) {
			result = name.equals((String) o);
		}
		return result;
	}

}
