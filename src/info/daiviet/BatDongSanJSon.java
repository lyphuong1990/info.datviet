package info.daiviet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;

import jdk.nashorn.internal.parser.JSONParser;
import utils.Helper;
import utils.MySQLConnUtils;

public class BatDongSanJSon implements Runnable {
	public static int page_number = 1;
	public static int ofset = 0;

	public void run() {
		try {
			String url = "https://www.batdongsan.com.vn/HandlerWeb/APIAccountHandler.ashx?";
			url += "type=LoadProduct&pType=38&cateId=0&cityCode=HN&distId=1&area=-1&price=-1&wardId=-1&streetId=-1&room=-1&direction=-1&projId=-1&sortBy=1&p=1";
			processPostBDSJS(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Chạy nhanh lên nào
	public static void main(String[] args) {

		String url = "https://www.batdongsan.com.vn/HandlerWeb/APIAccountHandler.ashx?";
		url += "type=LoadProduct&pType=38&cateId=0&cityCode=HN&distId=1&area=-1&price=-1&wardId=-1&streetId=-1&room=-1&direction=-1&projId=-1&sortBy=1&p=1";
		processPostBDSJS(url);
	}

	public static void processPostBDSJS(String url_json) {
		String json;
		try {
			json = Helper.readUrl(url_json);
			JSONObject obj = new JSONObject(json);
			boolean next_page = false;

			JSONArray arr = obj.getJSONArray("lst");
			for (int i = 0; i < arr.length(); i++) {
				int product_id = arr.getJSONObject(i).getInt("ProductId");
				System.out.println(product_id);

				JSONArray arrdeatil = getDeatil(product_id);
				for (int j = 0; j < arrdeatil.length(); j++) {
					String strhead = arrdeatil.getJSONObject(j).getString("Title").trim();
					String md5header = Helper.getMD5(strhead);
					String str_date = arrdeatil.getJSONObject(j).getString("StartDate").trim();
					int timestamp_post = Helper.getTimestamPost(str_date);
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
					String str_price = arrdeatil.getJSONObject(j).getString("Price").trim();
					String str_full_name = arrdeatil.getJSONObject(j).getString("ContactName").trim();
					String str_phone = arrdeatil.getJSONObject(j).getString("ContactMobile").trim();

					String addresspost = arrdeatil.getJSONObject(j).getString("Address").trim();
					String str_detail = arrdeatil.getJSONObject(j).getString("Description").trim();
					String header_link = "";

					MySQLConnUtils.insertTableNews(str_full_name, str_price, str_phone, strhead, str_detail, "Hà nội",
							addresspost, timestamp_post, header_link, 2, timestamp_nows, md5header);
				}
			}
			if (next_page) {
				page_number = page_number + 1;
				ofset = ofset + 10;
				System.out.println("page_number" + page_number);

				String url = "https://www.batdongsan.com.vn/HandlerWeb/APIAccountHandler.ashx?";
				url += "type=LoadProduct&pType=38&cateId=0&cityCode=HN&distId=1&area=-1&price=-1&wardId=-1&streetId=-1&room=-1&direction=-1&projId=-1&sortBy=1&p=";

				processPostBDSJS(url + page_number);
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

	public static JSONArray getDeatil(int productId) {
		String json;
		JSONArray jsonarray = null;
		try {
			json = Helper.readUrl(
					"https://www.batdongsan.com.vn/HandlerWeb/APIAccountHandler.ashx?type=ProductDetail&productId="
							+ productId);
			jsonarray = new JSONArray(json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Helper.writeLog4j(e.toString());
			e.printStackTrace();
		}
		return jsonarray;
	}

}