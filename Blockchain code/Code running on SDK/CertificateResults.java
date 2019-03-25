import java.lang.*;
import java.net.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.*;

public class CertificateResults {
	//Folder where the obu sends the certificate
	static File path = new File("/home/duser/Desktop/Certificates/");

    public static void main(String[] args) 
    {
    	
    	String path = "/home/duser/Desktop/Certificates/";
    	while(true)
        try{
        	
        	//Watchservice monitors the Certificates folder for a new file
        	WatchService watchService = FileSystems.getDefault().newWatchService();
        	Path certs = Paths.get(path);
        	certs.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
        	

    
        		
        	WatchKey key;
        	
        	while((key =watchService.take()) != null)
        	{
        	
        		for(WatchEvent<?> event : key.pollEvents())
        		{
        			//When a new file is created in the folder, it calls the transfer function
        			transfer();
        		}
        		key.reset();
        	}
        	}
        	
        	
         catch (Exception e){}
    	}      
    
   
    public static void transfer() throws UnknownHostException,IOException
    {
    	//Request to form a socket with the host
        Socket client = new Socket("169.254.0.3",5007);
        
        //Used to get results from the host
        DataInputStream input = new DataInputStream(client.getInputStream());
        
        boolean bool = false;
        
        //Selects the file to be sent and sends it to the host
        FileInputStream outputcert = new FileInputStream("/home/duser/Desktop/Certificates/certs.txt");
        byte size[] =new byte[20002];
        outputcert.read(size,0,size.length);
        OutputStream output = client.getOutputStream();
        output.write(size,0,size.length);
        File[] list = path.listFiles();
        
        //Sets the variable result equal to whatever is received from the host
        int result = input.readInt();
        outputcert.close();
        input.close();
        client.close();
      
        System.out.println(result);
        //Deletes everything in the folder to prevent uneccessary reauthentication of the same
        //certificate
        for(int i =0; i<list.length;i++){
        	
            bool = list[i].delete();
            }
        Result(result);
        
    }
    

    
   
    public static void Result(int r)
    {
    	if(r==0)
    	{
    		//Trusted
    		System.out.println("Trusted");
    	}
    	else
    	{
    		System.out.println("Turning off OBU");
    		//Untrusted or Revoked
    		System.out.println("Untrusted or revoked");
    		

    		//Turn off the OBU
    		new ProcessBuilder("/home/duser/Desktop/poweroff.sh");
    	}
    	
    }
    
}

