import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CompilationEngine {
    FileWriter fileWriter = null;
    JackTokeninzer jackTokeninzer;
    String nextToken;

    public CompilationEngine(File inputFile, File outputFile) {
        jackTokeninzer = new JackTokeninzer(inputFile);
        try {
            fileWriter = new FileWriter(outputFile);
            fileWriter.append("<tokens>\n");
            compileClass();
            fileWriter.append("</tokens>\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String makeTerminalLine() { // extra method : 토큰이 단말일 경우 <타입> terminal </타입>을 반환
        /* 여기서 return값이 tokenType에 따라 jackTokenizer의 keyword, symbol, identifier, intVal, stringVal 중 하나가 되어야 함. */
        jackTokeninzer.advance();
        String terminal = "";
        String tokenType = jackTokeninzer.tokenType();
        switch (tokenType) {
            case "keyword":
                terminal = jackTokeninzer.keyword();
                break;
            case "symbol":
                terminal = "" + jackTokeninzer.symbol();
                break;
            case "int_const":
                terminal = "" + jackTokeninzer.intVal();
                break;
            case "string_const":
                terminal = "" + jackTokeninzer.stringVal();
                break;
            default:
                terminal = jackTokeninzer.identifier();
                break;
        }
        nextToken = jackTokeninzer.nextToken;
        /* "<, >, " , &" 네 가지 특수기호는 각각 바꿔서 출력 */
        switch (terminal) {
            case "<":
                terminal = "&lt";
                break;
            case ">":
                terminal = "&gt";
                break;
            case "\"":
                terminal = "&quot";
                break;
            case "&":
                terminal = "&amp";
                break;
        }
        System.out.println("makeXml : " + "<" + tokenType + ">" + terminal + "</" + tokenType + ">");
        return "<" + tokenType + "> " + terminal + " </" + tokenType + ">\n";
    }

    public void compileClass() {
        System.out.println("<-- 컴파일 클래스 루틴 -->");
        /* "class" className "{" classVarDec* subroutineDec* "}" */
        try {
            fileWriter.append(makeTerminalLine()); // class
            fileWriter.append(makeTerminalLine()); // className -> identifier
            fileWriter.append(makeTerminalLine()); // "{"
            while (nextToken.equals("field") || nextToken.equals("static")) {
                compileClassVarDec();
            }
            while (nextToken.equals("constructor") || nextToken.equals("function") || nextToken.equals("method")) {
                compileSubroutine();
            }
            fileWriter.append(makeTerminalLine()); // "}"
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileClassVarDec() {
        System.out.println("<-- compileClassVarDec() -->");
        /* ("static" | "field") type varName ("," varName)* ";" */
        try {
            fileWriter.append(makeTerminalLine()); // ("static" | "field")
            fileWriter.append(makeTerminalLine()); // type - "int" | "char" | "boolean" | className
            fileWriter.append(makeTerminalLine()); // varName (identifier)
            while (nextToken.equals(",")) {
                fileWriter.append(makeTerminalLine()); // ","
                fileWriter.append(makeTerminalLine()); // varName (identifier)
            }
            fileWriter.append(makeTerminalLine()); // ";"
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileSubroutine() {
        /* ("constructor" | "function" | "method") ("void" | type) subroutineName "(" parameterList ")" subroutineBody */
        System.out.println("<-- compileSubroutine() -->");
        try {
            fileWriter.append(makeTerminalLine()); // ("constructor" | "function" | "method")
            fileWriter.append(makeTerminalLine()); // "void" || type - "int" | "char" | "boolean" | className
            fileWriter.append(makeTerminalLine()); // subroutineName (identifier)
            fileWriter.append(makeTerminalLine()); // "("
            compileParameterList();
            fileWriter.append(makeTerminalLine()); // ")"
            /* subroutineBody */
            fileWriter.append(makeTerminalLine()); // "{"
            while (nextToken.equals("var")) {
                compileVarDec();
            }
            compileStatements();
            fileWriter.append(makeTerminalLine()); // "}"
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileParameterList() {
        System.out.println("<-- compileParameterList() -->");
        try {
            if (!nextToken.equals(")")) {
                fileWriter.append(makeTerminalLine()); // type
                fileWriter.append(makeTerminalLine()); // varName (identifier)
                while (nextToken.equals(",")) {
                    fileWriter.append(makeTerminalLine()); // ","
                    fileWriter.append(makeTerminalLine()); // type
                    fileWriter.append(makeTerminalLine()); // varName (identifier)
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileVarDec() {
        System.out.println("<-- compileVarDec() -->");
        try {
            fileWriter.append(makeTerminalLine()); // "var"
            fileWriter.append(makeTerminalLine()); // type
            fileWriter.append(makeTerminalLine()); // varName (identifier)
            while (nextToken.equals(",")) {
                fileWriter.append(makeTerminalLine()); // ","
                fileWriter.append(makeTerminalLine()); // varName (identifier)
            }
            fileWriter.append(makeTerminalLine()); // ";"
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileStatements() {
        System.out.println("<-- compileStatements() - let, if, while, do, return 중 하나로 이동 -->");
        Set<String> setOfStatements = new HashSet<>();
        String[] statements = {"let", "if", "while", "do", "return"};
        List<String> inputList = Arrays.asList(statements);
        setOfStatements.addAll(inputList);
        try {
            while (setOfStatements.contains(nextToken)) {
                switch (nextToken) {
                    case "let":
                        compileLet();
                        break;
                    case "if":
                        compileIf();
                        break;
                    case "while":
                        compileWhile();
                        break;
                    case "do":
                        compileDo();
                        break;
                    case "return":
                        compileReturn();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void subroutineCall() {
        /* subroutineCall*/
        try {
            fileWriter.append(makeTerminalLine());
            if (nextToken.equals(".")) {
                fileWriter.append(makeTerminalLine());
                fileWriter.append(makeTerminalLine());
            }
            fileWriter.append(makeTerminalLine());
            compileExpressionList();
            fileWriter.append(makeTerminalLine());
            /* subroutineCall*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileDo() {
        System.out.println("<-- compileDo() -->");
        try {
            fileWriter.append(makeTerminalLine()); // "do"
            subroutineCall();
            fileWriter.append(makeTerminalLine()); // ";"
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileLet() {
        System.out.println("<-- compileLet() -->");
        try {
            fileWriter.append(makeTerminalLine()); // "let"
            fileWriter.append(makeTerminalLine()); // varName
            if (nextToken.equals("[")) {
                fileWriter.append(makeTerminalLine()); // "["
                compileExpression();
                fileWriter.append(makeTerminalLine()); // "]"
            }
            fileWriter.append(makeTerminalLine()); // "="
            compileExpression();
            fileWriter.append(makeTerminalLine()); // ";"
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileWhile() {
        System.out.println("<-- compileWhile() -->");
        try {
            fileWriter.append(makeTerminalLine()); // "while"
            fileWriter.append(makeTerminalLine()); // "("
            compileExpression();
            fileWriter.append(makeTerminalLine()); // ")"
            fileWriter.append(makeTerminalLine()); // "{"
            compileStatements();
            fileWriter.append(makeTerminalLine()); // "}"
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileReturn() {
        System.out.println("<-- compileReturn() -->");
        try {
            fileWriter.append(makeTerminalLine()); // "return"
            if (!nextToken.equals(";")) {
                compileExpression();
            }
            fileWriter.append(makeTerminalLine()); // ";"
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileIf() {
        System.out.println("<-- compileIf() -->");
        try {
            fileWriter.append(makeTerminalLine()); // "if"
            fileWriter.append(makeTerminalLine()); // "("
            compileExpression();
            fileWriter.append(makeTerminalLine()); // ")"
            fileWriter.append(makeTerminalLine()); // "{"
            compileStatements();
            fileWriter.append(makeTerminalLine()); // "}"
            if (nextToken.equals("else")) {
                fileWriter.append(makeTerminalLine()); // "else"
                fileWriter.append(makeTerminalLine()); // "{"
                compileStatements();
                fileWriter.append(makeTerminalLine()); // "}"
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileExpression() {
        System.out.println("<-- compileExpression() -->");
        Set<String> opSet = new HashSet<>();
        String[] opArr = {"+", "-", "*", "/", "&", "|", "<", ">", "=", "-", "~"};
        opSet.addAll(Arrays.asList(opArr));
        try {
            System.out.println("compileTerm으로 ----->");
            compileTerm(); // term
            while (opSet.contains(nextToken)) {
                System.out.println("해당");
                fileWriter.append(makeTerminalLine()); // "op"
                compileTerm(); // term
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileTerm() {
        System.out.println("<-- compileTerm() -->");
        try {
            jackTokeninzer.advance();
            String tokenType = jackTokeninzer.tokenType();
            jackTokeninzer.pointerBackward();
            if (tokenType.equals("identifier")) {
                switch (nextToken) {
                    case "[": // varName "[" expression "]"
                        fileWriter.append(makeTerminalLine());
                        fileWriter.append(makeTerminalLine());
                        compileExpression();
                        fileWriter.append(makeTerminalLine());
                        break;
                    case ".": // subroutineCall
                        subroutineCall();
                        break;
                    case "(": // "(" expression ")"
                        fileWriter.append(makeTerminalLine());
                        compileExpression();
                        fileWriter.append(makeTerminalLine());
                        break;
                    default:
                        fileWriter.append(makeTerminalLine());
                        break;
                }
            } else if (tokenType.equals("symbol")) { // unaryOp term
                System.out.println("심볼");
                fileWriter.append(makeTerminalLine());
                compileTerm();
            } else {
                fileWriter.append(makeTerminalLine());
            }
            System.out.println("끝");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileExpressionList() {
        System.out.println("<-- compileExpressionList() -->");
        try {
            if (!nextToken.equals(")")) {
                compileExpression();
                while (nextToken.equals(",")) {
                    compileExpression();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
