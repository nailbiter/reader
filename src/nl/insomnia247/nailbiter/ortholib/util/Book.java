package nl.insomnia247.nailbiter.ortholib.util;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONStringer;
import org.json.JSONException;

public class Book{
	public String title;
	public String pdf_link;
	public String id;
	public String category;
	public JSONObject _obj;//FIXME
	public Book(JSONObject obj)throws Exception{
		title=obj.getString("title");
		id=obj.getString("_id");
		pdf_link=obj.getJSONObject("formats").getString("pdf");
		category=obj.getString("category");
		_obj=obj;
	};
}
