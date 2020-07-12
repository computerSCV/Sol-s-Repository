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
                if (currentLine.length() <= 0 || currentLine.charAt(0) == '/') {
                    continue;
                }
                char[] charList = currentLine.toCharArray();
                String real = "";
                for (char each : charList) {
                    if (each != '/') {
                        real += each;
                    } else {
                        break;
                    }
                }
                currentLine = real;
                lineList.add(currentLine.trim());
//                System.out.println(currentLine.trim());
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
        System.out.println(tokens);
    }

    private void getToken() { // customized method
        /* 토큰 담기 */
        for (String eachLine : lineList) {
            int charPointer = 0;
            StringBuilder stringBuilder = new StringBuilder();
            while (charPointer < eachLine.length()) {
                char target = eachLine.charAt(charPointer);
                // target => " 일 때 String 문자열이므로 다음 " 까지 통째로 토큰화 하도록 수정
                if ((target >= 97 && target <= 122) || (target >= 65 && target <= 90)) {
                    stringBuilder.append(target);
                } else {
                    if (stringBuilder.length() > 0) {
                        token = stringBuilder.toString();
                        addToken();
                    }
                    if (target != ' ') {
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
            case "int_const":
                tokens.add("<" + tokenType() + "> " + intVal() + " </" + tokenType() + ">");
                break;
            case "string_const":
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
            } else {

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
            return "int_const";
        }
        if (token.charAt(0) == '"') {
            return "string_const";
        }
        return "identifier";
    }

    public String keyword() {
        return token;
    }

    public Character symbol() {
        return token.charAt(0);
    }

    public String identifier() {
        return token;
    }

    public int intVal() {
        return Integer.parseInt(token);
    }

    public String stringVal() {
        return token;
    }

    private boolean checkIfInteger(String token) { // extra method
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void pointerBackward() {
        tokenPointer--;
    }
}
