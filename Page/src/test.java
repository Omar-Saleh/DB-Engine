import java.util.Hashtable;
import java.util.Iterator;
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
		for(String key : keys) {
			System.out.println(v.get(key));
		}
	}

}
