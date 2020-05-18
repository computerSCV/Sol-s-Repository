import java.io.FileWriter;

public class CodeWriter {
    FileWriter fileWriter;
    int index = 0;
    int labelIndex = 0;
    String fileName;

    public void setFileName(String filename) {
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
                result = "//lt 시작 \n" + boolCommandFirstSegment +
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
                result += "//static 시작\n";
                result += "@" + fileName + "." + index + "\n" +
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
            } else { // arg, lcl, this, that 일 때
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
                result += pop;
                result += "@5\n";
                for (int i = 0; i < index; i++) {
                    result += "A=A+1\n";
                }
                result += "M=D\n";
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
                        "@" + fileName + "." + index + "\n" +
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

    /* 여기서부터 8장 */

    public void writerInit() {
        String result = "";
        result += "@256\n"
                + "D=A\n"
                + "@SP\n"
                + "M=D\n"; // SP=256 으로 initialize
        try {
            fileWriter.append(result);
            fileWriter.flush();
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        writeCall("Sys.init", 0);
    }

    public void writeLabel(String label) {
        String result = "";
        String labelName = label.split(" ")[1].trim();
        result += "(" + labelName + ")\n";
        try {
            fileWriter.append(result);
            fileWriter.flush();
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    public void writeGoto(String label) {
        String result = "//writeGoto 시작 \n";
        String labelName = label.split(" ")[1].trim();
        result += "@" + labelName + "\n"
                + "0;JMP\n";
        try {
            fileWriter.append(result);
            fileWriter.flush();
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    public void writeIf(String label) {
        String result = "//writeIf 시작\n";
        String labelName = label.split(" ")[1].trim();
        result += "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@" + labelName + "\n" // -1(true)면 점프
                + "D;JLT\n";
//                + "@" + labelName + "_END" + "\n"
//                + "0;JMP\n"
//                + "(" + labelName + "_END)\n";
        try {
            fileWriter.append(result);
            fileWriter.flush();
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    public void writeCall(String functionName, int numArgs) {
        String result = "//writeCall 시작" + functionName + "\n";
        String returnAddress = functionName + "$" + labelIndex;
        String commonPart = "@SP\n"
                + "A=M\n"
                + "M=D\n"
                + "@SP\n"
                + "M=M+1\n";
        /* push return-address, LCL, ARG, THIS, THAT */
        result += "@"
                + returnAddress + "\n"
                + "D=A\n"
                + commonPart
                + "@LCL\n"
                + "D=M\n"
                + commonPart
                + "@ARG\n"
                + "D=M\n"
                + commonPart
                + "@THIS\n"
                + "D=M\n"
                + commonPart
                + "@THAT\n"
                + "D=M\n"
                + commonPart;
        /* ARG = SP - n - 5 */
        result += "@SP\n"
                + "D=M\n";
        for (int i = 0; i < numArgs + 5; i++) {
            result += "D=D-1\n";
        }
        result += "@ARG\n"
                + "M=D\n";
        /* LCL = SP */
        result += "@SP\n"
                + "D=M\n"
                + "@LCL\n"
                + "M=D\n";
        /* goto f */
        result += "@" + functionName + "\n"
                + "0;JMP\n";
        /* (Return-address) */
        result += "(" + returnAddress + ")\n";

        try {
            fileWriter.append(result);
            fileWriter.flush();
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        labelIndex++;
    }

    public void writeFunction(String functionName, int numLocals) {
        String result = "//writeFunction 시작\n"
                + "(" + functionName + ")\n";
        for (int i = 0; i < numLocals; i++) {
            result += "@LCL\n"
                    + "D=M\n";
            for (int j = 0; j < i; j++) {
                result += "D=D+1\n";
            }
            result += "@SP\n"
                    + "M=D\n"
                    + "@SP\n"
                    + "A=M\n"
                    + "M=0\n"
                    + "@SP\n"
                    + "M=M+1\n";
        }
        try {
            fileWriter.append(result);
            fileWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeReturn() {
        String result = "";
        // FRAME = LCL
        result += "@LCL\n"
                + "D=M\n"
                + "@R13\n"
                + "M=D\n"
                + "D=M\n";
        //RET = *(FRAME-5)
        for (int i = 0; i < 5; i++) {
            result += "D=D-1\n";
        }
        result += "A=D\n"
                + "D=M\n"
                + "@R14\n"
                + "M=D\n";

        /*           *ARG = pop()           */
        result += "@SP\n"
                + "M=M-1\n"
                + "A=M\n"
                + "D=M\n"
                + "@ARG\n"
                + "A=M\n"
                + "M=D\n"
                // SP = ARG + 1
                + "@ARG\n"
                + "D=M\n"
                + "@SP\n"
                + "M=D+1\n";

        /*          THAT = *(FRAME-1)        */
        result += "@R13\n"
                + "D=M\n"
                + "A=D-1\n"
                + "D=M\n"
                + "@THAT\n"
                + "M=D\n";

        /*         THIS = *(FRAME-2)        */
        result += "@R13\n"
                + "D=M\n";
        for (int i = 0; i < 2; i++) {
            result += "D=D-1\n";
        }
        result += "A=D\n"
                + "D=M\n"
                + "@THIS\n"
                + "M=D\n";

        /*          ARG = *(FRAME-3)         */
        result += "@R13\n"
                + "D=M\n";
        for (int i = 0; i < 3; i++) {
            result += "D=D-1\n";
        }
        result += "A=D\n"
                + "D=M\n"
                + "@ARG\n"
                + "M=D\n";

        /*         LCL = *(FRAME-4)          */
        result += "@R13\n"
                + "D=M\n";
        for (int i = 0; i < 4; i++) {
            result += "D=D-1\n";
        }
        result += "A=D\n"
                + "D=M\n"
                + "@LCL\n"
                + "M=D\n";

        /*         goto RET            */
        result += "@R14\n"
                + "A=M\n"
                + "0;JMP\n";

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
