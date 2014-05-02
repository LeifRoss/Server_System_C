importPackage(Packages.gc.server.com);
importPackage(Packages.gc.server.database);
importPackage(Packages.gc.server.gui);
importPackage(Packages.gc.server.gui.views);
importPackage(Packages.java.awt);
importPackage(Packages.java.lang);

var name = MainFrame.getScriptEnv().getArgs();

var size = 0;
var panel_attr = GUIFactory.view("dbtable.panel.top");

loadTable(name);


function loadTable(pass){


	var table = DBHandler.get(pass);
	var window = GUIFactory.frame("dbtable",table.name());

	var panel = GUIFactory.view("dbtable.panel");
	panel.setLayout(new BorderLayout());
	window.add(panel);

	var structure = table.getColumns();
	size = structure.length;
	
	panel_attr.setLayout(new GridLayout(10,1));
	
	var val = "false";
	
	for(var i = 0; i < size; i++){	
	
		var name = structure[i];
		var tmp = GUIFactory.view("dbmanager.panel.tmp");
		tmp.setLayout(new FlowLayout());
	
		if(i%2==0){
			tmp.setColor(150,150,150);
		}
	
		// Name
		var label = GUIFactory.textfield("dbtable.label."+i);
		label.setText(name);
		tmp.add(label);
	
		// Data type
		var type = GUIFactory.combobox("dbtable.combobox."+i,"INTEGER","TEXT","REAL");
		type.setText(table.getColumnAttribute(name,"INTEGER","TEXT","REAL"));	
		tmp.add(type);
	
		// Not Null
		var not_null = GUIFactory.checkbox("dbtable.not_null."+i,"Not null");
		val = table.getColumnAttribute(name,"not null");
		if(val == "not null"){
			not_null.setText("true");
		}else{
			not_null.setText("false");
		}
		tmp.add(not_null);
	
		// Auto Increment
		var auto_increment = GUIFactory.checkbox("dbtable.auto_increment."+i,"Auto increment");
		val = table.getColumnAttribute(name,"autoincrement");
		if(val == "autoincrement"){
			auto_increment.setText("true");
		}else{
			auto_increment.setText("false");
		}	
		tmp.add(auto_increment);
	
		// Primary Key
		var primary_key = GUIFactory.checkbox("dbtable.primary_key."+i,"Primary Key");
		val = table.getColumnAttribute(name,"primary key");
		if(val == "primary key"){
			primary_key.setText("true");
		}else{
			primary_key.setText("false");
		}
		tmp.add(primary_key);
				
		panel_attr.add(tmp);
		
	}

	
	var panel_bottom = GUIFactory.view("dbtable.panel.bottom");
	panel_bottom.setLayout(new FlowLayout());
	panel_bottom.setRaisedBorder();
	
	var save = GUIFactory.button("dbtable.button.save","Save","dbmanager save "+table.name());
	MainFrame.addFunction("dbmanager save",table.name(), new Runnable(){	
			run: function(){
				saveTable(table.name());
			}
		});	
	panel_bottom.add(save);
	
	var add = GUIFactory.button("dbtable.button.add","Add","dbmanager add");
	MainFrame.addFunction("dbmanager","add", new Runnable(){	
			run: function(){
				addColumn();
			}
		});	
	panel_bottom.add(add);
	
	
	var scroll = GUIFactory.scrollpane("dbmanager.scroll",panel_attr);
	
	panel.add(panel_bottom, BorderLayout.SOUTH);	
	panel.add(scroll, BorderLayout.CENTER);
	window.pack();
}	


function addColumn(){

		var tmp = GUIFactory.view("dbmanager.panel.tmp");
		tmp.setLayout(new FlowLayout());
		
		if(size%2==0){
			tmp.setColor(150,150,150);
		}
		
		var label = GUIFactory.textfield("dbtable.label."+size);
		tmp.add(label);
	
		// Data type
		var type = GUIFactory.combobox("dbtable.combobox."+size,"INTEGER","TEXT","REAL");
    	tmp.add(type);
	
		// Not Null
		var not_null = GUIFactory.checkbox("dbtable.not_null."+size,"Not null");
		tmp.add(not_null);
	
		// Auto Increment
		var auto_increment = GUIFactory.checkbox("dbtable.auto_increment."+size,"Auto increment");
		tmp.add(auto_increment);
	
		// Primary Key
		var primary_key = GUIFactory.checkbox("dbtable.primary_key."+size,"Primary Key");
		tmp.add(primary_key);

		panel_attr.add(tmp);

		size++;
}


function saveTable(name){

var table3 = DBHandler.get(name);
var struct = "";

//var size = table3.getNumberOfColumns();

	for(var i = 0; i < size; i++){
	
		var cname = GUIHandler.getElementById("dbtable.label."+i).getText();
	
	if(cname){
		struct += cname+" "+GUIHandler.getElementById("dbtable.combobox."+i).getText()+" ";
	
		
		
		if(GUIHandler.getElementById("dbtable.not_null."+i).getText()=="true"){
			struct += "not null ";
		}
	
		if(GUIHandler.getElementById("dbtable.auto_increment."+i).getText()=="true"){
			struct += "autoincrement ";
		}
	
		if(GUIHandler.getElementById("dbtable.primary_key."+i).getText()=="true"){
			struct += "primary key";
		}
	
		if(i < size-1){
			struct += ",";
		}
	}	
	
}	

	//MainFrame.print(struct);
//table3.setName(name);	
table3.setStruct(struct);
table3.dropTable();
table3.init();
}

