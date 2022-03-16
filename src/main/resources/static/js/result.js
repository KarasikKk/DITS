/**
 * TODO:
 * 
 * - Check if this file is needed and delete if not
 */
var percentageComplete = 0.7;
var strokeDashOffsetValue = 100 - (percentageComplete * 100);
var progressBar = $(".js-progress-bar");
progressBar.css("stroke-dashoffset", strokeDashOffsetValue);