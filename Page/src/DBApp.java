import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;


public class DBApp {

	
	
	
	public void createTable(String name , Hashtable<String, String> name_type , Hashtable<String, String> name_ref , String key)
	throws IOException {
		int cols = name_type.size();
		new table(name, cols , name_type , key);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
