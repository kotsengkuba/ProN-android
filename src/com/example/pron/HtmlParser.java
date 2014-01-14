package com.example.pron;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class HtmlParser {
	public HtmlParser(){
		String html = "<p>An <a href='http://example.com/'><b>example</b></a> link.</p>";
		Document doc = Jsoup.parse(html);
		Element link = doc.select("a").first();

		String text = doc.body().text(); // "An example link"
		String linkHref = link.attr("href"); // "http://example.com/"
		String linkText = link.text(); // "example""

		String linkOuterH = link.outerHtml(); 
		    // "<a href="http://example.com"><b>example</b></a>"
		String linkInnerH = link.html(); // "<b>example</b>"
		
		//Log.d("jsoup", text);
	}
	
	public JSONArray toFourDayJSON(String html, String[] labels){
		Document doc = Jsoup.parse(html);
		JSONArray dates = new JSONArray();
		
		try{
			Elements tables = doc.select("table");
			Log.d("jsoup", "parsing html table: " + tables.size());
			for(Element table : tables){
				Elements rows = table.select("tr");
				
				JSONObject date_item = new JSONObject();	
				JSONArray row_json = new JSONArray();
				
				for(Element row : rows){
					Elements data = row.select("td");
					if(!data.isEmpty()){
						JSONObject details = new JSONObject();
						
						for(Element dataItem : data){							
							details.put(labels[data.indexOf(dataItem)], dataItem.text());
						}
						row_json.put(details);
					}
				}
				date_item.put("data", new JSONArray(row_json.toString()));
				date_item.put("date", table.previousElementSibling().text());
				dates.put(new JSONObject(date_item.toString()));
			}
			
		}catch (JSONException e){
			e.printStackTrace();
		}
		
		//Log.d("jsoup", s);
		//return s;
		
		return dates;
	}
	
	public JSONArray toRainChanceJSON(String html){
		JSONArray times = new JSONArray();
		
		return times;
	}
}
