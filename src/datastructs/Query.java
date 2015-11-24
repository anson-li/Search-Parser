package datastructs;
public class Query {
    private String query;
    private GenericStack<String[]> postDBProcess;
    private GenericStack<Stirng[]> preDBProcess;
    private GenericStack<String[]> inDBProcess;

    public static class SubQ {
        public enum FIELD { PRODUCT,
                            REVIEW,
                            PROD_REV,
                            WILDCARD,
                            SCORE,
                            PRICE,
                            DATE }

        private String product;
        private String review;
        private String prod_rev;
        private String wildcard;
        private String score;
        private String price;
        private String date;

        SubQ() {
             product    = "(?i:p:.*)";
             review     = "(?i:r:.*)";
             prod_rev   = ;
             wildcard   = ;
             score      = "(?i:rscore)";
             price      = "(?i:pprice)";
             date       = "(?i:rdate)";
        }
    }

    public Query(String query) {
        this.query = query;
        postDBProcess = new GenericStack<String[]>();
        preDBProcess  = new GenericStack<String[]>();
        inDBProcess   = new GenericStack<String[]>();
    }

    public void setQuery(String query) {
        this.query = query.toLowerCase();
        compress();
    }

    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return query;
    }

    public boolean isValid() {
        return false;
    }

    private void compress() {
        query = query.replaceAll("[  ]*<[  ]*", "<")
                     .replaceAll("[  ]*=[  ]*", "=")
                     .replaceAll("[  ]*>[  ]*", ">");
    }
}
