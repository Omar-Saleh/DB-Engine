import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;


public class table implements Serializable {

	String name;
	int p_index;
	int size = 0;
	int cols;
	HashMap<String, Integer> cNames = new HashMap<String, Integer>();
	
	public table(String name , int cols) {
		this.name = name;
		this.cols = cols;
	}

	public void insertIntoPage(Hashtable<String, String> values) throws IOException , ClassNotFoundException {
		p_index = (size / 200) + 1;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("page" + p_index)));
			page load = (page) in.readObject();
			in.close();
			Set<String> keys = values.keySet();
			for(String key : keys) {
				int insert = cNames.get(key);
				load.data[load.num][insert] = values.get(key);
				load.num++;
			}
			
		}
		catch(FileNotFoundException e) {
			new page(name ,p_index, cols);
			insertIntoPage(values);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
