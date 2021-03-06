package org.yeastrc.xlink.www.webservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.linked_positions.MonolinkLinkedPositions;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.SearchProteinMonolink;
import org.yeastrc.xlink.www.objects.SearchProteinMonolinkWrapper;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.ImageViewerData;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceIdSearchId;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/imageViewer")
public class ViewerMonolinkService {
	
	private static final Logger log = Logger.getLogger(ViewerMonolinkService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getMonolinkData") 
	public ImageViewerData getViewerData( 
			@QueryParam( "projectSearchId" ) List<Integer> projectSearchIdList,
			@QueryParam( "psmPeptideCutoffsForProjectSearchIds" ) String psmPeptideCutoffsForProjectSearchIds_JSONString,
			@QueryParam( "filterNonUniquePeptides" ) String filterNonUniquePeptidesString,
			@QueryParam( "filterOnlyOnePSM" ) String filterOnlyOnePSMString,
			@QueryParam( "filterOnlyOnePeptide" ) String filterOnlyOnePeptideString,
			@QueryParam( "excludeTaxonomy" ) List<Integer> excludeTaxonomy,
			@Context HttpServletRequest request )
	throws Exception {
		
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided projectSearchId is null or empty, projectSearchId = " + projectSearchIdList;
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( psmPeptideCutoffsForProjectSearchIds_JSONString ) ) {
			String msg = "Provided psmPeptideCutoffsForProjectSearchIds is null or psmPeptideCutoffsForProjectSearchIds is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for this search
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.addAll( projectSearchIdList );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int projectSearchId : projectSearchIdList ) {
					msg += projectSearchId + ", ";
				}				
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			//  Test access to the project id
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////

			ImageViewerData ivd = new ImageViewerData();
			
			//   Get PSM and Peptide Cutoff data from JSON
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization

			Set<Integer> searchIdsSet = new HashSet<>( projectSearchIdsSet.size() );
			List<SearchDTO> searchList = new ArrayList<>( projectSearchIdsSet.size() );
			
			for ( Integer projectSearchId : projectSearchIdsSet ) {
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = ": No search found for projectSearchId: " + projectSearchId;
					log.warn( msg );
				    throw new WebApplicationException(
				    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
				    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
				    	        .build()
				    	        );
				}
				Integer searchId = search.getSearchId();
				searchIdsSet.add( searchId );
				searchList.add( search );
			}
			
			Collections.sort( searchList, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getProjectSearchId() - o2.getProjectSearchId();
				}
			});
			
			CutoffValuesRootLevel cutoffValuesRootLevel = null;
			try {
				cutoffValuesRootLevel = jacksonJSON_Mapper.readValue( psmPeptideCutoffsForProjectSearchIds_JSONString, CutoffValuesRootLevel.class );
			} catch ( JsonParseException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', JsonParseException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			} catch ( JsonMappingException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', JsonMappingException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			} catch ( IOException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', IOException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			}
			
			Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( 
							searchIdsSet, cutoffValuesRootLevel ); 
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
			
			////////////
			//  Copy Exclude Taxonomy Set for lookup
			Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
			if( excludeTaxonomy != null ) { 
				excludeTaxonomy_Ids_Set_UserInput.addAll( excludeTaxonomy );
			}
			boolean filterNonUniquePeptides = false;
			if( filterNonUniquePeptidesString != null && filterNonUniquePeptidesString.equals( "on" ) )
				filterNonUniquePeptides = true;
			boolean filterOnlyOnePSM = false;
			if( "on".equals( filterOnlyOnePSMString ) )
				filterOnlyOnePSM = true;
			boolean filterOnlyOnePeptide = false;
			if( "on".equals( filterOnlyOnePeptideString ) )
				filterOnlyOnePeptide = true;
			
			Map<Integer, List<SearchProteinMonolinkWrapper>> wrappedMonolinks_MappedOnSearchId = new HashMap<>();
			for ( SearchDTO searchDTO : searchList ) {
				int projectSearchId = searchDTO.getProjectSearchId();
				int searchId = searchDTO.getSearchId();
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
				if ( searcherCutoffValuesSearchLevel == null ) {
					String msg = "Provided search_id " + projectSearchId + " is not found in psm peptide cutoffs.";
					log.error( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				List<SearchProteinMonolinkWrapper> wrappedMonolinks = 
						MonolinkLinkedPositions.getInstance()
						.getSearchProteinMonolinkWrapperList( searchDTO, searcherCutoffValuesSearchLevel );
				// Filter out links if requested
				if( filterNonUniquePeptides || filterOnlyOnePSM || filterOnlyOnePeptide 
						|| ( excludeTaxonomy != null && excludeTaxonomy.size() > 0 )  ) {
					///////  Output Lists, Results After Filtering
					List<SearchProteinMonolinkWrapper> wrappedMonolinksAfterFilter = new ArrayList<>( wrappedMonolinks.size() );
					for ( SearchProteinMonolinkWrapper searchProteinMonolinkWrapper : wrappedMonolinks ) {
						SearchProteinMonolink link = searchProteinMonolinkWrapper.getSearchProteinMonolink();
						// did user request removal of certain taxonomy IDs?
						if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
							boolean excludeOnProtein =
									ExcludeOnTaxonomyForProteinSequenceIdSearchId.getInstance()
									.excludeOnTaxonomyForProteinSequenceIdSearchId( 
											excludeTaxonomy_Ids_Set_UserInput, 
											link.getProtein().getProteinSequenceObject(), 
											searchId );
							if ( excludeOnProtein ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of non unique peptides?
						if( filterNonUniquePeptides  ) {
							if( link.getNumUniquePeptides() < 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						//	did they request to removal of links with only one PSM?
						if( filterOnlyOnePSM  ) {
							int psmCountForSearchId = link.getNumPsms();
							if ( psmCountForSearchId <= 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of links with only one Reported Peptide?
						if( filterOnlyOnePeptide ) {
							int peptideCountForSearchId = link.getNumPeptides();
							if ( peptideCountForSearchId <= 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						wrappedMonolinksAfterFilter.add( searchProteinMonolinkWrapper );
					}
					//  Copy new filtered list to original input variable name to overlay it
					wrappedMonolinks = wrappedMonolinksAfterFilter;
				}
				wrappedMonolinks_MappedOnSearchId.put( projectSearchId, wrappedMonolinks );
			}
			// build the JSON data structure for monolinks
			//  Build a Map of maps of <prot id>, <prot position>, <set of search ids>
			Map<Integer, Map<Integer, Set<Integer>>> monolinkPositions = new HashMap<>();
			for ( Map.Entry<Integer, List<SearchProteinMonolinkWrapper>> wrappedMonolinks_MappedOnSearchId_Entry :
				wrappedMonolinks_MappedOnSearchId.entrySet() ) {
				Integer searchIdForEntry = wrappedMonolinks_MappedOnSearchId_Entry.getKey();
				List<SearchProteinMonolinkWrapper> wrappedMonolinks = wrappedMonolinks_MappedOnSearchId_Entry.getValue();
				for ( SearchProteinMonolinkWrapper wrappedMonolink : wrappedMonolinks ) {
					SearchProteinMonolink searchProteinMonolink = wrappedMonolink.getSearchProteinMonolink();
					int protId = searchProteinMonolink.getProtein().getProteinSequenceObject().getProteinSequenceId();
					int protPosition = searchProteinMonolink.getProteinPosition();
					Map<Integer, Set<Integer>> map_keyed_by_protPosition = monolinkPositions.get( protId );
					if ( map_keyed_by_protPosition == null ) {
						map_keyed_by_protPosition = new HashMap<>();
						monolinkPositions.put( protId, map_keyed_by_protPosition );
					}
					Set<Integer> searchIdSet = map_keyed_by_protPosition.get( protPosition );
					if ( searchIdSet == null ) {
						searchIdSet = new HashSet<>();
						map_keyed_by_protPosition.put( protPosition, searchIdSet );
					}
					searchIdSet.add( searchIdForEntry );
				}
			}
			ivd.setProteinMonoLinkPositions( monolinkPositions );
			return ivd;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getMonolinkPSMCounts") 
	public ImageViewerData getPSMCounts( 
			@QueryParam( "projectSearchId" ) List<Integer> projectSearchIdList,
			@QueryParam( "psmPeptideCutoffsForProjectSearchIds" ) String psmPeptideCutoffsForProjectSearchIds_JSONString,
			@QueryParam( "filterNonUniquePeptides" ) String filterNonUniquePeptidesString,
			@QueryParam( "filterOnlyOnePSM" ) String filterOnlyOnePSMString,
			@QueryParam( "filterOnlyOnePeptide" ) String filterOnlyOnePeptideString,
			@QueryParam( "excludeTaxonomy" ) List<Integer> excludeTaxonomy,
			@Context HttpServletRequest request )
	throws Exception {
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided projectSearchId is null or empty, projectSearchId = " + projectSearchIdList;
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( psmPeptideCutoffsForProjectSearchIds_JSONString ) ) {
			String msg = "Provided psmPeptideCutoffsForProjectSearchIds is null or psmPeptideCutoffsForProjectSearchIds is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for this search
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.addAll( projectSearchIdList );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchIdList: ";
				for ( int projectSearchId : projectSearchIdList ) {
					msg += projectSearchId + ", ";
				}				
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			//  Test access to the project id
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////
			
			//   Get PSM and Peptide Cutoff data from JSON
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization

			Set<Integer> searchIdsSet = new HashSet<>( projectSearchIdsSet.size() );
			List<SearchDTO> searchList = new ArrayList<>( projectSearchIdsSet.size() );
			
			for ( Integer projectSearchId : projectSearchIdsSet ) {
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = ": No search found for projectSearchId: " + projectSearchId;
					log.warn( msg );
				    throw new WebApplicationException(
				    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
				    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
				    	        .build()
				    	        );
				}
				Integer searchId = search.getSearchId();
				searchIdsSet.add( searchId );
				searchList.add( search );
			}
			
			Collections.sort( searchList, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getProjectSearchId() - o2.getProjectSearchId();
				}
			});
			
			CutoffValuesRootLevel cutoffValuesRootLevel = null;
			try {
				cutoffValuesRootLevel = jacksonJSON_Mapper.readValue( psmPeptideCutoffsForProjectSearchIds_JSONString, CutoffValuesRootLevel.class );
			} catch ( JsonParseException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', JsonParseException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			} catch ( JsonMappingException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', JsonMappingException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			} catch ( IOException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', IOException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			}
			Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( 
							searchIdsSet, cutoffValuesRootLevel ); 
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
			
			ImageViewerData ivd = new ImageViewerData();
			
			////////////
			//  Copy Exclude Taxonomy Set for lookup
			Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
			if( excludeTaxonomy != null ) { 
				excludeTaxonomy_Ids_Set_UserInput.addAll( excludeTaxonomy );
			}
			boolean filterNonUniquePeptides = false;
			if( filterNonUniquePeptidesString != null && filterNonUniquePeptidesString.equals( "on" ) )
				filterNonUniquePeptides = true;
			boolean filterOnlyOnePSM = false;
			if( "on".equals( filterOnlyOnePSMString ) )
				filterOnlyOnePSM = true;
			boolean filterOnlyOnePeptide = false;
			if( "on".equals( filterOnlyOnePeptideString ) )
				filterOnlyOnePeptide = true;
			
			Map<Integer, List<SearchProteinMonolinkWrapper>> wrappedMonolinks_MappedOnSearchId = new HashMap<>();
			for ( SearchDTO searchDTO : searchList ) {
				int projectSearchId = searchDTO.getProjectSearchId();
				int searchId = searchDTO.getSearchId();
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
				if ( searcherCutoffValuesSearchLevel == null ) {
					String msg = "Provided search_id " + projectSearchId + " is not found in psm peptide cutoffs.";
					log.error( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				List<SearchProteinMonolinkWrapper> wrappedMonolinks = 
						MonolinkLinkedPositions.getInstance()
						.getSearchProteinMonolinkWrapperList( searchDTO, searcherCutoffValuesSearchLevel );

				// Filter out links if requested
				if( filterNonUniquePeptides || filterOnlyOnePSM || filterOnlyOnePeptide 
						|| ( excludeTaxonomy != null && excludeTaxonomy.size() > 0 )  ) {
					///////  Output Lists, Results After Filtering
					List<SearchProteinMonolinkWrapper> wrappedMonolinksAfterFilter = new ArrayList<>( wrappedMonolinks.size() );
					for ( SearchProteinMonolinkWrapper searchProteinMonolinkWrapper : wrappedMonolinks ) {
						SearchProteinMonolink link = searchProteinMonolinkWrapper.getSearchProteinMonolink();
						// did user request removal of certain taxonomy IDs?
						if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
							boolean excludeOnProtein =
									ExcludeOnTaxonomyForProteinSequenceIdSearchId.getInstance()
									.excludeOnTaxonomyForProteinSequenceIdSearchId( 
											excludeTaxonomy_Ids_Set_UserInput, 
											link.getProtein().getProteinSequenceObject(), 
											searchId );
							if ( excludeOnProtein ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of non unique peptides?
						if( filterNonUniquePeptides  ) {
							if( link.getNumUniquePeptides() < 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						//	did they request to removal of links with only one PSM?
						if( filterOnlyOnePSM  ) {
							int psmCountForSearchId = link.getNumPsms();
							if ( psmCountForSearchId <= 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of links with only one Reported Peptide?
						if( filterOnlyOnePeptide ) {
							int peptideCountForSearchId = link.getNumPeptides();
							if ( peptideCountForSearchId <= 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						wrappedMonolinksAfterFilter.add( searchProteinMonolinkWrapper );
					}
					//  Copy new filtered list to original input variable name to overlay it
					wrappedMonolinks = wrappedMonolinksAfterFilter;
				}
				wrappedMonolinks_MappedOnSearchId.put( searchId, wrappedMonolinks );
			}
			// build the JSON data structure for monolinks
			//  Build a Map of maps of <prot id>, <prot position>, <set of search ids>
			Map<Integer, Map<Integer, Integer>> proteinLinkPositionPsmCount = new HashMap<>();
			for ( Map.Entry<Integer, List<SearchProteinMonolinkWrapper>> wrappedMonolinks_MappedOnSearchId_Entry :
				wrappedMonolinks_MappedOnSearchId.entrySet() ) {
//				Integer searchIdForEntry = wrappedMonolinks_MappedOnSearchId_Entry.getKey();
				List<SearchProteinMonolinkWrapper> wrappedMonolinks = wrappedMonolinks_MappedOnSearchId_Entry.getValue();
				for ( SearchProteinMonolinkWrapper wrappedMonolink : wrappedMonolinks ) {
					SearchProteinMonolink searchProteinMonolink = wrappedMonolink.getSearchProteinMonolink();
					Integer numPsms = searchProteinMonolink.getNumPsms();
					int protId = searchProteinMonolink.getProtein().getProteinSequenceObject().getProteinSequenceId();
					int protPosition = searchProteinMonolink.getProteinPosition();
					Map<Integer, Integer> map_keyed_by_protPosition = proteinLinkPositionPsmCount.get( protId );
					if ( map_keyed_by_protPosition == null ) {
						map_keyed_by_protPosition = new HashMap<>();
						proteinLinkPositionPsmCount.put( protId, map_keyed_by_protPosition );
					}
					map_keyed_by_protPosition.put( protPosition, numPsms );
				}
			}
			ivd.setMonolinkPSMCounts( proteinLinkPositionPsmCount );
			return ivd;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
}
