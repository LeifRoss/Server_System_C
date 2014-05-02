importPackage(Packages.gc.server.com);
importPackage(Packages.gc.server.util);
importPackage(Packages.java.nio.file);
importPackage(Packages.java.io);
importPackage(Packages.connectivity.utility);
importPackage(Packages.gc.server.gui);
importPackage(Packages.gc.server.gui.views);
importPackage(Packages.java.awt);
importPackage(Packages.java.lang);
importPackage(Packages.javax.swing);
importPackage(Packages.javax.swing.event);
importPackage(Packages.java.awt.event);


var wLibrary = require("scripteditor/workspace.js");
var sLibrary = require("scripteditor/library.js");

var syntaxLibrary = sLibrary.getLibrary();

var path = Util.getAssetsLocation()+MainFrame.getScriptEnv().getArgs();
var frame = GUIFactory.frame("scripteditor","Script Editor");
var tabs = new GCTabbedView("scripteditor.tabs");
var workspace = wLibrary.getWorkspace();

setup(path);
createListener();

frame.setVisible(true);
frame.pack();


	function setup(filepath){

	// center area
			var panel = GUIFactory.view("scripteditor.panel");
			panel.setLayout(new BorderLayout());
			
			
			var scrollpane = GUIFactory.scrollpane("scripteditor.wspace.scroll",new GCView("scripteditor.workspace",workspace));
			
			var splitpane = GUIFactory.splitpane("scripteditor.split",0.25,true);	
			splitpane.add(scrollpane);
			splitpane.add(tabs);
			panel.add(splitpane,BorderLayout.CENTER);
			
			
			
	// top bar
			var toppanel = GUIFactory.view("scripteditor.toppanel");
			toppanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			toppanel.setRaisedBorder();
		
			var savebutton = GUIFactory.button("scripteditor.savebutton","Save","")
			savebutton.addListener(new Runnable(){ 
				run: function(){
						save();
					}
			});
			toppanel.add(savebutton);

			
			var newbutton = GUIFactory.button("scripteditor.savebutton","New","")
			newbutton.addListener(new Runnable(){ 
				run: function(){
						
						var fpath = "scripts//"+JOptionPane.showInputDialog("New Script");
						
						if(fpath.equals("scripts//")){
							return;
						}
						
						var f = new File(Util.getAssetsLocation()+fpath);
						
						if(f.exists()){
						
							if( JOptionPane.showConfirmDialog(null, "File already exists, Overwrite?") != JOptionPane.OK_OPTION){
								return;
							}	
						}
						
						Utility.writeFile(f.getPath(),"New Script..");
						
						createEditor(fpath);
					}
			});
			toppanel.add(newbutton);
			
			
			panel.add(toppanel,BorderLayout.NORTH);

			
			frame.add(panel);
	}
	

	function createEditor(fpath){
				
				
				var f = new File(Util.getAssetsLocation()+fpath);
				
				var panel = GUIFactory.view("scripteditor.panel");
				panel.setLayout(new BorderLayout());	
				var textarea = new ScriptEditor(syntaxLibrary,Utility.readFile(f.getPath()));
				var gctextarea = new GCComponent("scripteditor.scripteditor.area",textarea);
				panel.add(GUIFactory.scrollpane("scripteditor.scrollpane",gctextarea),BorderLayout.CENTER);

				textarea.highlightAll();
				
				tabs.addTab(panel,f.getName());	
	}
	
	
	
	
	var selected = null;
	var selected_path = null;
	
	function createListener(){
	
		workspace.addTreeSelectionListener(new TreeSelectionListener() {
		
			valueChanged: function(e){
				
				selected = workspace.getLastSelectedPathComponent();			
				
				if(selected == null){
					return;
				}
		
				var tp = e.getPath();
		
				selected_path = "";
		
				for(var i = 1; i < tp.getPathCount(); i++){
					
					if(i!=1){
					selected_path+="//";
					}
					
					selected_path+=tp.getPathComponent(i);
				}
			
			}
		
		
		
		});
	
		workspace.addMouseListener(new MouseListener(){
	
		mouseClicked: function(e){
	
			if(e.getClickCount() == 2){
		
				if(selected == null){
					return;
				}
		
				var f = new File(Util.getAssetsLocation()+selected_path);
		
				if(f.isFile()){
					createEditor(selected_path);
				}	
		
			}
		}
		});
	
	
	
	}
	
	
	function save(){
	
		var idx = tabs.getIndex();		
		
		var f = new File(path);
	
		if(!f.exists()){
			//f.mkdirs();
		}
	
		var source = textarea.getText();	
		//Utility.writeFile(path, source);		
	}
	
	