import java.io.File;
import java.util.Stack;

public class VirtualMachine {
    public static void main(String[] args) {
        String directoryPath = args[0];
        String fileNameInput = args[1];
        File[] fileList = new File(directoryPath).listFiles();
        CodeWriter codeWriter = new CodeWriter();
        codeWriter.setFileName(fileNameInput);


        Stack<File> stack = new Stack<>();
        File sys_vm = null;
        for (File eachFile : fileList) {
            if (eachFile.getName().equals("Sys.vm")) {
                sys_vm = eachFile;
            }
        }
        for (File eachFile : fileList) {
            if (eachFile.getName().contains(".vm") && !eachFile.getName().equals("Sys.vm")) {
                stack.add(eachFile);
            }
        }
        stack.add(sys_vm);

        while (!stack.isEmpty()) {
            File popedFile = stack.pop();
            String fileName = popedFile.getName();
            String filePath = popedFile.getPath();
            if (filePath.contains(".vm")) {
                if (filePath.contains("Sys.vm")) {
                    codeWriter.writerInit();
                }
                Parser parser = new Parser(filePath);
                codeWriter.fileName = fileName;
                while (true) {
                    String line = parser.currentLine.trim();
                    System.out.println(line);
                    if (line.contains("Sys.init")) {
                        codeWriter.writerInit();
                    }
                    String type = parser.commandType();
                    String arg1 = parser.arg1();
                    int arg2 = parser.arg2();
                    if (type.equals("C_PUSH") || type.equals("C_POP")) {
                        codeWriter.writerPushPop(type, arg1, arg2);
                    } else if (type.equals("C_ARITHMETIC")) {
                        codeWriter.writerArithmetic(line);
                    } else if (type.equals("C_LABEL")) {
                        codeWriter.writeLabel(line);
                    } else if (type.equals("C_IF")) {
                        codeWriter.writeIf(line);
                    } else if (type.equals("C_GOTO")) {
                        codeWriter.writeGoto(line);
                    } else if (type.equals("C_FUNCTION")) {
                        String[] functionLines = line.split(" ");
                        codeWriter.writeFunction(functionLines[1], arg2);
                    } else if (type.equals("C_RETURN")) {
                        codeWriter.writeReturn();
                    } else if (type.equals("C_CALL")) {
                        String[] functionLines = line.split(" ");
                        codeWriter.writeCall(functionLines[1], arg2);
                    }
                    if (!parser.hasMoreCommands()) {
                        break;
                    }
                    parser.advance();
                }
            }
        }
        codeWriter.Close();
    }
}
