import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;


public class ReadObject {

	public static void main(String[] args) {
		
		try (FileInputStream fs = new FileInputStream("person.class")){
			
			ObjectInputStream os = new ObjectInputStream(fs);
			
			Person persona = (Person) os.readObject();
			Person personb = (Person) os.readObject();
			
			os.close();
			
			System.out.println(persona);
			System.out.println(personb);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
