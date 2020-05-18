## Chapter 7,8 - Contents Summary & Study Feedback  (20.05.18)



Chapter 7과 8은 **객체지향 고수준 언어용 컴파일러**를 만드는 단계로, 

**중간코드(가상머신 코드)를 어셈블리어**로 번역하는 작업을 할 것이다.

아니, 앞 챕터들에서 기계어, 어셈블리어, 고수준언어에 대한 개념은 한 번씩 다루었지만, 갑자기 '중간코드'는 왜 등장하는가?

그 이유는 바로 **가상머신**의 존재 때문이다.



#### 1. Virtual Machine

내 주력 언어(이자 유일하게 다루는 언어...)인 Java는 JVM이라는 가상머신을 이용하여 컴퓨터에 명령을 내린다.

C와 같이 프로그램 형식으로 직접 컴퓨터를 제어하는 네이티브 언어와는 달리, 

.class 파일로 컴파일하는 과정을 거친 후 .class 파일이 JVM위에서 돌아가면 비로소 컴퓨터에 명령을 내리게 된다.

이처럼 한 단계가 더 있으므로 C보다는 처리 속도가 느리다.

그럼 왜 VM을 사용하는가?

**VM을 왜 사용하는지를 이해하는 것은 꽤나 중요하다.**



#### 2. 2단계로 나누어진 컴파일러

컴파일러는 고수준 언어를 기계어로 번역하는 번역기다.

문제는 고수준 언어를 특정 기계어로 번역하려면 두 언어 각각의 특징에 따라 수많은 컴파일러가 생겨나게 된다는 점이다.

(위 내용과 관련, [본인의 블로그](https://soulfulsol.github.io/technology/2020/03/05/Tech-Blogging-Java-(Part-1)) 에 정리된 내용이 있다)

이러한 종속성을 최소화하는 방법 중 하나는 컴파일 과정을 아래의 두 단계로 분리하는 것이다.

**1) 고수준언어 -> 중간코드**

**2) 중간코드 -> 기계어**

이 방식이 갖는 장점은 다음과 같다.



> VM 언어 구현부분(고수준->중간코드)만 바꾸면 (컴파일러 백엔드) 여러 플랫폼의 컴파일러(중간코드->기계어)들은 상대적으로 쉽게 만들 수 있다.
>
> 여러가지 언어의 컴파일러들이 같은 VM 언어(ex) .class) 를 공유하여, 코드를 공유하고 상호 운용하기 편해진다.
>
> 모듈성을 획득한다. VM 구현이 더 효율적으로 개선될 수록 컴파일러들도 즉각 그 이점을 누리며, VM이 내장된 수많은 소프트웨어도 그 이점을 얻는다.



#### 3. 스택 산술 연산

VM은 **스택**이라는 매우 아름다운 자료구조 위에서 동작한다.

**후입선출**이라는 아주 간단해보이는 원리를 갖는 스택 자료구조는 변수와 메소드를 저장하고 읽으며 재귀함수를 구현하는데 매우 적합한 구조다.

(사실 왜 적합한지 잘 몰랐지만 프로젝트를 수행하면서....정말 이 스택구조를 고안한 사람은 노벨상을 받아야 한다고 생각했다)

스택은 기존 메모리 접근방식과는 달리, 한번 꺼내면(pop) 꺼낸 Data는 (다시 저장하지 않는 한)소실되어 다시 접근할 수 없다. 

그 이유는 **스택 포인터**의 존재 때문인데, 스택 포인터는 메모리위 특정 위치 하나를 가리키고 있으며 현재 가리키고 있는 메모리 주소의 Data를 꺼내거나 담을 수 있도록 만든다.

얼핏 보면 너무나 단순해보이는 이 스택 구조로, 아래와 같은 것들을 모두 구현할 수 있다.

```
산술 명령 : 기본적인 사칙연산

메모리 접근 명령 : 스택과 메모리 세그먼트 사이의 상호작용

프로그램 흐름 명령 : 조건(if) 및 분기(재귀) 연산

함수 호출 명령 : 함수 호출 작업
```

메모리 세그먼트는 argument, local, static, constant 등으로 구성되어 있으며 각각의 세그먼트는 고유의 RAM 주소와 범위를 가진다.

Chapter 7장과 8장은 이 단순하면서도 아름다운 스택구조를 가지고 **Virtual Machine**을 통째로 구현하는 것이었다.

그리고 그 결과물은 아래와 같다.



#### 4. Virtual Machine 구현 : Java

 (1) Virtual Machine

```
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

```

 (2) Parser

```
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
```

 (3) CodeWriter

```
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
```

(소스코드는 [Github]([https://github.com/computerSCV/The-Elements-of-Computing-Systems-Sol-/tree/master/Chapter%207%20(Virtual%20Machine)/src](https://github.com/computerSCV/The-Elements-of-Computing-Systems-Sol-/tree/master/Chapter 7 (Virtual Machine)/src)에 올려놓았다.)



#### 5. Project 스터디 피드백

- 해당 챕터를 하면서, 스택 자료구조를 저절로 익히게 되었다.
- 전역변수, 지역변수, 메소드, 재귀함수 등을 코딩할 때, 스택구조를 머릿속으로 그릴 줄 알게 되었다.
- 문자열 처리 스킬이 늘었고, FileReader, Writer 기능을 사용할 줄 알게 되었다.
- 본 챕터는 역시나 처음엔 챌린지로 다가왔지만, 하면 할수록 순수 코딩으로 VM을 구현한다는 매력에 빠져 너무너무 재미있게 진행하였다. 프로그래밍 입문 10개월차 초보가 어찌저찌 VM을 구현해보다니!
