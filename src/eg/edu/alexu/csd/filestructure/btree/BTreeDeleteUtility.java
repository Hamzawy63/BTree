package eg.edu.alexu.csd.filestructure.btree;

public class BTreeDeleteUtility<K extends Comparable<K> , V> {
    IBTree<K , V> tree ;

    public BTreeDeleteUtility(IBTree<K, V> tree) {
        this.tree = tree;
    }
}
