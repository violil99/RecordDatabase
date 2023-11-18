import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;

public class Records{

    //An array of all records inserted from the records.txt file and the user
    Record[] records;

    //How many records are in the array, or the index of the last record -1
    int totalRecords;

    //txt file to get the records from
    File file;

    //size of the array (not necessarily full of records)
    int size;

    //The root of a binary search tree sorted by SIN number
    Node BSTRoot;

    //This would be more utilized in an implementation which allows the user to sort by any field
    //holds the index of the field the array is sorted by
    int sortedBy;


    public Records(int size, String recordFile){
        this.size = size; 
        this.records = new Record[size];
        this.totalRecords = 0;
        String data;
        BSTRoot = null;

        //opens the scanner and reads the file, adding applicable records to database
        try {
            this.file = new File(recordFile);
            Scanner scanner = new Scanner(file);

            // not reading the first line of file
            scanner.nextLine();

            //Add isFull code 
            while (scanner.hasNextLine()) {
                data = scanner.nextLine();

                //adds the record to unsorted array
                addRecord(data);
        
            }
            scanner.close();
          } catch (FileNotFoundException e) {
            System.out.println("No records file found");
        }

        //constructs a binary search tree based on the SIN numbers
        BSTRoot = constructBST(records, 0, totalRecords-1);

        //sorts the data based on ID
        heapSort("EmployeeID");
    }


    //Doubles the array size, copying the previous data into the new array
    public void extendArray(){
        size = size*2;
        Record[] biggerRecords = new Record[size];
        for (int i=0; i<totalRecords;i++){
            biggerRecords[i] = records[i];
        }

        records = biggerRecords;

    }

    //prints every record in records
    public void printRecords(){
        for (int i=0;i<totalRecords;i++){
            printFields(records[i]);
        }
    }

    //adds a record, unsorted. resizing the array if capacity is hit
    public void addRecord(String data){
        Record record;
        record = stringToRecord(data);
        if(record==null){
            return;
        }
        records[totalRecords]=record;
        totalRecords++;  

        if(size==totalRecords){
            extendArray();
        }   

    }


    //inserts a record into the records array at a given index
    public void insertToList(Record record, int index){
        totalRecords++;
        if(size<=totalRecords){
            extendArray();
        }
        for (int i=totalRecords;i>index;i--){
            records[i]=records[i-1];
        }
        records[index] = record;
    }

    //adds a record to an already sorted array and BST, maintaining the sort
    public void addRecordToSorted(Record record){

        if(record==null){
            return;
        }

        //The value in the record's field by which the database is sorted. 
        //if sortedBy=0, this would be the record's employeeID
        String insertRec = record.recordArr[sortedBy];

        //Find the index at which this record should be sorted
        int index = getArrayIndex(insertRec);

        //inserts to the sorted list, moving elements up to make space for the new element
        insertToList(record, index);

        //inserts into BST 
        BSTRoot = insertToBST(record, BSTRoot);


    }


    //the same as the method above, only this takes a string of data and creates a record with that string
    public void addRecordToSorted(String data){
        Record record = stringToRecord(data);
        if(record==null){
            return;
        }
        String insertRec = record.recordArr[sortedBy];

        int index = getArrayIndex(insertRec);

        insertToList(record, index);
        BSTRoot = insertToBST(record, BSTRoot);

    }

    //returns the index at which a given record should be inserted
    public int getArrayIndex(String insertRec){
        String arrRec;

        int start = 0;
        int end = totalRecords-1;
        int mid = (start+end)/2;

        //does a binary search (by sortedBy field) to find the right spot for a record
        while(start<=end){
            arrRec = records[mid].recordArr[sortedBy];
            if(insertRec.compareTo(arrRec)>0){
                start = mid+1;
                mid = (start+end)/2;
            }else{
                if(insertRec.compareTo(arrRec)<0){
                    end = mid-1;
                    mid=(start+end)/2;
                }
                else{
                    //matching record was found, insert at place of match
                    return mid;
                }
            }
        }

        //if mid=0 and the new record is smaller than that already at index 0
        if(mid==0 && insertRec.compareTo(records[mid].recordArr[sortedBy])<0){
            return mid;
        }
        //if the above condition is not met, the record should be inserted at mid+1
        return mid+1;
    }

    //inserts new record node to the BST based on second key
    public Node insertToBST(Record record, Node root){ 

        //recurses until an empty node is found 
        if (root==null){
            //sets emplty node to be the new record
            root = new Node(record);
            return root;
        }

        //initializes the fields to be compared
        String toInsert = record.recordArr[1];
        String rootVal = root.record.recordArr[1];

        //moves to the left if value of insertion is smaller than value of current node
        if(toInsert.compareTo(rootVal)<0){
            root.left = insertToBST(record, root.left);
        //moves to the right otherwise
        }else{
            root.right = insertToBST(record, root.right);
            
        } 
        return root;
    }


    //finds a record in BST based on a given field. Only returns the first item with a given value,
    //so not suitable for non-unique fields in its current form. Couls be easily implemented to deal 
    //duplicates by returning an array of matching right children after the matching record is found
    public Node findRecord(int field, String fieldValue){
        Node currNode = BSTRoot;


        while(currNode!=null){
            String currVal = currNode.record.recordArr[field];

            //found match
            if(currVal.equals(fieldValue)){
                return currNode;
            }
            //record<currNode
            if(currVal.compareTo(fieldValue)>0){
                currNode = currNode.left;
            }
            //record>=currNode
            else{
                currNode = currNode.right;
            }
        //No match was found
        }return null;
    }

    //Searches for and prints a record based on the ID or SIN
    public void printRecord(String field, String fieldValue){

        //search by ID in records array
        if(field=="EmployeeID"){
            //do binary search
            int start = 0;
            int end = totalRecords-1;
            int mid = (start+end)/2;
            while(start<=end){
                if(fieldValue.compareTo(records[mid].EmployeeID)>0){
                    start=mid+1;
                    mid = (start+end)/2;
                }else{
                    if(fieldValue.compareTo(records[mid].EmployeeID)<0){
                        end=mid-1;
                        mid = (start+end)/2;
                    }else{
                        //prints found record
                        System.out.println("Record found: ");
                        printFields(records[mid]);
                        return;
                    }
                }

            //no record was found in the array
            }System.out.println("record not found");
            return;
        }

        //Search by SIN using BST
        Node foundRecord = findRecord(1, fieldValue);

        //no record was found
        if(foundRecord==null){
            System.out.println("record not found");
        }else{
            //prints found record
            System.out.println("Record found: ");
            printFields(foundRecord.record);
        }
    }

    //finds and deletes a given record, returning 1 when the record was
    //found/deleted and 0 if the record was not found
    public int deleteRecord(Record record){

        //invalid record given
        if(record==null){

            //returns 0 to communicate no record was found
            return 0;
        }

        //does a binary search to find record
        String insertRec = record.recordArr[sortedBy];
        String arrRec;
        int start = 0;
        int end = totalRecords-1;
        int mid = (start+end)/2;
        int recordIndex = totalRecords+1;

        while(start<=end){// && end != 0 && start != totalRecords-1){
            arrRec = records[mid].recordArr[sortedBy];
            if(insertRec.compareTo(arrRec)>0){
                start = mid+1;
                mid = (start+end)/2;
            }else{
                if(insertRec.compareTo(arrRec)<0){
                    end = mid-1;
                    mid=(start+end)/2;
                }
                else{
                    recordIndex = mid;
                    break;
                }
            }
           
        }

        //if record index was not initialized, no record was found
        if(recordIndex>totalRecords){
            System.out.println("Cannot delete record. Does not exist");
            //returns 0 to communicate no record was found/deleted
            return 0;

        //deletes record and decrements totalRecords
        }else{
            for(int i = recordIndex; i<totalRecords;i++){
                records[i]=records[i+1];
            }
            totalRecords--;
        }

        //removes the record for BST
        BSTRoot = removeFromBST(BSTRoot, record);

        //returns 1 when record was found/deleted
        return 1;
        
    }

    //find and removes a given record from the BST, maintining BST property
    public Node removeFromBST(Node root, Record record){

        //stops recursing at the empty node
        if(root==null){
            return root;
        }

        String rootVal = root.record.recordArr[1];
        String recordVal = record.recordArr[1];

        //record is in the left subtree
        if(rootVal.compareTo(recordVal)>0){
            root.left = removeFromBST(root.left, record);
        }else{

            //record is in the right subtree
            if(rootVal.compareTo(recordVal)<0){
                root.right = removeFromBST(root.right, record);
            }else{
                //record was found

                //Record has no children. Remove record
                if (root.left==null && root.right==null){
                    return null;
                }else{

                    //record has one child. Replace record with child
                    if(root.left==null || root.right==null){
                        if(root.left==null){
                            Node temp = root.right;
                            root= null;
                            return temp;
                        }else{
                            Node temp = root.left;
                            root = null;
                            return temp;
                        }

                    //record has two children
                    }else{
                        Node parent = null;
                        Node child = root.right;
        
                        //find smallest in right subtree
                        while(child.left!=null){
                            parent = child;
                            child = child.left;
                        }
                        
                        //connects the descendents of smallest in right subtree to the rest of the tree
                        if(parent!=null){
                            parent.left = child.right;
                        }else{
                            root.right = child.right;
                        }
                        
                        //replaces root with child and deletes child
                        root.record = child.record;
                        child=null;
                    }
                    
                }

            }
        }

        //return updated root
        return root;
    }

    //finds record to update, deletes it, and adds new record with new data
    public void updateRecord(Record oldRecord, Record newRecord){

        //new record was invalid
        if(newRecord==null){
            return;
        }

        //old record was not found during deletion
        if(deleteRecord(oldRecord)==0){
            System.out.println("Record to update does not exist");
        }else{
            //if old record was deleted, adds the new record to array and BST
            addRecordToSorted(newRecord);
        }
    }



    //takes a string and converts it inro a record. if the number of fields is invalid, returns null
    public static Record stringToRecord(String data){
        Scanner scanner = new Scanner(data).useDelimiter(",\\s*");
        try{
            Record record = new Record(scanner.next(),  
            scanner.next(), 
            scanner.next(), 
            scanner.next(),
            scanner.next(), 
            scanner.next());
        return record;
        }catch(NoSuchElementException e){
            System.out.println("Uh oh, you didn't put the right number of fields");
            return null;
        }

    }

    //swaps records at index i and j in the records array
    public void swap(int i, int j){
        Record temp = records[i];
        records[i]=records[j];
        records[j]=temp;
    }

    //heapifies based on given field index
    public void heapify(int field, int n, int i){
		int largest = i; // Initialize largest as root
		int l = 2 * i + 1; // left = 2*i + 1
		int r = 2 * i + 2; // right = 2*i + 2

		// If left child is smaller than root
		if (l < n && records[l].recordArr[field].compareTo(records[largest].recordArr[field])>0)
			largest = l;

		// If right child is smaller than largest so far
		if (r < n && records[r].recordArr[field].compareTo(records[largest].recordArr[field])>0)
			largest = r;

		// If largest is not root
		if (largest != i) {
			//int temp = arr[i];
			//arr[i] = arr[largest];
			//arr[largest] = temp;
            swap(i,largest);

			// Recursively heapify the affected sub-tree
			heapify(field, n, largest);
		}
	}

    //takes the name of a field and returns its corresponding index
    //defaults to ID/0
    public int fieldIndex(String field){
        switch(field){
            case("EmployeeID"):
                return 0;
            case("SIN"):
                return 1;
            case("Name"):
                return 2;
            case("Department"):
                return 3;
            case("Address"):
                return 4;
            case("Salary"):
                return 5;
            default: return 0;
        }
    }

    //heapsorts based on field index
    public void heapSort(int fieldCode){
        int n = totalRecords;

		// Build heap
		for (int i = n / 2 - 1; i >= 0; i--)
			heapify(fieldCode, n, i);

		// One by one extract an element from heap
		for (int i = n - 1; i >= 0; i--) {
			
			// Move current root to end
			swap(0,i);

			// call min heapify on the reduced heap
			heapify(fieldCode, i, 0);
		}
        this.sortedBy = fieldCode;
	}

    //the same as the above method, but takes a string representing the field
    public void heapSort(String field){
        int n = totalRecords;
        int fieldCode = fieldIndex(field);

		// Build heap
		for (int i = n / 2 - 1; i >= 0; i--)
			heapify(fieldCode, n, i);

		// One by one extract an element from heap
		for (int i = n - 1; i >= 0; i--) {
			
			// Move current root to end
			swap(0,i);

			// call min heapify on the reduced heap
			heapify(fieldCode, i, 0);
		}
        this.sortedBy = fieldCode;
	}

    //prints the BST in order
    public void printInOrder(Node root){
        if(root==null){
            return;
        }
        printInOrder(root.left);
        printFields(root.record);
        printInOrder(root.right);
    }

    //constructs a BST from a sorted array. field sorted by must be unique.
    //could be easily changed to accommodate repeating values.
    public Node constructBST(Record[] records, int start, int end){

        //heapsorts the array of records based on SIN on the first pass
        if(start==0 && end ==totalRecords-1){
            heapSort(1);
        }
        

        if (start>end){
            return null;
        }
        //find median
        //create Node with median
        //for both sides around median, do the same until subarray is empty
        int median = (start+end)/2;
        Node root = insertToBST(records[median], BSTRoot);
        root.left = (constructBST(records, start, median-1));
        root.right = (constructBST(records, median+1, end));
        return root;
    }


    //prints the fields of a given record
    public static void printFields(Record record) {
        if (record == null){
            System.out.println("This record does not exist\n");
            return;
        }
        System.out.println("EmployeeID: " + record.EmployeeID);
        System.out.println("SIN: " + record.SIN);
        System.out.println("Name: " + record.Name);
        System.out.println("Department: " + record.Department);
        System.out.println("Address: " + record.Address);
        System.out.println("Salary: " + record.Salary);
        System.out.println();
    }


    //runs interactive system, allowing the user to print, add, remove, find, and update records
    public static void runSystem(){
        Scanner scanner = new Scanner(System.in);
        Records employees = new Records(50, "records.txt");



        String input;
        String input2;

        while(true){

            System.out.println("\nPlease input the number corresponding to one of the following options:");
            System.out.println("1) Print records");
            System.out.println("2) Add a record");
            System.out.println("3) Delete a record");
            System.out.println("4) Find a specific record");
            System.out.println("5) Update a record");
            System.out.println("Type any other character to quit the program");
            input = scanner.next();
            scanner.nextLine();



            switch(input){
                case "1":
                    System.out.println("Select 1 to print by SIN number or any other key to print by EmployeeID");
                    input2 = scanner.next();
                    if(input2.endsWith("1")){
                        employees.printInOrder(employees.BSTRoot);
                    }else{
                        employees.printRecords();
                    }
                    break;
                case"2":
                    System.out.println("Input your record, comma separated, in the following order: ");
                    System.out.println("Employee-ID, SIN, Name, Department, Address, Salary");
                    input2=scanner.nextLine();
                    employees.addRecordToSorted(input2);
                    break;
                case "3":
                    System.out.println("Input the record you would like to delete, comma separated, in the following order: ");
                    System.out.println("Employee-ID, SIN, Name, Department, Address, Salary");
                    input2 = scanner.nextLine();
                    Record record = employees.stringToRecord(input2);
                    System.out.println();
                    employees.deleteRecord(record);
                    break;
                case"4":
                    System.out.println("Select 1 to search by SIN number or any other key to search by EmployeeID");
                    input2 = scanner.next();
                    if(input2.equals("1")){
                        System.out.println("Input the SIN number you are searching for: ");
                        String input3 = scanner.next();
                        System.out.println();
                        employees.printRecord("SIN",input3.strip());
                    }else{
                        System.out.println("Input the EmployeeID you are searching for: ");
                        String input3 = scanner.next();
                        System.out.println();
                        employees.printRecord("EmployeeID", input3.strip());
                    }
                    break;

                case "5":
                    System.out.println("Input the SIN number of the record you would like to update: ");
                    input2 = scanner.nextLine();
                    Node node = employees.findRecord(1, input2);
                    if(node==null){
                        System.out.println("Uh oh, there isn't a record with that sin number");
                        break;
                    }

                    Record r1 = node.record;
                    
                    System.out.println("Input the updated record, comma separated, in the following order: ");
                    System.out.println("Employee-ID, SIN, Name, Department, Address, Salary");
                    String input3 = scanner.nextLine();
                    Record r2 = employees.stringToRecord(input3);
                    employees.updateRecord(r1,r2);
                    break;

                default:
                System.out.println("GoodBye!");
                return;                    

            }
        }
        

        

    }

    public static void main(String[] args){
        runSystem();
    }

}

//Node class for BST
class Node{
    Record record;
    Node left;
    Node right;

    //constructs a BST
    public Node(Record record){
        this.record = record;
    }
    

}

class Record{
    String EmployeeID;
    String SIN;
    String Name; 
    String Department;
    String Address;
    String Salary;

    //This variable helps sort by field
    String[] recordArr;

    //constructs a record
    public Record(String EmployeeID,String SIN,String Name,String Department,String Address,String Salary){
        this.EmployeeID=EmployeeID.strip();
        this.SIN=SIN.strip();
        this.Name=Name.strip();
        this.Department=Department.strip();
        this.Address = Address.strip();
        this.Salary=Salary.strip();
        this.recordArr = new String[]{EmployeeID,SIN,Name,Department,Address,Salary};

    }

}
