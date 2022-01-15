package com.php25.common.core.tree;


import com.google.common.base.Objects;

import java.util.LinkedList;

/**
 * 数节点
 *
 * @author penghuiping
 * @date 2020/7/1 15:01
 */

public class TreeNode<T extends TreeAble<?>> {

    private T data;

    private TreeNode<T> parent;

    private LinkedList<TreeNode<T>> children;

    public TreeNode(T data, TreeNode<T> parent) {
        this.data = data;
        this.parent = parent;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LinkedList<TreeNode<T>> getChildren() {
        return children;
    }

    public void setChildren(LinkedList<TreeNode<T>> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TreeNode<?> treeNode = (TreeNode<?>) o;
        return Objects.equal(data.getId(), treeNode.data.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data.getId());
    }
}
