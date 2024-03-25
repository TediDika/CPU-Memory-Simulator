import java.io.*;

public class Memory {
    static int[] mem=new int[2000]; //Memory Array

    static BufferedReader programReader = null;
    static BufferedReader pipeReader = null;
    public static void main(String[] args1) throws IOException{
        //Initializing memory array
        programReader = new BufferedReader(new FileReader(args1[0])); //read program

            String input = null;
            int pointer = 0;
            while((input= programReader.readLine()) != null)
            {
                if(input.trim().length() > 0 && input.trim().charAt(0) != '/'){
                    String[] split = input.split("\\s+");
                    try
                    {
                        mem[pointer++]=Integer.parseInt(split[0]); //storing file information in memory
                    }
                    catch(NumberFormatException e)
                    {
                        pointer=Integer.parseInt(input.substring(1)); //catch if line starts with .
                    }
                }

            }

        //Communicating with CPU during fetch cycle
        String output = null;
        pipeReader = new BufferedReader(new InputStreamReader(System.in)); //used to read from parent process (CPU)
        while(true)
        {
            output = pipeReader.readLine();
            if(output.equals("exit")) //execute instruction no. 50 to destroy itself
                System.exit(0);
            if(output!=null)
            {
                try
                {
                    System.out.println(mem[Integer.parseInt(output)]);	//fetch instruction to pipe
                }
                catch(NumberFormatException e)    // this is to catch the interrupt information
                {
                    mem[Integer.parseInt(output.split(" ")[0])]=Integer.parseInt(output.split(" ")[1]); //store instruction
                }
            }
        }

    }
}