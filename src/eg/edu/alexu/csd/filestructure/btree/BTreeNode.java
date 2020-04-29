package eg.edu.alexu.csd.filestructure.btree;

import java.util.List;

public class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {

    private int numberOfKeys;
    private List<K> keys;
    private List<V> values;
    private List<IBTreeNode<K, V>> children;
    private boolean leaf;

    @Override
    public int getNumOfKeys() {
        return numberOfKeys;
    }

    @Override
    public void setNumOfKeys(int numOfKeys) {
        this.numberOfKeys = numOfKeys;
    }

    @Override
    public boolean isLeaf() {
        // return (children == null || children.size() == 0 ) ;
        return leaf;
    }

    @Override
    public void setLeaf(boolean isLeaf) {
        leaf = isLeaf;
    }

    @Override
    public List<K> getKeys() {
        return keys;
    }

    @Override
    public void setKeys(List<K> keys) {
        this.keys = keys;
    }

    @Override
    public List<V> getValues() {
        return this.values;
    }

    @Override
    public void setValues(List<V> values) {
        this.values = values;
    }

    @Override
    public List<IBTreeNode<K, V>> getChildren() {
        return this.children;
    }

    @Override
    public void setChildren(List<IBTreeNode<K, V>> children) {
        this.children = children;
    }
}
