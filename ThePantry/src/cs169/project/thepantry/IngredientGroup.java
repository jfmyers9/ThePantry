package cs169.project.thepantry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;


public class IngredientGroup {
	
	private String name;
	private ArrayList<IngredientChild> children;
	
	public IngredientGroup(String name, ArrayList<IngredientChild> children) {
		this.name = name;
		this.children = children;
		Collections.sort(this.children, ALPHABETICAL_ORDER);
	}
	
	private static Comparator<IngredientChild> ALPHABETICAL_ORDER = new Comparator<IngredientChild>() {
	    public int compare(IngredientChild child1, IngredientChild child2) {
	    	String str1 = child1.getName();
	    	String str2 = child2.getName();
	        int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
	        if (res == 0) {
	            res = str1.compareTo(str2);
	        }
	        return res;
	    }
	};
	
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
		Collections.sort(this.children, ALPHABETICAL_ORDER);
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
