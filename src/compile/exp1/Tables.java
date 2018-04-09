package compile.exp1;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Tables {
    private Map<String, Integer> constantTable = new HashMap<>();
    private Map<String, Integer> constStringTable = new HashMap<>();
    private Map<String, Integer> identifierTable = new HashMap<>();


    void putConstant(String s, int type) {
        if (constantTable.containsKey(s)) return;
        else constantTable.put(s, type);
    }

    void putConstString(String s) {
        if (constStringTable.containsKey(s)) return;
        else constStringTable.put(s, Encoding.STRING);
    }

    void putIdentifier(String s) {
        if (identifierTable.containsKey(s)) return;
        else identifierTable.put(s, Encoding.IDENTIFIER);
    }


    private String tableToString(Map<String, Integer> table) {
        StringBuilder result = new StringBuilder();
        String item;
        Set<String> keys = table.keySet();
        for (String key : keys) {
            item = key + "," + table.get(key) + '\n';
            result.append(item);
        }
        return result.toString();
    }

    void saveToFile(String sourceName) {
        File constantFile = new File(sourceName + "_ct.bd");
        File constStringFile = new File(sourceName + "_cst.bd");
        File identifierFile = new File(sourceName + "_it.bd");

        CompileUtils.saveToFile(constantFile, tableToString(constantTable));
        CompileUtils.saveToFile(constStringFile, tableToString(constStringTable));
        CompileUtils.saveToFile(identifierFile, tableToString(identifierTable));
    }
}
