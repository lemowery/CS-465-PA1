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
	public static String[] encrypt(String[] input, String key) throws UnsupportedEncodingException {
		// 	Expand
		// XOR w key
		// S sub
		// P sub
		// XOR w Li
		String[] output = new String[input.length];
		String[] keys = keyGen(key);
		//for(int i = 0; i < input.length; ++i) {
			String leftString = input[0].substring(0, (input[0].length() + 1) / 2);
			String rightString = input[0].substring((input[0].length() + 1) / 2);
			for(int j = 0; j <  15; ++j) {
				
				// Function f
				rightString = eboxExpansion(rightString);
				rightString = XOR(rightString, keys[j]);
				rightString = sBoxSub(rightString);
				rightString = pBoxPerm(rightString);
				
				String tempString = rightString;
				leftString = XOR(leftString, rightString);
				rightString = leftString;
				leftString = tempString;
			}
			
			// Function f for last time
			rightString = eboxExpansion(rightString);
			rightString = XOR(rightString, keys[15]);
			rightString = sBoxSub(rightString);
			rightString = pBoxPerm(rightString);
			
			leftString = XOR(leftString, rightString);
			
			String combinedString = rightString + leftString;
			combinedString = inverseInitPerm(combinedString);
			System.out.println("0001001110000101101101000001000111000001110101011111001011110110");
			System.out.println(combinedString);

			
		//}
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
	
	public static String eboxExpansion(String input) {
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
		/*
		 * Performs bitwise XOR of binary string
		 * Returns result as binary string 
		 */
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

	public static String sBoxSub(String input) {
		/*
		 * Performs S-Box compression from 48-bits to 32-bits
		 * Returns 32-bit binary string
		 */
		String output = "";
		int[][] sBoxes = 
			{
			  {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7
			  , 0, 15, 7, 4, 14, 2, 13, 10, 3, 6, 12, 11, 9, 5, 3, 8
			  , 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0
			  , 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
			  
			, {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10
			  , 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5
			  , 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15
			  , 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
			
			, {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8
			  , 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1
			  , 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7
			  , 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
			
			, {7, 13, 4, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15
			  , 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9
			  , 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4
			  , 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
			
			, {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9
			  , 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6
			  , 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14
			  , 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
			
			, {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11
			  ,10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8
			  , 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6
			  , 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 10, 0, 8, 13}
			
			, {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1
			  , 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6
			  , 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2
			  , 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
			
			, {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7
			  , 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 10, 14, 9, 2
			  , 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 10, 15, 3, 5, 8
			  , 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 9, 3, 5, 6, 11}
			
			};
		
		for(int i = 0; i < 8; ++i) {
			String strByte = input.substring((i * 6), (i * 6) + 6);
			char[] outBits = new char[2];
			outBits[0] = strByte.charAt(0);
			outBits[1] = strByte.charAt(5);
			String outerString = new String(outBits);
			int row = Integer.parseInt(outerString, 2);
			String innerString = strByte.substring(1, 5);
			int col = Integer.parseInt(innerString, 2);
			String outString = Integer.toBinaryString(sBoxes[i][(row * 16) + col]);
			outString = String.format("%4s", outString);
			outString = outString.replace(" ", "0");
			output = output + outString;
		}
		return output;
	}
	
	public static String pBoxPerm(String input) {
		/*
		 * Performs P-Box straight permutation
		 * Returns permuted binary string
		 */
		String output = "";
		int[] pBox = 
			{16, 7, 20, 21, 29, 12, 28, 17
		    , 1, 15, 23, 26, 5, 18, 31, 10
		    , 2, 8, 24, 14, 32, 27, 3, 9 
		    , 19, 13, 30, 6, 22, 11, 4, 25};
		
		for (int i = 0; i < pBox.length; ++i) {
			output = output + input.charAt(pBox[i] - 1);
		}
		return output;
	}

	public static String inverseInitPerm(String input) {
		/*
		 * Performs inverse initial permutation on binary string
		 * Returns permuted binary string
		 */
		String output = "";
		int[] inverseP = 
			{40, 8, 48, 16, 56, 24, 64, 32
		    , 39, 7, 47, 15, 55, 23, 63, 31
		    , 38, 6, 46, 14, 54, 22, 62, 30
		    , 37, 5, 45, 13, 53, 21, 61, 29
		    , 36, 4, 44, 12, 52, 20, 60, 28
		    , 35, 3, 43, 11, 51, 19, 59, 27
		    , 34, 2, 42, 10, 50, 18, 58, 26
		    , 33, 1, 41, 9, 49, 17, 57, 25};
		
		for (int i = 0; i < inverseP.length; ++i) {
			output = output + input.charAt(inverseP[i] - 1);
		}
		
		return output;
	}
	// Need decryption method
}
