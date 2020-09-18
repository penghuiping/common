package com.php25.common.coresample;

import com.google.common.collect.Lists;
import com.php25.common.core.tree.TreeNode;
import com.php25.common.core.tree.Trees;
import com.php25.common.core.util.JsonUtil;
import com.php25.common.coresample.dto.Department;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author penghuiping
 * @date 2020/7/9 10:13
 */
public class TreesTest {

    private static final Logger log = LoggerFactory.getLogger(TreesTest.class);

    @Test
    public void test() {
        Department department = new Department("1", null, "根部门");
        Department department1 = new Department("2", "1", "部门1");
        Department department2 = new Department("3", "1", "部门2");
        Department department3 = new Department("4", "2", "部门3");
        Department department4 = new Department("5", "3", "部门4");

        TreeNode<Department> treeNode = Trees.buildTree(Lists.newArrayList(department, department1, department2, department3, department4));
        List<Department> nodes0 = Trees.getAllSuccessorNodes(treeNode, department1);
        log.info(JsonUtil.toJson(nodes0));
        Assertions.assertThat(nodes0.size()).isEqualTo(2);

        List<Department> nodes1 = Trees.getAllSuccessorNodes(treeNode, department2);
        Assertions.assertThat(nodes1.size()).isEqualTo(2);

        List<Department> nodes2 = Trees.getAllPredecessor(treeNode, department3);
        log.info(JsonUtil.toJson(nodes2));
        Assertions.assertThat(nodes2.size()).isEqualTo(3);


    }

}
