importPackage(Packages.gc.server.com);
importPackage(Packages.gc.server.database);
importPackage(Packages.gc.server.gui);
importPackage(Packages.gc.server.gui.views);
importPackage(Packages.java.awt);
importPackage(Packages.java.lang);


	MainFrame.addFunction("","dbmanager",null);
	MainFrame.getCommandHandler().execute("include \"dbmanager open;dbmanager_table.js; opens the table\"");
	
	MainFrame.addFunction("dbmanager","open_combo", new Runnable(){	
		run: function(){
			openTable();
		}
	});
	
	MainFrame.addFunction("dbmanager","drop_combo", new Runnable(){	
		run: function(){
			deleteTable();
		}
	});
	
		MainFrame.addFunction("dbmanager","create", new Runnable(){	
		run: function(){
			createTable();
		}
	});
		
	
	MainFrame.addFunction("dbmanager","save",null);
	var frame = GUIFactory.frame("dbmanager","Database Manager");
	frame.setMinSize(100,100);

	var panel = GUIFactory.view("dbmanager.panel");
	panel.setLayout(new BorderLayout());
	frame.add(panel);
	
	
	loadTables();
	frame.pack();
	
function loadTables(){
	
	var tables = DBHandler.getTables();
	var top = GUIFactory.view("dbmanager.panel.top");
	top.setLayout(new FlowLayout());
	

	var tablenames = new Array(tables.length);
	
	for (var i = 0; i < tables.length; i++) {	
		tablenames[i] = tables[i].name();
	}	
	
	var combo = GUIFactory.combobox("dbmanager.combobox",tablenames);
	var open = GUIFactory.button("dbmanager.open","Open","dbmanager open_combo")
	var drop = GUIFactory.button("dbmanager.drop","Drop","dbmanager drop_combo")

	
	
	top.add(combo);
	top.add(open);
	top.add(drop);
	panel.add(top, BorderLayout.CENTER);
	
	
	
	var bottom = GUIFactory.view("dbmanager.panel.bottom");
	bottom.setLayout(new FlowLayout());
	
	var newtablename = GUIFactory.textfield("dbmanager.name");
	var create = GUIFactory.button("dbmanager.create","Create","dbmanager create")
	bottom.add(newtablename);
	bottom.add(create);	
	
	panel.add(bottom, BorderLayout.NORTH);
}
		

function deleteTable(){

	var tablename = GUIHandler.getElementById("dbmanager.combobox").getText();
	DBHandler.get(tablename).dropTable();
	DBHandler.remove(tablename);	
}
	
function openTable(){

	var tablename = GUIHandler.getElementById("dbmanager.combobox").getText();
	MainFrame.getCommandHandler().execute("dbmanager open \""+tablename+"\"");
}	

function createTable(){

		var tablename = GUIHandler.getElementById("dbmanager.name").getText();	
		var table = new DBTable(tablename,"id");	
		MainFrame.getCommandHandler().execute("dbmanager open \""+tablename+"\"");
}	

