package info.daiviet;

import java.util.Iterator;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.Helper;
import utils.MySQLConnUtils;

public class MuaBanNet implements Runnable {

	public void run() {
		try {
			processPostMuaBan("https://muaban.net/mua-ban-nha-dat-cho-thue-ha-noi-l24-c3?sort=1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int page = 1;

	public static void main(String[] args) {
		processPostMuaBan("https://muaban.net/mua-ban-nha-dat-cho-thue-ha-noi-l24-c3?sort=1");
	}

	public static void processPostMuaBan(String url) {
		Connection conn = Jsoup.connect(url);
		try {
			boolean next_page = false;
			Document doc = conn.get();

			Element result_block = doc.select("div.mbn-body.container").first();
			Elements contens = result_block.select(".mbn-box-list .mbn-box-list-content");

			Iterator<Element> iterator = contens.iterator();

			while (iterator.hasNext()) {
				Element item = iterator.next();
				// get header
				Elements header = item.select(".mbn-content .mbn-title");
				String strhead = header.text().trim();

				// md5 header
				String md5header = Helper.getMD5(strhead);

				// date conten
				Elements post_conten_date = item.select(".mbn-content .mbn-date");
				String str_date = post_conten_date.text().trim();

				int timestamp_post = Helper.getTimestamPost(str_date);

				// deatime nows
				int timestamp_nows = Helper.getTimestamp(true, null, 0);
				// check timepost
				int check_tmepost = Helper.checkDatePostWithDateToday(timestamp_post);

				if (check_tmepost != 0) {
					next_page = false;
					page = 1;
					break;
				} else {
					next_page = true;
				}
				if (MySQLConnUtils.checkTitleExit(md5header)) {
					continue;
				}

				// link detal
				Element header_link_el = item.select("a.mbn-image").first();
				String header_link = header_link_el.attr("href");

				// String urlhead = header

				Connection connDetail = Jsoup.connect(header_link);
				Document docDetail = connDetail.get();

				Element result_detail = docDetail.select(".container.clearfix").first();
				Elements contens_result_detail = result_detail.select("#dvContent");

				// contacts info
				Elements contacts_info_element = contens_result_detail.select(".ct-contact");
				Element full_name_el = contacts_info_element.select(".clearfix .contact-name").first();
				String str_full_name = "";
				if(full_name_el != null) {
					str_full_name = full_name_el.text().trim();
				}

				// Phone
				Element phone_el = contacts_info_element.select(".clearfix .contact-mobile").first();
				String str_phone = "";
				if (phone_el != null) {
					str_phone = phone_el.text().trim();
				}
				if (str_phone == "") {
					continue;
				}

				// address
				Elements address = contens_result_detail.select(".cl-price-sm .visible-lg:eq(1)");
				String addresspost = address.text().trim();

				// Price
				Elements price_element = contens_result_detail.select(".ct-price .price-value");
				String str_price = price_element.text().trim();

				// Elements detail_info_element = result_detail.select("#product-detail
				// #photoSlide #divPhotoActive > div > img:eq(0)");
				// String link_image = detail_info_element.attr("src");

				// Elements detail_acreage = result_detail.select("#product-detail .kqchitiet
				// .gia-title:eq(1)");
				// String str_acreage = detail_acreage.text().trim();
				//
				// detail
				Elements contens_detail = contens_result_detail.select(".ct-body.overflow");
				String str_detail = contens_detail.html();

				MySQLConnUtils.insertTableNews(str_full_name, str_price, str_phone, strhead, str_detail, "Hà nội",
						addresspost, timestamp_post, header_link, 6, timestamp_nows, md5header);
			}
			if (next_page) {
				page = page + 1;
				processPostMuaBan("https://muaban.net/mua-ban-nha-dat-cho-thue-ha-noi-l24-c3?sort=1&cp=" + page);
			} else {
				page = 1;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Helper.writeLog4j(e.toString());
			e.printStackTrace();
		}

	}

}
