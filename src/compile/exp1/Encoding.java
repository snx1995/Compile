package compile.exp1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Encoding {

    // 各类型的编码
    public static final int KEY_WORD = 0;
    public static final int IDENTIFIER = 1;
    public static final int INTEGER = 2;
    public static final int REAL = 3;
    public static final int BOOLEAN = 4;
    public static final int STRING = 5;
    public static final int OPERATOR = 6;
    public static final int LIMITOR = 7;
    public static final int ANNOTATION = 8;
    public static final int ARRAY = 9;

    public static final Map<String, Integer> key_words = new HashMap<>();   // 关键字集合
    public static final Map<String, Integer> operators = new HashMap<>();   // 操作符集合
    public static final Map<String, Integer> limitors = new HashMap<>();    // 界符集合
    public static final Map<String, Integer> arrays = new HashMap<>();      // 数组类型集合

    static {
        readFromConfigFile();
    }

    private static void readFromConfigFile(){
        BufferedReader reader = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream("encoding.bcfg");
            reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line, item;
            int code;
            while ((line = reader.readLine()) != null) {
                item = line.substring(0, line.length()-2);
                code = Integer.valueOf(line.substring(line.length()-1, line.length()));
                switch (code) {
                    case KEY_WORD:
                        key_words.put(item, code);
                        break;
                    case OPERATOR:
                        operators.put(item, code);
                        break;
                    case LIMITOR:
                        limitors.put(item, code);
                        break;
                    case ARRAY:
                        arrays.put(item, code);
                        break;
                        default:
                            System.out.println("Invalid code found: " + line);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
