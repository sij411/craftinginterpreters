package com.craftinginterpreters.loxJw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.craftinginterpreters.loxJw.TokenType.*;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    //  필드 문자열 위치 가리키는 오프셋
    private int start = 0; // 스캔 중인 렉심의 첫 번째 문자
    private int current = 0; // 현재 처리 중
    private int line = 1; // current 위치한 소스 줄 번호

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

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(' : addToken(LEFT_PAREN); break;
            case ')' : addToken(RIGHT_PAREN); break;
            case '{' : addToken(LEFT_BRACE); break;
            case '}' : addToken(RIGHT_BRACE); break;
            case ',' : addToken(COMMA); break;
            case '.' : addToken(DOT); break;
            case '-' : addToken(MINUS); break;
            case '+' : addToken(PLUS); break;
            case ';' : addToken(SEMICOLON); break;
            case '*' : addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=' :
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<' :
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>' :
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/' :
                if (match('/')) {
                    // 주석은 줄 끝까지 이어진다
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r' :
            case '\t' :
                break;
            case '\n':
                line++;
                break;
            case '"' :
                string();
                break;
            default:
              if  (isDigit(c)) {
                  number();
              } else {
                  Lox.error(line, "Unexpected character.");
              }
              break;
        }
    }

    private void number() {
        // 정수부를 찾은 만큼 소비하거나 '.'이 나오면 소수부 찾기
        // 소수부 역시 숫자가 나온 만큼 소비
        while (isDigit(peek())) advance();

        // 소수부를 peek
        if (peek() == '.' && isDigit(peekNext())) {
            // consume "."
            advance();

            while (isDigit(peek())) advance();
        }
        // 스캐너의 로직 : peek를 2번하면서 2개까지의 문자를 룩어헤드 가능하다
        addToken(NUMBER,
                Double.parseDouble(source.substring(start, current)));
        // 렉심 -> 숫자 값으로 변환
    }

    private void string() {
        // 문자열에 "가 나올 때까지 문자 소비
        // 문자열이 끝나기 전에 문자가 소진되면
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        // 에러 로그 출력
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // 닫는 큰따옴표
        advance();

        // 앞뒤 큰 따옴표 제거 -> start = ", current = " 일 것이므로
        String value = source.substring(start + 1, current - 1);
        // 인터프리터가 사용할 실제 문자열 값
        addToken(STRING, value);
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    // 문자를 모두 소비했는지 체크
    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }
    // 현재 렉심의 텍스트를 가져와서 그에 맞는 새 토큰을 만든다.
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
