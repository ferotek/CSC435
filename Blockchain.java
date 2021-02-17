/* CSC435 Spring-2019

1. Name:Amy Aumpansub (Tested and Compiled on Mac OS with java 1.8)
   Due Date: May 29th, 2019

2. This file uses java version [1.8.0_181] and works with Mac OS (I didn't test on Window)

3. Instructions: javac Blockchain.java	

4. Commands for execution:
		
		a. Open 3 terminal console windows 
		b. My program is implemented in Mac OS, so I compile this .java file and run it by these commands:
				 java Blockchain 0(for process0)	
		 		 java Blockchain 1(for process1)	
		 		 java Blockchain 2(for process2)
		 		 
		c. After the process 2 starts, and get all 3 public keys you can go enter the Mode C, R, V, V hash, L
		d. Command: "C" is for a vertification block credit for each process
					"R" is for reading .txt file to the program and send to worker thread to process and add data
					"V" is to verify each block with 256 shaString
					"V hash" to verify block with hash
					"L" is to print the list of data on ledger to the console

5. Files needed: Blockchain.java, BlockInput.txt
 		
6. Notes: Some part of my codes are modified from sample codes provided in BlockChain instruction by Prof. Clark Elliott:
 	      Blockchain Version I, Input Utility program, Sample Work program, and Process Coordination program by Prof. Elliott
*/

import java.util.*;//import the util java library
import java.io.*;//to read-in the command input and write to buffer and file
import java.net.*;
import java.security.*;//import the security func for geting and verifying public and private keys
import java.security.spec.X509EncodedKeySpec;// to get key spec for creating public keys
import java.text.SimpleDateFormat;//to format the timestamp
import javax.xml.bind.JAXBContext;//use this library to process the xaml
import javax.xml.bind.JAXBException;//throw error if fails to convert object 
import javax.xml.bind.Marshaller;//convert obj to xml
import javax.xml.bind.Unmarshaller;//convert xml to obj
import javax.xml.bind.annotation.XmlElement;//to get and set the field in xml file
import javax.xml.bind.annotation.XmlRootElement;//root is BlockRecord


/**************** All codes in class "BlockRecord" are modified from BlockInputE.java ********************/
/*************************************By Prof. Clark Elloit **********************************************/

@XmlRootElement
class BlockRecord{
	/* PURPOSE: Serve as a block that contains all fields that will be converted to the xml file
	 * 			The header field starts with A, F is the patient's info, G is for treatment
	 * 			The header includes block number, process id, datahash, hash of previous block, etc.
	 * 			The body field includes name, ssn, birthday, symtomp, and treatment of patients
	 * 			After we add the new data, the BCserver will process data and add the block to the blockchain
	 * 			Each block except the first initial block will contain all 20 fields as following:
	 */
	String SHA256String;//a 256-bit string 
	String SignedSHA256;//signed using encrption and private key
	String BlockID;//block id is created from UUID
	String SignedBlockID;//signed with private key
	String VerifyPID;//process# that verify block
	String CreatePID;//process# that create data
	String CreateTime;//time that data added
	String VerifyTime;//time that data verifiled 
	String AProDataHash;//this hash of process created
	String ABlockPreviousHash;//hash of the previous block
	String ASignedDataHash;//signed with private key for check and audit
	String SeedString;//random string number
	String ABlockNum;//block number in the chain
	String Fname;//first-name 
	String Lname;//last-name
	String SSNum;//social security num
	String DOB;//birthdate
	String Diag;//symptom
	String Treat;//treament
	String Rx;//medicine
	
	/***************** BlockInputE.java By Prof. Clark Elloit ****************************/
	/* This section is provided by Prof. Elloit, I add methods to add more fields to the block
	 * PURPOSE: To set and get the elements for each  blocks. 
	 * 			These methods will be used in BCserver and BCWorker, UnvBCServer class
	 * 			Then the fields will be processed using jabx Marshaller and UnMarshller to convert it between object and xml
	 */
	
	public String getABlockNum() {return ABlockNum;}//Get block number in the chain
	@XmlElement
	public void setABlockNum(String abn){this.ABlockNum = abn;}//Set block number in the chain

	public String getASHA256String() {return SHA256String;}//Get a 256-bit string 
	@XmlElement
	public void setASHA256String(String shs){this.SHA256String = shs;}//Set a 256-bit string 

	public String getASignedSHA256() {return SignedSHA256;}//Get a signed 256-bit string 
	@XmlElement
	public void setASignedSHA256(String ssh){this.SignedSHA256 = ssh;}//set a signed 256-bit string 

	public String getACreatePID() {return CreatePID;}//Get process# that create data
	@XmlElement
	public void setACreatePID(String cip){this.CreatePID = cip;}//Set process# that create data

	public String getAVerifyPID() {return VerifyPID;}//Get process# that verify block
	@XmlElement
	public void setAVerifyPID(String cid){this.VerifyPID = cid;}//Set process# that verify block
	
	public String getAProDataHash() {return AProDataHash;}//Get this hash of process created
	@XmlElement
	public void setAProDataHash(String vvv) {this.AProDataHash = vvv;}//Set this hash of process created
	
	public String getABlockPreviousHash() {return ABlockPreviousHash;}//Get hash of the previous block
	@XmlElement
	public void setABlockPreviousHash(String pvv) {this.ABlockPreviousHash = pvv;}//Set hash of the previous block

	public String getASignedDataHash() {return ASignedDataHash;}//Get Data hash signed with private key
	@XmlElement
	public void setASignedDataHash(String sda) {this.ASignedDataHash = sda;}//Set Data hash signed with private key

	public String getASeedString() {return SeedString;}//Get random string number
	@XmlElement
	public void setASeedString(String rss) {this.SeedString = rss;}//Set random string number

	public String getABlockID() {return BlockID;}//Get block id is created from UUID
	@XmlElement
	public void setABlockID(String bid){this.BlockID = bid;}//Set block id is created from UUID

	public String getASignedBlockID() {return SignedBlockID;}//Get signed BlockID with private key
	@XmlElement
	public void setASignedBlockID(String sid){this.SignedBlockID = sid;}//Set signed BlockID with private key

	public String getFSSNum() {return SSNum;}//Get SSN
	@XmlElement
	public void setFSSNum(String ssf){this.SSNum = ssf;}//Set SSN

	public String getFFname() {return Fname;}//Get first-name 
	@XmlElement
	public void setFFname(String dna){this.Fname = dna;}//Set first-name 

	public String getFLname() {return Lname;}//Get last-name 
	@XmlElement
	public void setFLname(String fna){this.Lname = fna;}//Set last-name 

	public String getFDOB() {return DOB;}//Get birthdate
	@XmlElement
	public void setFDOB(String dob){this.DOB = dob;}//Set birthdate

	public String getGDiag() {return Diag;}//Get dianogsys
	@XmlElement
	public void setGDiag(String dag){this.Diag = dag;}//Set dianogsys

	public String getGTreat() {return Treat;}//Get treatment
	@XmlElement
	public void setGTreat(String dtr){this.Treat = dtr;}//Set treatment

	public String getGRx() {return Rx;}//Get drugs
	@XmlElement
	public void setGRx(String drr){this.Rx = drr;}//set drugs

	public String getACreateTime() {return CreateTime;}//Get time that data created
	@XmlElement
	public void setACreateTime(String ctt) {this.CreateTime = ctt;}//Set time that data created 

	public String getAVerifyTime() {return VerifyTime;}//Get time that data verifiled 
	@XmlElement
	public void setAVerifyTime(String ctv) {this.VerifyTime = ctv;}//Set time that data verifiled 

	
}


class BCServer implements Runnable {
	
	/* This class is modified from Webserver hw and BlockChain Sample by Prof. Elloit, I add several methods to the class
	 * PURPOSE: To Serve as Block Chain Server that will call unverifiedBC class to preprocess the input text record
	 * 	        Then, the UnSgnBCServer will call BC Worker class to verify the record before adding the new block to the chain
	 * 			This BCServer will have main roles as following:
	 *			1. It connects with the different base Port numbers for process1, process2, and process3
	 *			2. It uses the datagramsocket to read and write the data from the thread and from the obj of UnSgnBCServer class
	 * 			3. It creates the first blockchain with empty ledger
	 * 			4. It takes the command inputs: C, L, V from console, and process the request to credit, list the elements of ledger, verifiy each bloack
	 * 			5. Then, it and return the output from those methods to the main program in BlockChain class to print the result to console
	 */

	DatagramSocket mySockFD = null;//use this sock to get and send the packets (ref. java.net)
	UnSgnBCServer unSgnServ;//obj from UnSgnBCServer class
	LinkedList<BlockRecord> blockChain;//this is our BC that holds each block data
	ArrayList<String> blockChainBlockIds;//use arrlist to store the blockID in the chain
	int proID;//the process number 0,1,2
	int sizeBC;//the size of this BC
	int bcPort;//the base port number 
	int updPort;//the port number for update BC
	int bSize = 200000;//the buffer size for send and get datapacket

	public BCServer(int bcPort, UnSgnBCServer unSgnServ, int prID, int updPort)  throws SocketException {
		/* PURPOSE: Serve as a constructor for server to connect with 3 processes to get and send data using UDP 
		 * 			Contain eight members. The main program in BC class will create a new BCServer object by
		 * 			passing 4 args
		 */

		this.mySockFD = new DatagramSocket(bcPort);//connect with 3 proCesses using 3 pot numbers
		this.unSgnServ = unSgnServ;//obj for unSgnServ
		this.blockChain = new LinkedList<BlockRecord>();//BC that holds each block data
		this.blockChainBlockIds = new ArrayList<String>();//the blockID Arrlist in the chain
		this.proID = prID;//ProcessID
		this.bcPort = bcPort;//Base port from main program
		this.updPort = updPort;//port for update BC
	}

	public void run() {
		/* PURPOSE: This run method will run the thread to start getting and sending the data via packet
		 * 			If the process id which runs the thread is 0 , it will create a new xml file on your directory
		 * 			We use BufferandFilewriter to write new XML file from Thread of process 0
		 * 
		 */
		byte[] inBuf = new byte[bSize];
		DatagramPacket reBuf = new DatagramPacket(inBuf, bSize);//getting and sending the data via Socketpacket
		while (true) {//keep looping to update block chain and to write new XML file for process#0
			try {
				this.mySockFD.receive(reBuf);//get the data via packetsocket
			} 
			catch (IOException exr2) {
				System.out.println(exr2);
			}
			int pLe = reBuf.getLength();
			String ndat = new String(inBuf, 0, pLe);
			this.linkMyBC(ndat);//update datablock on the chain if the process is 1,2
			if (this.proID != 0)//if process is 1,2 we lopp again to update BC
				continue;
			else if (this.proID == 0) {// to write new XML Ledger file from process#0's Thread
				try {   
					BufferedWriter wrtBuf = new BufferedWriter(new FileWriter("BlockchainLedger.xml"));//new file will be created on your directory 
					wrtBuf.write(ndat);//write xml of block data to the file
					wrtBuf.flush();//flush buffer
					wrtBuf.close();//close after finishing writing the file
				}
				catch (IOException exr2) {System.out.println(exr2);}//print out if error occurred in creating and writing file
			}
		}
	}

	public ArrayList<String>listMyBC() {//Mode "L"
		/* PURPOSE: To process the "L" command from user's console
		 * 			It will get each block record from the chain and store it in ArrList
		 * 			The method lists the most recently added block first (last block with latest time)
		 * 			The it will add the string data of each block to the ArrList and return it to main program
		 */

		int BClen = this.blockChain.size();//get the total num of record in this Bchain
		BlockRecord [] brArr = new BlockRecord [BClen];//array thau holds Brecords
		ArrayList<String> listArr = new ArrayList<>();//hold the string of each block data to print out on console
		this.blockChain.toArray(brArr);//convert the Bchain to array
		int i = (brArr.length-1);//counter
		while (i > 0) {//loop for whole Bchain elements
			BlockRecord entry = brArr[i];//store each block data
			String DL = new String ("#" + entry.getABlockNum() + " " + entry.getACreateTime() + " " + entry.getFFname() + " " + entry.getFLname() + " " + 
					entry.getFDOB() + " " + entry.getFSSNum() + " " + entry.getGDiag() + " " + entry.getGRx());
			listArr.add(DL);//create string containing each block's number, time, name, dob, ssn,....
			i--;///decrement ourCounter
		}
		return listArr;//return the arrlist to the main program
	}

	public String creditMyBC() {//Mode "C"
		/* PURPOSE: To process the "C" command from user's console
		 * 			It will match the verified processID for each block added to BChain
		 * 			Then, increment the counter for each process
		 * 			The it will make the string data of process's credit and return it to main program
		 */
		int Pzero, Pone, Ptwo;//credit counter for 3 pros
		Pzero = Pone = Ptwo = 0;//set to 0
		int pCre = 1;//for loop counter
		sizeBC = this.blockChain.size();
		while (pCre < sizeBC ) {//loop for whole Bchain
			BlockRecord curB = this.blockChain.get(pCre);//get the blockRecord
			String verP = curB.getAVerifyPID();//get the PID that verified this block
			if (verP.equals("PID0")) Pzero = Pzero + 1;//incre Processcounter by 1 if the process verified this block
			if (verP.equals("PID1")) Pone = Pone + 1;
			if (verP.equals("PID2")) Ptwo = Ptwo +1;
			pCre++;
		}
		String out = new String ("Verify-Block-Credit: P0: " + Pzero + ", P1: " + Pone + ", P2: " + Ptwo);
		return out;//return the string to the main program
	}


	public String veriMyBC(boolean hash) {//Mode "V", "V hash"
		/* PURPOSE: To process the "V" , "V hash" command from user's console
		 * 			It will get each block record from the chain and store it in ArrList
		 * 			Them it will check if each block has been verified with SHA256 and has value
		 * 			The it will make the string data of verified result and return it to main program
		 */

		StringBuilder outS = new StringBuilder();
		int bcSize = this.blockChain.size();
		outS.append("Blocks 1-"+(bcSize-1) + " have been verified.\n");
		if (hash == true) {//True for V Hash, False for V
			for (int i = 1; i < bcSize; i++) {//Code from Blockchain Version I
				try {
					BlockRecord preBR = this.blockChain.get(i-1);//get the previous Brecord
					BlockRecord curBR = this.blockChain.get(i);//get the current Brecord
					BlockRecord verBR = curBR;
					StringWriter stW = new StringWriter();//initialize strwriter
					JAXBContext myJCon = JAXBContext.newInstance(BlockRecord.class);//declare new JAXC obj
					Marshaller myJMar = myJCon.createMarshaller();//declare and set Marshaller obj
					myJMar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//to format output
					myJMar.marshal(verBR, stW);//to convert our Brecord obj to XML format

					MessageDigest myMess = MessageDigest.getInstance("SHA-256");//to readin the messdata
					String myXml= stW.toString();//convert strwriter to string
					myMess.update((myXml + preBR.getASHA256String()).getBytes());//update with previousBlock data
					byte[] buffD = myMess.digest();//put it in buffer in bytes
					int buffD_len = buffD.length;//get len of buff
					StringBuffer buffS = new StringBuffer();//declare new str buffer
					for (int j = 0; j < buffD_len; j++) {//loop over the buffer
						buffS.append(Integer.toString((buffD[j] & 0xff) + 0x100, 16).substring(1));//add to string buffer
					}
					
					String SHA256String = buffS.toString();
					if (!verBR.getASHA256String().equals(SHA256String)){//check if the sha256 of current Bcord string matches
						String currBNum = curBR.getABlockNum();//if not match, we get the unmatched bRecord
						int nextBNum = Integer.parseInt(currBNum) + 1;//we find the next block number
						outS.append("Block  "+currBNum+"     invalid: SHA-256 hash does not match\n");//print put unmatched blockNum
						outS.append("Blocks "+nextBNum + "-" + (bcSize-1) +" following the previous invalid block\n");//print remaining blockNum
						break;
					}
				} catch (Exception e) {
					System.out.println(e);}//printout info for error
			}
		}
		String out = outS.toString();//we will return the output string to main program
		return out;
	}

	/***********BlockChainInputUtility.java By Prof. Clark Elloit***********/
	public BlockRecord makeMyBR(String inXml) {
		/* 	Codes provided from Prof. Elloit form BlockChainInput Utility program
		 * 	PURPOSE: To convert the received XMLdata to BlockRecord object using UnMarshaller		
		 */
		StringReader reader = new StringReader(inXml);//create new str reader to read and store the received xml record
		BlockRecord myBR = null;//declare and set BR
		JAXBContext myJCon = null;//declare and set JAXBobj
		Unmarshaller myJUnma = null;////declare and set UnmarshalerObj
		try {
			myJCon = JAXBContext.newInstance(BlockRecord.class);//create new obj
			myJUnma = myJCon.createUnmarshaller();//call create unmarshal 
			myBR = (BlockRecord) myJUnma.unmarshal(reader);//convert the xml to Blockrecord object 
		} catch (JAXBException e) {e.printStackTrace();}//printout the trace error
		return myBR;//return the Blockrecord obj
	}
	
	
	private String head = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";//store header of xmlFile
	
	/***********Blockchain sample program Version I By Prof. Clark Elloit***********/
	
	public void linkMyBC(String input)  {
		/* To create the string for new block and add it to the BChain
		 * It also checks the length of newly created BChain with the current BChain
		 * The bChain with longer length will be kept, another will not be used
		 * if the two Bchains have same size, we will compare the Block's ID and Time added
		 */
		String body = input.replace(head,"");//remove the Xml header
		body = body.replace("<CSC435_Block>","");//remove the blockheader
		body = body.replace("</CSC435_Block>","");//remove the blockender
		String [] mul =  body.split("\n\n");//split string and store it in arrayStr
		int mull = mul.length;
		LinkedList<BlockRecord> newBC = new LinkedList<>();//create new BChain
		ArrayList<String> newBIDArr = new ArrayList<>();//create New Bid array
		int im = 0;//counter
		String xM;//body
		String m;//each body content
		while (im < mull) {//loop until you reach the end of chain
			m = mul[im];
			im ++;//incre counter
			if (m == "" || m == null) continue;//skip the empty and null str
			else {
				xM = head + m;//add the header and body of XML
				BlockRecord myBRec = this.makeMyBR(xM);//set the returned obj to Brecord object
				if (myBRec != null) {//if it's not empty Brecord
					newBC.add(myBRec);//add to the new Bchain
					newBIDArr.add(myBRec.getABlockID());//store the new BID
				}
			}	
		}
		int curBClen = this.blockChain.size();//get total num of Brecords in current Bchain
		int newBClen = newBC.size();//get total num of Brecords in new Bchain

		if (curBClen < newBClen) {//if the new Bchain is larger, we will replace the current one with the new one
			this.blockChain = newBC;//set the new Bchain
			this.blockChainBlockIds = newBIDArr;//set the new BIDs array
			this.unSgnServ.updateBlockChain(this.blockChain , this.blockChainBlockIds);//pass it to UnsignServ class to verify and update the records to Bchain
		}

		else if (curBClen == newBClen){//if the new Bchain size is same as the current one, we will check 2 conditions
			int ibc = 1;
			int bcSize = this.blockChain.size();//get total num of Brecords in current Bchain
			while (ibc < bcSize)//loop for the whole Bchain
			{
				String curBID = this.blockChain.get(ibc).BlockID;//get the BID
				String newBID = newBC.get(ibc).BlockID;//get the BID
				String curTime = this.blockChain.get(ibc).CreateTime;//get the time 
				String newTime = newBC.get(ibc).CreateTime;//get the time 
				ibc++;

				if(!curBID.equals(newBID))	//if the BID of two Bchains are same, we stop looping 
					break;//exit the whole loop		
			}
		}}}

/***********Modifiled from Process Coordination and Blockchain input utilty Program By Prof. Elloit***********/

class UnSgnBCServer implements Runnable {
	/* PURPOSE: Serve as the server for unverified block record
	 * 			Connect with the port to send and get data via socket
	 * 			It runs the threads, generate key pair, and process the input.txt file into the BlockRecord format
	 * 			Then, it sends the Blockrecords to BC worker to verfily the blockRecord
	 */

	int[] UnvPorts = {4820, 4821, 4822};//use this port for unver class
	int bSize = 200000;//buffer write size
	int pid;//processID
    int uSleep;//to set thread to sleep a while
    DatagramSocket socket = null;//use this socket to send data and receive it 
    BCWorker blockVerifier;//call BCworker object
    KeyPair keyPair;//get KeyPair
    

    public UnSgnBCServer(int port, int pid) {//constructor have portNUmber and processID as input
        
        try {
            this.socket = new DatagramSocket(port);//generate the new socket to send data and receive it 
        } 
        catch (SocketException e) {}
        
        //This part is provided by prof. Clark on Process Coordination program
        KeyPairGenerator myKP = null;//declare new KeyPair obj
        SecureRandom KPseed = null;//declare new random seed obj
        try {
        	myKP = KeyPairGenerator.getInstance("RSA");//create KeyPair
        	KPseed = SecureRandom.getInstance("SHA1PRNG", "SUN");//Use rand seed
        } catch (Exception e) {}
        myKP.initialize(1024, KPseed);
        this.keyPair = myKP.generateKeyPair();
        this.pid = pid;
        this.blockVerifier = new BCWorker(this.socket, this.pid, this.keyPair); //Initialize the blockWorker for verification
        if (this.pid == 2) {//if process#2 starts, we will tickle all processes to generate the PubKey
            this.sendKeys("allKeys");    
        }
        this.uSleep =0;//put thread to sleep a while while waiting
    }
   

    
    public void updateBlockChain(LinkedList<BlockRecord> nBC, ArrayList<String> NBIDs){//for updating the Bchain
        this.blockVerifier.joinMyBC(nBC,NBIDs);//will add block and update the Bchain
    }
    
    public void sendKeys(String ks) {
    	//PURPOSE: To send the key to the specified port via datasocket
    	byte[] sk = ks.getBytes();//get byte size
    	int skN  = sk.length;
    	InetAddress pp_ip = null;//initialize to null
    	try {
    		pp_ip = InetAddress.getLocalHost();//local host&
    	} catch (UnknownHostException e) {}
    	int pii = 0;
    	int pp_port = 0;
    	while (pii < 3) {//loop through the port array
    		pp_port = UnvPorts[pii];//get the portNumber
    		pii++;
    		DatagramPacket outD = new DatagramPacket(sk, skN, pp_ip, pp_port);//send the key out via socket
    		try {
    			this.socket.send(outD);//send the data out
    		} catch (IOException e) {}
    	}
    }

    public void run() {
    	/* PURPOSE: To run the thread for the class to process the BlockRecord 
    	 * 			Pass the Brecord object to the BCWorker class for verification
    	 * 			Check the several conditions if it is muticast, we will start coordinating among three processes
    	 */
    	byte[] newB = new byte[bSize];//get the buff size
    	int newBL = newB.length;
    	DatagramPacket newP = new DatagramPacket(newB, newBL);//define the new data socket for sending and recieve data
    	new Thread(this.blockVerifier).start();//start the thread of block verifier to do the work
    	while (true) {//keep looping
    		try {      
    			this.socket.receive(newP);//received data via socket
    		} 
    		catch (IOException e) {} 

    		int newPL = newP.getLength();
    		String inStr = new String(newB, 0, newPL);
    		//This part is modified from Prof's process coordination program
    		if (inStr.equals("blockRecord") || inStr.toLowerCase().indexOf("blockrecord") >= 0 ) {//check for the bkockrecord header
    			BlockRecord newBlock = this.blockVerifier.makeB(inStr);//send this data to the verifier to create BlockRecord
    			//Then we will add a new block to the queue for verification
    			if (newBlock != null) {//if the newly made blockRecord is empty, we will not add it to out unverified queue
    				this.blockVerifier.myUNBC.add(newBlock);
    			}
    		}
             else if (inStr.contains("allKeys")){//check it the data received have been multicast
                //start to send the public key from each process
                System.out.println("Sending publicKey from process #" + this.pid +" --------- >>>" );
                //then, print this to inform user on console
                String myPubK = Base64.getEncoder().encodeToString(this.keyPair.getPublic().getEncoded());//decode the public key
                String data = "publicKey" + " " + "Process" + this.pid + " " +  myPubK;
                this.sendKeys(data);//send the publicKey to port via dataPacketSocket
            } else if (inStr.contains("publicKey")) {//if the inout str has pubKey, we will split the str
                String[] tok = inStr.split("\\s+"); 
                    
                try {
                	String Kstr = tok[2];//this is decoded publicKey str
                    byte[] decodedCreatorPublicKey = Base64.getDecoder().decode(Kstr);//key
                    X509EncodedKeySpec Ksp = new X509EncodedKeySpec(decodedCreatorPublicKey);//for key spec
                    String Npro = tok[1];//process#
                    PublicKey mPK = KeyFactory.getInstance("RSA").generatePublic(Ksp);
                    this.blockVerifier.myPubKeyHM.put(Npro,mPK);//send it to blockWorker to put the public key in HAshMAsh
                    System.out.println("Got publicKey from  " + Npro);//inform that we got the pubKey from process
                } catch(Exception e) {}
                if(this.blockVerifier.myPubKeyHM.size() == 3) {//when we got all pubKey from 3 processes
                	System.out.println("\nCommand Info: ");//we can start out work by taking the user's input choice
            		System.out.println("C: Credit Request   R: Read .txt      L: Ledger");
            		System.out.println("V: Verify Request   V hash: Verify with 256Hash");
                    this.textInFile("BlockInput" + this.pid + ".txt");
                    this.uSleep = 1;//make it awake and start working again
                }
            }
        }
    }  
    /*********** Modifiled from Blockchain input utilty Program By Prof. Elloit***********/
    
    public int textInFile(String inFL) {
        /*  PURPOSE: To process the input txt. file and create the BlockRecord with the input data
         * 			 Use Marshaller to convert obj from Brecord to XML format
         * 			 Then, send the formatted data out vis packetSocket
         */
        int tNum = 0;//for counting the total fields added
        try {
            BufferedReader inFD = new BufferedReader(new FileReader(inFL));//reas in the input data
            JAXBContext jCon = JAXBContext.newInstance(BlockRecord.class);//use Jaxb obj
            Marshaller jMar= jCon.createMarshaller();//create function for BlockRecord->XML
            jMar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//format xml output
            String inLine;//for input
            String myUUID;//for random sha256
            UUID myID;
            BlockRecord[] totBR = new BlockRecord[500];//for a new blockrecord
            String[] tts = new String[500];
            int nR = 0;
            while ((inLine = inFD.readLine()) != null) {//read till it reaches the new line
            	//add each dataline by line
                totBR[nR] = new BlockRecord();//create new obj
                myID = UUID.randomUUID();//get uuid for sha string
                myUUID = new String(myID.toString());
                totBR[nR].setABlockID(myUUID );//set the blockID
                String sigBID = Base64.getEncoder().encodeToString(this.encrySig(myUUID));//decode the uuid str
                totBR[nR].setASignedBlockID(sigBID);//set the sign BlockID
                totBR[nR].setACreatePID("Process" + this.pid);  //set the PID
                totBR[nR].setAVerifyPID("Will be set later");//this will be set by BCWorker class
                String dt = timeDT(); //set the time blocked was added
                totBR[nR].setACreateTime(dt);
               
                tts = inLine.split(" +");//This part is the patient info
                totBR[nR].setFFname(tts[0]);//name
                totBR[nR].setFLname(tts[1]);//name
                totBR[nR].setFDOB(tts[2]);//dob
                totBR[nR].setFSSNum(tts[3]);//ssn
                totBR[nR].setGDiag(tts[4]);//dianigsis
                totBR[nR].setGTreat(tts[5]);//treatment
                totBR[nR].setGRx(tts[6]);//medicine

              
                StringWriter outStr = new StringWriter();//use new str writer
                jMar.marshal(totBR[nR], outStr);//write from Marshller
                String bBuf = outStr.toString();//convert to str
                MessageDigest mess = MessageDigest.getInstance("SHA-256");
                mess.update(bBuf.getBytes());//get bytes size
                byte[] mBuf = mess.digest();
                int mBufL = mBuf.length;
                StringBuffer bStr = new StringBuffer();
                int ibs = 0;
                while (ibs < mBufL) {
                	bStr.append(Integer.toString((mBuf[ibs] & 0xff) + 0x100, 16).substring(1));
                	ibs++;
                }//This part is to get a sign data hash and process ID has
                String shaStr = bStr.toString();
                byte[] digSig = this.encrySig(shaStr);//encript the signature
                String sigSha = Base64.getEncoder().encodeToString(digSig);//convert digital sig to string
                totBR[nR].setAProDataHash(shaStr);//use this data for checking error and debugging
                totBR[nR].setASignedDataHash(sigSha);//use this data for checking error and debugging
                
                StringWriter outStr2 = new StringWriter();//use new str writer
                jMar.marshal(totBR[nR], outStr2);//write from Marshller
                String comBR = outStr2.toString();//convert to str
                byte[] buff = comBR.getBytes();//get byte
                this.sendSyn(buff);//send the xml format data out via socket
                nR++;
                tNum ++;
            }
          inFD.close();//close the red-in buffer
        } catch (Exception e) {}
        return tNum;//return the total data block added
    }
    public String timeDT() {
    	/* PURPOSE: To format the time stamp to be easier to read
       	 * 			This time obj will be printed on the console with ledger and to xml file
       	 */
    	String pat = "yyyy-MM-dd HH:mm:ss";//format pattern
    	SimpleDateFormat dtFormat = new SimpleDateFormat(pat);//set the format
    	String dt = dtFormat.format(new Date()); //format the new date object 
    	return dt;//return formatted date obj
    }
    
   	
    public byte[] encrySig(String inStr) {
    	//PURPOSE: To get the digital signature using the private key to sign data
    	//This whole part is provide by Prof. Clark from BlockChain Version I.java
    	byte [] myD = inStr.getBytes();//get str byte
        try {
            Signature enSig = Signature.getInstance("SHA1withRSA");//create instance 
            enSig.initSign(this.keyPair.getPrivate());//get the privateKey
            enSig.update(myD);
            return (enSig.sign());//sign it
        } catch (Exception en) {
        	System.out.println("Error: The doc is not encrpted and signed");//if error, print out to the console
            return null;
        }
    }
    
    public void sendSyn (byte[] buff) throws IOException {
    	//PURPOSE: to get the local ip& for sending the packet date via socket 
    	InetAddress ips = InetAddress.getLocalHost();//get local ip&
    	int bSize = buff.length;//get the length
    	int si;
    	for (si = 0; si< 3; si++) {//loop through the 3 port numbers
    		int myPor = UnvPorts[si];//store the port number
    		DatagramPacket dOut = new DatagramPacket(buff,bSize,ips,myPor);//initialize new data socket with local ip& and portNumber
    		this.socket.send(dOut);	//send out the datapacket
    	}
    }
    
}


/***********Modifliefd from Blockchain Work Program By Prof. Clark Elloit***********/
    
class BCWorker extends Thread {
    	/* This class is modified from Webserver hw and BlockChain Sample Work Program by Prof. Elloit
    	 * I add several methods to the class
    	 * PURPOSE: To Serve as Block Chain worker that will do the work for each process to compete to solve the puzzle for verification
    	 * 	        It will loop to the unverifliedblock queue, with first in first out 
    	 * 			If the block is not verified, we will break the loop and continue to the next block in the queue
    	 * 			if the block is verified, we will start writing the new blockRecord from xml and add it to the blockchain
    	 * 			The process will complete to solve the puzzle. if it < 50000, the puzzle is solved and the block will be verfied
    	 */

    	int[] portNum = {4710, 4711, 4712};//the global portnumber for each process
    	int pid;//global processID# 0,1,2
        int sizeBC;//totalsize of Bchain
  
        LinkedList<BlockRecord> myBCLinkL;//the Bchain
        ArrayList<String> myBCID;//the BID array
        Queue<BlockRecord> myUNBC;//the queue for unverified block
        DatagramSocket mySockFD;//for sending and getting the data
        HashMap<String,PublicKey> myPubKeyHM;//hashmap with publicKey value
        KeyPair myKPs;//keypair obj
        

        public BCWorker(DatagramSocket sockFD, int prID, KeyPair kP){//the constructor for class and will set the first block0 as well
            this.mySockFD = sockFD;//for sending and getting the data
            this.pid = prID;//processID# 0,1,2
            this.myKPs = kP;//keypair obj
            this.myUNBC = new LinkedList<>();//queue for unverified block
            this.myBCLinkL = new LinkedList<>();//the Bchain
            this.myPubKeyHM = new HashMap<>();//hashmap with publicKey value
            this.myBCID = new ArrayList<String>();//the BID array
            BlockRecord B1 = new BlockRecord();//creat new Brecord object
            String biid = new String(UUID.randomUUID().toString());//set the uuid number
            String shiid = "63b95d9c17799463acb7d37c85f255a511f23d7588d871375d0119ba4a96a";//string SHA256 for first block from Prof. Elliot's program
            //This will be setting the first blockRecord #0 of our bChain, so we need to hard cord the elements 
            B1.setABlockID(biid);//set the BID equals uuid
            B1.setABlockNum("0");//set to Block#0
            B1.setASHA256String(shiid);//hard code,  number is from Prof's program
            this.myBCLinkL.add(B1);//add the first block to the chain
            this.myBCID.add("biid"); //add the blockID to the array for tracking     
            
        }

        /***********Modifiled from BlockChainInputUtility.java By Prof. Clark Elloit***********/
        public BlockRecord makeB(String inXml) {
        	/* 	Codes are provided from Prof. Elloit form BlockChainInput Utility program
    		 * 	PURPOSE: To convert the  XMLdata to BlockRecord object using UnMarshaller		
    		 */
            BlockRecord NB = null; //create and set BR
            StringReader inStr = new StringReader(inXml);//declare new str reader to read and store the received xml record
    		JAXBContext jCon = null;//declare and set JAXBobj
    		Unmarshaller jMar = null;//declare and set UnmarshalerObj
    		try {
    			jCon = JAXBContext.newInstance(BlockRecord.class);//create new obj
    			jMar= jCon.createUnmarshaller();//call create unmarshal 
    			NB = (BlockRecord)jMar.unmarshal(inStr);//convert the xml to Blockrecord object 
    		} catch (JAXBException ej){}  //printout the trace error
    		return NB;//return the Blockrecord obj
    		       
        }
        
        
        public void joinMyBC(LinkedList<BlockRecord> newBC, ArrayList<String> newBIDs){//This method will update the Bchain
            this.myBCID = newBIDs;//the new BlockID to the current array 
            this.myBCLinkL = newBC;//add the newBlock to the Bchain
        }

    public void run() {//run the thread for the BCWorker class
    	while (true) {
    		 
    		/*********** Modifiled From BlockChain Version I, Worker Program.java By Prof. Clark Elloit***********/
    		/* PURPOSE: Run the thread to do the work to solve the puzzle for verifing new Brecord
    		 * 			The process that can solves the puzzles (work <50000) will win and get to verify the block
    		 * 			To verify, check the encrypted sign by private key 
    		 */
    		
    		if (this.myUNBC.isEmpty() == false) {//check if the queue is empty or not
               // if the queue not empty, we will verify the unverified Brecord
                BlockRecord myBlc = myUNBC.remove();//the first Brecord of the queue (FIFO)
                String cCP = myBlc.getACreatePID();//its process# which created the Brecord 
                String sBID = myBlc.getASignedBlockID();//the signed process# which created the Brecord 
                String BID = myBlc.getABlockID();//the block ID
                PublicKey pubmk = this.myPubKeyHM.get(cCP);//get the pubKey from process
                byte[] deSigBID  = Base64.getDecoder().decode(sBID);//Then,decode the signed blockID 
                byte[] byBID = BID.getBytes();//save the byte of bid to the array byte
                boolean isVerBID;//if the signature is verified, return true and process the owrk and write the new BRecord
                
     /**************This section provided by prof. Elloit on Blockchain Ver I.java ******************/
                //I modified his original codess
                try {//Check Signature
    				Signature sgn = Signature.getInstance("SHA1withRSA");
    				sgn.initVerify(pubmk);
    				sgn.update(byBID);
    				isVerBID = sgn.verify(deSigBID);//use verify function to check signature, return boolean <T/F>
    			} catch (Exception e) { isVerBID = false;} 
    			if (isVerBID == false) continue;//block not verified, exit the loop for this block
                
                //This section will be processed if the block is now verified
                BlockRecord endB = this.myBCLinkL.getLast();//get and store the last block in current Bchain
                Integer endN = Integer.parseInt(endB.getABlockNum());//block Num
    			Integer nextN= endN + 1;//set the next block number
    			String strBNum = nextN.toString();//convert to str
    			myBlc.setABlockNum(strBNum);//set the block number for this newly verified block
    			String preHash = endB.getASHA256String();//get the last block SHA has
    			myBlc.setABlockPreviousHash(preHash);//set the preHash to the field in Brecord
    			String blockId = myBlc.getABlockID();//block id for this block
    			
    			JAXBContext jCon;//create new jaxb obj
    			Marshaller jMar;//create new marshall obj
    			
    			try {//for converting obj to xml
    				jCon = JAXBContext.newInstance(BlockRecord.class);//new obj
    				jMar= jCon.createMarshaller();//use create method 
    				jMar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//fotmat the output
    				byte[] shr = new byte[256];//set the byte size for sha256 str
    				Random rand= new Random(); //get the random number
    				while (true) {  //keep looping 
                        rand.nextBytes(shr); //get the rand bytes
                        StringWriter outS = new StringWriter();//create new strwriter for writing to xml file
                        String rdd = Base64.getEncoder().encodeToString(shr);//decode the rand byte to str format
                        myBlc.setASeedString(rdd);//set the rand str to the field in Brecord
                        jMar.marshal(myBlc, outS);//use marshall obj to convert our obj to xml format
                        String outX = outS.toString();//convert to str format

                        MessageDigest mess = MessageDigest.getInstance("SHA-256");//use digest algr
                        String ccat = (outX + preHash);//combine our xml data to the previous hash str
                        mess.update(ccat.getBytes());//update it to mess
                        byte[] bBuff = mess.digest();//convert, save it to byte array
    					int bBuffLen = bBuff.length;//len
                        
    					//This section proved by prof. Elloit on Blockchain Work.java
    					StringBuffer sBuff = new StringBuffer(); //Do the work 
    					int by = 0;//counter for looping
    					while (by < 4) {//loop for the last four byte
    						sBuff.append(Integer.toBinaryString((bBuff[by] & 0xFF) + 0x100).substring(1));
    						by ++;
    					}
                        //This section will check it it solve the puzzle, the winner process get digital sign 
    					Long myWork = Long.parseLong(sBuff.toString(), 2);
    					if (myWork < 50000) {//if it less than 5K, it solves the puzzle
    						StringBuffer wBuff = new StringBuffer();//create new str obj
    						int bi = 0;
    						while (bi < bBuffLen) {//loop for whole buffer
    							wBuff.append(Integer.toString((bBuff[bi] & 0xff) + 0x100, 16).substring(1));//add the result
    							bi ++;
    						}
    						String shaStr = wBuff.toString();//convert to str
    						byte[] digSig = null; //initialize obj to null
    						byte [] myD = shaStr.getBytes();//get the bytes
    				    	try {//encrpt with privateKate for digital signing
    				    		Signature enSig  = Signature.getInstance("SHA1withRSA");//get digital signature
    				    		PrivateKey myPriK = this.myKPs.getPrivate();//got privateKey
    				    		enSig.initSign(myPriK);//create obj to sign the doc
    				    		enSig.update(myD);//update data tp be signed
    				    		digSig = enSig.sign();//sign it
    				    	} catch (Exception ens) {//print if not signed
    				    		System.out.println("Error: The doc is not encrpted and signed");
    				    	}
    						myBlc.setASHA256String(shaStr);//set the 256str to the field of Brecord
    						String signedSha = Base64.getEncoder().encodeToString(digSig);//decode the returned digital signature to str        
    						myBlc.setASignedSHA256(signedSha);//set it to signedsha field 
    						String dt = timeDT(); //call the time format method
    						myBlc.setAVerifyTime(dt);//set the time the block is verified
    						myBlc.setAVerifyPID("PID" + this.pid);//set the verifiedprocess if number
    
                            this.myBCLinkL.add(myBlc);//add this newly verified Block to our Bchain
                            this.joinMulBC(blockId);//call the method to add the block to Bchain using BIZD
                            break;
                        }
                        if (this.myBCID.contains(blockId)) {//but if it has same BID, we will exit the loop
                            break;}
                    }
                } catch(Exception e) {}
            }
            System.out.flush();}//flush out the buffer
    }
    
    public String timeDT() {
    	 /* PURPOSE: To format the time stamp to be easier to read
    	 * 			This time obj will be printed on the console with ledger and to xml file
    	 */
    	String pat = "yyyy-MM-dd HH:mm:ss";//format pattern
    	SimpleDateFormat dt_Format = new SimpleDateFormat(pat);//set the format
    	String d_t = dt_Format.format(new Date()); //format the new date object 
    	return d_t;//return formatted date obj
    }
    
    public void sendXml (byte[] buff) throws IOException {
    	// PURPOSE: To get the local ipAddress for getting and receiving the data packet via datasocket	
    	InetAddress ips = InetAddress.getLocalHost();//get ip local&
    	int bSize = buff.length;//get length
    	for (int i = 0; i< portNum.length; i++) {//loop for 3 portNums
    		int myPNum = portNum[i];//get the portNum
    		DatagramPacket dOut = new DatagramPacket(buff,bSize,ips,myPNum);//connect that port with pur datasocket
    		this.mySockFD.send(dOut);//to send the data packet out 	
    	}
    }
    
    /**************This section provided by prof. Elloit on Blockchain Ver I.java ******************/
    private void joinMulBC(String b) {
    	//PURPOSE: to update the data by converting the xml obj to Brecord obj
    	JAXBContext jCon;//create new jaxb obj
    	Marshaller jMar;//new marshall obj
    	try {
    	StringWriter outStr = new StringWriter();//new str writer
    	jCon = JAXBContext.newInstance(BlockRecord.class);//get new inst
    	jMar= jCon.createMarshaller();//use create method
    	jMar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//format the xml output
    	int iw = 0;//count
    	int bcL = this.myBCLinkL.size();//size of bchain
    	while (iw < bcL) {//loop for the whole blocks
    		BlockRecord strB = this.myBCLinkL.get(iw);//get each block
    		jMar.marshal(strB, outStr);//convert to xml obj and store it 
    		iw++;
    	}	
    	String myB = outStr.toString();//convert to str
    	String head = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";//the header of xml
    	String myB_clean = myB.replace(head, "");//remove the header from the obj to get clean body content
    	String fXml = head + "\n<CSC435_Block>" + myB_clean + "</CSC435_Block>";//add the block header 
    	byte []	fbXml = fXml.getBytes();//get the total bytes of contents
    	if (!this.myBCID.contains(b))//if it has different blockID
    		this.sendXml(fbXml);//we will send it to the method that will send out the XML obj via data socket		
   
        } catch (Exception err) {} 
    }  
}


/**************This section are modified from by prof. Elloit on Blockchain Ver I.java ******************/
public class Blockchain {
	/* PURPOSE: To process the command input from the user's
	 * 			Get the process ID and set the port number for connecting with DataSocket
	 * 			Create the new objects and new threads for BCServer and UNsignServer and run the threads to do work
	 * 			It calls the methods to print out the output on console regarding
	 */
	private static int setBCport(int prID) { return 4710 + prID;}//get the processID#0,1,2 from args input and set to the portnum
	private static int setUNVport(int prID) { return 4820 + prID;}//get the processID#0,1,2 from args input and set to the portnum
	private static int setUPport(int prID) { return 4930 + prID;}//get the processID#0,1,2 from args input and set to the portnum

	//Main Program
	public static void main(String args[]) throws Exception {
		//loop for taking the args input to get the processID from the terminal
		int prID;
		if (args.length < 1) prID = 0;//if the user didn't put any number, we will set PID to 0
		else {
			prID= Integer.parseInt(args[0]);//convert the str to int
			if (prID != 0 && prID != 1 && prID != 2)//if the processID is not 0,1,2-> we will set it to 0 as a default PID
				prID = 0;
		}

		int bcPort = setBCport(prID);//set the portNUm for base
		int unvePort = setUNVport(prID);//set the portNUm for unverfied class
		int updPort = setUPport(prID);//set the portNUm for updating block

		System.out.println("\nStarting the process and getting all public keys....");
		System.out.println("Getting Public Keys at Port:      " + bcPort );
		System.out.println("Getting Unverified block at Port: " + unvePort);
		System.out.println("Getting Updated block at Port:    " + updPort + "\n");
	
		UnSgnBCServer unBcServer = new UnSgnBCServer(unvePort, prID);//create a new unsgnBCServer object
		Thread unBcThread = new Thread(unBcServer);//set a new thread and run it
		unBcThread.start(); 
		
		BCServer bcServ = new BCServer(bcPort, unBcServer, prID, updPort);//create a new BCServer object
		Thread bcThread = new Thread(bcServ);//set a new thread and run it
		bcThread.start(); 

		while (true) {//This part is to fake the sleep
			if (unBcServer.uSleep == 1)//it works like sleep function to hold the time and wait for updating Bchain
				break;//if it is 1, meaning the current work is done, so we break it 
			System.out.flush();//to avoid overflow, we need to flush buffer
		}

		BufferedReader inFD = new BufferedReader(new InputStreamReader(System.in));//use the buf reader to readin the command input
		try {
			String comIN;//to save the user's input
			do {	
				System.out.println("\n\nPlease select command by typing: \"C\", \"R\", \"V\", \"V hash\", \"L\" or quit");//the mode to be selected by users
				System.out.flush();//flush the buffer
				comIN = inFD.readLine();//read in the input and store it 

				if (comIN.indexOf("C")>=0)//input is C so we will print out the verify credit for each process
					System.out.println(bcServ.creditMyBC());

				else if (comIN.indexOf("R")>=0) {//input is R so we will read the txt file and write the input data to xml ledger by process#0
					if (comIN.length() < 3) System.out.println("Please type: \"R filename.txt\"");//ask for the filename
					else {
					String fname = comIN.split("\\s+")[1];//split the str to get only file name
					int numRec = unBcServer.textInFile(fname);//pass to method 
					System.out.println(numRec + " records are now added to unverified blocks.");
					}
				}
				else if (comIN.indexOf("V")>=0 && (!comIN.equals("V hash")))//input is V, so we will verify the block
					System.out.println(bcServ.veriMyBC(false));//with no hash verify
				else if (comIN.equals("V hash")) //with hash verify
					System.out.println(bcServ.veriMyBC(true));//printout the returned output

				else if (comIN.indexOf("L")>=0) {//input is L , so we will list all datalines from our ledgers
					ArrayList<String> arr = bcServ.listMyBC();//the function return the arrlist with info of each block in Bchain
					for (String s: arr)//loop over the list
						System.out.println(s);//print each entry (block data) in the ledger from descending time order
				} 
				else {
					System.out.println("Please retype a valid option shown above.");//ask for valid command
				}
			}		
			while (comIN.indexOf("quit") < 0);{//if type quit, we will inform the process will be ended
			System.out.println("Process quited by user");
			}
		}   
		catch (IOException exr1 ){exr1.printStackTrace(); }//to deal with potential error
	}
}