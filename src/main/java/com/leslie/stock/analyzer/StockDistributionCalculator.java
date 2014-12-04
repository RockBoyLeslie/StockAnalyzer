package com.leslie.stock.analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

import org.apache.commons.lang.StringUtils;

import com.leslie.stock.bean.StockBean;

public class StockDistributionCalculator extends RecursiveTask<List<StockBean>> {

	private static final long serialVersionUID = -7051444768110556613L;
	private static final int PER_COUNT = 10;

	private int startCode;
	private int endCode;
	
	public StockDistributionCalculator(int startCode, int endCode) {
		this.startCode = startCode;
		this.endCode = endCode;
	}
	
	@Override
	protected List<StockBean> compute() {
		List<StockBean> stocks = new ArrayList<StockBean>();
		if (endCode - startCode <= PER_COUNT) {
			for (int i = startCode; i <= endCode; i ++) {
				String code = StringUtils.leftPad(String.valueOf(i), 6, '0');
				StockBean stock = new StockBean(code);
				try {
					StockInformationCrawler.marshallHeader(stock);
					StockInformationCrawler.marshallDetail(stock);
				} catch (Exception e) {
					continue;
				}
				stocks.add(stock);
			}
			return stocks;
		}

		int childCalculator = (endCode - startCode) % PER_COUNT == 0 ? (endCode - startCode) / PER_COUNT : (endCode - startCode) / PER_COUNT +1;
		List<StockDistributionCalculator> calculators = new ArrayList<StockDistributionCalculator>(childCalculator);
		for (int i = 0; i < childCalculator; i ++) {
			int subStartCode = startCode + PER_COUNT * i;
			int subEndCode = (startCode + PER_COUNT * (i + 1) - 1) > endCode ? endCode : (startCode + PER_COUNT * (i + 1) - 1);
			StockDistributionCalculator calculator = new StockDistributionCalculator(subStartCode, subEndCode);
			calculator.fork();
			calculators.add(calculator);
		}
		
		for (StockDistributionCalculator calculator : calculators) {
			stocks.addAll(calculator.join());
		}
		
		return stocks;
	}

	
	public static void main(String[] args) {
		ForkJoinPool pool = new ForkJoinPool();
		Future<List<StockBean>> future = pool.submit(new StockDistributionCalculator(2600, 2700));
		try {
			List<StockBean> stocks = future.get();
			System.out.println(stocks.size());
			
			Collections.sort(stocks);
			for (int i = stocks.size() - 1; i > stocks.size() - 90; i --) {
				System.out.println(stocks.get(i));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
