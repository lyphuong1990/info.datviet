package info.daiviet;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.Helper;
import utils.MySQLConnUtils;

public class BatDongSanJSon implements Runnable {
	public static int page_number = 1;

	@Override
	public void run() {
		try {
			while (true) {
				System.out.println("batdongsan.com.vn");
				String url = "http://apimap.batdongsan.com.vn/api/p_sync?ptype=38&cate=0&city=HN&dist=0&maxarea=0&minarea=0&maxprice=0&minprice=0&ward=0";
				url += "&street=-1&room=-1&direct=-1&projectid=-1&sort=1&searchType=1&client=android&m=list&pagesize=20&page=1";
				processPostBDSJS(url);
				Thread.sleep(30000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Chạy nhanh lên nào
	public static void main(String[] args) {
		// String url =
		// "http://apimap.batdongsan.com.vn/api/p_sync?ptype=38&cate=0&city=HN&dist=0&maxarea=0&minarea=0&maxprice=0&minprice=0&ward=0";
		// url +=
		// "&street=-1&room=-1&direct=-1&projectid=-1&sort=1&searchType=1&client=android&m=list&pagesize=20&page=1";
		// processPostBDSJS(url);
		BatDongSanJSon t = new BatDongSanJSon();
		Thread a = new Thread(t);
		a.start();
	}

	public static void processPostBDSJS(String url_json) {
		String json;
		try {
			json = Helper.readUrl(url_json);
			json = Helper.decodeBase64(json);
			JSONObject obj = new JSONObject(json);
			boolean next_page = false;

			JSONArray arr = obj.getJSONArray("data");
			for (int i = 0; i < arr.length(); i++) {
				int product_id = arr.getJSONObject(i).getInt("id");

				JSONArray arrdeatil = getDeatil(product_id);
				if (arrdeatil != null) {
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
							continue;
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

						MySQLConnUtils.insertTableNews(str_full_name, str_price, str_phone, strhead, str_detail,
								"Hà nội", addresspost, timestamp_post, header_link, 2, timestamp_nows, md5header);
					}
				}
			}
			if (next_page) {
				page_number = page_number + 1;
				System.out.println("batdongsan+page_number" + page_number);
				if (page_number > 100) {
					return;
				}
				String url = "http://apimap.batdongsan.com.vn/api/p_sync?ptype=38&cate=0&city=HN&dist=0&maxarea=0&minarea=0&maxprice=0&minprice=0&ward=0";
				url += "&street=-1&room=-1&direct=-1&projectid=-1&sort=1&searchType=1&client=android&m=list&pagesize=20&page=";
				processPostBDSJS(url + page_number);
			} else {
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
			System.out.println(
					"https://www.batdongsan.com.vn/HandlerWeb/APIAccountHandler.ashx?type=ProductDetail&productId="
							+ productId);
			json = Helper.readUrl(
					"https://www.batdongsan.com.vn/HandlerWeb/APIAccountHandler.ashx?type=ProductDetail&productId="
							+ productId);
			jsonarray = new JSONArray(json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Helper.writeLog4j(
					"https://www.batdongsan.com.vn/HandlerWeb/APIAccountHandler.ashx?type=ProductDetail&productId="
							+ productId);
			Helper.writeLog4j(e.toString());
			return jsonarray;
		}
		return jsonarray;
	}

}