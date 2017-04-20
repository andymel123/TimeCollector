function startData(){
	console.log("start data");
	
//	var hashes = [0,1,2,3,4,5,6,7,8,9];
	var hashes = [0,1,2];
	initCharts(hashes);
	
//	var calls = 5;
	var calls = -1;
	var intervalId = window.setInterval(
		function(){
			var msg = generateRandomMsg(
					hashes,		// nr of graphs
					100,		// nr of requests per graph
					25			// nr of values per request
				);
//			console.log("msg", msg);
			handleServerEvent(msg);
			if(calls-- == 0){
				window.clearInterval(intervalId);
			}else{
				console.log("intervals "+calls);
			}
		},
		1000
	);
	
}

function generateRandomMsg(hashes, requestsPerGraph, valuesPerRequest){
	
	var msg = {
		type: "fulldata",
		description: "Desc",
		hashes: [],
		graphData: []
	}
	
	
	for(var g=0; g<hashes.length; g++){

		var hashVal = hashes[g];
		msg.hashes.push(hashVal);

		var graphData = {
			hash: hashVal,
			description: "graph-"+g,
			labels: [],
			datasets: []
		};
		
		for(var v=0; v<valuesPerRequest; v++){
			var dataSet = {
				label: "R"+g+"-"+v, 
				backgroundColor: getRandomColor(), borderColor: "#FFFFFF",
				data: []
			}

			for(var r=0; r<requestsPerGraph; r++){
				if(v==0)graphData.labels.push("R"+r);
				dataSet.data.push(getRandom(20,500));
			}
			graphData.datasets.push(dataSet);
		}

		msg.graphData.push(graphData);
		
	}
	
	return msg;
}

function getRandom(min, max){
	var span = max-min;
	return Math.floor((Math.random() * span) + min)
}

function getRandomColor() {
	// from http://stackoverflow.com/a/1484514/7869582
    var letters = '0123456789ABCDEF';
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}