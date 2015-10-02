package com.leslie.hengda.sms;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jsoup.Connection;
import org.jsoup.Connection.Request;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HttpVCode {

	private static final String INDEX_URL = "https://platform.sao.so/t/_s5G8OQdADUDi2";
	private static final String REQUEST_VCODE_URL = "https://platform.sao.so/vcode/get";
	private static final String CHECK_VCODE_URL = "https://platform.sao.so/t/checkVCode";
	private static final String ENTER_LOTTERY_URL = "https://platform.sao.so/t/enterLottery";
	
	private static final Map<String, String> cookies = new HashMap<String, String>();
	private static final Map<String, String> headers = new HashMap<String, String>();
	static {
		cookies.put("ARRAffinity", "07d8a239688deb57fef1dfdd2efcd813ed7fb1e3d48433a3ddb293b51db4a802");
		cookies.put("Hm_lvt_7a8b6f0c8380f98a7dda22d44ad18049", "1441948152");
		
		headers.put("Referer", INDEX_URL);
		headers.put("Origin", "https://platform.sao.so");
		headers.put("Host", "platform.sao.so");
		headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36");
		headers.put("Connection", "keep-alive");
	}

	public static void vcode(String phoneNum) throws IOException {
		Map<String, String> params = new HashMap<String, String>();

		// 请求首页， 获取页面session 及 vcodeToken
		Response response = get(INDEX_URL, params, cookies);
		String session = response.cookie("SESSION");
		Document doc = response.parse();
		Elements vcodeElement = doc.getElementsByAttribute("data-vcodeToken");
		String vcodeToken = vcodeElement.first().attr("data-vcodeToken");
		System.out.println(session + ":" + vcodeToken);

		// 获取短信验证码
		params.put("honestId", "_s5G8OQdADUDi2");
		params.put("phoneNum", phoneNum);
		params.put("vcodeToken", vcodeToken);
		cookies.put("SESSION", session);
		post(REQUEST_VCODE_URL, params, cookies, null);
		
		// 校验验证码是否正确
		BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));  
        System.out.print("请输入验证码：");  
        String vcode = strin.readLine();  
		System.out.println("------------"+vcode);
		
		params.remove("vcodeToken");
		params.put("vCode", vcode);
		
		doc = post(CHECK_VCODE_URL, params, cookies, headers);
		String checked = doc.body().html();
		if ("false".equals(checked)) {
			System.out.println("验证码校验失败");
			return;
		}
		
		// 抽奖
		params.put("openId", "");
		params.put("needCheck", "0");
		doc = post(ENTER_LOTTERY_URL, params, cookies, null);
		System.out.println(doc);
	}
	

	private static Response get(String url, Map<String, String> params, Map<String, String> cookies) throws IOException {
		Connection connection = Jsoup.connect(url).data(params).cookies(cookies).timeout(10 * 1000);
		Request request = connection.request();
		request.ignoreHttpErrors(true);
		request.ignoreContentType(true);

		return connection.execute();
	}

	private static Document post(String url, Map<String, String> params, Map<String, String> cookies, Map<String, String> headers) throws IOException {
		Connection connection = Jsoup.connect(url).data(params).cookies(cookies).timeout(10 * 1000);
		Request request = connection.request();
		request.ignoreHttpErrors(true);
		request.ignoreContentType(true);

		if (headers != null) {
			for (Entry<String, String> header : headers.entrySet()) {
				request.header(header.getKey(), header.getValue());
			}
		} 
		return connection.post();
	}

	public static void main(String[] args) throws IOException {
		vcode("15618971727");
	}
}
