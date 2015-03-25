import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author  mohamed
 */
public class ExtensibleHashTable implements Map<Object, Object> {

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

	private int number = 0;
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
		size = 0;
		digits = 1;
		Bucket bucket = new Bucket(bucketSize);
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
			int b = getBucket((Object)key);
			Bucket bucket = buckets.get(b);
			LHTEntry entry;
			entry = bucket.getEntry(key);
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
	public Object get(Object key) {
		LHTEntry entry = getEntry(key);
		return null == entry ? null : entry.getValue();
	}

	public int getBucket(Object key){
		int hash = hash(key);
		number |= (1 << (32 - digits));
		int bits = hash & number;
		bits = Integer.parseInt(Integer.toBinaryString(bits).substring(0, digits));
		if(bits <= size){
			return bits;
		}else{
			bits = bits - (int)Math.pow(2, (digits-1));
			return bits;
		}
	}

	@Override
	public Object put(Object key, Object value) {
		int b = getBucket(key);
		Bucket bucket = buckets.get(b);
		int hash = hash(key);
		bucket.put(key, value, hash);
		numberOfItems++;
		if(numberOfItems / ((size+1) * bucketSize) >= loadFactor){
			resize();
		}
		return null;
	}

	private void resize() {
		//		size++;
		for(int i = 0 ; i < size ; i++) {
			Bucket b = new Bucket(bucketSize);
			buckets.add(b);
		}
		digits++;
		for(int i = 0 ; i < size ; i++) {
			Bucket bucket = buckets.get(i);
			bucket.scan();
		}
		size *= 2;
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
	public Object remove(Object key) {
		int b = getBucket(key);
		Bucket bucket = buckets.get(b);
		LHTEntry entry = bucket.remove((Object)key);
		numberOfItems--;
		return entry.value;
	}

	@Override
	public void putAll(Map<? extends Object, ? extends Object> m) {
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
	public Collection<Object> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Entry<Object, Object>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @author   mohamed
	 */
	class Bucket {
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
			for (int i = 0; i < lastItem; i++) {
				Object dataKey = (Object) key;
				if(entries[i].getKey().equals(dataKey)){
					return entries[i];
				}
			}
			return null;
		}

		public void put(Object key, Object value, int hash) {
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
				int bits = entries[i].hash & number;
				bits = Integer.parseInt(Integer.toBinaryString(bits).substring(0 , digits));
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
					int bits = element.hash & number;
					bits = Integer.parseInt(Integer.toBinaryString(bits).substring(0 , digits));
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
	 * @author   mohamed
	 */
	class LHTEntry implements Entry<Object, Object>{
		/**
		 * @uml.property  name="key"
		 * @uml.associationEnd  
		 */
		private Object key;
		/**
		 * @uml.property  name="value"
		 * @uml.associationEnd  
		 */
		private Object value;
		private int hash;

		public LHTEntry(Object key, Object value, int hash) {
			this.key = key;
			this.value = value;
			this.hash = hash;
		}

		/**
		 * @return
		 * @uml.property  name="value"
		 */
		public Object getValue(){
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
		public Object setValue(Object value) {
			Object old = this.value;
			this.value = value;
			return old;
		}
	}

}