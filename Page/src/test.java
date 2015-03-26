import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;


public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			Hashtable<String, String> v = new Hashtable<String, String>();
			v.put("Character", "c");
			v.put("Boolean", "true");
			v.put("Integer", "2");
			v.put("String", "z");
			v.put("String", "y");
			Set<String> keys = v.keySet();
			int x = Integer.MIN_VALUE;
			System.out.println(x);
			int y = 1 << 31;
			int z = y & (1 << 31);
		//	y |= 1 << 30;
			String b = Integer.toBinaryString(y);
			int m = Integer.parseInt(Integer.toBinaryString(y).substring(0, 2) , 2);
			System.out.println(m);
			int count = 0;
			for(int i = 0 ; i < b.length() ; i++) {
				++count;
			}
			System.out.println(count);
			System.out.println(z);
			System.out.println(b);
	//	Random gen = new Random();
	//	for(int i = 0 ; i < 10 ; i++)
	//		System.out.println(gen.nextInt());
	}

}
