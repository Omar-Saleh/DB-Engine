
import java.util.ArrayList;
import java.util.Arrays;

class Node {
	String key;
	Node left;
	Node right;
	Node parent;
	Bucket leftBucket;
	Bucket rightBucket;
	int level;

	public Node() {

	}

	public Node(String key) {
		this.key = key;
	}

}

class Bucket {
	ArrayList<String> values;

	// Node parent;
	public Bucket() {
		this.values = new ArrayList<String>();
	}

	public void put(String val) {
		if (this.values == null) {
			this.values = new ArrayList<String>();
		}
		this.values.add(val);
	}
}

public class KDTree {

	Node root;
	String key1Name;
	String key2Name;
	int max = 3;
	int columnNumberOfKey1 = 0; // assign it to the column number of key one(the
								// Table has a hashtable of columns, this
								// variable
								// is the index of the column that we build the
								// index on )
	int columnNumberOfKey2 = 0; // like the previous variable
	table table; // assign it to the table you build the kd on

	public KDTree(int maxPerBucket) {
		key1Name = "";
		key2Name = "";
		this.root = new Node();
		root.level = 1;
		root.parent = null;
		root.leftBucket = new Bucket();
		root.rightBucket = new Bucket();
		this.max = maxPerBucket;
	}

	public KDTree(Node root) {
		this.root = root;
	}

	public void put(String k1, String k2, String value) {
		if (root.key == null) {
			root.key = k1;
			root.leftBucket.put(value);

		} else {
			int level = 1;
			Node current = root;
			int cmp = 0;
			Node parent = null;
			while (current != null) {
				if (level % 2 == 1) {
					cmp = k1.compareTo(current.key);
				} else {
					cmp = k2.compareTo(current.key);
				}
				parent = current;
				if (cmp >= 0) {
					current = current.left;
				} else {
					current = current.right;
				}
				level = 1 - level;
			}

			if (cmp >= 0) {
				if (parent.leftBucket == null) {
					parent.leftBucket = new Bucket();
					parent.leftBucket.put(value);
				} else {
					Bucket tempbucket = parent.leftBucket;
					if (tempbucket.values.size() < this.max) {
						parent.leftBucket.put(value);
					} else {
						Node tempNode = new Node();
						tempNode.parent = parent;
						parent.left = tempNode;
						int parentLevel = parent.level;
						tempNode.level = 1 - parentLevel;
						ArrayList<String> a = loadAllValuesInBucket(tempbucket);
						parent.leftBucket = null;
						tempNode.leftBucket = new Bucket();
						tempNode.rightBucket = new Bucket();
						for (String s : a) {
							// redistribute the original bucket on left and
							// right buckets of the new node
							String[] listOfvaluesInRecord = s.split(",");
							String compareVal = "";
							int compareIndex = 0;
							if (tempNode.level % 2 == 1) { // compare k1
								compareVal = getAvg(a, 1);
								compareIndex = columnNumberOfKey1;
							} else { // compare k2
								compareVal = getAvg(a, 2);
								compareIndex = columnNumberOfKey2;
							}
							tempNode.key = compareVal;
							if (listOfvaluesInRecord[compareIndex]
									.compareTo(tempNode.key) >= 0) {
								tempNode.leftBucket
										.put(listOfvaluesInRecord[compareIndex]);
							} else {
								tempNode.rightBucket
										.put(listOfvaluesInRecord[compareIndex]);
							}

						}
						// int cmp2 = 0;
						// if (tempNode.level % 2 == 1) {
						// cmp2 = k1.compareTo(tempNode.key);
						// }
						// else {
						// cmp2 = k2.compareTo(tempNode.key);
						// }
						// if (cmp2 >= 0) {
						// tempNode.leftBucket.put(value);
						// }
						// else {
						// tempNode.leftBucket.put(value);
						// }
						//
					}
				}
			} else {
				if (parent.rightBucket == null) {
					parent.rightBucket = new Bucket();
					parent.rightBucket.put(value);
				} else {

					Bucket tempbucket = parent.rightBucket;
					if (tempbucket.values.size() < this.max) {
						parent.rightBucket.put(value);
					} else {
						Node tempNode = new Node();
						tempNode.parent = parent;
						parent.right = tempNode;
						int parentLevel = parent.level;
						tempNode.level = 1 - parentLevel;
						ArrayList<String> a = loadAllValuesInBucket(tempbucket);
						parent.rightBucket = null;
						tempNode.leftBucket = new Bucket();
						tempNode.rightBucket = new Bucket();
						for (String s : a) {
							// redistribute the original bucket on left and
							// right buckets of the new node
							String[] listOfvaluesInRecord = s.split(",");
							String compareVal = "";
							int compareIndex = 0;
							if (tempNode.level % 2 == 1) { // compare k1
								compareVal = getAvg(a, 1);
								compareIndex = columnNumberOfKey1;
							} else { // compare k2
								compareVal = getAvg(a, 2);
								compareIndex = columnNumberOfKey2;
							}
							tempNode.key = compareVal;
							if (listOfvaluesInRecord[compareIndex]
									.compareTo(tempNode.key) >= 0) {
								tempNode.leftBucket
										.put(listOfvaluesInRecord[compareIndex]);
							} else {
								tempNode.rightBucket
										.put(listOfvaluesInRecord[compareIndex]);
							}

						}
						int cmp2 = 0;
						if (tempNode.level % 2 == 1) {
							cmp2 = k1.compareTo(tempNode.key);
						} else {
							cmp2 = k2.compareTo(tempNode.key);
						}
						if (cmp2 >= 0) {
							tempNode.leftBucket.put(value);
						} else {
							tempNode.leftBucket.put(value);
						}
					}

				}

			}
		}
	}

	// get the average value in the bucket to be the new key
	public String getAvg(ArrayList<String> a, int keyNumber) {
		String avg = "";
		String[] toBeSorted = new String[a.size()];
		int index = 0;
		if (keyNumber == 1) {
			index = columnNumberOfKey1;
		} else {
			index = columnNumberOfKey2;
		}
		int i = 0;
		for (String string : a) {
			String[] columnValues = string.split(",");
			toBeSorted[i] = columnValues[index];
			i++;
		}
		Arrays.sort(toBeSorted);
		avg = toBeSorted[a.size() / 2];
		return avg;
	}

	// The bucket stores references to the records, so we need to get the record
	// to compare them to redistribute the records in new buckets after
	// resizing occurs
	public ArrayList<String> loadAllValuesInBucket(Bucket b) {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> valInBucket = b.values;
		for (String string : valInBucket) {
			String[] pos = string.split(",");
			String tableName = pos[0];
			String pageNumber = pos[1];
			String recordNumber = pos[2];
			// load The page to get the record and add it to the result
			Page p = this.table.pages.get(Integer.parseInt(pageNumber));
			String[] r = p.content[Integer.parseInt(recordNumber)];
			String r1 = "";
			for (int i = 0; i < r.length; i++) {
				r1 += r[i] + ",";
			}
			r1 = r1.substring(0, r1.length() - 1);
			result.add(r1);
		}
		return result;
	}

	public ArrayList<String> get(String k1, String k2) {
		ArrayList<String> result = new ArrayList<String>();
		if (root.key == null) {
			return result;
		} else {
			int level = 1;
			Node current = root;
			int cmp = 0;
			Node parent = null;
			while (current != null) {
				if (level % 2 == 1) {
					cmp = k1.compareTo(current.key);
				} else {
					cmp = k2.compareTo(current.key);
				}
				parent = current;
				if (cmp >= 0) {
					current = current.left;
				} else {
					current = current.right;
				}
				level = 1 - level;
			}
			// check on number of values per bucket

			if (cmp >= 0) {
				if (parent.leftBucket == null) {
					parent.leftBucket.values = new ArrayList<String>();
				}
				return parent.leftBucket.values;
			} else {
				if (parent.rightBucket == null) {
					parent.rightBucket.values = new ArrayList<String>();
				}
				return parent.rightBucket.values;
			}
		}
	}

	public static void main(String[] args) {
		KDTree k = new KDTree(3);
		k.put("mariam", "25", "Employee,2,4");
		k.put("sara", "21", "Employee,4,6");
		k.put("ssara", "22", "Employee,4,5");
		k.put("sssara", "22", "Employee,4,5");
		k.put("alaa", "24", "Employee,2,5");
		k.columnNumberOfKey1 = 3;
		k.columnNumberOfKey2 = 4;
		// ArrayList<String> tempRecords = new ArrayList<String>();
		// tempRecords.add("1,0,1,aariam,25");
		// tempRecords.add("1,0,2,sara,41");
		// tempRecords.add("1,0,3,aager,35");
		// tempRecords.add("1,0,4,yara,21");
		// System.out.println(k.getAvg(tempRecords, 2));
		System.out.println(k.root.rightBucket.values.size());
		System.out.println(k.root.left.leftBucket.values.size());
		System.out.println(k.root.left.leftBucket.values.size());
	}

}