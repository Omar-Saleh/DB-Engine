import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class WriteObject {
	
	static int number =0;
	
	//Parameters schema : 
	public void write(String NameOfTheObject, String Parameters){
		
	}
	
	
	public static void main(String[] args) {
		Person a = new Person("ahmed",20);
		Person b = new Person("aly",21);
		
		System.out.println(a);
		System.out.println(b);
		
		ArrayList <FileOutputStream> pages = new ArrayList <FileOutputStream>();
		try (FileOutputStream fs = new FileOutputStream("page"+ number+ ".class")){
			
			ObjectOutputStream os = new ObjectOutputStream(fs);
			os.writeObject(a);
			os.writeObject(b);
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
