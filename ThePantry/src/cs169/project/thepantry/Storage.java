package cs169.project.thepantry;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* The Storage class stores object representations of data that we get from the Yummly API.
 * It parses different attributes of the JSON response and puts them into fields of the object.
 * Right now these extend Storage: Recipe, Attribution, RecipeImages, RecipeSource, SearchResult, SearchMatch
 */
class Storage {
	
}

/* Recipe keeps track of recipe info. There is a lot more we can take from the Yummly response, such as nutritional info, flavors,
 * cuisine, etc. For now it stores the basic info we need to display the recipe in RecipeActivity.
 */
class Recipe extends Storage {
	String id;
	String name;
	Attribution attribution;
	ArrayList<String> ingredientLines; //in order
	RecipeImages images; //not always present
	RecipeSource source; //not always present

	protected Recipe(JSONObject results) {
		try {
			this.id = results.getString("id");
			this.name = results.getString("name");
			this.images = new RecipeImages((JSONObject)((JSONArray)results.get("images")).get(0));
			this.source = new RecipeSource((JSONObject)results.get("source"));
			this.attribution = new Attribution((JSONObject)results.get("attribution"));
			JSONArray ings = results.getJSONArray("ingredientLines");
			ingredientLines = new ArrayList<String>();
			if (ings != null){
				for (int i=0;i<ings.length();i++){ 
					ingredientLines.add(ings.getString(i)); 
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/* Attribution stores the stuff we are required to display on RecipeActivity because we are using the free version of Yummly.
 */
class Attribution extends Storage {
	String url;
	String text;
	String logo;
	
	protected Attribution(JSONObject att) {
		try {
			this.url = att.getString("url");
			this.text = att.getString("text");
			this.logo = att.getString("logo");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/* RecipeImages stores the images associated with a recipe.
 */
class RecipeImages extends Storage {
	String hostedLargeUrl;
	String hostedSmallUrl;
	
	protected RecipeImages(JSONObject ims) {
		try {
			this.hostedLargeUrl = ims.getString("hostedLargeUrl");
			this.hostedSmallUrl = ims.getString("hostedSmallUrl");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/* RecipeSource stores the info to link to the recipe online.
 */
class RecipeSource extends Storage {
	String sourceRecipeUrl;
	String sourceSiteUrl;
	String sourceDisplayName;
	
	protected RecipeSource(JSONObject src) {
		try {
			this.sourceRecipeUrl = src.getString("sourceRecipeUrl");
			this.sourceSiteUrl = src.getString("sourceSiteUrl");
			this.sourceDisplayName = src.getString("sourceDisplayName");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/* SearchResult stores the info that is received when a search is made. It is the info that will be displayed
 * on SearchResultsActivity. It contains an array of all the recipes (SearchMatches) matched by the search.
 */
class SearchResult extends Storage {
	ArrayList<SearchMatch> matches;
	Attribution attribution;
	
	protected SearchResult(JSONObject results) {
		try {
			this.attribution = new Attribution((JSONObject)results.get("attribution"));
			JSONArray mtchs = results.getJSONArray("matches");
			matches = new ArrayList<SearchMatch>();
			if (mtchs != null){
				for (int i=0;i<mtchs.length();i++){ 
					matches.add(new SearchMatch((JSONObject)mtchs.get(i))); 
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/* SearchMatch stores all the info that will be displayed for each recipe on SearchResultsActivity.
 */
class SearchMatch extends Storage {
	String id;
	String name;
	ArrayList<String> ingredients;
	String smallImageUrl; //not always present
	
	protected SearchMatch(JSONObject info) {
		try {
			this.id = info.getString("id");
			this.name = info.getString("recipeName");
			//this.smallImageUrl = info.getJSONArray("smallImageUrl").getString(0);
			JSONArray ings = info.getJSONArray("ingredients");
			ingredients = new ArrayList<String>();
			if (ings != null){
				for (int i=0;i<ings.length();i++){ 
					ingredients.add(ings.getString(i)); 
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}