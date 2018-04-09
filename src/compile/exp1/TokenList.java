package compile.exp1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class TokenList {
    private List<Token> list = new ArrayList<>();


    void add(Token token) {
        list.add(token);
    }

    void add(int code, String identifier) {
        add(new Token(code, identifier));
    }

    void saveToFile(File file) {
        CompileUtils.saveToFile(file, toString());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Token token : list) {
            builder.append(token);
            builder.append('\n');
        }
        return builder.toString();
    }

    class Token {
        int code;
        String identifier;

        public Token(int code, String identifier) {
            this.code = code;
            this.identifier = identifier;
        }

        @Override
        public String toString() {
            return code + " , " + identifier;
        }
    }
}
