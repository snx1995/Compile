package compile.exp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class LexicalAnalyzer {

    // 标识符的正则表达式
    // 标识符必须以英文字母或者下划线或者美元符号$开头,并且美元符号
    // 只能出现在标识符的第一个字符
    private static final String identifierRegex = "[a-zA-Z$_][a-zA-Z_]*";
    private static final String ERROR_SPELL = "spell";
    private static final String ERROR_BRACKET_MATCH = "bracket not match";
    private static final String ERROR_STRING_INCOMPLETE = "invalid string";
    private static final String ERROR_INVALID_CHARACTER = "invalid character";
    private static final String ERROR_INVALID_OPERATOR = "invalid operator";

    private TokenList tokenList;    //token串
    private List<String> errorList = new ArrayList<>();
    private Tables tables = new Tables();
    private Stack<Integer> bracketsStack = new Stack<>();

    LexicalAnalyzer(TokenList tokenList, Tables tables) {
        this.tokenList = tokenList;
        this.tables = tables;
    }

    /***
     * 词法分析器的主要方法,接收输入的源代码,进行词法分析
     * @param source 参数source为输入的程序源文件
     */
    void analyze(File source) {
        bracketsStack.clear();
        BufferedReader reader = null;
        int line = 1, col = 0;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(source)));
            int c;
            c = reader.read();
            col++;
            while (c != -1) {
                StringBuilder builder = new StringBuilder();
                builder.append((char)c);
                if (isDigital(c)) {
                    // 如果当前单词以数字开头,那么只可能是整数或者实数
                    // 如果后续读到小数点,则该单词必为实数
                    int code = Encoding.INTEGER;
                    while (isDigital(c = reader.read()) || c == '.') {
                        col++;
                        builder.append((char) c);
                        if (c == '.') {
                            code = Encoding.REAL;
                        }
                    }
                    tokenList.add(code, builder.toString());
                    tables.putConstant(builder.toString(), code);
                } else if (isKeywordOrIdentifierOrBoolean(c)) {
                    // 如果单词以字母或者$或者_开头,有三种情况
                    // 1.该单词是关键字
                    // 2.该单词是标识符
                    // 3.该单词是bool常量true,false
                    // 继续读取接下来的字符,直到出现不可能包含在上述三种情况的字符时停止
                    // 将读取的字符组合成字符串,然后判断该单词的具体分类
                    while (true) {
                        c = reader.read();
                        col++;
                        if (isOperator(c) || isLimitor(c) || c == ' ') break;
                        else builder.append((char)c);
                    }
                    String symbol = builder.toString();
                    if (Encoding.key_words.get(symbol) != null) tokenList.add(Encoding.KEY_WORD, symbol);
                    else if (symbol.equals("false") || symbol.equals("true")) tokenList.add(Encoding.BOOLEAN, symbol);
                    else if (isIdentifier(symbol)) {
                        tokenList.add(Encoding.IDENTIFIER, symbol);
                        tables.putIdentifier(symbol);
                    }
                    else {
                        reportError(line, col, ERROR_SPELL, "invalid identifier: " + symbol);
                    }
                } else if (isLimitor(c)) {
                    // 判断当前字符是否为界符
                    if (c == '@') {
                        // 如果是注释则全部忽略
                        while ((c = reader.read()) != '\n') col++;
                        line++;
                        col = 0;
                    } else {
                        tokenList.add(Encoding.LIMITOR, (char)c + "");
                        if (isBracket(c)) {
                            if (c == '(' || c == '[' || c == '{') bracketsStack.push(c);
                            else if (c == ')' && bracketsStack.peek() == '(') bracketsStack.pop();
                            else if (c == ']' && bracketsStack.peek() == '[') bracketsStack.pop();
                            else if (c == '}' && bracketsStack.peek() == '{') bracketsStack.pop();
                            else reportError(line, col, ERROR_BRACKET_MATCH, " brackets not match.");
                        }
                    }
                    c = reader.read();
                    col++;
                } else if (isOperator(c)) {
                    int cc = reader.read();
                    col++;
                    builder.append((char)cc);
                    if (Encoding.operators.get(builder.toString()) != null) tokenList.add(Encoding.OPERATOR, builder.toString());
                    else {
                        tokenList.add(Encoding.OPERATOR, (char)c + "");
                        c = cc;
                    }
                } else if (isString(c)) {
                    int cc = c;
                    boolean flag = true;
                    while (cc != (c = reader.read())) {
                        col++;
                        if (c == '\n') {
                            reportError(line, col, ERROR_STRING_INCOMPLETE, "string is incomplete: " + builder.toString());
                            flag = false;
                            break;
                        } else {
                            builder.append((char)c);
                        }
                    }
                    if (flag) {
                        builder.append((char)c);
                        tokenList.add(Encoding.STRING, builder.toString());
                        tables.putConstString(builder.toString());
                        c = reader.read();
                        col++;
                    }
                } else if (c == ' ' || c == '\n') {
                    if (c == '\n') {
                        line++;
                        col = 0;
                    }
                    c = reader.read();
                    col++;
                } else {
                    reportError(line, col, ERROR_INVALID_CHARACTER, " invalid character found: " + (char)c);
                    c = reader.read();
                    col++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tokenList.saveToFile(new File("token_list.bd"));

        tables.saveToFile("source");
        reportError();
    }

    private boolean isBracket(int c) {
        return c == '{' || c == '}' || c == '[' || c == ']' || c == '(' || c == ')';
    }

    private boolean isDigital(int c) {
        return c >='0' && c <= '9';
    }

    private boolean isString(int c) {
        return c == '\'' || c == '\"';
    }

    private boolean isKeywordOrIdentifierOrBoolean(int c) {
        return c == '$' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isOperator(int c) {
        return Encoding.operators.get(String.valueOf((char)c)) != null;
    }

    private boolean isLimitor(int c) {
        return Encoding.limitors.get(String.valueOf((char)c)) != null;
    }

    private boolean isAlphaOrUnderline(int c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isIdentifier(String symbol) {
        return Pattern.matches(identifierRegex, symbol);
    }

    private void reportError() {
        for (String s : errorList) {
            System.out.println(s);
        }
    }

    private void reportError(int line, int col, String type, String msg) {
        errorList.add(type + " error found at (" + line +":" + col + "): " + msg);
    }

    void debug(File source) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(source)));
            for (int i=0;i<8;i++) {
                System.out.print((char)reader.read());
            }
            reader.skip(-1);
            System.out.print((char)reader.read());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
