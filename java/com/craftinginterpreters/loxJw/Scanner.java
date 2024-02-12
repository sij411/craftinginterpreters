package com.craftinginterpreters.loxJw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.loxJw.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        // 원시 소스 코드를 문자열로 저장 후, 앞으로 생성할 토큰을 하나씩 리스트에 채운다.
        while (!isAtEnd()) {
            // 다음 렉심의 시작부
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
