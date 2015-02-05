package com.leslie.stock.bean;

public class StockBean implements Comparable<StockBean> {

    // 股票代码
    private String code;

    // 当日价格
    private double price;

    // 总市值
    private double zsz;

    // 流通市值
    private double ltsz;

    // 每股净资产
    private double jzc;

    // 每股公积金
    private double gjj;

    // 每股未分配利润
    private double wfplr;

    // 净利润增长率
    private double zzl;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "StockBean [code=" + code + ", price=" + price + ", zsz=" + zsz + ", ltsz=" + ltsz + ", jzc=" + jzc + ", gjj=" + gjj + ", wfplr=" + wfplr
                + ", zzl=" + zzl + "]";
    }

    public StockBean(String code) {
        super();
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getZsz() {
        return zsz;
    }

    public void setZsz(double zsz) {
        this.zsz = zsz;
    }

    public double getLtsz() {
        return ltsz;
    }

    public void setLtsz(double ltsz) {
        this.ltsz = ltsz;
    }

    public double getJzc() {
        return jzc;
    }

    public void setJzc(double jzc) {
        this.jzc = jzc;
    }

    public double getGjj() {
        return gjj;
    }

    public void setGjj(double gjj) {
        this.gjj = gjj;
    }

    public double getWfplr() {
        return wfplr;
    }

    public void setWfplr(double wfplr) {
        this.wfplr = wfplr;
    }

    public int compareTo(StockBean o) {
        return Double.compare(this.getAbsValue(), o.getAbsValue());
    }

    public double getAbsValue() {
        return price == 0 ? 0 : (jzc + gjj + wfplr) / price;
    }

    public void setZzl(double zzl) {
        this.zzl = zzl;
    }

    public double getZzl() {
        return zzl;
    }
}
