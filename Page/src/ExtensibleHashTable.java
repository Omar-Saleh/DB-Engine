import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author  Omar Saleh
 */
public class ExtensibleHashTable implements Map<Object, Point> , Serializable {

	/**
	 * @uml.property  name="loadFactor"
	 */
	private float loadFactor;
	/**
	 * @uml.property  name="bucketSize"
	 */
	private int bucketSize;
	/**
	 * @uml.property  name="size"
	 */
	private int size;
	/**
	 * @uml.property  name="digits"
	 */
	private int digits;
	/**
	 * @uml.property  name="hashSeed"
	 */
	private int hashSeed;
	/**
	 * @uml.property  name="numberOfItems"
	 */
	private int numberOfItems;
	/**
	 * @uml.property  name="buckets"
	 * @uml.associationEnd  multiplicity="(0 -1)" inverse="this$0:data_structures.linearHashTable.LinearHashTable$Bucket"
	 */
	private ArrayList<Bucket> buckets;

	public ExtensibleHashTable(float loadFactor, int bucketSize) {
		this.loadFactor = loadFactor;
		this.bucketSize = bucketSize;
		buckets = new ArrayList<>();
		init();
	}

	private void init() {
		size = 2;
		digits = 1;
		Bucket bucket = new Bucket(bucketSize);
		buckets.add(bucket);
		bucket = new Bucket(bucketSize);
		buckets.add(bucket);
		Random generator = new Random();
		hashSeed = generator.nextInt();
	}

	@Override
	public int size() {
		return numberOfItems;
	}

	@Override
	public boolean isEmpty() {
		return numberOfItems == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		return getEntry(key) != null;
	}

	private LHTEntry getEntry(Object key) {
		if (key instanceof Object){
			//	System.out.println(key);
			int b = getBucket((Object)key);
			//	System.out.println(b);
			Bucket bucket = buckets.get(b);
			//	for(int i = 0 ; i < bucket.entries.length ; i++) {
			//		System.out.println(bucket.entries[i].value);
			//	}
			LHTEntry entry;
			entry = bucket.getEntry(key);
			//	System.out.println(entry);
			return entry;
		}
		return null;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Point get(Object key) {
		LHTEntry entry = getEntry(key);
		return null == entry ? null : entry.getValue();
	}

	public int getBucket(Object key){
		int hash = //key.hashCode(); 
					hash(key);
		//	number |= (1 << (32 - digits));
		//	int bits = hash;
		//	System.out.println("digits : " + digits);
		int bits = hash == 0 ? 0 : Integer.parseInt(Integer.toBinaryString(hash).substring(0, digits) , 2);
		//	System.out.println("bits : " + bits);
		//	System.out.println(Integer.toBinaryString(bits).substring(0 , digits));
		//	System.out.println(Integer.parseInt(Integer.toBinaryString(bits).substring(0, digits) , 2));
		//	System.out.println("-----");
		//	bits = Integer.parseInt(Integer.toBinaryString(bits).substring(0, digits) , 2);
		if(bits < size){
			return bits;
		}else{
			bits = bits - (int)Math.pow(2, (digits-1));
			return bits;
		}
	}

	@Override
	public Point put(Object key, Point value) {
		int b = getBucket(key);
		//	System.out.println("bucket for insert:" + b);
		//	System.out.println("buckets number :" + buckets.size());
		//	System.err.println(buckets.size());
		Bucket bucket = buckets.get(b);
		int hash = hash(key);
		bucket.put(key, value, hash);
		numberOfItems++;
		if((float) numberOfItems / ((size) * bucketSize) >= loadFactor){
			//		System.out.println("!!!");
			resize();
		}
		//	System.out.println("hashtable size : " + numberOfItems);
		//System.out.println("-------");
		return null;
	}

	private void resize() {
		//		size++;v
		int temp = size;
		size *= 2;
		for(int i = 0 ; i < temp ; i++) {
			Bucket b = new Bucket(bucketSize);
			buckets.add(b);
		}
		digits++;
		for(int i = 0 ; i < temp ; i++) {
			Bucket bucket = buckets.get(i);
			bucket.scan();
		}
		//	System.out.println("size after resizing :" + size);
	}

	public void downSize(){

	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	final int hash(Object k) {
		int h = hashSeed;
		h ^= k.hashCode();
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	@Override
	public Point remove(Object key) {
		int b = getBucket(key);
		Bucket bucket = buckets.get(b);
		LHTEntry entry = bucket.remove((Object)key);
		numberOfItems--;
		return entry.value;
	}

	@Override
	public void putAll(Map<? extends Object, ? extends Point> m) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<Object> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Point> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Entry<Object, Point>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @author   Omar Saleh
	 */
	class Bucket implements Serializable {
		/**
		 * @uml.property  name="entries"
		 * @uml.associationEnd  multiplicity="(0 -1)"
		 */
		LHTEntry[] entries;
		int lastItem;
		LinkedList<LHTEntry> overflow;

		public Bucket(int bucketSize) {
			entries = new LHTEntry[bucketSize];
			lastItem = 0;
		}

		public LHTEntry remove(Object key) {
			LHTEntry r = null;
			for (int i = 0; i < lastItem; i++) {
				if(entries[i].getKey().equals(key)){
					r = entries[i];
					for (int j = i; j < lastItem-1; j++) {
						entries[j] = entries[j+1];
					}
					if(overflow != null){
						if(! overflow.isEmpty()){
							LHTEntry entry =overflow.removeFirst();
							entries[entries.length-1] = entry;
							lastItem++;
						}
					}
					lastItem--;
					return r;
				}
			}
			if(overflow != null){
				Iterator<LHTEntry> itr = overflow.iterator();
				while(itr.hasNext()) {
					LHTEntry element = itr.next();
					if ((element.getKey()).equals(key)){
						r = element;
						itr.remove();
						break;
					}
				}
			}
			return r;
		}

		public LHTEntry getEntry(Object key) {
			Object dataKey = (Object) key;
			for (int i = 0; i < lastItem; i++) {
				if(entries[i].getKey().equals(dataKey)){
					return entries[i];
				}
			}
			if(overflow != null) {
				Iterator<LHTEntry> itr = overflow.iterator();
				while(itr.hasNext()) {
					LHTEntry element = itr.next();
					if(element.getKey().equals(dataKey))
						return element;
				}
			}
			return null;
		}

		public void put(Object key, Point value, int hash) {
			if(lastItem == entries.length){
				overflow.add(new LHTEntry(key, value, hash));
			}else{
				entries[lastItem++] = new LHTEntry(key, value, hash);
				if(lastItem == entries.length){
					overflow = new LinkedList<>();
				}
			}
		}

		public void scan() {
			for (int i=0; i< lastItem; i++){
				int bits = entries[i].hash;
				bits = bits == 0 ? 0 : Integer.parseInt(Integer.toBinaryString(bits).substring(0 , digits) , 2);
				if(bits > (int)Math.pow(2, digits-1)-1){
					LHTEntry entry = entries[i];
					remove(entries[i].key);
					numberOfItems--;
					ExtensibleHashTable.this.put(entry.getKey(),entry.getValue());
					i--;
				}
			}
			if (overflow != null){
				Iterator<LHTEntry> itr = overflow.iterator();
				while(itr.hasNext()) {
					LHTEntry element;
					element = itr.next();
					int bits = element.hash;
					bits = bits == 0 ? 0 : Integer.parseInt(Integer.toBinaryString(bits).substring(0 , digits));
					if(bits > (int)Math.pow(2, digits-1)-1){
						itr.remove();
						numberOfItems--;
						ExtensibleHashTable.this.put(element.getKey(),element.getValue());
					}
				}
			}
		}
	}

	/**
	 * @author   Omar Saleh
	 */
	class LHTEntry implements Entry<Object, Point> , Serializable {
		/**
		 * @uml.property  name="key"
		 * @uml.associationEnd  
		 */
		private Object key;
		/**
		 * @uml.property  name="value"
		 * @uml.associationEnd  
		 */
		private Point value;
		private int hash;

		public LHTEntry(Object key, Point value, int hash) {
			this.key = key;
			this.value = value;
			this.hash = hash;
		}

		/**
		 * @return
		 * @uml.property  name="value"
		 */
		public Point getValue(){
			return value;
		}

		/**
		 * @return
		 * @uml.property  name="key"
		 */
		@Override
		public Object getKey() {
			return key;
		}

		/**
		 * @param value
		 * @return
		 * @uml.property  name="value"
		 */
		@Override
		public Point setValue(Point value) {
			Point old = this.value;
			this.value = value;
			return old;
		}
	}

//	public static void main(String[] args) {
//		ExtensibleHashTable test = new ExtensibleHashTable(0.75f, 2);
//		for(int i = 0 ; i <= 1000 ; i++) {
//			test.put(i, new Point(i, i));
		//	System.out.println(test.buckets.size());
//		}
//		System.out.println(test.get(1).x);
//		for (int i = 0; i <= 1000; i++) {
//			if(i == 500)
//				System.out.println();
//			System.out.print(test.get(i).x + "" + test.get(i).y);
//		}
//	}

}