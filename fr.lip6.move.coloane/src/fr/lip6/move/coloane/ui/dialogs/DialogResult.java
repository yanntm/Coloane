package fr.lip6.move.coloane.ui.dialogs;

import java.util.ArrayList;

public class DialogResult {
	protected int dialogId;
	protected int answerType;
	protected boolean modified;
	ArrayList<String> text;
	
	public DialogResult(int dialogId, int answerType, boolean modified, ArrayList<String> text) {
		this.dialogId = dialogId;
		this.answerType = answerType;
		this.modified = modified;
		this.text = text;
	}
	
	public ArrayList<String> getText() {
		return text;
	}

	public int getAnswerType() {
		return answerType;
	}

	public int getDialogId() {
		return dialogId;
	}

	public boolean hasBeenModified() {
		return modified;
	}
	
	public String translateToCAMI() {
        StringBuffer s;
        
        String returnValue = text.toString();
        
        s = new StringBuffer();
        s.append("RD(");
        s.append(this.dialogId);
        s.append(",");
        s.append(this.answerType);
        s.append(",");
        s.append(this.modified);
        s.append(",");
        s.append(returnValue.length());
        s.append(":");
        s.append(returnValue);
        s.append(")");

        return s.toString();
	}
}
