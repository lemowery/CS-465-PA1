
public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String test = "111111111111111111111111111111111111111111111111";
		String test2 = "00000000000000000000000000000000000000000000000";
		int int1 = Integer.parseInt(test, 2);
		int int2 = Integer.parseInt(test2, 2);
		int1 = int1 ^ int2;
		System.out.println(Integer.toBinaryString(int1));
	}

}
