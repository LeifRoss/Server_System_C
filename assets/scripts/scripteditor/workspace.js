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
importPackage(Packages.javax.swing.tree);

exports.getWorkspace = function(){

	var tree = new javax.swing.JTree();

	var path = Util.getAssetsLocation()+"scripts/";
	var f = new File(path);
	var root = DefaultMutableTreeNode("assets");
	
	fillTree(root,f);
	
	tree.setModel(new DefaultTreeModel(root));
	
	return tree;
};



function fillTree(node,file){


	var next_node = DefaultMutableTreeNode(file.getName());
	node.add(next_node);
	
	if(file.isDirectory()){
	
		var children = file.listFiles();
	
		for(var i = 0; i < children.length; i++){		
			fillTree(next_node,children[i])
		}
	}


}