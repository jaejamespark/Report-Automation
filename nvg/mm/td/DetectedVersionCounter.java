package nvg.mm.td;

public class DetectedVersionCounter {
	int modelIdentifier;
	int verIdentifier;
	String DetectedInVer;
	int aMajor;
	int bMinor;
	int cComment;
	
	public DetectedVersionCounter() {
		modelIdentifier = 0;
		verIdentifier = 0;
		DetectedInVer = null;
		aMajor = 0;
		bMinor = 0;
		cComment = 0;
	}
	
	public void countDetectVer(int modelIdentifier, int verIdentifier, String DetVerName, String Priority, String Status) {
		this.modelIdentifier = modelIdentifier;
		this.verIdentifier = verIdentifier;
		DetectedInVer = DetVerName;
		
		if (Priority.equals("A-Major")){
			aMajor ++;
		}
		
		if (Priority.equals("B-Minor")){
			bMinor ++;
		}
		
		if (Priority.equals("C-Comment")){ // FIX IF NECESSARY!!!
			cComment ++;
		}
	}

	public int getModelIdentifier() {
		return modelIdentifier;
	}

	public void setModelIdentifier(int modelIdentifier) {
		this.modelIdentifier = modelIdentifier;
	}

	public int getVerIdentifier() {
		return verIdentifier;
	}

	public void setVerIdentifier(int verIdentifier) {
		this.verIdentifier = verIdentifier;
	}

	public String getDetectedInVer() {
		return DetectedInVer;
	}

	public void setDetectedInVer(String detectedInVer) {
		DetectedInVer = detectedInVer;
	}

	public int getaMajor() {
		return aMajor;
	}

	public void setaMajor(int aMajor) {
		this.aMajor = aMajor;
	}

	public int getbMinor() {
		return bMinor;
	}

	public void setbMinor(int bMinor) {
		this.bMinor = bMinor;
	}

	public int getcComment() {
		return cComment;
	}

	public void setcComment(int cComment) {
		this.cComment = cComment;
	}
	
}
