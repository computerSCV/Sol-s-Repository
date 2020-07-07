import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;

public class JackAnalyzer {
    public static void main(String[] args) {
        /* file input */
//        String inputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\SquareGame.jack";
//        String inputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\Square.jack";
//        String inputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\Main.jack";
        String directoryPath = args[0];
        File file;
        try {
            file = new File(directoryPath);
            if (file.isDirectory()) {
                String[] fileList = file.list();
                for (String eachFile : fileList) {
                    if (eachFile.contains(".jack")) {
                        String inputFilePath = directoryPath + "\\" + eachFile;
                        String outputFilePath = directoryPath + "\\(JackTokenized)" + eachFile;
                        File inputFile = new File(inputFilePath);
                        File outputFile = new File(outputFilePath);
                        CompilationEngine compilationEngine = new CompilationEngine(inputFile, outputFile);
                    }
                }
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        String outputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\SquareGameOUTPUT.xml";
//        String outputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\SquareOUTPUT.xml";
//        String outputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\MainOUTPUT.xml";
//        File inputFile = new File(inputFilePath);
//        File outputFile = new File(outputFilePath);
//        CompilationEngine compilationEngine = new CompilationEngine(inputFile, outputFile);
    }
}
