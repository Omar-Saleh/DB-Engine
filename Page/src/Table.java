import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(name +"page" + p_index)));
			page load = (page) in.readObject();
			in.close();
			Set<String> keys = values.keySet();
			for(String key : keys) {
				int insert = cNames.get(key);
				System.out.println(insert + " " + load.index);
				load.data[load.index][insert - 1] = values.get(key);
			}
			load.index = load.index + 1;
			size++;
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(name + "page" + p_index)));
			out.writeObject(load);
			out.close();
		}
		catch(FileNotFoundException e) {
			new page(name ,p_index, cols);
			insertIntoPage(values);
		}
		System.out.println("here");
	}

	public static void main(String[] args) throws Exception {
//		table x = new table("test", 2);
//		x.cNames.put("Name", 1);
//		x.cNames.put("Age", 2);
//		Hashtable<String, String> values = new Hashtable<String, String>();
//		values.put("Name", "Ahmad");
//		values.put("Age" , "5");
//		x.insertIntoPage(values);
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("testpage1")));
		page test = (page) in.readObject();
		System.out.println(test.data[0][0] + " " + test.data[0][1]);
	}

}
