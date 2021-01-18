package com.php25.common.db.core.manager;

import com.php25.common.db.core.annotation.GeneratedValue;
import com.php25.common.db.core.annotation.SequenceGenerator;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author: penghuiping
 * @date: 2018/8/29 23:30
 * @description:
 */
public class ModelMeta {

    private String logicalTableName;

    private String dbPkName;

    private String classPkName;

    private Field pkField;

    private List<String> classColumns;

    private List<String> dbColumns;

    private List<Class> columnTypes;

    private Field versionField;

    private GeneratedValue generatedValue;

    private SequenceGenerator sequenceGenerator;

    /**
     * 是否存在集合属性
     */
    private Boolean existCollectionAttribute;

    public List<String> getClassColumns() {
        return classColumns;
    }

    public void setClassColumns(List<String> classColumns) {
        this.classColumns = classColumns;
    }

    public List<String> getDbColumns() {
        return dbColumns;
    }

    public void setDbColumns(List<String> dbColumns) {
        this.dbColumns = dbColumns;
    }

    public String getLogicalTableName() {
        return logicalTableName;
    }

    public void setLogicalTableName(String logicalTableName) {
        this.logicalTableName = logicalTableName;
    }



    public String getDbPkName() {
        return dbPkName;
    }

    public void setDbPkName(String dbPkName) {
        this.dbPkName = dbPkName;
    }

    public String getClassPkName() {
        return classPkName;
    }

    public void setClassPkName(String classPkName) {
        this.classPkName = classPkName;
    }

    public Field getPkField() {
        return pkField;
    }

    public void setPkField(Field pkField) {
        this.pkField = pkField;
    }

    public Field getVersionField() {
        return versionField;
    }

    public void setVersionField(Field versionField) {
        this.versionField = versionField;
    }

    public GeneratedValue getGeneratedValue() {
        return generatedValue;
    }

    public void setGeneratedValue(GeneratedValue generatedValue) {
        this.generatedValue = generatedValue;
    }

    public SequenceGenerator getSequenceGenerator() {
        return sequenceGenerator;
    }

    public void setSequenceGenerator(SequenceGenerator sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    public List<Class> getColumnTypes() {
        return columnTypes;
    }

    public void setColumnTypes(List<Class> columnTypes) {
        this.columnTypes = columnTypes;
    }

    public Boolean getExistCollectionAttribute() {
        return existCollectionAttribute;
    }

    public void setExistCollectionAttribute(Boolean existCollectionAttribute) {
        this.existCollectionAttribute = existCollectionAttribute;
    }
}
