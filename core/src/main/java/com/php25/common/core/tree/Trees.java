package com.php25.common.core.tree;

import com.php25.common.core.exception.Exceptions;
import com.php25.common.core.util.AssertUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author penghuiping
 * @date 2020/7/1 15:02
 */
public class Trees {
    /**
     * 构建树
     *
     * @param nodes 一维数组转链表树
     * @param <T>   节点数据类型
     * @return
     */
    public static <T extends TreeAble<?>> TreeNode<T> buildTree(List<T> nodes) {
        AssertUtil.notEmpty(nodes, "树节点不能为空");
        LinkedList<TreeNode<T>> nodes0 = mapFrom(nodes);
        T rootData = findRoot(nodes);
        if (rootData == null) {
            throw Exceptions.throwIllegalStateException("nodes中无法找出根节点");
        }
        TreeNode<T> root = new TreeNode<>(rootData, null);
        getChildren(nodes0, root);
        return root;
    }

    /**
     * 从树中找出本节点的子孙节点(包含本节点)
     *
     * @param rootNode
     * @param node
     * @param <T>
     * @return
     */
    public static <T extends TreeAble<?>> List<T> getAllSuccessorNodes(TreeNode<T> rootNode, T node) {
        List<T> list = new ArrayList<>();
        TreeNode<T> node1 = find(rootNode, node);
        visit(node1, (tTreeNode, context) -> {
            list.add(tTreeNode.getData());
        }, new HashMap<>(16));
        return list;
    }

    /**
     * 获取树中本节点的所有祖先节点(包含本节点)
     *
     * @param rootNode
     * @param node
     * @param <T>
     * @return
     */
    public static <T extends TreeAble<?>> List<T> getAllPredecessor(TreeNode<T> rootNode, T node) {
        List<T> list = new ArrayList<>();
        TreeNode<T> node1 = find(rootNode, node);
        while (true) {
            list.add(node1.getData());
            if (node1.getParent() == null) {
                break;
            }
            node1 = node1.getParent();
        }
        return list;
    }

    /***
     * 访问遍历树节点，从上至下，从左到右
     * @param node
     * @param handler
     */
    public static <T extends TreeAble<?>, S> void visit(TreeNode<T> node, VisitHandler<TreeNode<T>, S> handler, S context) {
        handler.handle(node, context);
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            node.getChildren().forEach(child -> {
                visit(child, handler, context);
            });
        }
    }


    /**
     * 是否是叶子节点
     *
     * @param node 树节点
     * @param <T>  节点数据类型
     * @return
     */
    public static <T extends TreeAble<?>> boolean isLeafNode(TreeNode<T> node) {
        return node.getChildren() == null || node.getChildren().isEmpty();
    }

    private static <T extends TreeAble<?>> void getChildren(LinkedList<TreeNode<T>> nodes, TreeNode<T> parent) {
        AssertUtil.notNull(parent, "树父节点不能为空");
        LinkedList<TreeNode<T>> children = new LinkedList<>();
        Iterator<TreeNode<T>> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            TreeNode<T> treeNode = iterator.next();
            if (null != treeNode.getParent() && treeNode.getParent().equals(parent)) {
                children.add(treeNode);
                iterator.remove();
            }
        }
        parent.setChildren(children);
        if (!children.isEmpty()) {
            children.forEach(tTreeNode -> {
                getChildren(nodes, tTreeNode);
            });
        }
    }

    private static <T extends TreeAble<?>> T findRoot(List<T> nodes) {
        return nodes.stream().filter(t -> t.getParentId() == null).findFirst().orElse(null);
    }

    private static <T extends TreeAble<?>> LinkedList<TreeNode<T>> mapFrom(List<T> nodes) {
        List<TreeNode<T>> treeNodes = nodes.stream().map(t -> new TreeNode<>(t, null)).collect(Collectors.toList());

        //设置parent
        for (int i = 0; i < treeNodes.size(); i++) {
            TreeNode<T> node = treeNodes.get(i);
            for (int j = 0; j < treeNodes.size(); j++) {
                TreeNode<T> nodeParent = treeNodes.get(j);
                if (null == node.getData().getParentId()) {
                    node.setParent(null);
                    break;
                }

                if (node.getData().getParentId().equals(nodeParent.getData().getId())) {
                    node.setParent(nodeParent);
                    break;
                }
            }
        }
        return new LinkedList<>(treeNodes);
    }


    private static <T extends TreeAble<?>> TreeNode<T> find(TreeNode<T> rootNode, T node) {
        if (rootNode.getData() != null && rootNode.getData().getId().equals(node.getId())) {
            return rootNode;
        }

        TreeNode<T> result = null;
        if (rootNode.getChildren() != null && !rootNode.getChildren().isEmpty()) {
            for (int i = 0; i < rootNode.getChildren().size(); i++) {
                TreeNode<T> node1 = rootNode.getChildren().get(i);
                result = find(node1, node);
                if (null != result) {
                    break;
                }
            }
        }
        return result;
    }
}
