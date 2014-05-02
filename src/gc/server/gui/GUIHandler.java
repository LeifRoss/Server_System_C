package gc.server.gui;


import gc.server.com.MainFrame;
import gc.server.gui.views.GCConsole;
import gc.server.gui.views.GCMenuBar;
import gc.server.gui.views.GCServerLog;
import gc.server.util.Util;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * 
 * GCServer GUIHandler
 * 
 * @author Leif Andreas Rudlang
 * @date 24.02.2014
 */
public class GUIHandler {


	private static final String APP_NAME = "GCServer [0.170]";
	private static final String LAF_NIMBUS = "Nimbus";
	private static final String MESSAGE_ON = "ON";
	private static final String MESSAGE_OFF = "OFF";


	private static Image ICON_HOME;
	private static Image ICON_ON;
	private static Image ICON_OFF;

	private static JFrame window;
	private static ActionHandler action_handler;
	private static TrayIcon trayIcon;
	private static PopupMenu popup;

	private static HashMap<String,GCView> views;
	private static HashMap<String,GCComponent> components;

	public static void init(){


		setLookAndFeel(LAF_NIMBUS);

		action_handler = new ActionHandler();

		views = new HashMap<String,GCView>();
		components = new HashMap<String,GCComponent>();

		initWindow();
		initViews();
		initSystemTray();

		window.setVisible(true);
	}


	private static void initWindow(){

		window = new JFrame(APP_NAME + (MainFrame.isDebug() ? " (DEBUG)":""));
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);


		window.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {

				if(MainFrame.getServerHandler().isRunning()){
					window.setVisible(false);

					if(trayIcon!=null){
						trayIcon.displayMessage(APP_NAME, "The server is still running in the background", MessageType.INFO);
					}

				}else{
					MainFrame.shutdown();
				}
			}
		});

		
		window.setMinimumSize(new Dimension(640,400));
		window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		window.setLayout(new BorderLayout());


		try {
			ICON_HOME = ImageIO.read(new File(Util.getAssetsLocation()+"graphics//home_icon64.png"));
			window.setIconImage(ICON_HOME);
		} catch (IOException e1) {
			e1.printStackTrace();
		}


	}


	private static void initViews(){

		// top
		GCMenuBar menubar = new GCMenuBar();
		window.add(menubar.getContainer(), BorderLayout.NORTH);

		// center
		GCConsole console = new GCConsole();
		GCServerLog log = new GCServerLog();


		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,console.getContainer(),log.getContainer());
		splitPane.setOneTouchExpandable(true);
		splitPane.setResizeWeight(0.7); 

		window.add(splitPane, BorderLayout.CENTER);
	}


	public static GCComponent getElementById(String id){

		if(components.containsKey(id)){
			return components.get(id);
		}

		return null;
	}

	public static GCView getViewById(String id){

		if(views.containsKey(id)){
			return views.get(id);
		}

		return null;
	}

	public static void add(GCView in){

		views.put(in.getID(), in);
	}

	public static void add(GCComponent in){

		components.put(in.getID(), in);
	}

	private static void initSystemTray(){


		if(!SystemTray.isSupported()){
			return;
		}


		popup = new PopupMenu();

		addToTray("Open","system open");
		addToTray("Exit","system exit");


		String path_on = Util.getAssetsLocation()+"//graphics//trayicon_on.png";
		String path_off = Util.getAssetsLocation()+"//graphics//trayicon_off.png";

		try {

			ICON_ON = ImageIO.read(new File(path_on));
			ICON_OFF = ImageIO.read(new File(path_off));
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}


		trayIcon = new TrayIcon(ICON_OFF,APP_NAME);
		trayIcon.setPopupMenu(popup);


		try {

			final SystemTray tray = SystemTray.getSystemTray();
			tray.add(trayIcon);

		} catch (AWTException e) {
			e.printStackTrace();
		}


	}

	public static ActionHandler getActionHandler(){
		return action_handler;
	}

	public static JFrame getWindow(){
		return window;
	}

	public static PopupMenu getTray(){
		return popup;
	}

	/**
	 * Adds a item to the tray menu
	 * @param in
	 * @param command
	 */
	public static void addToTray(String in, String command){

		if(getTray()==null){
			return;
		}

		MenuItem item = new MenuItem(in);	
		item.setActionCommand(command);
		item.addActionListener(getActionHandler());
		popup.add(item);
	}


	/**
	 * Set the look-and-feel of the application
	 * @param in
	 */
	public static void setLookAndFeel(String in){

		try{

			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {

				if(in.equals(info.getName())){
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
	}


	public static void setTray_On(){
		trayIcon.setImage(ICON_ON);
	}

	public static void setTray_Off(){
		trayIcon.setImage(ICON_OFF);
	}

	public static void open(){

		if(window!=null){
			window.setVisible(true);
		}
	}

	public static void close(){

		if(window!=null){
			window.setVisible(false);
		}
	}


	public static Image getIcon(){
		return ICON_HOME;
	}

	public static void updateStatus(String in){

		switch(in.toUpperCase()){

		case MESSAGE_ON:
			setTray_On();
			break;

		case MESSAGE_OFF:
			setTray_Off();
			break;

		}


	}





}
