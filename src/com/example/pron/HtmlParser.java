package com.example.pron;

import java.util.StringTokenizer;

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
	}
	
	public JSONArray toFourDayJSON(String html, String[] labels){
		Document doc = Jsoup.parse(html);
		JSONArray dates = new JSONArray();
		
		try{
			Elements tables = doc.select("table");
			//Log.d("jsoup", "Four day: Parsing html table: " + tables.size());
			for(Element table : tables){
				Elements rows = table.select("tr");
				
				JSONObject date_item = new JSONObject();	
				JSONArray row_json = new JSONArray();
				
				for(Element row : rows){
					Elements data = row.select("td");
					if(!data.isEmpty()){
						JSONObject details = new JSONObject();
						
						for(Element dataItem : data){
							Elements img = dataItem.select("img");
							String img_src = null, label = null;
							String[] tokens = null;
							if(img.size() == 0){
								label = labels[data.indexOf(dataItem)];
								if(label.equals("Time")){
									details.put(label, dataItem.text().split("-")[0]);
								}
								else{
									details.put(label, dataItem.text());
								}
							}
							else{
								img_src = img.get(0).attr("src");
								tokens = img_src.split("/");
								//Log.d("jsoup", "img: "+tokens[tokens.length-1]);
								details.put(labels[data.indexOf(dataItem)], tokens[tokens.length-1]);
							}
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
		
		return dates;
	}
	
	public JSONArray toRainChanceJSON(String html, String [] labels){
		Document doc = Jsoup.parse(html);
		JSONArray times = new JSONArray();
		
		try{
			Element table = doc.select("table").first();
			Elements rows = table.select("tr");
			
			for(Element row : rows){
				Elements data = row.select("td");
				data.remove(0);
				if(!data.isEmpty()){
					JSONObject details = new JSONObject();
					
					String str = data.get(0).text();
					String delims = "[ ]";
					String[] tokens = str.split(delims);
					details.put(labels[0], tokens[1]);
					
					str = data.get(1).text();
					delims = "[()]";
					tokens = str.split(delims);
//					Log.d("jsoup", "str: "+str);
					details.put(labels[1], tokens[1]);
					
					//for(Element dataItem : data){							
					//	details.put(labels[data.indexOf(dataItem)], dataItem.text());
					//}
					times.put(details);
				}
			}
			
		}catch (JSONException e){
			e.printStackTrace();
		}
		
		//Log.d("jsoup", times.toString());
		//return s;
		
		return times;
	}
}
