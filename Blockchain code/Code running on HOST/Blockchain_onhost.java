import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.security.MessageDigest;
import java.nio.file.*;
import java.sql.*;


public class Blockchain {
	static int result = 1;
	//Folder that stores all the certificates.
		static File folder = new File("C:/Users/A-Team/Documents/Certificates/");
		
		public static int Verification(String certificate, String Date) {
			
			//Establish a connection to SQL database that contains the Blockchain
			Connection();
			String url = "jdbc:mysql://localhost:3306/blockchain";
			String username = "root";
			String password = "";
			try
			{
				Connection block = DriverManager.getConnection(url,username,password);
				//Searches the revoked table for the certificate
				
				PreparedStatement revokeVerify = (PreparedStatement) block.prepareStatement("SELECT Certificate FROM revoked ");
				ResultSet revoked = revokeVerify.executeQuery();
				
				
					while(revoked.next())
					{
						if(revoked.getString(1)!=null && revoked.getString(1).matches(certificate))
							{
								//Untrusted if it is found in the revoked chain
								System.out.println("Blockchain returned: This certificate is revoked. ");
								result=1;
							}
				
						else
							{
								//If it is not found in the revoked chain, it sends the certificate to be searched in the 
								//issued chain
								result=issuedVer(certificate,Date);
							}
						
					}
					if(!revoked.first())
					{
						//If the revoked chain is empty, it sends the certificate to be searched in the 
						//issued chain
						issuedVer(certificate,Date);
						System.out.println(result);
					}
				revokeVerify.close();
			} catch(Exception e) {System.out.println(e);}
			return result;
		}
		public static int issuedVer(String certificate, String Date)
		{
			//Establish a connection to SQL database that contains the Blockchain
			Connection();
			String url = "jdbc:mysql://localhost:3306/blockchain";
			String username = "root";
			String password = "";
			try
			{
				Connection block = DriverManager.getConnection(url,username,password);
				//Searches the issued table for the certificate
				PreparedStatement issueVerify = (PreparedStatement) block.prepareStatement("SELECT Certificate FROM issued ");
				ResultSet issued = issueVerify.executeQuery();
				
				while(issued.next())
				{
					if(issued.getString(1).matches(certificate))
						{
							//The certificate is trusted if a match is found
							result=0;
							issued.afterLast();
						}
			
					else
						{
							//The certificate is unissued if a match is not found
							result=1;
						}
					
				}
				System.out.println(result);
				if(result==1)
					{
						System.out.println("Untrusted");
					}
				else
					{
						System.out.println("Blockchain returned: This certificate is an issued one. ");
					}
				
				issueVerify.close();
				block.close();
				
			} catch(Exception e) {System.out.println(e);}
			
			return result;
		}
		public static void main(String[] args) throws IOException, InterruptedException {
			//Listens on a Socket, waiting for the SDK to request a connection
			ServerSocket server = new ServerSocket(5007);	
			while(true) {
					
					
						Socket serv = server.accept();
						
						//Used to output data to SDK
						DataOutputStream result = new DataOutputStream(serv.getOutputStream());
						
						//Used to receive data from the SDK
						InputStream inputcert = serv.getInputStream(); 
						
						//Specifies a the certificate file that will be verified
						FileOutputStream certfile = new FileOutputStream("C:/Users/A-Team/Documents/Certificates/cert.txt");
						byte []size=new byte[20002];
						
						//Reads the file it receives and writes it to the newly created file
						inputcert.read(size,0,size.length);
						certfile.write(size,0,size.length);
						
						//Calls this function to start the authentication process
						int value = GenerateBlock();
						System.out.println(value);
						
						//A result of 1 is untrusted and a result of 0 is trusted, sends the result to the SDK to take 
						//necessary actions
				    	result.writeInt(value);
						    	
			        serv.close();
						   
				}
				
		}
		//Establish Connection with SQL each time a file needs to be transfered to the database
		public static void Connection()
		{
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public static int GenerateBlock() throws InterruptedException {
			
			File[] listOfFiles = folder.listFiles();
			boolean bool = false;
			for (int i = 0; i < listOfFiles.length; i++) 
			{
				try {	
					//Used to read the contents of the files in the folder
					BufferedReader data = new BufferedReader(new FileReader(listOfFiles[i]));
					//This is used to only transfer my Test files because it was sending random hidden files to the database
					
						
					String line;
					line = data.readLine();
						
						
						Calendar cal = Calendar.getInstance();
						//Sends clock time of transfer 
						SimpleDateFormat sdf = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
						
						String time = sdf.format(cal.getTime());
						//Hashes the certificate
						String cert = hash(line);
						
						result=Verification(cert, time);
						
						//Deletes the file after it is verified or not to prevent authenticating the same vehicle multiple times unnecessarily
						data.close();
						listOfFiles[i].delete();
						
						}
					
			
					 catch (IOException e) {
						
				}	
			
			}
			return result;
		}
		
		
		//Hash function for SHA 256
		public static String hash(String data) {
			try{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
			String encoded = Base64.getEncoder().encodeToString(hash);
			return encoded;
			} catch(Exception ex){
		        throw new RuntimeException(ex);
		    }
		
		}
}
