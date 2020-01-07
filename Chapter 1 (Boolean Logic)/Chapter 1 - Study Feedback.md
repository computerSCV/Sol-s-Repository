## Study Feedback



##### 1. Mux를 해결하며 수학과 출신 개발자 Mong에게 배운점

- 문제 해결을 수학적으로 한다는 점.

- Mux 구현을 할 때, And와 Or의 성질을 이용해서 풀었다는 점에서 많은 깨달음을 얻음.

- 나는 And와 Or를 구현하는데만 급급했지 그 둘의 '본질'이 무엇인지 별로 고려하지 않았기 때문에 많은 것을 배울 수 있었음.

  ```
  Mong's Approach to implement Mux
  
  1. And : [x And y = output] 일 때, And의 성질 상 x의 값이 1로 고정되면 output = y 가 된다.
  2. Or : [x Or y = output] 일 때, Or의 성질 상 x의 값이 0으로 고정되면 output = y 가 된다.
  
   => 이 원리를 이용하면 sel이 0 또는 1로 고정될 때 a or b를 출력하도록 조작할 수 있다.
  ```

  

- Mong's approach를 활용한 **better code for Mux** : 

  ```
  CHIP Mux {
      IN a, b, sel;
      OUT out;
  
      PARTS:
      // Put your code here:
      Not(in=sel, out=notSel);
      And(a=a, b=notSel, out=andOut1);
      And(a=b, b=sel, out=andOut2);
      Or(a=andOut1, b=andOut2, out=out);
  }
  ```

- 위 Code를 이용한 **Mux4Way16 Implementation** : 

  ```
  CHIP Mux4Way16 {
      IN a[16], b[16], c[16], d[16], sel[2];
      OUT out[16];
  
      PARTS:
      //Put your code here:
      Not(in=sel[0], out=sel0out);
      And(a=a[0], b=sel[0], out=aOut);
      And(a=b[0], b=sel[0], out=bOut);
      And(a=c[0], b=sel0out, out=cOut);
      And(a=d[0], b=sel0out, out=dOut);
      Or(a=aOut, b=cOut, out=firstOut);
      Or(a=bOut, b=dOut, out=secondOut);
      Not(in=sel[1], out=sel1out);
      And(a=firstOut, b=sel[1], out=w1);
      And(a=secondOut, b=sel1out, out=w2);
      Or(a=w1, b=w2, out=out[0]);
      //[0]만 처리함
      }
  ```

- 보다시피 위 코드는 지저분하다...그래서 Mong이 구글을 뒤적거렸는데 아래와 같은 코드를 찾았다.

  ```
      CHIP Mux4Way16 {
      IN a[16], b[16], c[16], d[16], sel[2];
      OUT out[16];
  
      PARTS:
      //Put your code here:
      Mux16(a=a, b=b, sel=sel[0], out=muxab);
      Mux16(a=c, b=d, sel=sel[0], out=muxcd);
      Mux16(a=muxab, b=muxcd, sel=sel[1], out=out);
  }
  ```

  그렇다..16비트 Mux 3개만 이용하면 되는 아주 간단하고 깔끔한 코드였던 것이다..........

  

- **Feedback : I must firstly consider to use the gates that are already implemented beforehand.**

  

##### 2. 다음주 까지의 과제

- Chapter 2 해오기
- Chapter 1에서 구현하지 못했던 4비트 멀티플렉스, 디멀티플렉서 구현, Mux 한번 더 구현해보기.