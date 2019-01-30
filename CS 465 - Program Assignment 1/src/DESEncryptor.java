import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DESEncryptor {
	public static void main(String[] args) throws FileNotFoundException {
		
		// Read input file
		String fileContents = readFile("plaintext.txt");
		fileContents = inputTrim(fileContents);
		
		System.out.println("Please enter an 8-character key >> ");
		Scanner input = new Scanner(System.in);
		String key = input.next();
		
		// Export to output file
		
	}
	
	public static String inputTrim(String input) {
		/*
		 * Takes in file contents and removes all characters
		 * which are not a - z, A - Z, or 0 - 9
		 * Returns new string with undesired characters omitted
		 */
		String newString = input.replaceAll("[^a-zA-Z0-9]+", "");
		return newString;
	}
	
	public static String readFile(String fileName) throws FileNotFoundException {
		/*
		 * Receives file name and returns the contents of the file 
		 * as a string
		 */
		String fileContents = "";
		File file = new File(fileName);
		Scanner sc = new Scanner(file);
		sc.useDelimiter("\\Z");
		fileContents = sc.next();
		sc.close();
		return fileContents;
	}
	
	// Convert to binary method
	public static bytes[] toBinary(String strInput) {
		
	}
	
	// File write method
	
	// Need encryption method
	
	// Need decryption method
}
