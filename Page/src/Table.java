import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;



public class Table implements Serializable {

	String name;
	int p_index = 0;
	int size = 0;
	int cols;
	HashMap<String, Integer> cNames = new HashMap<String, Integer>();
	ArrayList<String> indexes = new ArrayList<String>();
	ArrayList<String[]> multiIndexes = new ArrayList<String[]>();
	
	public Table(String name , int cols , Hashtable<String, String> name_type , String key) throws IOException {
		mapCols(name_type);
		this.name = name;
		this.cols = cols;
		indexes.add(key);
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(name)));
		ExtensibleHashTable key_index = new ExtensibleHashTable(2);
		out.writeObject(this);
		out.close();
		out = new ObjectOutputStream(new FileOutputStream(new File(name + key + "hash")));
		out.writeObject(key_index);
		out.close();
		Page first = new Page(name, p_index, cols);
		out = new ObjectOutputStream(new FileOutputStream(new File(name + "page" + p_index)));
		out.writeObject(first);
		out.close();
	}

	private void mapCols(Hashtable<String, String> name_type) {
		int index = 0;
		Set<String> keys = name_type.keySet();
		for(String key : keys) {
			cNames.put(key, index++);
		}
	}

	public void insertIntoPage(Hashtable<String, String> values) throws IOException , ClassNotFoundException {
		p_index = (size / 200);
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(name + "page" + p_index)));
			Page load = (Page) in.readObject();
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
			for(int i = 0 ; i < indexes.size() ; i++) {
				in = new ObjectInputStream(new FileInputStream(new File(name + indexes.get(i) + "hash")));
				ExtensibleHashTable temp = (ExtensibleHashTable) in.readObject();
				temp.put(values.get(indexes.get(i)), new Point(p_index, load.index - 1));
				out = new ObjectOutputStream(new FileOutputStream(new File(name + indexes.get(i) + "hash")));
				out.writeObject(temp);
				out.close();
			}
			for(int i = 0 ; i < multiIndexes.size() ; i++) {
				String temp = "";
				for(int j = 0 ; j < multiIndexes.get(i).length ; j++) {
					temp += multiIndexes.get(i)[j];
				}
				in = new ObjectInputStream(new FileInputStream(new File(name + temp + "kdt")));
				KDTree kdt = (KDTree) in.readObject();
				Object[] insert = new Object[multiIndexes.get(i).length];
				for(int k = 0 ; k  < multiIndexes.get(i).length ; k++) {
					insert[k] = values.get(multiIndexes.get(i)[k]);
				}
				kdt.insert(insert, new Point(p_index, load.index - 1));
				out = new ObjectOutputStream(new FileOutputStream(new File(name + temp + "kdt")));
				out.writeObject(kdt);
				out.close();
			}
			out = new ObjectOutputStream(new FileOutputStream(new File(name)));
			out.writeObject(this);
			out.close();
		}
		catch(FileNotFoundException e) {
			new Page(name ,p_index, cols);
			insertIntoPage(values);
		}
	//	System.out.println("here");
	}
	
	public ArrayList<Point> anding(ArrayList<Point>[] toBeAnded)
	{
		ArrayList<Point> result = new ArrayList<Point>();
		
		for(int i = 0; i < toBeAnded[0].size();i++)
		{
			boolean flag = false;
			
			for(int j = 1; j < toBeAnded.length; j++)
			{
				if(!toBeAnded[j].contains(toBeAnded[0].get(i)))
				{
					flag = true;
					break;
				}
				
				if(!flag)
				{
					result.add(toBeAnded[0].get(i));
				}
			}
				
		}
		
		return result;
	}
	
	public ArrayList<Point> oring(ArrayList<Point>[] toBeOred)
	{
		ArrayList<Point> result = new ArrayList<Point>();
		HashMap<Point, Integer> hm = new HashMap<Point, Integer>();
		
		for(int i = 0; i < toBeOred.length; i++)
		{
			for(int j = 0; j < toBeOred[i].size(); j++)
			{
				hm.put(toBeOred[i].get(j), 1);
			}
		}
		
		Set<Point> points = hm.keySet();
		
		for(Point p : points)
		{
			result.add(p);
		}
		
		return result;
	}
	
	public ArrayList<Point> selectFromPages(Hashtable<String, String> values , String opr) throws Exception {
		ArrayList<Point> result = new ArrayList<Point>();
		ArrayList<Point>[] results = new ArrayList[values.size()];
		for(int i = 0 ; i < results.length ; i++) {
			results[i] = new ArrayList<Point>();
		}
		Set<String> keys = values.keySet();
		int index = 0;
		for(String key : keys) {
			if(indexes.contains(key)) {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(name + key + "hash")));
				ExtensibleHashTable temp = (ExtensibleHashTable) in.readObject();
				in.close();
				results[index].add(new Point(temp.get(values.get(key)).x , temp.get(values.get(key)).y));
			}
			else {
				for(int i = 0 ; i <= p_index ; i++) {
					ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(name + "page" + i)));
					Page loaded = (Page) in.readObject();
					int field = cNames.get(key);
					for(int j = 0 ; j < loaded.index ; j++) {
						if(((String) loaded.data[j][field]).equals(values.get(key))) {
							results[index].add(new Point(i , j));
						}
					}
				}
			}
		}
		if(opr.equalsIgnoreCase("and")) {
			result = this.anding(results);
		}
		result = this.oring(results);
		return result;
	}

//	public static void main(String[] args) throws Exception {
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
//		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("testpage1")));
//		page test = (page) in.readObject();
//		System.out.println(test.data[0][0] + " " + test.data[0][1] + " " + test.data[0][2]);
//		System.out.println(test.data[1][0] + " " + test.data[1][1] + " " + test.data[1][2]);
//	}

}
