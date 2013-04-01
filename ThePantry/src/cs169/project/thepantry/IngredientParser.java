package cs169.project.thepantry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngredientParser {
	
	/* Parse returns a list of 3 strings: number, measurement, additional info, ingredient */
	public static String[] parse(String ingredientLine) {
		
		//match number?, ignore weird pre-measurements?, match measurement, ignore anything in parenths?, match the ingredient but not a comma
		//measurement and ingredient groups are required
		String measRegex = "(boxes|box|bunches|bunch|cans?|cloves?|cups?|handfuls?|heads?|ounces?|ozs?|ozs?\\.|packages?|pounds?|slices?|sprigs?|sticks?|strips?|teaspoons?|tablespoons?|tsps?|tsps?\\.|tbsps?|tbsps?\\.)";
		String ingredRegex = "([^[a-zA-Z]]+)? ?(\\w*?)? ?" + measRegex + " ?(\\(.*\\))? ?([a-zA-Z ]+)";
		
		String number = "";
		String measurement = "";
		String additional = "";
		String ingredient = "";
		
		Pattern p = Pattern.compile(ingredRegex);
	    Matcher m = p.matcher(ingredientLine);
	    if (m.find()) {
	    	if (m.group(1) != null ) {
	    		number = m.group(1); }
	    	if (m.group(2) != null ) {
	    		additional = m.group(2) + " "; }
	    	if (m.group(3) != null ) {
	    		measurement = m.group(3); }
	    	if (m.group(4) != null ) {
	    		additional += m.group(4); }
	    	if (m.group(5) != null ) {
	    		ingredient = m.group(5); }
	    }
	    
	    // if there was no measurement matched, it won't match
	    // match things like "4 pumpkins"
	    else {
	    	ingredRegex = "([^[a-zA-Z]]+)? ?([a-zA-Z ]+)";
	    	p = Pattern.compile(ingredRegex);
		    m = p.matcher(ingredientLine);
		    if (m.find()) {
		    	if (m.group(1) != null ) {
		    		number = m.group(1); }
		    	if (m.group(2) != null ) {
		    		ingredient = m.group(2); }
		    }
	    }
	    
	    String[] parsed = {number, measurement, additional, ingredient};
	    return parsed;
	}
}
	
