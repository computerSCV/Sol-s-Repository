import java.io.IOException;

public class VirtualMachine {
    public static void main(String[] args) {
//        String setYourFile = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\07\\StackArithmetic\\StackTest\\StackTest.vm";
//        String setYourFile = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\07\\MemoryAccess\\BasicTest\\BasicTest.vm";
//        String setYourFile = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\07\\MemoryAccess\\PointerTest\\PointerTest.vm";
        String setYourFile = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\07\\MemoryAccess\\StaticTest\\StaticTest.vm";
        Parser parser = new Parser(setYourFile);
        CodeWriter codeWriter = new CodeWriter();
        /* 한 줄씩 읽기 시작 */
        while (true) {
            String line = parser.currentLine;
            String type = parser.commandType();
            String arg1 = parser.arg1();
            int arg2 = parser.arg2();
//            codeWriter.setFimeName("P:\\The Elements of Computing Systems\\nand2tetris\\projects\\07\\StackArithmetic\\StackTest\\StackTest.asm");
//            codeWriter.setFimeName("P:\\The Elements of Computing Systems\\nand2tetris\\projects\\07\\MemoryAccess\\BasicTest\\BasicTest.asm");
//            codeWriter.setFimeName("P:\\The Elements of Computing Systems\\nand2tetris\\projects\\07\\MemoryAccess\\PointerTest\\PointerTest.asm");
            codeWriter.setFimeName("P:\\The Elements of Computing Systems\\nand2tetris\\projects\\07\\MemoryAccess\\StaticTest\\StaticTest.asm");
            if (type.equals("C_PUSH") || type.equals("C_POP")) {
                codeWriter.writerPushPop(type, arg1, arg2);
            } else if (type.equals("C_ARITHMETIC")) {
                codeWriter.writerArithmetic(line);
            }
            if (!parser.hasMoreCommands()) {
                return;
            }
            parser.advance();
        }
    }
}
