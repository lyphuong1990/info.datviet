package utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


public class Helper {

	public static String getMD5(String input) {
		String hashtext;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			hashtext = number.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return hashtext;
	}

	public static Map<String, Integer> getDatetimeArray() {
		Map<String, Integer> aMap = new HashMap<String, Integer>();
		aMap.put("year_now", Integer.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
		aMap.put("month_now", Integer.valueOf(Calendar.getInstance().get(Calendar.MONTH)));
		aMap.put("day_now", Integer.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)));
		aMap.put("hour_now", Integer.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
		aMap.put("ampm_now", Integer.valueOf(Calendar.getInstance().get(Calendar.AM_PM)));
		aMap.put("minute_now", Integer.valueOf(Calendar.getInstance().get(Calendar.MINUTE)));
		return aMap;
	}

	/*
	 * tstm_now value true: get timestamp current tstm_now value false: get
	 * timestamp with datetime_post params empty datetime_post : get timestamp with
	 * second_post params
	 * 
	 * datetime_post {format, string datepost}
	 */

	public static String getTimesNow() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		String timeDay = dateFormat.format(date);
		return timeDay;
	}

	public static int getTimestamp(Boolean tstm_now, String[] datetime_post, int second_post) {
		Date datetime = new Date();
		int timestm_now = (int) (datetime.getTime() / 1000L);
		int timestm_post = 0;
		try {
			if (tstm_now) {
				timestm_post = timestm_now;
			} else {
				if (datetime_post != null && datetime_post.length > 0) {
					Date date = new SimpleDateFormat(datetime_post[0]).parse(datetime_post[1]);
					timestm_post = (int) (date.getTime() / 1000L);
				} else {
					timestm_post = timestm_now - second_post;
				}

			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timestm_post;

	}

	public static String slugify(String input) {
		return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
				.replaceAll("[^ \\w]", "").trim().replaceAll("\\s+", "-").toLowerCase(Locale.ENGLISH);
	}

	/*
	*return -1 if datetime_post > datetime_today
	*return 0 if datetime_post = datetime_today
	*return 1 if datetime_post < datetime_today
	 * 
	 * */
	public static int checkDatePostWithDateToday(int input) throws ParseException {
		System.out.println(input);
		String timestamp_str = new SimpleDateFormat("dd/MM/yyyy").format(new Date(input * 1000L));
		System.out.println(timestamp_str);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date datetime_post = sdf.parse(timestamp_str);
		Date datetime_today = sdf.parse(sdf.format(new Date()));
		return datetime_post.compareTo(datetime_today);
	}
	
	public static int getTimestamPost(String input) {
		int post_date = 0;

		String str_date = input.substring(input.indexOf(":") + 1, input.length());

		str_date = str_date.trim();
		String[] date_array = str_date.split(" ");

		Map<String, Integer> datemap = getDatetimeArray();
		int check_year = str_date.indexOf(String.valueOf(datemap.get("year_now")));
		int check_hour = str_date.indexOf("giờ");
		int check_minute = str_date.indexOf("phút");

		if (check_year > -1) {
			String datetime_post[] = new String[2];
			if(str_date.indexOf(":") == -1) {
				str_date = str_date+" "+datemap.get("hour_now")+":"+datemap.get("minute_now")+":00";
			}else {
				str_date = str_date+":00";
			}
			
			datetime_post[0] = "dd/MM/yyyy HH:mm:ss";
			datetime_post[1] = str_date;
			post_date = getTimestamp(false, datetime_post, 0);
		} else {
			int hour = 0;
			int minute = 0;
			if (check_hour > -1) {
				hour = Integer.valueOf(date_array[0]);
				minute = Integer.valueOf(date_array[2]);

			} else if (check_minute > -1) {
				minute = Integer.valueOf(date_array[0]);
			}
			// 1h = 60phut , 1p= 60 giay;
			int hour_post = (hour * 3600);
			int minute_post = (minute * 60);
			int second_post = hour_post + minute_post;
			post_date = getTimestamp(false, null, second_post);

		}

		return post_date;

	}
	
	public static String escapeHTML(String s) {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&' || c == '\'') {
                out.append("&#");
                out.append((int) c);
                out.append(';');
                out.append("");
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
	
	public static String convertWord(String str) {
		str = str.replace("✅", "").replace("💸", "").replace("📱", "").replace("\'", "").replace("\"", "");
		return str;
	}

}
