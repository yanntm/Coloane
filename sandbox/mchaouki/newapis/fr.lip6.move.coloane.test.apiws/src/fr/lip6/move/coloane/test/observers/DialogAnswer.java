package fr.lip6.move.coloane.test.observers;

import java.util.List;

import fr.lip6.move.coloane.interfaces.objects.dialog.IDialogAnswer;

public class DialogAnswer implements IDialogAnswer {
	
	private int buttonType;
	
	private int idDialg;
	
	private List<Integer> objects;
	
	private List<String> value;
	
	private boolean modified;
	
	public DialogAnswer(int idDialg, int buttonType, boolean modified, List<String> value, List<Integer> objects){
		this.buttonType = buttonType;
		this.idDialg = idDialg;
		this.objects = objects;
		this.value = value;
		this.modified = modified;
	}

	public int getButtonType() {
		return buttonType;
	}

	public int getIdDialog() {
		return idDialg;
	}

	public List<Integer> getObjects() {
		return objects;
	}

	public List<String> getAllValue() {
		return value;
	}

	public boolean isModified() {
		return modified;
	}

}