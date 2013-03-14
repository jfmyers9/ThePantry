package cs169.project.thepantry;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import org.apache.commons.lang3.StringEscapeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* The Storage class stores object representations of data that we get from the Yummly API.
 * It parses different attributes of the JSON response and puts them into fields of the object.
 * Right now these extend Storage: Recipe, Attribution, RecipeImages, RecipeSource, SearchResult, SearchMatch
 */
class Storage implements Serializable {
	private static final long serialVersionUID = 0L; //change this each version to remain consistent
}

/* Recipe keeps track of recipe info. There is a lot more we can take from the Yummly response, such as nutritional info, flavors,
 * cuisine, etc. For now it stores the basic info we need to display the recipe in RecipeActivity.
 */
class Recipe extends Storage implements Serializable {
	String id;
	String name;
	Attribution attribution;
	ArrayList<String> ingredientLines; //in order
	RecipeImages images; //not always present
	RecipeSource source; //not always present

	protected Recipe(JSONObject results) {
		// images and source not necessarily included
		try {
			this.images = new RecipeImages((JSONObject)((JSONArray)results.get("images")).get(0));
			this.source = new RecipeSource((JSONObject)results.get("source"));
		} catch (JSONException e) {}
		
		// id, name, attribution, ingredientLines always present in response
		try {
			this.id = results.getString("id");
			this.name = StringEscapeUtils.unescapeHtml4(results.getString("name"));
			this.attribution = new Attribution((JSONObject)results.get("attribution"));
			JSONArray ings = results.getJSONArray("ingredientLines");
			ingredientLines = new ArrayList<String>();
			if (ings != null){
				for (int i=0;i<ings.length();i++){ 
					ingredientLines.add(StringEscapeUtils.unescapeHtml4(ings.getString(i))); 
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/* Attribution stores the stuff we are required to display on RecipeActivity because we are using the free version of Yummly.
 */
class Attribution extends Storage implements Serializable {
	String url;
	String text;
	String logo;
	
	protected Attribution(JSONObject att) {
		try {
			this.url = att.getString("url");
			this.text = StringEscapeUtils.unescapeHtml4(att.getString("text"));
			this.logo = att.getString("logo");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/* RecipeImages stores the images associated with a recipe.
 */
class RecipeImages extends Storage implements Serializable {
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
class RecipeSource extends Storage implements Serializable {
	String sourceRecipeUrl;
	String sourceSiteUrl;
	String sourceDisplayName;
	
	protected RecipeSource(JSONObject src) {
		try {
			this.sourceRecipeUrl = src.getString("sourceRecipeUrl");
			this.sourceSiteUrl = src.getString("sourceSiteUrl");
			this.sourceDisplayName = StringEscapeUtils.unescapeHtml4(src.getString("sourceDisplayName"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

/* SearchResult stores the info that is received when a search is made. It is the info that will be displayed
 * on SearchResultsActivity. It contains an array of all the recipes (SearchMatches) matched by the search.
 */
class SearchResult extends Storage implements Serializable {
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
class SearchMatch extends Storage implements Serializable {
	String id;
	String name;
	ArrayList<String> ingredients;
	String smallImageUrl; //not always present
	
	protected SearchMatch(JSONObject info) {
		// smallImageUrl not always present
		try {
			this.smallImageUrl = info.getJSONArray("smallImageUrls").getString(0);
		} catch (JSONException e) {}
		
		// id, name, ingredients always present in response
		try {
			this.id = info.getString("id");
			this.name = StringEscapeUtils.unescapeHtml4(info.getString("recipeName"));
			JSONArray ings = info.getJSONArray("ingredients");
			ingredients = new ArrayList<String>();
			if (ings != null){
				for (int i=0;i<ings.length();i++){ 
					ingredients.add(StringEscapeUtils.unescapeHtml4(ings.getString(i))); 
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}