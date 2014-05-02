package gc.server.gui;

import gc.server.gui.views.GCTabbedView;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;


/**
 * 
 * GCServer GCView
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class GCView{

	private static final long serialVersionUID = 1L;
	private final String id;
	private Container container;

	public GCView(String id, Container container){
		super();
		this.id = id;
		this.container = container;
		setup();
		container.setVisible(true);
		GUIHandler.add(this);
	}

	public String getID(){
		return id;
	}

	public void setup(){

	}

	public void setColor(int r, int g, int b){
		container.setBackground(new Color(r,g,b));		
	}

	public Container getContainer(){
		return container;
	}

	public void setLayout(LayoutManager in){
		container.setLayout(in);
	}

	public void add(GCComponent in){
		container.add(in.getComponent());
		container.revalidate();
	}


	public void add(GCComponent in, int idx, int idx2){
		container.add(in.getComponent(), idx, idx2);
		container.revalidate();
	}

	public void add(GCComponent in, int idx){
		container.add(in.getComponent(), idx);
		container.revalidate();
	}

	public void add(GCComponent in, Object constraint){
		container.add(in.getComponent(), constraint);
		container.revalidate();
	}

	public void add(GCComponent in, String constraint){
		container.add(in.getComponent(), constraint);
		container.revalidate();
	}

	public void add(GCView in){
		container.add(in.getContainer());
		container.revalidate();
	}

	public void add(GCView in, int idx){
		container.add(in.getContainer(), idx);
		container.revalidate();
	}

	public void add(GCView in, int idx, int idx2){
		container.add(in.getContainer(), idx, idx2);
		container.revalidate();
	}

	public void add(GCView in, Object constraint){
		if(this instanceof GCTabbedView){
			((GCTabbedView)this).addTab(in, (String)constraint);	
		}else{
			container.add(in.getContainer(), constraint);
			container.revalidate();
		}
	}

	public void add(GCView in, String constraint){

		if(this instanceof GCTabbedView){
			((GCTabbedView)this).addTab(in, constraint);	
		}else{
			container.add(in.getContainer(), constraint);
			container.revalidate();
		}
	}


	public void revalidate(){
		container.revalidate();
	}

	public void setVisible(boolean in){
		container.setVisible(in);
	}

	public void pack(){

		if(container instanceof JFrame){
			((JFrame)container).pack();
		}		
	}

	public void setMinSize(int x, int y){
		container.setMinimumSize(new Dimension(x,y));
	}

	public void setRaisedBorder(){

		if(container instanceof JPanel){
			((JPanel) container).setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		}

	}

	public void setLoweredBorder(){

		if(container instanceof JPanel){
			((JPanel) container).setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		}
	}

	public void close(){

		if(container instanceof JFrame){
			((JFrame)container).dispose();
		}				
	}


}


