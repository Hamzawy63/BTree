//package eg.edu.alexu.csd.filestructure.btree;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BTreeInsertUtility<K extends Comparable<K> , V> {
//    IBTree<K , V> tree ;
//    int minimumDegree ;
//    int maxKeys ;
//    IBTreeNode<K, V> root ;
//    public BTreeInsertUtility(IBTree<K, V> tree) {
//        this.tree = tree;
//        minimumDegree = tree.getMinimumDegree() ;
//        this.root = tree.getRoot() ;
//        int maxKeys = 2 * minimumDegree - 1 ;
//    }
//    void insert(K key) {
//
//    }
//    private void split(IBTreeNode<K, V> parent, int idx) {
//        if (parent == null || parent.getChildren() == null || parent.getChildren().get(idx) == null) {
//            System.out.println("Unexpected corrupted data in the split function");
//            LocalException.throwRunTimeErrorException();
//        }
//        IBTreeNode<K, V> target = parent.getChildren().get(idx);
//        IBTreeNode<K, V> newNode = new BTreeNode<>();
//        newNode.setLeaf(target.isLeaf());
//
//        /*
//        Fill the new node
//         */
//        List<K> keys = new ArrayList<>();
//        List<V> val = new ArrayList<>();
//        List<K> newTargetKeys = new ArrayList<>();
//        List<V> newTargetValues = new ArrayList<>();
//        K medianKey = target.getKeys().get(minimumDegree - 1);
//        V medianValue = target.getValues().get(minimumDegree - 1);
//
//        for (int i = 0; i < minimumDegree - 1; i++) {
//            keys.add(target.getKeys().get(i + minimumDegree));
//            val.add(target.getValues().get(i + minimumDegree));
//
//            newTargetKeys.add(target.getKeys().get(i));
//            newTargetValues.add(target.getValues().get(i));
//        }
//        newNode.setKeys(keys);
//        newNode.setValues(val);
//        newNode.setNumOfKeys(keys.size());
//
//        target.setKeys(newTargetKeys);
//        target.setValues(newTargetValues);
//        target.setNumOfKeys(keys.size());
//
//
//        if (!newNode.isLeaf()) {
//            List<IBTreeNode<K, V>> children = new ArrayList<>();
//            List<IBTreeNode<K, V>> newTargetChildren = new ArrayList<>();
//            for (int i = 0; i < minimumDegree; i++) {
//                children.add(target.getChildren().get(i + minimumDegree));
//                newTargetChildren.add(target.getChildren().get(i));
//            }
//            target.setChildren(newTargetChildren);
//            newNode.setChildren(children);
//        }
//
//
//        /*
//        Shift parent nodes so that we make the median place ready for the median element
//         */
//        List<K> parentKeys = parent.getKeys();
//        List<V> parentValues = parent.getValues();
//        List<IBTreeNode<K, V>> children = parent.getChildren();
//        children.add(idx + 1, newNode);
//        parentKeys.add(idx, medianKey);
//        parentValues.add(idx, medianValue);
//
//        parent.setValues(parentValues);
//        parent.setChildren(children);
//        parent.setKeys(parentKeys);
//        parent.setNumOfKeys(parent.getKeys().size());
//
//    }
//    public void insert(K key, V value) {
//        checkNullKey(key);
//        checkNullValue(value);
//        if (root == null) {
//            root = new BTreeNode<>();
//            List<K> keys = new ArrayList<>();
//            keys.add(key);
//            List<V> values = new ArrayList<>();
//            values.add(value);
//            root.setKeys(keys);
//            root.setValues(values);
//            root.setLeaf(true);
//            root.setNumOfKeys(1);
//            return;
//        }
//        if (getRoot().getKeys().size() == maxKeys) {
//            /*
//            in this case we will add no keys in the root and we will call the "split" subroutine which will
//            put the median element in the parent node ,namely the root
//             */
//            IBTreeNode<K, V> newRoot = new BTreeNode<>();
//            List<IBTreeNode<K, V>> newRootChildren = new ArrayList<>();
//            newRootChildren.add(getRoot());
//            newRoot.setLeaf(false);
//            newRoot.setChildren(newRootChildren);
//            newRoot.setValues(new ArrayList<>());
//            newRoot.setKeys(new ArrayList<>());
//            newRoot.setNumOfKeys(1);
//            root = newRoot;
//            split(root, 0);
//        }
//        insertNonFull(root, key, value);
//    }
//    private void insertNonFull(IBTreeNode<K, V> node, K key, V value) {
//        List<K> keys = node.getKeys();
//        List<V> values = node.getValues();
//
//        if (node.isLeaf()) {
//            // we are sure that this leaf is not full ;)
//            int i = 0;
//            while (i < node.getKeys().size() && node.getKeys().get(i).compareTo(key) <= 0) {
//                i++;
//            }
//            addEntry(node, key, value, i);
//
//        } else {
//            int i = 0;
//            K cur = keys.get(i);
//            while (i < keys.size() && key.compareTo(cur) >= 0)
//                i++;
//
//            if (node.getChildren().get(i).getNumOfKeys() == maxKeys) {
//                split(node, i);
//                if (key.compareTo(keys.get(i)) > 0) i++;
//            }
//            insertNonFull(node.getChildren().get(i), key, value);
//        }
//
//    }
//
//
//
//
//}
