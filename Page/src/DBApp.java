import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
		ExtensibleHashTable newIndex = new ExtensibleHashTable(0.75f, 2);
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
		out = new ObjectOutputStream(new FileOutputStream(new File(name + Colname)));
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
