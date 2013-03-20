package cs169.project.thepantry;

import java.net.URL;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DirectionParser {

	public static ArrayList<String> getDirections(String url) {
		try {
			Document doc = Jsoup.connect(url).timeout(10000).get();
			System.out.println("connected");
			// get host site and call correct parser
			String site = new URL(url).getHost();
			System.out.println(site);
			if (site.equals("www.allrecipes.com") || site.equals("allrecipes.com")) {
				return getAllRecipesDirections(doc);
			} else if (site.equals("www.foodrepublic.com") || site.equals("foodrepublic.com")) {
				return getFoodRepublicDirections(doc); 
			} else if (site.equals("www.simplyrecipes.com") || site.equals("simplyrecipes.com")) {
				return getSimplyRecipesDirections(doc); 
			} else if (site.equals("www.foodnetwork.com") || site.equals("foodnetwork.com")) {
				return getFoodNetworkDirections(doc); 
			} else if (site.equals("www.epicurious.com") || site.equals("epicurious.com")) {
				return getEpicuriousDirections(doc); 
			} else if (site.equals("www.hellmanns.com") || site.equals("hellmanns.com")) {
				return getHellmannsDirections(doc); 
			} else if (site.equals("www.seriouseats.com") || site.equals("seriouseats.com")) {
				return getSeriousEatsDirections(doc); 
			} else if (site.equals("www.tasteofhome.com") || site.equals("tasteofhome.com")) {
				return getTasteOfHomeDirections(doc); 
			} else if (site.equals("www.marthastewart.com") || site.equals("marthastewart.com")) {
				return getMarthaStewartDirections(doc); 
			} else if (site.equals("www.food.com") || site.equals("food.com")) {
				return getFoodDirections(doc); 
			} else if (site.equals("www.onceuponachef.com") || site.equals("onceuponachef.com")) {
				return getOnceUponAChefDirections(doc); 
			} else {
				return new ArrayList<String>();
			}
		} catch (Exception e) {
			System.out.println("notconnected");
			e.printStackTrace();
			return new ArrayList<String>();
		}
	}
	
	/* ALLRECIPES.COM */
	private static ArrayList<String> getAllRecipesDirections(Document doc) {
		ArrayList<String> directions = new ArrayList<String>();
		Elements content = doc.getElementsByClass("directions");
		Elements dirs = content.get(0).getElementsByTag("ol").get(0).getElementsByTag("li");
		for (Element dir : dirs) {
			  String dirText = dir.text();
			  directions.add(dirText);
			}
		return directions;
	}
	
	/* FOODREPUBLIC.COM */
	private static ArrayList<String> getFoodRepublicDirections(Document doc) {
		ArrayList<String> directions = new ArrayList<String>();
		Elements content = doc.getElementsByClass("field-field-recipe-directions");
		Elements dirs = content.get(0).getElementsByTag("ol").get(0).getElementsByTag("li");
		for (Element dir : dirs) {
			  String dirText = dir.text();
			  directions.add(dirText);
			}
		return directions;
	}
	
	/* SIMPLYRECIPES.COM */
	//TODO: remove preceding numbers & yield
	private static ArrayList<String> getSimplyRecipesDirections(Document doc) {
		ArrayList<String> directions = new ArrayList<String>();
		Elements content = doc.getElementsByClass("instructions");
		Elements dirs = content.get(0).getElementsByTag("p");
		for (Element dir : dirs) {
			  String dirText = dir.text();
			  directions.add(dirText);
			}
		return directions;
	}
	
	/* FOODNETWORK.COM */
	private static ArrayList<String> getFoodNetworkDirections(Document doc) {
		ArrayList<String> directions = new ArrayList<String>();
		Elements content = doc.getElementsByClass("fn_instructions");
		Elements dirs = content.get(0).getElementsByTag("p");
		for (Element dir : dirs) {
			  String dirText = dir.text();
			  directions.add(dirText);
			}
		return directions;
	}
	
	/* EPICURIOUS.COM */
	// TODO: separate subtitles
	private static ArrayList<String> getEpicuriousDirections(Document doc) {
		ArrayList<String> directions = new ArrayList<String>();
		Elements content = doc.getElementsByClass("instructions");
		Elements dirs = content.get(0).getElementsByTag("p");
		for (Element dir : dirs) {
			  String dirText = dir.text();
			  directions.add(dirText);
			}
		return directions;
	}
	
	/* HELLMANNS.COM */
	// TODO: idk why this one isn't working. not finding <ol class="directions">
	private static ArrayList<String> getHellmannsDirections(Document doc) {
		ArrayList<String> directions = new ArrayList<String>();
		Elements content = doc.getElementsByClass("directions");
		Elements dirs = content.get(0).getElementsByTag("li");
		for (Element dir : dirs) {
			  String dirText = dir.text();
			  directions.add(dirText);
			}
		return directions;
	}
	
	/* SERIOUSEATS.COM */
	// TODO: remove preceding numbers
	private static ArrayList<String> getSeriousEatsDirections(Document doc) {
		ArrayList<String> directions = new ArrayList<String>();
		Elements content = doc.getElementsByClass("instructions");
		Elements dirs = content.get(0).getElementsByTag("li");
		for (Element dir : dirs) {
			  String dirText = dir.text();
			  directions.add(dirText);
			}
		return directions;
	}
	
	/* TASTEOFHOME.COM */
	private static ArrayList<String> getTasteOfHomeDirections(Document doc) {
		ArrayList<String> directions = new ArrayList<String>();
		Elements content = doc.getElementsByClass("directions");
		Elements dirs = content.get(0).getElementsByTag("li");
		for (Element dir : dirs) {
			  String dirText = dir.text();
			  directions.add(dirText);
			}
		return directions;
	}
	
	/* MARTHASTEWART.COM */
	private static ArrayList<String> getMarthaStewartDirections(Document doc) {
		ArrayList<String> directions = new ArrayList<String>();
		Elements content = doc.getElementsByClass("instructions");
		Elements dirs = content.get(0).getElementsByTag("ol").get(0).getElementsByTag("li");
		for (Element dir : dirs) {
			  String dirText = dir.text();
			  directions.add(dirText);
			}
		return directions;
	}
	
	/* FOOD.COM */
	// TODO: remove preceding numbers
	private static ArrayList<String> getFoodDirections(Document doc) {
		ArrayList<String> directions = new ArrayList<String>();
		Elements content = doc.getElementsByClass("directions");
		Elements dirs = content.get(0).getElementsByTag("ol").get(0).getElementsByTag("li");
		for (Element dir : dirs) {
			  String dirText = dir.text();
			  directions.add(dirText);
			}
		return directions;
	}
	
	/* ONCEUPONACHEF.COM */
	// TODO: idk why this isn't working
	private static ArrayList<String> getOnceUponAChefDirections(Document doc) {
		ArrayList<String> directions = new ArrayList<String>();
		Elements content = doc.getElementsByClass("instructions");
		Elements dirs = content.get(0).getElementsByTag("ol").get(0).getElementsByTag("li");
		for (Element dir : dirs) {
			  String dirText = dir.text();
			  directions.add(dirText);
			}
		return directions;
	}
}
