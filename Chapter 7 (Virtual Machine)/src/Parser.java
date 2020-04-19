import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    String[] lines;
    int linePointer;
    String currentLine;

    public Parser(String inputFile) {
        lines = getLines(inputFile);
    }

    public String arg1() {
        String commandTypeResult = commandType();
        if (commandTypeResult.equals("C_RETURN")) {
            return "";
        }
        if (commandTypeResult.equals("C_ARITHMETIC")) {
            return currentLine;
        }
        String[] args = currentLine.split(" ");
        return args[1];
    }

    public int arg2() {
        String commandTypeResult = commandType();
        if (commandTypeResult.equals("C_PUSH") || commandTypeResult.equals("C_POP") || commandTypeResult.equals("C_FUNCTION")
                || commandTypeResult.equals("C_CALL")) {
            String[] args = currentLine.split(" ");
            return Integer.parseInt(args[2]);
        }
        return 0;
    }

    public String commandType() {
        String[] splited = currentLine.split(" ");
        String command = splited[0];
        if (command.equals("push")) {
            return "C_PUSH";
        }
        if (command.equals("pop")) {
            return "C_POP";
        }
        if (command.equals("label")) {
            return "C_LABEL";
        }
        if (command.equals("goto")) {
            return "C_GOTO";
        }
        if (command.equals("if-goto")) {
            return "C_IF";
        }
        if (command.equals("function")) {
            return "C_FUNCTION";
        }
        if (command.equals("return")) {
            return "C_RETURN";
        }
        if (command.equals("call")) {
            return "C_CALL";
        }
        return "C_ARITHMETIC";
    }

    public void advance() {
        linePointer++;
        currentLine = lines[linePointer];
    }

    public boolean hasMoreCommands() {
        if (linePointer == lines.length - 1) {
            return false;
        }
        return true;
    }

    private String[] getLines(String inputFile) {
        String[] resultLines;
        FileReader fileReader = null;
        Scanner scanner = null;
        linePointer = 0;
        ArrayList<String> lineList = new ArrayList<>();

        try {
            fileReader = new FileReader(inputFile);
            scanner = new Scanner(fileReader);
            String currentLine = scanner.nextLine();
            while (true) { //지환이한테 true break 물어보기
                currentLine.trim();
                if (currentLine.isBlank() || currentLine.charAt(0) == '/') {
                    currentLine = scanner.nextLine();
                    continue;
                }
                char[] charList = currentLine.toCharArray();
                String real = "";
                for (char each : charList) {
                    if (each != '/') {
                        real += each;
                    }
                }
                currentLine = real;
                lineList.add(currentLine);
                if (!scanner.hasNextLine()) {
                    break;
                }
                currentLine = scanner.nextLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
        resultLines = new String[lineList.size()];
        for (int i = 0; i < lineList.size(); i++) {
            resultLines[i] = lineList.get(i);
        }
        currentLine = resultLines[linePointer];
        return resultLines;
    }


}


