importPackage(Packages.gc.server.com);
importPackage(Packages.gc.server.gui);
importPackage(Packages.gc.server.gui.views);
importPackage(Packages.java.awt);
importPackage(Packages.java.lang);

	var frame = GUIFactory.frame("settings","Settings");
	frame.setMinSize(200,300);
	
	
	////////// Functions //////////////
	
	MainFrame.addFunction("","settings",null)
	MainFrame.addFunction("settings","close", new Runnable(){	
		run: function(){
			cleanup();
		}
	});
	
	MainFrame.addFunction("settings","save", new Runnable(){	
		run: function(){
			saveFields();
			cleanup();
		}
	});
	
	
	MainFrame.addFunction("settings","reload", new Runnable(){	
		run: function(){
			saveFields();
			cleanup();
			MainFrame.restart();	
		}
	});
	
	
	function fillFields(){	
	
		port.setText(MainFrame.readConfig("port"));
		pool.setText(MainFrame.readConfig("thread_pool_size"));
		timeout.setText(MainFrame.readConfig("timeout"));
		boot.setText(MainFrame.readConfig("boot"));
		override.setText(MainFrame.readConfig("request_handler_override"));
		discovery.setText(MainFrame.readConfig("network_discovery"));
	}
	
		
	function saveFields(){
	
		MainFrame.writeConfig("port",port.getText());
		MainFrame.writeConfig("thread_pool_size",pool.getText());
		MainFrame.writeConfig("timeout",timeout.getText());
		MainFrame.writeConfig("boot",boot.getText());
		MainFrame.writeConfig("request_handler_override",override.getText());
		MainFrame.writeConfig("network_discovery",discovery.getText());
	
		MainFrame.saveConfig();
	}
	
	function cleanup(){	
	
		MainFrame.removeFunction("settings");
		frame.close();
	}
	
	
	/////////////// GUI ///////////////	
	var panel = GUIFactory.view("settings.frame");
	panel.setLayout(new BorderLayout())	
	frame.add(panel);
	
	
	
	var center = GUIFactory.view("settings.frame.center");
	center.setLayout(new GridLayout(6,2));
	panel.add(center, BorderLayout.CENTER);
	
	
	// fields
	var label = GUIFactory.label("settings.frame.center.label.port","Port");
	var port = GUIFactory.textfield("settings.frame.center.port");
	center.add(label,0,0);
	center.add(port,0,1);
	
	label = GUIFactory.label("settings.frame.center.label.threads","#Threads");
	var pool = GUIFactory.textfield("settings.frame.center.thread_pool_size");
	center.add(label,1,0);
	center.add(pool,1,1);
	
	label = GUIFactory.label("settings.frame.center.label.timeout","Timeout");
	var timeout = GUIFactory.textfield("settings.frame.center.timeout");
	center.add(label,2,0);
	center.add(timeout,2,1);
	
	label = GUIFactory.label("settings.frame.center.label.override","Request Handler Override");
	var override = GUIFactory.combobox("settings.frame.center.timeout","true","false");
	center.add(label,3,0);
	center.add(override,3,1);
	
	label = GUIFactory.label("settings.frame.center.label.discovery","UDP Discovery");
	var discovery = GUIFactory.combobox("settings.frame.center.discovery","true","false");
	center.add(label,4,0);
	center.add(discovery,4,1);
	
	label = GUIFactory.label("settings.frame.center.label.boot","Boot Script");
	var boot = GUIFactory.textfield("settings.frame.center.boot");
	center.add(label,5,0);
	center.add(boot,5,1);
	
	
	var bottom = GUIFactory.view("settings.frame.bottom");
	bottom.setLayout(new FlowLayout(FlowLayout.LEFT))
	bottom.setRaisedBorder();
	panel.add(bottom, BorderLayout.SOUTH);
	
	
	
	var reload = GUIFactory.button("settings.frame.bottom.reload","Save and Reload", "settings reload");
	bottom.add(reload);
	var ok = GUIFactory.button("settings.frame.bottom.ok","Save", "settings save");
	bottom.add(ok);
	var cancel = GUIFactory.button("settings.frame.bottom.save","Cancel", "settings close");
	bottom.add(cancel);	
	frame.pack();
			
	
	fillFields();
	

	
	
	
	
	
	
	
	