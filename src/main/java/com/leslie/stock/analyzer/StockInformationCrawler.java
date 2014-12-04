package com.leslie.stock.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.leslie.stock.bean.StockBean;

public class StockInformationCrawler {

	private static final String STOCK_DETAIL_URL = "http://stockpage.10jqka.com.cn/{stock_code}/";
	private static final String STOCK_HEADER_URL = "http://stockpage.10jqka.com.cn/spService/{stock_code}/Header/realHeader";

	private static Document getStockDocument(String url) {
		Document doc = null;

		try {
			doc = Jsoup.connect(url).timeout(10 * 1000).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return doc;
	}

	public static void marshallHeader(StockBean stock) {
		String url = StringUtils.replace(STOCK_HEADER_URL, "{stock_code}",
				stock.getCode());
		Document doc = getStockDocument(url);
		if (doc == null) {
			return;
		}

		JSONObject json = JSONObject.fromObject(doc.body().text());
		stock.setPrice(json.getDouble("xj"));
	}

	public static void marshallDetail(StockBean stock) {
		String url = StringUtils.replace(STOCK_DETAIL_URL, "{stock_code}",
				stock.getCode());
		Document doc = getStockDocument(url);
		if (doc == null) {
			return;
		}

		// Element zszElement = doc.getElementsByClass("zsz").get(0);
		// System.out.println(zszElement.child(0).text());
		// stock.setZsz(Double.parseDouble(zszElement.child(0).text()));
		//
		// Element ltszElement = doc.getElementsByClass("ltsz").get(0);
		// stock.setLtsz(Double.parseDouble(ltszElement.child(0).text()));

		// Element priceElement = doc.getElementsByClass("price").get(0);
		// stock.setPrice(Double.parseDouble(priceElement.text()));

		Element detailElement = doc.getElementsByClass("company_details")
				.get(0);
		for (int i = 0; i < detailElement.children().size(); i++) {
			if ("每股净资产：".equals(detailElement.child(i).text())) {
				stock.setJzc(Double.parseDouble(detailElement.child(i + 1)
						.child(0).text()));
			}

			if ("每股公积金：".equals(detailElement.child(i).text())) {
				stock.setGjj(Double.parseDouble(detailElement.child(i + 1)
						.child(0).text()));
			}

			if ("每股未分配利润：".equals(detailElement.child(i).text())) {
				stock.setWfplr(Double.parseDouble(detailElement.child(i + 1)
						.child(0).text()));
			}
		}

	}

	public static void main(String[] args) {

		List<StockBean> stocks = new ArrayList<StockBean>();
		for (int i = 2600; i < 2750; i++) {
			String code = StringUtils.leftPad(String.valueOf(i), 6, '0');
			StockBean stock = new StockBean(code);
			try {
				marshallHeader(stock);
				marshallDetail(stock);
			} catch (Exception e) {
				continue;
			}
			stocks.add(stock);
		}

		Collections.sort(stocks);

		for (int i = stocks.size() - 1; i > stocks.size() - 10; i --) {
			System.out.println(stocks.get(i));
		}
	}
}
