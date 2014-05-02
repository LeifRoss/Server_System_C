importPackage(Packages.gc.server.com);
importPackage(Packages.gc.server.util);
importPackage(Packages.gc.server.http);
importPackage(Packages.org.apache.http);
importPackage(Packages.org.apache.http.entity);
importPackage(Packages.org.apache.http.nio.entity);
importPackage(Packages.connectivity.httpserver);
importPackage(Packages.gc.server.database);


/*
	Request Handler for School Project
*/

var images = DBHandler.get("images");
var category = DBHandler.get("category");
var comments = DBHandler.get("comments");
var tags = DBHandler.get("tags");
var metadata_table = DBHandler.get("metadata");




var handler = new DynamicRequestHandler(){

	onGET: function(request, response, context){
		handler.serveFile(request, response);
	},
	onPOST: function(request, response, context){
	
		response.setStatusCode(HttpStatus.SC_OK);	
		var POST = new PostHandler(request);
		var response_set = false;
		
		if(POST.isset("function")){
		
	
			switch(String(POST.post("function"))){
			
				case "search images":
					searchImages(POST, response);
					response_set = true;
				break;
			
				case "upload":
					handleUpload(POST,response);	
					response_set = true;
				break;
				
				case "update image":
					updateImage(POST,response);	
					response_set = true;
				break;
				
				
				case "delete image":
					handleDelete(POST,response);
					response_set = true;
				break;
				
				case "get all":					
					handleGetAll(POST,response);
					response_set = true;
				break;
				
				case "get comments":
					getComments(POST,response);
					response_set = true;
				break;
				
				case "add comment":
					addComment(POST,response);
					response_set = true;
				break;
			
				case "delete comment":
					deleteComment(POST,response);
					response_set = true;
				break;
			
				case "rotate image":
					rotateImage(POST,response);
					response_set = true;
				break;
			
				case "get metadata":
					getMetadata(POST,response);
					response_set = true;
				break;
					
				case "update metadata":
					updateMetadata(POST,response);
					response_set = true;
				break;
				
				case "delete metadata":
					deleteMetadata(POST,response);
					response_set = true;
				break;	
				
				case "get categories":
					getAllCategories(POST,response);
					response_set = true;
				break;
				
				case "create category":
					createCategory(POST, response);
					response_set = true;
				break;
				
				case "delete category":
					deleteCategory(POST, response);
					response_set = true;
				break;
				
				case "get tags":
					getTags(POST, response);
					response_set = true;
				break;
				
				case "delete tag":
					deleteTag(POST, response);
					response_set = true;
				break;
				
				case "add tag":
					createTag(POST, response);
					response_set = true;
				break;
				
				
			}
	
		}
		
		if(!response_set){
			var entity = new NStringEntity("error", ContentType.create("text/plain", "UTF-8"));	
			response.setEntity(entity);		
		}		
				
				
		POST.clear();
	}

};


function deleteCategory(POST, response){

	if(POST.isset("id")){	
		category.update("delete",POST.post("id"));
		
		var entity = new NStringEntity("true", ContentType.create("text/plain", "UTF-8"));	
		response.setEntity(entity);
		
	}else{
	
		var entity = new NStringEntity("false", ContentType.create("text/plain", "UTF-8"));	
		response.setEntity(entity);
	}

}


function createCategory(POST, response){

	if(POST.isset("name")){
	
		var name = POST.post("name");
		var description = "";
			
		if(POST.isset("description")){
			description = POST.post("description");
		}	
			
		category.update("create",name,description);	
						
		var entity = new NStringEntity("true", ContentType.create("text/plain", "UTF-8"));	
		response.setEntity(entity);	
	}else{
		var entity = new NStringEntity("false", ContentType.create("text/plain", "UTF-8"));	
		response.setEntity(entity);
	}


}




function getTags(POST,response){

	if(POST.isset("id")){

		var id = POST.post("id");
	
		var result = tags.query("get",id);
		var entity = new NStringEntity(DBTable.toCSV(result), ContentType.create("text/plain", "UTF-8"));	
		response.setEntity(entity);	
					
		}else{
		
		var entity = new NStringEntity("error", ContentType.create("text/plain", "UTF-8"));	
		response.setEntity(entity);	
		}

}

function deleteTag(POST,response){

	if(POST.isset("id")){

		var id = POST.post("id");
	
		tags.update("delete tag",id);
		var entity = new NStringEntity("true", ContentType.create("text/plain", "UTF-8"));	
		response.setEntity(entity);	
					
		}else{
		
		var entity = new NStringEntity("false", ContentType.create("text/plain", "UTF-8"));	
		response.setEntity(entity);	
		}

}

function createTag(POST,response){

	if(POST.isset("id") && POST.isset("data")){

		var id = POST.post("id");
		var data = POST.post("data");
	
		tags.update("create",id,data);
		var entity = new NStringEntity("true", ContentType.create("text/plain", "UTF-8"));	
		response.setEntity(entity);	
					
		}else{
		
		var entity = new NStringEntity("false", ContentType.create("text/plain", "UTF-8"));	
		response.setEntity(entity);	
		}

}





function getAllCategories(POST,response){

					var result = category.query("get all");
					var entity = new NStringEntity(DBTable.toCSV(result), ContentType.create("text/plain", "UTF-8"));	
					response.setEntity(entity);	

}



function deleteComment(POST, response){


				try{
				
					if(POST.isset("id")){
					
					var id = POST.post("id");
					comments.update("delete one",id);
					
					var entity = new NStringEntity("true", ContentType.create("text/plain", "UTF-8"));	
					response.setEntity(entity);	
			
					}else{
					
					var entity = new NStringEntity("false", ContentType.create("text/plain", "UTF-8"));	
					response.setEntity(entity);		
					}		
				
				
				}catch(err){
					handleError(err,response);
				}

}


function getComments(POST, response){


				try{
				
					if(POST.isset("id")){
					
					var id = POST.post("id");

					var csv = DBTable.toCSV(comments.query("get",id));
					
					var entity = new NStringEntity(csv, ContentType.create("text/plain", "UTF-8"));	
					response.setEntity(entity);	
			
					}else{
					
					var entity = new NStringEntity("error", ContentType.create("text/plain", "UTF-8"));	
					response.setEntity(entity);		
					}		
				
				
				}catch(err){
					handleError(err,response);
				}

}

function addComment(POST, response){

				try{
				
					if(POST.isset("id") && POST.isset("data") && POST.isset("user")){
					
					var id = POST.post("id");
					var data = POST.post("data");
					var user = POST.post("user");
					var time = getDateTime();
					
					comments.update("create",id,time,user,data);
					
					var entity = new NStringEntity("true", ContentType.create("text/plain", "UTF-8"));	
					response.setEntity(entity);	
			
					}else{
					
					var entity = new NStringEntity("false", ContentType.create("text/plain", "UTF-8"));	
					response.setEntity(entity);		
					}		
				
				
				}catch(err){
					handleError(err,response);
				}

}


// not yet implemented
function rotateImage(POST, response){
				var entity = new NStringEntity("false", ContentType.create("text/plain", "UTF-8"));	
				response.setEntity(entity);		

}


function searchImages(POST, response){

	try{

	if(POST.isset("name")){
	
		var name = POST.post("name");
	
		var search_term = "%"+name+"%";
	
		var result = metadata_table.query("search",search_term,search_term,search_term,search_term);
		
		var entity = new NStringEntity(DBTable.toCSV(result), ContentType.create("text/plain", "UTF-8"));	
		response.setEntity(entity);	
	
	}else{
		
		var entity = new NStringEntity("error", ContentType.create("text/plain", "UTF-8"));	
		response.setEntity(entity);	
	}

	}catch(err){
		handleError(err,response);
	}
	
}


function getMetadata(POST, response){


		if(POST.isset("id")){
			
					try{
					
						var imageid = POST.post("id");
						var imagepath = Util.getAssetsLocation()+"webroot//"+(images.query("get image path",imageid)[0][0]).replace("'","");

						var metadata = new Metadata(imagepath);

						
						var csvstring = metadata.toString();
						var csventity = new NStringEntity(csvstring, ContentType.create("text/plain", "UTF-8"));	
						response.setEntity(csventity);		
				
					}catch(err){
						handleError(err,response);
					}
				
			}else{
				 	
					var entity = new NStringEntity("false", ContentType.create("text/plain", "UTF-8"));	
					response.setEntity(entity);		
		}
}


function updateMetadata(POST, response){


			if(POST.isset("id") && POST.isset("key") && POST.isset("value")){
			
				try{
			
					var imageid = POST.post("id");
					var imagepath = Util.getAssetsLocation()+"webroot//"+(images.query("get image path",imageid)[0][0]).replace("'","");

					var metadata = new Metadata(imagepath);

					metadata.updateTag(POST.post("key"),POST.post("value"));
					metadata.write();

					metadata_table.update("update",metadata.getSearchData(),imageid);
					
					var entity = new NStringEntity("true", ContentType.create("text/plain", "UTF-8"));	
					response.setEntity(entity);		
				
				}catch(err){
					handleError(err,response);
				}
				
			}else{
				 
				var entity = new NStringEntity("false", ContentType.create("text/plain", "UTF-8"));	
				response.setEntity(entity);		
			}
}


function deleteMetadata(POST, response){


			if(POST.isset("id") && POST.isset("key")){
			
				try{
			
					var imageid = POST.post("id");
					var imagepath = Util.getAssetsLocation()+"webroot//"+(images.query("get image path",imageid)[0][0]).replace("'","");

					var metadata = new Metadata(imagepath);

					metadata.removeTag(POST.post("key"));
					metadata.write();
					
					metadata_table.update("update",metadata.getSearchData(),imageid);
					
					var entity = new NStringEntity("true", ContentType.create("text/plain", "UTF-8"));	
					response.setEntity(entity);		
				
				}catch(err){
					handleError(err,response);
				}
				
			}else{
				 
				var entity = new NStringEntity("false", ContentType.create("text/plain", "UTF-8"));	
				response.setEntity(entity);		
			}
}




function handleGetAll(POST,response){

				if(POST.isset("category")){
				
					var category_id = POST.post("category");
					var result = images.query("get all by category",category_id);
					var entity = new NStringEntity(DBTable.toCSV(result), ContentType.create("text/plain", "UTF-8"));	
					response.setEntity(entity);	
				
				}else{	
				
					var result = images.query("get all");
					var entity = new NStringEntity(DBTable.toCSV(result), ContentType.create("text/plain", "UTF-8"));	
					response.setEntity(entity);	
				}

}

function handleUpload(POST,response){

		

		if(POST.hasFile()){
			
			DBHandler.beginTransaction();
			
			images.update("create");

			var imageid = images.query("uuid")[0][0];
			
			var imagename = "unknown";
			var imagepath = "images//"+imageid+"."+getFileType(POST.getFilename());
			var date = getDateTime();
			var category_id = "0";
			var thumbnail = "thumbnails//"+imageid+".jpg";
			var folder = "";
			
			if(POST.isset("name")){
				imagename = POST.post("name");
			}
				
			if(POST.isset("category")){
				category_id = POST.post("category");
			}	
			
			if(POST.isset("folder")){
				folder = POST.post("folder");
			}
	
			images.update("update",imagename,imagepath,thumbnail,category_id,date,folder,imageid);		
			handler.saveFile(imagepath, POST.getFileContent());
			handler.createThumbnail(128,128,POST.getFileContent(),thumbnail);

			var metadata = new Metadata(Util.getAssetsLocation()+"webroot//"+imagepath);
			metadata_table.update("create",imageid,metadata.getSearchData());
			
			DBHandler.commitTransaction();
			
	
			var entity = new NStringEntity("true", ContentType.create("text/plain", "UTF-8"));	
			response.setEntity(entity);	

	}else{
			var entity = new NStringEntity("false", ContentType.create("text/plain", "UTF-8"));	
			response.setEntity(entity);		
	}	
	
}


function updateImage(POST,response){

	
	
	try{
	
			
			if(POST.isset("id")){
			
				var imageid = POST.post("id");
			
			
				var currentInfo = images.query("get info",imageid);
			
				var name = currentInfo[0][0];
				var category = currentInfo[0][1];
				var path = currentInfo[0][2];
				var thumbnail_path = currentInfo[0][3];
			
				
				if(POST.isset("name")){
					name = POST.post("name");
				}
				
				if(POST.isset("category")){
					category = POST.post("category");
				}
				
				if(POST.hasFile()){	
					
					var real_path = Util.getAssetsLocation()+"webroot//"+path;
					
					var metadata_old = new Metadata(real_path).toString();
					handler.saveFile(path, POST.getFileContent());
					
					var metadata_new = new Metadata(real_path);
					metadata_new.importData(metadata_old);
					metadata_new.write();
					
					handler.createThumbnail(128,128,POST.getFileContent(),thumbnail_path);		
				}
										
				images.update("update late",name,path,thumbnail_path,category,imageid);						
			
				var entity = new NStringEntity("true", ContentType.create("text/plain", "UTF-8"));	
				response.setEntity(entity);	
			}else{
			
				var entity = new NStringEntity("error", ContentType.create("text/plain", "UTF-8"));	
				response.setEntity(entity);	
			}
			
	}catch(err){
		handleError(err,response);
	}

}


function handleDelete(POST,response){

	
			if(POST.isset("id")){
			
				var imageid = POST.post("id");
				var imagepath = images.query("get image path",imageid)[0][0];
				var thumbnailpath = images.query("get thumbnail path",imageid)[0][0];
				images.update("delete",imageid);
				handler.deleteFile(imagepath);			
				handler.deleteFile(thumbnailpath);	
				
				metadata_table.update("delete",imageid);
				comments.update("delete",imageid);
				tags.update("delete",imageid);
				
				var entity = new NStringEntity("true", ContentType.create("text/plain", "UTF-8"));	
				response.setEntity(entity);	
				
			}else{
			
				var entity = new NStringEntity("false", ContentType.create("text/plain", "UTF-8"));	
				response.setEntity(entity);		
			}


}


function getFileType(fname){

return fname.substr((~-fname.lastIndexOf(".") >>> 0) + 2);
}

function handleLogin(response, POST) {
	
	// check uname and pw against database, and check for bruteforce
		
	handler.setCookie(response);	
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

function handleError(err,response){

	MainFrame.print("ERROR: "+err.message);
	var entity = new NStringEntity("error", ContentType.create("text/plain", "UTF-8"));	
	response.setEntity(entity);		
	
}

MainFrame.addUniformHandler(handler);

		// MainFrame.addHandler(handler,"/test.js");


		//	response.setStatusCode(HttpStatus.SC_OK);			
			
		//	var entity = new NStringEntity("<html><body><h1>Error</h1></body></html>", ContentType.create("text/html", "UTF-8"));
			
		//	MainFrame.getCommandHandler().execute("console print \"A connection has appeared!\"");
		//	response.setEntity(entity);	
		
		