// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

//기본적으로 곱셈은 덧셈의 연속이므로, R2 = R0 * R1 은 R0을 R1만큼 더하는 것을 의미한다. 따라서 반복문으로 구현한다.
	@I
	M = 0 //I를 0으로 세팅
	@R2
	M = 0 //R2를 0으로 세팅
	@R1
	D = M //R1의 값을 D에 넣고
	@COUNT
	M = D //COUNT를 세팅(=R1의 값)
(LOOP)
	@I
	D = M
	@COUNT
	D = D - M
	@END
	D;JEQ //여기까지 반복문 : COUNT(=R1) - I 가 0이 될 때까지 반복 (I가 +1 되므로 R1만큼 반복한다)
	@R0
	D = M	
	@R2
	M = D + M //R2를 계산한다 (R2 + R0)
	@I
	M = M + 1
	@LOOP
	0;JMP
(END)
	@END
	0;JMP //Terminate