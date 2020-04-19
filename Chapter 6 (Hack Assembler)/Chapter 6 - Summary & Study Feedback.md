## Chapter 6 - Contents Summary & Study Feedback  (20.04.19)



Chapter 5 까지의 내용이 chip 기반의 컴퓨터 플랫폼을 만드는 것이었다면, Chapter 6 부터는 프로그래밍 언어를 통해 **소프트웨어 모듈**을 구현하게 된다. 

그 중 Chapter 6 에서 만들 모듈은 4장에서 다루었던 **Hack Assembler**이며, 우리는 즉 기호로 이루어진 Hack 어셈블리 코드를 2진수로 번역하는 작업을 하게 된다.

본 프로젝트는 Java 언어로 수행하였으며, 어셈블리 구현 코드는 아래에 위치해있다.



#### 1. 기계어 - 기호형 언어

- 기계에게 명령을 내리기 위해 매번 2진수를 써서 명령을 내리는 것은 너무나도 고역이다. 매번 100010101010100과 같은 코드를 쓴다고 생각해보라.

- 이러한 기계어들은 대략 '램주소 5에 해당하는 value를 D레지스터에 로드하라'와 같은 명령체계를 지닌다. 만약 이를 기호로 표현하면 '**Load RAM[5] on Register[D]** ' 와 같은 형태가 될 것이다.

- 이러한 기호형 명령어를 2진수로 '번역'하는 작업이 2진수를 직접 작성하는 것보다 인간에게 훨씬 쉬운 작업이다.

- 따라서 기호형 명령어의 체계가 잘 잡혀있다면, 기호형 명령어를 번역하는 번역기를 개발한다면 컴퓨터에게 보다 쉽게 명령을 내릴 수 있을 것이다.

- 이 기호형 명령어가 바로 **어셈블리어**이며, 어셈블리어를 2진수로 번역하는 것이 **어셈블러**다. 우리가 이 챕터에서 다룰 어셈블리어는 핵 어셈블리어이기에, 우리는 이를 2진수로 번역해주는 **핵 어셈블러**를 Java로 개발할 것이다.

  

#### 2. 기호 변환 규칙과 메모리 주소

- 다음과 같은 High-level 코드가 있다고 해 보자.

  ```java
  int x = 0;
  int y = 100;
  while(x == y){
  	if(x < y){
  		x++;
  	}
  }
  ```

  위 코드는 x, y 라는 변수가 2개 있으며, 명령어는 while, if, x++ 등이 존재한다.

  이러한 코드를 어떻게 기호 변환 규칙에 따라 변환할 수 있을까?

- 교재에서는, '**번역된 코드는 주소 0부터 시작하는 메모리에 저장하고, 변수들은 (임의의)주소 1024부터 할당한다**'는 방식을 취한다.

- 즉 x 는 Memory[1024], y는 Memory[1025]에 각각 할당된다.

- 다른 변수들이 추가된다면? 해당 변수는 1024부터 시작하여 아무것도 할당되어있지 않은 주소에 값이 할당되고, 동시에 '**기호 테이블**'에 해당 변수와 메모리 주소값이 기록된다.

- 따라서 명령어 주소값 0부터 명령어 reading이 진행되면서 해당 변수가 명령어에 입력된다면, 우리는 기호 테이블을 통해 해당 변수의 주소값을 찾아낼 수 있으며, 그 주소로 이동해 value를 얻어낼 수 있을 것이다.

- 1024에서부터 변수값 저장이 시작된다는 것은 이 교재의 단순한 가정일 뿐이며, 프로그래밍 언어 타입, 컴퓨터, 운영체제에 따라 이 규칙은 모두 다르다. 그러나 기본적으로는 같은 메커니즘을 사용한다.

- 핵 어셈블리어를 2진 코드로 번역하려먼 해당 2진코드가 어떤 체계로 기록되는지에 대한 가이드라인이 있어야 한다. 이 내용은 지난 4,5장에서 다루었던 내용이므로 참고하면 된다.

  

#### 3. Hack Assembler 구현

##### 	**(1) Parser	**

```
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

/* File path 가 입력으로 들어온다 */
public class Parser {
	String entirePath;
	String[] lines;
	String currentLine;
	int linePointer;

	public Parser(String entirePath) {
		lines = getLines(entirePath);
	}

	private String[] getLines(String entirePath) {
		String[] lines = {};
		ArrayList<String> lineList = new ArrayList<String>();
		FileReader fileReader = null;
		Scanner scanner = null;

		try {
			fileReader = new FileReader(entirePath);
			scanner = new Scanner(fileReader); // non-memory 자원
			String currentLine = scanner.nextLine();
			int count = 0;
			boolean finish = false;

			while (true) {
				if (scanner.hasNextLine() == false) {
					finish = true;
				}
				if (currentLine.isBlank() || currentLine.charAt(0) == '/') {
					currentLine = scanner.nextLine();
					continue;
				}
				char[] list = currentLine.toCharArray();
				String real = "";
				for (char each : list) {
					if (each == ' ') {
						continue;
					}
					if (each == '/') {
						break;
					}
					real += each;
				}
				currentLine = real;
				if (finish) {
					lineList.add(currentLine);
					count++;
					break;
				}
				lineList.add(currentLine);
				count++;
				currentLine = scanner.nextLine();
			}
			lines = new String[count];
			for (int i = 0; i < lineList.size(); i++) {
				lines[i] = lineList.get(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.linePointer = 0;
		this.currentLine = lines[linePointer];
		return lines;
	}

	public void advance() {
		if (hasMoreCommands() == false) {
			return;
		}
		linePointer++;
		if (linePointer == lines.length) {
			return;
		}
		currentLine = lines[linePointer];
	}

	public boolean hasMoreCommands() {
		if (linePointer < lines.length) {
			return true;
		}
		return false;
	}

	public String commandType() {
		if (currentLine.charAt(0) == '@') {
			return "A_COMMAND";
		}
		if (currentLine.charAt(0) == '(') {
			return "L_COMMAND";
		}
		return "C_COMMAND";
	}

	public String symbol() {
		if (commandType().equals("C_COMMAND")) {
			return null;
		}
		if (commandType().equals("A_COMMAND")) {
			char[] charList = currentLine.toCharArray();
			String result = "";
			for (int i = 1; i < charList.length; i++) {
				result += charList[i];
			}
			return result;
		}

		char[] charList = currentLine.toCharArray();
		String result = "";
		for (int i = 1; i < charList.length - 1; i++) {
			result += charList[i];
		}
		return result;
	}

	public String dest() {
		if (!commandType().equals("C_COMMAND")) {
			return null;
		}
		String result = "";
		if (currentLine.contains(";")) {
			return "0";
		}
		int equalIndex = currentLine.indexOf("=");
		for (int i = 0; i < equalIndex; i++) {
			result += currentLine.charAt(i);
		}
		return result;
	}

	public String comp() {
		if (!commandType().equals("C_COMMAND")) {
			return null;
		}
		String result = "";
		if (currentLine.contains("=")) {
			int equalIndex = currentLine.indexOf("=");
			for (int i = equalIndex + 1; i < currentLine.length(); i++) {
				result += currentLine.charAt(i);
			}
		} else {
			result += currentLine.charAt(0);
		}
		return result;
	}

	public String jump() {
		if (!commandType().equals("C_COMMAND")) {
			return null;
		}
		String result = "";
		if (currentLine.contains(";")) {
			for (int i = 2; i < 5; i++) {
				result += currentLine.charAt(i);
			}
		}
		return result;
	}
}

```

##### 	

##### 	(2) Code

```
public class Code {
	public String dest(String input) {
		if (input == null || input.equals("0")) {
			return "000";
		}
		if (input.equals("M")) {
			return "001";
		}
		if (input.equals("D")) {
			return "010";
		}
		if (input.equals("MD")) {
			return "011";
		}
		if (input.equals("A")) {
			return "100";
		}
		if (input.equals("AM")) {
			return "101";
		}
		if (input.equals("AD")) {
			return "110";
		}
		if (input.equals("AMD")) {
			return "111";
		}
		return null;
	}

	public String comp(String input) {
		if (input.equals("0")) {
			return "0101010";
		}
		if (input.equals("1")) {
			return "0111111";
		}
		if (input.equals("-1")) {
			return "0111010";
		}
		if (input.equals("D")) {
			return "0001100";
		}
		if (input.equals("A")) {
			return "0110000";
		}
		if (input.equals("M")) {
			return "1110000";
		}
		if (input.equals("!D")) {
			return "0001101";
		}
		if (input.equals("!A")) {
			return "0110011";
		}
		if (input.equals("!M")) {
			return "1110001";
		}
		if (input.equals("-D")) {
			return "0001111";
		}
		if (input.equals("-A")) {
			return "0110011";
		}
		if (input.equals("-M")) {
			return "1110011";
		}
		if (input.equals("D+1")) {
			return "0011111";
		}
		if (input.equals("A+1")) {
			return "0110111";
		}
		if (input.equals("M+1")) {
			return "1110111";
		}
		if (input.equals("D-1")) {
			return "0001110";
		}
		if (input.equals("A-1")) {
			return "0110010";
		}
		if (input.equals("M-1")) {
			return "1110010";
		}
		if (input.equals("D+A")) {
			return "0000010";
		}
		if (input.equals("D+M")) {
			return "1000010";
		}
		if (input.equals("D-A")) {
			return "0010011";
		}
		if (input.equals("D-M")) {
			return "1010011";
		}
		if (input.equals("A-D")) {
			return "0000111";
		}
		if (input.equals("M-D")) {
			return "1000111";
		}
		if (input.equals("D&A")) {
			return "0000000";
		}
		if (input.equals("D&M")) {
			return "1000000";
		}
		if (input.equals("D|A")) {
			return "0010101";
		}
		if (input.equals("D|M")) {
			return "1010101";
		}
		return null;
	}
	
	public String jump(String input) {
		if (input.equals("")) {
			return "000";
		}
		if (input.equals("JGT")) {
			return "001";
		}
		if (input.equals("JEQ")) {
			return "010";
		}
		if (input.equals("JGE")) {
			return "011";
		}
		if (input.equals("JLT")) {
			return "100";
		}
		if (input.equals("JNE")) {
			return "101";
		}
		if (input.equals("JLE")) {
			return "110";
		}
		if (input.equals("JMP")) {
			return "111";
		}
		return null;
	}
}

```

​	

##### 	(3) SymbolTable

```
import java.util.HashMap;

public class SymbolTable {
	HashMap<String, Integer> map = new HashMap<String, Integer>();

	public void addEntry(String symbol, int address) {
		// not contain
		map.put(symbol, address);
	}

	public boolean contains(String symbol) {
		boolean isContained = map.getOrDefault(symbol, -1) == -1 ? false : true;
		return isContained;
	}

	public int GetAddress(String symbol) {
		if (map.get(symbol) == null) {
			return 0;
		}
		return map.get(symbol);
	}
}
```



본 Assembler로 번역한 교재의 과제 source는 깃헙에 따로 올려놓았다.



**4. Project 스터디 피드백**

- 해당 챕터는 '고수준 레벨로 어플리케이션 구현하기'의 첫 단계였다. 이전까지 완전히 새로운 내용을 습득하여 하드웨어를 구현하는 것보다는, 내가 그나마 익숙한 프로그래밍 언어로 어플리케이션을 구현하는 것이 훨씬 친숙하게 다가왔다.
- 그럼에도 불구하고, 아직 코딩에 미숙함이 많고 Study mate인 Mong의 코드와 비교해봐도 좀 지저분하고 효율적이지 못한게 사실이다.
- 그럼에도 무사히 과제를 끝냈다는 것이 기쁘고, 그동안 조금씩 해왔던 알고리즘 스터디 덕분인지, Java언어로 어떤 어플리케이션을 구현한다는 것에 대한 자신감이 생겨서 좋다.
