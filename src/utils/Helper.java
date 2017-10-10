package utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class Helper {
	public static String path_log = "usr/local/checkmysql/";

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
	 * return -1 if datetime_post > datetime_today return 0 if datetime_post =
	 * datetime_today return 1 if datetime_post < datetime_today
	 * 
	 */
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
			if (str_date.indexOf(":") == -1) {
				str_date = str_date + " " + datemap.get("hour_now") + ":" + datemap.get("minute_now") + ":00";
			} else {
				str_date = str_date + ":00";
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

	public static void writeLog4j(String inputLog) {
		// creates pattern layout
		PatternLayout layout = new PatternLayout();
		String conversionPattern = "%-7p %d [%t] %c %x - %m%n";
		layout.setConversionPattern(conversionPattern);

		// creates console appender
		ConsoleAppender consoleAppender = new ConsoleAppender();
		consoleAppender.setLayout(layout);
		consoleAppender.activateOptions();

		// creates file appender
		FileAppender fileAppender = new FileAppender();
		fileAppender.setFile(path_log + "applog.txt");
		fileAppender.setLayout(layout);
		fileAppender.activateOptions();

		// configures the root logger
		Logger rootLogger = Logger.getRootLogger();
		rootLogger.setLevel(Level.DEBUG);
		rootLogger.addAppender(consoleAppender);
		rootLogger.addAppender(fileAppender);

		// creates a custom logger and log messages
		Logger logger = Logger.getLogger(Helper.class);
		logger.info(inputLog);
	}

	public static boolean sendDataToServer(StringBuffer strParam,boolean ckInsert) {
		System.out.println(strParam);
		boolean check_requet =true;
		String urlParameters = "";
		String request = "";
		
		if(ckInsert) {
			//insert databse
			urlParameters = "query=" + strParam;
			request = "http://www.admin.sangiaodichnhadat.info/api/insertquery?";
		}else {
			//check title
			urlParameters = "md5title=" + strParam;
			request ="http://www.admin.sangiaodichnhadat.info/api/checkdata?";
		}
		System.out.println(urlParameters);
		byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;
		URL url;
		try {
			url = new URL(request);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
			conn.setUseCaches(false);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.write(postData);
			wr.flush();

			String result = "";
			String line;
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			while ((line = reader.readLine()) != null) {
				result += line;
			}
			wr.close();
			reader.close();
			System.out.println(result);
			if( Integer.valueOf(result) == 0) {
				check_requet = false;
			}
			System.out.println(check_requet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return check_requet;
	}
	
	public static String readUrl(String urlString) throws Exception {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}
			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}

	}
}
