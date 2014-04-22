<!DOCTYPE html>
<!--[if lt IE 7 ]> <html class="ie ie6 no-js" lang="en"> <![endif]-->
<!--[if IE 7 ]>    <html class="ie ie7 no-js" lang="en"> <![endif]-->
<!--[if IE 8 ]>    <html class="ie ie8 no-js" lang="en"> <![endif]-->
<!--[if IE 9 ]>    <html class="ie ie9 no-js" lang="en"> <![endif]-->
<!--[if gt IE 9]><!--><html class="no-js" lang="en"><!--<![endif]-->
	<head>
		<meta charset="UTF-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"> 
        <title>AirConditionerSystem</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"> 
        <meta name="description" content="CSS buttons with pseudo-elements" />
        <meta name="keywords" content="css, css3, pseudo, buttons, anchor, before, after, web design" />
        <meta name="author" content="Sergio Camalich for Codrops" />
        <link rel="shortcut icon" href="../favicon.ico"> 
        <link rel="stylesheet" type="text/css" href="css/demo.css" />
        <link rel="stylesheet" type="text/css" href="css/style.css" />
		<link rel="stylesheet" type="text/css" href="ext-all.css" />
        <script type="text/javascript" src="js/ext-all.js"></script>
       <!-- <script type="text/javascript" src="js/array-grid.js"></script>  --> 
        <script type="text/javascript">
             var div = null;
             function control(command){
                 alert(command);
                 Ext.Ajax.request({
                     url: 'control.action?control=' +command;
                     success: function(response, request){
                        if (command== 'on'){
                            alert("the server starts successfully");
                        }
                        else{
                            alert("the server stops now");
                        }
                     }
                 });
             }
             function manage(){
                 div = Ext.get("grid-example");
                 div.show();
                 var reader = Ext.data.JsonReader({
                     root: "serverlist", //objects of threads
                     },
                     [
                       {name: 'threadID'},
                       {name: 'currentstatus'},
                       {name: 'usestatus'},
                       {name: 'temperdirection'},
                       {name: 'roomID'}
                     ]
                 });
                 var proxy = new Ext.data.HttpProxy({
                     url: 'server.action'
                 });
                 var colm = new Ext.grid.ColumnModel({
                     [
                     {id:"threads", header:"threadID", dataIndex:"threadID", width:60, sortable:false},
                     {header:"currentstatus",dataIndex:"currentstatus",width:80},
                     {header:"usestatus",dataIndex:"usestatus",width:80},
                     {header:"temperdirection",dataIndex:"temperdirection",width:80},
                     {header:"roomID",dataIndex:"roomID",width:80}
                     ]
                 });
                 var store = new Ext.data.Store({
                     proxy:proxy,
                     reader:reader
                 });
                 var grid = new Ext.grid.GridPanel({
                     title:"Vlient Working State Monitor",
                     store:store,
                     height:150,
                     width:300,
                     stripeRows:true,
                     cm:colm,
                     autoScroll:true,
                     renderTo:'grid-example'
                 });
             }
        </script>
    </head>
    <body onload="manage()">
        <div class="container">
			<!-- Codrops top bar -->
            <div class="codrops-top">
                <a href="http://touchlike.me">
                    <strong>Made By :</strong>Xinyangli
                </a>
                <span class="right">
                    <a href="https://me.alipay.com/xinyangli">
                        <strong>Donate For me</strong>
                    </a>
                </span>
                <div class="clr"></div>
            </div><!--/ Codrops top bar -->
			<header>
                <h1>AirConditioner <span>System</span></h1>
                <h2>Thunder Li Corp</h2>
                <nav class="codrops-demos">
					<a class="current" href="index.jsp">Main</a>
					<a href="bill.jsp">Bill</a>
                <nav>
			</header>
			<section>
            	<h2 align="center">Server Power</h2>
                <div id="container_buttons">
                    <p>
                        <a href="#" onClick="control('on')" class="a_demo_two">
                            O n
                        </a>
                    </p>
                    <p>
                    	<a href="#" onClick="control('off')" class="a_demo_two">
                        	OFF
                        </a>
                    </p>
                </div>
			</section>
            
            <section>
            	<h2 align="center">Client Monitor</h2>
                <div id="container_monitors">
                	<div id="grid-example"></div>
                </div>
            </section>
        </div>
    </body>
</html>