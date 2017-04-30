/*
 * copyright Andreas Melcher
 * Apache v2
 * https://github.com/andymel123/TimeCollector
 */

function drawAllowedPath(svgId, paths, config, recPath){
	if(!config)config={};
	var numberOfPaths = paths.length;

	var edgeColor = 	config.edgeColor 	|| "#bbbbbb"
	var edgeColor2 = 	config.edgeColor2	|| "#aaaaaa";
	
	var hPerPath = 		config.hPerPath		|| 50;
	var w = 			config.w 			|| 2000;
	var h = 			config.h 			|| hPerPath * numberOfPaths;
	var nodeRadius  = 	config.nodeRadius 	|| 12;
	var strokeWidth = 	config.strokeWidth 	|| 5;
	var paddingX = 		config.paddingX 	|| 15;
	var paddingY = 		config.paddingY 	|| 15;
	
	paddingY = 50;
	
	var svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
	svg.setAttribute("id", svgId);
	svg.setAttribute("width", "100%");
	svg.setAttribute("height", "100%");
	svg.setAttribute("viewBox", "0 0 " + w + " " + h);
	svg.setAttribute("preserveAspectRatio", "xMinYMin meet");
	svg.setAttribute("class", "svg_allowedGraph");

	var paddingX = nodeRadius + paddingX;
	var paddingY = nodeRadius + paddingY;

	
	var gapY = (h - 2 * paddingY) / (numberOfPaths-1);

	var paddingTestRect1 = buildNode('rect', {
		x: paddingX, y: paddingY, 
		width: w-2*paddingX, height: h-2*paddingY, 
		fill:'none'
		, stroke:'black', strokeWidth:1 
	})
	svg.appendChild(paddingTestRect1);
	
	var paddingTestRect2 = buildNode('rect', {
		x: paddingX, y: paddingY, 
		width: w-2*paddingX, height: (numberOfPaths-1)*gapY, 
		fill:'none'
		, stroke:'red', strokeWidth:1 
	})
	svg.appendChild(paddingTestRect2);
	
	
	

	// remember all nodes and edges to paint them at the 
	// end when I already know 
	var allCircs = {};
	var allEdges = {};

	var yGridIdxExtra = [0];
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
		}
		var lastCirc = null;
		var nextExistingNode = null;
		var lastNodeDidAlreadyExist = false;
		for (var n = 0; n < nodesInThisPath; n++) {
			var nodeHash = path[n];
			
			if(nodeHash=="1299641336"){
				var stop=1;
			}
			
			var nodeId = "n_" + svgId +"_"+nodeHash;
			var circ = allCircs[nodeHash];
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
					
					if(allEdges[edgeHash]){
						console.warn("edge '"+edgeHash+"' is here multiple times! I just add one and skip any other.");
					} 
					else if(lastNodeDidAlreadyExist && lastY==y){
						/* Simply drawing a line as edge between these nodes would
						not be visible as there will be other lines there for sure
						So I draw this line on another y (connected to the nodes
						with vertical lines */
						var line = buildExtraPath(lastCirc, circ, x,y, lastX,lastY, edgeColor2, yGridIdxExtra);
						addTitle(line, edgeHash);
						allEdges[edgeHash] = line;
					}else{
						// this line connects a node to a node of another path
						// add edge
						var line = buildEdge2(lastX,lastY, x,y, edgeColor2);
						addTitle(line, edgeHash);
						allEdges[edgeHash] = line;
					}
					
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
								var existingNode = allCircs[path[a]];
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

					if(allEdges[edgeHash]){
						console.warn("edge '"+edgeHash+"' is here multiple times! I just add one and skip any other.");
					} else {
						// add edge
						x =  lastX + gapX;
						var line = buildEdge(lastX,lastY, x,y, edgeColor); 
						addTitle(line, edgeHash);
						allEdges[edgeHash] = line;
					} 

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
				allCircs[nodeHash] = circ;

				addTitle(circ, nodeHash);

				lastNodeDidAlreadyExist = false;
			}

			lastCirc = circ;
		}
		
	}
	
	var realYLayers = numberOfPaths + yGridIdxExtra[0];
	
	// infos that I need when appending the elements to the svg
	var layout = {
		factor: (numberOfPaths-1) / (realYLayers-1),
		numberOfOrigPaths: numberOfPaths,
		origGapY: gapY,
		paddingY: paddingY
	}
	
	/* I append lines(edges) first, the nodes at the end
	 so that the nodes are on top*/
	
	for(var edgeHash in allEdges){
		var edge = allEdges[edgeHash];
		realAppend(svg, edge, layout);
	}
	
	/* I go through the recorded path and add a class to mark 
	 * nodes and edges that are part of the recorded path */
	var lastCirc = null;
	var lastHash = null;
	var recNodes = recPath.path;
	var recEdges = [];
	var nodeCounters = {};
	var edgeCounters = {};
	for(var i=0; i<recNodes.length; i++){
		var hash = recNodes[i];
		var node = allCircs[hash];

		// count how often this node is visited in this recorded path
		var nodeCounter = nodeCounters[hash];
		if(nodeCounter){
			nodeCounter[0]++;
		}else{
			nodeCounter = nodeCounters[hash] = [1];
		}

		// push one entry to recEdges per visit of this edge
		if(lastHash!=null){
			var edgeHash = lastHash+" -> "+hash;

			if(edgeHash=="1604839423 -> 1512981843"){
				var s=1;
			}
			
			var edgeCounter = edgeCounters[edgeHash];
			if(edgeCounter){
				edgeCounter[0]++;
			}else{
				edgeCounter = edgeCounters[edgeHash] = [1];
			}
			
			
			if(edgeCounter[0]==1){
				recEdges.push(edgeHash);	
			}else{
				
				/* this recEdge hash belongs to a edge that has
				 * been visited multiple times in this recorded path
				 * append an own line for each visit */
				
				var x = 	parseFloat(node.getAttribute('cx'));
				var y = 	parseFloat(node.getAttribute('cy'));
				var lastX = parseFloat(lastCirc.getAttribute('cx'));
				var lastY = parseFloat(lastCirc.getAttribute('cy'));
				
				var line = buildEdge2(x,y, lastX,lastY, edgeColor2);
				realAppend(svg, line, layout);
				
				var edgeHash = edgeCounter[0]+"_"+edgeHash;
				recEdges.push(edgeHash);
				allEdges[edgeHash] = line;
			}
		}
		
		if(nodeCounter[0]==1){
			// only 1 time (not for each rec visit)
			
			// mark as recNode
			var classes = node.getAttribute("class");	
			node.setAttribute("class", classes+" recNode");

			// append to svg
			realAppend(svg, node, layout);	
		}

		lastHash = hash;
		lastCirc = node;

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
		
		if(edgeHash=="1604839423 -> 1512981843"){
			var s=1;
		}
		
		var line = allEdges[edgeHash];
		
		var dataSet = recPath.datasets[i];
		var color = dataSet.backgroundColor;
//		if(recEdges.includes(edgeHash)){
		var classes = line.getAttribute("class");
		line.setAttribute("class", classes+" recEdge");
		line.setAttribute("stroke", color);
		addTitle(line, dataSet.label);
		count++;
//		}
	}
	
	// and another check
	if(count!=e){
		console.error("Found just "+count+" edges from "+e);
	}
	
	// append nodes that are not in the recorded path
	for(var hash in allCircs){
		var nodeCounter = nodeCounters[hash];
		if(!nodeCounter || nodeCounter[0]==0){
			// never visited in recorded path
			realAppend(svg, allCircs[hash], layout);
		}
	}
	
	return svg;
}

function realAppend(svg, elem, layout){

	if(layout && layout.factor!=1){
		/* if factor is != 1 we have to move all elements closer together
		 * because we have to paint additional paths on other y coordinates */

//		{
//			factor: numberOfPaths / realYLayers,
//			numberOfOrigPaths: numberOfPaths,
//			origGapY: gapY
//			paddingY: paddingY
//		}
		
		var gap = layout.origGapY * layout.factor;
		
		var transform = function(origY){
			// multiply by factor (but don't multiply the padding!)
			return (origY-layout.paddingY) * layout.factor +layout.paddingY;
		}
		
		if(elem.getAttribute("extra-layer")){
			// this is an extra path
			var offsetY = layout.paddingY + (layout.numberOfOrigPaths * gap);
			switch(elem.nodeName){
				case "line":{
					var y1 = parseFloat(elem.getAttribute("y1"));
					var y2 = parseFloat(elem.getAttribute("y2"));
					elem.setAttribute("y1", offsetY + y1 * gap);
					elem.setAttribute("y2", offsetY + y2 * gap);
					break;
				}
				case "path":{
					var x1 = parseFloat(elem.getAttribute("nx1"));
					var y1 = parseFloat(elem.getAttribute("ny1"));
					var x2 = parseFloat(elem.getAttribute("nx2"));
					var y2 = parseFloat(elem.getAttribute("ny2"));
					var yIdx = parseInt(elem.getAttribute("y-idx"));
					
					var newY1 = transform(y1);
					var newY2 = transform(y2);
					var yE = offsetY + yIdx * gap
					
					var pathString = 
						 "M"+x1+" "+newY1	// move to first node
						+"L"+x1+" "+yE		// line to extra layer x=firstNode
						+"L"+x2+" "+yE		// line on extra layer x=secondNode
						+"L"+x2+" "+newY2	// line to secondNode
					;
					
					elem.setAttribute("d", pathString);	
					
					console.log(x1+","+y1+" "+x2+","+y2+", factor "+layout.factor+", offsetY "+offsetY+", gap "+gap+" => pathStringh: ", pathString);
					
					break;
				}
				default:{
					console.error("I don't know '"+elem.nodeName+"'");	
				}
			}
		}else{
			switch(elem.nodeName){
				case "line":{
					var y1 = parseFloat(elem.getAttribute("y1"));
					var y2 = parseFloat(elem.getAttribute("y2"));
					elem.setAttribute("y1", transform(y1));
					elem.setAttribute("y2", transform(y2));
					break;
				}
				case "circle":{
					var cy = parseFloat(elem.getAttribute("cy"));
					elem.setAttribute("cy", transform(cy));
					break;
				}
				default:{
					console.error("I don't know '"+elem.nodeName+"'");	
				}
			}
		}
	}
	
	svg.appendChild(elem);
}

function buildEdge(x1,y1, x2,y2, edgeColor){
	return buildNode('line', {
		  x1 : x1
		, y1 : y1
		, x2 : x2
		, y2 : y2
		, stroke : edgeColor
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
		, class : "edge edge2"
	});
}

function buildExtraPath(circ1, circ2, x1,y1, x2,y2, edgeColor, yGridIdxExtra){
	
	// use the actual value of extra y layer as 
	// height (0,1,2,...), this is multiplied later
	var yIdx = yGridIdxExtra[0]++;
	
	return buildNode('path', {
		stroke: edgeColor
		, fill: "none"
		, class: "edge edge2"
		, extraLayer: true
		, nx1: x1
		, ny1: y1
		, nx2: x2
		, ny2: y2
		, yIdx: yIdx
	});
	
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