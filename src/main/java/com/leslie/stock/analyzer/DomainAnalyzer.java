package com.leslie.stock.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DomainAnalyzer {
	
	private static final String URL = "http://www.cndns.com/Ajax/domainQuery.ashx";
	private static final int CONNECTION_TIME_OUT = 10 * 1000;
	
	private static Map<String, String> params;
	
	static {
		params = new HashMap<String, String>();
		params.put("panel", "domain");
		params.put("domainSuffix", ".com");
		params.put("usrcls", "1");
		params.put("cookieid", "jfrogr1yptpindgtdrukbx2m");
	}
	public static void analyze(String domainName) {
		params.put("domainName", domainName);
		String url = toUrl(params);
		
		Document doc = null;
        try {
            Connection connection = Jsoup.connect(url).timeout(CONNECTION_TIME_OUT);
            Request request = connection.request();
            request.ignoreHttpErrors(true);
            request.ignoreContentType(true);
            doc = connection.get();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        
        if (doc == null) {
        	return;
        }
        
        String result = doc.body().text();
        if (result.startsWith("10000")) {
        	System.out.println("amaizing " + domainName);
        }

	}
	
	private static String toUrl(Map<String, String> params) {
		StringBuilder url = new StringBuilder(URL);
		
		if (params != null && params.size() > 0) {
			url.append("?");
			for (Entry<String, String> param : params.entrySet()) {
				url.append(param.getKey()).append("=").append(param.getValue()).append("&");
			}
		}
		
		return url.toString();
	}
	
	
	
	public static void main(String[] args) {
		String numbers = "abcdefghijklmnopqrstuvwxyz";
		
		List<String> domainList = new CopyOnWriteArrayList<String>();
		for (int i = 0; i < numbers.length(); i ++) {
			String first = String.valueOf(numbers.charAt(i));
			for (int j = 0; j < numbers.length(); j ++) {
				 String second = String.valueOf(numbers.charAt(j));
				 for (int k = 0; k < numbers.length(); k ++) {
					 String domainName = first + second + numbers.charAt(k);
					 domainList.add(domainName);
				 }
			}
		}
		
		
		final List<String> domains = domainList;
		for (int i = 0 ; i < 10; i ++) {
			new Thread() {
				@Override
				public void run() {
					while (true) {
						try {
							String domainName = domains.remove(0);
							if (StringUtils.isEmpty(domainName)) {
								break;
							}
							
							analyze(domainName);
						} catch (Exception e) {
							break;
						}
						
					}
				}
			}.start();
		}
	}
}
