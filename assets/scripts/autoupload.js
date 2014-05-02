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


var images = DBHandler.get("images");
var category = DBHandler.get("category");
var metadata_table = DBHandler.get("metadata");


var r = new Runnable(){

	run: function(){
		eventListener();
	}
}

var tr = new Thread(r);
tr.start();



function upload(imagename, category_id, data){

			DBHandler.beginTransaction();
			
			images.update("create");

			var imageid = images.query("uuid")[0][0];
			
			var imagepath = "images//"+imageid+"."+getFileType(imagename);
			var date = getDateTime();
			var thumbnail = "thumbnails//"+imageid+"."+getFileType(imagename);
			var folder = "";	
				
			images.update("update",imagename,imagepath,thumbnail,category_id,date,folder,imageid);		
			handler.saveFile(imagepath, data);
			handler.createThumbnail(128,128,data,thumbnail);
			
			DBHandler.commitTransaction();
				
			var metadata = new Metadata(Util.getAssetsLocation()+"webroot//"+imagepath);
			metadata_table.update("create",imageid,metadata.getSearchData());
}


function primaryScan(path){


		var content = path.listFiles();
		

		for(var i = 0; i < content.length; i++){
		
			var file = content[i];
			
		
			var location = file.getPath();
			var filename = file.getName();
		
			MainFrame.print("attempting to upload: "+filename);
		
			var data = Utility.readBytes(location);
			upload(filename,"0",data);
			file.delete();
		
			MainFrame.print("Uploaded: "+filename);
	}


}


function eventListener(){


var location = Util.getAssetsLocation()+"upload";

var f = new File(location);

MainFrame.print("Autoupload started at: "+location);

primaryScan(f);

var dir = f.toPath();

var watcher = FileSystems.getDefault().newWatchService();


var key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);


						   
while(true){


	var pollEvents = key.pollEvents();
	
	
    for (var i = 0; i < pollEvents.size(); i++) {
	
		
		var event = pollEvents.get(i);
	
        var kind = event.kind();

        if (kind == StandardWatchEventKinds.OVERFLOW) {
            continue;
        }

		var filename = event.context();	

		var file = new File(location,filename);
		
		MainFrame.print("attempting to upload: "+location+"//"+filename);
		
		var data = Utility.readBytes(location+"//"+filename);
		upload(filename,"0",data);
		file.delete();
		

        MainFrame.print("Uploaded: "+filename.toString());
   }

	
    var valid = key.reset();
	
    if (!valid) {
		MainFrame.print("Autoupload ended unexpectedly");
        break;
    }
}

}



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

function getFileType(fname){

var ftname = String(fname);

return ftname.substr((~-ftname.lastIndexOf(".") >>> 0) + 2);
}

