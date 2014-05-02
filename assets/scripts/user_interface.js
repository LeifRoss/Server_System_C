importPackage(Packages.gc.server.com);
importPackage(Packages.gc.server.gui);
importPackage(Packages.gc.server.gui.views);


	var panel = GUIHandler.getViewById("menubar.bottom");
	var b1 = GUIFactory.button("menubar.bottom.bexit","Exit","system exit");
	panel.add(b1);
	
	
	var meny = GUIHandler.getViewById("menubar.top.file");
	var item = GUIFactory.menuitem("menubar.top.file.settings","Settings","execute \"settings.js\"");
	meny.add(item);
	item = GUIFactory.menuitem("menubar.top.file.settings","Database Manager","execute \"dbmanager/dbmanager.js\"");
	meny.add(item);
	
	
	var edit = GUIHandler.getViewById("menubar.top.edit");
	item = GUIFactory.menuitem("menubar.top.edit.editor","ScriptEditor","execute \"scripteditor/main.js\"");
	edit.add(item);

	
	GUIHandler.addToTray("Settings","execute \"settings.js\"");
	
	
	
	
	
	