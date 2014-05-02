importPackage(Packages.gc.server.com);
importPackage(Packages.gc.server.util);
importPackage(Packages.gc.server.http);
importPackage(Packages.org.apache.http);
importPackage(Packages.org.apache.http.entity);
importPackage(Packages.org.apache.http.nio.entity);
importPackage(Packages.connectivity.httpserver);
importPackage(Packages.gc.server.database);
importPackage(Packages.java.nio.file);
importPackage(Packages.java.io);
importPackage(Packages.java.lang);
importPackage(Packages.connectivity.utility);


/*
	autoupload.js
	Run this script to automaticly upload images from the <upload> folder into the server
	- by Leif Andreas Rudlang
*/

// add functions to console


	MainFrame.addFunction("","autoupload",null);

	MainFrame.addFunction("autoupload","start", new Runnable(){	
		run: function(){
			start();
		}
	});

		MainFrame.addFunction("autoupload","stop", new Runnable(){	
		run: function(){
			stop();
		}
	});



// reference required tables
var running = false;
var images = DBHandler.get("images");
var category = DBHandler.get("category");
var metadata_table = DBHandler.get("metadata");
var key = null;


/** Start autoupload
*/
function start(){

	if(running){
		MainFrame.error("Autoupload already running");
		return;
	}
	
	running = true;
	
	var r = new Runnable(){

		run: function(){
			eventListener();
		}
	}	

	var tr = new Thread(r);
	tr.start();
}


/** Stop autoupload
*/
function stop(){

	if(running && key!=null){
	
		running = false;
		key.cancel();
		
		MainFrame.print("Autoupload cancelled");
	}else{
		MainFrame.error("No autoupload process running");
	}
	
}


/** Upload a image
*/
function upload(file,folder){


			var location = file.getPath();
			var imagename = file.getName();	
			var category_id = "0";
	
			var filetype = getFileType(imagename).toLowerCase();			
			
			if(!isValid(filetype)){
				MainFrame.print("Format not supported: "+imagename);
				file.delete();
				return;
			}

			MainFrame.print("attempting to upload: "+imagename);	

			var data = Utility.readBytes(location);

			DBHandler.beginTransaction();
			
			images.update("create");

			var imageid = images.query("uuid")[0][0];
			
			var imagepath = "images//"+imageid+"."+filetype;
			var date = getDateTime();
			var thumbnail = "thumbnails//"+imageid+"."+filetype;
				
			images.update("update",imagename,imagepath,thumbnail,category_id,date,folder,imageid);		
			
			
			file.renameTo(new File(Util.getAssetsLocation()+"webroot/",imagepath));
			//handler.saveFile(imagepath, data);
			handler.createThumbnail(128,128,data,thumbnail);
			
			DBHandler.commitTransaction();
				
			var metadata = new Metadata(Util.getAssetsLocation()+"webroot//"+imagepath);
			metadata_table.update("create",imageid,metadata.getSearchData());
			
			MainFrame.print("Uploaded: "+imagename);
}

/** Run a scan for existing files in the <upload> folder
*/
function primaryScan(path){

		var content = path.listFiles();
		

		for(var i = 0; i < content.length; i++){
		
			var file = content[i];	
			scan(file,"");
	}


}


function eventListener(){


	var location = Util.getAssetsLocation()+"upload";

	var f = new File(location);

	MainFrame.print("Autoupload started at: "+location);

	// run the primary scan
	primaryScan(f);

	// listen for changes
	var dir = f.toPath();
	var watcher = FileSystems.getDefault().newWatchService();
	key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
						   
	while(running){


		var pollEvents = key.pollEvents();
	
	
		for (var i = 0; i < pollEvents.size(); i++) {
	
		
			var event = pollEvents.get(i);
	
			var kind = event.kind();

			if (kind == StandardWatchEventKinds.OVERFLOW) {
				continue;
			}

			var filename = event.context();	

			var file = new File(location,filename);
			scan(file,"");
   }
	
    var valid = key.reset();
	
    if (!valid) {
		MainFrame.print("Autoupload ended unexpectedly");
        break;
    }
}

}


function scan(file,folder){

	if(file.isFile()){
		upload(file,folder);
		return;
	}
	
	if(file.isDirectory()){
	
		var dir = file.listFiles();
	
		for(var i = 0; i < dir.length; i++){
			scan(dir[i],folder+file.getName()+"/");
		}

		try{
			file.delete();
		}catch(err){
			MainFrame.error("Autoupload: Error removing folder");
		}			
	}


}


// UTILITY FUNCTIONS

/** Return properly formatted date and time
*/
function getDateTime() {
    var now     = new Date(); 
    var year    = now.getFullYear();
    var month   = now.getMonth()+1; 
    var day     = now.getDate();
    var hour    = now.getHours();
    var minute  = now.getMinutes();
    var second  = now.getSeconds(); 
    if(month.toString().length == 1) {
        var month = '0'+month;
    }
    if(day.toString().length == 1) {
        var day = '0'+day;
    }   
    if(hour.toString().length == 1) {
        var hour = '0'+hour;
    }
    if(minute.toString().length == 1) {
        var minute = '0'+minute;
    }
    if(second.toString().length == 1) {
        var second = '0'+second;
    }   
    var dateTime = year+'/'+month+'/'+day+' '+hour+':'+minute+':'+second;   
    return dateTime;
}


/** Return file suffix
*/
function getFileType(fname){
	var ftname = String(fname);
	return ftname.substr((~-ftname.lastIndexOf(".") >>> 0) + 2);
}


/** Return true if the filename is supported
*/
function isValid(fname){
	return fname=="jpg" || fname=="jpeg" || fname=="png" || fname=="gif" || fname=="bmp";
}

