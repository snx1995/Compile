package compile.exp1;

import java.io.File;

public class Compile {
    private TokenList tokenList;
    private Tables tables;

    private LexicalAnalyzer lexicalAnalyzer;

    public static void main(String[] args) {
        Compile compile = new Compile();
        compile.start();
    }

    private void start() {
        //debug();
        tokenList = new TokenList();
        tables = new Tables();
        lexicalAnalyzer = new LexicalAnalyzer(tokenList, tables);

        File file = new File("source.b");
        lexicalAnalyzer.analyze(file);
//        lexicalAnalyzer.debug(file);
//        debug();
    }

    private void debug() {
//        System.out.println(Encoding.key_words);
//        System.out.println(Encoding.operators);
//        System.out.println(Encoding.limitors);
//        System.out.println(Encoding.arrays);
        System.out.println(tokenList);
    }
}
