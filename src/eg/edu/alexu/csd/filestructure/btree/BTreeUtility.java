package eg.edu.alexu.csd.filestructure.btree;
import java.util.ArrayList;
import java.util.List;

public class BTreeUtility<K extends Comparable<K>, V> {
    IBTree<K, V> tree;

    public BTreeUtility(IBTree<K, V> tree) {
        this.tree = tree;
    }

    /**
     * @param node  the node at which we want to add new entry
     * @param key   the key to be added
     * @param value the value to be added
     * @assumption this funtion deals with nodes which can afford adding new entry without violation BTree properties
     */
    void addEntry(IBTreeNode<K, V> node, K key, V value) {
        List<K> keys = node.getKeys();
        List<V> values = node.getValues();

        int n = keys.size();
        int i = 0;
        while (i < n && key.compareTo(keys.get(i)) >= 0) {
            if (key.compareTo(keys.get(i)) == 0) return;
            i++;
        }
        if (i == n) {
            keys.add(key);
            values.add(value);
        } else {
            keys.add(i, key);
            values.add(i, value);
        }

        node.setValues(values);
        node.setKeys(keys);
        node.setNumOfKeys(node.getNumOfKeys() + 1);
    }

void merge(IBTreeNode<K, V> parent, int idx) {

    List<IBTreeNode<K, V>> children = parent.getChildren();
    IBTreeNode<K, V> ch1 = children.get(idx);
    IBTreeNode<K, V> ch2 = children.get(idx + 1);

    List<K> parentKeys = parent.getKeys();
    List<V> parentValues = parent.getValues();
    K key = parentKeys.get(idx);
    V value = parentValues.get(idx);
    parentKeys.remove(idx);
    parentValues.remove(idx);

    List<K> mergedKeysList = ch1.getKeys();
    List<V> mergedValuesList = ch1.getValues();
    List<IBTreeNode<K, V>> mergedChildrenList = ch1.getChildren();

    mergedKeysList.add(key);
    mergedValuesList.add(value);
    /* add keys and values to the merged list */
    for (int i = 0; i < ch2.getKeys().size(); i++) {
        mergedKeysList.add(ch2.getKeys().get(i));
        mergedValuesList.add(ch2.getValues().get(i));
    }
    /*add the children of the sibling to the merged list */
    if (mergedChildrenList != null) {
        for (int i = 0; i < ch2.getChildren().size(); i++) {
            IBTreeNode<K, V> child = ch2.getChildren().get(i);
            mergedChildrenList.add(child);
        }
    }
    children.remove(idx + 1);
    children.remove(idx);
    children.add(idx, ch1);

    parent.setChildren(children);
    parent.setKeys(parentKeys);
    parent.setValues(parentValues);
    parent.setNumOfKeys(parentKeys.size());


}

boolean deleteEntry(IBTreeNode<K, V> node, K key) {
    List<K> keys = node.getKeys();
    List<V> values = node.getValues();
    int n = keys.size();
    int i = 0;
    while (i < n && key.compareTo(keys.get(i)) > 0) {
        i++;
    }
    if (i == n) return false;
    else if (key.compareTo(keys.get(i)) == 0) {
        keys.remove(i);
        values.remove(i);
        node.setValues(values);
        node.setKeys(keys);
        node.setNumOfKeys(keys.size());
        return true;
    }
    return false;

}
IBTreeNode<K, V> getPredecessor(IBTreeNode<K, V> node, int keyIdx) {
    if (node == null) return null;
    IBTreeNode<K, V> nxt = node.getChildren().get(keyIdx);
    while (!nxt.isLeaf()) {
        int last = nxt.getChildren().size() - 1;
        nxt = nxt.getChildren().get(last);
    }
    return nxt;
}
IBTreeNode<K, V> getSuccessor(IBTreeNode<K, V> node , int keyIdx) {
    if (node == null) return null;
    IBTreeNode<K, V> nxt = node.getChildren().get(keyIdx+1);
    while (!nxt.isLeaf()){
        nxt = nxt.getChildren().get(0) ;
    }
    return nxt;
}
}
