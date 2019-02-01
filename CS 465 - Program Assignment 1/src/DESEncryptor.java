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
		key = preProcessKey(key);
		String[] processedData = preProcessData(fileContents);
		encrypt(processedData, key);
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
	
	public static String preProcessKey(String input) {
		byte[] strBytes = input.getBytes();
		String output = toBinString(strBytes);
		return output;
	}
	
	public static String[] preProcessData(String input) {
		String outputString, paddedString = "";
		String[] inputBlocks = null;
		byte[] strBytes = null;	
		strBytes = input.getBytes();
		paddedString = inputPad(strBytes);
		inputBlocks = inputDivide(paddedString);
		inputBlocks = initialPermutaion(inputBlocks);
		return inputBlocks;
	}
	
	// Need encryption method
	public static String encrypt(String[] input, String key) throws UnsupportedEncodingException {
		// 	Expand
		// XOR w key
		// S sub
		// P sub
		// XOR w Li
		String[] keys = keyGen(key);
		
		return null;	
	}
	
	public static String[] inputDivide(String input) {
		// Divides input string into 64-bit  blocks
		
		String[] inputBlocks = new String[(int) Math.ceil(input.length() / (double) 64)];
		int j = 0;
		for(int i = 0; i < inputBlocks.length - 1; i++) {
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
			outputString = outputString + "00000000";
		}
		return outputString;
	}
	
	public static String toBinString(byte[] input) {
		String output = "";
		for(int i = 0; i < input.length; ++i) {
			output = output + "0" + Integer.toBinaryString(input[i]);
		}
		return output;
	}
	
	public static String[] initialPermutaion(String[] input) {
		// Permutes each block
		String[] outStrings = input;
		for(int i = 0; i < input.length; ++i) {
			outStrings[i] = initPermute(input[i]);
		}
		return outStrings;
	}
	
	public static String[] keyGen(String input) {
		String[] outputStrings = null;
		String compressedKey = parityDrop(input);
		System.out.println(compressedKey);
		return null;
	}
	
	public static String parityDrop(String input) {
		/*
		 * Parity Drop
		 */
		int[] parityTable = 
					 {57, 49, 41, 33, 25, 17, 9, 1
					, 58, 50, 42, 34, 26, 18, 10, 02
					, 59, 51, 43, 35, 27, 19, 11, 03
					, 60, 52, 44, 36, 63, 55, 47, 39
					, 31, 23, 25, 07, 62, 54, 46, 38
					, 30, 22, 14, 06, 61, 53, 45, 37
					, 29, 21, 13, 05, 28, 20, 12, 04};
		
		String compressedKey = "";		

		for(int i = 0; i < parityTable.length; ++i) {
			compressedKey = compressedKey + input.charAt(parityTable[i] - 1);
		}
		return compressedKey;
	}
	
	public static String initPermute(String input) {
		// Permutes each character according to IP
		int[] permLocations = 
			{58, 50, 42, 34, 26, 18, 10, 2
			, 60, 52, 44, 36, 28, 20, 12, 4
			, 62, 54, 46, 38, 30, 22, 14, 6
			, 64, 56, 48, 40, 32, 24, 26, 8
			, 57, 49, 41, 33, 25, 17, 9, 1
			, 59, 51, 43, 35, 27, 19, 11, 3
			, 61, 53, 45, 37, 29, 21, 13, 5
			, 63, 55, 47, 39, 31, 23, 15, 7};
		
		String outputString = "";
		for (int i = 0; i < input.length(); ++i) {
			outputString = outputString + input.charAt(permLocations[i] - 1);
		}
		return outputString;
	}
	
	
	
	
	// Need decryption method
}
