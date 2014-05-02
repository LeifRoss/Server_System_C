package gc.server.gui.views;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import gc.server.com.MainFrame;
import gc.server.gui.GCView;
import gc.server.util.Util;

public class GCTabbedView extends GCView{

	public static final Icon ICON_EDIT = new ImageIcon(Util.getAssetsLocation()+"graphics//edit.png");
	public static final Icon ICON_CLOSE_REG = new ImageIcon(Util.getAssetsLocation()+"graphics//close_reg.png");
	public static final Icon ICON_CLOSE_OVER = new ImageIcon(Util.getAssetsLocation()+"graphics//close_over.png");
	
	private JTabbedPane pane;
	private ArrayList<GCView> forms;
	
	public GCTabbedView(String id){
		super(id, new JTabbedPane());
	
		pane = (JTabbedPane)this.getContainer();
		forms = new ArrayList<GCView>();				
	}
	
	
	@Override
	public void add(GCView in, String title){
				
		addTab(in, title);		
	}
	
	
	public void addTab(GCView view, final String title) {

		
		if(ICON_EDIT == null){
			MainFrame.print("NULL ICON");
		}
		
		final Container c = view.getContainer();
		
		// Add the tab to the pane without any label
		pane.addTab(null, c);
		int pos = pane.indexOfComponent(c);

		// Create a FlowLayout that will space things 5px apart
		FlowLayout f = new FlowLayout(FlowLayout.CENTER, 5, 0);

		// Make a small JPanel with the layout and make it non-opaque
		JPanel pnlTab = new JPanel(f);
		pnlTab.setOpaque(false);

		// Add a JLabel with title and the left-side tab icon
		JLabel lblTitle = new JLabel(title);
		lblTitle.setIcon(ICON_EDIT);

		// Create a JButton for the close tab button
		JButton btnClose = new JButton();
		btnClose.setOpaque(false);

		// Configure icon and rollover icon for button
		btnClose.setRolloverIcon(ICON_CLOSE_OVER);
		btnClose.setRolloverEnabled(true);
		btnClose.setIcon(ICON_CLOSE_REG);

		// Set border null so the button doesn't make the tab too big
		btnClose.setBorder(null);

		// Make sure the button can't get focus, otherwise it looks funny
		btnClose.setFocusable(false);

		// Put the panel together
		pnlTab.add(lblTitle);
		pnlTab.add(btnClose);

		// Add a thin border to keep the image below the top edge of the tab
		// when the tab is selected
		pnlTab.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));

		// Now assign the component for the tab
		pane.setTabComponentAt(pos, pnlTab);

		// Add the listener that removes the tab
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				forms.remove(pane.indexOfComponent(c));
				pane.remove(c);
			}
		};
		btnClose.addActionListener(listener);

		// Optionally bring the new tab to the front
		pane.setSelectedComponent(c);
		forms.add(view);
	}



	public int getIndex(){
		return pane.getSelectedIndex();
	}
			
	
}
