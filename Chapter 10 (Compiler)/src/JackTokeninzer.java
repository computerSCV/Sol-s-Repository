import java.io.File;
import java.io.FileReader;
import java.util.*;

public class JackTokeninzer {
    private int tokenPointer = -1; // advance 하기 전 초기값 -1
    private ArrayList<String> tokens = new ArrayList<>();
    private ArrayList<String> lineList = new ArrayList<>();

    // Generic / Interface
    private Set<String> keywordSet = new HashSet<>();
    private Set<String> symbolSet = new HashSet<>();
    String token;
    String nextToken;

    public JackTokeninzer(File file) {
        String[] keywordArr = {"class", "constructor", "function", "method",
                "field", "static", "var", "int", "char",
                "boolean", "void", "true", "false", "null",
                "this", "let", "do", "if", "else",
                "while", "return"};
        String[] symbolArr = {"{", "}", "(", ")", "[", "]", "."
                , ",", ";", "+", "-", "*", "/", "&"
                , "|", "<", ">", "=", "~"};
        List<String> keywordLs = Arrays.asList(keywordArr);
        List<String> symbolLs = Arrays.asList(symbolArr);

        keywordSet.addAll(keywordLs);
        symbolSet.addAll(symbolLs);

        FileReader fileReader = null;
        Scanner scanner = null;
        String currentLine;
        try {
            fileReader = new FileReader(file);
            scanner = new Scanner(fileReader);
            while (scanner.hasNextLine()) {
                currentLine = scanner.nextLine().trim();
                if (currentLine.length() <= 0 || currentLine.charAt(0) == '/' || currentLine.charAt(0) == '*') {
                    continue;
                }
                char[] charList = currentLine.toCharArray();
                String lineWithoutSpecialSymbols = "";
                for (char each : charList) {
                    lineWithoutSpecialSymbols += each;
                }
                currentLine = lineWithoutSpecialSymbols;
                lineList.add(currentLine.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
            try {
                fileReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getToken();
        System.out.println("======토큰 리스트==");
        for (String element : tokens) {
            System.out.println(element);
        }
    }

    private void getToken() { // customized method
        /* 토큰 담기 */
        for (String eachLine : lineList) {
            int charPointer = 0;
            StringBuilder stringBuilder = new StringBuilder();
            while (charPointer < eachLine.length()) {
                if (charPointer < eachLine.length() - 1 && eachLine.charAt(charPointer) == '/' && eachLine.charAt(charPointer + 1) == '/') {
                    break;
                }
                char target = eachLine.charAt(charPointer);
                // target => " 일 때 String 문자열이므로 다음 " 까지 통째로 토큰화 하도록 수정
                if ((target >= 'A' && target <= 'Z') || (target >= 'a' && target <= 'z') || // 문자이거나
                        (target >= 48 && target <= 58)) { // 숫자이거나
                    stringBuilder.append(target);
                } else {
                    if (stringBuilder.length() > 0) {
                        token = stringBuilder.toString();
                        addToken();
                    }
                    if (target == '"') { // string token 만들기
                        StringBuilder sb = new StringBuilder();
                        sb.append(eachLine.charAt(charPointer++));
                        while (eachLine.charAt(charPointer) != '"') {
                            sb.append(eachLine.charAt(charPointer));
                            charPointer++;
                        }
                        token = sb.toString().trim();
                        addToken();
                    } else if (target != ' ') {
                        token = "" + target;
                        addToken();
                    }
                    stringBuilder = new StringBuilder();
                }
                charPointer++;
            }
        }
    }

    private void addToken() {
        switch (tokenType()) {
            case "keyword":
                tokens.add("<" + tokenType() + "> " + keyword() + " </" + tokenType() + ">");
                break;
            case "symbol":
                tokens.add("<" + tokenType() + "> " + symbol() + " </" + tokenType() + ">");
                break;
            case "integerConstant":
                tokens.add("<" + tokenType() + "> " + intVal() + " </" + tokenType() + ">");
                break;
            case "stringConstant":
                tokens.add("<" + tokenType() + "> " + stringVal() + " </" + tokenType() + ">");
                break;
            default:
                tokens.add("<" + tokenType() + "> " + identifier() + " </" + tokenType() + ">");
                break;
        }
    }

    public boolean hasMoreTokens() {
        if (tokenPointer >= tokens.size() - 1) {
            return false;
        }
        return true;
    }

    public void advance() { // 토큰을 잡아서 토큰 및 토큰 타입을 설정하고 pointer++를 함
        if (hasMoreTokens()) {
            tokenPointer++;
            token = tokens.get(tokenPointer);
            if (tokenPointer < tokens.size() - 1) {
                nextToken = tokens.get(tokenPointer + 1);
            }
        }
    }

    public String tokenType() {
        if (keywordSet.contains(token)) {
            return "keyword";
        }
        if (symbolSet.contains(token)) {
            return "symbol";
        }
        if (checkIfInteger(token)) {
            return "integerConstant";
        }
        if (token.charAt(0) == '"') {
            return "stringConstant";
        }
        return "identifier";
    }

    public String keyword() {
        return token;
    }

    public String symbol() {
        switch (token.charAt(0)) {
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '"':
                return "&quot;";
            case '&':
                return "&amp;";
            default:
                return "" + token.charAt(0);
        }
    }

    public String identifier() {
        return token;
    }

    public int intVal() {
        return Integer.parseInt(token);
    }

    public String stringVal() {
        return token.substring(1); // 맨 앞 '"'를 삭제해야 하므로 substring하여 반환
    }

    private boolean checkIfInteger(String token) { // extra method
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
