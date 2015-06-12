package nvg.mm.td;

public class Issues {
	private String tdNum;
	private String model;
	private	String status;
	private String priority;
	private String detectedVer;
	private int modelIdentifier;
	private int DetVerIdentifier;
	private boolean isMatched;
	private boolean isMatchedVer;
	
	
	public Issues(){
		tdNum = null;
		model = null;
		status = null;
		priority = null;
		detectedVer = null;
		modelIdentifier = 0;
		DetVerIdentifier = 0;
		isMatched = false;
		isMatchedVer = false;
	}

	public String getTdNum() {
		return tdNum;
	}

	public void setTdNum(String string) {
		this.tdNum = string;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getDetectedVer() {
		return detectedVer;
	}

	public void setDetectedVer(String detectedVer) {
		this.detectedVer = detectedVer;
	}

	public int getModelIdentifier() {
		return modelIdentifier;
	}

	public void setModelIdentifier(int modelIdentifier) {
		this.modelIdentifier = modelIdentifier;
	}

	public boolean isMatched() {
		return isMatched;
	}

	public void setMatched(boolean isMatched) {
		this.isMatched = isMatched;
	}

	public int getDetVerIdentifier() {
		return DetVerIdentifier;
	}

	public void setDetVerIdentifier(int detVerIdentifier) {
		DetVerIdentifier = detVerIdentifier;
	}

	public boolean isMatchedVer() {
		return isMatchedVer;
	}

	public void setMatchedVer(boolean isMatchedVer) {
		this.isMatchedVer = isMatchedVer;
	}
	
	
		
	
	
}