package domain;

/**
 * Description:
 * User: HC
 * Date: 2019-12-26-10:40
 */
public class QueryVO {
    private String rname;
    private String minprice;
    private String maxprice;

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getMinprice() {
        return minprice;
    }

    public void setMinprice(String minprice) {
        this.minprice = minprice;
    }

    public String getMaxprice() {
        return maxprice;
    }

    public void setMaxprice(String maxprice) {
        this.maxprice = maxprice;
    }
}
