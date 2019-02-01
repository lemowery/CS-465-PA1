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
		/*
		 * Converts string key data given by user
		 * Returns binary string representation of key
		 */
		byte[] strBytes = input.getBytes();
		String output = toBinString(strBytes);
		return output;
	}
	
	public static String[] preProcessData(String input) {
		/*
		 * Converts string data from file
		 * Pads to complete blocks
		 * Divides data into 64-bit chunks
		 * Applies initial permutation
		 * Returns string array of 64-bit blocks
		 */
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
		for(int i = 0; i < input.length; ++i) {
			for(int j = 0; j <  16; ++j) {
				String leftString = input[i].substring((input[i].length() + 1) / 2);
				String rightString = input[i].substring(input[i].length() + 1);
				rightString = dboxExpansion(rightString);
				
			}
		}
		return null;	
	}
	
	public static String[] inputDivide(String input) {
		/*
		 *  Divides input string into 64-bit  blocks
		 */
		
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
		/*
		 *  Pads 00s onto byte array to complete 64-bit blocks
		 */
		
		String outputString = toBinString(input);
		for(int i = 0; i < 8 - (input.length % 8); ++i) {
			outputString = outputString + "00000000";
		}
		return outputString;
	}
	
	public static String toBinString(byte[] input) {
		/*
		 * Turns byte array into binary string representation
		 * Converts from 7-bit to 8-bit
		 */
		String output = "";
		for(int i = 0; i < input.length; ++i) {
			output = output + "0" + Integer.toBinaryString(input[i]);
		}
		return output;
	}
	
	public static String[] initialPermutaion(String[] input) {
		/*
		 * Applies initial permutation on each string block
		 * in string array
		 */
		String[] outStrings = input;
		for(int i = 0; i < input.length; ++i) {
			outStrings[i] = initPermute(input[i]);
		}
		return outStrings;
	}
	
	public static String[] keyGen(String input) {
		/*
		 * Generates String array of 16, 48-bit keys
		 * based on given key string
		 */
		String[] keys = new String[16];
		int[] shiftCycles = {1, 1, 2, 2, 2, 2, 2, 2
						   , 1, 2, 2, 2, 2, 2, 2, 1};
		String[] outputStrings = null;
		String parityString = parity(input);
		String compressedKey = keyCompress(parityString);
		String leftString = compressedKey.substring(0, (compressedKey.length() + 1) / 2);
		String rightString = compressedKey.substring((compressedKey.length() + 1) / 2);

		for(int i = 0; i < 16; ++i) {
			int j = 0;
			while(j < shiftCycles[i]) {
				leftString = keyShift(leftString);
				rightString = keyShift(rightString);
				++j;
			}
			String newString = leftString + rightString;
			newString = dBoxCompress(newString);
			keys[i]= newString; 
		}
		return keys;
	}
	
	public static String parity(String input) {
		/*
		 * Does odd parity correction on binary string input
		 * returns string with parity correction
		 */
		String outputString = "";
		for (int i = 0; i < input.length() / 8; ++i) {
			int parityCount = 0;
			for(int j = 1; j < 8; ++j) {
				outputString = outputString + input.charAt((i * 8) + j);
				if(input.charAt((i * 8) + j) == '1') {
					++parityCount;
				}
			}
			if((parityCount % 2) == 0) {
				outputString = outputString + '1';
			}
			else {
				outputString = outputString + '0';
			}
		}
		return outputString;
	}
	
	public static String keyCompress(String input) {
		/*
		 * Compresses 64-bit key to 56-bit key using parity-bit drop table
		 * Returns 56-bit binary string
		 */
		int[] parityTable = 
					 {57, 49, 41, 33, 25, 17, 9, 1
					, 58, 50, 42, 34, 26, 18, 10, 02
					, 59, 51, 43, 35, 27, 19, 11, 03
					, 60, 52, 44, 36, 63, 55, 47, 39
					, 31, 23, 15, 07, 62, 54, 46, 38
					, 30, 22, 14, 06, 61, 53, 45, 37
					, 29, 21, 13, 05, 28, 20, 12, 04};
		
		String compressedKey = "";		

		for(int i = 0; i < parityTable.length; ++i) {
			compressedKey = compressedKey + input.charAt(parityTable[i] - 1);
		}
		return compressedKey;
	}
	
	public static String initPermute(String input) {
		/*
		 *  Permutes each character in binary string according to IP
		 *  Returns new string with appropriate permutation
		 */
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
	
	public static String keyShift(String input) {
		/*
		 * Shifts string left one bit
		 * Returns shifted binary string
		 */
		String output = input.substring(1);
		output = output + input.charAt(0);
		return output;
	}
	
	public static String dBoxCompress(String input) {
		/*
		 * Compresses 56-bit input key to 48-bit key
		 * Returns 48-bit key
		 */
		String output = "";
		int[] keyCompressionTable = 
			 {14, 17, 11, 24, 01, 05, 03, 28
			, 15, 06, 21, 10, 23, 19, 12, 04
			, 26, 8, 16, 07, 27, 20, 13, 02
			, 41, 52, 31, 37, 47, 55, 30, 40
			, 51, 45, 33, 48, 44, 49, 39, 56
			, 34, 53, 46, 42, 50, 36, 29, 32};
		
		for (int i = 0; i < keyCompressionTable.length; ++i) {
			output = output + input.charAt(keyCompressionTable[i] - 1);
		}
		return output;
	}
	
	public static String dboxExpansion(String input) {
		/*
		 * Expands 32-bit right half to 48-bits
		 * Returns 48-bit binary string
		 */
		String output = "";
		int[] expansionTable = 
					 {32, 1, 2, 3, 4, 5
					, 4, 5, 6, 7, 8, 9
					, 8, 9, 10, 11, 12, 13
					, 12, 13, 14, 15, 16, 17
					, 16, 17, 18, 19, 20, 21
					, 20, 21, 22, 23, 24, 25
					, 24, 25, 26, 27, 28, 29
					, 28, 29, 30, 31, 32, 1};
		
		for(int i = 0; i < expansionTable.length; ++i) {
			output = output + input.charAt(expansionTable[i]- 1);
		}
		return output;
	}
	
	public static String XOR(String input1, String input2) {
		String output = "";
		
		for(int i = 0; i < input1.length(); ++i) {
			if((input1.charAt(i) == '1' && input2.charAt(i) == '1') || (input1.charAt(i) == '0' && input2.charAt(i) == '0')) {
				output = output + '0';
			}
			else {
				output = output + '1';
			}
		}
		
		return output;
	}
	
	
	// Need decryption method
}
