package cs169.project.thepantry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngredientParser {
	
	/* Parse returns a list of 3 strings: number, measurement, additional info, ingredient */
	public static String[] parse(String ingredientLine) {
		
		//match number, followed by measurement, ignore anything in parenths, match the ingredient, not after comma
		String measRegex = "(teaspoon|tablespoon|pounds)?";
		String ingredRegex = "([^[a-zA-Z]]+)? " + measRegex + " (\\(.*\\))? ([a-zA-Z\\s]+)";
		
		String number = "";
		String measurement = "";
		String additional = "";
		String ingredient = "";
		
		Pattern p = Pattern.compile(ingredRegex);
	    Matcher m = p.matcher(ingredientLine);
	    if (m.find()) {
	        number = m.group(1);
	        measurement = m.group(2);
	        additional = m.group(3);
	        ingredient = m.group(4);
	    }
	    
	    String[] parsed = {number, measurement, additional, ingredient};
	    return parsed;
	}
}
	
