//    viewProject_ProjectAdminSection.js

//  Javascript for the project admin section of the page viewProject.jsp

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

// /////////////////////////////////////////

var adminGlobals = {

	projectToMoveSearchesToSelected : false,

	searchCheckboxesCheckedCount : 0,

	project_id : null,

	logged_in_user_id : null,
	
	logged_in_user_access_level_owner_or_better : false
};

// //////////////////////////

// //////////////////////////

var initMaintTitleAndAbstract = function() {


	$("#maint_title_init_button").click(function(eventObject) {

		hideAllErrorMessages();
		
		// var clickThis = this;

		var title = $("#title_span").text();

		$("#maint_title_field").val(title);

		$("#maint_title_div").show();
		$("#title_container_div").hide();
	});

	$("#maint_title_reset_button").click(function(eventObject) {
		
		hideAllErrorMessages();

		// var clickThis = this;

		var title = $("#title_span").text();

		$("#maint_title_field").val(title);

	});

	$("#maint_title_cancel_button").click(function(eventObject) {
		
		hideAllErrorMessages();

		// var clickThis = this;

		$("#maint_title_div").hide();
		$("#title_container_div").show();
	});

	$("#maint_title_save_button").click(function(eventObject) {
		
		hideAllErrorMessages();

		var clickThis = this;

		updateProjectTitle(clickThis, eventObject);
	});

	// ////////// Abstract

	$("#maint_abstract_init_button").click(function(eventObject) {
		
		hideAllErrorMessages();

		// var clickThis = this;

		var abstractText = $("#abstract_span").text();

		$("#maint_abstract_field").val(abstractText);

		$("#maint_abstract_div").show();
		$("#abstract_container_div").hide();
	});

	$("#maint_abstract_reset_button").click(function(eventObject) {
		
		hideAllErrorMessages();

		// var clickThis = this;

		var abstractText = $("#abstract_span").text();

		$("#maint_abstract_field").val(abstractText);

	});

	$("#maint_abstract_cancel_button").click(function(eventObject) {
		
		hideAllErrorMessages();

		// var clickThis = this;

		$("#maint_abstract_div").hide();
		$("#abstract_container_div").show();
	});

	$("#maint_abstract_save_button").click(function(eventObject) {
		
		hideAllErrorMessages();

		var clickThis = this;

		updateProjectAbstract(clickThis, eventObject);
	});

};

// ///////////

var updateProjectTitle = function(clickThis, eventObject) {

	var title = $("#maint_title_field").val();

	if (title === undefined || title === null || title === "") {

//		alert("Title cannot be empty");
		
		
		var $element = $("#error_message_project_title_required");
		
		showErrorMsg( $element );
			
		return;  //  !!!  EARLY EXIT
	}

	if (adminGlobals.project_id === null) {

		throw "Unable to find input field for id 'project_id' ";
	}

	var requestData = {
		title : title,
		projectId : adminGlobals.project_id
	};

	var _URL = contextPathJSVar + "/services/project/updateTitle";

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			updateProjectTitleComplete(requestData, data);
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};

////

var updateProjectTitleComplete = function(requestData, responseData) {

	if (responseData.status) {

		$("#title_span").text(responseData.title);

		$("#maint_title_div").hide();
		$("#title_container_div").show();
		
		var $header_project_title = $("#header_project_title");
		
		if ( $header_project_title.length > 0 ) {
			
			$header_project_title.text( responseData.titleHeaderDisplay );
			
			if ( responseData.title !== responseData.titleHeaderDisplay ) {
			
				//  Update the tool tip
			
				var $header_project_title_link = $("#header_project_title_link");
				
				if ( $header_project_title_link.length > 0 ) {
					
					$header_project_title_link.attr("title", responseData.title );
				}
			}
		}
		
		var $header_current_project_in_drop_down_list = $("#header_current_project_in_drop_down_list");
		
		if ( $header_current_project_in_drop_down_list.length > 0 ) {
			
			$header_current_project_in_drop_down_list.text( responseData.titleHeaderDisplay );
		}
	}
};

// ///////////

var updateProjectAbstract = function(clickThis, eventObject) {

	var abstractText = $("#maint_abstract_field").val();

//	if (abstractText === undefined || abstractText === null
//			|| abstractText === "") {
//
////		alert("Abstract cannot be empty");
//		
//		var $element = $("#error_message_project_abstract_required");
//		
//		showErrorMsg( $element );
//			
//		return;  //  !!!  EARLY EXIT
//	}

	if (adminGlobals.project_id === null) {

		throw "Unable to find input field for id 'project_id' ";
	}

	var requestData = {
		abstractText : abstractText,
		projectId : adminGlobals.project_id
	};

	var _URL = contextPathJSVar + "/services/project/updateAbstract";

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			updateProjectAbstractComplete(requestData, data);
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};

var updateProjectAbstractComplete = function(requestData, responseData) {

	if (responseData.status) {

		$("#abstract_span").text(responseData.abstractText);

		$("#maint_abstract_div").hide();
		$("#abstract_container_div").show();
	}
};


////////////////////////////


var initMaintProjectNotes = function() {

	//  Note Add
	
	
	$("#add_note_init_button").click(function(eventObject) {
		
		hideAllErrorMessages();

		// var clickThis = this;

		$("#add_note_field").val("");
		
		$("#add_note_div").show();
		$("#add_note_button_container_div").hide();
		
		$("#add_note_field").focus();
	});

	$("#add_note_cancel_button").click(function(eventObject) {

		hideAllErrorMessages();
		
		// var clickThis = this;

		$("#add_note_field").val("");

		$("#add_note_div").hide();
		$("#add_note_button_container_div").show();
	});

	$("#add_note_save_button").click(function(eventObject) {
		
		hideAllErrorMessages();

		var clickThis = this;

		addProjectNote(clickThis, eventObject);
	});
	

	//  only process ".note_root_container_div_jq" under "#notes_list_container_div"
	
	var $notes_list_container_div = $("#notes_list_container_div");
	
	$notes_list_container_div.find(".note_root_container_div_jq").each( function( index, element ) {
		
		var $note_root_container_div_jq = $( this );
		
		attachProjectNoteMaintOnClick( $note_root_container_div_jq );
	});

};




// ///////////

var addProjectNote = function(clickThis, eventObject) {

	var note = $("#add_note_field").val();

	if (note === undefined || note === null || note === "") {

//		alert("Note cannot be empty");
		
		var $element = $("#error_message_project_note_required");
		
		showErrorMsg( $element );
			
		return;  //  !!!  EARLY EXIT
	}

	if (adminGlobals.project_id === null) {

		throw "Unable to find input field for id 'project_id' ";
	}

	var requestData = {
			noteText : note,
			projectId : adminGlobals.project_id
	};

	var _URL = contextPathJSVar + "/services/project/addNote";

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			addProjectNoteComplete(requestData, data);
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};

////

var addProjectNoteComplete = function(requestData, responseData) {

	if (responseData.status) {
		
		var $notes_list_container_div = $("#notes_list_container_div");

		var source = $("#notes_template_div").html();

		if ( source === undefined ) {
			throw '$("#notes_template_div").html() === undefined';
		}
		if ( source === null ) {
			throw '$("#notes_template_div").html() === null';
		}
		
		var template = Handlebars.compile(source);

		var context = responseData;
		
		context.noteText = requestData.noteText;

		var html = template(context);

		var note_root_container_div_jq = $(html).appendTo($notes_list_container_div);
		
		attachProjectNoteMaintOnClick( note_root_container_div_jq );
				
		$("#add_note_field").val("");

		$("#add_note_div").hide();
		$("#add_note_button_container_div").show();
		
		addToolTips( $notes_list_container_div );
	}
};


///////////

var attachProjectNoteMaintOnClick = function( $note_root_container_div_jq  ) {

	/////////  Note Maint


	$note_root_container_div_jq.find(".notes_update_button_jq").click(function(eventObject) {

		hideAllErrorMessages();
		
//		var clickThis = this;
		
		var $this = $( this );
		
		var $note_root_container_div_jq_parent = $this.closest(".note_root_container_div_jq");

		var note = $note_root_container_div_jq_parent.find(".notes_text_jq").text();

		$note_root_container_div_jq_parent.find(".note_maint_textarea_jq").val(note);

		$note_root_container_div_jq_parent.find(".note_maint_container_div_jq").show();
		$note_root_container_div_jq_parent.find(".note_display_container_div_jq").hide();
	});

	$note_root_container_div_jq.find(".note_maint_reset_button_jq").click(function(eventObject) {

		hideAllErrorMessages();
		
//		var clickThis = this;

		var $this = $( this );
		
		var $note_root_container_div_jq_parent = $this.closest(".note_root_container_div_jq");


		var note = $note_root_container_div_jq_parent.find(".notes_text_jq").text();

		$note_root_container_div_jq_parent.find(".note_maint_textarea_jq").val(note);
	});

	$note_root_container_div_jq.find(".note_maint_cancel_button_jq").click(function(eventObject) {

		hideAllErrorMessages();
		
//		var clickThis = this;

		var $this = $( this );
		
		var $note_root_container_div_jq_parent = $this.closest(".note_root_container_div_jq");


		$note_root_container_div_jq_parent.find(".note_maint_container_div_jq").hide();
		$note_root_container_div_jq_parent.find(".note_display_container_div_jq").show();
	});

	$note_root_container_div_jq.find(".note_maint_save_button_jq").click(function(eventObject) {

		hideAllErrorMessages();
		
		var clickThis = this;

		updateProjectNote(clickThis, eventObject);
	});
	

	$note_root_container_div_jq.find(".notes_remove_button_jq").click(function(eventObject) {

		hideAllErrorMessages();
		
		var clickThis = this;

		removeProjectNote(clickThis, eventObject);
	});
	
};



/////////////

var updateProjectNote = function(clickThis, eventObject) {


	
	var $clickThis = $( clickThis );
	
	var $note_root_container_div_jq_parent = $clickThis.closest(".note_root_container_div_jq");

	var note = $note_root_container_div_jq_parent.find(".note_maint_textarea_jq").val();

	if ( note === undefined || note === null || note === "" ) {

//		alert("Note cannot be empty");
		
		var $element = $note_root_container_div_jq_parent.find(".error_message_project_note_required_jq");
		
		showErrorMsg( $element );
			
		return;  //  !!!  EARLY EXIT
	}
	
	var noteId = $note_root_container_div_jq_parent.attr("note_id");

	if ( noteId === undefined || noteId === null || noteId === "") {

		throw "Unable to find attribute for id 'noteId' ";
	}

	var requestData = {
			noteText : note,
			noteId : noteId
	};

	var _URL = contextPathJSVar + "/services/project/updateNote";

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			var responseParams = { requestData: requestData, responseData: data, clickThis: clickThis };
			updateProjectNoteComplete( responseParams);
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};

///

var updateProjectNoteComplete = function( params ) {
	
	var requestData =  params.requestData;
	var responseData = params.responseData;
	var clickThis = params.clickThis;
	
	
	if (responseData.status) {
		
		var $clickThis = $( clickThis );
		
		var $note_root_container_div_jq_parent = $clickThis.closest(".note_root_container_div_jq");
		
		$note_root_container_div_jq_parent.find(".notes_text_jq").text( requestData.noteText );

		$note_root_container_div_jq_parent.find(".note_maint_textarea_jq").val("");

		$note_root_container_div_jq_parent.find(".note_maint_container_div_jq").hide();
		$note_root_container_div_jq_parent.find(".note_display_container_div_jq").show();
	}
};



/////////////

//var removeProjectNote = function(clickThis, eventObject) {
//
//
//
//	var $clickThis = $( clickThis );
//
//	var $note_root_container_div_jq_parent = $clickThis.closest(".note_root_container_div_jq");
//
//	var noteId = $note_root_container_div_jq_parent.attr("note_id");
//	
//	
//};



/////////////////

var removeProjectNote = function(clickThis, eventObject) {

	openConfirmRemoveProjectNoteOverlay(clickThis, eventObject);

	return;

};

///////////

var openConfirmRemoveProjectNoteOverlay = function(clickThis, eventObject) {

	var $clickThis = $(clickThis);

	//	get root div for this note
	var $note_root_container_div_jq_parent = $clickThis.closest(".note_root_container_div_jq");

	var noteId = $note_root_container_div_jq_parent.attr("note_id");

	var $delete_note_confirm_button = $("#delete_note_confirm_button");
	$delete_note_confirm_button.data("noteId", noteId);
	
	// Position dialog over clicked delete icon
	
	//  get position of div containing the dialog that is inline in the page
	var $delete_note_overlay_containing_outermost_div_inline_div = $("#delete_note_overlay_containing_outermost_div_inline_div");
	
	var offset__containing_outermost_div_inline_div = $delete_note_overlay_containing_outermost_div_inline_div.offset();
	var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
	
	var offset__ClickedDeleteIcon = $clickThis.offset();
	var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
	
	var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
	
	//  adjust vertical position of dialog 
	
	var $delete_note_overlay_container = $("#delete_note_overlay_container");
	
	var height__delete_note_overlay_container = $delete_note_overlay_container.outerHeight( true /* [includeMargin ] */ );
	
	var positionAdjust = offsetDifference - ( height__delete_note_overlay_container / 2 );
	
	$delete_note_overlay_container.css( "top", positionAdjust );

	
	var $delete_note_overlay_background = $("#delete_note_overlay_background"); 
	$delete_note_overlay_background.show();
	$delete_note_overlay_container.show();
};

//////////	/

var closeConfirmRemoveProjectNoteOverlay = function(clickThis, eventObject) {

	var $delete_note_confirm_button = $("#delete_note_confirm_button");
	$delete_note_confirm_button.data("noteId", null);

	$(".delete_note_overlay_show_hide_parts_jq").hide();
};


/////////////////

//	put click handler for this on #delete_note_confirm_button

var deleteProjectNoteConfirmed = function(clickThis, eventObject) {
	

	var $clickThis = $(clickThis);

	var noteId = $clickThis.data("noteId");
	
	if ( noteId === undefined || noteId === null ) {
		
		throw " noteId === undefined || noteId === null ";
	}

	if ( noteId === "" ) {
		
		throw ' noteId === "" ';
	}

	if ( noteId === undefined || noteId === null || noteId === "") {

		throw "Unable to find attribute for id 'noteId' ";
	}
	

	var requestData = {
			noteId : noteId
	};
	
	
	var _URL = contextPathJSVar + "/services/project/deleteNote";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			var responseParams = { requestData: requestData, responseData: data, noteIdDeleted: noteId, clickThis: clickThis };
			removeProjectNoteComplete( responseParams);
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});
};

///

var removeProjectNoteComplete = function( params ) {

	
	var responseData = params.responseData;

	if (responseData.status) {

		closeConfirmRemoveProjectNoteOverlay();

		//  Get the note deleted and remove it from the DOM
		
		var noteIdDeleted = params.noteIdDeleted;
		
		var $notes_list_container_div = $("#notes_list_container_div");
		
		$notes_list_container_div.find(".note_root_container_div_jq").each( function( index, element ) {
			
			//  The root DOM node for a note
			var $note_root_container_div_jq = $( this );
			
			var noteIdOnDOMnode = $note_root_container_div_jq.attr("note_id");
			
			if ( noteIdDeleted === noteIdOnDOMnode ) {
				
				//  The noteId on this DOM node matches the noteId that was deleted so remove this DOM node
				
				$note_root_container_div_jq.remove();
			}
		});
	
	} else {
		
		//  TODO  Do something when status is false
	}

	
	//  WAS
	
//	var responseData = params.responseData;
//	var clickThis = params.clickThis;
//
//	if (responseData.status) {
//
//		var $clickThis = $( clickThis );
//		
//		var $note_root_container_div_jq_parent = $clickThis.closest(".note_root_container_div_jq");
//
//		$note_root_container_div_jq_parent.remove( );
//	}
};


// //////////////////////////

function updateMoveSearchesButton() {

//	if (adminGlobals.projectToMoveSearchesToSelected
//			&& adminGlobals.searchCheckboxesCheckedCount > 0) {

	if (adminGlobals.searchCheckboxesCheckedCount > 0) {

		// enable button
		$("#move_search_button").removeAttr("disabled");
		//  hide covering div
		$("#move_search_button_cover_when_disabled").hide();

	} else {

		// disable button
		$("#move_search_button").attr("disabled", "disabled");
		//  show covering div
		$("#move_search_button_cover_when_disabled").show();

	}

};

// //////////////////////////

function updateMoveSearchesButtonFromSearchCheckboxes(searchCheckboxesCheckedCount) {

	adminGlobals.searchCheckboxesCheckedCount = searchCheckboxesCheckedCount;

	updateMoveSearchesButton();
};

//////////////////////////////

var openMoveSearchesOverlay = function(clickThis, eventObject) {

	$(".move_searches_overlay_confirmation_steps_jq").hide();
	$("#move-searches-overlay-select-project-block").show();
	$("#move-searches-overlay-background").show();
	$("#move-searches-overlay-container").show();

};



//////////////////////////////

var closeMoveSearchesOverlay = function(clickThis, eventObject) {

	$(".move_searches_overlay_show_hide_parts_jq").hide();
};


////////////////////////////

var moveSearchesProjectClicked = function(clickThis, eventObject) {

	var $clickThis = $(clickThis);
	
	var otherProjectIdValue = $clickThis.attr("otherProjectId");
	
	$("#move_search_confirm_button").data( {moveToProjectId: otherProjectIdValue } );

	var projectTitle = $clickThis.html();
	
	$("#move-searches-overlay-project-to-move-title").html( projectTitle );
	
	$("#move-searches-overlay-select-project-block").hide();

	$("#move-searches-overlay-confirm-project-block").show();
};


/////////////

var executeMoveSearches = function(clickThis, eventObject) {

	var storedData = $("#move_search_confirm_button").data(  );
	
	var moveToProjectId = storedData.moveToProjectId;
	
	var searchesToMoveToOtherProject = searchesToMerge; //  searchesToMerge is an array managed as the user clicks each check box
	
	if (adminGlobals.project_id === null) {

		throw "Unable to find input field for id 'project_id', adminGlobals.project_id not set ";
	}

	var requestData = {
		moveToProjectId : moveToProjectId,
		searchesToMoveToOtherProject: searchesToMoveToOtherProject,
		projectId : adminGlobals.project_id
	};
	
	var requestDataJSON = JSON.stringify( requestData );

	var _URL = contextPathJSVar + "/services/project/moveSearches";

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
	    data: requestDataJSON,
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
		success : function(data) {

			executeMoveSearchesComplete(requestData, data);
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});
};

///////

var executeMoveSearchesComplete = function(requestData, responseData) {

	if (responseData.status) {

		$("#show-project-searches-moved-to").data( {moveToProjectId: requestData.moveToProjectId } );
		
		//  Remove the moved searches from the DOM
		
		var searchIds = requestData.searchesToMoveToOtherProject;
		
		var $search_row_jq = $(".search_row_jq");
		
		$search_row_jq.each(function( index, element ){

			var $thisRow = $(this);
			
			var searchIdString = $thisRow.attr("searchId");
			
			try {
				
				var searchId = parseInt( searchIdString, 10 );

				if ( searchId !== undefined && searchId !== null ) {

					var index = searchIds.indexOf( searchId );
					if( index != -1 ) {

						$thisRow.remove();
					}
				}
			
			} catch ( exception ) {
				
//				var z = 90;
				
			}
		
		});
		
		
		$("#move-searches-overlay-confirm-project-block").hide();
		$("#move-searches-overlay-confirmation-project-block").show();
	
	} else {
		
		$("#move-searches-overlay-confirm-project-block").hide();

//		private boolean moveToProjectMarkedForDeletion;
//		private boolean moveToProjectDisabled;

		if ( responseData.moveToProjectMarkedForDeletion ) {
			
			$("#move-searches-overlay-move-to-project-marked-for-deletion-block").show();
			
		} else if ( responseData.moveToProjectDisabled ) {
			
			$("#move-searches-overlay-move-to-project-disabled-block").show();
			
		} else {

			//  Shouldn't get here, no other reason for status to be false

			$("#move-searches-overlay-move-project-failed-block").show();
			
		}
	}
};


/////////////

var showProjectSearchesMovedTo = function(clickThis, eventObject) {

	var storedData = $("#show-project-searches-moved-to").data(  );
	
		
	var moveToProjectId = storedData.moveToProjectId;
	
	if ( moveToProjectId === undefined || moveToProjectId === null ) {
		throw "executeMoveSearches(...)  moveToProjectId === undefined || moveToProjectId === null";
	}
	
	document.location.href= contextPathJSVar + "/viewProject.do?project_id=" + moveToProjectId;
	
};




////////////////////////////////

//Give existing user access to the project

var existingUserIdForAddProjectAccess = "";


//Set up autocomplete for add user by Last Name to project

var initInviteUserLastNameAutoComplete = function() {

	// Autocomplete support for searching for a key to assign

	var $invite_user_last_name = $("#invite_user_last_name");

	if ($invite_user_last_name.length === 0) {
		
		console.log( "Unable to find input field for id 'invite_user_last_name' to attach autocomplete, probably since project is locked " );

		return;  // Exit since input field is not on the page, probably since project is locked. 
		
//		throw "Unable to find input field for id 'invite_user_last_name' ";
	}

	$invite_user_last_name.autocomplete({
		source : function(request, response) {

			if (adminGlobals.project_id === null) {

				throw "Unable to find input field for id 'project_id' ";
			}

			$.ajax({
				url : contextPathJSVar + "/services/user/lookupLastNameNotInProjectId",
		        failure: function(errMsg) {
		        	handleAJAXFailure( errMsg );
		        },

				error : function(jqXHR, textStatus, errorThrown) {

					handleAJAXError(jqXHR, textStatus, errorThrown);

				},
				data : {
					query : request.term,
					projectId : adminGlobals.project_id
				}, // The data sent as params on the URL
				dataType : "json",

				success : function(data) {

					// call "response" passed into the function defined at
					// "source"
					response($.map(data.queryResultList, function(item) {
						return {

							label : item.lastName + ", " + item.firstName,
							value : item.lastName,
							id : item.authUser.id
						};
					}));
				}
			});
		},
		select : function(event, ui) {
			// var $this = $(this);
			var thisValue = this.value;

			processChosenUserForAddProjectAccess(ui, thisValue);
		},

		// open: function() {
		// $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
		// },
		// close: function() {
		// $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
		// },

		minLength : 0
	// minLength: 1

	}).focus(function() {
		// This searches on focus to search the autocomplete with no data
		$(this).autocomplete("search", "");
	});

};



//Set up autocomplete for add user by Email to project

var initInviteUserEmailAutoComplete = function() {

	// Autocomplete support for searching for a key to assign

	var $invite_user_email = $("#invite_user_email");

	if ($invite_user_email.length === 0) {

		console.log( "Unable to find input field for id 'invite_user_email' to attach autocomplete, probably since project is locked " );

		return;  // Exit since input field is not on the page, probably since project is locked. 
		
//		throw "Unable to find input field for id 'invite_user_email' ";
	}


	$invite_user_email.autocomplete({
		source : function(request, response) {

			if (adminGlobals.project_id === null) {

				throw "Unable to find input field for id 'project_id' ";
			}

			$.ajax({
				url : contextPathJSVar + "/services/user/lookupEmailNotInProjectId",
		        failure: function(errMsg) {
		        	handleAJAXFailure( errMsg );
		        },

				error : function(jqXHR, textStatus, errorThrown) {

					handleAJAXError(jqXHR, textStatus, errorThrown);

				},
				data : {
					query : request.term,
					projectId : adminGlobals.project_id
				}, // The data sent as params on the URL
				dataType : "json",

				success : function(data) {

					// call "response" passed into the function defined at
					// "source"
					response($.map(data.queryResultList, function(item) {
						return {

							label : item.authUser.email + ", " + item.lastName + ", " + item.firstName,
							value : item.authUser.email,
							id : item.authUser.id
						};
					}));
				}
			});
		},
		select : function(event, ui) {
			// var $this = $(this);
			var thisValue = this.value;

			processChosenUserForAddProjectAccess(ui, thisValue);
		},

		// open: function() {
		// $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
		// },
		// close: function() {
		// $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
		// },

		minLength : 0
	// minLength: 1

	}).focus(function() {
		// This searches on focus to search the autocomplete with no data
		$(this).autocomplete("search", "");
	});

};
////////////////////////////

var processChosenUserForAddProjectAccess = function(ui, thisValue) {

	var item = ui.item;

	var userId = item.id;

	existingUserIdForAddProjectAccess = userId;

	$("#invite_user_auto_complete_value").text(item.label);

	$("#invite_user_auto_complete_display").show();

	$("#invite_user_input_fields").hide();

};



////////////////////////////

var initInviteUser = function() {

	initInviteUserLastNameAutoComplete();
	
	initInviteUserEmailAutoComplete();
	
	$("#close_invite_user_auto_complete_display").click(function(eventObject) {

		hideAllErrorMessages();
		
		// var clickThis = this;

		clearInviteUserFieldsAndAutocompleteDisplay();
		
		return false;
	});
	
	
	$("#invite_user_button").click(function(eventObject) {

		hideAllErrorMessages();
		
		var clickThis = this;
		
		inviteUserToProject( clickThis );

		return false;
	});
	
	$(".invite_user_expand_link_jq").click(function(eventObject) {
		
		$("#invite_user_collapsed").hide();
		$("#invite_user_expanded").show();

		return false;
	});
	
	
	$(".invite_user_cancel_button_jq").click(function(eventObject) {

		clearInviteUserFieldsAndAutocompleteDisplay();

		$("#invite_user_collapsed").show();
		$("#invite_user_expanded").hide();
		
		return false;
	});

};

/////////////


var clearInviteUserFieldsAndAutocompleteDisplay = function() {
	
	$("#invite_user_auto_complete_display").hide();

	existingUserIdForAddProjectAccess = "";

	$("#invite_user_input_fields").show();
	$("#invite_user_last_name").val("");
	$("#invite_user_email").val("");	
};


////////////////////////////

var inviteUserToProject = function(clickThis) {

	// set above: existingUserIdForAddProjectAccess

	if (adminGlobals.project_id === null) {

		throw "Unable to find input field for id 'project_id' ";
	}
	

	var $invite_person_to_project_access_level_entry_field = $("#invite_person_to_project_access_level_entry_field");

	if ($invite_person_to_project_access_level_entry_field.length === 0) {

		throw "Unable to find input field for id 'invite_person_to_project_access_level_entry_field' ";
	}




	var invite_person_to_project_access_level_entry_field = $invite_person_to_project_access_level_entry_field.val();
	
	var requestData = { 
			invite_person_to_project_access_level_entry_field : invite_person_to_project_access_level_entry_field, 
			projectId : adminGlobals.project_id
	};

	var ajaxParams = {
			invitedPersonAccessLevel : invite_person_to_project_access_level_entry_field,
			projectId : adminGlobals.project_id
	};
	
	if ( existingUserIdForAddProjectAccess !== "" ) {
		
		ajaxParams.invitedPersonUserId = existingUserIdForAddProjectAccess;
		requestData.existingUserIdForAddProjectAccess = existingUserIdForAddProjectAccess;
		
	} else {
		
		var $invite_user_last_name = $("#invite_user_last_name");

		if ($invite_user_last_name.length === 0) {

			throw "Unable to find input field for id 'invite_user_last_name' ";
		}
		
		var $invite_user_email = $("#invite_user_email");

		if ($invite_user_email.length === 0) {

			throw "Unable to find input field for id 'invite_user_email' ";
		}

		var invite_user_last_name = $invite_user_last_name.val();
		var invite_user_email = $invite_user_email.val();

		if ( invite_user_last_name === "" && invite_user_email === "" ) {

//			alert("last name or email must be specified");
			
			var $element = $("#error_message_invite_name_or_email_required");
			
			showErrorMsg( $element );
				
			return false;  //  !!!  EARLY EXIT			
		}

		if ( invite_user_last_name !== "" && invite_user_email !== "" ) {

//			alert("last name and email cannot both be specified");
			
			var $element = $("#error_message_invite_name_and_email_have_values");
			
			showErrorMsg( $element );
				
			return false;  //  !!!  EARLY EXIT	
		}
		
		ajaxParams.invitedPersonLastName = invite_user_last_name;
		ajaxParams.invitedPersonEmail = invite_user_email;
		
		requestData.invite_user_last_name = invite_user_last_name;
		requestData.invite_user_email = invite_user_email;
	}
	
	requestData.ajaxParams = ajaxParams;

	var _URL = contextPathJSVar + "/services/user/invite";


	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : ajaxParams,
		dataType : "json",
		success : function(data) {

			inviteUserToProjectResponse({
				responseData : data,
				requestData : requestData,
				clickThis : clickThis
			});
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});

};

////////////////////////////

var inviteUserToProjectResponse = function(params) {

	var responseData = params.responseData;
	var requestData = params.requestData;

	if (responseData.status) {
		
		clearInviteUserFieldsAndAutocompleteDisplay();
		
		var addedExistingUser = responseData.addedExistingUser;
		var existingUserThatWasAdded = responseData.existingUserThatWasAdded;
		
		var invite_user_email = requestData.invite_user_email;
		
		if ( addedExistingUser ) {

			if ( existingUserThatWasAdded ) {
				
//				var firstName = existingUserThatWasAdded.firstName;
//				var lastName = existingUserThatWasAdded.lastName;

//				alert( "Access to project added for " + firstName + " " + lastName );

			} else {
			
//				alert( "Access to project added for provided user" );
			}
		} else {
			
//			alert( "email sent to " + invite_user_email  + " inviting them to this project" );
			
			$("#invite_user_email_that_was_sent").text( invite_user_email );

			var $element = $("#success_message_invite_email_sent");
			
			showErrorMsg( $element );  //  Used for success messages as well

		}	

		updateInvitedPeopleCurrentUsersLists();

	} else {

		
//		status: false
		
//		addedExistingUser: false
//		duplicateInsertError: false
//		emailAddressDuplicateError: false
//		emailAddressInvalidSendError: false
//		emailSent: false
//		existingUserThatWasAdded: null
//		lastNameDuplicateError: false
//		lastNameNotFoundError: true
//		unableToSendEmailError: false
		
		
		if (responseData.duplicateInsertError) {

//			alert("User already has access to this project");
			
			var $element = $("#error_message_invite_already_has_access");
			
			showErrorMsg( $element );
			
		} else if (responseData.lastNameNotFoundError ) {
			
//			alert("Unable to send email, email address is invalid.");
			
			var $element = $("#error_message_invite_name_not_found");
			
			showErrorMsg( $element );			
			
		} else if (responseData.lastNameDuplicateError ) {
			
			//  More than one user has this last name
			
//			alert("Unable to send email, email address is invalid.");
			
			var $element = $("#error_message_invite_name_duplicate");
			
			showErrorMsg( $element );	
			
		} else if (responseData.emailAddressInvalidSendError ) {
			
//			alert("Unable to send email, email address is invalid.");
			
			var $element = $("#error_message_invite_email_address_invalid");
			
			showErrorMsg( $element );
			
		} else if (responseData.unableToSendEmailError ) {
			
//			alert("Unable to send email, system error.");

			var $element = $("#error_message_invite_email_send_sytem_error");
			
			showErrorMsg( $element );

		} else {

//			alert("Error adding user to project");

			var $element = $("#error_message_invite_error_adding_user_to_project");
			
			showErrorMsg( $element );
		}
	}

};



// //////////////////////////

var initAdminPublicAccessCode = function() {
	
	
	$("#public_access_expand_link_jq").click( function(eventObject) {

		var clickThis = this;

		showPublicAccessControlDataButtonClicked(clickThis,eventObject);
		
		return false;
	});
	
	
	$("#public_access_collapse_link_jq").click( function(eventObject) {

		var params = { clickThis : this, eventObject : eventObject };

		hidePublicAccessControlData(params);
		
		return false;
	});
	

	$("#enable_project_public_access_button").click( function(eventObject) {

		var clickThis = this;

		enable_project_public_access_button(clickThis,eventObject);
		
		return false;
	});

	$("#disable_project_public_access_button").click( function(eventObject) {

				var clickThis = this;

				disable_project_public_access_button(clickThis, eventObject);
				
				return false;
			});

	$("#generate_new_public_access_code_button").click( function(eventObject) {

				var clickThis = this;

				generate_new_public_access_code_button(clickThis, eventObject);
				
				return false;
	});
	
	$("#generate_new_pub_access_code_confirm_button").click( function(eventObject) {

		var clickThis = this;

		generate_new_pub_access_code_confirm_button(clickThis, eventObject);
		
		return false;
	});

	$(".generate_new_pub_access_code_overlay_cancel_parts_jq").click( function(eventObject) {

		var clickThis = this;

		generate_new_pub_access_code_overlay_cancel(clickThis, eventObject);
		
		return false;
	});
	

	
	
	
	
	$("#lock_project_public_access_button").click( function(eventObject) {

		var clickThis = this;

		lock_project_public_access_button(clickThis, eventObject);
		
		return false;
	});
	
	$("#unlock_project_public_access_button").click( function(eventObject) {

		var clickThis = this;

		unlock_project_public_access_button(clickThis, eventObject);
		
		return false;
	});
	
	
	$("#require_public_access_code_yes_radio_button").change( function(eventObject) {

		var changeThis = this;

		require_public_access_code_yes_radio_button(changeThis, eventObject);
		
		return false;
	});
	
	
	$("#require_public_access_code_no_radio_button").change( function(eventObject) {

		var changeThis = this;

		require_public_access_code_no_radio_button(changeThis, eventObject);
		
		return false;
	});
	
	
	
	
};



////////////////////////////


////////////////////////////


var showPublicAccessControlDataButtonClicked = function(clickThis, eventObject) {

	var $this = $( clickThis );
	
	var params = { clickThis : clickThis, eventObject : eventObject };

	var isDataLoaded = $this.data( "isDataLoaded" );
	
	if ( isDataLoaded ) {
		
		showPublicAccessControlData( params );
		
	} else {
		
		loadPublicAccessControlData( params );
		
	}
};


var loadPublicAccessControlData = function( clickParams ) {
	

	var requestData = {
		projectId : adminGlobals.project_id
	};

	var _URL = contextPathJSVar + "/services/project/publicAccessAdmin/getData";

	// var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			loadPublicAccessControlDataResponse( { clickParams : clickParams, requestData : requestData, responseData : data } );
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});

	
};


////////

var loadPublicAccessControlDataResponse = function( params ) {
	
	var clickParams = params.clickParams; 
	var requestData = params.requestData; 
	var responseData = params.responseData;
	
	var anyPublicAccessEnabled = responseData.anyPublicAccessEnabled;
	
	var publicAccessCode = responseData.publicAccessCode;
	var publicAccessCodeEnabled = responseData.publicAccessCodeEnabled;
	
	var publicAccessEnabled = responseData.publicAccessEnabled;
	var publicAccessLocked = responseData.publicAccessLocked;
	

	
	if ( publicAccessLocked ) {
		
		$("#lock_project_public_access_button").hide();
		$("#unlock_project_public_access_button").show();
		
	} else {
		
		//  Enable all buttons and radio buttons except lock and unlock public access buttons. 
		//  Will disable or hide specific items in the following code. 
		
		$(".first_enable_when_public_access_not_locked_jq").each( function() {
			
			var $this = $( this );
			
			enableIfAllowedPublicAccessElements( $this );
		});
		
		$("#lock_project_public_access_button").show();
		$("#unlock_project_public_access_button").hide();
	}
	
	
	
	if ( anyPublicAccessEnabled ) {
	
		$("#enable_project_public_access_button").hide();
		$("#disable_project_public_access_button").show();
		$("#generate_new_public_access_code_button").show();

		$(".show_when_public_access_or_public_access_code_disabled_jq").hide();
		$(".show_when_public_access_or_public_access_code_enabled_jq").show();


		if ( publicAccessEnabled ) {


			// Check
			$( "#require_public_access_code_no_radio_button" ).prop( "checked", true );

			// Uncheck
			$( "#require_public_access_code_yes_radio_button" ).prop( "checked", false );
			
			$("#generate_new_public_access_code_button").hide();

			//  The URL is simply the current page
			$("#project_public_access_code_div").text( window.location.href );


		} else if ( publicAccessCodeEnabled && publicAccessCode ) {

			// Check
			$( "#require_public_access_code_yes_radio_button" ).prop( "checked", true );
			 
			// Uncheck
			$( "#require_public_access_code_no_radio_button" ).prop( "checked", false );

			$("#generate_new_public_access_code_button").show();

			// Build the public access code URL and display it
			
			var project_public_access_code_template = $("#project_public_access_code_template_div").text();
			var project_public_access_code_div = project_public_access_code_template + publicAccessCode;

			$("#project_public_access_code_div").text(project_public_access_code_div);

			$("#project_public_access_code_outer_div").show();

			
		} else {
			
			throw "shouldn't get here";
		}

		
	} else {
		
		$("#enable_project_public_access_button").show();
		$("#disable_project_public_access_button").hide();
		$("#generate_new_public_access_code_button").hide();


		$(".show_when_public_access_or_public_access_code_disabled_jq").show();
		$(".show_when_public_access_or_public_access_code_enabled_jq").hide();
		
		
	}
	
	if ( publicAccessLocked ) {
		
		//  Disable all buttons and radio buttons except lock and unlock public access buttons. 
		
		$(".first_enable_when_public_access_not_locked_jq").each( function() {
			
			var $this = $( this );
			$this.prop('disabled', true);
		});
	}


	if ( clickParams && clickParams.clickThis ) {

		var $clickThis = $( clickParams.clickThis );

		$clickThis.data( "isDataLoaded", true );
		
		showPublicAccessControlData( clickParams );
	}
		
	
		
};

var enableIfAllowedPublicAccessElements = function( $element ) {
	
	var allowed_to_enable = $element.attr( "allowed_to_enable" );

	if ( allowed_to_enable === "true" ) {
		
		$element.prop('disabled', false);
	}
};




var showPublicAccessControlData = function(params) {

	
	var clickThis = params.clickThis;
//	var eventObject = params.eventObject;
	
	var $this = $( clickThis );
	var $collapsable_container_jq = $this.closest(".collapsable_container_jq");
	
	var $collapsable_jq = $collapsable_container_jq.children(".collapsable_jq");
	
	var $public_access_collapse_link_jq = $("#public_access_collapse_link_jq");
	
	
	$this.hide();
	$collapsable_jq.show();
	$public_access_collapse_link_jq.show();

	if ( $public_access_collapse_link_jq.length === 0 ) {
		
		throw "Unable to find id=public_access_collapse_link_jq to show";
	}


	
};



var hidePublicAccessControlData = function(params) {

	var clickThis = params.clickThis;
//	var eventObject = params.eventObject;
	
	var $this = $( clickThis );
	var $collapsable_container_jq = $this.closest(".collapsable_container_jq");
	
	var $collapsable_jq = $collapsable_container_jq.children(".collapsable_jq");
	
	var $public_access_expand_link_jq = $("#public_access_expand_link_jq");
	
	$this.hide();
	$collapsable_jq.hide();
	$public_access_expand_link_jq.show();


	if ( $public_access_expand_link_jq.length === 0 ) {
		
		throw "Unable to find expand link";
	}
};


////////////////////////////

var require_public_access_code_yes_radio_button = function(changeThis, eventObject) {
	
	enable_project_public_access_button(changeThis, eventObject);
};


////////////////////////////

var require_public_access_code_no_radio_button = function(changeThis, eventObject) {

	enable_project_public_access_button(changeThis, eventObject);
};



// //////////////////////////

var enable_project_public_access_button = function(clickThis, eventObject) {
	
	var require_public_access_code = "false";
	
	var require_public_access_code_yes_radio_button = $( "#require_public_access_code_yes_radio_button:checked" ).val();
	
	if ( require_public_access_code_yes_radio_button !== undefined
			&& require_public_access_code_yes_radio_button !== "" ) {
		
		require_public_access_code = "true";
	}
	
	

	var requestData = {
		projectId : adminGlobals.project_id,
		require_public_access_code : require_public_access_code
	};

	var _URL = contextPathJSVar + "/services/project/publicAccessAdmin/enable";

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(responseData) {

			loadPublicAccessControlDataResponse( { requestData : requestData, responseData : responseData } );
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});

};


// ///////////////

var disable_project_public_access_button = function(clickThis, eventObject) {

	var requestData = {
		projectId : adminGlobals.project_id
	};

	var _URL = contextPathJSVar + "/services/project/publicAccessAdmin/disable";

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(responseData) {

			loadPublicAccessControlDataResponse( { requestData : requestData, responseData : responseData } );
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});

};



///////////

var openConfirmGenerateNewPublicAccessCodeOverlay = function(clickThis, eventObject) {

//	var $clickThis = $(clickThis);

//	Position dialog over clicked delete icon

////	get position of div containing the dialog that is inline in the page
//	var $generate_new_pub_access_code_overlay_containing_outermost_div_inline_div = $("#generate_new_pub_access_code_overlay_containing_outermost_div_inline_div");
//
//	var offset__containing_outermost_div_inline_div = $generate_new_pub_access_code_overlay_containing_outermost_div_inline_div.offset();
//	var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
//
//	var offset__ClickedDeleteIcon = $clickThis.offset();
//	var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
//
//	var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
//
////	adjust vertical position of dialog 
//
	var $generate_new_pub_access_code_overlay_container = $("#generate_new_pub_access_code_overlay_container");
//
//	var height__generate_new_pub_access_code_overlay_container = $generate_new_pub_access_code_overlay_container.outerHeight( true /* [includeMargin ] */ );
//
//	var positionAdjust = offsetDifference - ( height__generate_new_pub_access_code_overlay_container / 2 );
//
//	$generate_new_pub_access_code_overlay_container.css( "top", positionAdjust );


	var $generate_new_pub_access_code_overlay_background = $("#generate_new_pub_access_code_overlay_background"); 
	$generate_new_pub_access_code_overlay_background.show();
	$generate_new_pub_access_code_overlay_container.show();
};

///////////

var closeConfirmGenerateNewPublicAccessCodeOverlay = function(clickThis, eventObject) {

	$(".generate_new_pub_access_code_overlay_show_hide_parts_jq").hide();
};


/////////////////

var generate_new_public_access_code_button = function(clickThis, eventObject) {

	
	openConfirmGenerateNewPublicAccessCodeOverlay(clickThis, eventObject);
};


/////////////////

var generate_new_pub_access_code_confirm_button = function(clickThis, eventObject) {

	var requestData = {
			projectId : adminGlobals.project_id
	};

	var _URL = contextPathJSVar + "/services/project/publicAccessAdmin/generateNewPublicAccessCode";

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(responseData) {

			closeConfirmGenerateNewPublicAccessCodeOverlay(clickThis, eventObject);
			
			loadPublicAccessControlDataResponse( { requestData : requestData, responseData : responseData } );
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});

};


/////////////////

var generate_new_pub_access_code_overlay_cancel = function(clickThis, eventObject) {

	closeConfirmGenerateNewPublicAccessCodeOverlay(clickThis, eventObject);
	
};

/////////////////

var lock_project_public_access_button = function(clickThis, eventObject) {

	var requestData = {
			projectId : adminGlobals.project_id
	};

	var _URL = contextPathJSVar + "/services/project/publicAccessAdmin/lock";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(responseData) {

			loadPublicAccessControlDataResponse( { requestData : requestData, responseData : responseData } );
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});

};



/////////////////

var unlock_project_public_access_button = function(clickThis, eventObject) {

	var requestData = {
			projectId : adminGlobals.project_id
	};

	var _URL = contextPathJSVar + "/services/project/publicAccessAdmin/unlock";

//	var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(responseData) {

			loadPublicAccessControlDataResponse( { requestData : requestData, responseData : responseData } );
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

//			alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
//			textStatus: " + textStatus );
		}
	});

};












/////////////////

var updateInvitedPeopleCurrentUsersLists = function() {

	getInvitedPeople();
};


/////////////////

var getInvitedPeople = function() {

	var requestData = {
		projectId : adminGlobals.project_id
	};

	var _URL = contextPathJSVar + "/services/user/listInvitedPeopleForProjectId";

	// var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			getInvitedPeopleResponse(requestData, data);
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});

};

///////

var getInvitedPeopleResponse = function(requestData, responseData) {

	var $invited_people = $("#invited_people_current_users");
	
	$invited_people.empty();

	if (responseData && responseData.length > 0) {

		var access_level_id_project_owner_String = $("#access-level-id-project-owner").val();
		var access_level_id_project_researcher_String = $("#access-level-id-project-researcher").val();
		
		if ( access_level_id_project_owner_String === undefined || 
				access_level_id_project_owner_String === null || 
				access_level_id_project_owner_String === "" ) {
			
			throw "No value for hidden field with id 'access-level-id-project-owner'";
		}
		
		
		if ( access_level_id_project_researcher_String === undefined || 
				access_level_id_project_researcher_String === null || 
				access_level_id_project_researcher_String === "" ) {
			
			throw "No value for hidden field with id 'access-level-id-project-researcher'";
		}
		
		var access_level_id_project_owner = parseInt( access_level_id_project_owner_String, 10 ); 
		var access_level_id_project_researcher = parseInt( access_level_id_project_researcher_String, 10 ); 
		
		if ( isNaN( access_level_id_project_owner ) ) {
			throw "value in hideden field with id 'access-level-id-project-owner' is not a number, it is: " + access_level_id_project_owner_String;
		}
		
		if ( isNaN( access_level_id_project_researcher ) ) {
			throw "value in hideden field with id 'access-level-id-project-researcher' is not a number, it is: " + access_level_id_project_researcher_String;
		}
		
		
		//////////////////

		//  verson for '#invited_person_entry_template' is a div:
		
//		var source = $("#invited_person_entry_template").html();

		//  verson for '#invited_person_entry_template' is a table:
		
		var $invited_person_entry_template = $("#invited_person_entry_template tbody");
		
		
		var source = $invited_person_entry_template.html();


		if ( source === undefined ) {
			throw '$("#invited_person_entry_template tbody").html() === undefined';
		}
		if ( source === null ) {
			throw '$("#invited_person_entry_template tbody").html() === null';
		}
		
		var template = Handlebars.compile(source);
		
		
		for (var index = 0; index < responseData.length; index++) {

			var responseDataItem = responseData[index];

			var context = responseDataItem;

			
			var html = template(context);

			var $invited_person_entry = $(html).appendTo($invited_people);
			addToolTips( $invited_person_entry );
			
			$invited_person_entry.data("context", context);
			
			var access_level_id_project_owner = parseInt( access_level_id_project_owner_String, 10 ); 
			var access_level_id_project_researcher = parseInt( access_level_id_project_researcher_String, 10 ); 

			var attachClickHandlerToRemoveButton = true;

			
			if ( responseDataItem.invitedUserAccessLevel === access_level_id_project_owner ) {
				
				var $access_level_owner_jq = $invited_person_entry.find(".access_level_owner_jq");
				
				$access_level_owner_jq.show();

				
				var $invited_person_entry_access_level_update_button_jq = $access_level_owner_jq.find(".invited_person_entry_access_level_update_button_jq");

				if ( adminGlobals.logged_in_user_access_level_owner_or_better ) {

					//  Logged in User is project owner or better


					$invited_person_entry_access_level_update_button_jq.click(function(eventObject) {

						var clickThis = this;

						updateInvitedPersonAccessLevel( { clickThis: clickThis, newAccessLevel: access_level_id_project_researcher } );

						return false;
					});

				} else {
					
					//  Logged in user is not owner level
					
					//     hide buttons for "remove user from project" and "change user access level"

					$invited_person_entry_access_level_update_button_jq.hide();
					
					attachClickHandlerToRemoveButton = false;
					
				}
				
			} else if ( responseDataItem.invitedUserAccessLevel === access_level_id_project_researcher ) {

				//  User in list is "Researcher" access level
				
				var $access_level_researcher_jq = $invited_person_entry.find(".access_level_researcher_jq");
				
				$access_level_researcher_jq.show();

				var $invited_person_entry_access_level_update_button_jq = $access_level_researcher_jq.find(".invited_person_entry_access_level_update_button_jq");

				if ( adminGlobals.logged_in_user_access_level_owner_or_better ) {

					//  Logged in User is project owner or better
					
					$invited_person_entry_access_level_update_button_jq.click(function(eventObject) {

						var clickThis = this;

						updateInvitedPersonAccessLevel( { clickThis: clickThis, newAccessLevel: access_level_id_project_owner } );

						return false;
					});

				} else {
					
					//  Logged in user is not owner level, 
					
					//     hide buttons for "change user access level"

					$invited_person_entry_access_level_update_button_jq.hide();
					
				}
			
			} else {
				
				
			}
			
			
			var $invited_person_entry_access_level_remove_button_jq = $invited_person_entry.find(".invited_person_entry_access_level_remove_button_jq");

			
			if ( attachClickHandlerToRemoveButton ) {
				
				$invited_person_entry_access_level_remove_button_jq.click(function(eventObject) {

					var clickThis = this;

					revokePersonInvite(clickThis);

					return false;
				});
			} else {
				
				$invited_person_entry_access_level_remove_button_jq.css({visibility:"hidden"});
			}

		}

	} else {

//		var noDataMsg = $("#invited_person_entry_no_data_template_div").html();
//
//		$invited_persons.html(noDataMsg);
	}
	
	
	getCurrentUserAccess();

};



/////////////////

var updateInvitedPersonAccessLevel = function(params) {
	
	var clickThis = params.clickThis;
	var newAccessLevel = params.newAccessLevel;
	
	var $clickThis = $(clickThis);

	// get root div for this invited person entry
	var $invited_person_entry_root_div_jq = $clickThis.closest(".invited_person_entry_root_div_jq");

//	var $invited_person_entry_access_level_entry_field_jq = $invited_person_entry_root_div_jq
//			.find(".current_user_entry_access_level_entry_field_jq");
//
//	var invited_person_entry_access_level_entry = $invited_person_entry_access_level_entry_field_jq
//			.val();

	var invited_person_entry_user_id = $invited_person_entry_root_div_jq.attr("inviteId");

	if (adminGlobals.project_id === null) {

		throw "Unable to find input field for id 'project_id' ";
	}

	var _URL = contextPathJSVar + "/services/user/updateInviteAccessLevel";

	var ajaxParams = {
		inviteId : invited_person_entry_user_id,
		personAccessLevel : newAccessLevel, // invited_person_entry_access_level_entry,
		projectId : adminGlobals.project_id
	};

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : ajaxParams,
		dataType : "json",
		success : function(data) {

			updateInvitedPersonAccessLevelResponse({
				data : data,
				clickThis : clickThis
			});
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});

};

//////

var updateInvitedPersonAccessLevelResponse = function(params) {

	var data = params.data;

	if (data.status) {

//		alert("User access to project updated");

	} else {

		alert("Error Updating invited person access to project");

	}

	updateInvitedPeopleCurrentUsersLists();

};




/////////////////

var revokePersonInvite = function(clickThis, eventObject) {


	openRevokePersonInviteOverlay(clickThis, eventObject);

	return;

};




var openRevokePersonInviteOverlay = function(clickThis, eventObject) {


	var $clickThis = $(clickThis);

//	get root div for this current user entry
	var $invited_person_entry_root_div_jq = $clickThis.closest(".invited_person_entry_root_div_jq");

	var inviteId = $invited_person_entry_root_div_jq.attr("inviteId");	

//	copy the email address to the overlay

	var $invited_person_entry_email_address_jq = $invited_person_entry_root_div_jq.find(".invited_person_entry_email_address_jq");

	var invited_person_entry_email_address_jq = $invited_person_entry_email_address_jq.text();

	var $revoke_invite_to_project_overlay_email = $("#revoke_invite_to_project_overlay_email");
	$revoke_invite_to_project_overlay_email.text( invited_person_entry_email_address_jq );


	// Position dialog over clicked revoke invite icon
	
	//  get position of div containing the dialog that is inline in the page
	var $revoke_invite_to_project_overlay_containing_outermost_div_inline_div = $("#revoke_invite_to_project_overlay_containing_outermost_div_inline_div");
	
	var offset__containing_outermost_div_inline_div = $revoke_invite_to_project_overlay_containing_outermost_div_inline_div.offset();
	var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
	
	var offset__ClickedRevokeInviteIcon = $clickThis.offset();
	var offsetTop__ClickedRevokeInviteIcon = offset__ClickedRevokeInviteIcon.top;
	
	var offsetDifference = offsetTop__ClickedRevokeInviteIcon - offsetTop__containing_outermost_div_inline_div;
	
	//  adjust vertical position of dialog 
	
	var $revoke_invite_to_project_overlay_container = $("#revoke_invite_to_project_overlay_container");
	
	var height__delete_search_overlay_container = $revoke_invite_to_project_overlay_container.outerHeight( true /* [includeMargin ] */ );
	
	var positionAdjust = offsetDifference - ( height__delete_search_overlay_container / 2 );
	
	$revoke_invite_to_project_overlay_container.css( "top", positionAdjust );

	
	
	
	
	

	var $revoke_invite_to_project_confirm_button = $("#revoke_invite_to_project_confirm_button");
	$revoke_invite_to_project_confirm_button.data("inviteId", inviteId);

	$("#revoke_invite_to_project_overlay_background").show();
	$("#revoke_invite_to_project_overlay_container").show();
};

///////////

var closeRevokePersonInviteOverlay = function(clickThis, eventObject) {

	var $revoke_invite_to_project_confirm_button = $("#revoke_invite_to_project_confirm_button");
	$revoke_invite_to_project_confirm_button.data("userId", null);

	$(".revoke_invite_to_project_overlay_show_hide_parts_jq").hide();
};


/////////////////

//put click handler for this on #revoke_invite_to_project_confirm_button

var revokePersonInviteConfirmed = function(clickThis, eventObject) {

//	if (!confirm('Are you sure you want to revoke this invite?')) {
//
//		return false;
//	}

	var $clickThis = $(clickThis);

//	// get root div for this current user entry
//	var $invited_person_entry_root_div_jq = $clickThis.closest(".invited_person_entry_root_div_jq");
//
//	var inviteId = $invited_person_entry_root_div_jq.attr("inviteId");
	
	

	var inviteId = $clickThis.data("inviteId");
	
	if ( inviteId === undefined || inviteId === null ) {
		
		throw " inviteId === undefined || inviteId === null ";
	}

	if ( inviteId === "" ) {
		
		throw ' inviteId === "" ';
	}
	

	if (adminGlobals.project_id === null) {

		throw "Unable to find input field for id 'project_id' ";
	}
	

	var ajaxParams = {
			inviteId : inviteId,
			projectId : adminGlobals.project_id
	};

	var _URL = contextPathJSVar + "/services/user/revokeInvite";


	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : ajaxParams,
		dataType : "json",
		success : function(data) {

			revokePersonInviteResponse({
				data : data,
				clickThis : clickThis
			});
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});

};

//////

var revokePersonInviteResponse = function(params) {

	var data = params.data;

	if (data.status) {

		closeRevokePersonInviteOverlay();
		
//		alert("User access to project removed");

		getInvitedPeople();

	} else {

		alert("Error revoking person invite");

	}

};

// //////


// ///////////////

var getCurrentUserAccess = function() {

	var requestData = {
		projectId : adminGlobals.project_id
	};

	var _URL = contextPathJSVar + "/services/user/listForProjectId";

	// var request =
	$.ajax({
		type : "GET",
		url : _URL,
		data : requestData,
		dataType : "json",
		success : function(data) {

			getCurrentUserAccessResponse(requestData, data);
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});

};

//////////

var getCurrentUserAccessResponse = function(requestData, responseData) {

	var $current_users = $("#invited_people_current_users");

//	$current_users.empty();

	if (responseData && responseData.length > 0) {

		var access_level_id_project_owner_String = $("#access-level-id-project-owner").val();
		var access_level_id_project_researcher_String = $("#access-level-id-project-researcher").val();
		
		if ( access_level_id_project_owner_String === undefined || 
				access_level_id_project_owner_String === null || 
				access_level_id_project_owner_String === "" ) {
			
			throw "No value for hidden field with id 'access-level-id-project-owner'";
		}
		
		
		if ( access_level_id_project_researcher_String === undefined || 
				access_level_id_project_researcher_String === null || 
				access_level_id_project_researcher_String === "" ) {
			
			throw "No value for hidden field with id 'access-level-id-project-researcher'";
		}
		
		var access_level_id_project_owner = parseInt( access_level_id_project_owner_String, 10 ); 
		var access_level_id_project_researcher = parseInt( access_level_id_project_researcher_String, 10 ); 
		
		if ( isNaN( access_level_id_project_owner ) ) {
			throw "value in hidden field with id 'access-level-id-project-owner' is not a number, it is: " + access_level_id_project_owner_String;
		}
		
		if ( isNaN( access_level_id_project_researcher ) ) {
			throw "value in hidden field with id 'access-level-id-project-researcher' is not a number, it is: " + access_level_id_project_researcher_String;
		}
		
		
		//////////////////

		//////////////////

		//  verson for '#current_user_entry_template' is a div:
		
//		var source = $("#current_user_entry_template").html();

		//  verson for '#current_user_entry_template' is a table:
		
		var $current_user_entry_template = $("#current_user_entry_template tbody");
				
		var source = $current_user_entry_template.html();

		if ( source === undefined ) {
			throw '$("#current_user_entry_template tbody").html() === undefined';
		}
		if ( source === null ) {
			throw '$("#current_user_entry_template tbody").html() === null';
		}
		
		var template = Handlebars.compile(source);
		
		
		for (var index = 0; index < responseData.length; index++) {

			var responseDataItem = responseData[index];

			var context = responseDataItem;


			//  Assign to the boolean result of the comparison
			var thisIsCurrentlyLoggedInUser = adminGlobals.logged_in_user_id === responseDataItem.userId;

			
			var html = template(context);

			var $current_user_entry = $(html).appendTo($current_users);
			addToolTips( $current_user_entry );

			$current_user_entry.data("context", context);
			
			var access_level_id_project_owner = parseInt( access_level_id_project_owner_String, 10 ); 
			var access_level_id_project_researcher = parseInt( access_level_id_project_researcher_String, 10 ); 

			var attachClickHandlerToRemoveButton = true;

			
			if ( responseDataItem.userAccessLevelId === access_level_id_project_owner ) {
				
				var $access_level_owner_jq = $current_user_entry.find(".access_level_owner_jq");
				
				$access_level_owner_jq.show();

				
				var $current_user_entry_access_level_update_button_jq = $access_level_owner_jq.find(".current_user_entry_access_level_update_button_jq");

				if ( adminGlobals.logged_in_user_access_level_owner_or_better ) {

					//  Logged in User is project owner or better
					
					$current_user_entry_access_level_update_button_jq.click(function(eventObject) {

						var clickThis = this;

						updateUserAccessLevel( { clickThis: clickThis, newAccessLevel: access_level_id_project_researcher } );

						return false;
					});


				} else {
					
					//  If currently logged in user is not owner level, 
					//     hide buttons for "remove user from project" and "change user access level"

					$current_user_entry_access_level_update_button_jq.hide();
					
					attachClickHandlerToRemoveButton = false;
					
				}
				
			} else if ( responseDataItem.userAccessLevelId === access_level_id_project_researcher ) {

				//  User in list is "Researcher" access level
				
				var $access_level_researcher_jq = $current_user_entry.find(".access_level_researcher_jq");
				
				$access_level_researcher_jq.show();

				
				var $current_user_entry_access_level_update_button_jq = $access_level_researcher_jq.find(".current_user_entry_access_level_update_button_jq");

				if ( adminGlobals.logged_in_user_access_level_owner_or_better ) {

					//  Logged in User is project owner or better
					
					$current_user_entry_access_level_update_button_jq.click(function(eventObject) {

						var clickThis = this;

						updateUserAccessLevel( { clickThis: clickThis, newAccessLevel: access_level_id_project_owner } );

						return false;
					});


				} else {
					
					//  If currently logged in user is not owner level, 
					//     hide button for "change user access level"

					$current_user_entry_access_level_update_button_jq.hide();
					
				}



			
			} else {
				
				
			}
			
			
			if ( thisIsCurrentlyLoggedInUser ) {
				
				//  The user in the list being processed is the currently logged in user.  Don't allow them to change their own account
				
				$current_user_entry_access_level_update_button_jq = $current_user_entry.find(".current_user_entry_access_level_update_button_jq");
				
				$current_user_entry_access_level_update_button_jq.css({visibility:"hidden"});
				
				attachClickHandlerToRemoveButton = false;
				

				var $currently_logged_in_user_jq = $current_user_entry.find(".currently_logged_in_user_jq");

				$currently_logged_in_user_jq.show();
			}
			

			var $current_user_entry_access_level_remove_button_jq = $current_user_entry.find(".current_user_entry_access_level_remove_button_jq");

			
			if ( attachClickHandlerToRemoveButton ) {
				
				$current_user_entry_access_level_remove_button_jq.click(function(eventObject) {

					var clickThis = this;

					removeUserAccess(clickThis, eventObject);

					return false;
				});
			} else {
				
				$current_user_entry_access_level_remove_button_jq.css({visibility:"hidden"});
			}

			
			
			//   Condition to check for currently logged in user 
			
//			if (thisIsCurrentlyLoggedInUser) {
//
//				//   Code to hide the currently logged in user so they cannot remove themselves or lower their access level
//				
//				var $update_remove_access_div_jq = $current_user_entry.find(".update_remove_access_div_jq");
//
//				$update_remove_access_div_jq.hide();
//
//			} else {

//				var $current_user_entry_access_level_remove_button_jq = $current_user_entry.find(".current_user_entry_access_level_remove_button_jq");
//
//				$current_user_entry_access_level_remove_button_jq
//						.click(function(eventObject) {
//
//							var clickThis = this;
//
//							removeUserAccess(clickThis);
//							
//							return false;
//						});
//			}

				


		}

	} else {

		var noDataMsg = $("#current_user_entry_no_data_template_div").html();

		$current_users.html(noDataMsg);
	}
	
};

// ///////////////

var updateUserAccessLevel = function(params) {
	
	var clickThis = params.clickThis;
	var newAccessLevel = params.newAccessLevel;
	
	var $clickThis = $(clickThis);

	// get root div for this current user entry
	var $current_user_entry_root_div_jq = $clickThis
			.closest(".current_user_entry_root_div_jq");

//	var $current_user_entry_access_level_entry_field_jq = $current_user_entry_root_div_jq
//			.find(".current_user_entry_access_level_entry_field_jq");
//
//	var current_user_entry_access_level_entry = $current_user_entry_access_level_entry_field_jq
//			.val();

	var current_user_entry_user_id = $current_user_entry_root_div_jq
			.attr("userId");

	if (adminGlobals.project_id === null) {

		throw "Unable to find input field for id 'project_id' ";
	}

	var _URL = contextPathJSVar + "/services/user/updateAccessToProject";

	var ajaxParams = {
		personId : current_user_entry_user_id,
		personAccessLevel : newAccessLevel, // current_user_entry_access_level_entry,
		projectId : adminGlobals.project_id
	};

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : ajaxParams,
		dataType : "json",
		success : function(data) {

			updateUserAccessLevelResponse({
				data : data,
				clickThis : clickThis
			});
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});

};

// ////

var updateUserAccessLevelResponse = function(params) {

	var data = params.data;

	if (data.status) {

//		alert("User access to project updated");

		updateInvitedPeopleCurrentUsersLists();

	} else {

		alert("Error Updating user access to project");

	}

};




/////////////////

var removeUserAccess = function(clickThis, eventObject) {

	
	openRemoveUserAccessOverlay(clickThis, eventObject);
	
	return;
	
};


//////////////////////////////

var openRemoveUserAccessOverlay = function(clickThis, eventObject) {


	var $clickThis = $(clickThis);

	// get root div for this current user entry
	var $current_user_entry_root_div_jq = $clickThis.closest(".current_user_entry_root_div_jq");

	var current_user_entry_user_id = $current_user_entry_root_div_jq.attr("userId");	
	
	//  copy the name to the overlay
	
	var $current_user_entry_name_jq = $current_user_entry_root_div_jq.find(".current_user_entry_name_jq");
	
	var current_user_entry_name = $current_user_entry_name_jq.text();
	
	var $remove_user_from_project_overlay_name_of_user = $("#remove_user_from_project_overlay_name_of_user");
	$remove_user_from_project_overlay_name_of_user.text( current_user_entry_name );
	

	// Position dialog over clicked delete icon
	
	//  get position of div containing the dialog that is inline in the page
	var $remove_user_from_project_overlay_containing_outermost_div_inline_div = $("#remove_user_from_project_overlay_containing_outermost_div_inline_div");
	
	var offset__containing_outermost_div_inline_div = $remove_user_from_project_overlay_containing_outermost_div_inline_div.offset();
	var offsetTop__containing_outermost_div_inline_div = offset__containing_outermost_div_inline_div.top;
	
	var offset__ClickedDeleteIcon = $clickThis.offset();
	var offsetTop__ClickedDeleteIcon = offset__ClickedDeleteIcon.top;
	
	var offsetDifference = offsetTop__ClickedDeleteIcon - offsetTop__containing_outermost_div_inline_div;
	
	//  adjust vertical position of dialog 
	
	var $remove_user_from_project_overlay_container = $("#remove_user_from_project_overlay_container");
	
	var height__remove_user_from_project_overlay_container = $remove_user_from_project_overlay_container.outerHeight( true /* [includeMargin ] */ );
	
	var positionAdjust = offsetDifference - ( height__remove_user_from_project_overlay_container / 2 );
	
	$remove_user_from_project_overlay_container.css( "top", positionAdjust );
	
	
	
	
	var $remove_user_from_project_confirm_button = $("#remove_user_from_project_confirm_button");
	$remove_user_from_project_confirm_button.data("userId", current_user_entry_user_id);
	
	$("#remove_user_from_project_overlay_background").show();
	$remove_user_from_project_overlay_container.show();

	
};

///////////

var closeRemoveUserAccessOverlay = function(clickThis, eventObject) {

	var $remove_user_from_project_confirm_button = $("#remove_user_from_project_confirm_button");
	$remove_user_from_project_confirm_button.data("userId", null);

	$(".remove_user_from_project_overlay_show_hide_parts_jq").hide();
};


// ///////////////

//  put click handler for this on #remove_user_from_project_confirm_button

var removeUserAccessConfirmed = function(clickThis, eventObject) {

//	if (!confirm('Are you sure you want to remove access to this project from this user?')) {
//
//		return false;
//	}

	var $clickThis = $(clickThis);

//	// get root div for this current user entry
//	var $current_user_entry_root_div_jq = $clickThis.closest(".current_user_entry_root_div_jq");
//
//	var userId = $current_user_entry_root_div_jq.attr("userId");	

	var userId = $clickThis.data("userId");
	
	if ( userId === undefined || userId === null ) {
		
		throw " userId === undefined || userId === null ";
	}

	if ( userId === "" ) {
		
		throw ' userId === "" ';
	}

	
	if (adminGlobals.project_id === null) {

		throw "Unable to find input field for id 'project_id' ";
	}

	var _URL = contextPathJSVar + "/services/user/removeAccessToProject";

	var ajaxParams = {
		personId : userId,
		projectId : adminGlobals.project_id
	};

	// var request =
	$.ajax({
		type : "POST",
		url : _URL,
		data : ajaxParams,
		dataType : "json",
		success : function(data) {

			removeUserAccessResponse({
				data : data,
				clickThis : clickThis
			});
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
		error : function(jqXHR, textStatus, errorThrown) {

			handleAJAXError(jqXHR, textStatus, errorThrown);

			// alert( "exception: " + errorThrown + ", jqXHR: " + jqXHR + ",
			// textStatus: " + textStatus );
		}
	});

};

// ////

var removeUserAccessResponse = function(params) {

	var data = params.data;

	if (data.status) {
		
		closeRemoveUserAccessOverlay();

//		alert("User access to project removed");

		updateInvitedPeopleCurrentUsersLists();

	} else {

		alert("Error removing user access to project");

	}

};

// ////////////////

function initProjectAdminSection() {

	var $project_id = $("#project_id");

	if ($project_id.length === 0) {

		throw "Unable to find input field for id 'project_id' ";
	}

	adminGlobals.project_id = $project_id.val();

	var $view_searches_project_admin_div = $("#view_searches_project_admin_div");

	if ($view_searches_project_admin_div.length === 0) {

		throw "Unable to find div for id 'view_searches_project_admin_div' ";
	}

	var logged_in_user_id = $view_searches_project_admin_div.attr("logged_in_user_id");

	if (logged_in_user_id === undefined || logged_in_user_id === null
			|| logged_in_user_id.length === 0) {

		throw "Unable to find attr 'logged_in_user_id' in div with id 'view_searches_project_admin_div' ";
	}

	try {
		adminGlobals.logged_in_user_id = parseInt(logged_in_user_id, 10);
	} catch (ex) {

		throw "failed to parse logged_in_user_id: " + logged_in_user_id;
	}
	
	if ( isNaN( adminGlobals.logged_in_user_id ) ) {
		
		throw "failed to parse logged_in_user_id (parse to NaN): " + logged_in_user_id;
	}
	
	
	var logged_in_user_access_level_owner_or_better_String = $view_searches_project_admin_div.attr("logged_in_user_access_level_owner_or_better");
	
	if ( logged_in_user_access_level_owner_or_better_String === "true" ) {
		
		adminGlobals.logged_in_user_access_level_owner_or_better = true;
	}

	
	
	$("#delete_note_confirm_button").click(function(eventObject) {

		var clickThis = this;

		deleteProjectNoteConfirmed( clickThis, eventObject );

		return false;
	});

	$(".delete_note_overlay_show_hide_parts_jq").click(function(eventObject) {

		var clickThis = this;

		closeConfirmRemoveProjectNoteOverlay( clickThis, eventObject );
		
		return false;
	});
	
	
	$(".move_search_project_choice_jq").click(function(eventObject) {

		// var clickThis = this;

		adminGlobals.projectToMoveSearchesToSelected = true;

		updateMoveSearchesButton();
		
		return false;
	});

	$("#move_search_button").click(function(eventObject) {

		var clickThis = this;

		openMoveSearchesOverlay( clickThis, eventObject );
		
		return false;
	});
	
	$(".move_searches_overlay_cancel_parts_jq").click(function(eventObject) {

		var clickThis = this;

		closeMoveSearchesOverlay( clickThis, eventObject );
		
		return false;
	});
	
	
	
	$(".move_search_project_choice_jq").click(function(eventObject) {

		var clickThis = this;

		moveSearchesProjectClicked( clickThis, eventObject );
		
		return false;
	});
	
	$("#move_search_confirm_button").click(function(eventObject) {

		var clickThis = this;

		executeMoveSearches( clickThis, eventObject );
		
		return false;
	});
	
	$("#show-project-searches-moved-to").click(function(eventObject) {

		var clickThis = this;

		showProjectSearchesMovedTo( clickThis, eventObject );
		
		return false;
	});

	

	$("#revoke_invite_to_project_confirm_button").click(function(eventObject) {

		var clickThis = this;

		revokePersonInviteConfirmed( clickThis, eventObject );

		return false;
	});

	$(".revoke_invite_to_project_overlay_show_hide_parts_jq").click(function(eventObject) {

		var clickThis = this;

		closeRevokePersonInviteOverlay( clickThis, eventObject );
		
		return false;
	});
	
	
	
	$("#remove_user_from_project_confirm_button").click(function(eventObject) {

		var clickThis = this;

		removeUserAccessConfirmed( clickThis, eventObject );

		return false;
	});

	$(".remove_user_from_project_overlay_show_hide_parts_jq").click(function(eventObject) {

		var clickThis = this;

		closeRemoveUserAccessOverlay( clickThis, eventObject );
		
		return false;
	});
	

	initAdminPublicAccessCode();
	
	initInviteUser();

	initMaintTitleAndAbstract();
	
	initMaintProjectNotes();

	updateInvitedPeopleCurrentUsersLists();

};

// /////////////

$(document).ready(function() {

	var $view_searches_project_admin_div = $("#view_searches_project_admin_div");

	if ($view_searches_project_admin_div.length !== 0) {

		// The admin section is on the page so initialize it

		initProjectAdminSection();
	}

});