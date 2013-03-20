package cs169.project.thepantry.test;

import junit.framework.TestCase;
import cs169.project.thepantry.SearchModel;
import cs169.project.thepantry.Storage;

public class SearchModelTest extends TestCase {

	SearchModel searchmodel;
	Storage result;
	
	protected void setUp() throws Exception {
		searchmodel = new SearchModel();
	}

	protected void tearDown() throws Exception {
		searchmodel = null;
	}

	// test simple search query
	public void testSearchURL1() {
		result = searchmodel.search(Storage.makeSC("search", "beef"));
		assertEquals("Error: incorrect URL encoded for search: onions", searchmodel.getURL, "http://api.yummly.com/v1/api/recipes?q=beef&maxResult=40&start=0");
	}
	
	// test search query with ingredients
	public void testSearchURL2() {
		searchmodel.search(Storage.makeSC("search", "beef, cheese, kale"));
		assertEquals("Error: incorrect URL encoded for search: beef, cheese, kale", searchmodel.getURL, "http://api.yummly.com/v1/api/recipes?q=beef&allowedIngredient%5B%5D=cheese&allowedIngredient%5B%5D=kale&maxResult=40&start=0");
	}
	
	// test search query with ingredients and max results
	public void testSearchURL3() {
		searchmodel.search(Storage.makeSC("search", "beef, oranges", 30));
		assertEquals("Error: incorrect URL encoded for search: beef, oranges, maxResults=30", searchmodel.getURL, "http://api.yummly.com/v1/api/recipes?q=beef&allowedIngredient%5B%5D=oranges&maxResult=30&start=0");
	}
	
	// test search query with ingredients, max results, and skip
	public void testSearchURL4() {
		searchmodel.search(Storage.makeSC("search", "salmon, carrots, cheese, salt", 20, 5));
		assertEquals("Error: incorrect URL encoded for search: salmon, carrots, cheese, salt, maxResults=20, resultsToSkip=5", searchmodel.getURL, "http://api.yummly.com/v1/api/recipes?q=salmon&allowedIngredient%5B%5D=carrots&allowedIngredient%5B%5D=cheese&allowedIngredient%5B%5D=salt&maxResult=20&start=5");
	}
	
	// test home query with ingredients, max results, and skip
	public void testHomeURL() {
		searchmodel.search(Storage.makeSC("home", "salmon, carrots, cheese, salt", 20, 5));
		assertEquals("Error: incorrect URL encoded for home: salmon, carrots, cheese, salt, maxResults=20, resultsToSkip=5", searchmodel.getURL, "http://api.yummly.com/v1/api/recipes?q=salmon&allowedIngredient%5B%5D=carrots&allowedIngredient%5B%5D=cheese&allowedIngredient%5B%5D=salt&maxResult=20&start=5");
	}
	
	// test recipe query with id=Salmon-Chowder-Recipezaar_5
	public void testRecipeUrl() {
		searchmodel.search(Storage.makeSC("recipe", "Salmon-Chowder-Recipezaar_5"));
		assertEquals("Error: incorrect URL encoded for recipe: id=Salmon-Chowder-Recipezaar_5", searchmodel.getURL, "http://api.yummly.com/v1/api/recipe/Salmon-Chowder-Recipezaar_5");
	}

}


