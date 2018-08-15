package com.php25.common.core.collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Auther: penghuiping
 * @Date: 2018/6/25 13:36
 * @Description:
 */
public class BinaryTreeMap<Key extends Comparable, Value> {
    private static final Logger logger = LoggerFactory.getLogger(BinaryTreeMap.class);

    private TreeNode head;

    private int size;

    public BinaryTreeMap() {
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(Key key) {
        return search(key) != null;
    }

    public Value get(Key key) {
        TreeNode treeNode = search(key);
        if (null != treeNode) {
            return treeNode.data;
        } else
            return null;
    }

    public Value put(Key key, Value value) {
        if (size == 0) {
            this.head = new TreeNode();
            this.head.setKey(key);
            this.head.setData(value);
            size++;
            return value;
        }

        TreeNode treeNode = this.head;
        //新增的节点永远都可以放到叶子节点上。这样最简单
        while (null != treeNode) {

            if (key.compareTo(treeNode.key) > 0) {
                //比节点数值大，需要放入右子树比较
                //右子树
                if (treeNode.rightChild != null)
                    treeNode = treeNode.rightChild;
                else {
                    //没有右子树,把新增的节点放入这个位置
                    TreeNode tmp1 = new TreeNode();
                    tmp1.setData(value);
                    tmp1.setKey(key);
                    tmp1.setParent(treeNode);
                    treeNode.rightChild = tmp1;
                }
            } else if (key.compareTo(treeNode.key) < 0) {
                //比节点数值小，需要放入左子树比较
                //左子树
                if (treeNode.leftChild != null) {
                    treeNode = treeNode.leftChild;
                } else {
                    //没有左子树，把新增的节点放入这个位置
                    TreeNode tmp1 = new TreeNode();
                    tmp1.setData(value);
                    tmp1.setKey(key);
                    tmp1.setParent(treeNode);
                    treeNode.leftChild = tmp1;
                }
            } else {
                //找到节点
                break;
            }
        }
        size++;
        return treeNode.getData();
    }

    public Value remove(Key key) {
        //查询需要删除的节点
        TreeNode deleteNode = search(key);

        if (deleteNode == null) {
            //key不存在
            return null;
        }

        //判断需要删除的节点是否存在右孩子
        if (deleteNode.rightChild == null) {
            //不存在右孩子
            //如果需要删除的是头节点
            if (this.head.equals(deleteNode)) {
                this.head = deleteNode.leftChild;
                deleteNode.parent = null;
            }

            //如果不存在,直接把删除节点的父节点指向删除节点的左孩子节点
            if (deleteNode.leftChild != null) {
                //删除节点存在左孩子节点
                deleteNode.leftChild.parent = deleteNode.parent;
                if (deleteNode.parent != null) {
                    if (deleteNode.parent.leftChild != null && deleteNode.key.compareTo(deleteNode.parent.leftChild.key) == 0) {
                        //左节点
                        deleteNode.parent.leftChild = deleteNode.leftChild;
                    } else {
                        //右节点
                        deleteNode.parent.rightChild = deleteNode.leftChild;
                    }
                }
            } else {
                //删除节点不存在左孩子节点,既删除节点是叶子节点，直接删除
                if (deleteNode.parent != null) {
                    if (deleteNode.parent.leftChild != null && deleteNode.key.compareTo(deleteNode.parent.leftChild.key) == 0) {
                        //左节点
                        deleteNode.parent.leftChild = null;
                    } else {
                        //右节点
                        deleteNode.parent.leftChild = null;
                    }
                }
            }
        } else {
            //删除节点存在右孩子
            //查询后继节点
            //1. 获取删除节点的右孩子节点
            TreeNode treeNode = deleteNode.rightChild;

            //2. 持续遍历右孩子的左子树的最左边，直到没有左孩子为止，找到后继节点
            while (treeNode.leftChild != null)
                treeNode = treeNode.leftChild;


            //3. 删除节点的父节点孩子指向后继节点
            if (deleteNode.parent != null) {
                if (deleteNode.parent.leftChild != null && deleteNode.key.compareTo(deleteNode.parent.leftChild.key) == 0) {
                    //左节点
                    deleteNode.parent.leftChild = treeNode;
                } else {
                    //右节点
                    deleteNode.parent.rightChild = treeNode;
                }
            }


            if (treeNode.equals(deleteNode.rightChild)) {
                //如果后继节点就是删除节点的右孩子，后继节点的左孩子指向删除节点的左孩子
                //后继节点的父亲节点指向删除节点的父亲节点
                //设置删除节点左孩子的父亲是后继节点
                treeNode.setLeftChild(deleteNode.leftChild);
                if (deleteNode.leftChild != null)
                    deleteNode.leftChild.setParent(treeNode);
                treeNode.setParent(deleteNode.parent);
            } else {
                //1. 后继节点的右孩子的父亲设置为后继节点的父亲，后继节点父亲的左孩子设置为后继节点的右孩子
                //2. 后继节点的左孩子、右孩子指向删除节点的左孩子与右孩子
                //3. 设置删除节点左、右孩子的父亲是后继节点
                //4. 后继节点的父亲节点指向删除节点的父亲节点
                if (treeNode.rightChild != null) {
                    treeNode.rightChild.parent = treeNode.parent;
                    treeNode.parent.leftChild = treeNode.rightChild;
                }

                treeNode.setLeftChild(deleteNode.leftChild);
                treeNode.setRightChild(deleteNode.rightChild);

                if (deleteNode.leftChild != null)
                    deleteNode.leftChild.setParent(treeNode);

                if (deleteNode.rightChild != null)
                    deleteNode.rightChild.setParent(treeNode);

                treeNode.setParent(deleteNode.parent);
            }

            //如果需要删除的是头节,头指针指向后继节点，设置头指针的parent为null
            if (this.head.equals(deleteNode)) {
                this.head = treeNode;
                this.head.parent = null;
            }
        }

        size--;
        return deleteNode.data;
    }

    public void clear() {
        this.head = null;
        this.size = 0;
    }

    private TreeNode search(Key key) {
        TreeNode treeNode = this.head;
        while (treeNode != null) {
            if (key.compareTo(treeNode.key) > 0) {
                //右子树
                treeNode = treeNode.rightChild;
            } else if (key.compareTo(treeNode.key) < 0) {
                //左子树
                treeNode = treeNode.leftChild;
            } else {
                //找到节点
                break;
            }
        }
        return treeNode;
    }

    private void transTree(TreeNode treeNode, StringBuilder sb) {
        if (treeNode == null) return;

        sb.append(treeNode.key).append(",");

        TreeNode left = treeNode.leftChild;
        TreeNode right = treeNode.rightChild;

        if (left != null) {
            transTree(left, sb);
        }

        if (right != null) {
            transTree(right, sb);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        TreeNode treeNode = this.head;
        transTree(treeNode, sb);
        return sb.toString();
    }

    private class TreeNode {
        private Key key;
        private Value data;
        private TreeNode leftChild;
        private TreeNode rightChild;
        private TreeNode parent;

        public TreeNode() {
        }

        public TreeNode getLeftChild() {
            return leftChild;
        }

        public void setLeftChild(TreeNode leftChild) {
            this.leftChild = leftChild;
        }

        public TreeNode getRightChild() {
            return rightChild;
        }

        public void setRightChild(TreeNode rightChild) {
            this.rightChild = rightChild;
        }

        public Key getKey() {
            return key;
        }

        public void setKey(Key key) {
            this.key = key;
        }

        public Value getData() {
            return data;
        }

        public void setData(Value data) {
            this.data = data;
        }

        public TreeNode getParent() {
            return parent;
        }

        public void setParent(TreeNode parent) {
            this.parent = parent;
        }
    }
}
