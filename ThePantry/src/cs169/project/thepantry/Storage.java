package cs169.project.thepantry;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import org.apache.commons.lang3.StringEscapeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** The Storage class stores object representations of data that we get from the Yummly API.
 * It parses different attributes of the JSON response and puts them into fields of the object.
 * Right now these extend Storage: Recipe, Attribution, RecipeImages, RecipeSource, SearchResult, SearchMatch
 */
public class Storage implements Serializable {
	private static final long serialVersionUID = 0L; //change this each version to remain consistent
	
	public static Recipe recipe;
	public static Attribution att;
	public static RecipeImages imgs;
	
	// for testing:
	public static SearchCriteria makeSC(String s, String q, int m, int sk) {
		return new SearchCriteria(s,q,m,sk);
	}
	
	public static SearchCriteria makeSC(String s, String q, int m) {
		return new SearchCriteria(s,q,m);
	}
	
	public static SearchCriteria makeSC(String s, String q) {
		return new SearchCriteria(s,q);
	}
	
	public static void makeRecipe(JSONObject res) {
		recipe = new Recipe(res);
	}
	
	public String getRecipeId() {
		return recipe.id;
	}
	
	public String getRecipeName() {
		return recipe.name;
	}
	
	public Attribution getRecipeAtt() {
		return recipe.attribution;
	}
	
	public ArrayList<String> getRecipeIngLines() {
		return recipe.ingredientLines;
	}
	
	public void makeAtt(JSONObject obj) {
		att = new Attribution(obj);
	}
	
	public String getAttLogo() {
		return att.logo;
	}
	
	public String getAttUrl() {
		return att.url;
	}
	
	public String getAttText() {
		return att.text;
	}
	
	public void makeImg(JSONObject obj) {
		imgs = new RecipeImages(obj);
	}
	
	public String getImgSUrl() {
		return imgs.hostedSmallUrl;
	}
	
	public String getImgLUrl() {
		return imgs.hostedLargeUrl;
	}
}

/** Recipe keeps track of recipe info. There is a lot more we can take from the Yummly response, such as nutritional info, flavors,
 * cuisine, etc. For now it stores the basic info we need to display the recipe in RecipeActivity.
 * Call the constructor using the JSON response returned by Yummly after a request for a certain recipe.
 */
class Recipe extends Storage implements Serializable {
	String id;
	String name;
	Boolean cooked; // can probably take out, leave until caching works properly
	Boolean favorite; // can probably take out, leave until caching works properly
	Attribution attribution;
	ArrayList<String> ingredientLines; //in order
	RecipeImages images; //not always present
	RecipeSource source; //not always present

	Recipe() {
		cooked=false;
		favorite=false;
	}
	
	protected Recipe(JSONObject results) {
		// images and source not necessarily included
		cooked=false;
		favorite=false;
		try {
			this.images = new RecipeImages((JSONObject)((JSONArray)results.get("images")).get(0));
		} catch (JSONException e) {}
		try {
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
	
	public String getId() {
		return id;
	}
}

/** Attribution stores the stuff we are required to display on RecipeActivity because we are using the free version of Yummly.
 */
class Attribution extends Storage implements Serializable {
	String url;
	String text;
	String logo;
	
	Attribution (String url, String text, String logo) {
		this.url = url;
		this.text = text;
		this.logo = logo;
	}
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

/** RecipeImages stores the images associated with a recipe.
 * A recipe may have a small image and a large image associated with it but may not. Check if these are null.
 */
class RecipeImages extends Storage implements Serializable {
	String hostedLargeUrl;
	String hostedSmallUrl;
	
	RecipeImages(String lrgUrl, String smlUrl) {
		hostedLargeUrl = lrgUrl;
		hostedSmallUrl = smlUrl;
	}
	
	protected RecipeImages(JSONObject ims) {
		try {
			this.hostedLargeUrl = ims.getString("hostedLargeUrl");
		} catch (JSONException e) {}
		try {
			this.hostedSmallUrl = ims.getString("hostedSmallUrl");
		} catch (JSONException e) {}
	}
}

/** RecipeSource stores the info to link to the recipe online.
 * The URL to the recipe, the URL to the source site, and the site display name may be accessible. Check if they are null.
 */
class RecipeSource extends Storage implements Serializable {
	String sourceRecipeUrl;
	String sourceSiteUrl;
	String sourceDisplayName;
	
	RecipeSource(String recipeUrl, String siteUrl, String name) {
		sourceRecipeUrl = recipeUrl;
		sourceSiteUrl = siteUrl;
		sourceDisplayName = name;
	}
	
	protected RecipeSource(JSONObject src) {
		try {
			this.sourceRecipeUrl = src.getString("sourceRecipeUrl");
		} catch (Exception e) {}
		try {
			this.sourceSiteUrl = src.getString("sourceSiteUrl");
		} catch (Exception e) {}
		try {
			this.sourceDisplayName = StringEscapeUtils.unescapeHtml4(src.getString("sourceDisplayName"));
		} catch (Exception e) {}
	}
}

/** SearchResult stores the info that is received when a search is made. It is the info that will be displayed
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

/** SearchMatch stores all the info that will be displayed for each recipe on SearchResultsActivity.
 */
class SearchMatch extends Storage implements Serializable {
	String id;
	String name;
	ArrayList<String> ingredients;
	String smallImageUrl; //not always present
	String sourceDisplayName; //not always present but defaults to Unknown
	
	SearchMatch(){}
	protected SearchMatch(JSONObject info) {
		// smallImageUrl not always present
		try {
			this.smallImageUrl = info.getJSONArray("smallImageUrls").getString(0);
		} catch (JSONException e) {}
		
		// sourceDisplayName not always present, default to Unknown
		try {
			this.sourceDisplayName = info.getString("sourceDisplayName");
		} catch (JSONException e) {
			this.sourceDisplayName = "Unknown";
		}
		
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

/** SearchCriteria stores information for a search so we can easily pass # of results, etc.
 * It can be created either with just the type and query, type query and maxresults, or type query maxresults and resultstoskip
*/
class SearchCriteria extends Storage implements Serializable {
	int maxResults = 40;
	int resultsToSkip = 0;
	String q;
	String type;
	
	protected SearchCriteria(String t, String query) {
		this.type = t;
		this.q = query;
	}
	
	protected SearchCriteria(String t, String query, int max) {
		this.type = t;
		this.q = query;
		this.maxResults = max;
	}
	
	protected SearchCriteria(String t, String query, int max, int skip) {
		this.type = t;
		this.q = query;
		this.maxResults = max;
		this.resultsToSkip = skip;
	}
}