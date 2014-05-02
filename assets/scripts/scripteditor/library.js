importPackage(Packages.gc.server.com);
importPackage(Packages.gc.server.util);
importPackage(Packages.java.nio.file);
importPackage(Packages.java.io);
importPackage(Packages.java.awt);
importPackage(Packages.connectivity.utility);



exports.getLibrary = function(){

	var library = new SyntaxLibrary();
	
	

		library.createGroup("string", Color.GRAY);
		library.createGroup("comment", Color.GREEN);
		library.createGroup("reserved", Color.MAGENTA);
		library.createGroup("std", Color.BLACK);
		library.createGroup("local", Color.BLUE);

		library.addGroup("\"","\"","string");
		library.addGroup("\'","\'","string");
		library.addGroup("//","\n", "comment");
		library.addGroup("/*","*/", "comment");
		
		library.addReserved("var", "reserved");
		library.addReserved("new", "reserved");
		library.addReserved("for", "reserved");
		library.addReserved("while", "reserved");
		library.addReserved("do", "reserved");
		library.addReserved("switch", "reserved");
		library.addReserved("importPackage", "reserved");
		library.addReserved("true", "reserved");
		library.addReserved("false", "reserved");
		library.addReserved("null", "reserved");
		library.addReserved("function", "reserved");
		library.addReserved("return", "reserved");
		library.addReserved("continue", "reserved");
		library.addReserved("break", "reserved");
		library.addReserved("try", "reserved");
		library.addReserved("catch", "reserved");
		
		library.addReserved("case","reserved");
		library.addReserved("default","reserved");
		
		library.addReserved("]","std");
		library.addReserved("[","std");
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
		

		
	
	return library;
};


