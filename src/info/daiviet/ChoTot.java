package info.daiviet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Helper;
import utils.MySQLConnUtils;

public class ChoTot implements Runnable{
	public static int page_number = 1;
	public static int ofset = 0;
	public void run() {
		try {
			while (true) {
				System.out.println(".chotot.com");
				processPostChoTot(
						"https://gateway.chotot.com/v1/public/ad-listing?region=12&cg=1000&w=1&limit=20&o=0&st=s,k&page=1");
				Thread.sleep(30000);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Chạy nhanh lên nào
	public static void main(String[] args) {

		
		processPostChoTot(
				"https://gateway.chotot.com/v1/public/ad-listing?region=12&cg=1000&w=1&limit=20&o=0&st=s,k&page=1");
	}

	public static void processPostChoTot(String url_json)  {
		String json;
		try {
			json = Helper.readUrl(url_json);
			JSONObject obj = new JSONObject(json);
			boolean next_page = false;
			
			JSONArray arr = obj.getJSONArray("ads");
			for (int i = 0; i < arr.length(); i++)
			{
			    int list_id = arr.getJSONObject(i).getInt("list_id");
			    JSONObject objdeatil = getDeatil(list_id);
			    JSONObject deatil =  objdeatil.getJSONObject("ad");
			    String strhead = deatil.get("subject").toString().trim();
			    String md5header = Helper.getMD5(strhead);
			    
			    int timestamp_post = (int) (deatil.getLong("list_time") / 1000L);
				// deatime nows
				int timestamp_nows = Helper.getTimestamp(true, null, 0);
				int check_tmepost = Helper.checkDatePostWithDateToday(timestamp_post);
				if (check_tmepost != 0) {
					next_page = false;
					page_number = 1;
					ofset = 0;
					break;
				} else {
					next_page = true;
				}
				if (MySQLConnUtils.checkTitleExit(md5header)) {
					continue;
				}
				String str_price ="";
				if(deatil.has("price_string")) {
					str_price = deatil.getString("price_string");
				}
				String str_full_name = deatil.getString("account_name");
				String str_phone ="";
				if(deatil.has("price_string")) {
					str_phone = deatil.getString("phone");
				}
				String addresspost = "";
				if(deatil.has("address")) {
					addresspost = deatil.getString("address");
				}
				String str_detail = deatil.getString("body");
				String header_link = "https://gateway.chotot.com/v1/public/ad-listing/" + list_id;

				MySQLConnUtils.insertTableNews(str_full_name, str_price, str_phone, strhead, str_detail, "Hà nội",
						addresspost, timestamp_post, header_link, 4, timestamp_nows, md5header);
			}
			if (next_page) {
				page_number = page_number + 1;
				ofset = ofset + 20;
				System.out.println("chotot page_number"+page_number);
				processPostChoTot("https://gateway.chotot.com/v1/public/ad-listing?region=12&cg=1000&w=1&limit=20&o="+ofset+"&st=s,k&page=" + page_number);
			} else {
				ofset = 0;
				page_number = 1;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Helper.writeLog4j(e.toString());
			e.printStackTrace();
		}
		
	}

	public static JSONObject getDeatil(int id_list){
		String json;
		JSONObject obj = null;
		try {
			json = Helper.readUrl("https://gateway.chotot.com/v1/public/ad-listing/" + id_list);
			obj = new JSONObject(json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Helper.writeLog4j(e.toString());
			e.printStackTrace();
		}
		return obj;
	}

}