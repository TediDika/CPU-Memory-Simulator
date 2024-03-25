//Tedi Dika
//CS 4348.501

import java.io.*;
import java.util.Random;

public class CPU
{
    static int PC = 0; //Program Counter
    static int SP = 1000; //Stack Pointer
    static String IR = "0"; //Instruction Register
    static int AC; //Accumulator
    static int X;
    static int Y;
    static int timer = 0; //A timer will interrupt the processor after every X instructions

    static BufferedReader childData = null;
    static Process memoryProcess = null; 
    static PrintWriter pipe = null; 
    static boolean interruptCheck = true;

   public static void main(String[] args) throws IOException {
           //Memory Child Process is created, the text file specified in the command line is sent
           memoryProcess = Runtime.getRuntime().exec("java Memory "+args[0]);
           //Buffered Reader will act as the System Bus to communicate to and from CPU and Memory
           childData = new BufferedReader(new InputStreamReader(memoryProcess.getInputStream()));
           pipe = new PrintWriter(new OutputStreamWriter(memoryProcess.getOutputStream()));

           while(true)  //Program execution is active until End instruction (opcode 50) is found
           {
               fetching();   //this will ask memory to fetch the next instruction based on PC

               IR = childData.readLine();
               if(IR != null && !IR.equals(""))
               {
                   int opcode = Integer.parseInt(IR);
                   switch (opcode) {
                       case 1:
                           PC++;
                           fetching();
                           AC = Integer.parseInt(childData.readLine());
                           PC++;
                           break;
                       case 2:
                           PC++;
                           fetching();
                           pipe.println(childData.readLine());
                           pipe.flush();
                           AC = Integer.parseInt(childData.readLine());
                           PC++;
                           break;
                       case 3:
                           PC++;
                           fetching();
                           pipe.println(childData.readLine());
                           pipe.flush();
                           pipe.println(childData.readLine());
                           pipe.flush();
                           AC = Integer.parseInt(childData.readLine());
                           PC++;
                           break;
                       case 4:
                           PC++;
                           fetching();
                           pipe.println(Integer.parseInt(childData.readLine()) + X);
                           pipe.flush();
                           AC = Integer.parseInt(childData.readLine());
                           PC++;
                           break;
                       case 5:
                           PC++;
                           fetching();
                           pipe.println(Integer.parseInt(childData.readLine()) + Y);
                           pipe.flush();
                           AC = Integer.parseInt(childData.readLine());
                           PC++;
                           break;
                       case 6:
                           pipe.println((SP + X));
                           pipe.flush();
                           AC = Integer.parseInt(childData.readLine());
                           PC++;
                           break;
                       case 7:
                           PC++;
                           fetching();
                           pipe.println(childData.readLine() + " " + AC);
                           pipe.flush();
                           PC++;
                           break;
                       case 8:
                           Random rg = new Random();
                           AC = rg.nextInt(99) + 1; //random number 1-100
                           PC++;
                           break;
                       case 9:
                           PC++;
                           fetching();
                           if (Integer.parseInt(childData.readLine()) == 1)
                               System.out.print(AC);
                           else
                               System.out.print((char) AC);
                           PC++;
                           break;
                       case 10:
                           AC = AC + X;
                           PC++;
                           break;
                       case 11:
                           AC = AC + Y;
                           PC++;
                           break;
                       case 12:
                           AC = AC - X;
                           PC++;
                           break;
                       case 13:
                           AC = AC - Y;
                           PC++;
                           break;
                       case 14:
                           X = AC;
                           PC++;
                           break;
                       case 15:
                           AC = X;
                           PC++;
                           break;
                       case 16:
                           Y = AC;
                           PC++;
                           break;
                       case 17:
                           AC = Y;
                           PC++;
                           break;
                       case 18:
                           SP = AC;
                           PC++;
                           break;
                       case 19:
                           AC = SP;
                           PC++;
                           break;
                       case 20:
                           PC++;
                           fetching();
                           PC = Integer.parseInt(childData.readLine());
                           break;
                       case 21:
                           PC++;
                           if (AC == 0) {
                               fetching();
                               PC = Integer.parseInt(childData.readLine());
                           } else {
                               PC++;
                           }
                           break;
                       case 22:
                           PC++;
                           if (AC != 0) {
                               fetching();
                               PC = Integer.parseInt(childData.readLine());
                           } else {
                               PC++;
                           }
                           break;
                       case 23:
                           SP--;
                           PC++;
                           pipe.println(SP + " " + (++PC));
                           pipe.flush();
                           pipe.println(--PC);
                           pipe.flush();
                           PC = Integer.parseInt(childData.readLine());
                           break;
                       case 24:
                           pipe.println(SP);
                           pipe.flush();
                           PC = Integer.parseInt(childData.readLine());
                           SP++;
                           break;
                       case 25:
                           X++;
                           PC++;
                           break;
                       case 26:
                           X--;
                           PC++;
                           break;
                       case 27:
                           SP--;
                           PC++;
                           pipe.println(SP + " " + AC);
                           pipe.flush();
                           break;
                       case 28:
                           PC++;
                           pipe.println(SP);
                           pipe.flush();
                           AC = Integer.parseInt(childData.readLine());
                           SP++;
                           break;
                       case 29:
                           if (interruptCheck)  //check for nested interrupts
                           {
                               //saving the system state
                               pipe.println("1999 " + SP);
                               pipe.println("1998 " + (++PC));
                               pipe.println("1997 " + AC);
                               pipe.println("1996 " + X);
                               pipe.println("1995 " + Y);
                               PC = 1500;
                               SP = 1995;
                               interruptCheck = false;
                           } else {
                               PC++;
                           }
                           break;
                       case 30:
                           interruptCheck = true;
                           //revert system state
                           pipe.println(SP);
                           pipe.flush();
                           Y = Integer.parseInt(childData.readLine());
                           SP++;
                           pipe.println(SP);
                           pipe.flush();
                           X = Integer.parseInt(childData.readLine());
                           SP++;
                           pipe.println(SP);
                           pipe.flush();
                           AC = Integer.parseInt(childData.readLine());
                           SP++;
                           pipe.println(SP);
                           pipe.flush();
                           PC = Integer.parseInt(childData.readLine());
                           SP++;
                           pipe.println(SP);
                           pipe.flush();
                           SP = Integer.parseInt(childData.readLine());
                           break;
                       case 50:
                           pipe.println("exit"); //telling child memory process to end
                           System.exit(0);
                           break;
                   }
               }

               //Every execution loop the timer is incremented, and we check to see if an interrupt needs to occur
               timer++;
               if(timer == Integer.parseInt(args[1]))
                   switchMode();
           }

   }

    static private void switchMode(){
        if(interruptCheck) //checking mode type
        {
            pipe.println("1999 " + SP);
            pipe.println("1998 " + PC);
            pipe.println("1997 " + AC);
            pipe.println("1996 " + X);
            pipe.println("1995 " + Y);
            interruptCheck = false;
            SP = 1995;
            PC = 1000;

        }
        timer = 0;
    }

    static private void fetching()
    {
        pipe.println(PC);
        pipe.flush();
    }

}

