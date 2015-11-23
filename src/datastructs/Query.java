package datastructs;
public class Query {
    private String query;
    
    public Query(String query) {
        this.query = query;
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
