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
import org.apache.commons.codec.binary.Base64;

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

	public static boolean sendDataToServer(StringBuffer strParam, boolean ckInsert) {
		System.out.println(strParam);
		boolean check_requet = true;
		String urlParameters = "";
		String request = "";

		if (ckInsert) {
			// insert databse
			urlParameters = "query=" + strParam;
			request = "http://www.admin.sangiaodichnhadat.info/api/insertquery?";
		} else {
			// check title
			urlParameters = "md5title=" + strParam;
			request = "http://www.admin.sangiaodichnhadat.info/api/checkdata?";
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
			if (Integer.valueOf(result) == 0) {
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

	public static String decodeBase64(String paramString) {
		byte[] arrayOfByte = Base64.decodeBase64(paramString.getBytes());
		StringBuilder localStringBuilder = new StringBuilder();
		int i = 0;
		while (i < arrayOfByte.length) {
			int j = arrayOfByte[i];
			if (j != 0) {
				localStringBuilder.append((char) (0xFF ^ (0xF ^ (j & 0xF0) >> 4 | 0xF0 ^ (j & 0xF) << 4)));
				i++;
				continue;
			}
			localStringBuilder.append((char) (arrayOfByte[(i + 1)] | arrayOfByte[(i + 2)] << 8));
			i += 3;
		}
		return localStringBuilder.toString();
	}
//	public static void main(String[] args) {
//		String decode = decode("tyJGFkcWIqO1tyJHlkfGViKjIiNTRyfy1iMCxVcDExMTxVcDEyYDxVcTVkZG5nYCxVcDExMTlgITA9bCAsVXAxMTE8VXE1YWU0cCdsVXE1YWc+YCxVcDExMDhAJFhg7mhgLFVwMTEwNPAsQWlgKFHgLFVwMTExPFVxNWFlNHAjYeNoYCMwMD1gInFgLFVwMTExPFVwMTJgPFVxNWRkbmdgIzIyLCIhZGRidWNzcioyJFJ8VXE1YWE9YCRSdPlgLSAoT2DpYCxVcDExMDxVcTVlaTNiLCIhZnFkcWJyKjIoZHRwc3o/LyZpbGVkPiJhZHRvbmdjcW5uI29tbiZ+byNif2B/IjAwOHIwMD8iMDE3PyA4PyIwPyIwMTcwODIwMDg0MDE5PSNkZGFvV31uKmB3YiwiIHJ5Y2ViKjIiNTAkUnlsVXE1Y2c1fy1isiwiLGFkcioyMT4gNjg1NjI8IixvbmIqMTA1PicxMDE3OTU5OTk5OTk1PCIpZGIqMTMyODMxOTc8IiFidWFiKjIrSGT+Z2AoceNgLFVwMTExPFVxNWNibmhiLCIjYWRyKjIiQe5gLFVwMTExPFVxNWFlNHIsIiRhZHViKjIhMT8hMD8iMDE3MiwiIn9vbWIqMD18K3IkeWR8ZWIqMiJB7mAnbFVxNWFlMHAjbFVwMTAzPmAobFVxNWRpMCU1PWI8ICNoZX5nYCNsVXAxMmAwJFhk/mdgJFxVcTVhZT5sICxVcDExMTxVcDEyYDxVcTVkZG5nYCRSfFVxNWFnPmAoTFVxNWVmZXAkTFVxNWZhM2wgJ2lh4Cc1MDRyfCAgOTgxNzIyMzY4MiwiIWRkYnVjc3IqMi1MVXE1ZmkwLFVwMTEwPO5oYCIwLSAuQW1gJFxVcTVlYmAsSWrtYiwiIWZxZHFicioyIiwiIHJ5Y2ViKjInNTAwJFJ5bFVxNWNnNXIsIixhZHIqMjE+IDMxOTI5MDE2MTEzMzwiLG9uYioxMDU+JzY2OTIxOTk3MDc8IilkYioxMzQ2ODg2ODwiIWJ1YWIqMiU1MC1isiwiI2FkcioyIkHuYCNsVXAxMDM+YChsVXE1ZGkwI2hlfmdgI2xVcDEyYDIsIiRhZHViKjIhMT8hMD8iMDE3MiwiIn9vbWIqMj18K3IkeWR8ZWIqMiNC7FVwMzAwPmAiYWxVcDMwMT5gJ2LsVXAzMDEwcC5oYWxVcDMwMDAkfFVxNWFhOWAuZ29sVXAzMDMwJjYzMCRSfFVwMTJgPFVwMTFhPmdgLFVwMTEwOWxVcDMyMz5oYiwiIWRkYnVjc3IqMihPYO5nYC1BaWAtIChBbFVwMzAwMC5E/FVwMzIzOWIsIiFmcWRxYnIqMiIsIiByeWNlYioyIzcwJFJ5bFVxNWNnNX8tYrIsIixhZHIqMjA+KTgxMjg2OTQ4NTcwMTwiLG9uYioxMDU+KDQ1MjQwMjA2ODg1MDI8IilkYio4NzM2ODczPCIhYnVhYioyJTUwLWKyLCIjYWRyKjIiQe5gLmhg4CJ5au5nYiwiJGFkdWIqMiExPyEwPyIwMTcyLCIif29tYiowPXwrciR5ZHxlYioyI0xVcTVhZz5gImHuYC5oYOAjbFVxNWFlMHAkMCN8VXE1ZGUwLFVwMTExPFVxNWNmYCNobe5oYCNobFVxNWVnPCAkaWxVcTVjZz5gJH3jaGAkMD1iPCAkfFVxNWFhOWAuZ2XwITEwMCB4bFVxNWRhMC5HZXl8VXE1Y2U+YCNIbe5oYiwiIWRkYnVjc3IqMiRYbFVxNWNibmhgLElsVXE1Y2c0cC0gKE9g7mdgLUFpYiwiIWZxZHFicioyIiwiIHJ5Y2ViKjIhPicwJFxVcTVmZzIsIixhZHIqMjA+KTc4NzIxNTkwODYzOTY5PCIsb25iKjEwNT4oNDgxODAzODI4MDMzMjwiKWRiKjExMjM3NTc1PCIhYnVhYioyJDAwLWKyLCIjYWRyKjIiQe5gLmhg4CJ5au5nYiwiJGFkdWIqMiExPyEwPyIwMTcyLCIif29tYiowPXwrciR5ZHxlYioyI0xVcTVhZz5gImHuYC5oYOAjXFVwMTEwM0NAJERaMCQ1PWIwKHAkMCR8VXE1YWc+Z2AuZ2XwJHJ8VXAxMmA8VXE1ZGJjYC5oYOAlPWwgJPAkdPAmcO9gLmhg7iAnSWHqMCI8JTAkfFVxNWZnMiwiIWRkYnVjc3IqMixMVXAxMjk+aGAuQW1gLSAoT2DuZ2AtQWliLCIhZnFkcWJyKjIoZHRwc3o/LyZpbGVkPiJhZHRvbmdjcW5uI29tbiZ+byNif2B/IjAwOHIwMD8iMDE2PyA4PyA0PyxjWXhydjJiTyIwMTYwODA0MTMxODM4PSc4MWQ+KmB3YiwiIHJ5Y2ViKjIiPiUwJFxVcTVmZzIsIixhZHIqMjA+KTc3Nzk0MzMzMTcwMDI0PCIsb25iKjEwNT4oOTMxMTg1MjE5NTEyNDwiKWRiKjk5ODA4MjM8IiFidWFiKjIkNTAtYrIsIiNhZHIqMiJB7mAuaGDgInlq7mdiLCIkYWR1YioyITE/ITA/IjAxNzIsIiJ/b21iKjQ9fCtyJHlkfGViKjIjSG3uaGAjaGxVcTVlZzAiYe5gLFVwMTExPFVxNWFlNHAsVXE1ZGZsICU1PWIwI3xVcTVkZTAsVXAxMTE8VXE1Y2ZgJHxVcTVhYTlgLFVwMTEwPFVxNWFhOWAkXFVxNWZhPCAoT2DpYCxVcDExMDxVcTVlaTNsICTwJHTwI2HjaGAlMD1sICU3NzAkcnlsVXE1Y2c1fiIsIiFkZGJ1Y3NyKjIrSW1gI0hlfmdgLSAoT2DpYCxVcDExMDxVcTVlaTNiLCIhZnFkcWJyKjIoZHRwc3o/LyZpbGVkPiJhZHRvbmdjcW5uI29tbiZ+byNif2B/IjAwOHIwMD8iMDE3PyEwPyExPyIwMTcxMDExMjMxMDAzPSA3M2NvV31uKmB3YiwiIHJ5Y2ViKjIlNzcwJFJ5bFVxNWNnNXIsIixhZHIqMjE+IDY1MTA5NDwiLG9uYioxMDU+JzE2MDQ2OTAwMDAwMDQ8IilkYioxMzc4NTIyODwiIWJ1YWIqMiU1MC1isiwiI2FkcioyIkHuYCxVcDExMTxVcTVhZTRyLCIkYWR1YioyITE/ITA/IjAxNzIsIiJ/b21iKjA9fCtyJHlkfGViKjItTFVxNWJnNHAgeGxVcTVkYTAjSGnxYCJMVXE1ZGkzYCMwPWAlNFAiMCR4b2HuZ2wgK2luaGAkb2FuaGAjfFVxNWFnPWAlfFVxNWFlNHAsVXIwMTMwJT4iMCR8VXE1ZmcyLCIhZGRidWNzcioyK0hsVXAxMmA8VXAxMWE+Z2AkWGxVcDEyYDxVcDExYTxVcDMyMz5nYC0gLFVwMTEwPFVxNWRhPmdgLFVwMTEwMWIsIiFmcWRxYnIqMihkdHBzej8vJmlsZWQ+ImFkdG9uZ2Nxbm4jb21uJn5vI2J/YH8iMDA4cjAwPyIwMTc/ITA/ITE/IjAxNzEwMTEyMzEwMDQ9ImZmZm9XfW4qYHdiLCIgcnljZWIqMiU+IjAkXFVxNWZnMiwiLGFkcioyMT4gMDMwNDM4PCIsb25iKjEwNT4oMjgyOTU5MDAwMDAwNjwiKWRiKjEzNzg1MjI3PCIhYnVhYioyIzAwLWKyLCIjYWRyKjIiQe5gLmhg4C1sVXE1Ymc0cCB4bFVxNWRhMiwiJGFkdWIqMiExPyEwPyIwMTcyLCIif29tYiozPXwrciR5ZHxlYioyLkhg4CxVcDExMTxVcTViaTBwLFVwMTEwPFVxNWRpOWAjTFVxNWFlPmAsZPAnY/NgIzAtbFVxNWJnNHAkeG9h7mdgJTA9YjAocCUwJHxVcTVhZz5nbCAtICY8JjAkXFVxNWZnPiIsIiFkZGJ1Y3NyKjIsVXAxMTA0/FVwMzIzOWAjQuxVcDMwMT5gLSAiQWAsVXAxMTA87mhiLCIhZnFkcWJyKjIoZHRwc3o/LyZpbGVkPiJhZHRvbmdjcW5uI29tbiZ+byNif2B/IjAwOHIwMD8iMDE3PyEwPyExPyIwMTcxMDExMjMwNzU4PSg1YWFvV31uKmB3YiwiIHJ5Y2ViKjImPiYwJFxVcTVmZzIsIixhZHIqMjE+IDM0MzMwNjwiLG9uYioxMDU+KDMwNTI4ODAwMDAwMDI8IilkYioxMzc4NTIxOTwiIWJ1YWIqMiUwMC1isiwiI2FkcioyIkHuYC5oYOAieWruZ2IsIiRhZHViKjIhMT8hMD8iMDE3MiwiIn9vbWIqND18K3IkeWR8ZWIqMiJB7mAjbFVwMTAzPmAobFVxNWRpMCgzPWAoSEIxQCR8VXE1YWc+Z2AsVXAxMTE8VXE1YmkwcCZ5ZWdwKGxVcTVkYzAsSW5oYCxVcDExMDDtYCR8VXE1ZGU+Z2AhMCR8VXE1ZmcwJDUwMCRyeWxVcTVjZzVwImFvYCR67mIsIiFkZGJ1Y3NyKjIoT2DuZ2AsSWxVcTVjZzRwLSAoT2DuZ2AtQWliLCIhZnFkcWJyKjIoZHRwc3o/LyZpbGVkPiJhZHRvbmdjcW5uI29tbiZ+byNif2B/IjAwOHIwMD8iMDE3PyEwPyExPyIwMTcxMDExMjMwMjQ3PSk0YTk/V31uKmB3YiwiIHJ5Y2ViKjIhNDUwMCRSeWxVcTVjZzVyLCIsYWRyKjIwPik2NDMzNDQ4NzkxNTwiLG9uYioxMDU+KDI3MjkzMzk1OTk1OTg8IilkYioxMzc4NTIxMjwiIWJ1YWIqMigzMC1isiwiI2FkcioyIkHuYCNsVXAxMDM+YChsVXE1ZGkwI2hlfmdgI2xVcDEyYDIsIiRhZHViKjIhMT8hMD8iMDE3MiwiIn9vbWIqMD18K3IkeWR8ZWIqMiAjTFVxNWFmPkAkWUxVcTVjYD5AIkHOQCdMVXE1YWQwUCAuSEDAJFJVXkdAKVrOQCxE0CIyMUAsICRSVV5HQCtNzkhAJERQJzAwKFAlNFAtRFgwJ0lBwCEyODAkUllMVXE1Y2Y1UCE9ZWRwJnV0/mdiLCIhZGRidWNzcioyKVruYChC8WAtICNMVXE1YWc1cCdJbFVxNWFlOXIsIiFmcWRxYnIqMiIsIiByeWNlYioyIiwiLGFkcioyMT4gMjE4MDQ0PCIsb25iKjEwNT4nOTA4NzIwMDAwMDAwNDwiKWRiKjEzNzg1MjA1PCIhYnVhYioyK0hk/mdgKHHjYCxVcDExMTxVcTVjYm5oYiwiI2FkcioyIkHuYC5oYOAieWruZ2IsIiRhZHViKjIhMT8hMD8iMDE3MiwiIn9vbWIqMD18K3IkeWR8ZWIqMiNIbe5oYCNobFVxNWVnMCJh7mAuaGDgLFVwMTExPFVxNWJpMHAgeGxVcTVkYTAsTFVxNWFhM2AkUnV+Z2wgJElsVXE1Y2c+YCR942hgIzA9YjwgJDAkfFVxNWFnPmdsIC1sVXE1Ymc0cCR5bFVxNWNhPmAjPiY9biAnSWHgIj4kMCR8VXE1Zmc+ICxVcTVkZWAuZ2FpciwiIWRkYnVjc3IqMixVcDExMDT8VXAzMDA+Z2AkUu1gLSAoQWlgIkDgJFJ8VXAxMmA+Z2IsIiFmcWRxYnIqMihkdHBzej8vJmlsZWQ+ImFkdG9uZ2Nxbm4jb21uJn5vI2J/YH8iMDA4cjAwPyIwMTc/IDg/IjM/IjAxNzA4MjMwOTI1NTI9IzVkNT9XfW4qYHdiLCIgcnljZWIqMiI+JDAkXFVxNWZnMiwiLGFkcioyMT4gMDI1NjM3PCIsb25iKjEwNT4oNjE3NzI3MDAwMDAwNzwiKWRiKjExNzIyMjUzPCIhYnVhYioyIzAwLWKyLCIjYWRyKjIiQe5gLmhg4CJ5au5nYiwiJGFkdWIqMiExPyEwPyIwMTcyLCIif29tYio0PXwrciR5ZHxlYioyLkhg4ChJbFVxNWJmbWwgK0hlcCxVcDExMDTwJFhsVXE1Y2JgLFVwMTEwPFVxNWNibmhgI0T+Z2wgJjI9YjAocCQwJHxVcTVhZz5nbC1EcCQ+Ij1sK0luaGAkT2FuaGAsVXAxMTA8VXE1Y2k+aGIsIiFkZGJ1Y3NyKjIsVXAxMTA8VXE1Y2JuaGAjRP5nYC0gKE9g7mdgLUFpYiwiIWZxZHFicioyIiwiIHJ5Y2ViKjIpPigwJFxVcTVmZzIsIixhZHIqMjA+KTgyNzgyNjwiLG9uYioxMDU+KDM0MzgyNDk5OTk5OTU8IilkYioxMzc4NTE5ODwiIWJ1YWIqMiYyMC1isiwiI2FkcioyIkHuYC5oYOAieWruZ2IsIiRhZHViKjIhMT8hMD8iMDE3MiwiIn9vbWIqMD18K3IkeWR8ZWIqMiJBzkAsVXAxMTA8VXE1YWQ0UCBYTFVxNWRgMCxMVXAxMWZsVXAxMWA+R0AsVXAxMTA8zkhAI0xVcTVlZjFALSAsVXAxMTA8VXE1ZGA+R0AsVXAxMTAxQC0gKEDALkxVcTVkaDlCLCIhZGRidWNzcioyK0ltYCxJau5gLSAsVXAxMTA8VXE1ZGE+Z2AsVXAxMTAxYiwiIWZxZHFicioyKGR0cHN6Py8maWxlZD4iYWR0b25nY3FubiNvbW4mfm8jYn9gfyIwMDhyMDA/IjAxNz8hMD8hMT8iMDE3MTAxMTIyNTQxMz0haTM1b1d9bipgd2IsIiByeWNlYioyITQwMCRSeWxVcTVjZzV/LWKyLCIsYWRyKjIxPiAwNzMyNDk3MDQ5OTA2NzwiLG9uYioxMDU+KDM0NzU2MTQyOTQ0MzQ8IilkYioxMzc4NTE5MzwiIWJ1YWIqMiE0NTAtYrIsIiNhZHIqMiJB7mAsVXAxMTE8VXE1YWU0ciwiJGFkdWIqMiExPyEwPyIwMTcyLCIif29tYiowPXwrciR5ZHxlYioyI0ht7mhgI2hsVXE1ZWcwI2xVcTVhZz5gImHuYCEwKHV8VXE1YWU0cCxVcDExMTxVcTVhZTRwJExVcTVjYmNoYCZcVXE1ZWUwJTA9YjAkeGT+YCFOYCRYbFVxNWNkYChz4CFOYCtIYe5oYChPYOlgLFVwMTEwPFVxNWVpM2AoQOAuTFVxNWRpOWAnSWHgIjAwJHJ5bFVxNWNnNX8hPWI+IiwiIWRkYnVjc3IqMiFOYCtIYe5oYC0gKE9g6WAsVXAxMTA8VXE1ZWkzYiwiIWZxZHFicioyKGR0cHN6Py8maWxlZD4iYWR0b25nY3FubiNvbW4mfm8jYn9gfyIwMDhyMDA/IjAxNz8hMD8hMT8iMDE3MTAxMTIyNTExMD0lM2A2P1d9bipgd2IsIiByeWNlYioyIjAwJFJ5bFVxNWNnNXIsIixhZHIqMjA+KTg5NTk4OTwiLG9uYioxMDU+JzIyNDUzODAwMDAwMDQ8IilkYioxMzc4NTE4ODwiIWJ1YWIqMiUwMC1isiwiI2FkcioyIkHuYCxVcDExMTxVcTVhZTRwLmxVcTVjYT5gJGxVcTVmYTAh7mIsIiRhZHViKjIhMT8hMD8iMDE3MiwiIn9vbWIqMD18K3IkeWR8ZWIqMiJBzkAhNDU9YjAsVXAxMTA8VXE1YWQ0UCBYTFVxNWRgMCxMVXAxMWZsVXAxMWA+R0AsVXAxMTA8zkhAI0xVcTVlZjFALSAsVXAxMTA8VXE1ZGA+R0AsVXAxMTAxQC0gKEDALkxVcTVkaDlALSAsSWruYChMVXE1Y2c6MCFOaGAiTFVxNWFmY2owIDkzPiQ0OD4lNTQ2MiwiIWRkYnVjc3IqMitJbWAsSWruYC0gLFVwMTEwPFVxNWRhPmdgLFVwMTEwMWIsIiFmcWRxYnIqMihkdHBzej8vJmlsZWQ+ImFkdG9uZ2Nxbm4jb21uJn5vI2J/YH8iMDA4cjAwPyIwMTc/ITA/ITE/IjAxNzEwMTEyMjUxMDE9JjRlYT9XfW4qYHdiLCIgcnljZWIqMiE0MDAkUnlsVXE1Y2c1fy1isiwiLGFkcioyMT4gMDg4ODc0MTc3MTM1NjM8IixvbmIqMTA1PigzNTgwNzU2ODg3ODE4PCIpZGIqMTM3ODUxODY8IiFidWFiKjIhNDUwLWKyLCIjYWRyKjIiQe5gLFVwMTExPFVxNWFlNHIsIiRhZHViKjIhMT8hMD8iMDE3MiwiIn9vbWIqMD18K3IkeWR8ZWIqMiJBzkAuSEDAJFJVXkdAKVrOQCxE0CUyNEAkUlVeR0ArTc5IQCwgLkhAwCIwLUxVcTViZjRQLFVwMTEwPFVwMTFmbFVxNWRjbkdOICEwLUxVcTViZjRQLkdF0CREUCcyMChQJDAtRFApMCdJQcAoPCIwJFxVcTVmZjAiLCIhZGRidWNzcioyKVruYChC8WAtICNMVXE1YWc1cCdJbFVxNWFlOXIsIiFmcWRxYnIqMiIsIiByeWNlYioyIiwiLGFkcioyMT4gMjE4MDQ0PCIsb25iKjEwNT4nOTA4NzIwMDAwMDAwNDwiKWRiKjEzNzg1MTgyPCIhYnVhYioyK0hk/mdgKHHjYCxVcDExMTxVcTVjYm5oYiwiI2FkcioyIkHuYC5oYOAieWruZ2IsIiRhZHViKjIhMT8hMD8iMDE3MiwiIn9vbWIqMD18K3IkeWR8ZWIqMiJB7mArSGHjaGAjfFVxNWFhPmAgeGxVcTVkYTAjbFVxNWRlMChPYO5gK0lsVXE1YmZtbCAoQOAuTFVxNWRpOWAkaWxVcTVjZz5gJHljaGAjMTA9YChy6XAkbFVxNWZhPmdgITAwJHxVcTVhZz5nbiAsSGAgOTY4NjYxMzUzMiwiIWRkYnVjc3IqMihBbFVwMzAwPmdgJFJ0/FVwMzAxPmdgLSAoT2DuYCtJbFVxNWJmbWIsIiFmcWRxYnIqMiIsIiByeWNlYioyIjM1MCRcVXE1ZmcyLCIsYWRyKjIxPiAyODQ3OTI8IixvbmIqMTA1Pig1MDMxMzQ5OTk5OTk3PCIpZGIqMTM3ODUxNzY8IiFidWFiKjIjMTAwLWKyLCIjYWRyKjIiQe5gLmhg4C1sVXE1Ymc0cCB4bFVxNWRhMiwiJGFkdWIqMiExPyEwPyIwMTcyLCIif29tYio1MD18K3IkeWR8ZWIqMiNIbe5oYCNobFVxNWVnMCJh7mAuaGDgLFVwMTExPFVxNWJpMHAgeGxVcTVkYTAkVP5gJFhsVXE1YWU0cCRZ/mdsICM4PWI8ICQwJHxVcTVhZz5nbCAnaWHgIj4pMCR8VXE1Zmc8ICIwLWxVcTViZzRwJHhvYe5nbCAsVXE1ZGZgLmdhaX4iLCIhZGRidWNzcioyK0hsVXAxMmA8VXAxMWE+Z2AkWGxVcDEyYDxVcDExYTxVcDMyMz5nYC0gLFVwMTEwPFVxNWRhPmdgLFVwMTEwMWIsIiFmcWRxYnIqMihkdHBzej8vJmlsZWQ+ImFkdG9uZ2Nxbm4jb21uJn5vI2J/YH8iMDA4cjAwPyIwMTc/IDQ/IDc/IjAxNzA0MDcxMjU0MjU9JGkxYD9XfW4qYHdiLCIgcnljZWIqMiI+KTAkXFVxNWZnMiwiLGFkcioyMT4gMDE5ODk8IixvbmIqMTA1PigyOTU3ODIwMDAwMDAxPCIpZGIqMTE5ODk1MDc8IiFidWFiKjIjODAtYrIsIiNhZHIqMiJB7mAuaGDgInlq7mdiLCIkYWR1YioyITE/ITA/IjAxNzIsIiJ/b21iKjA9fCtyJHlkfGViKjIjSG3uaGAjaGxVcTVlZzAjbFVxNWFnPmAiYe5gI2hlfmdgI2xVcDEyYDAmWW5hY29uZWhwJzwgJjEwI0xVcTVhZzVwJElsVXE1Y2U+bCAjbFVwMTAzPmAnY/NsICEzMD1iMiwiIWRkYnVjc3IqMiNMVXE1YWc1cCRJbFVxNWNlPmAtIC5BbWAkXFVxNWViYCxJau1iLCIhZnFkcWJyKjIiLCIgcnljZWIqMiIsIixhZHIqMjE+IDMzODg3ODYzMTU5MjwiLG9uYioxMDU+JzU5NjI4Mjk1ODk4PCIpZGIqMTE4MjAxNjc8IiFidWFiKjIhMzAwLWKyLCIjYWRyKjIiQe5gI2xVcDEwMz5gKGxVcTVkaTAjaGV+Z2AjbFVwMTJgMiwiJGFkdWIqMiExPyEwPyIwMTcyLCIif29tYiozPXwrciR5ZHxlYioyIkHuYCxVcDExMTxVcTVhZTRwLEFgK0hq7CAkeWxVcTVjZz5gK2luaGAkb2FuaGwgLFVwMTExPFVxNWRhOWAkaWxVcTVjZz5gJHJ8VXAxMmA8VXE1ZGRuZ2AobFVxNWNkY2wgJPAkdPAmcO9gJHxVcTVhZG5gLmhg7iAjXFVwMTEwNFowIDk3NjY3ODgxMzIsIiFkZGJ1Y3NyKjIsQWArSGrgLSAoQOAsVXAxMTA0/mdiLCIhZnFkcWJyKjIoZHRwc3o/LyZpbGVkPiJhZHRvbmdjcW5uI29tbiZ+byNif2B/IjAwOHIwMD8iMDE3PyA5PyA4PyIwMTcwOTA4MTEyNTQyPSFlNTI/V31uKmB3YiwiIHJ5Y2ViKjImMDAkUnlsVXE1Y2c1fy1isiwiLGFkcioyMD4pNjcxNjgzPCIsb25iKjEwNT4nNTU2NTY0MDAwMDAwMjwiKWRiKjEzNDcyNzk2PCIhYnVhYioyJDMwLWKyLCIjYWRyKjIiQe5gLFVwMTExPFVxNWFlNHIsIiRhZHViKjIhMT8hMD8iMDE3MiwiIn9vbWIqMD19XX");
//	System.out.println(decode);
//	}
}
