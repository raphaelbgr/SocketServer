package app.model.messages;

import java.io.Serializable;
import java.util.List;

public class History implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8709301814741794114L;
	
	private int rowLimit = 0;
	
	private List<String> headers = null;
	
	private List<String> column1 = null;
	private List<String> column2 = null;
	private List<String> column3 = null;
	private List<String> column4 = null;
	private List<String> column5 = null;
	private List<String> column6 = null;
	
	public List<String> getHeaders() {
		return headers;
	}
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}
	public List<String> getColumn1() {
		return column1;
	}
	public void setColumn1(List<String> column1) {
		this.column1 = column1;
	}
	public List<String> getColumn2() {
		return column2;
	}
	public void setColumn2(List<String> column2) {
		this.column2 = column2;
	}
	public List<String> getColumn3() {
		return column3;
	}
	public void setColumn3(List<String> column3) {
		this.column3 = column3;
	}
	public List<String> getColumn4() {
		return column4;
	}
	public void setColumn4(List<String> column4) {
		this.column4 = column4;
	}
	public List<String> getColumn5() {
		return column5;
	}
	public void setColumn5(List<String> column5) {
		this.column5 = column5;
	}
	public List<String> getColumn6() {
		return column6;
	}
	public void setColumn6(List<String> column6) {
		this.column6 = column6;
	}
	public int getRowLimit() {
		return rowLimit;
	}
	public void setRowLimit(int rowLimit) {
		this.rowLimit = rowLimit;
	}

}
