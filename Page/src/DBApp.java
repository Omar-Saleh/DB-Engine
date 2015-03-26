import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;


public class DBApp {

	
	
	
	public void createTable(String name , Hashtable<String, String> name_type , Hashtable<String, String> name_ref , String key)
	throws IOException {
		int cols = name_type.size();
		new Table(name, cols , name_type , key);
	}
	
	public void createIndex(String name , String Colname) throws IOException , ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(name)));
		Table load = (Table) in.readObject();
		in.close();
		load.indexes.add(Colname);
		ExtensibleHashTable newIndex = new ExtensibleHashTable(2);
		for(int i = 0 ; i <= load.p_index ; i++) {
			in = new ObjectInputStream(new FileInputStream(new File(name + "page" + i)));
			Page toBeInserted = (Page) in.readObject();
			in.close();
			for(int j = 0 ; j < toBeInserted.index ; j++) {
				newIndex.put(toBeInserted.data[j][load.cNames.get(Colname)], new Point(i, j));
			}
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(name)));
		out.writeObject(load);
		out.close();
		out = new ObjectOutputStream(new FileOutputStream(new File(name + Colname + "hash")));
		out.writeObject(newIndex);
		out.close();
	}
	
	public void insertIntoTable(String name , Hashtable<String, String> values) throws IOException , ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(name)));
		Table load = (Table) in.readObject();
		in.close();
		load.insertIntoPage(values);
		ObjectOutputStream out = new ObjectOutputStream(new ObjectOutputStream(new FileOutputStream(name)));
		out.writeObject(load);
		out.close();
	}
	
	public void createMultiDimIndex(String name, ArrayList<String> colNames) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(name)));
		Table load = (Table) in.readObject();
		in.close();
		KDTree kdt = new KDTree(colNames.size());
		String temp = "";
		String[] indexes = new String[colNames.size()];
		for(int i = 0 ; i < colNames.size() ; i++) {
			temp += colNames.get(i);
			indexes[i] = colNames.get(i);
		}
		load.multiIndexes.add(indexes);
		for(int i = 0 ; i < load.p_index ; i++) {
			in = new ObjectInputStream(new FileInputStream(new File(name + "page" + i)));
			Page toBeInserted = (Page) in.readObject();
			in.close();
			for(int j = 0 ; j < toBeInserted.index ; j++) {
				kdt.insert(indexes, new Point(i , j));
			}
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(name)));
		out.writeObject(load);
		out.close();
		out = new ObjectOutputStream(new FileOutputStream(new File(name + temp + "kdt")));
		out.writeObject(kdt);
		out.close();
	}
	
	public static void main(String[] args) throws Exception {
////		// TODO Auto-generated method stub
//		Hashtable<String, String> types = new Hashtable<>();
//		types.put("Name", "String");
//		types.put("ID", "Integer");
//		types.put("Age", "Integer");
//		DBApp x = new DBApp();
//		x.createTable("Student" , types, types, "ID");
//		Hashtable<String, String> values = new Hashtable<>();
//		values.put("Name", "Ahmad");
//		values.put("ID", "1");
//		values.put("Age" , "20");
//		x.insertIntoTable("Student", values);
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("Studentpage0")));
		Page temp = (Page) in.readObject();
		in.close();
		System.out.println(temp.data[0][0]);
	}

}
