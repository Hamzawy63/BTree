package eg.edu.alexu.csd.filestructure.btree;

import java.util.*;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {
    private int minimumDegree; // t
    private IBTreeNode<K, V> root;
    private int maxKeys; /*maximum keys in a node */
    private int minKeys;
    BTreeUtility<K, V> treeUtility;

    public BTree(int minimumDegree) {
        if (minimumDegree < 2) LocalException.throwRunTimeErrorException();
        this.minimumDegree = minimumDegree;
        maxKeys = 2 * minimumDegree - 1;
        minKeys = minimumDegree - 1;
        treeUtility = new BTreeUtility<>(this);
    }

    @Override
    public int getMinimumDegree() {
        return minimumDegree;
    }

    @Override
    public IBTreeNode<K, V> getRoot() {
        return root;
    }

    @Override
    public void insert(K key, V value) {
        if (search(key) != null)
            return;
        InputChecker.checkNullValue(key, value);
        if (getRoot() == null) {
            root = new BTreeNode<>();
            List<K> keys = new ArrayList<>();
            keys.add(key);
            List<V> values = new ArrayList<>();
            values.add(value);
            root.setKeys(keys);
            root.setValues(values);
            root.setLeaf(true);
            root.setNumOfKeys(1);
            return;
        }
        if (getRoot().getKeys().size() == maxKeys) {
            /*
            in this case we will add no keys in the root and we will call the "split" subroutine which will
            put the median element in the parent node ,namely the root
             */
            IBTreeNode<K, V> newRoot = new BTreeNode<>();
            List<IBTreeNode<K, V>> newRootChildren = new ArrayList<>();
            newRootChildren.add(getRoot());
            newRoot.setLeaf(false);
            newRoot.setChildren(newRootChildren);
            newRoot.setValues(new ArrayList<>());
            newRoot.setKeys(new ArrayList<>());
            newRoot.setNumOfKeys(1);
            root = newRoot;
            split(root, 0);
        }
        insertNonFull(root, key, value);
    }

    private void insertNonFull(IBTreeNode<K, V> node, K key, V value) {
        List<K> keys = node.getKeys();
        if (node.isLeaf()) {
            treeUtility.addEntry(node, key, value);
        } else {
            int i = 0;
            int n = node.getKeys().size();
            while (i < n && key.compareTo(keys.get(i)) > 0)
                i++;
            if (node.getChildren().get(i).getKeys().size() == maxKeys) {
                split(node, i);
                if (key.compareTo(keys.get(i)) > 0) i++;
            }
            insertNonFull(node.getChildren().get(i), key, value);
        }
    }

    @Override
    public V search(K key) {
        InputChecker.checkNullValue(key);
        return searchHelper(getRoot(), key);
    }

    private V searchHelper(IBTreeNode<K, V> node, K key) {
        if (node == null || node.getKeys() == null)
            return null;
        int i = 0;
        List<K> keys = node.getKeys();
        while (i < node.getKeys().size() && key.compareTo(node.getKeys().get(i)) > 0) {
            i++;
        }
        if (i < keys.size() && key.compareTo(node.getKeys().get(i)) == 0) {
            return node.getValues().get(i);
        } else if (node.isLeaf()) {
            return null;
        } else
            return searchHelper(node.getChildren().get(i), key);
    }


    @Override
    public boolean delete(K key) {
        InputChecker.checkNullValue(key);
        if (search(key) == null) return false;
        delete(getRoot(), key);
        return true;
    }

    private boolean delete(IBTreeNode<K, V> node, K key) {
        if (node == null) return false;
        int i = 0;
        List<K> keys = node.getKeys();
        int n = keys.size();
        while (i < n && key.compareTo(keys.get(i)) > 0) {
            i++;
        }
        if (i < n && key.compareTo(keys.get(i)) == 0) {
            if (node.isLeaf()) {
                return treeUtility.deleteEntry(node, key);
            } else if (node.getChildren().get(i + 1).getKeys().size() > minKeys) {
                IBTreeNode<K, V> successor = treeUtility.getSuccessor(node, i);
                editEntry(node, successor.getKeys().get(0), successor.getValues().get(0), i);
                return delete(node.getChildren().get(i + 1), successor.getKeys().get(0));
            } else if (node.getChildren().get(i).getKeys().size() > minKeys) {
                IBTreeNode<K, V> predecessor = treeUtility.getPredecessor(node, i);
                int last = predecessor.getKeys().size() - 1;
                editEntry(node, predecessor.getKeys().get(last), predecessor.getValues().get(last), i);
                return delete(node.getChildren().get(i), predecessor.getKeys().get(last));

            } else {
                if (node == getRoot() && node.getKeys().size() == 1) {
                    modifyStructure();
                    return delete(getRoot(), key);
                } else {
                    treeUtility.merge(node, i);
                    return delete(node, key);
                }
            }

        } else {
            if (node.isLeaf()) return false;
            // the index of the next child is i
            if (node.getChildren().get(i).getKeys().size() == minKeys) {
                if (!borrowFromLeftSibling(node, i) && !borrowFromRightsibling(node, i)) {
                    if (node == getRoot() && node.getKeys().size() == 1) {
                        modifyStructure();
                        return delete(getRoot(), key);
                    } else {
                        if (i == n)
                            treeUtility.merge(node, i - 1);
                        else
                            treeUtility.merge(node, i);

                        return delete(node, key);
                    }

                } else {
                    // in case we were managed to borrow a key
                    return delete(node, key);
                }
            } else {
                return delete(node.getChildren().get(i), key);
            }
        }
    }

    /**
     * This fuction is rarely called and basically it merge the root and both of the children in one node
     */
    void modifyStructure() {
        IBTreeNode<K, V> newRoot = new BTreeNode<>();
        List<IBTreeNode<K, V>> newChildren = getRoot().getChildren().get(0).getChildren();
        List<K> newKeys = getRoot().getChildren().get(0).getKeys();
        List<V> newVal = getRoot().getChildren().get(0).getValues();
        newKeys.add(getRoot().getKeys().get(0));
        newVal.add(getRoot().getValues().get(0));

        for (int i = 0; i < getRoot().getChildren().get(1).getKeys().size(); i++) {
            newKeys.add(getRoot().getChildren().get(1).getKeys().get(i));
            newVal.add(getRoot().getChildren().get(1).getValues().get(i));
        }

        if (newChildren != null) {
            for (IBTreeNode<K, V> child : getRoot().getChildren().get(1).getChildren())
                newChildren.add(child);
        }
        newRoot.setKeys(newKeys);
        newRoot.setValues(newVal);
        newRoot.setChildren(newChildren);
        newRoot.setNumOfKeys(newKeys.size());
        newRoot.setLeaf(newChildren == null);
        root = newRoot;
    }
    /*
    ======================================================================================================================
    Helpful methods to get stuff done and reduce dublicated code in the Class
     */

    /**
     * @param parent the parent whose child is full and
     * @param idx    the index of the child that needs to be splitted
     *               The method assumes that the parent is not full :)
     */
    private void split(IBTreeNode<K, V> parent, int idx) {
        if (parent == null || parent.getChildren() == null || parent.getChildren().get(idx) == null) {
            System.out.println("Unexpected corrupted data in the split function");
            LocalException.throwRunTimeErrorException();
        }
        IBTreeNode<K, V> target = parent.getChildren().get(idx);
        IBTreeNode<K, V> newNode = new BTreeNode<>();
        newNode.setLeaf(target.isLeaf());
        /*
        Fill the new node
         */
        List<K> keys = new ArrayList<>();
        List<V> val = new ArrayList<>();
        List<K> newTargetKeys = new ArrayList<>();
        List<V> newTargetValues = new ArrayList<>();
        K medianKey = target.getKeys().get(minimumDegree - 1);
        V medianValue = target.getValues().get(minimumDegree - 1);

        for (int i = 0; i < minimumDegree - 1; i++) {
            keys.add(target.getKeys().get(i + minimumDegree));
            val.add(target.getValues().get(i + minimumDegree));

            newTargetKeys.add(target.getKeys().get(i));
            newTargetValues.add(target.getValues().get(i));
        }
        newNode.setKeys(keys);
        newNode.setValues(val);
        newNode.setNumOfKeys(keys.size());

        target.setKeys(newTargetKeys);
        target.setValues(newTargetValues);
        target.setNumOfKeys(keys.size());
        if (!newNode.isLeaf()) {
            List<IBTreeNode<K, V>> children = new ArrayList<>();
            List<IBTreeNode<K, V>> newTargetChildren = new ArrayList<>();
            for (int i = 0; i < minimumDegree; i++) {
                children.add(target.getChildren().get(i + minimumDegree));
                newTargetChildren.add(target.getChildren().get(i));
            }
            target.setChildren(newTargetChildren);
            newNode.setChildren(children);
        }
        /*
        Shift parent nodes so that we make the median place ready for the median element
         */
        List<K> parentKeys = parent.getKeys();
        List<V> parentValues = parent.getValues();
        List<IBTreeNode<K, V>> children = parent.getChildren();
        children.add(idx + 1, newNode);
        parentKeys.add(idx, medianKey);
        parentValues.add(idx, medianValue);

        parent.setValues(parentValues);
        parent.setChildren(children);
        parent.setKeys(parentKeys);
        parent.setNumOfKeys(parent.getKeys().size());

    }


    /*
    Here is a list of facts
        1 - leaf node will have leaf sibling
     */
    private boolean borrowFromLeftSibling(IBTreeNode<K, V> parent, int childrenIndex) {
        if (childrenIndex == 0) return false;
        IBTreeNode<K, V> sibling = parent.getChildren().get(childrenIndex - 1);
        if (sibling.getKeys().size() > minKeys) {
            treeUtility.addEntry(parent.getChildren().get(childrenIndex), parent.getKeys().get(childrenIndex - 1), parent.getValues().get(childrenIndex - 1));
            int largestKeyIndex = sibling.getKeys().size() - 1;
            editEntry(parent, sibling.getKeys().get(largestKeyIndex), sibling.getValues().get(largestKeyIndex), childrenIndex - 1);
            deleteEntryFromLeafNode(sibling, largestKeyIndex);

            if (!sibling.isLeaf()) {
                int last = sibling.getChildren().size() - 1;
                IBTreeNode<K, V> lastChild = sibling.getChildren().get(last);
                deleteChild(sibling, last);
                addChild(parent.getChildren().get(childrenIndex), 0, lastChild);
            }
            return true;
        } else {
            return false;
        }
    }

    private void deleteChild(IBTreeNode<K, V> node, int idx) {
        if (node.isLeaf()) return;
        List<IBTreeNode<K, V>> children = node.getChildren();
        children.remove(idx);
        node.setChildren(children);

    }

    private void addChild(IBTreeNode<K, V> node, int idx, IBTreeNode<K, V> newChild) {
        if (newChild == null) return;
        List<IBTreeNode<K, V>> children = node.getChildren();
        if (idx == children.size())
            children.add(newChild);
        else
            children.add(idx, newChild);

        node.setChildren(children);
    }

    private boolean borrowFromRightsibling(IBTreeNode<K, V> parent, int childrenIndex) {
        if (childrenIndex == parent.getChildren().size() - 1) return false;
        IBTreeNode<K, V> sibling = parent.getChildren().get(childrenIndex + 1);
        if (sibling.getKeys().size() > minKeys) {
            IBTreeNode<K, V> cur = parent.getChildren().get(childrenIndex);
            treeUtility.addEntry(cur, parent.getKeys().get(childrenIndex), parent.getValues().get(childrenIndex));
            editEntry(parent, sibling.getKeys().get(0), sibling.getValues().get(0), childrenIndex);
            deleteEntryFromLeafNode(sibling, 0);
            if (!cur.isLeaf()) {
                IBTreeNode<K, V> childToBeMoved = sibling.getChildren().get(0);
                deleteChild(sibling, 0);
                addChild(cur, cur.getChildren().size(), childToBeMoved);
            }
            return true;
        } else {
            return false;
        }
    }


    private void editEntry(IBTreeNode<K, V> node, K newKey, V newValue, int index) {
        List<K> keys = new ArrayList<>();
        List<V> values = new ArrayList<>();

        for (int i = 0; i < node.getKeys().size(); i++) {
            if (i == index) {
                keys.add(newKey);
                values.add(newValue);
            } else {
                keys.add(node.getKeys().get(i));
                values.add(node.getValues().get(i));
            }
        }
        node.setKeys(keys);
        node.setValues(values);

    }

    void getAllKeys(IBTreeNode<K, V> root, List<K> res) {
        if (root == null) return;
        else {
            for (int i = 0; i < root.getKeys().size(); i++)
                res.add(root.getKeys().get(i));
            if (root.isLeaf() == false)
                for (IBTreeNode<K, V> child : root.getChildren())
                    getAllKeys(child, res);
            else return;
        }
    }


    private void deleteEntryFromLeafNode(IBTreeNode<K, V> node, int idx) {
        List<K> keys = node.getKeys();
        List<V> val = node.getValues();
        keys.remove(idx);
        val.remove(idx);

        node.setKeys(keys);
        node.setValues(val);
        node.setNumOfKeys(keys.size());

    }

    public void checkAllIsOk(IBTreeNode<K, V> node) {
        if (node == null || node.isLeaf()) return;
        int numKeys = node.getKeys().size();
        int numChild = node.getChildren().size();
        if (numChild - numKeys != 1) {
            System.out.println("YOUR BTREE IS VIOLATED ,BROTHER ");
        }
        for (IBTreeNode<K, V> child : node.getChildren()) checkAllIsOk(child);
    }

    public static void main(String[] args) {
        List<Integer> input = Arrays.asList(new Integer[]{1, 3, 7, 10, 11, 13, 14, 15, 18, 16, 19, 24, 25, 26, 21, 4, 5, 20, 22, 2, 17, 12, 6});
//        List<Integer> input = Arrays.asList(new Integer[]{1, 11, 5, 14, 7, 13, 20, 4, 12, 26, 24, 3, 15, 2, 18, 6, 21, 19, 22, 16, 17, 25, 10});
//        List<Integer> del = Arrays.asList(new Integer[]{14, 20, 3, 24, 13, 26, 11, 2, 22, 10, 7, 4, 6, 1, 12, 21, 17, 15, 19, 18, 16, 25, 5});
        // List<Integer> del = Arrays.asList(new Integer[]{13, 17, 12, 25, 11, 22, 24, 1, 2, 15, 21, 4, 16, 10, 14, 19, 3, 20, 18, 7, 5, 6, 26});
        List<Integer> del = Arrays.asList(new Integer[]{19, 10, 2, 20, 16, 22, 17, 5, 6, 24, 26, 3, 7, 12, 15, 4, 21, 1, 13, 25, 11, 18, 14});


        BTree<Integer, String> btree = new BTree<>(3);

        for (int i = 0; i < input.size(); i++)
            btree.insert(input.get(i), "Soso" + input.get(i));


        for (int i = -1; i < del.size(); i++) {
            if (i > -1) {
                if (i == 1) {
                    int x = 5;
                }
                btree.delete(del.get(i));
            }

            List<Integer> keys = new ArrayList<>();
            btree.getAllKeys(btree.getRoot(), keys);
            Collections.sort(keys);
            for (int j : keys) System.out.print(j + " ");
            System.out.println();
        }
    }

}
