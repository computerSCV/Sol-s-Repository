## Chapter 5 - Contents Summary & Study Feedback  (20.04.03)



<Contents Summary>

```
'형태는 언제나 기능을 따른다'
 - 루이스 설리번(건축가)
```

---



Chapter 4를 끝내고 Chapter 5의 내용을 정리해서 올리는 데까지 거의 한달이 걸렸다.

게으름을 피워서도, 코로나 바이러스 때문도 아니라, Chapter 5 의 내용을 완전히 이해하고, 프로젝트를 구현하는 것이 정말 어려웠기 때문이다.

Chapter 5는 여태까지 해왔던 칩, 메모리, PC, I/O 등등 각종 '하드웨어'관련 내용의 **정점**이다. 

그만큼 내용이해가 어려웠고, 구현하는데도 시간이 많이 걸렸다.

그러나 언제나처럼, 끝없는 삽질과 고군분투속에 결국 해내고야 말았다.

Hack 이라 불리는, 16비트 컴퓨터를 만들고야 말았던 것이다 (비록 시뮬레이터로 만들긴 했지만).



#### 1. 디지털 컴퓨터의 확장성 - 내장식 프로그램의 개념

- 컴퓨터가 세상을 바꾼 이유는, 그리고 현재도 세상이 이처럼 빠르게 변화하는 이유는, 디지털 컴퓨터가 갖고있는 '확장성'과 '다재다능성' 때문이다. 컴퓨터는 '**유한한 하드웨어**'만으로, '**무한한 Application**'이 가능한 기기다.

- 컴퓨터 하드웨어 플랫폼을 특정 방식으로 작동하도록 하는 '명령어의 집합체'가 바로 이 어플리케이션이며, 이 집합체가 컴퓨터 메모리상에 데이터처럼 저장되어 연산되면 이것을 우리는 **소프트웨어**라고 한다. 

- 즉, 동일한 하드웨어 플랫폼이라도, 어떤 어플리케이션을 불러오느냐에 따라서 하드웨어가 수행할 일이 달라지며, 이것이 컴퓨터의 무한한 확장성의 근간이다.

  

#### 2. 폰 노이만 아키텍쳐

- 위에 언급했던 내장식 프로그램 중 '폰 노이만 아키텍쳐'는 현대 모든 컴퓨터 플랫폼의 개념적 설계도이다.

- 따라서 폰 노이만 아키텍쳐를 잘 이해한다면, 현대의 컴퓨터 구조 뼈대를 잘 이해할 수 있다.

- 폰 노이만 아키텍쳐는 크게 세 부분으로 이루어져있다.

  1) 메모리 : 데이터와 명령어가 담김

  2) CPU : 중앙 처리장치

  3) 입력 / 출력 장치

  즉 폰 노이만 구조는, '**입력장치에서 데이터를 받은 CPU가 메모리와 통신하여 출력장치에 그 결과를 보내는**'구조이다.

- 그 구조를, 내가 쓰는 '고수준 언어(Java 등)'와 연관지어 분석해보면...

  - 변수를 선언한다 : 데이터 메모리에 단어 형태로 저장된다(Hack의 A-명령어).

  - 변수 값을 가져오거나 수정한다 : 데이터 메모리에 저장된 단어의 값을 가져오거나, 새로운 값을 덧씌운다.

  - 특정 명령을 내린다 : 명령어 메모리에 연산 명령어(Hack의 C-명령어)를 저장한다.

  - 컴퓨터가 연산을 수행한다 : 

    명령어 메모리에서 PC가 가리키는 명령어를 Fetch한 후 CPU로 보낸다 -> CPU가 처리한 결과를 메모리에 쓰고, PC를 조작하고, 레지스터에 데이터나 주소값이 담긴다.

  - 컴퓨터가 다음번 명령어를 입력받는다

  - 이 과정을 반복한다.

- 입출력 장치 또한 4장에서 살펴본 바와 같이 '**메모리 매핑**'의 전략으로 작동한다. 즉, 각 I/O 장치마다 메모리 내에 전용영역이 할당되어 메모리 맵의 역할을 하게 된다.

- 따라서 우리는 메모리에 해당 I/O 장치의 '**시작점**'만을 기록해두면 되는데, 이 환경설정은 보통 **운영체제**가 담당한다.



#### **3. (대망의) Hack 컴퓨터 플랫폼 구현**

##### 	**(1) cpu**

```
CHIP CPU {

    IN  inM[16],         // M value input  (M = contents of RAM[A])
        instruction[16], // Instruction for execution
        reset;           // Signals whether to re-start the current
                         // program (reset==1) or continue executing
                         // the current program (reset==0).

    OUT outM[16],        // M value output
        writeM,          // Write to M? 
        addressM[15],    // Address in data memory (of M)
        pc[15];          // address of next instruction
        
    PARTS:
    // Put your code here:
    
  	 /* Decode */
    And(a=instruction[15], b=true, out=decodeResult); 
    Not(in=decodeResult, out=decodeResultNot); 

    /* First Mux */
    Mux16(a=ALUoutput, b=instruction, sel=decodeResultNot, out=firstMuxResult);

    /* A Register */
    Or(a=decodeResultNot, b=instruction[5], out=AregisterControlBit);
    ARegister(in=firstMuxResult, load=AregisterControlBit, out=AregisterResult);

    /* Second Mux */
    And(a=instruction[12], b=decodeResult, out=secondMuxC); 
    Mux16(a=AregisterResult, b=inM, sel=secondMuxC, out=secondMuxResult);

    /* ALU */
    ALU(x=DregisterResult, y=secondMuxResult, zx=instruction[11], nx=instruction[10], zy=instruction[9], ny=instruction[8], f=instruction[7], no=instruction[6], 
    out=ALUoutput, zr=zr, ng=ng);
    And(a=instruction[3], b=instruction[15], out=MAndOut);
    Mux16(a=false, b=ALUoutput, sel=MAndOut, out=outM);

    /* D register */
    And(a=instruction[15], b=instruction[4], out=DregisterLoadBit);
    DRegister(in=ALUoutput, load=DregisterLoadBit, out=DregisterResult);

    /* PC */
    And(a=instruction[2], b=ng, out=j1Out); //ng 
    Not(in=ng, out=notNG); //not ng
    Xor(a=notNG, b=zr, out=positive);
    And(a=instruction[1], b=zr, out=j2Out); //zr
    And(a=instruction[0], b=positive, out=j3Out); //positive
    Or(a=j1Out, b=j2Out, out=or1);
    Or(a=or1, b=j3Out, out=or2);
    And(a=instruction[15], b=or2, out=pcLoad);
    PC(in=AregisterResult, load=pcLoad, inc=true, reset=reset, out=pcOut, out[0..14] = pc);

    /* addressM 으로 보낼 A레지스터의 주소값 */
    And16(a=AregisterResult, b=true, out = andandOUt, out[0..14]=addressM);

    /* Write M */
    And(a=instruction[3], b=instruction[15], out=writeM, out = writeMClone);
}
```

##### 	

##### 	(2) Memory

```
CHIP Memory {
    IN in[16], load, address[15];
    OUT out[16];

    PARTS:
    // Put your code here:
    DMux4Way(in=load, sel=address[13..14], a=loadRAM1, b=loadRAM2, c=loadScreen, d=noNeed);
    /* RAM */
    Or(a=loadRAM1, b=loadRAM2, out=loadRAM);
    RAM16K(in=in, load=loadRAM, address=address[0..13], out=RAMOut);
    /* Screen Memory Map */
    Screen(in=in, load=loadScreen, address=address[0..12], out=ScreenOut);
    Keyboard(out=keyboardOut);
    /* out selection */
    Mux4Way16(a=RAMOut, b=RAMOut, c=ScreenOut, d=keyboardOut, sel=address[13..14], out=out);
}
```

​	

##### 	(3) Computer

```
CHIP Computer {

    IN reset;

    PARTS:
    // Put your code here:
    ROM32K(address=pc, out=instruction);
    CPU(inM=inM, instruction=instruction, reset=reset, outM=outM, writeM=writeM, addressM=addressM, pc=pc);
    Memory(in=outM, load=writeM, address=addressM, out=inM); 
}

```





**4. Project 스터디 피드백**

- 본 5장은, 1~4장에서 달려왔던 컴퓨터 하드웨어 구조의 끝판왕이었다.
- 1~4장에서 배운 것들을 총망라하여 결국 하나의 컴퓨터 칩이 완성되었을 때 그 쾌감은 이루말할 수 없었다.
- 특히 CPU가 구현이 정말 어렵고 오래걸렸는데, 그 이유는 CPU의 동작 방식 자체가 순차적이라기 보다는 하나의 시점에 여러가지 일들이 동시에 연결되어 발생하기 때문에, 구현하는 사람도 컴퓨터처럼 사고를 해야하기 때문이다.
- 1~4장을 공부하면서 조금씩 놓친 부분이 있었다. 가령 ALU의 input 과 output 값이 왜 그런 형식으로 설정되어있는지 몰랐었는데, CPU를 구현하면서 놓치거나 이해를 하지 못하고 넘어갔던 부분들을 자연스럽게 이해할 수 있게 되었다. 왜냐하면 그런 요소들 하나하나를 이해하지 못하면 CPU를 구현할 수 없었을 것이기 때문이다.
- 약 한 달이라는 시간 동안 컴퓨터 한대를 (칩으로나마) 구현하고 나니, 이제 이 기본적인 폰 노이만 플랫폼 정도는 내가 다른 사람들에게 그림을 그려가며 설명할 수 있을 정도로 그 개념이 머릿속에 자리잡힌 것 같아 기쁘다.
- 그리고 역시 마찬가지로, 삽질은 할 때는 고통스럽지만, 끝내 결과물을 도출했을 때의 그 쾌감은 이루말할 수 없다는 점을 깨닫는다.
