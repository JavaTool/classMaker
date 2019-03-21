package org.tool.classMaker.input.reader.cassandra;

import com.alibaba.fastjson.annotation.JSONField;
import com.datastax.driver.core.*;
import com.google.common.collect.*;
import org.tool.classMaker.Utils;
import org.tool.classMaker.input.reader.LineReader;
import org.tool.classMaker.input.struct.*;
import org.tool.classMaker.struct.Access;
import org.tool.classMaker.struct.IClasses;
import org.tool.classMaker.struct.IInterface;

import java.util.*;

public class CassandraReader extends LineReader {

    private static final String KEY_URL = "url";

    private static final String KEY_TABLE = "table";

    private static final String KEY_USERNAME = "username";

    private static final String KEY_PASSWORD = "password";

    private static final String[] KEYS = {KEY_URL, KEY_USERNAME, KEY_PASSWORD, KEY_TABLE};

    private static final String COLUMN_TABLE = "table_name";

    private static final String COLUMN_NAME = "column_name";

    private static final String COLUMN_TYPE = "type";

    private static final String COLUMN_KIND = "kind";

    private static final String SQL = new StringBuilder()
            .append("select ")
            .append(COLUMN_TABLE)
            .append(", ")
            .append(COLUMN_NAME)
            .append(", ")
            .append(COLUMN_TYPE)
            .append(", ")
            .append(COLUMN_KIND)
            .append(" from system_schema.columns where keyspace_name='")
            .toString();

    private final Properties properties = new Properties();

    private String _package;

    public CassandraReader(String a) {}

    @Override
    protected void read(IClasses classes, String line) {
        properties.setProperty(KEYS[properties.size()], line);
    }

    @Override
    protected void readFinish(IClasses classes) {
        Cluster cluster = null;
        ListMultimap<String, FieldInfo> listMap = LinkedListMultimap.create();
        try {
            cluster = Cluster.builder()
                    .addContactPoint(properties.getProperty(KEY_URL))
                    .withAuthProvider(new PlainTextAuthProvider(properties.getProperty(KEY_USERNAME), properties.getProperty(KEY_PASSWORD)))
                    .build();
            Session session = cluster.connect();
            ResultSet rs = session.execute(SQL + properties.getProperty(KEY_TABLE) + "'");
            for (Row row : rs) {
                String tableName = row.getString(COLUMN_TABLE);
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.kind = row.getString(COLUMN_KIND);
                fieldInfo.name = row.getString(COLUMN_NAME);
                fieldInfo.type = row.getString(COLUMN_TYPE);
                listMap.put(tableName, fieldInfo);
            }
        } finally {
            if (cluster != null) cluster.close();
        }

        CMInterface cmInterface = CMStructBuilder.createCMInterface(0);
        cmInterface.setName("Serializable");
        cmInterface.setPackage("java.io");
        classes.getInterfaces().put(cmInterface.getName(), cmInterface);

        createClasses(classes, listMap);
        classes.getInterfaces().remove(cmInterface.getName());
    }

    private void createClasses(IClasses classes, ListMultimap<String, FieldInfo> listMap) {
        IInterface serializable = classes.getInterfaces().get("Serializable");
        for (String tableName : listMap.keySet()) {
            List<FieldInfo> list = listMap.get(tableName);
            CMClass cmClass = CMStructBuilder.createCMClass(list.size(), list.size() << 1);
            cmClass.getAnnotations().add("Table(\"" + tableName + "\")");
            cmClass.getInterfaces().add(serializable);
            cmClass.setName(Utils._ToUppercase(tableName));
            cmClass.setPackage(_package);
            CMImportGroup importGroup = new CMImportGroup();
            importGroup.addImport(CMStructBuilder.createCMImport("org.springframework.data.cassandra.core.mapping.Column"));
            importGroup.addImport(CMStructBuilder.createCMImport("org.springframework.data.cassandra.core.mapping.PrimaryKey"));
            importGroup.addImport(CMStructBuilder.createCMImport("org.springframework.data.cassandra.core.mapping.Table"));
            importGroup.addImport(CMStructBuilder.createCMImport(JSONField.class.getName()));
            cmClass.setImportGroup(importGroup);
            boolean hasList = false, hasSet = false, hasMap = false;
            for (FieldInfo fieldInfo : list) {
                CMField field = new CMField();
                field.setNeedGetter(true);
                field.setNeedSetter(true);
                field.setType(fieldInfo.getJavaType());
                field.setAccess(Access.PRIVATE);
                field.setName(Utils.firstLower(Utils._ToUppercase(fieldInfo.name)));
                if (fieldInfo.isList()) {
                    field.setDefaultValue("Lists.newLinkedList()");
                } else if (fieldInfo.isSet()) {
                    field.setDefaultValue("Sets.newHashSet()");
                } else if (fieldInfo.isMap()) {
                    field.setDefaultValue("Maps.newHashMap()");
                }
                field.setAnnotations(Lists.newLinkedList());
                field.getAnnotations().add(fieldInfo.isPrimaryKey() ? "PrimaryKey" : "Column(value=\"" + fieldInfo.name + "\")");
                field.getAnnotations().add("JSONField(name=\"" + fieldInfo.name + "\")");
                cmClass.getFields().add(field);
                if (!hasList && fieldInfo.isList()) {
                    importGroup.addImport(CMStructBuilder.createCMImport(List.class.getName()));
                    importGroup.addImport(CMStructBuilder.createCMImport(Lists.class.getName()));
                    hasList = true;
                } else if (!hasSet && fieldInfo.isSet()) {
                    importGroup.addImport(CMStructBuilder.createCMImport(Set.class.getName()));
                    importGroup.addImport(CMStructBuilder.createCMImport(Sets.class.getName()));
                    hasSet = true;
                } else if (!hasMap && fieldInfo.isMap()) {
                    importGroup.addImport(CMStructBuilder.createCMImport(Map.class.getName()));
                    importGroup.addImport(CMStructBuilder.createCMImport(Maps.class.getName()));
                    hasMap = true;
                }
                cmClass.getMethods().add(CMStructBuilder.createGetter(field));
                cmClass.getMethods().add(CMStructBuilder.createSetter(field));
            }
            classes.getClasses().put(cmClass.getName(), cmClass);
        }
    }

    @Override
    public void setPackage(String _package) {
        this._package = _package;
    }

    private static class FieldInfo {

        private String name;

        private String type;

        private String kind;

        public String getJavaType() {
            String type = getBaseType(this.type);
            if (type.startsWith("list<")) {
                return "List<" + getBaseType(type.substring("list<".length(), type.length() - 1)) + ">";
            } else if (type.startsWith("set<")) {
                return "Set<" + getBaseType(type.substring("set<".length(), type.length() - 1)) + ">";
            } else if (type.startsWith("map<")) {
                String[] types = type.substring("map<".length(), type.length() - 1).split(",");
                return "Map<" + getBaseType(types[0]) + ", " + getBaseType(types[1]) + ">";
            } else {
                return type;
            }
        }

        private static String getBaseType(String type) {
            switch (type.trim().toLowerCase()) {
                case "text" : case "varchar" : case "ascii" : case "inet" :
                    return "String";
                case "timeuuid" : case "uuid" :
                    return UUID.class.getName();
                case "bigint" : case "timestamp" :
                    return "Long";
                case "int" : case "counter" :
                    return "Integer";
                case "blob" :
                    return "byte[]";
                case "boolean" :
                    return "Boolean";
                case "decimal" : case "float" :
                    return "Float";
                case "double" : case "varint" :
                    return "Double";
                default:
                    return type;
            }
        }

        public boolean isPrimaryKey() {
            return kind.equals("partition_key");
        }

        public boolean isMap() {
            return type.startsWith("map<");
        }

        public boolean isSet() {
            return type.startsWith("set<");
        }

        public boolean isList() {
            return type.startsWith("list<");
        }

    }

}
