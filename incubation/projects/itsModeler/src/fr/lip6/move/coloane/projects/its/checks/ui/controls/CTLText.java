/**
 * Copyright (c) 2006-2010 MoVe - Laboratoire d'Informatique de Paris 6 (LIP6).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jean-Baptiste VORON (LIP6) - Project Head / Initial contributor
 *   Clément DÉMOULINS (LIP6) - Project Manager
 *   Yann THIERRY-MIEG (LIP6)
 *
 * Official contacts:
 *   coloane@lip6.fr
 *   http://coloane.lip6.fr
 */
package fr.lip6.move.coloane.projects.its.checks.ui.controls;

import java.util.ArrayList;
import java.util.List;

import fr.lip6.move.coloane.projects.its.antlrutil.ErrorReporter;
import fr.lip6.move.coloane.projects.its.checks.CheckList;
import fr.lip6.move.coloane.projects.its.ctl.CTLFormula;

import main.antlr3.fr.lip6.move.coloane.projects.its.ctl.parser.CTLParserLexer;
import main.antlr3.fr.lip6.move.coloane.projects.its.ctl.parser.CTLParserParser;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class CTLText {

	// owning list, used to find variables and other formulas for cross refs
	private CheckList cl;
	// current parse errors
	private List<ParseError> errors = new ArrayList<ParseError>();
	// The user input field that contains the formula
	private StyledText text;
	
	private Text errorText;
	
	public CTLText(Composite parent, GridData gd) {
		Composite comp = new Composite(parent, SWT.BORDER | SWT.FILL);
		comp.setLayout(new GridLayout());
		
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		
		gd2.heightHint = 60;
		comp.setLayoutData(gd2);
		
	//	gd.heightHint = 25;
		text = new StyledText(comp, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(gd);
		
		text.addModifyListener(new GrammarListener());
		
		char[] autoActivationCharacters = new char[] { '#', '(' };
		KeyStroke keyStroke = null;
		try {
			keyStroke = KeyStroke.getInstance("Ctrl+Space");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// ContentProposalAdapter adapter = 
		new ContentProposalAdapter(
			text, new StyledTextContentAdapter(), 
			new CTLContentProposalProvider(),
			keyStroke, autoActivationCharacters);
		
		errorText = new Text(comp, SWT.MULTI);
		errorText.setLayoutData(gd);
		errorText.setEditable(false);
		errorText.setForeground(errorText.getDisplay().getSystemColor(SWT.COLOR_RED));
	}

	
	class CTLContentProposalProvider implements IContentProposalProvider {

		public IContentProposal[] getProposals(String contents, int position) {
			List<IContentProposal> props = new ArrayList<IContentProposal>();
			for (ParseError error : errors) {
				if (error.getCharAt() == position) {
					for (String sug : error.getSuggs())
						props.add(new ContentProposal(sug));
				}
			}
			return props.toArray(new IContentProposal[props.size()]);
		}
		
	}
	
	public void setCheckList(CheckList cl) {
		this.cl = cl;
	}

	private CTLFormula expr;

	private void setExpr(CTLFormula expr) {
		this.expr = expr;
	}

	public CTLFormula getFormula() {
		return expr;
	}

	class GrammarListener implements ModifyListener {
		private String previousCTL = "";

		public void modifyText(ModifyEvent e) {
			String ctl = text.getText();
			if (ctl != null && !ctl.equals(previousCTL)) {

				clearErrors();

				previousCTL = ctl;
				CTLParserLexer lexer;
				lexer = new CTLParserLexer(new ANTLRStringStream(ctl));

				CommonTokenStream tokens = new CommonTokenStream(lexer);

				CTLParserParser parser = new CTLParserParser(tokens);
				setExpr(null);
				ErrorReporter report = new ErrorReporter();
				parser.setErrorReporter(CTLText.this);
				parser.setCheckList(cl);
				try {
					setExpr(parser.ctlformula());

					for (String error : report) {
						System.err.println(error);
						// addCheckFail(elt, att, error, result);
						// testok = false;
					}
				} catch (RecognitionException ee) {
					System.err.println(ee + ee.getMessage());
					ee.printStackTrace();
					// addCheckFail(elt, att, e.getLocalizedMessage(), result);
					// testok = false;
				}

			}
			updateErrors();
		}

	}

	private void clearErrors() {
		errors = new ArrayList<ParseError>();
		text.setToolTipText("");
		errorText.setText("");
		// setStyleRange(null);
		StyleRange sr = new StyleRange();
		sr.start = 0;
		sr.length = text.getText().length();
		sr.underline = false;
		text.setStyleRange(sr);
	}
	

	public void reportError(String msg, int charAt, int len, List<String> suggs) {
		errors.add(new ParseError(msg,charAt,len, suggs));
	}
	
	public void updateErrors () {
		for (ParseError error : errors) {
			text.setToolTipText(text.getToolTipText() + error.getMsg() + "\n");
			errorText.setText(errorText.getText() + error.getMsg() + "\n");
			StyleRange sr = new StyleRange();
			sr.start = error.getCharAt() == -1 ? text.getText().length() - 1 : error.getCharAt();
			sr.length = Math.min(error.getLen(), text.getText().length() - sr.start);
			sr.underline = true;
			sr.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
			sr.underlineColor = text.getDisplay().getSystemColor(SWT.COLOR_RED);
			// sr.underlineStyle = SWT.UNDERLINE_SINGLE;//SWT.UNDERLINE_SQUIGGLE;
			// sr.underlineColor = getDisplay().getSystemColor(SWT.COLOR_RED);
			text.setStyleRange(sr);
		}
	}


	public void addModifyListener(ModifyListener modifyListener) {
		text.addModifyListener(modifyListener);
	}


	public String getText() {
		return text.getText();
	}


	public void setText(String ctlFormula) {
		text.setText(ctlFormula);
	}

}
