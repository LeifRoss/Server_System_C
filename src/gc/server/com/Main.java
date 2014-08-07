package gc.server.com;

/**
 * 
 * 
	Copyright [2014] [Leif Andreas Rudlang]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 *
 * @version 0.162
 * @last edit 24.02.2014
 */
public class Main {


	private static String MODE_DEBUG = "debug";
	
	/**
	 * Pass in "debug" as arg0 to enter Debug mode
	 * @param args
	 */
	public static void main(String[] args) {

		if(args.length != 0 && args[0].contains(MODE_DEBUG)){	
			initDEV();		
		}else{				
			initMASTER();
		}
		
	}

	
	private static void initDEV(){

		System.out.println("[GCServer]: DEBUG MODE");
		MainFrame.setDebug(true);
		MainFrame.initMainFrame();
	}

	
	private static void initMASTER(){
		
		MainFrame.initMainFrame();
	}



}
