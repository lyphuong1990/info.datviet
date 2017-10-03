package info.daiviet;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.Helper;
import utils.MySQLConnUtils;

public class DiaOcOnline implements Runnable{

	public static int page = 1;
	public void run() {
		try {
			processPost("http://diaoconline.vn/sieu-thi/loc/?tindang=1&tp=2");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, ParseException {
		processPost("http://diaoconline.vn/sieu-thi/loc/?tindang=1&tp=2");
	}

	public static void processPost(String url) throws ClassNotFoundException, SQLException, ParseException {
		Connection conn = Jsoup.connect(url);
		try {
			boolean next_page = false;
			Document doc = conn.get();

			Element result_block = doc.select("div#result_block").first();
			Elements contens = result_block.select(".hightlight_type_1.margin_bottom");

			Iterator<Element> iterator = contens.iterator();

			while (iterator.hasNext()) {
				Element item = iterator.next();
				// get header
				Elements header = item.select(".content .info > h2 > a");
				String strhead = header.text().trim();
				// md5 header
				String md5header = Helper.getMD5(strhead);
				// date conten
				Elements post_conten_date = item.select(".content .right .content .post_type");
				String str_date = post_conten_date.text().trim();
//				System.out.println(str_date);
				int timestamp_post = Helper.getTimestamPost(str_date);
				// deatime nows
				int timestamp_nows = Helper.getTimestamp(true, null, 0);
				// check timepost
				int check_tmepost = Helper.checkDatePostWithDateToday(timestamp_post);
//				System.out.println(str_date);
				// System.out.println(check_tmepost);
				System.out.println(str_date);
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

				// Price
				Elements price_element = item.select(".content .right .content .price > p > strong");
				String str_price = price_element.text().trim();

				// contacts info
				Elements contacts_info_element = item.select(".content .right .content .contact_info");
				Element full_name_el = contacts_info_element.select("p > strong").first();
				String str_full_name = full_name_el.text().trim();

				// Phone
				Element phone_el = contacts_info_element.select("p > span").first();
				String str_phone = phone_el.text().trim();

				// link detal
				String header_link = "http://diaoconline.vn" + header.attr("href");
				// String urlhead = header

				Connection connDetail = Jsoup.connect(header_link);
				Document docDetail = connDetail.get();

				Element result_detail = docDetail.select("div#result_detail").first();
				Elements contens_result_detail = result_detail.select(".body");
				// address
				Elements address = contens_result_detail.select(".location");
				String addresspost = address.text().trim();

				Elements detail_info_element = result_detail.select(".detail_info #image_gallery .flexslider > img");
				String link_image = detail_info_element.attr("src");

				Elements detail_acreage = result_detail.select(".detail_info .right .feat_item > dl  dd:eq(3)");
				String str_acreage = detail_acreage.text().trim();
				// detail
				Elements contens_detail = result_detail.select("#detail .body > p");
				String str_detail = contens_detail.html();

				MySQLConnUtils.insertTableNews(str_full_name, str_price, str_phone, strhead, str_detail, "Ha noi",
						addresspost, timestamp_post, header_link, 1, timestamp_nows, md5header);
			}
			if (next_page) {
				page = page + 1;
				processPost("http://diaoconline.vn/sieu-thi/loc/?tindang=1&tp=2&pi=" + page);
			} else {
				page = 1;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
