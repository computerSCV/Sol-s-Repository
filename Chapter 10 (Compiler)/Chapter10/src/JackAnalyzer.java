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
        /* 전체 디렉토리 분석일 떄 */
        String directoryPath = args[0];
        System.out.println("directoryPath : " + directoryPath);
        File file;
        try {
            file = new File(directoryPath);
            /* 디렉토리 내 java 파일 모두 읽음 */
            if (file.isDirectory()) {
                String[] fileList = file.list();
                for (String eachFile : fileList) {
                    if (eachFile.contains(".jack")) {
                        System.out.println("sensed file : " + eachFile);
                        String inputFilePath = directoryPath + "\\" + eachFile;
                        String outputFilePath = directoryPath + "\\(JackTokenized)" + eachFile;
                        File inputFile = new File(inputFilePath);
                        File outputFile = new File(outputFilePath);
                        CompilationEngine compilationEngine = new CompilationEngine(inputFile, outputFile);
                    }
                }
            } else {
                /* 지정 파일만 읽음*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
