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
        return true; // TODO:
    }

    /**
    * Cleans up the query with extra whitespace,
    * returns the query with a single whitespace if necessary. 
    */
    private void compress() {
        query = query.replaceAll("[  ]*<[  ]*", "<")
                     .replaceAll("[  ]*=[  ]*", "=")
                     .replaceAll("[  ]*>[  ]*", ">");
    }
}
