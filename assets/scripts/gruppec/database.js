importPackage(Packages.gc.server.com);
importPackage(Packages.gc.server.database);

/*
	Database.js
	Initializes and creates the required tables.
	- by Leif Andreas Rudlang	
*/


// tables
var images = new DBTable("images","id integer primary key autoincrement, name text not null, path text not null, thumbnail text, category integer, date text, folder text");
var category = new DBTable("category","id integer primary key autoincrement, name text not null, description text");
var comments = new DBTable("comments","id integer primary key autoincrement, imageid integer, date text, user text, content text");
var tags = new DBTable("tags","id integer primary key autoincrement, imageid integer, data text");
var metadata = new DBTable("metadata","id integer primary key, data text");

// create the tables if they dont already exist
images.createTable();
category.createTable();
comments.createTable();
metadata.createVirtualTable();
tags.createTable();

// prepare queries

// image queries
images.createSQL("get images by category","select id from images where category=?");
images.createSQL("get all","select id, name, path, thumbnail from images");
images.createSQL("get all by category","select id, name, path, thumbnail from images where category=?");
images.createSQL("get image path","select path from images where id=?");
images.createSQL("get thumbnail path","select thumbnail from images where id=?");
images.createSQL("upload","insert into images(id,name,path, thumbnail,category,date) values(?,?,?,?,?,?)");
images.createSQL("create","insert into images(id,name,path,thumbnail,category,date) values(null,'not set','not set','not set','not set','not set')");
images.createSQL("update","update images set name=?,path=?,thumbnail=?,category=?,date=?,folder=? where id =?");
images.createSQL("update late","update images set name=?,path=?,thumbnail=?,category=? where id =?");
images.createSQL("get info","select name, category, path, thumbnail from images where id=?");

images.createSQL("size","select count(*) from images");
images.createSQL("delete","delete from images where id=?");
images.createSQL("uuid","select last_insert_rowid() from images");
images.createSQL("search","select id, name, path, thumbnail from images where name like ?");


// category queries
category.createSQL("get names","select name from category");
category.createSQL("get all","select id, name, description from category");
category.createSQL("create","insert into category(id,name,description) values(null,?,?)");
category.createSQL("delete","delete from category where id=?");


// metadata queries
metadata.createSQL("create","insert into metadata(id,data) values(?,?)");
metadata.createSQL("delete","delete from metadata where id=?");
metadata.createSQL("update","update metadata set data=? where id=?");

metadata.createSQL("search",
"SELECT I.id, I.name, I.path, I.thumbnail "+ 
"FROM images I "+
"WHERE I.name LIKE ? OR I.folder LIKE ? OR I.id IN( "+
"SELECT id FROM metadata WHERE data MATCH ? ) "+
" OR I.id IN( "+
" SELECT imageid FROM tags WHERE data like ? "+
" ) "
);

// Comment queries
comments.createSQL("create","insert into comments(id,imageid,date,user,content) values(null,?,?,?,?)");
comments.createSQL("delete all","delete from comments where imageid=?");
comments.createSQL("delete one","delete from comments where id=?");
comments.createSQL("get","select id, date, user, content from comments where imageid=?");



// Tag queries
tags.createSQL("create","insert into tags(id,imageid,data) values(null,?,?)");
tags.createSQL("delete image","delete from tags where imageid=?")
tags.createSQL("delete tag","delete from tags where id=?");
tags.createSQL("get","select id,data from tags where imageid=?");
tags.createSQL("get all","select distinct data from tags");




