import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class DESEncryptor {
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		// Read input file
		String fileContents = readFile("plaintext.txt");
		fileContents = inputTrim(fileContents);
		
		System.out.println("Please enter an 8-character key >> ");
		Scanner input = new Scanner(System.in);
		String key = input.next();
		encrypt(fileContents);
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
	
	// File write method
	
	// Need encryption method
	public static String encrypt(String input) throws UnsupportedEncodingException {
		String outputString, paddedString = "";
		String[] inputBlocks = null;
		byte[] strBytes = null;
		
		strBytes = input.getBytes("ISO-8859-1");
		System.out.println(Integer.toBinaryString(strBytes[0]));
		paddedString = inputPad(strBytes);
		System.out.println(paddedString.length());
		inputBlocks = inputDivide(paddedString);
	

		return null;		
	}
	
	public static String[] inputDivide(String input) {
		
		String[] inputBlocks = new String[(int) Math.ceil(input.length() / (double) 64)];
		System.out.println(input);
		int j = 0;
		for(int i = 0; i < inputBlocks.length; i++) {
			inputBlocks[i] = input.substring(j, j + 64);
			j += 64;
		}
		
		inputBlocks[inputBlocks.length - 1] = input.substring(j);
		return inputBlocks;
	}
	
	public static String inputPad(byte[] input) {
		// Pads 00s onto byte array
		String outputString = toBinString(input);
		for(int i = 0; i < 8 - (input.length % 8); ++i) {
			outputString = outputString+ "00000000";
		}
		return outputString;
	}
	
	public static String toBinString(byte[] input) {
		String output = "";
		for(int i = 0; i < input.length; ++i) {
			output = output + Integer.toBinaryString(input[i]);
		}
		return output;
	}
	
	public static String initialPermutaion(String input) {
		
		return null;
	}
	
	// Need decryption method
}
