import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    ArrayList<String> lines;
    int linePointer;
    String currentLine;

    public Parser(String fileName) {
        lines = getLines(fileName);
        currentLine = lines.get(linePointer); // at this line, linePointer is 0
    }

    public String arg1() {
        String commandTypeResult = commandType();
        if (commandTypeResult.equals("C_RETURN")) {
            return "";
        }
        if (commandTypeResult.equals("C_ARITHMETIC")) {
            return currentLine.trim();
        }
        String[] args = currentLine.split(" ");
        return args[1].trim();
    }

    public int arg2() {
        String commandTypeResult = commandType();
        if (commandTypeResult.equals("C_PUSH") || commandTypeResult.equals("C_POP") || commandTypeResult.equals("C_FUNCTION")
                || commandTypeResult.equals("C_CALL")) {
            String[] args = currentLine.split(" ");
            return Integer.parseInt(args[2].trim());
        }
        return 0;
    }

    public String commandType() {
        String[] splited = currentLine.split(" ");
        String command = splited[0].trim();
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
        currentLine = lines.get(linePointer);
    }

    public boolean hasMoreCommands() {
        if (linePointer == lines.size() - 1) {
            return false;
        }
        return true;
    }

    private ArrayList<String> getLines(String inputFile) {
        FileReader fileReader = null;
        Scanner scanner = null;
        linePointer = 0;
        ArrayList<String> lineList = new ArrayList<>();
        try {
            fileReader = new FileReader(inputFile);
            scanner = new Scanner(fileReader);
            String currentLine;
            while (scanner.hasNextLine()) { //지환이한테 true break 물어보기
                currentLine = scanner.nextLine();
                currentLine.trim();
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
        return lineList;
    }
}