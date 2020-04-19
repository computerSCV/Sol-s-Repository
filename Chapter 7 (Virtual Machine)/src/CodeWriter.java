import java.io.FileWriter;

public class CodeWriter {
    FileWriter fileWriter;
    int index = 0;

    public void setFimeName(String filename) {
        try {
            fileWriter = new FileWriter(filename, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writerArithmetic(String command) {
        String result = "";
        String boolCommandFirstSegment =
                "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M-D\n";
        String boolCommandSecondSegment =
                "@1\n" + // true = -1
                        "D=-A\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n";
        String boolCommandThirdSegment =
                "@0\n" + // false = 0
                        "D=A\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n";
        String calculFirstSegment =
                "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n";

        switch (command) {
            case "add":
                result = calculFirstSegment +
                        "M=D+M\n" +
                        "@SP\n" +
                        "M=M+1\n";
                break;
            case "sub":
                result = calculFirstSegment +
                        "M=M-D\n" +
                        "@SP\n" +
                        "M=M+1\n";
                break;
            case "neg":
                result = "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "M=-D\n" +
                        "@SP\n" +
                        "M=M+1\n";
                break;
            case "and":
                result = calculFirstSegment +
                        "M=D&M\n" +
                        "@SP\n" +
                        "M=M+1\n";
                break;
            case "or":
                result = calculFirstSegment +
                        "M=D|M\n" +
                        "@SP\n" +
                        "M=M+1\n";
                break;
            case "not":
                result = "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "M=!D\n" +
                        "@SP\n" +
                        "M=M+1\n";
                break;
            case "eq":
                result = boolCommandFirstSegment +
                        "@EQ" + index + "\n" +
                        "D;JEQ\n" +
                        "@NOTEQ" + index + "\n" +
                        "D;JNE\n" +
                        "(EQ" + index + ")\n" +
                        boolCommandSecondSegment +
                        "@CONTINUE" + index + "\n" +
                        "0;JMP\n" +
                        "(NOTEQ" + index + ")\n" +
                        boolCommandThirdSegment +
                        "(CONTINUE" + index + ")\n";
                break;
            case "lt": // -1(true) if x < y else 0 (false)
                result = boolCommandFirstSegment +
                        "@LT" + index + "\n" +
                        "D;JLT\n" +
                        "@NOTLT" + index + "\n" +
                        "D;JGE\n" +
                        "(LT" + index + ")\n" +
                        boolCommandSecondSegment +
                        "@CONTINUE" + index + "\n" +
                        "0;JMP\n" +
                        "(NOTLT" + index + ")\n" +
                        boolCommandThirdSegment +
                        "(CONTINUE" + index + ")\n";
                break;
            case "gt": // -1(true) if x > y, else 0 (False)
                result = boolCommandFirstSegment +
                        "@GT" + index + "\n" +
                        "D;JGT\n" +
                        "@NOTGT" + index + "\n" +
                        "D;JLE\n" +
                        "(GT" + index + ")\n" +
                        boolCommandSecondSegment +
                        "@CONTINUE" + index + "\n" +
                        "0;JMP\n" +
                        "(NOTGT" + index + ")\n" +
                        boolCommandThirdSegment +
                        "(CONTINUE" + index + ")\n";
                break;
        }
        try {
            fileWriter.append(result);
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        index++;
    }

    public void writerPushPop(String command, String segment, int index) {
        String result = "";
        String parsedSegment = "";
        if (segment.equals("argument") || segment.equals("local") || segment.equals("this") || segment.equals("that")) {
            if (segment.equals("argument")) {
                parsedSegment += "ARG";
            } else if (segment.equals("local")) {
                parsedSegment += "LCL";
            } else if (segment.equals("this")) {
                parsedSegment += "THIS";
            } else {
                parsedSegment += "THAT";
            }
        }
        if (command.equals("C_PUSH")) {
            result = "";
            String push = "@SP\n" + //pop module 코드
                    "A=M\n" +
                    "M=D\n" +
                    "@SP\n" +
                    "M=M+1\n";
            if (segment.equals("constant")) { //constant 일 떄
                result = "@" + index + "\n" +
                        "D=A\n" +
                        push;
            } else if (segment.equals("temp")) { //temp 일 때
                result += "@5\n" +
                        "M=A\n";
                for (int i = 0; i < index; i++) {
                    result += "M=M+1\n";
                }
                result += "A=M\n" +
                        "D=M\n" +
                        push;
            } else if (segment.equals("static")) { //static 일 때
                result += "@" + (index + 16) + "\n" +
                        "D=M\n" +
                        push;
            } else if (segment.equals("pointer")) { //pointer 일 때
                if (index == 0) {
                    result += "@THIS\n" +
                            "D=M\n";
                } else if (index == 1) {
                    result += "@THAT\n" +
                            "D=M\n";
                }
                result += push;
            } else { // arg, lol, this, that 일 때
                result = "@" + parsedSegment + "\n" +
                        "A=M\n";
                for (int i = 0; i < index; i++) {
                    result += "A=A+1\n";
                }
                result += "D=M\n" + push;
            }
        }
        if (command.equals("C_POP")) { //POP일때
            result = "";
            String pop = "@SP\n" + //pop module 코드
                    "M=M-1\n" +
                    "A=M\n" +
                    "D=M\n";
            if (segment.equals("temp")) { //temp일때
                result += "@5\n" +
                        "M=A\n";
                for (int i = 0; i < index; i++) {
                    result += "M=M+1\n";
                }
                result += "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M-1\n";
            } else if (segment.equals("pointer")) { //pointer일 때
                result += pop;
                if (index == 0) {
                    result += "@THIS\n" +
                            "M=D\n";
                } else if (index == 1) {
                    result += "@THAT\n" +
                            "M=D\n";
                }
            } else if (segment.equals("static")) { //static 일 때
                result += pop +
                        "@" + (index + 16) + "\n" +
                        "M=D\n";
            } else { // arg, lol, this, that 일 때
                result += pop +
                        "@" + parsedSegment + "\n" +
                        "A=M\n";
                for (int i = 0; i < index; i++) {
                    result += "A=A+1\n";
                }
                result += "M=D\n";
            }
        }
        try {
            fileWriter.append(result);
            fileWriter.flush();
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    public void Close() {
        try {
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
