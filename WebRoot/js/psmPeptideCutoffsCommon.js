
//   psmPeptideCutoffsCommon.js


//  Javascript:  Common code for processing the Cutoffs on the page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";



//Constructor

var CutoffProcessingCommonCode = function() {

			
//			_query_json_field_Contents: Object
//				cutoffs: Object
//					searches: Object
//						128: Object			
//							peptideCutoffValues: Object
//								238: Object
//									id: 238
//									value: "0.01"
//							psmCutoffValues: Object
//								384: Object
//									id: 384
//									value: "0.01"
//							searchId: 128
//				linkTypes: null
//				mods: null
			
			

	//  function called to put cutoffs on the page, update the input fields with the values in the object
	
	//  params is normally { cutoffs : _query_json_field_Contents.cutoffs }

	this.putCutoffsOnThePage = function( params ) {

		var cutoffs = params.cutoffs;
		
		var inputCutoffSearches = cutoffs.searches;

		var inputCutoffSearchIdArray = Object.keys( inputCutoffSearches );
		
		for ( var cutoffSearchIdIndex = 0; cutoffSearchIdIndex < inputCutoffSearchIdArray.length; cutoffSearchIdIndex++ ) {

			var inputCutoffSearchId = inputCutoffSearchIdArray[ cutoffSearchIdIndex ];

			var inputCutoffSearchEntry = inputCutoffSearches[ inputCutoffSearchId ];


			//  put PSM cutoff values

			var inputCutoffValuesPsm = inputCutoffSearchEntry.psmCutoffValues;
			
			_putCutoffsOnThePagePerPsmPeptideType( { inputCutoffValues : inputCutoffValuesPsm } );
			

			//  put Peptide cutoff values

			var inputCutoffValuesPeptide = inputCutoffSearchEntry.peptideCutoffValues;
			
			_putCutoffsOnThePagePerPsmPeptideType( { inputCutoffValues : inputCutoffValuesPeptide } );
		}

	};
	
	///////////////
	
	//   Internal function called by object function  this.putCutoffsOnThePage()
	
	
	var _putCutoffsOnThePagePerPsmPeptideType = function( params ) {
		
		var inputCutoffValues = params.inputCutoffValues;

		var inputCutoffAnnotationIdArray = Object.keys( inputCutoffValues );

		for ( var cutoffAnnotationIdIndex = 0; cutoffAnnotationIdIndex < inputCutoffAnnotationIdArray.length; cutoffAnnotationIdIndex++ ) {
			
			var inputCutoffAnnotationId = inputCutoffAnnotationIdArray[ cutoffAnnotationIdIndex ];
			
			var cutoffAnnotationData = inputCutoffValues[ inputCutoffAnnotationId ];
			
			var cutoffAnnotationDataIdFromObj = cutoffAnnotationData.id;
			
			var inputCutoffValueEntryValue = cutoffAnnotationData.value;

			
			var inputFieldId = "annotation_cutoff_input_field_ann_id_" + cutoffAnnotationDataIdFromObj;
			
			var $inputField = $("#" + inputFieldId);

			$inputField.val( inputCutoffValueEntryValue );
			
			var $annotation_entry_root_tr_jq =  $inputField.closest(".annotation_entry_root_tr_jq");
			
//			$annotation_entry_root_tr_jq.show();
			

			var linkId = "add_annotation_cutoff_link_ann_id_" + cutoffAnnotationDataIdFromObj;
			
			var $link = $("#" + linkId );
					
			var $linkEnclosure = $link.closest(".add_annotation_link_enclosing_div_jq");
			
//			$linkEnclosure.hide();
		}
	};
	
	
	
	
	
	
	
	
	

	//  function called to get cutoffs from the page, from the user input fields
	
	//  Returns cutoffs by search id ("cutoffsBySearchId") and in flat format ("cutoffsByAnnotationId") ( currently all web services expect the by search id format )
	
	//   "cutoffsBySearchId" object is the old "outputCutoffs" standard value
	
	//  Returns object of format
	
//	{
//		cutoffsBySearchId : { 
//			searches : 
//			{
//				"<searchId>" : 
//				{	"searchId" : <searchId>,
//					"psmCutoffValues" :
//						{"<annotationId>" :
//							{ "id" : <annotationId>, "value" : "0.01001" } 
//						},
//					"peptideCutoffValues" :
//						{"<annotationId>":
//						{"id":<annotationId>,"value":"0.01001"} 
//				}
//			}
//		},
//		cutoffsByAnnotationId :
//			[
//			 	{ "id" : <annotationId>, "value" : "0.01001" }, ...
//			]
//		
//	}
	
	//  WAS  params is normally { cutoffs : _query_json_field_Contents.cutoffs }

	this.getCutoffsFromThePage = function( params ) {


		var outputCutoffsBySearchId = { searches : {} };

		var outputCutoffsByAnnotationId = [];
		
		var output_FieldDataFailedValidation = false;

		var getCutoffsPerPsmPeptideTypeParams;
		
		var _getCutoffsPerPsmPeptideTypeResult; 

		
		//  Get annotation data for PSM
		
		getCutoffsPerPsmPeptideTypeParams = {

				psmPeptideCutoffsObjKey : "psmCutoffValues",
				psmPeptideCutoffs_CSS_Class : "psm_annotation_cutoff_input_field_jq",

				outputCutoffsBySearchId : outputCutoffsBySearchId,
				outputCutoffsByAnnotationId : outputCutoffsByAnnotationId
		};
		
		_getCutoffsPerPsmPeptideTypeResult = 
			_getCutoffsPerPsmPeptideType( getCutoffsPerPsmPeptideTypeParams );
		
		if ( _getCutoffsPerPsmPeptideTypeResult.output_FieldDataFailedValidation  ) {
		
			output_FieldDataFailedValidation = true;
		}
			
		
		if ( ! output_FieldDataFailedValidation ) {

			//  Get annotation data for Peptide

			getCutoffsPerPsmPeptideTypeParams = {

					psmPeptideCutoffsObjKey : "peptideCutoffValues",
					psmPeptideCutoffs_CSS_Class : "peptide_annotation_cutoff_input_field_jq",

					outputCutoffsBySearchId : outputCutoffsBySearchId,
					outputCutoffsByAnnotationId : outputCutoffsByAnnotationId


			};

			_getCutoffsPerPsmPeptideTypeResult =
				_getCutoffsPerPsmPeptideType( getCutoffsPerPsmPeptideTypeParams );


			if ( _getCutoffsPerPsmPeptideTypeResult.output_FieldDataFailedValidation  ) {

				output_FieldDataFailedValidation = true;
			}
			
		}
		
		var resultObj = { 
				
				cutoffsBySearchId : outputCutoffsBySearchId ,
				cutoffsByAnnotationId : outputCutoffsByAnnotationId,
				
				getCutoffsFromThePageResult_FieldDataFailedValidation : output_FieldDataFailedValidation
		};

		
		//  TODO  TEMP
		
//		alert( "output_FieldDataFailedValidation: " + output_FieldDataFailedValidation );
//		
//		throw "TEMP STOP";
		
		if ( output_FieldDataFailedValidation ) {

			var $element = $("#error_message_cutoff_value_invalid");
		
			showErrorMsg( $element );
		}
		
		
				
		return resultObj;
	};
	
	
	
	///////////////
	
	//   Internal function called by object function  this.getCutoffsFromThePage( ... )
	
		
	var _getCutoffsPerPsmPeptideType = function( params ) {
		
		var psmPeptideCutoffsObjKey = params.psmPeptideCutoffsObjKey;

		var psmPeptideCutoffs_CSS_Class = params.psmPeptideCutoffs_CSS_Class;
		
		var outputCutoffsBySearchId = params.outputCutoffsBySearchId.searches;
		
		var outputCutoffsByAnnotationId = params.outputCutoffsByAnnotationId;
		
		
		var output_FieldDataFailedValidation = false;
		

		var $annotation_cutoff_input_field_jq_for_psm_or_peptide_type = $( "." + psmPeptideCutoffs_CSS_Class );

		$annotation_cutoff_input_field_jq_for_psm_or_peptide_type.each( function( index, element ) {
			
			//   The processing of input fields is stopped if an input value is not a valid decimal

			var $item = $( this );

//			if ( ! $item.is(':visible')  ) {
//				
//				return;  // EARLY EXIT from processing this input field since hidden
//			}

			var annotationTypeIdString = $item.attr("data-type_id");
			var searchIdString = $item.attr("data-search_id");
			
			var cutoffValue = $item.val();
			
			
			
			var searchId = parseInt( searchIdString, 10 );

			if ( isNaN( searchId ) ) {

				throw "Unable to parse search Id: " + searchIdString;
			}



			///////////////////////////
			
			///   Add output cutoffs for search id here so always create it 
			//          even if only has empty arrays for psm and peptide cutoffs

			//  get by search base object and create if necessary
			
			var outputCutoffsForSearchId = outputCutoffsBySearchId[ searchIdString ];
			
			if ( outputCutoffsForSearchId === undefined ) {
				
				//  not in object so create and add it
				
				outputCutoffsForSearchId = { psmCutoffValues : {}, peptideCutoffValues : {}, searchId : searchId };
				
				outputCutoffsBySearchId[ searchIdString ] = outputCutoffsForSearchId;
			}
			
			
			///////////////////
			
			
			
			//  Check for empty string since empty string does not get sent to the server.

			if ( cutoffValue === "" ) {
				
				return;  // EARLY EXIT from processing this input field since is empty string
			}

			var annotationTypeId = parseInt( annotationTypeIdString, 10 );

			if ( isNaN( annotationTypeId ) ) {

				throw "Unable to parse annotation Type Id: " + annotationTypeIdString;
			}
			

			
			///   Validate the value for the cutoff is a valid decimal number
			
			//     Valid formats are:  
			//			optional leading "-", one or more digits
			//			optional leading "-", one or more digits followed by a decimal point followed by zero or more digits
			//			optional leading "-", zero or more digits followed by a decimal point followed by one or more digits
			
			//  regex expressions
			
//			var validationRegexOneOrMoreDigits = /^-?[0-9]+$/;	//  optional leading "-", one or more digits
//			var validationRegex_Gt_ZeroDigits_DP_GE_ZeroDigits = /^-?[0-9]+\.[0-9]*$/;	//  optional leading "-", one or more digits followed by a decimal point followed by zero or more digits
//			var validationRegex_GE_ZeroDigits_DP_Gt_ZeroDigits = /^-?[0-9]*\.[0-9]+$/;  //  optional leading "-", zero or more digits followed by a decimal point followed by one or more digits	
//			
//			var validationRegexOneOrMoreDigitsResult = validationRegexOneOrMoreDigits.test( cutoffValue );
//			var validationRegex_Gt_ZeroDigits_DP_GE_ZeroDigitsResult = validationRegex_Gt_ZeroDigits_DP_GE_ZeroDigits.test( cutoffValue );
//			var validationRegex_GE_ZeroDigits_DP_Gt_ZeroDigitsResult = validationRegex_GE_ZeroDigits_DP_Gt_ZeroDigits.test( cutoffValue );
//			
//			if ( ( ( ! validationRegexOneOrMoreDigits.test( cutoffValue ) ) 
//					&& ( ! validationRegex_Gt_ZeroDigits_DP_GE_ZeroDigits.test( cutoffValue ) ) 
//					&& ( ! validationRegex_GE_ZeroDigits_DP_Gt_ZeroDigits.test( cutoffValue ) ) ) ) {

			if ( cutoffValue !== "" ) {
				
				// only test for valid cutoff value if not empty string

				if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( cutoffValue ) ) {
					
					//  cutoff value is not a valid decimal number

					if ( ! output_FieldDataFailedValidation ) {

						//  Put focus on first error

						$item.focus();
					}


					output_FieldDataFailedValidation = true;


					//  For now, exit after find first error.  Can change to continue if flag errors visually on the page

					return false;  //  EARLY EXIT of ".each" loop
					//  Stop the loop from within the callback function by returning false.

				}
			}
			
			
			var outputByAnnotationIdCutoffValueEntry = { id: annotationTypeId, value : cutoffValue };
			outputCutoffsByAnnotationId.push( outputByAnnotationIdCutoffValueEntry );
			
			
			
			var outputCutoffsForPsmOrPeptide = outputCutoffsForSearchId[ psmPeptideCutoffsObjKey ];
			

			if ( outputCutoffsForSearchId === undefined ) {
				
				throw "output object does not contain property with key: " + psmPeptideCutoffsObjKey;
			}
			
			outputCutoffsForPsmOrPeptide[ annotationTypeIdString ] = outputByAnnotationIdCutoffValueEntry;
			
			
		} );
		
		
		return { output_FieldDataFailedValidation : output_FieldDataFailedValidation };
		
	};
	
	
	/////////////////////
	
	///   Handle toggling display and hide/clear the annotations
	

	this.showAnnotationInputField = function( params ) {

		//  user clicked link of annotation name to show
		
		var clickedThis = params.clickedThis;
		
		var $clickedThis = $( clickedThis );
		
		var ann_type_id = $clickedThis.attr("data-type_id");
		
		var inputFieldId = "annotation_cutoff_input_field_ann_id_" + ann_type_id;
		
		var $inputField = $("#" + inputFieldId );
		
		var $annotation_entry_root_tr_jq = $inputField.closest(".annotation_entry_root_tr_jq");
		
		$annotation_entry_root_tr_jq.show();
		
		var $linkEnclosure = $clickedThis.closest(".add_annotation_link_enclosing_div_jq");
		
		$linkEnclosure.hide();
	};
	
	///////////

	this.hideAnnotationInputField = function( params ) {
		
		//  user clicked delete icon next to input field for annotation value

		var clickedThis = params.clickedThis;
		
		var $clickedThis = $( clickedThis );

		var ann_type_id = $clickedThis.attr("data-type_id");

		var linkId = "add_annotation_cutoff_link_ann_id_" + ann_type_id;
		
		var $link = $("#" + linkId );
				
		var $linkEnclosure = $link.closest(".add_annotation_link_enclosing_div_jq");
		
		$linkEnclosure.show();

		
		var $annotation_entry_root_tr_jq = $clickedThis.closest(".annotation_entry_root_tr_jq");
		
		$annotation_entry_root_tr_jq.hide();
		
	};
	
	
	
};


//   Instance of class

var cutoffProcessingCommonCode = new CutoffProcessingCommonCode();
