package info.daiviet;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.Helper;
import utils.MySQLConnUtils;


public class BatDongSan implements Runnable{
	//Chạy nhanh lên nào
	public void run() {
		try {
			while (true) {
				processPostBDS("https://batdongsan.com.vn/nha-dat-ban-ha-noi/p1");
				Thread.sleep(30000);
			}
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	 public static int page = 1;
	 
	public static void main(String[] args) {
		processPostBDS("https://batdongsan.com.vn/nha-dat-ban-ha-noi/p1");
	}


	public static void processPostBDS(String url){
		Connection conn = Jsoup.connect(url);
		try {
			boolean next_page = false;
			Document doc = conn.get();

			Element result_block = doc.select("div.product-list-page .Main").first();
			Elements contens = result_block.select(".search-productItem");

			Iterator<Element> iterator = contens.iterator();

			while (iterator.hasNext()) {
				Element item = iterator.next();
				// get header
				Elements header = item.select(".p-title > h3 > a");
				String strhead = header.text().trim();

				// md5 header
				String md5header = Helper.getMD5(strhead);
			
				// date conten
				Elements post_conten_date = item.select(".p-bottom-crop .floatright");
				String str_date = post_conten_date.text().trim();
				
				int timestamp_post = Helper.getTimestamPost(str_date);
				//deatime nows
				int timestamp_nows = Helper.getTimestamp(true,null,0);
				//check timepost
				int check_tmepost = Helper.checkDatePostWithDateToday(timestamp_post);
				
				if(check_tmepost != 0) {
					next_page = false;
					page = 1;
					break;
				}else {
					next_page = true;
				}
				if(MySQLConnUtils.checkTitleExit(md5header)){
					continue;
				}
			
				// Price
				Elements price_element = item.select(".product-price");
				String str_price = price_element.text().trim();

//
				// link detal
				String header_link = "https://batdongsan.com.vn" + header.attr("href");
				// String urlhead = header
//
				Connection connDetail = Jsoup.connect(header_link);
				Document docDetail = connDetail.get();

				Element result_detail = docDetail.select("div.body-left").first();
				Elements contens_result_detail = result_detail.select(".container-default");
				
				// contacts info
				Elements contacts_info_element = contens_result_detail.select(".div-table .div-table-cell.table2 .table-detail");
				Element full_name_el = contacts_info_element.select(".right-content:eq(0) .right").first();
				String str_full_name = full_name_el.text().trim();
				

				// Phone
				Element phone_el = contacts_info_element.select(".right-content:eq(2) .right").first();
				Element mobel_el = contacts_info_element.select(".right-content:eq(3) .right").first();
				String str_phone = "";
				if(phone_el == null){
					if(mobel_el == null) {
						continue;
					}else {
						str_phone = mobel_el.text().trim();
					}
				}else {
					str_phone = phone_el.text().trim();
				}
				
				// address
				Elements address = contens_result_detail.select(".div-table .table-detail .row:eq(1) .right");
				String addresspost = address.text().trim();


				Elements detail_info_element = result_detail.select("#product-detail #photoSlide #divPhotoActive > div > img:eq(0)");
				String link_image = detail_info_element.attr("src");
				
				Elements detail_acreage = result_detail.select("#product-detail .kqchitiet  .gia-title:eq(1)");
				String str_acreage = detail_acreage.text().trim();

				// detail
				Elements contens_detail = result_detail.select("#product-detail .pm-content .pm-desc");
				String str_detail = contens_detail.html();
				
				MySQLConnUtils.insertTableNews(str_full_name, str_price, str_phone, strhead, str_detail, "Hà nội",
						addresspost, timestamp_post, header_link, 2, timestamp_nows, md5header);
			}
			if(next_page) {
				page = page+1;
				System.out.println("batdongsan.com.vn page" +page);
				processPostBDS("https://batdongsan.com.vn/nha-dat-ban-ha-noi/p="+page);
			}else {
				page = 1;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Helper.writeLog4j(e.toString());
			e.printStackTrace();
		}
		
		
	}

}
