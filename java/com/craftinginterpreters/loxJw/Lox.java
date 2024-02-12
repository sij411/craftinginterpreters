package com.craftinginterpreters.loxJw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    static boolean hadError = false;
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1){
            // 인수가 존재하는 경우 파일 경로 지정 후 스크립트 파일 실행
            runFile(args[0]);

        } else {
            // 인수 없는 경우 대화형으로 실행 - 한 줄씩 코드 입력
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        // 파일 경로에서 파일 읽어오기
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        // charset 기준으로 바이트 해석해서 문자열로 반환
        run(new String(bytes, Charset.defaultCharset()));

        // 종료 코드로 에러 식별
        if (hadError) System.exit(65);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        // 프롬프트 구현
        for (;;) {
            System.out.print("> ");
            // > System.out.println("Hello, World"); 식으로 표현됨
            String line = reader.readLine();
            // 더 이상 읽을 라인이 없는 경우 반복문 종료
            if (line == null) break;
            // 해당 라인이 존재하면 실행
            run(line);
            // 플래그 리셋
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // now just print tokens
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where,
            String message) {
        // 에러 발생 시 에러 발생 라인과 메세지 표시
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

}
