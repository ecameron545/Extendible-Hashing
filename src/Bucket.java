
public class Bucket {

	public int amountOfRecords; // amount of records in this bucket
	public Record[] records; // array of all the records in this bucket
	public Bucket parent, left, right; // parent, left, and right bucket
	public int localDepth; // just read the name
	public boolean full; // this bucket is full
	public String code; // the binary code of the bucket. unique identifier

	public Bucket(int localDepth, String code, Bucket parent) {
		amountOfRecords = 0;
		records = new Record[2];
		full = false;
		this.localDepth = localDepth;
		left = null;
		right = null;
		this.code = code;
		this.parent = parent;
	}

	public String find(int id) {

		// if bucket is not full we found right bucket
		if (!full) {
			// find right record
			for (int i = 0; i < amountOfRecords; i++)
				if (records[i].id == id)
					return records[i].name;

			// otherwise recurse to correct bucket
		} else {
			int mask = (id % 32) >>> (localDepth);
			if ((mask & 1) == 0)
				return left.find(id);
			else
				return right.find(id);
		}

		return id + " not found";
	}

	public void insert(Record rec) {

		// if the bucket is full
		if (full == true || amountOfRecords == 2) {

			// if the bucket has no children, create children and rehash
			if (left == null) {
				full = true;
				amountOfRecords = 0;
				Main.directory.clear();
				Main.directorySize *= 2;
				left = new Bucket(localDepth, "0" + code, this);
				right = new Bucket(localDepth, "1" + code, this);
				++Main.globalDepth;
				++Main.amountOfBuckets;

				// rehash to new buckets
				for (int i = 0; i < records.length; i++) {

					// hash function shifted over by the local depth
					int id = (records[i].id % 32) >>> (localDepth);

					// put in left bucket
					if ((1 & id) == 0) {
						if (left.records[0] == null) {
							Main.directory.add("0" + code);
							left.localDepth = left.localDepth + 1;
						}
						left.insert(records[i]);

						// put in right bucket
					} else {
						if (right.records[0] == null) {
							Main.directory.add("1" + code);
							right.localDepth = right.localDepth + 1;
						}

						right.insert(records[i]);
					}
					records[i] = null;
				}

				// *** add new record after rehashing
				int id = (rec.id % 32) >>> (localDepth);

				// put it in left bucket
				if ((1 & id) == 0)
					left.insert(rec);

				// put it in right bucket
				else
					right.insert(rec);

				// bucket has children so put in the child bucket
			} else {

				int id = (rec.id % 32) >>> (localDepth);

				if ((1 & id) == 0)
					left.insert(rec);
				else
					right.insert(rec);
			}

		} else {

			// this bucket has room so add to this bucket
			if (!Main.directory.contains(code))
				Main.directory.add(code);
			records[amountOfRecords++] = rec;
		}
	}

	public void delete(int id, String path) {

		// if search for the right bucket
		if (!full) {

			// go through records in the right bucket
			for (int i = 0; i < amountOfRecords; i++) {

				// if this record is the right one to delete
				if (records[i].id == id) {

					// one record left so need to delete bucket
					if (amountOfRecords == 1) {
						// if the path we took was left, delete left bucket.
						// same for right
						if (path.equals("left")) {
							--Main.amountOfBuckets;
							Main.directory.remove(code);
							parent.left = null;
							
							// if both buckets WILL be null, reset the instance variables
							// on the parent bucket
							if (parent.right == null) {
								reset(parent);
								if (Main.directory.size() == 0)
									Main.directorySize /= 2;
							}

							// same code as above for right child bucket
						} else if (path.equals("right")) {
							--Main.amountOfBuckets;
							Main.directory.remove(code);

							parent.right = null;
							if (parent.left == null) {
								reset(parent);
								if (Main.directory.size() == 0)
									Main.directorySize /= 2;
							}

							// delete if global depth = 0
						} else if (path.equals("")) {
							--Main.amountOfBuckets;
							records[0] = null;
							reset(this);
						}

						// record has another with it so delete the record
					} else {

						if (i == 0) {
							records[i] = records[i + 1];
							records[i + 1] = null;
						} else
							records[i] = null;
						--amountOfRecords;
					}

				} else if (i == (amountOfRecords - 1))
					Main.amountOfRecords++;
			}
			return;

			
			// recurse to right bucket
		} else {
			int mask = (id % 32) >>> (localDepth);

			if ((mask & 1) == 0) {
				left.delete(id, "left");
			} else {
				right.delete(id, "right");
			}

		}

	}

	// reset a bucket
	public void reset(Bucket bucket) {
		bucket.amountOfRecords = 0;
		bucket.full = false;
	}

}
