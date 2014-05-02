package gc.server.gui.views;

import gc.server.util.SyntaxLibrary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

public class ScriptEditor extends JTextPane implements DocumentListener, KeyListener{

	private static final long serialVersionUID = 1L;
	private static final int MAX_OFFSET = 128;

	private StyledDocument sdoc;
	private SyntaxLibrary library;
	private String name;
	private Runnable doHighlight;
	private SimpleAttributeSet attrs;

	public ScriptEditor(SyntaxLibrary lib){
		super();		
		this.library = lib;

		setup("");
	}


	public ScriptEditor(SyntaxLibrary lib, String text){
		super();		
		this.library = lib;
		setup(text);

	}



	public String getName(){
		return name;
	}

	public void setName(String in){
		name = in;
	}

	private void setup(String text){

		library.inserted();
		doHighlight = new Runnable() {
			@Override
			public void run() {
				try {
					handleSyntax();
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}	
			}
		};       

		attrs = new SimpleAttributeSet();
		Font font = new Font("Arial",Font.BOLD, 13);
		setFont(font);
		sdoc = getStyledDocument();

		StyleContext sc = StyleContext.getDefaultStyleContext();

		TabStop[] stops = new TabStop[25];
		for(int i = 0; i < 25; i++){
			stops[i] = new TabStop(25+i*25);
		}
		TabSet tabs = new TabSet(stops);
		AttributeSet paraSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.TabSet, tabs);
		setParagraphAttributes(paraSet, false);

		this.setText(text);
		this.getDocument().addDocumentListener(this);
		this.addKeyListener(this);
	}



	public void handleSyntax() throws BadLocationException{

		int doclength = this.getDocument().getLength();
		String doc_text = this.getDocument().getText(0, doclength);

		calculateOffsets(doc_text);

		int max_length = Math.min(offset, doclength);
		int max_length_offset = Math.min(length, doclength-max_length);


		String text = doc_text.substring(max_length, max_length+max_length_offset);

		StyleConstants.setItalic(attrs, false);
		StyleConstants.setForeground(attrs, Color.BLACK);
		sdoc.setCharacterAttributes(max_length, max_length_offset, attrs, false);


		char[] data = text.toCharArray();
		String value = "";
		int idx0 = 0;
		int idx1 = 0;

		for(int i = 0; i < max_length_offset ; i++){

			char c = data[i];

			if(c==' ' || c =='\n' || c=='\t'){
				value = "";
				continue;
			}

			value += c;


			if(library.isGroup(value)){

				idx0 = i;
				String terminator = library.getGroupTerminator(value);

				while(true){
					idx1 = text.indexOf(terminator, idx0+1);
					if(idx1 <= 0){
						return;
					}
					if(data[idx1-1] != library.getEscape()){
						break;
					}
					idx0 = idx1;
				}
				StyleConstants.setItalic(attrs, false);
				StyleConstants.setForeground(attrs, library.getGroup(value));
				sdoc.setCharacterAttributes(max_length + i - (value.length()-1), 1+terminator.length() +  (idx1 - i), attrs, false);

				i = idx1;
				value = "";

			}else if(library.isReserved(value)){

				StyleConstants.setItalic(attrs, true);
				StyleConstants.setForeground(attrs, library.getReserved(value));
				sdoc.setCharacterAttributes(max_length + i-value.length()+1, value.length(), attrs, false);
				value = "";		
			}else if(library.isReserved(c+"")){

				value = c+"";
				StyleConstants.setItalic(attrs, false);
				StyleConstants.setForeground(attrs, library.getReserved(value));
				sdoc.setCharacterAttributes(max_length + i-value.length()+1, value.length(), attrs, false);
				value = "";		
			}

		}

	}

	private void calculateOffsets(String doc_text){

		Set<String> set = library.getGroupList();

		int start = offset;
		int stop = offset + length;


		for(String s : set){

			String terminator = library.getGroupTerminator(s);
			int start_idx = doc_text.lastIndexOf(s, start);

			if(start_idx != -1){
				start = Math.min(start, doc_text.lastIndexOf(s, start));		
			}

			stop = Math.max(stop, doc_text.indexOf(terminator, stop)+terminator.length());			
		}

		int start_idx = doc_text.lastIndexOf(" ", start);

		if(start_idx != -1){
			start = Math.min(start, start_idx);		
		}		
		stop = Math.max(stop, doc_text.indexOf(" ", stop)+1);			

		if(start < 20){
			start = 0;
		}

		offset = Math.max(offset-MAX_OFFSET, start);
		length = Math.min(length+MAX_OFFSET,stop-start); 


	}


	private int offset, length;
	private DocumentEvent lastEvent;

	@Override
	public void insertUpdate(DocumentEvent e) {
		lastEvent = e;
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		lastEvent = e;

	}

	@Override
	public void changedUpdate(DocumentEvent e) {


	}


	private void highlight() {
		if(disable){
			return;
		}

		SwingUtilities.invokeLater(doHighlight);
	}

	public void highlightAll(){

		offset = 0;
		length = this.getDocument().getLength();

		try {
			handleSyntax();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args){

		JFrame window = new JFrame("editor");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel(new BorderLayout());

		SyntaxLibrary library = new SyntaxLibrary();

		library.createGroup("string", Color.GRAY);
		library.createGroup("comment", new Color(0,175,0));
		library.createGroup("reserved", new Color(128,0,64));
		library.createGroup("std", Color.BLACK);

		library.addGroup("\"","\"","string");
		library.addGroup("\'","\'","string");
		library.addGroup("//","\n", "comment");
		library.addGroup("/*","*/", "comment");

		library.addReserved("var", "reserved");
		library.addReserved("new", "reserved");
		library.addReserved("for", "reserved");
		library.addReserved("while", "reserved");
		library.addReserved("importPackage", "reserved");
		library.addReserved("true", "reserved");
		library.addReserved("false", "reserved");
		library.addReserved("null", "reserved");
		library.addReserved("function", "reserved");

		library.addReserved("{","std");
		library.addReserved("}","std");
		library.addReserved("(","std");
		library.addReserved(")","std");
		library.addReserved(",","std");
		library.addReserved(".","std");
		library.addReserved("+","std");
		library.addReserved("-","std");
		library.addReserved("*","std");
		library.addReserved("%","std");
		library.addReserved("=","std");
		library.addReserved(">","std");
		library.addReserved("<","std");
		library.addReserved(";", "std");

		panel.add(new JScrollPane(new ScriptEditor(library)),BorderLayout.CENTER);
		window.add(panel);
		window.setVisible(true);
		window.setMinimumSize(new Dimension(500,500));

	}


	boolean disable = false;

	@Override
	public void keyTyped(KeyEvent e) {

	}


	@Override
	public void keyPressed(KeyEvent e) {
		
	}


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		char c = e.getKeyChar();

		boolean enable = (c==' ' || library.exists(c+"") || c=='\n' || c=='\t' || c=='.');

		if(enable && lastEvent!=null){
			offset = Math.max(0, lastEvent.getOffset()-library.maxLength());
			length = lastEvent.getLength()+library.maxLength();
			highlight();
		}

	}


}
