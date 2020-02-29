	@SCREEN
	D = A //여기서 D는 16384가 된다
	@TARGET
	M = D  //target 이 가리키는 RAM 의 값은 D = 16384가 된다.
	@MIN
	M = D - 1 //MIN이 가리키는 RAM의 값은 16383이다.
	@KBD
	D = A
	@MAX  //MAX가 가리키는 RAM의 값은 24575이다.
	M = D - 1
(KEYBOARD)
	@KBD
	D = M
	@BLACK
	D;JGT
	@WHITE
	D;JEQ
(BLACK)
	@TARGET
	D = M

	@MAX
	D = M - D
	@END
	D;JEQ

	@TARGET
	A = M
	M =  -1
	@TARGET
	M = M + 1
	@KEYBOARD
	0;JMP
(WHITE)
	@TARGET
	D = M

	@MIN
	D = M - D
	@KEYBOARD
	D;JEQ

	@TARGET
	A = M
	M =  0
	@TARGET
	M = M - 1
	@WHITE
	0;JMP
(END)
	@END
	0;JMP

