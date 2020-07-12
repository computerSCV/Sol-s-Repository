import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;

public class JackAnalyzer {
    public static void main(String[] args) {
        /* file input */
//        String inputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\SquareGame.jack";
//        String inputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\Square.jack";
//        String inputFilePath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\Main.jack";/

        /* 입력값이 없을 때 / 있을 때로 나눔 */
        String inputPath = "";
        if (args.length == 0) {
            inputPath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ExpressionLessSquare\\Main.jack"; // main을 읽음
            System.out.println("arg[] 입력값 없음");
        } else {
            inputPath = args[0];
            System.out.println("arg[] 입력값 있음");
        }
        File file;
        try {
            file = new File(inputPath);
            /* 디렉토리 내 java 파일 모두 읽음 */
            if (file.isDirectory()) {
                System.out.println("==the input path is a directory");
                String[] fileList = file.list();
                for (String eachFile : fileList) {
                    if (eachFile.contains(".jack")) {
                        System.out.println("sensed file : " + eachFile);
                        String inputFilePath = inputPath + "\\" + eachFile;
                        String outputFilePath = inputPath + "\\(JackTokenized)" + eachFile;
                        File inputFile = new File(inputFilePath);
                        File outputFile = new File(outputFilePath);
                        CompilationEngine compilationEngine = new CompilationEngine(inputFile, outputFile);
                    }
                }
            } else {
                /* 지정 파일만 읽음*/
                System.out.println("==the input path is a file");
                String inputFilePath = inputPath;
                String outputFilePath = inputPath + "(JackTokenized)";
                File inputFile = new File(inputFilePath);
                File outputFile = new File(outputFilePath);
                CompilationEngine compilationEngine = new CompilationEngine(inputFile, outputFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
