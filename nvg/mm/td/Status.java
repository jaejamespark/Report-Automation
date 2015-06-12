package nvg.mm.td;

public class Status {

	private int aClosed, aWithdrawn, aDeferred, aNotaBug, aFixed, aAssigned, aOpen, aReOpen, aDemand, aNew;
	private int bClosed, bWithdrawn, bDeferred, bNotaBug, bFixed, bAssigned, bOpen, bReOpen, bDemand, bNew;
	private int cClosed, cWithdrawn, cDeferred, cNotaBug, cFixed, cAssigned, cOpen, cReOpen, cDemand, cNew;	

	public Status(){
		aClosed = 0; 
		aWithdrawn = 0; 
		aDeferred = 0;
		aNotaBug = 0;
		aFixed = 0;
		aAssigned = 0; 
		aOpen = 0;
		aReOpen = 0; 
		aDemand = 0;
		aNew = 0;
		
		bClosed = 0;
		bWithdrawn = 0;
		bDeferred = 0;
		bNotaBug = 0;
		bFixed = 0;
		bAssigned = 0; 
		bOpen = 0;
		bReOpen = 0;
		bDemand = 0;
		bNew = 0;
		
		cClosed = 0;
		cWithdrawn = 0;
		cDeferred = 0;
		cNotaBug = 0;
		cFixed = 0;
		cAssigned = 0; 
		cOpen = 0;
		cReOpen = 0;
		cDemand = 0;
		cNew = 0;
	}

	public void PriStatCounter(String priority, String status) {
		if (priority.equals("A-Major")){
			
			if (status.equals("Closed")){
				aClosed++;
			}
			if (status.equals("Closed.Withdrawn")){
				aWithdrawn++;
			}
			if (status.equals("Closed.Deferred")){
				aDeferred++;
			}
			if (status.equals("Closed.Not a bug")){
				aNotaBug++;
			}
			if (status.equals("Fixed")){
				aFixed++;
			}
			if (status.equals("Assigned")){
				aAssigned++;
			}
			if (status.equals("Open")){
				aOpen++;
			}
			if (status.equals("ReOpen")){
				aReOpen++;
			}
			if (status.equals("Demand")){
				aDemand++;
			}
			if (status.equals("New")){
				aNew++;
			}
		}
		
		if (priority.equals("B-Minor")){
			if (status.equals("Closed")){
				bClosed++;
			}
			if (status.equals("Closed.Withdrawn")){
				bWithdrawn++;
			}
			if (status.equals("Closed.Deferred")){
				bDeferred++;
			}
			if (status.equals("Closed.Not a bug")){
				bNotaBug++;
			}
			if (status.equals("Fixed")){
				bFixed++;
			}
			if (status.equals("Assigned")){
				bAssigned++;
			}
			if (status.equals("Open")){
				bOpen++;
			}
			if (status.equals("ReOpen")){
				bReOpen++;
			}
			if (status.equals("Demand")){
				bDemand++;
			}
			if (status.equals("New")){
				bNew++;
			}
		}
		
		if (priority.equals("C-Comment")){ // FIX IF NECESSARY!!!
			if (status.equals("Closed")){
				cClosed++;
			}
			if (status.equals("Closed.Withdrawn")){
				cWithdrawn++;
			}
			if (status.equals("Closed.Deferred")){
				cDeferred++;
			}
			if (status.equals("Closed.Not a bug")){
				cNotaBug++;
			}
			if (status.equals("Fixed")){
				cFixed++;
			}
			if (status.equals("Assigned")){
				cAssigned++;
			}
			if (status.equals("Open")){
				cOpen++;
			}
			if (status.equals("ReOpen")){
				cReOpen++;
			}
			if (status.equals("Demand")){
				cDemand++;
			}
			if (status.equals("New")){
				cNew++;
			}
		}
		
	}

	public int getaClosed() {
		return aClosed;
	}

	public void setaClosed(int aClosed) {
		this.aClosed = aClosed;
	}

	public int getaWithdrawn() {
		return aWithdrawn;
	}

	public void setaWithdrawn(int aWithdrawn) {
		this.aWithdrawn = aWithdrawn;
	}

	public int getaDeferred() {
		return aDeferred;
	}

	public void setaDeferred(int aDeferred) {
		this.aDeferred = aDeferred;
	}

	public int getaNotaBug() {
		return aNotaBug;
	}

	public void setaNotaBug(int aNotaBug) {
		this.aNotaBug = aNotaBug;
	}

	public int getaFixed() {
		return aFixed;
	}

	public void setaFixed(int aFixed) {
		this.aFixed = aFixed;
	}

	public int getaAssigned() {
		return aAssigned;
	}

	public void setaAssigned(int aAssigned) {
		this.aAssigned = aAssigned;
	}

	public int getaOpen() {
		return aOpen;
	}

	public void setaOpen(int aOpen) {
		this.aOpen = aOpen;
	}

	public int getaReOpen() {
		return aReOpen;
	}

	public void setaReOpen(int aReOpen) {
		this.aReOpen = aReOpen;
	}

	public int getaDemand() {
		return aDemand;
	}

	public void setaDemand(int aDemand) {
		this.aDemand = aDemand;
	}

	public int getbClosed() {
		return bClosed;
	}

	public void setbClosed(int bClosed) {
		this.bClosed = bClosed;
	}

	public int getbWithdrawn() {
		return bWithdrawn;
	}

	public void setbWithdrawn(int bWithdrawn) {
		this.bWithdrawn = bWithdrawn;
	}

	public int getbDeferred() {
		return bDeferred;
	}

	public void setbDeferred(int bDeferred) {
		this.bDeferred = bDeferred;
	}

	public int getbNotaBug() {
		return bNotaBug;
	}

	public void setbNotaBug(int bNotaBug) {
		this.bNotaBug = bNotaBug;
	}

	public int getbFixed() {
		return bFixed;
	}

	public void setbFixed(int bFixed) {
		this.bFixed = bFixed;
	}

	public int getbAssigned() {
		return bAssigned;
	}

	public void setbAssigned(int bAssigned) {
		this.bAssigned = bAssigned;
	}

	public int getbOpen() {
		return bOpen;
	}

	public void setbOpen(int bOpen) {
		this.bOpen = bOpen;
	}

	public int getbReOpen() {
		return bReOpen;
	}

	public void setbReOpen(int bReOpen) {
		this.bReOpen = bReOpen;
	}

	public int getbDemand() {
		return bDemand;
	}

	public void setbDemand(int bDemand) {
		this.bDemand = bDemand;
	}

	public int getaNew() {
		return aNew;
	}

	public void setaNew(int aNew) {
		this.aNew = aNew;
	}

	public int getbNew() {
		return bNew;
	}

	public void setbNew(int bNew) {
		this.bNew = bNew;
	}

	public int getcClosed() {
		return cClosed;
	}

	public void setcClosed(int cClosed) {
		this.cClosed = cClosed;
	}

	public int getcWithdrawn() {
		return cWithdrawn;
	}

	public void setcWithdrawn(int cWithdrawn) {
		this.cWithdrawn = cWithdrawn;
	}

	public int getcDeferred() {
		return cDeferred;
	}

	public void setcDeferred(int cDeferred) {
		this.cDeferred = cDeferred;
	}

	public int getcNotaBug() {
		return cNotaBug;
	}

	public void setcNotaBug(int cNotaBug) {
		this.cNotaBug = cNotaBug;
	}

	public int getcFixed() {
		return cFixed;
	}

	public void setcFixed(int cFixed) {
		this.cFixed = cFixed;
	}

	public int getcAssigned() {
		return cAssigned;
	}

	public void setcAssigned(int cAssigned) {
		this.cAssigned = cAssigned;
	}

	public int getcOpen() {
		return cOpen;
	}

	public void setcOpen(int cOpen) {
		this.cOpen = cOpen;
	}

	public int getcReOpen() {
		return cReOpen;
	}

	public void setcReOpen(int cReOpen) {
		this.cReOpen = cReOpen;
	}

	public int getcDemand() {
		return cDemand;
	}

	public void setcDemand(int cDemand) {
		this.cDemand = cDemand;
	}

	public int getcNew() {
		return cNew;
	}

	public void setcNew(int cNew) {
		this.cNew = cNew;
	}
	
}
