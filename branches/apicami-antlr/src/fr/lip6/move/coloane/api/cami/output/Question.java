package fr.lip6.move.coloane.api.cami.output;

public class Question {

	public String rootName;
	public String elementName;
	public String moreInformation;

	public Question( String rootName, String elementName, String moreInformation) {
		this.rootName = rootName;
		this.elementName = elementName;
		this.moreInformation = moreInformation;
	}
}
