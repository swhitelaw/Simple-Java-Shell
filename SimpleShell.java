package bin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SimpleShell {
	
	public static void historyFunc(List<String> command,List<List<String>> history ){
		for(int i=0; i<history.size(); i++){
			String toPrint = "";
			for(int j=0; j<history.get(i).size(); j++){
				toPrint = toPrint+" "+history.get(i).get(j);
			}
			System.out.println(i+1+" "+toPrint);
		}
	}

	public static void main(String[] args) throws java.io.IOException {
		String commandLine;
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		File homeDir = new File(System.getProperty("user.dir"));
		File curDir = homeDir;
		List<List<String>> history = new ArrayList<List<String>>();
		// we break out with <control><C>
		while (true) {
			// read what the user entered
			System.out.print("jsh>");
			commandLine = console.readLine();
			// if the user entered a return, just loop again
			if (commandLine.equals(""))
				continue;
			/**
			 * The steps are: (1) parse the input to obtain the command and any
			 * parameters
			 */
			List<String> command = new ArrayList<String>();
			StringTokenizer tokenizer = new StringTokenizer(commandLine);
			command.add(tokenizer.nextToken());
			while (tokenizer.hasMoreTokens()) {
				command.add(tokenizer.nextToken());
			}
			//adding to history 
			history.add(command);
			if(command.get(0).equals("history")){
				historyFunc(command, history);
				continue;
			}
			//making process builder
			ProcessBuilder p;
			try{
				if(command.get(0).contains("!")){
					if(command.get(0).equals("!!")){
						if(history.size()==1){
							System.out.println("No history exists to execute");
							continue;
						}
						if(history.get(history.size()-2).get(0).equals("history")){
							historyFunc(command, history);
							continue;
						}
						if(history.get(history.size()-2).get(0).equals("!!")){
							int past = 0;
							while(history.get(history.size()-(2+past)).get(0).equals("!!")){
								if(history.size()-(2+past)==0){
									break;
								}
								past = past+1;
							}
							if(past+1 == history.size()){
								System.out.println("No history exists to execute");
							}
							if(history.get(history.size()-(2+past)).get(0).equals("history")){
								historyFunc(command, history);
								continue;
							}
							else{
								p = new ProcessBuilder(history.get(history.size()-(2+past)));
							}
						}
						else{
							p = new ProcessBuilder(history.get(history.size()-2));
						}
					}
					else{
						int histIndex = Integer.parseInt(command.get(0).substring(1));
						if(histIndex >=1+history.size()){
							System.out.println("History index out of bounds");
							continue;
						}
						if(history.get(histIndex-1).get(0).equals("history")){
							historyFunc(command, history);
							continue;
						}
						if(history.get(histIndex-1).get(0).contains("!")){
							while(history.get(histIndex-1).get(0).contains("!")){
								histIndex = Integer.parseInt(history.get(histIndex-1).get(0).substring(1));
							}
							if(history.get(histIndex-1).get(0).equals("history")){
								historyFunc(command, history);
								continue;
							}
							else{
								p = new ProcessBuilder(history.get(histIndex-1));
							}
						}
						else{
							p = new ProcessBuilder(history.get(histIndex-1));
						}
					}
				}
				else{	
				p = new ProcessBuilder(command);
				}
			}
			catch(NumberFormatException e){
				System.out.println("Invalid history index");
				continue;
			}
			
			//Code for cd command
			if(command.contains("cd")){
				if(command.size()==1){
					curDir = homeDir;
				}
				else{
					File dirToMove = new File(curDir+"/"+command.get(1));
					if(dirToMove.exists()){
						curDir = dirToMove;
					}
					else{
						System.out.println("No such file or directory");
					}
				}
				
			}
			p.directory(curDir);
			// (3) start the process
			try {
				
				Process process = p.start();
				

				InputStream outputStream = (process.getInputStream());

				BufferedReader outputReader = new BufferedReader
						(new InputStreamReader(outputStream));
				while(true){
					String line = outputReader.readLine();
					if(line != null){
						System.out.println(line);
					}
					else{
						break;
					}
				}
				}
			catch (IOException e) {
				System.out.println("command not found");
			}
			// (4) obtain the output stream

			// (5) output the contents returned by the command */

		}
	}
}
