import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* className, subroutineName, varName, op, unaryOp, KeywordConstant 등 전부 <> 달아야 함*/

public class CompilationEngine {
    FileWriter fileWriter = null;
    JackTokeninzer jackTokeninzer;
    String nextToken;

    public CompilationEngine(File inputFile, File outputFile) {
        jackTokeninzer = new JackTokeninzer(inputFile);
        try {
            fileWriter = new FileWriter(outputFile);
            compileClass();
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

    private String makeTerminalLine() { // extra method
        jackTokeninzer.advance();
        String token = jackTokeninzer.token;
        String[] splitedToken = jackTokeninzer.token.split(" ");
        String tokenInTheMiddle = splitedToken[1];

        /* nextToken 갱신 */
        String[] splitedNextToken = jackTokeninzer.nextToken.split(" ");
        nextToken = splitedNextToken[1];

        /* "<, >, " , &" 네 가지 특수기호는 각각 바꿔서 출력 */
        switch (tokenInTheMiddle) {
            case "<":
                tokenInTheMiddle = "&lt";
                break;
            case ">":
                tokenInTheMiddle = "&gt";
                break;
            case "\"":
                tokenInTheMiddle = "&quot";
                break;
            case "&":
                tokenInTheMiddle = "&amp";
                break;
        }
        token = splitedToken[0] + tokenInTheMiddle + splitedToken[2];
        System.out.println("makeXml : " + token);
        return token + "\n";
    }

    public void compileClass() {
        System.out.println("<-- 컴파일 클래스 루틴 -->");
        /* "class" className "{" classVarDec* subroutineDec* "}" */
        try {
            fileWriter.append("<class>\n");
            fileWriter.append(makeTerminalLine()); // class
            fileWriter.append(makeTerminalLine()); // className -> identifier
            fileWriter.append("<subroutineBody>"); // <subroutineBody>
            fileWriter.append(makeTerminalLine()); // "{"
            while (nextToken.equals("field") || nextToken.equals("static")) {
                compileClassVarDec();
            }
            while (nextToken.equals("constructor") || nextToken.equals("function") || nextToken.equals("method")) {
                compileSubroutine();
            }
            fileWriter.append(makeTerminalLine()); // "}"
            fileWriter.append("</subroutineBody>");
            fileWriter.append("</class>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileClassVarDec() {
        System.out.println("<-- compileClassVarDec() -->");
        /* ("static" | "field") type varName ("," varName)* ";" */
        try {
            fileWriter.append("<classVarDec>\n");
            fileWriter.append(makeTerminalLine()); // ("static" | "field")
            fileWriter.append(makeTerminalLine()); // type - "int" | "char" | "boolean" | className
            fileWriter.append(makeTerminalLine()); // varName (identifier)
            while (nextToken.equals(",")) {
                fileWriter.append(makeTerminalLine()); // ","
                fileWriter.append(makeTerminalLine()); // varName (identifier)
            }
            fileWriter.append(makeTerminalLine()); // ";"
            fileWriter.append("</classVarDec>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileSubroutine() {
        /* ("constructor" | "function" | "method") ("void" | type) subroutineName "(" parameterList ")" subroutineBody */
        System.out.println("<-- compileSubroutine() -->");
        try {
            fileWriter.append("<subroutineDec>\n");
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
            fileWriter.append("</subroutineDec>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileParameterList() {
        System.out.println("<-- compileParameterList() -->");
        try {
            fileWriter.append("<parameterList>\n");
            if (!nextToken.equals(")")) {
                fileWriter.append(makeTerminalLine()); // type
                fileWriter.append(makeTerminalLine()); // varName (identifier)
                while (nextToken.equals(",")) {
                    fileWriter.append(makeTerminalLine()); // ","
                    fileWriter.append(makeTerminalLine()); // type
                    fileWriter.append(makeTerminalLine()); // varName (identifier)
                }
            }
            fileWriter.append("</parameterList>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileVarDec() {
        System.out.println("<-- compileVarDec() -->");
        try {
            fileWriter.append("<varDec>\n");
            fileWriter.append(makeTerminalLine()); // "var"
            fileWriter.append(makeTerminalLine()); // type
            fileWriter.append(makeTerminalLine()); // varName (identifier)
            while (nextToken.equals(",")) {
                fileWriter.append(makeTerminalLine()); // ","
                fileWriter.append(makeTerminalLine()); // varName (identifier)
            }
            fileWriter.append(makeTerminalLine()); // ";"
            fileWriter.append("</varDec>\n");
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
            fileWriter.append("<statement>\n");
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
            fileWriter.append("</statement>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void subroutineCall() {
        /* subroutineCall*/
        try {
            fileWriter.append("<subroutineCall>\n");
            fileWriter.append(makeTerminalLine());
            if (nextToken.equals(".")) {
                fileWriter.append(makeTerminalLine());
                fileWriter.append(makeTerminalLine());
            }
            fileWriter.append(makeTerminalLine());
            compileExpressionList();
            fileWriter.append(makeTerminalLine());
            /* subroutineCall*/
            fileWriter.append("</subroutineCall>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileDo() {
        System.out.println("<-- compileDo() -->");
        try {
            fileWriter.append("<doStatement>\n");
            fileWriter.append(makeTerminalLine()); // "do"
            subroutineCall();
            fileWriter.append(makeTerminalLine()); // ";"
            fileWriter.append("</doStatement>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileLet() {
        System.out.println("<-- compileLet() -->");
        try {
            fileWriter.append("<letStatement>\n");
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
            fileWriter.append("</letStatement>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileWhile() {
        System.out.println("<-- compileWhile() -->");
        try {
            fileWriter.append("<whileStatement>\n");
            fileWriter.append(makeTerminalLine()); // "while"
            fileWriter.append(makeTerminalLine()); // "("
            compileExpression();
            fileWriter.append(makeTerminalLine()); // ")"
            fileWriter.append(makeTerminalLine()); // "{"
            compileStatements();
            fileWriter.append(makeTerminalLine()); // "}"
            fileWriter.append("</whileStatement>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileReturn() {
        System.out.println("<-- compileReturn() -->");
        try {
            fileWriter.append("<returnStatement>\n");
            fileWriter.append(makeTerminalLine()); // "return"
            if (!nextToken.equals(";")) {
                compileExpression();
            }
            fileWriter.append(makeTerminalLine()); // ";"
            fileWriter.append("</returnStatement>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileIf() {
        System.out.println("<-- compileIf() -->");
        try {
            fileWriter.append("<ifStatement>\n");
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
            fileWriter.append("</ifStatement>\n");
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
            fileWriter.append("<expression>\n");
            System.out.println("compileTerm으로 ----->");
            compileTerm(); // term
            while (opSet.contains(nextToken)) {
                fileWriter.append(makeTerminalLine()); // "op"
                compileTerm(); // term
            }
            fileWriter.append("</expression>\n");
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
            fileWriter.append("<term>\n");
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
            fileWriter.append("</term>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileExpressionList() {
        System.out.println("<-- compileExpressionList() -->");
        try {
            fileWriter.append("<expressionList>\n");
            if (!nextToken.equals(")")) {
                compileExpression();
                while (nextToken.equals(",")) {
                    compileExpression();
                }
            }
            fileWriter.append("</expressionList>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
