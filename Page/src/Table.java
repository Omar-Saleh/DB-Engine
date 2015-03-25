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
	
	public table(String name , int cols , Hashtable<String, String> name_type) {
		mapCols(name_type);
		this.name = name;
		this.cols = cols;
	}

	private void mapCols(Hashtable<String, String> name_type) {
		int index = 0;
		Set<String> keys = name_type.keySet();
		for(String key : keys) {
			cNames.put(key, index++);
		}
	}

	public void insertIntoPage(Hashtable<String, String> values) throws IOException , ClassNotFoundException {
		p_index = (size / 200) + 1;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(name + "page" + p_index)));
			page load = (page) in.readObject();
			in.close();
			Set<String> keys = values.keySet();
			for(String key : keys) {
				int insert = cNames.get(key);
				System.out.println(insert + " " + load.index);
				load.data[load.index][insert] = values.get(key);
			}
			load.data[load.index][cols] = false;
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
	//	System.out.println("here");
	}

	public static void main(String[] args) throws Exception {
//		HashMap<String, Integer> cNames = new HashMap<String, Integer>();
//		cNames.put("Name", 1);
//		cNames.put("Age", 2);
//		table x = new table("test", 2 , cNames);
//		Hashtable<String, String> values = new Hashtable<String, String>();
//		values.put("Name", "Ahmad");
//		values.put("Age" , "5");
//		x.insertIntoPage(values);
//		values.clear();
//		values.put("Name", "Omar");
//		values.put("Age" , "20");
//		x.insertIntoPage(values);
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("testpage1")));
		page test = (page) in.readObject();
		System.out.println(test.data[0][0] + " " + test.data[0][1] + " " + test.data[0][2]);
		System.out.println(test.data[1][0] + " " + test.data[1][1] + " " + test.data[1][2]);
	}

}
