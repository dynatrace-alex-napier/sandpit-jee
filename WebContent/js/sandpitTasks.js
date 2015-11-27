/**
 * 
 */

$(function(){
	console.log("ready sandpit");
	// query server for task list
	var jqxhr = $.getJSON("../js/taskList.json", function(data) {
		$.each(data, function(index, task){
			createTile(task, index);
		});
		  
	}).fail(function() {
		    alert( "Fix that JSON: " );
			  console.log( arguments );
	});
});

/*
 CurvyData creates an object that makes data with curves ;)
 the Param cfg is json like this:
		{
		"base" : 10,				// the default value
		"peaks" : [					// an array of definitions to mess with the data
			{
									// this object creates a peak in the series with a value of 50 using a sine curve
				"start" : 0.3,		// start point of messin
				"end" : 0.5,		// end point of messin
				"peak" : 50			// max value while messin
			},
			{
				"start" : 0.65, 	// this peak is an inverse, ie reduces the series to 0.1 at its lowest point (again as a sine curve)  
				"end" : 0.75, 
				"peak" : 0.1
			}
		]
	}    
 To use this lets assume the data runs from some time point a to b and you want to generate 1440 points (eg one per minute of the day)
 Then we call this likle so
 
 var curvy = new CurvyData({ " assume the json above was used..." });
 var arr = []
 for(var i=0; i<1440; i++){
 	arr.push(curvy.dataPoint(i/1440));
 } 
 
 dow is dayOfWeek
*/
function CurvyData(cfg){

	cfg = cfg || {};
	
	this.base = cfg.base;
	this.dayLoading = cfg.dayLoading;
	var peakArr = [];
	function strFn(){
		return $.sprintf("s: %f, e: %f, peak: %f", this.start, this.end, this.peak)
	}
	$.each(cfg.peaks || [], function(i, peakDef){
		peakArr.push($.extend({idx: i, span: peakDef.end-peakDef.start, toString : strFn}, peakDef));
	});

	// returns the value for the TimeSeries based on the pos.
		// pos is between 0 and 1 inclusive
	this.dataPoint = function(){
	    // time is the fraction of the day that has elapsed, ranges from 0 to 1 and drives the number generated
	    var dt = new Date();
	    var timeFactor = (dt.getMinutes()+dt.getHours()*60)/1440;
	    var dayOfWeek = dt.getDay();	// 0-6	for the day of the week
		
		if( isNaN(this.base) ) return -1;
		var peakDef, value=this.base;
		var dayLoading = this.dayLoading;
		$.each(peakArr, function(i, def){

			// finds the peakDefinition if one exists for this timeFactor
			if( def.start <= timeFactor && def.end >= timeFactor){
				// how much of the day is complete, one full circle is a day.
				var radians = ((timeFactor-def.start)/def.span)*Math.PI*2;
				var range = (def.peak - value) * (dayLoading[dayOfWeek] || 1);
				value += range*(Math.cos(radians+Math.PI)+1)/2;	// slice the pie, the +1 and /2 just line it up so we get the right bit of the curve
					return false;	// end the each loop
			}
		});
		return value;
	}
	this.toString = function(){
		var arr = [$.sprintf("base: %f", this.base)];
		$.each(peakArr, function(idx, def){
			arr.push(def.toString());
		});
		return arr.join(", ");
	}
	
}

function createTile(data, idx){
	console.log($.sprintf("create tile: %s [%d]", data.name, idx));
	data.params = data.params || {};
	 
	// clone the html from #tile-template
	var template = $('#tile-template').html();
	var id = data.name + "-" +idx;
	var name = data.name + " ("+(data.params.Name || "untitled")+")";
	template = template.replace(/@id@/g, id).replace(/@name@/, name);
//	console.log(template);
    $('#tiles').append(template);
    
    var response = new CurvyData(data.response);
    var error = new CurvyData(data.error);
    var volume = new CurvyData(data.volume);

    
    var timeoutId = 0;
    data.start = function(){

    	console.log("executing "+id+" now");
		var params = $.extend({}, this.params, {
			"action" : "run",
			"job" : data.name,
			"Cycle" : Math.round(response.dataPoint()) 			
		});
		if( Math.random() < error.dataPoint()){
			params.throwError = Math.round(Math.random() * 5) + 500; 
		}
		// adjust the Duration based on time of day and day of week
		console.log($.sprintf("job: %s, duration: %f, error: %f", params.job, params.Cycle, params.throwError));
		$.get("../task", params, function(){
			console.log("passed");
		}).fail(function(){
			console.log("failed");    			
		});    	
    	
    	var delay = (60/volume.dataPoint())*1000;
    	
    	console.log("DELAY IS "+delay);
    	var selfie = this;
    	timeoutId = setTimeout(function(){
    		selfie.start();
    	}, delay);
    }
    
    data.stop = function(){
    	if( timeoutId > 0 )
    		clearTimeout(timeoutId);
    }

    var play = $("#play_"+id);
    var pause = $("#pause_"+id);
    var active = $("#active_"+id);
    
    play.click(function(){
    	console.log("play was clicked, run, hide and show pause");    	
    	play.toggle();
    	pause.toggle();
    	data.start();
    });
    
    pause.click(function(){
    	console.log("pause was clicked, suspend, hide and show play");
    	play.toggle();
    	pause.toggle();
    	data.stop();
    });
    
    $("#edit_"+id).click(function(){
    	console.log("Not implemented yet! Pleaes edit the taslList.json");	
    	console.log("display UI to edit the properties");
    
    });
  	$("#copy_"+id).click(function(){
    	console.log("Not implemented yet! Please editthe taskList.json");	
  		console.log("make a clone of this object");
    });
    active.click(function(){
    	console.log("toggle active checkbox, suspend a running task, enable/disable play and pause ");
    	// this is the state after the user action
    	if( this.checked ){
    		//play.click();	dont start it, let the user do that
    	}
    	else{
    		//pause.click();
    		
    	}
		play.prop("disabled", !this.checked);
		pause.prop("disabled", !this.checked);
    });
    // set the initial state
    if( data.active )
    	active.click();

}