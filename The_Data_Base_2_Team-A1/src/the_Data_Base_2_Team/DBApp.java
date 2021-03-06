package the_Data_Base_2_Team;


import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import libs.ExtensibleHashTable;
import libs.KDTree;



public class DBApp {

	
	
	public DBApp() throws IOException
	{
		init();
	}
	
	public void createTable(String name , Hashtable<String, String> name_type , Hashtable<String, String> name_ref , String key)
	throws IOException {
		int cols = name_type.size();
		new Table(name, cols , name_type , key);
		
		Set<String> keys = name_type.keySet();
		
		for(String col : keys)
		{
			String entry = name;
			entry += ", " + col + ",java.lang." + name_type.get(col) + ", " + (key.equals(col) ? "True" : "False") + ", " + (key.equals(col) ? "True" : "False") + "null";
			PrintWriter pr = new PrintWriter("./data/metadata.csv");
			pr.write(entry + "\n");
			pr.flush();
			pr.close();
		}
		
	}
	
	public void init() throws IOException
	{
		new File("./data/metadata.csv");
		Properties properties = new Properties();
		properties.setProperty("MaximumRowsCountinPage", "200");
		properties.setProperty("BucketSize", "3");
		File file = new File("./config/DBApp.properties");
		FileOutputStream fileOut = new FileOutputStream(file);
		properties.store(fileOut, "DB Engine");
		fileOut.close();
	}
	
	public void createIndex(String name , String Colname) throws IOException , ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("./data/" + name)));
		Table load = (Table) in.readObject();
		in.close();
		load.indexes.add(Colname);
		File file = new File("./config/DBApp.properties");
		FileInputStream in1 = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(in1);
		ExtensibleHashTable newIndex = new ExtensibleHashTable(Integer.parseInt(properties.getProperty("BucketSize")));
		for(int i = 0 ; i <= load.p_index ; i++) {
			in = new ObjectInputStream(new FileInputStream(new File("./data/" + name + "page" + i)));
			Page toBeInserted = (Page) in.readObject();
			in.close();
			for(int j = 0 ; j < toBeInserted.index ; j++) {
				newIndex.put(toBeInserted.data[j][load.cNames.get(Colname)], new Point(i, j));
			}
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("./data/" + name)));
		out.writeObject(load);
		out.close();
		out = new ObjectOutputStream(new FileOutputStream(new File("./data/" + name + Colname + "hash")));
		out.writeObject(newIndex);
		out.close();
	}
	
	public void insertIntoTable(String name , Hashtable<String, String> values) throws IOException , ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("./data/" + name)));
		Table load = (Table) in.readObject();
		in.close();
		load.insertIntoPage(values);
	}
	
	public void createMultiDimIndex(String name, ArrayList<String> colNames) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("./data/" + name)));
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
		for(int i = 0 ; i <= load.p_index ; i++) {
			in = new ObjectInputStream(new FileInputStream(new File(name + "page" + i)));
			Page toBeInserted = (Page) in.readObject();
			in.close();
			for(int j = 0 ; j < toBeInserted.index ; j++) {
				Object[] insert = new Object[colNames.size()];
				for(int k = 0 ; k  < colNames.size() ; k++) {
					insert[k] = toBeInserted.data[j][load.cNames.get(colNames.get(k))];
				}
				kdt.insert(insert, new Point(i , j));
			}
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("./data/" + name)));
		out.writeObject(load);
		out.close();
		out = new ObjectOutputStream(new FileOutputStream(new File("./data/" + name + temp + "kdt")));
		out.writeObject(kdt);
		out.close();
	}
	
	public void deleteFromTable(String name, Hashtable<String,String> htblColNameValue, String opr) throws Exception {
		
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("./data/" + name)));
		Table t = (Table)in.readObject();
		ArrayList<Point> toBeDeleted = t.selectFromPages(htblColNameValue,  opr);
		in.close();
		
		for(int i = 0; i < toBeDeleted.size(); i++)
		{
			in =  new ObjectInputStream(new FileInputStream(new File("./data/" + name + "page" + toBeDeleted.get(i).x)));
			Page p = (Page)in.readObject();
			in.close();
			p.data[(int) toBeDeleted.get(i).y][t.cols] = true;
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("./data/" + name + "page" + toBeDeleted.get(i).x)));
			out.writeObject(p);
			out.close();
		}
		
	}
	
	
	public Iterator<Object[]> selectFromTable(String name, Hashtable<String, String> htblColNameValue, String opr) throws Exception {
		
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("./data/" + name)));
		Table t = (Table)in.readObject();
		ArrayList<Point> toBeComputed = t.selectFromPages(htblColNameValue, opr);
		return t.returnSelect(toBeComputed);
	}
	
	
	
	public static void main(String[] args) throws Exception {
////		// TODO Auto-generated method stub
        DBApp x = new DBApp();
//        ArrayList<String> al = new ArrayList<String>();
//        al.add("Age");
//        al.add("ID");
//        x.createMultiDimIndex("Student", al);
		Hashtable<String, String> types = new Hashtable<>();
		types.put("Name", "String");
		types.put("ID", "Integer");
		types.put("Age", "Integer");
		x.createTable("Student" , types, types, "ID");
//		Hashtable<String, String> values = new Hashtable<>();
//		values.put("Name", "Clay");
//		values.put("ID", "3");
//		values.put("Age" , "22");
//		x.insertIntoTable("Student", values);
//    	x.createIndex("Student",  "Age");
		
//		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("StudentAgeIDkdt")));
//		KDTree temp = (KDTree) in.readObject();
//		in.close();
//		System.out.println(temp.get("3"));
//	    String[] arr = {"22", "3"};
//		
//		System.out.println(temp.search(arr));
//		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("Student")));
//		out.writeObject(temp);
//		out.close();
		
//		Hashtable<String, String> values = new Hashtable<>();
//		values.put("ID", "3");
//		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("Studentpage0")));
//		Page temp = (Page) in.readObject();
//		in.close();
//		in = new ObjectInputStream(new FileInputStream(new File("Student")));
//		Table temp1 = (Table) in.readObject();
//		in.close();
////		//ArrayList<Point> test = temp.selectFromPages(values, "OR");
////		
////		x.deleteFromTable("Student", values, "or");
//		System.out.println(temp.data[0][temp1.cols]);
//		
//		
//		
//		for(String index : temp.multiIndexes.get(0)) {
//			System.out.println(index);
//			}
	}

}
