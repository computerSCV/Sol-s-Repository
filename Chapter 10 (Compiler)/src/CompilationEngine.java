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
    StringBuilder space_sb;

    public CompilationEngine(File inputFile, File outputFile) {
        jackTokeninzer = new JackTokeninzer(inputFile);
        space_sb = new StringBuilder();
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
        String token;
        String[] splitToken = jackTokeninzer.token.split(" ");
        String tokenInTheMiddle = splitToken[1];

        /* nextToken 갱신 */
        String[] splitNextToken = jackTokeninzer.nextToken.split(" ");
        nextToken = splitNextToken[1];

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
        token = jackTokeninzer.token;
        System.out.println("makeXml : " + token);
        return token + "\n";
    }

    public void compileClass() {
        System.out.println("<-- 컴파일 클래스 루틴 -->");
        /* "class" className "{" classVarDec* subroutineDec* "}" */
        try {
            fileWriter.append(space_sb.toString()).append("<class>\n");
            space_sb.append(' ');
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // class
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // className
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "{"
            while (nextToken.equals("field") || nextToken.equals("static")) {
                compileClassVarDec();
            }
            while (nextToken.equals("constructor") || nextToken.equals("function") || nextToken.equals("method")) {
                compileSubroutine();
            }
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "}"
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</class>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileClassVarDec() {
        System.out.println("<-- compileClassVarDec() -->");
        /* ("static" | "field") type varName ("," varName)* ";" */
        try {
            fileWriter.append(space_sb.toString()).append("<classVarDec>\n");
            space_sb.append(' ');
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ("static" | "field")
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // type - "int" | "char" | "boolean" | className
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // varName (identifier)
            while (nextToken.equals(",")) {
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ","
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // varName (identifier)
            }
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ";"
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</classVarDec>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileSubroutine() {
        /* ("constructor" | "function" | "method") ("void" | type) subroutineName "(" parameterList ")" subroutineBody */
        System.out.println("<-- compileSubroutine() -->");
        try {
            fileWriter.append(space_sb.toString()).append("<subroutineDec>\n");
            space_sb.append(' ');
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ("constructor" | "function" | "method")
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "void" || type - "int" | "char" | "boolean" | className
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // subroutineName (identifier)
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "("
            compileParameterList();
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ")"
            /* subroutineBody */
            fileWriter.append(space_sb.toString()).append("<subroutineBody>\n");
            space_sb.append(' ');
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "{"
            while (nextToken.equals("var")) {
                compileVarDec();
            }
            compileStatements();
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "}"
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</subroutineBody>\n");
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</subroutineDec>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileParameterList() {
        System.out.println("<-- compileParameterList() -->");
        try {
            fileWriter.append(space_sb.toString()).append("<parameterList>\n");
            space_sb.append(' ');
            if (!nextToken.equals(")")) {
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // type
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // varName (identifier)
                while (nextToken.equals(",")) {
                    fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ","
                    fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // type
                    fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // varName (identifier)
                }
            }
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</parameterList>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileVarDec() {
        System.out.println("<-- compileVarDec() -->");
        try {
            fileWriter.append(space_sb.toString()).append("<varDec>\n");
            space_sb.append(' ');
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "var"
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // type
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // varName (identifier)
            while (nextToken.equals(",")) {
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ","
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // varName (identifier)
            }
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ";"
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</varDec>\n");
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
            space_sb.append(' ');
            fileWriter.append(space_sb.toString()).append("<statements>\n");
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
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</statements>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void subroutineCall() {
        /* subroutineCall*/
        try {
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // className | varName or SubroutineName
            if (nextToken.equals(".")) {
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // '.'
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // subroutineName
            }
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // (
            compileExpressionList();
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // )
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileDo() {
        System.out.println("<-- compileDo() -->");
        try {
            fileWriter.append(space_sb.toString()).append("<doStatement>\n");
            space_sb.append(' ');
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "do"
            subroutineCall();
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ";"
            space_sb.append(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</doStatement>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileLet() {
        System.out.println("<-- compileLet() -->");
        try {
            fileWriter.append(space_sb.toString()).append("<letStatement>\n");
            space_sb.append(' ');
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "let"
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // varName
            if (nextToken.equals("[")) { // 배열이라면
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "["
                compileExpressionList();
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "]"
            }
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "="
            compileExpression();
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ";"
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</letStatement>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileWhile() {
        System.out.println("<-- compileWhile() -->");
        try {
            fileWriter.append(space_sb.toString()).append("<whileStatement>\n");
            space_sb.append(' ');
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "while"
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "("
            compileExpression();
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ")"
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "{"
            compileStatements();
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "}"
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</whileStatement>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileReturn() {
        System.out.println("<-- compileReturn() -->");
        try {
            fileWriter.append(space_sb.toString()).append("<returnStatement>\n");
            space_sb.append(' ');
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "return"
            if (!nextToken.equals(";")) {
                compileExpression();
            }
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ";"
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</returnStatement>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileIf() {
        System.out.println("<-- compileIf() -->");
        try {
            fileWriter.append(space_sb.toString()).append("<ifStatement>\n");
            space_sb.append(' ');
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "if"
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "("
            compileExpression();
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // ")"
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "{"
            compileStatements();
            fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "}"
            if (nextToken.equals("else")) {
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "else"
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "{"
                compileStatements();
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "}"
            }
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</ifStatement>\n");
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
            fileWriter.append(space_sb.toString()).append("<expression>\n");
            space_sb.append(' ');
            System.out.println("compileTerm으로 ----->");
            compileTerm(); // term
            while (opSet.contains(nextToken)) {
                fileWriter.append(space_sb.toString()).append(makeTerminalLine()); // "op"
                compileTerm(); // term
            }
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</expression>\n");
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
            fileWriter.append(space_sb.toString()).append("<term>\n");
            space_sb.append(' ');
            if (tokenType.equals("identifier")) {
                switch (nextToken) {
                    case "[": // varName "[" expression "]"
                        fileWriter.append(space_sb.toString()).append(makeTerminalLine());
                        fileWriter.append(space_sb.toString()).append(makeTerminalLine());
                        compileExpression();
                        fileWriter.append(space_sb.toString()).append(makeTerminalLine());
                        break;
                    case ".": // subroutineCall
                        subroutineCall();
                        break;
                    case "(": // "(" expression ")"
                        fileWriter.append(space_sb.toString()).append(makeTerminalLine());
                        compileExpression();
                        fileWriter.append(space_sb.toString()).append(makeTerminalLine());
                        break;
                    default:
                        fileWriter.append(space_sb.toString()).append(makeTerminalLine());
                        break;
                }
            } else if (tokenType.equals("symbol")) { // unaryOp term
                System.out.println("심볼");
                fileWriter.append(space_sb.toString()).append(makeTerminalLine());
                compileTerm();
            } else {
                fileWriter.append(space_sb.toString()).append(makeTerminalLine());
            }
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</term>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void compileExpressionList() {
        System.out.println("<-- compileExpressionList() -->");
        try {
            space_sb.append(' ');
            fileWriter.append(space_sb.toString()).append("<expressionList>\n");
            if (!nextToken.equals(")")) {
                compileExpression();
                while (nextToken.equals(",")) {
                    compileExpression();
                }
            }
            space_sb.deleteCharAt(space_sb.length() - 1);
            fileWriter.append(space_sb.toString()).append("</expressionList>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
