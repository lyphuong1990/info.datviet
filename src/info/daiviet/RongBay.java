package info.daiviet;

import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.Helper;
import utils.MySQLConnUtils;

public class RongBay implements Runnable{
	static String url = "http://rongbay.com/Ha-Noi/Nha-rieng-Mua-Ban-nha-dat-c15-t4.html";
	static String urlPage = "http://rongbay.com/Ha-Noi/Nha-rieng-Mua-Ban-nha-dat-c15-t4-trang";
	static String page = ".html?ft=1";
	static int pageCount = 1;
	public void run() {
		try {
			while (true) {
				System.out.println("rongbay.com");
				processPost(url);
				Thread.sleep(30000);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		
	}

	public static void processPost(String url) {
		try {
//			System.err.println(url);
			boolean isCheckNextPage = false;
			Document doc = Jsoup.connect(url).get();
			Element result_block = doc.select("div.content_bds .list_content_bds").first();
			Elements data = result_block.select(".subCateWork");

			for (int i = 0; i < data.size(); i++) {
				Element test = data.get(i);
				Element header = test.select(".link_l > p > a").first();
				String title = "";
				String link = null;
				if (header != null) {
					title = header.text();
					link = header.attr("href");
				}
				String md5header = Helper.getMD5(title);
				if (!MySQLConnUtils.checkTitleExit(md5header)) {

					Element add_element = test.select(".address_name_store").first();
					String adderees = add_element.text();

					Element price_element = test.select(".huong").first();
					String price = "Thỏa thuận";
					if (price_element != null) {
						price = price_element.text();
					}

					if (link != null) {

						Connection connDetail = Jsoup.connect(link);
						Document docDetail = connDetail.get();

						Elements detail_element = docDetail.select(".info_text");
						String description = detail_element.text();

						Elements time_element = docDetail.select(".color.cl_888.font_13");
						String textTime = time_element.text();
						String time = textTime.substring(0, textTime.indexOf(" ")).trim();
						int timestamp_post = Helper.getTimestamPost(time);
						if (!time.equals(Helper.getTimesNow())) {
							isCheckNextPage = false;
							continue;
						} else {
							isCheckNextPage = true;
						}

						Element phone_element = docDetail.select("#mobile_show").first();

						Elements fullName_element = docDetail.select(".name_store.icon_bds");
						String fullName = fullName_element.text();

						if (phone_element != null) {
							String phone = phone_element.text();
//							System.err.println(phone);
							MySQLConnUtils.insertTableNews(fullName, price, phone, title, description, "Ha noi",
									adderees, timestamp_post, link, 5, Helper.getTimestamp(true, null, 0), md5header);
						}
					}
				}
			}

			if (isCheckNextPage) {
				pageCount++;
				System.out.println("rongbay.com +page" + pageCount);
				String urlNext = urlPage + pageCount + page;
				processPost(urlNext);
			}
		} catch (Exception e) {
			Helper.writeLog4j(e.toString());
			e.printStackTrace();
			// TODO: handle exception
		}
	}

}
