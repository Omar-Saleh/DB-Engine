import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;


public class Page implements Serializable {

	Object[][] data;
	int index = 0;
	int num;
	
	public Page(String name , int num , int cols) throws IOException {
		this.num = num;
		data = new Object[200][cols + 1];
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(name + "page" + num)));
		out.writeObject(this);
		out.close();
	}

	
	public static void main(String[] args) throws IOException , ClassNotFoundException {
//		Page test = new Page(1);
//		test.insert(5);
//		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("page" + test.num)));
//		out.writeObject(test);
//	 	out.close();
//		ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File("page1")));
//		Page test = (Page) in.readObject();
//		in.close();
//		System.out.println(test.data[0][0]);
	}

}
