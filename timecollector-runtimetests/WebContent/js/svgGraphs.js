/*
 * copyright Andreas Melcher
 * Apache v2
 * https://github.com/andymel123/TimeCollector
 */

function drawAllowedPath(svgId, paths, config, recPath){
	if(!config)config={};
	var numberOfPaths = paths.length;

//	var circleColor = 	config.nodeColor 	|| "#b1b1b1";	css!
	var edgeColor = 	config.edgeColor 	|| "#bbbbbb"
	var edgeColor2 = 	config.edgeColor2	|| "#aaaaaa";
	
	var hPerPath = 		config.hperPath		|| 50;
	var w = 			config.w 			|| 2000;
	var h = 			config.h 			|| hPerPath * numberOfPaths;
	var nodeRadius  = 	config.nodeRadius 	|| 12;
	var strokeWidth = 	config.strokeWidth 	|| 5;
	var paddingX = 		config.paddingX 	|| 15;
	var paddingY = 		config.paddingY 	|| 15;
	
	var svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
	svg.setAttribute("id", svgId);
	svg.setAttribute("width", "100%");
	svg.setAttribute("height", "100%");
	svg.setAttribute("viewBox", "0 0 " + w + " " + h);
	svg.setAttribute("preserveAspectRatio", "xMinYMin meet");

//	better per css
//	var backgroundRect = buildNode(svg, 'rect', {
//		width : w,
//		height : h,
//		fill : '#ffffff',
//		"stroke-width" : 1,
//		stroke : "rgb(0,0,0)"
//	});
//	svg.appendChild(backgroundRect);

	var paddingX = nodeRadius + paddingX;
	var paddingY = nodeRadius + paddingY;
	
	var gapY = (h - 2 * paddingY) / (numberOfPaths - 1);

	// remember all nodes and edges to paint them at the 
	// end when I already know 
	var allNodes = {};
	var allEdges = {};

	//console.log("rad: "+nodeRadius+"%, padding: "+paddingX+"%/"+paddingY+"#, gapY: "+gapY+"%");

	//var x = paddingX;
	
	// idx for node x coordinates
//	var xGridIdx = 0;
	// idx for node y coordinates
//	var yGridIdx = 0;
	// 
	var yGridIdxExtra = 0;
	for (var p = 0; p < numberOfPaths; p++) {
		var path = paths[p];
		var nodesInThisPath = path.length;
		var gapX;

		if (p == 0) {
//			// the first path should be the longest
//			// and gapX should fit to the longest path
			gapX = (w - 2 * paddingX) / (nodesInThisPath - 1);
		} else {
			// this is an alternative path

			/*
				I have to this extra logic for a special case
				-> an alternative path 
				-> with length 2 
				-> both nodes already exist
				-> both nodes have the same y (are in the same path)
				
				simply drawing a line as edge between the nodes would
				not be visible as there will be other lines there for 
				sure, so I draw this line on another y and connect 
				with 2 vertical lines
			*/
			
			var firstNodeHash = path[0];
			var lastNodeHash = path[nodesInThisPath - 1];
			var firstCirc = allNodes[firstNodeHash];
			var lastCirc = allNodes[lastNodeHash];
			
			if (firstCirc && lastCirc) {
				// this path starts and ends with already existing nodes
				if (nodesInThisPath == 2) {
					// TODO
					// I have to know in advance that this will happen to calculate 
					// gapY including this 
				} 
			}
		}
		var lastCirc = null;
		var nextExistingNode = null;
		var lastNodeDidAlreadyExist = false;
		for (var n = 0; n < nodesInThisPath; n++) {
			var nodeHash = path[n];
			var nodeId = "n_" + svgId +"_"+nodeHash;
			var circ = allNodes[nodeHash];
			if(circ==nextExistingNode){
				nextExistingNode = null;
			}
			if (circ) {
				// circle was already part of another path
				if (lastCirc) {
					var x = 	parseFloat(circ.getAttribute('cx'));
					var y = 	parseFloat(circ.getAttribute('cy'));
					var lastX = parseFloat(lastCirc.getAttribute('cx'));
					var lastY = parseFloat(lastCirc.getAttribute('cy'));
					var lastNodeHash = lastCirc.getAttribute('nodehash');
					var edgeHash = lastNodeHash+" -> "+nodeHash;
					
					if(lastNodeDidAlreadyExist && lastY==y){
						/*
						I have to add extra logic here
														
						simply drawing a line as edge between the nodes would
						not be visible as there will be other lines there for 
						sure
						
						for now I just draw the line a little bit off 
						TODO 
						but it would be nicer too draw this line on another 
						y and connect with 2 vertical lines.
						The only not so easy part on that is I have to know in 
						advance that this will happen to calculate gapY including it 
						*/
						
//						buildExtraPath(lastCirc, circ)
						y = y-strokeWidth;
						lastY = lastY-strokeWidth;
					}
					
					// this line connects a node to a node of another path
					var line = buildEdge2(lastX,lastY, x,y, edgeColor2);

					svg.appendChild(line);
					// TODO milestone name as title!
					addTitle(line, edgeHash);
					allEdges[edgeHash] = line;
				}
				lastNodeDidAlreadyExist = true;
			} else {
				// this is a node that was not drawn earlier (if have to draw it now)
				var x = paddingX;
				var y = paddingY + (gapY * p);

				if (lastCirc) {
					
					if(p!=0){
						// in alternative paths I calculate the x position dependant on
						// already exitsing nodes this node is in between
						
						if(!nextExistingNode){
							// this is the first not already existing node in this alternative path
							// I start by searching the next already existing node, so I can calc
							// the gapX for the part of the path until the next already existing node
							
							// search
							var nodesTilEnd = 1;
							for (var a = n+1; a < nodesInThisPath; a++) {
								var existingNode = allNodes[path[a]];
								nodesTilEnd++;
								if(existingNode){
									nextExistingNode = existingNode;
									break;
								}
							}
							
							// calc gapX
							if(nextExistingNode){
								// this (part of the) path merges into an already exitsing other path 
								// a later node was found in the path that already exists
								var startX = parseFloat(lastCirc.getAttribute('cx'));
								var endX = parseFloat(nextExistingNode.getAttribute('cx'));
								var distance = endX-startX;
								gapX = distance / nodesTilEnd; 
							}else{
								// this path has an alternative end
							}
						}
						
					}
					
					var lastX = parseFloat(lastCirc.getAttribute('cx'));
					var lastY = parseFloat(lastCirc.getAttribute('cy'));
					var pathOfLast = lastCirc.getAttribute('pathidx');
					var lastNodeHash = lastCirc.getAttribute('nodehash');
					var edgeHash = lastNodeHash+" -> "+nodeHash;
					
					x =  lastX + gapX;
					var line = buildEdge(lastX,lastY, x,y, edgeColor); 
//						buildNode(svg, 'line', {
//						x1 : lastX,
//						y1 : lastY,
//						x2 : x,
//						y2 : y,
//						stroke : edgeColor,
//						strokeWidth : strokeWidth,
//						//style:"stroke:#77de68;stroke-width:2",
//						class : "edge"
//					});
					svg.appendChild(line);
					
					//console.log("", line);
					addTitle(line, edgeHash);
					allEdges[edgeHash] = line;

				} else {
					// this is the first node in this path
					// this is only possible for the first path
					if(p!=0){
						// alternative paths always have to satrt with an existing node
						console.error("The path with index "+p+" starts with a node, that is not part of a former path!");
					}
				}

				circ = buildNode('circle', {
					cx : x,
					cy : y,
					r : nodeRadius,
					id : nodeId,
					pathidx : p,
					class : "node",
					nodehash: nodeHash	
				});
				allNodes[nodeHash] = circ;

				addTitle(circ, nodeHash);

				// console.log("", circ);

				lastNodeDidAlreadyExist = false;
			}

			lastCirc = circ;
			
			//if (n == 0) console.log(nodeHash, circ);
		}
		
	}
	
	/* I append lines(edges) immediately, but the nodes at the end
	 so that the nodes are on top
	 While I append edges I also add an own class to mark nodes
	 that are part of the recorded path
	 I also write edgeHashes to an array to do the same with the 
	 edges afterwards */
	var lastHash = null;
	var recNodes = recPath.path;
	var recEdges = [];
	
	for(var i=0; i<recNodes.length; i++){
		var hash = recNodes[i];
		var node = allNodes[hash];
		if(recNodes.includes(hash)){
			var classes = node.getAttribute("class");
			node.setAttribute("class", classes+" recNode");
			if(lastHash!=null){
				recEdges.push(lastHash+" -> "+hash);
			}
			lastHash = hash;
		}
		svg.appendChild(node);
		delete allNodes[hash]; // remove to know which nodes I already added
	}

	// append nodes that are not in the recorded path
	for(var hash in allNodes){
		svg.appendChild(allNodes[hash]);	
	}
	
	// just a little check
	var n = recNodes.length;
	var e = recEdges.length;
	if(n-1 != e){
		console.error(n + " nodes should have "+n-1+" edges, but there are " +e+" edges!");
	}
	
	// add class to each 'line' svg element that represents 
	// an edge in the recorded path
	var count = 0;
	for(var i=0; i<recEdges.length; i++){
		var edgeHash = recEdges[i];
		var line = allEdges[edgeHash];
		var dataSet = recPath.datasets[i];
		var color = dataSet.backgroundColor;
		if(recEdges.includes(edgeHash)){
			var classes = line.getAttribute("class");
			line.setAttribute("class", classes+" recEdge");
			line.setAttribute("stroke", color);
//			var sw = parseInt(line.getAttribute("stroke-width"));
//			line.setAttribute("stroke-width", sw*2);
			addTitle(line, dataSet.label);
			count++;
		}
	}
	
	// and another check
	if(count!=e){
		console.error("Found just "+count+" edges from "+e);
	}
	
	return svg;
}

function buildEdge(x1,y1, x2,y2, edgeColor){
	return buildNode('line', {
		  x1 : x1
		, y1 : y1
		, x2 : x2
		, y2 : y2
		, stroke : edgeColor
//		strokeWidth : strokeWidth,
		, class : "edge"
	});
}
function buildEdge2(x1,y1, x2,y2, edgeColor){
	return buildNode('line', {
		  x1 : x1
		, y1 : y1
		, x2 : x2
		, y2 : y2
		, stroke : edgeColor
//		strokeWidth : strokeWidth,
		, class : "edge edge2"
	});
}

function buildExtraPath(circ1, circ2, x1,y1, x2,y2, edgeColor){
	
	// TODO path instead of lines
	
	var lines = [
		buildEdge2(x1,yGridIdxExtra,x2,yGridIdxExtra, 	edgeColor),	// main line
		buildEdge2(x1,y1, 			x1,yGridIdxExtra, 	edgeColor),				// circ1 to main line
		buildEdge2(x2,y2, 			x2,yGridIdxExtra, 	edgeColor),				// circ2 to main line
	];
	
	/* this field saves the y idx of additional y layers that I need 
	 * This is such a path placed on an additional layer, so +1 */
	yGridIdxExtra++;
}

function addTitle(node, txt) {
	var title = node.querySelector('title');
	if(!title){
		var title = document.createElementNS(
				"http://www.w3.org/2000/svg", "title");
		node.append(title)
	}
	title.textContent = txt;
}

// inspired by http://stackoverflow.com/a/37411738/7869582
function buildNode(n, v) {
	n = document.createElementNS("http://www.w3.org/2000/svg", n);
	for ( var p in v)
		n.setAttributeNS(null, p.replace(/[A-Z]/g, function(m, p,
				o, s) {
			return "-" + m.toLowerCase();
		}), v[p]);
	return n;
}