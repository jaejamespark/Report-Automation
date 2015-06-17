package nvg.mm.td;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;


public class ExtractData {
	public static void main(String[] args) throws RowsExceededException, WriteException {
	

	boolean modelNameNdetInVerisMISMATCHED = false;
	int modelNameNdetInVerMISMATCHCounter = 0;
		/*
			For Mac OSX, use the following lines in the command window.
			
			javac -cp jxl.jar nvg/mm/td/ExtractData.java
		  	java -cp .:jxl.jar nvg.mm.td.ExtractData all_issues.xls
			
			Source:
			http://stackoverflow.com/questions/8949413/how-to-run-java-program-in-terminal-with-external-library-jar 
		 */
		
		
		/*
		For Windows, use the following lines in the command window.
		
		javac -cp jxl.jar; nvg/mm/td/ExtractData.java
	  	java -cp jxl.jar;. nvg.mm.td.ExtractData all_issues.xls
		
		Source:
		http://stackoverflow.com/questions/8949413/how-to-run-java-program-in-terminal-with-external-library-jar 
	 */
		// Get file path. This will be used for Input/Output folder path
		String currentDirectory = null;
		File file = new File(".");
		currentDirectory = file.getAbsolutePath();
		String inputDirectory = currentDirectory + "/Input/";
		
		int inputCounter = 0;
		for (inputCounter = 0; inputCounter < args.length; ++inputCounter){
			//System.out.println(args[inputCounter]);
			
		
			try {
				// Read a workbook from a file
				Workbook workbook = Workbook.getWorkbook(new File(inputDirectory, args[inputCounter]));
				//Workbook workbook = Workbook.getWorkbook(new File(inputDirectory, "all_issues2.xls")); // For testing only

				// Read a sheet from the workbook
				Sheet sheet = workbook.getSheet(0);
				
				// Identify number of columns in the sheet
				int numCols = sheet.getColumns();
				
				// Find columns of TD Num, Model, Status, Priority, Detected in version
				int tdNum = 0, colModel = 0, colStatus = 0, colPriority = 0, colDetectVer = 0;
				int colSummary = 0, colReproducible = 0;
				
				for (int cols = 0; cols < numCols; ++cols) {
					Cell cellCols = sheet.getCell(cols, 0);
					
					String label = cellCols.getContents();
					
					if (label.equals("Defect ID")) {
						tdNum = cols;
						//System.out.println("TD # is at col " + tdNum);
					}
					
					
					if (label.equals("Model")) {
						colModel = cols;
						//System.out.println("Model is at col " + colModel);
					}
					
					if (label.equals("Status")) {
						colStatus = cols;
						//System.out.println("Status is at col " + colStatus);
					}
					
					if (label.equals("Priority")) {
						colPriority = cols;
						//System.out.println("Priority is at col " + colPriority);
					}
					
					if (label.equals("Detected in Version")) {
						colDetectVer = cols;
						//System.out.println("Detected in Version is at col " + colDetectVer);
					}
				
					if (label.equals("Summary")) {
						colSummary = cols;
					}
					
					if (label.equals("Reproducible")) {
						colReproducible = cols;
					}
				}
	
				// put each row's info into issues class then issuesList
				int numRows = sheet.getRows();
				LinkedList <Issues> issuesList = new LinkedList<Issues>();
				
				
				for (int rows = 1; rows < numRows; ++rows){
					
					Issues issue = new Issues(); // This has to be here!!!!!!
					
					Cell cellTdNum = sheet.getCell(tdNum, rows);
					issue.setTdNum((String) cellTdNum.getContents());
					
					Cell cellModel = sheet.getCell(colModel, rows);
					issue.setModel(cellModel.getContents());
					
					Cell cellStatus = sheet.getCell(colStatus, rows);
					issue.setStatus(cellStatus.getContents());
					
					Cell cellPriority = sheet.getCell(colPriority, rows);
					issue.setPriority(cellPriority.getContents());
						
					Cell cellDetectedVer = sheet.getCell(colDetectVer, rows);
					issue.setDetectedVer(cellDetectedVer.getContents());
					
					Cell CellSummary = sheet.getCell(colSummary, rows);
					issue.setSummary(CellSummary.getContents());
					
					Cell CellReproducible = sheet.getCell(colReproducible, rows);
					issue.setReproducible(CellReproducible.getContents());
					
					issuesList.add(issue);
				} //end of for loop
				
				
				// identify model using model identifier
				int modelIdentifier = 1;
				int isMatchedCounter = 0;
				ArrayList<String> ListModelName = new ArrayList<String>();
	
				for (int rootModelCounter = 0; isMatchedCounter != issuesList.size(); ++rootModelCounter){
					issuesList.get(rootModelCounter).setModelIdentifier(modelIdentifier);
					ListModelName.add(issuesList.get(rootModelCounter).getModel());
					int nextDiffModel = 0; // This variable is for the # of iteration to get to the new model. init the variable here.
					for(int nextModelCounter = rootModelCounter + 1; nextModelCounter < issuesList.size(); ++nextModelCounter){	
						String rootModelname = issuesList.get(rootModelCounter).getModel();
						String nextModelname = issuesList.get(nextModelCounter).getModel();
						
						//System.out.println("Previous value " + issuesList.get(nextModelCounter).isMatched());
						if (issuesList.get(nextModelCounter).isMatched() == false && rootModelname.regionMatches(0, nextModelname, 0, 7)) {
							issuesList.get(nextModelCounter).setModelIdentifier(modelIdentifier);
							issuesList.get(nextModelCounter).setMatched(true);
						}
						else{
							// only want 1st index of next different model
							//System.out.println(issuesList.get(nextModelCounter).isMatched());
							if (nextDiffModel == 0 && issuesList.get(nextModelCounter).isMatched() == false) {
								nextDiffModel = nextModelCounter - 1; // -1 added to prevent root model being skipped when adding identifier 
							}
						}
						//System.out.println("root model counter is " + rootModelCounter);
					}
					modelIdentifier += 1;
					// for loop breaker
					// if yes, jumps to next model
					if (rootModelCounter < nextDiffModel){
						rootModelCounter = nextDiffModel;
					}
					else{
						break;
					}	
			} //end of for loop
				
				
				// Arraylist to collect New in current build issue count
				ArrayList<Integer> finalVerAList = new ArrayList<Integer>();
				ArrayList<Integer> finalVerBList = new ArrayList<Integer>();
				ArrayList<Integer> finalVerCList = new ArrayList<Integer>();
				
				// variables to save the NEw in current build issue count
				int finalVerACounter = 0;
				int finalVerBCounter = 0;
				int finalVerCCounter = 0;
				
				// Linkedlist to hold list of Detected in Versions.
				LinkedList <String>verFinalList = null;
				
				//Put verPriorityList & verStatusList LL into this LL.
				LinkedList<LinkedList<DetectedVersionCounter>> ModelVerPrioList = new LinkedList<LinkedList<DetectedVersionCounter>>();
				LinkedList<LinkedList<Status>> ModelVerStatList = new LinkedList<LinkedList<Status>>();
				
				// # of detected in version for each model
				ArrayList <Integer >numOfDetVerForEachModel = new ArrayList <Integer>();
				
				// Linkedlist to hold list of priority A, B, C counter for each detected in version
				LinkedList <DetectedVersionCounter> verPriorityList = new LinkedList<DetectedVersionCounter>();
				// Linkedlist to hold status counter for each detected in version.
				LinkedList <Status> verStatusList = new LinkedList<Status>();
				
				// Get detected in version for each model from issue object	
				//LinkedList <Status> finalVerList = new LinkedList<Status>();
				for (int modelCounter = 1; modelCounter < modelIdentifier; ++ modelCounter){
					LinkedList <String> verListEachModel = new LinkedList<String>(); // initialize linkedlist for each model
					//Status finalVer = new Status();	// init the Status object
					for (int listCounter = 0; listCounter < issuesList.size(); ++listCounter) {
						if (issuesList.get(listCounter).getModelIdentifier() == modelCounter) {
							verListEachModel.add(issuesList.get(listCounter).getDetectedVer());
						}
						//System.out.println(verListEachModel.size());
					}
					
					// Remove duplicated detected in version names. info goes into "set"
					Set <String> set = new TreeSet<String>();
					Iterator<String> i = verListEachModel.iterator();
					while (i.hasNext()){
						String s = i.next();
						if (set.contains(s)) {
							i.remove();
						}
						else {
							set.add(s);
						}
					}
					
					// convert SET -> LinkedList
					verFinalList = new LinkedList<String>();
					verFinalList.addAll(set);
					
					for (int s = 0; s < verFinalList.size(); ++s ){
						System.out.println("verFinalList is " + verFinalList.get(s));
					}
					
					// Count Priority A, B, C for each detected in version
					
					// INIT Linkedlist to hold list of priority A, B, C counter for each detected in version
					verPriorityList = new LinkedList<DetectedVersionCounter>();
					// INIT Linkedlist to hold status counter for each detected in version.
					verStatusList = new LinkedList<Status>();
					
					for (int verIdentifier = 0; verIdentifier < verFinalList.size(); ++ verIdentifier) {
						// object below is created for Priority A, B, C count of each detected in version
						DetectedVersionCounter detVerPriorityCounter = new DetectedVersionCounter();
						// object below is created to count status for each detected in version
						Status detVerStatusCounter = new Status();
						for (int issueCounter = 0; issueCounter < issuesList.size(); ++issueCounter){
							String DetectedInVer = verFinalList.get(verIdentifier);
							if (DetectedInVer.equals(issuesList.get(issueCounter).getDetectedVer())) {
								// because of possible tester's input error, the below line has been added. (case: model name is correct but detected in version is correct.)
								if (issuesList.get(issueCounter).getModelIdentifier() == modelCounter){
									
									String Priority = issuesList.get(issueCounter).getPriority();
									String Status = issuesList.get(issueCounter).getStatus();
									detVerPriorityCounter.countDetectVer(modelCounter, verIdentifier, DetectedInVer, Priority, Status);
									detVerStatusCounter.PriStatCounter(Priority, Status);
								}
								else {
									// ERROR CASE: MODEL NAME & DETECTED IN VERSION MISMATCH 
									modelNameNdetInVerisMISMATCHED = true;
									modelNameNdetInVerMISMATCHCounter += 1;
									// GET MORE SPECIFIC ERROR DATA LATER
								}
							}	
						}
						verPriorityList.add(detVerPriorityCounter);
						verStatusList.add(detVerStatusCounter);
					}
					ModelVerPrioList.add(verPriorityList);
					ModelVerStatList.add(verStatusList);
					numOfDetVerForEachModel.add(verFinalList.size());
					
					
					// Print # of Priority A, B, C for each detected in version.
					for (int verCounter = 0; verCounter < verPriorityList.size(); ++verCounter){
						int a = verPriorityList.get(verCounter).getModelIdentifier();
						int b = verPriorityList.get(verCounter).getVerIdentifier();
						String c = verPriorityList.get(verCounter).getDetectedInVer();
						int d = verPriorityList.get(verCounter).getaMajor();
						int e = verPriorityList.get(verCounter).getbMinor();
						int f = verPriorityList.get(verCounter).getcComment();
						
						
						
						// Print # of each Status (not a bug for now) for each detected in version. 
						int notA = verStatusList.get(verCounter).getaNotaBug();
						int notB = verStatusList.get(verCounter).getbNotaBug();
						int notC = verStatusList.get(verCounter).getcNotaBug();
						int notTotal = notA + notB + notC;
	
						System.out.println("M_ID " + a + " V_ID " + b + " VerName " + c + " #A " + d + " #B " + e + " #C " + f + " NotaBugA " + notA + " NotaBugB " + notB + " NotaBugC " + notC + " NotaBugTot " + notTotal);				
					}


					// Get # of A/B/C issues from final version
					// init the values for each model
					finalVerACounter = 0;
					finalVerBCounter = 0;
					finalVerCCounter = 0;
	
					for (int verCounter = 0; verCounter < issuesList.size(); ++verCounter) {
						if (issuesList.get(verCounter).getDetectedVer().equals(verFinalList.getLast())) {
							String finalVerPriorty = issuesList.get(verCounter).getPriority();
							
							if (finalVerPriorty.equals("A-Major")) {
								finalVerACounter += 1;	
							}
							if (finalVerPriorty.equals("B-Minor")) {
								finalVerBCounter += 1;	
							}
							if (finalVerPriorty.equals("C-Comment")) {
								finalVerCCounter += 1;	
							}
						}
					}
	
					finalVerAList.add(finalVerACounter);
					finalVerBList.add(finalVerBCounter);
					finalVerCList.add(finalVerCCounter);
					
					
					System.out.println("final version is" + verFinalList.getLast());			
					System.out.println("# of A is " + finalVerACounter);
					System.out.println("# of B is " + finalVerBCounter);
					System.out.println("# of C is " + finalVerCCounter);
					
					
					// print the list of detected in version
					for (String s: set){
						System.out.println("Set is " + s);
					}			
					System.out.println("Set size is " + set.size());
		
				}// end of for loop
	
				
		
				// count the status and priority for each model
				LinkedList <Status> listStatus = new LinkedList<Status>();
				for (int modelCounter = 1; modelCounter < modelIdentifier + 1; ++modelCounter) {
					Status status = new Status();
					for (int listCounter = 0; listCounter < issuesList.size(); ++listCounter) {
						
						if (issuesList.get(listCounter).getModelIdentifier() == modelCounter){
							String Priority = issuesList.get(listCounter).getPriority();
							String Status = issuesList.get(listCounter).getStatus();
							status.PriStatCounter(Priority, Status);	
						}
					}
					listStatus.add(status);
				} //end of for loop
				
				
				
				// PRINT THE RESULTS TO EXCEL
				
				//WritableWorkbook workbookOutput = Workbook.createWorkbook(new File ("output.xls")); // For testing only
				

				String outputDirectory = currentDirectory + "/Output/";
				WritableWorkbook workbookOutput = Workbook.createWorkbook(new File (outputDirectory, "OUTPUT_" + args[inputCounter]));  // use this when running command window
			
				for (int statusCounter = 0; statusCounter < modelIdentifier - 1; ++statusCounter){
					
					//WritableSheet sheetOutput = workbookOutput.createSheet("tab " + statusCounter, statusCounter);
					WritableSheet sheetOutput = workbookOutput.createSheet(ListModelName.get(statusCounter).substring(0, 7), statusCounter);
					

					// Generate open issue list
					int rowCounter = 0;
					for (int issuesCounter = 0; issuesCounter < issuesList.size(); ++issuesCounter){
						if (issuesList.get(issuesCounter).getModelIdentifier() == statusCounter + 1) {
							String statusOpen = issuesList.get(issuesCounter).getStatus();
							if (statusOpen.equals("New") || statusOpen.equals("Demand") || statusOpen.equals("ReOpen") || statusOpen.equals("Open") || statusOpen.equals("Assigned") || statusOpen.equals("Fixed") || statusOpen.equals("R&D Rejected")) {
								String tdNumOpen = issuesList.get(issuesCounter).getTdNum();
								String priorityOpen = issuesList.get(issuesCounter).getPriority();
								String summaryOpen = issuesList.get(issuesCounter).getSummary();
								String reproOpen = issuesList.get(issuesCounter).getReproducible();
								
								Label tdNumOpenLabel = new Label (15, 28 + rowCounter, tdNumOpen);
								sheetOutput.addCell(tdNumOpenLabel);
								
								Label priorityOpenLabel = new Label (16, 28 + rowCounter, priorityOpen);
								sheetOutput.addCell(priorityOpenLabel);
								
								Label statusOpenLabel = new Label (17, 28 + rowCounter, statusOpen);
								sheetOutput.addCell(statusOpenLabel);
								
								Label summaryOpenLabel = new Label (18, 28 + rowCounter, summaryOpen);
								sheetOutput.addCell(summaryOpenLabel);
								
								Label reproOpenLabel = new Label (19, 28 + rowCounter, reproOpen);
								sheetOutput.addCell(reproOpenLabel);
								
								rowCounter += 1;
							}
							
						}
					}
					
					Label openIssuesTitle = new Label (15, 26, "[ OPEN ISSUES ]");
					sheetOutput.addCell(openIssuesTitle);
					
					Label tdNumOpenTitle = new Label (15, 27, "Defect ID");
					sheetOutput.addCell(tdNumOpenTitle);
					
					Label priorityOpenTitle = new Label (16, 27, "Priority");
					sheetOutput.addCell(priorityOpenTitle);
					
					Label statusOpenTitle = new Label (17, 27, "Status");
					sheetOutput.addCell(statusOpenTitle);
					
					Label summaryOpenTitle = new Label (18, 27, "Summary");
					sheetOutput.addCell(summaryOpenTitle);
					
					Label reproOpenTitle = new Label (19, 27, "Reproducible");
					sheetOutput.addCell(reproOpenTitle);
					
					
					
					
					// KPI Calculation
					String LastDetInVerName = null;
					int sumRow = 0;
					LinkedList<DetectedVersionCounter> detInVerNameList = ModelVerPrioList.get(statusCounter);
					LinkedList<Status> detInVerStatusList = ModelVerStatList.get(statusCounter);
					for (int verCounter = 0; verCounter < numOfDetVerForEachModel.get(statusCounter); ++verCounter){
						String detInVerName = detInVerNameList.get(verCounter).getDetectedInVer();
						int detInVerA = detInVerNameList.get(verCounter).getaMajor();
						int detInVerB = detInVerNameList.get(verCounter).getbMinor();
						int detInVerC = detInVerNameList.get(verCounter).getcComment();
						
						// Print # of # of not a bug for each detected in version. 
						int notBugA = detInVerStatusList.get(verCounter).getaNotaBug();
						int notBugB = detInVerStatusList.get(verCounter).getbNotaBug();
						int notBugC = detInVerStatusList.get(verCounter).getcNotaBug();
						int notBugTotal = notBugA + notBugB + notBugC;
						
						// save the name of last det. in version using the last counter.
						LastDetInVerName = detInVerNameList.get(verCounter).getDetectedInVer();
						
						
						// Print the result to excel
						Label detInVerNameLabel = new Label (1, 28 + verCounter, detInVerName);
						sheetOutput.addCell(detInVerNameLabel);
						
						Number detInVerNameALabel = new Number (2, 28 + verCounter, detInVerA);
						sheetOutput.addCell(detInVerNameALabel);
						
						Number detInVerNameBLabel = new Number (3, 28 + verCounter, detInVerB);
						sheetOutput.addCell(detInVerNameBLabel);
						
						Number detInVerNameCLabel = new Number (4, 28 + verCounter, detInVerC);
						sheetOutput.addCell(detInVerNameCLabel);
						
						sumRow = verCounter + 29;
						Formula sum = new Formula(5, 28 + verCounter, "SUM(C" + sumRow + ":" + "E" + sumRow + ")");
						sheetOutput.addCell(sum);
						
						
						Label detInVerNameLabelKPI = new Label (7, 28 + verCounter, detInVerName);
						sheetOutput.addCell(detInVerNameLabelKPI);
						
						Number NotaBugTotLabel = new Number (8, 28 + verCounter, notBugTotal);
						sheetOutput.addCell(NotaBugTotLabel);
	
						Formula RealBugTot = new Formula (9, 28 + verCounter, "F" + sumRow + "-" + "I" + sumRow);
						sheetOutput.addCell(RealBugTot);						
						
						System.out.println(" VerName " + detInVerName + " #A " + detInVerA + " #B " + detInVerB + " #C " + detInVerC +  "NotaBugTot " + notBugTotal);				
					}
				
					Label ReportTitle2 = new Label (1, 26, "[ WEEKLY REPORT DATA ]");
					sheetOutput.addCell(ReportTitle2);
					
					Label detInVerNameLabelTitle = new Label (1, 27, "Detected in Version");
					sheetOutput.addCell(detInVerNameLabelTitle);
					
					Label detInVerNameALabelTitle = new Label (2, 27, "A-Major");
					sheetOutput.addCell(detInVerNameALabelTitle);
					
					Label detInVerNameBLabelTitle = new Label (3, 27, "B-Minor");
					sheetOutput.addCell(detInVerNameBLabelTitle);
					
					Label detInVerNameCLabelTitle = new Label (4, 27, "C-Comment");
					sheetOutput.addCell(detInVerNameCLabelTitle);
					
					Label detInVerNameTotLabelTitle = new Label (5, 27, "Total");
					sheetOutput.addCell(detInVerNameTotLabelTitle);
	
					
					Formula VerMajorTot = new Formula (2, sumRow, "SUM(C29:C" + sumRow +")");
					sheetOutput.addCell(VerMajorTot);
					
					Formula VerMinorTot = new Formula (3, sumRow, "SUM(D29:D" + sumRow +")");
					sheetOutput.addCell(VerMinorTot);
					
					Formula VerCommentTot = new Formula (4, sumRow, "SUM(E29:E"  + sumRow +")");
					sheetOutput.addCell(VerCommentTot);
					
					Formula VerTotalTot = new Formula (5, sumRow, "SUM(F29:F"  + sumRow +")");
					sheetOutput.addCell(VerTotalTot);

					
					
					Label TitleKPI = new Label (7, 26, "[ KPI DATA ]");
					sheetOutput.addCell(TitleKPI);
					
					Label detInVerNameLabelTitleKPI = new Label (7, 27, "Detected in Version");
					sheetOutput.addCell(detInVerNameLabelTitleKPI);
					
					Label NotaBugTotLabel = new Label (8, 27, "Closed Not a Bug Total");
					sheetOutput.addCell(NotaBugTotLabel);
					
					Label RealBugTotLabel = new Label (9, 27, "Real Bug Total");
					sheetOutput.addCell(RealBugTotLabel);
					
					Formula NotaBugTotalTot = new Formula (8, sumRow, "SUM(I29:I"  + sumRow +")");
					sheetOutput.addCell(NotaBugTotalTot);
					
					Formula RealBugTotalTot = new Formula (9, sumRow, "SUM(J29:J"  + sumRow +")");
					sheetOutput.addCell(RealBugTotalTot);
					
					// PRINT ALL A-MAJOR ISSUES
					System.out.println("FOR THIS MODEL, " + ListModelName.get(statusCounter) + ".......");
					System.out.println(" ");
					System.out.println("A-Major Closed = " + listStatus.get(statusCounter).getaClosed());
					System.out.println("A-Major Closed.withdrawn = " + listStatus.get(statusCounter).getaWithdrawn());
					System.out.println("A-Major Closed.deferred = " + listStatus.get(statusCounter).getaDeferred());				
					System.out.println("A-Major Closed.Not a bug = " + listStatus.get(statusCounter).getaNotaBug());
					System.out.println("A-Major Demand = " + listStatus.get(statusCounter).getaDemand());
					System.out.println("A-Major Fixed = " + listStatus.get(statusCounter).getaFixed());
					System.out.println("A-Major Assigned = " + listStatus.get(statusCounter).getaAssigned());
					System.out.println("A-Major New = " + listStatus.get(statusCounter).getaNew());
					System.out.println("A-Major Open = " + listStatus.get(statusCounter).getaOpen());
					System.out.println("A-Major ReOpen = " + listStatus.get(statusCounter).getaReOpen());
					System.out.println("A-Major R&D rejected = " + listStatus.get(statusCounter).getaRnDRejected());
					
					//System.out.println("A-Major DEMAND NEW VER = " + finalVerList.get(statusCounter).getaDemand());			
					
					int numOpenAIssues = listStatus.get(statusCounter).getaReOpen() + listStatus.get(statusCounter).getaOpen() + listStatus.get(statusCounter).getaNew()
							+ listStatus.get(statusCounter).getaAssigned() + listStatus.get(statusCounter).getaFixed() + listStatus.get(statusCounter).getaDemand() + listStatus.get(statusCounter).getaRnDRejected();
					System.out.println("A-Major TOTAL OPEN = " + numOpenAIssues);
								
					System.out.println(" ");
					
					// PRINT ALL B-MINOR ISSUES
					System.out.println("B-Minor Closed = " + listStatus.get(statusCounter).getbClosed());
					System.out.println("B-Minor Closed.withdrawn = " + listStatus.get(statusCounter).getbWithdrawn());
					System.out.println("B-Minor Closed.deferred = " + listStatus.get(statusCounter).getbDeferred());				
					System.out.println("B-Minor Closed.Not a bug = " + listStatus.get(statusCounter).getbNotaBug());
					System.out.println("B-Minor Demand = " + listStatus.get(statusCounter).getbDemand());
					System.out.println("B-Minor Fixed = " + listStatus.get(statusCounter).getbFixed());
					System.out.println("B-Minor Assigned = " + listStatus.get(statusCounter).getbAssigned());
					System.out.println("B-Minor New = " + listStatus.get(statusCounter).getbNew());
					System.out.println("B-Minor Open = " + listStatus.get(statusCounter).getbOpen());
					System.out.println("B-Minor ReOpen = " + listStatus.get(statusCounter).getbReOpen());
					System.out.println("B-Minor R&D rejected = " + listStatus.get(statusCounter).getbRnDRejected());
					
					int numOpenBIssues = listStatus.get(statusCounter).getbReOpen() + listStatus.get(statusCounter).getbOpen() + listStatus.get(statusCounter).getbNew()
							+ listStatus.get(statusCounter).getbAssigned() + listStatus.get(statusCounter).getbFixed() + listStatus.get(statusCounter).getbDemand() + listStatus.get(statusCounter).getbRnDRejected();
					System.out.println("B-Minor TOTAL OPEN = " + numOpenBIssues);
					
					System.out.println(" ");
					
					// PRINT ALL C-COMMENT ISSUES
					System.out.println("C-Comment Closed = " + listStatus.get(statusCounter).getcClosed());
					System.out.println("C-Comment Closed.withdrawn = " + listStatus.get(statusCounter).getcWithdrawn());
					System.out.println("C-Comment Closed.deferred = " + listStatus.get(statusCounter).getcDeferred());				
					System.out.println("C-Comment Closed.Not a bug = " + listStatus.get(statusCounter).getcNotaBug());
					System.out.println("C-Comment Demand = " + listStatus.get(statusCounter).getcDemand());
					System.out.println("C-Comment Fixed = " + listStatus.get(statusCounter).getcFixed());
					System.out.println("C-Comment Assigned = " + listStatus.get(statusCounter).getcAssigned());
					System.out.println("C-Comment New = " + listStatus.get(statusCounter).getcNew());
					System.out.println("C-Comment Open = " + listStatus.get(statusCounter).getcOpen());
					System.out.println("C-Comment ReOpen = " + listStatus.get(statusCounter).getcReOpen());
					System.out.println("C-Comment R&D rejected = " + listStatus.get(statusCounter).getcRnDRejected());
					
					int numOpenCIssues = listStatus.get(statusCounter).getcReOpen() + listStatus.get(statusCounter).getcOpen() + listStatus.get(statusCounter).getcNew()
							+ listStatus.get(statusCounter).getcAssigned() + listStatus.get(statusCounter).getcFixed() + listStatus.get(statusCounter).getcDemand() + listStatus.get(statusCounter).getcRnDRejected();
					System.out.println("C-Comment TOTAL OPEN = " + numOpenCIssues);
					
					System.out.println(" ");
					System.out.println("---------------------------------------");
	
					System.out.println(" ");
					System.out.println(" ");
					
					
					
					
					//Print to excel					
					Label ModelName = new Label (1, 1, "Report generated for this model, " + ListModelName.get(statusCounter));
					sheetOutput.addCell(ModelName);
					
					Label ReportTitle1 = new Label (1, 16, "[ WEEKLY REPORT DATA + NVG TEST REPORT DATA ]");
					sheetOutput.addCell(ReportTitle1);
					
					Label NewinCurrentBuild = new Label (1, 18, "New in Current Build");
					sheetOutput.addCell(NewinCurrentBuild);
					
						Number NewinCurrentBuildA = new Number (2, 18, finalVerAList.get(statusCounter));
						sheetOutput.addCell(NewinCurrentBuildA);
						
						Number NewinCurrentBuildB = new Number (3, 18, finalVerBList.get(statusCounter));
						sheetOutput.addCell(NewinCurrentBuildB);
						
						Number NewinCurrentBuildC = new Number (4, 18, finalVerCList.get(statusCounter));
						sheetOutput.addCell(NewinCurrentBuildC);
						
						Formula NewinCurrentBuildTot = new Formula (5, 18, "SUM(C19:E19)");
						sheetOutput.addCell(NewinCurrentBuildTot);
						
						Label LastDetInVerNameLabel = new Label (6, 18, "<- This calculation was made assuming that " + LastDetInVerName + " is the CURRENT build");
						sheetOutput.addCell(LastDetInVerNameLabel);
					
					Label TotalOpen = new Label (1, 19, "Total Open");
					sheetOutput.addCell(TotalOpen);
					
						Number TotalOpenA = new Number (2, 19, numOpenAIssues);
						sheetOutput.addCell(TotalOpenA);
						
						Number TotalOpenB = new Number (3, 19, numOpenBIssues);
						sheetOutput.addCell(TotalOpenB);
						
						Number TotalOpenC = new Number (4, 19, numOpenCIssues);
						sheetOutput.addCell(TotalOpenC);
						
						Formula TotalOpenTot = new Formula (5, 19, "SUM(C20:E20)");
						sheetOutput.addCell(TotalOpenTot);
					
					Label TotalClosed = new Label (1, 20, "Total Closed");
					sheetOutput.addCell(TotalClosed);
					
						Number TotalClosedA = new Number (2, 20, listStatus.get(statusCounter).getaClosed());
						sheetOutput.addCell(TotalClosedA);
						
						Number TotalClosedB = new Number (3, 20, listStatus.get(statusCounter).getbClosed());
						sheetOutput.addCell(TotalClosedB);
						
						Number TotalClosedC = new Number (4, 20, listStatus.get(statusCounter).getcClosed());
						sheetOutput.addCell(TotalClosedC);
						
						Formula TotalClosedTot = new Formula (5, 20, "SUM(C21:E21)");
						sheetOutput.addCell(TotalClosedTot);
					
					Label TotalClosedDef = new Label (1, 21, "Total Closed Deferred");
					sheetOutput.addCell(TotalClosedDef);
					
						Number TotalClosedDefA = new Number (2, 21, listStatus.get(statusCounter).getaDeferred());
						sheetOutput.addCell(TotalClosedDefA);
						
						Number TotalClosedDefB = new Number (3, 21, listStatus.get(statusCounter).getbDeferred());
						sheetOutput.addCell(TotalClosedDefB);
						
						Number TotalClosedDefC = new Number (4, 21, listStatus.get(statusCounter).getcDeferred());
						sheetOutput.addCell(TotalClosedDefC);
						
						Formula TotalClosedDefTotal = new Formula (5, 21, "SUM(C22:E22)");
						sheetOutput.addCell(TotalClosedDefTotal);
					
					Label TotalClosedWith = new Label (1, 22, "Total Closed Withdrawn");
					sheetOutput.addCell(TotalClosedWith);
					
						Number TotalClosedWithA = new Number (2, 22, listStatus.get(statusCounter).getaWithdrawn());
						sheetOutput.addCell(TotalClosedWithA);
						
						Number TotalClosedWithB = new Number (3, 22, listStatus.get(statusCounter).getbWithdrawn());
						sheetOutput.addCell(TotalClosedWithB);
						
						Number TotalClosedWithC = new Number (4, 22, listStatus.get(statusCounter).getcWithdrawn());
						sheetOutput.addCell(TotalClosedWithC);
						
						Formula TotalClosedWithTotal = new Formula (5, 22, "SUM(C23:E23)");
						sheetOutput.addCell(TotalClosedWithTotal);
					
					Label TotalClosedNot = new Label (1, 23, "Total Closed Not a bug");
					sheetOutput.addCell(TotalClosedNot);
					
						Number TotalClosedNotA = new Number (2, 23, listStatus.get(statusCounter).getaNotaBug());
						sheetOutput.addCell(TotalClosedNotA);
						
						Number TotalClosedNotB = new Number (3, 23, listStatus.get(statusCounter).getbNotaBug());
						sheetOutput.addCell(TotalClosedNotB);
						
						Number TotalClosedNotC = new Number (4, 23, listStatus.get(statusCounter).getcNotaBug());
						sheetOutput.addCell(TotalClosedNotC);
						
						Formula TotalClosedNotTot = new Formula (5, 23, "SUM(C24:E24)");
						sheetOutput.addCell(TotalClosedNotTot);
				
					
					Label Major = new Label (2, 17, "Major");
					sheetOutput.addCell(Major);
					
					Label Minor = new Label (3, 17, "Minor");
					sheetOutput.addCell(Minor);
					
					Label Comment = new Label (4, 17, "Comment");
					sheetOutput.addCell(Comment);
					
					Label Total = new Label (5, 17, "Total");
					sheetOutput.addCell(Total);
					
					Formula MajorTot = new Formula (2, 24, "SUM(C20:C24)");
					sheetOutput.addCell(MajorTot);
					
					Formula MinorTot = new Formula (3, 24, "SUM(D20:D24)");
					sheetOutput.addCell(MinorTot);
					
					Formula CommentTot = new Formula (4, 24, "SUM(E20:E24)");
					sheetOutput.addCell(CommentTot);
					
					Formula TotalTot = new Formula (5, 24, "SUM(F20:F24)");
					sheetOutput.addCell(TotalTot);
					

			}
				// end of for loop
				
				// Print result to excel			
					
				workbookOutput.write();
				workbookOutput.close();

				/* FOR DEBUG
				int modelID = 1;
				for (int a = 0; a < issuesList.size(); ++ a){
					//System.out.println("before loop : " +issuesList.get(a).getModel());
					if (issuesList.get(a).getModelIdentifier() == modelID){
						System.out.println(" in loop " + issuesList.get(a).getModel());
						//System.out.println("hello yo");
					}
					else {
						System.out.println("Else : " +issuesList.get(a).getModel());
					}
				}
				*/
				
				System.out.println("end");
				System.out.println("");
				
				if (modelNameNdetInVerisMISMATCHED == true){
					System.out.println("Warning: Possible wrong data from TD. Model name and Detected in version NOT MATCHED");
					System.out.println("Number of mismatch occured in this file is " + modelNameNdetInVerMISMATCHCounter);
				}

				
			} catch (BiffException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
