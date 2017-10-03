package info.daiviet;

import java.util.Iterator;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.Helper;
import utils.MySQLConnUtils;

public class MuaBanNhaDat implements Runnable{
	static String urlLInk = "http://www.muabannhadat.vn/nha-dat-3490/tp-ha-noi-s28?sf=dpo&so=d";
	static String urlNext = "http://www.muabannhadat.vn/nha-dat-3490/tp-ha-noi-s28?sf=dpo&so=d&p=";
	static int pageCount = 1;
	public void run() {
		try {
			processPost(urlLInk);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void processPost(String urlLInk) {
		boolean isNextPage = false;
		try {
//			System.err.println(urlLInk);
			Document doc = Jsoup.connect(urlLInk).get();
			Element result_block = doc.select("div.list-group.result-list").first();
			Elements data = result_block.select(".resultItem");
			Iterator<Element> iterator = data.iterator();
			while (iterator.hasNext()) {
				Element item = iterator.next();
				Elements header = item.select(".title-filter-link");
				// get title
				String title = header.text().trim();
				// md5 header
				String md5header = Helper.getMD5(title);
				// get time:
				Elements time_element = item.select(".col-lg-4.lline.hidden-xs");
				String str_time = time_element.text().trim();
				int timestamp_post = Helper.getTimestamPost(str_time);
				String str_date = str_time.substring(str_time.indexOf(":") + 1, str_time.length());
				int timestamp_nows = Helper.getTimestamp(true,null,0);
				str_date = str_date.trim();
				// check timepost
				if (!str_date.equals(Helper.getTimesNow())) {
					isNextPage = false;
					return;
				}else {
					isNextPage = true;
				}
				if (MySQLConnUtils.checkTitleExit(md5header)) {
					return;
				}
				// get price
				Elements price_element = item.select(".col-md-3.text-right.listing-price");
				String str_price = price_element.text().trim();
				// get add
				Elements add_element = item.select(".col-md-9.col-xs-6");
				Elements trousers_element = item.select(".col-md-3.col-xs-6.text-right");
				String str_add = add_element.text().trim() + " " + trousers_element.text().trim();
				// get description
				// get link
				String header_link = "http://www.muabannhadat.vn" + header.attr("href");
				Connection connDetail = Jsoup.connect(header_link);
				Document docDetail = connDetail.get();

				Elements contens_detail = docDetail.select("#Description");
				String str_description = contens_detail.text().trim();
				
				Elements fullName_detail = docDetail.select(".col-xs-12.name-contact");
				String fullName = fullName_detail.text().trim();
				
				String phone = getPhone(getId(header_link));
				if (phone == null ) {
					return;
				}
//				System.err.println(phone);
				MySQLConnUtils.insertTableNews(fullName, str_price, phone, title, str_description, "Ha noi",
						str_add, timestamp_post, header_link, 3, timestamp_nows, md5header);
				// conect database
			}

			if (isNextPage) {
				pageCount ++;
				String url = urlNext + pageCount;
				processPost(url);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	public static String getId(String link) {
		return link.substring(link.lastIndexOf("-") + 1);
	}

	public static String getPhone(String id) {
		String phone = null;
		 try {
			 String url = "http://www.muabannhadat.vn/Services/Tracking/a" + id + "/GetPhoneCustom";
			 Document response = Jsoup.connect(url).header("Referer", url).header("Content-Type", "text/*").post();
			 phone =  response.body().text().replaceAll("\"", "");
		} catch (Exception e) { 
			System.err.println("Loi: " + e.getMessage());
		}
		return phone;
	}


}
