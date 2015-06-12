package nvg.mm.td;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

import jxl.*;
import jxl.read.biff.BiffException;
import jxl.biff.RecordData;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;


public class ExtractData {
	@SuppressWarnings("null")
	public static void main(String[] args) throws RowsExceededException, WriteException {
		
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
		
		int inputCounter = 0;
		//for (inputCounter = 0; inputCounter < args.length; ++inputCounter){
			//sSystem.out.println(args[inputCounter]);
			
		
			try {
				// Read a workbook from a file
				//Workbook workbook = Workbook.getWorkbook(new File(args[inputCounter]));
				Workbook workbook = Workbook.getWorkbook(new File("all_issues.xls"));
				
				// Read a sheet from the workbook
				Sheet sheet = workbook.getSheet(0);
				
				// Identify number of columns in the sheet
				int numCols = sheet.getColumns();
				
				// Find columns of TD Num, Model, Status, Priority, Detected in version
				int tdNum = 0, colModel = 0, colStatus = 0, colPriority = 0, colDetectVer = 0;
				
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
						if (issuesList.get(nextModelCounter).isMatched() == false && rootModelname.regionMatches(0, nextModelname, 0, 6)) {
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
								String Priority = issuesList.get(issueCounter).getPriority();
								String Status = issuesList.get(issueCounter).getStatus();
								detVerPriorityCounter.countDetectVer(modelCounter, verIdentifier, DetectedInVer, Priority, Status);
								detVerStatusCounter.PriStatCounter(Priority, Status);
							}	
						}
						verPriorityList.add(detVerPriorityCounter);
						verStatusList.add(detVerStatusCounter);
					}
					
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
				WritableWorkbook workbookOutput = Workbook.createWorkbook(new File (inputCounter + "_output.xls"));
							
				for (int statusCounter = 0; statusCounter < modelIdentifier - 1; ++statusCounter){
					
					WritableSheet sheetOutput = workbookOutput.createSheet("tab " + statusCounter, statusCounter);
					
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
					
					//System.out.println("A-Major DEMAND NEW VER = " + finalVerList.get(statusCounter).getaDemand());			
					
					int numOpenAIssues = listStatus.get(statusCounter).getaReOpen() + listStatus.get(statusCounter).getaOpen() + listStatus.get(statusCounter).getaNew()
							+ listStatus.get(statusCounter).getaAssigned() + listStatus.get(statusCounter).getaFixed() + listStatus.get(statusCounter).getaDemand();
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
					
					int numOpenBIssues = listStatus.get(statusCounter).getbReOpen() + listStatus.get(statusCounter).getbOpen() + listStatus.get(statusCounter).getbNew()
							+ listStatus.get(statusCounter).getbAssigned() + listStatus.get(statusCounter).getbFixed() + listStatus.get(statusCounter).getbDemand();
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
					
					int numOpenCIssues = listStatus.get(statusCounter).getcReOpen() + listStatus.get(statusCounter).getcOpen() + listStatus.get(statusCounter).getcNew()
							+ listStatus.get(statusCounter).getcAssigned() + listStatus.get(statusCounter).getcFixed() + listStatus.get(statusCounter).getcDemand();
					System.out.println("C-Comment TOTAL OPEN = " + numOpenCIssues);
					
					System.out.println(" ");
					System.out.println("---------------------------------------");
	
					System.out.println(" ");
					System.out.println(" ");
					
					
					
					
					//Print to excel
					
					Label ModelName = new Label (1, 1, ListModelName.get(statusCounter));
					sheetOutput.addCell(ModelName);
									
					Label NewinCurrentBuild = new Label (1, 18, "New in Current Build");
					sheetOutput.addCell(NewinCurrentBuild);
					
						Number NewinCurrentBuildA = new Number (2, 18, finalVerAList.get(statusCounter));
						sheetOutput.addCell(NewinCurrentBuildA);
						
						Number NewinCurrentBuildB = new Number (3, 18, finalVerBList.get(statusCounter));
						sheetOutput.addCell(NewinCurrentBuildB);
						
						Number NewinCurrentBuildC = new Number (4, 18, finalVerCList.get(statusCounter));
						sheetOutput.addCell(NewinCurrentBuildC);
					
					Label TotalOpen = new Label (1, 19, "Total Open");
					sheetOutput.addCell(TotalOpen);
					
						Number TotalOpenA = new Number (2, 19, numOpenAIssues);
						sheetOutput.addCell(TotalOpenA);
						
						Number TotalOpenB = new Number (3, 19, numOpenBIssues);
						sheetOutput.addCell(TotalOpenB);
						
						Number TotalOpenC = new Number (4, 19, numOpenCIssues);
						sheetOutput.addCell(TotalOpenC);
					
					Label TotalClosed = new Label (1, 20, "Total Closed");
					sheetOutput.addCell(TotalClosed);
					
						Number TotalClosedA = new Number (2, 20, listStatus.get(statusCounter).getaClosed());
						sheetOutput.addCell(TotalClosedA);
						
						Number TotalClosedB = new Number (3, 20, listStatus.get(statusCounter).getbClosed());
						sheetOutput.addCell(TotalClosedB);
						
						Number TotalClosedC = new Number (4, 20, listStatus.get(statusCounter).getcClosed());
						sheetOutput.addCell(TotalClosedC);
					
					Label TotalClosedDef = new Label (1, 21, "Total Closed Deferred");
					sheetOutput.addCell(TotalClosedDef);
					
						Number TotalClosedDefA = new Number (2, 21, listStatus.get(statusCounter).getaDeferred());
						sheetOutput.addCell(TotalClosedDefA);
						
						Number TotalClosedDefB = new Number (3, 21, listStatus.get(statusCounter).getbDeferred());
						sheetOutput.addCell(TotalClosedDefB);
						
						Number TotalClosedDefC = new Number (4, 21, listStatus.get(statusCounter).getcDeferred());
						sheetOutput.addCell(TotalClosedDefC);
					
					Label TotalClosedWith = new Label (1, 22, "Total Closed Withdrawn");
					sheetOutput.addCell(TotalClosedWith);
					
						Number TotalClosedWithA = new Number (2, 22, listStatus.get(statusCounter).getaWithdrawn());
						sheetOutput.addCell(TotalClosedWithA);
						
						Number TotalClosedWithB = new Number (3, 22, listStatus.get(statusCounter).getbWithdrawn());
						sheetOutput.addCell(TotalClosedWithB);
						
						Number TotalClosedWithC = new Number (4, 22, listStatus.get(statusCounter).getcWithdrawn());
						sheetOutput.addCell(TotalClosedWithC);
					
					Label TotalClosedNot = new Label (1, 23, "Total Closed Not a bug");
					sheetOutput.addCell(TotalClosedNot);
					
						Number TotalClosedNotA = new Number (2, 23, listStatus.get(statusCounter).getaNotaBug());
						sheetOutput.addCell(TotalClosedNotA);
						
						Number TotalClosedNotB = new Number (3, 23, listStatus.get(statusCounter).getbNotaBug());
						sheetOutput.addCell(TotalClosedNotB);
						
						Number TotalClosedNotC = new Number (4, 23, listStatus.get(statusCounter).getcNotaBug());
						sheetOutput.addCell(TotalClosedNotC);
				
					
					Label Major = new Label (2, 17, "Major");
					sheetOutput.addCell(Major);
					
					Label Minor = new Label (3, 17, "Minor");
					sheetOutput.addCell(Minor);
					
					Label Comment = new Label (4, 17, "Comment");
					sheetOutput.addCell(Comment);
										
					
					for (int verCounter = 0; verCounter < verPriorityList.size(); ++verCounter){
						String detInVerName = verPriorityList.get(verCounter).getDetectedInVer();
						int detInVerA = verPriorityList.get(verCounter).getaMajor();
						int detInVerB = verPriorityList.get(verCounter).getbMinor();
						int detInVerC = verPriorityList.get(verCounter).getcComment();
						
						// Print # of # of not a bug for each detected in version. 
						int notBugA = verStatusList.get(verCounter).getaNotaBug();
						int notBugB = verStatusList.get(verCounter).getbNotaBug();
						int notBugC = verStatusList.get(verCounter).getcNotaBug();
						int notBugTotal = notBugA + notBugB + notBugC;
						
						Label detInVerNameLabel = new Label (5, 19 + verCounter, detInVerName);
						sheetOutput.addCell(detInVerNameLabel);
								
						System.out.println(" VerName " + detInVerName + " #A " + detInVerA + " #B " + detInVerB + " #C " + detInVerC +  "NotaBugTot " + notBugTotal);				
						
					}

					

				} // end of for loop
				
				// Print result to excel			
					
				workbookOutput.write();
				workbookOutput.close();

				
				System.out.println("end");
				
			} catch (BiffException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	//}
	
}
