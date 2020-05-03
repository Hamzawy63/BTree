package eg.edu.alexu.csd.filestructure.btree;

public class SearchResult implements ISearchResult {
    String Id ;
    int rank ;

    public SearchResult(String id, int rank) {
        Id = id;
        this.rank = rank;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String id) {

    }

    @Override
    public int getRank() {
        return 0;
    }

    @Override
    public void setRank(int rank) {

    }
}
