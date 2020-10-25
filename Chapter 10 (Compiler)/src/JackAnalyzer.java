import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;

public class JackAnalyzer {
    public static void main(String[] args) {
        String inputPath = "";
        if (args.length == 0) {
//            inputPath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\Square"; // Square directory 를 읽음
            inputPath = "P:\\The Elements of Computing Systems\\nand2tetris\\projects\\10\\ArrayTest"; // ArrayTest directory 를 읽음
            System.out.println("arg[] 입력값 없음");
        } else {
            inputPath = args[0];
            System.out.println("arg[] 입력값 있음");
        }
        setInputAndOutputFile(inputPath);
    }

    public static void setInputAndOutputFile(String inputPath) {
        File[] returnFiles = new File[2];
        File inputFile;
        CompilationEngine compilationEngine;
        try {
            inputFile = new File(inputPath);
            String outputFilePath = "";
            /* 디렉토리 내 java 파일 모두 읽음 */
            if (inputFile.isDirectory()) {
                System.out.println("==the input path is a directory");
                String[] fileList = inputFile.list();
                for (String eachFile : fileList) {
                    if (eachFile.contains(".jack")) {
                        String eachInputFilePath = inputPath + "\\" + eachFile;
                        String[] splitedFileName = eachFile.split(".jack");
                        outputFilePath = inputPath + "\\" + splitedFileName[0] + "(JackTokenized).xml";
                        returnFiles[0] = new File(eachInputFilePath);
                        returnFiles[1] = new File(outputFilePath);
                        compilationEngine = new CompilationEngine(returnFiles[0], returnFiles[1]); // 쓰기
                    }
                }
            } else {
                /* 지정 파일만 읽음*/
                System.out.println("==the input path is a file");
                String[] splitedPath = inputPath.split(".jack");
                outputFilePath += splitedPath[0] + "(JackTokenized).xml";
                System.out.println("outputFilePath " + outputFilePath);
                returnFiles[0] = new File(inputPath);
                returnFiles[1] = new File(outputFilePath);
                compilationEngine = new CompilationEngine(returnFiles[0], returnFiles[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


