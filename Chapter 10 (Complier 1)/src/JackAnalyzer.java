import java.io.File;

public class JackAnalyzer {
    public static void main(String[] args) {
        /* file input */
//        String inputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\SquareGame.jack";
//        String inputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\Square.jack";
        String inputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\Main.jack";

//        String outputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\SquareGameOUTPUT.xml";
//        String outputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\SquareOUTPUT.xml";
        String outputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\MainOUTPUT.xml";
        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);
        CompilationEngine compilationEngine = new CompilationEngine(inputFile, outputFile);
    }
}
