package cs169.project.thepantry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngredientParser {
	
	/* Parse returns a list of 3 strings: number, measurement, additional info, ingredient */
	public static String[] parse(String ingredientLine) {
		
		//match number, followed by measurement, ignore anything in parenths, match the ingredient, not after comma
		String measRegex = "(boxes|box|bunches|bunch|cans?|cloves?|cups?|handfuls?|heads?|ounces?|ozs?|ozs?\\.|packages?|pounds?|slices?|sprigs?|sticks?|strips?|teaspoons?|tablespoons?|tsps?|tsps?\\.|tbsps?|tbsps?\\.)?";
		String ingredRegex = "([^[a-zA-Z]]+)? ?" + measRegex + " ?(\\(.*\\))? ?([a-zA-Z ]+)";
		
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
	
