
//  viewProteinCoverageReport.js

//  Javascript for the viewProteinCoverageReport.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//  Constructor

var ViewProteinCoverageReportPageCode = function() {

	//  function called after all HTML above main table is generated, called from inline script on page

	this.createPartsAboveMainTable = function() {

		try {

			var params = {
					listOfObjectsToPassPsmPeptideCutoffsRootTo : [ ] };

			viewSearchProteinPageCommonCrosslinkLooplinkCoverage.createPartsAboveMainTableSearchProteinPageCommon( params );

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}		
	};
	
		
	///////////////////////
	
	//   Called by "onclick" on HTML element

	this.updatePageForFormParams = function() {
	
		try {

			viewSearchProteinPageCommonCrosslinkLooplinkCoverage.updatePageForFormParams();

		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
		
		
};

//  Instance of class

var viewProteinCoverageReportPageCode = new ViewProteinCoverageReportPageCode();


