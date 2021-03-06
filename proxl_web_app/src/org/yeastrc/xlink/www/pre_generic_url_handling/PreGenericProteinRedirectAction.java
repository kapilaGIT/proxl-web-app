package org.yeastrc.xlink.www.pre_generic_url_handling;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesAnnotationLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.servlet_context.CurrentContext;
import org.yeastrc.xlink.www.web_utils.URLEncodeDecodeAURL;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class PreGenericProteinRedirectAction extends Action {

	private static final Logger log = Logger.getLogger(PreGenericPeptideRedirectAction.class);

	public ActionForward execute( ActionMapping mapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response )
					throws Exception {


		try {
			PreGenericProteinForm form = (PreGenericProteinForm) actionForm;

			//  Detect which Struts action mapping was called by examining the value of the "parameter" attribute
			//     accessed by calling mapping.getParameter()

			String strutsActionURLToRedirectTo = mapping.getParameter();

			boolean mergedAction = false;

			if ( strutsActionURLToRedirectTo.contains( "erged" ) ) {

				mergedAction = true;
			}


			Set<Integer> searchIdsSet = new HashSet<>();

			
			StringBuilder redirectURLSB = new StringBuilder( 1000 );
			
			redirectURLSB.append( CurrentContext.getCurrentWebAppContext() );
			redirectURLSB.append( strutsActionURLToRedirectTo ); 
			redirectURLSB.append( ".do?" );		
					

			if ( form.getSearchIds() == null ) {

				String msg = "form.getSearchIds() == null for strutsActionURLToRedirectTo: " + strutsActionURLToRedirectTo;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}

			if ( mergedAction ) {

				for ( int index = 0; index < form.getSearchIds().length; index++ ) {
					
					int searchId = form.getSearchIds()[ index ];
					
					if ( index > 0 ) {
						
						redirectURLSB.append( "&" );	
					}
					
					redirectURLSB.append( "searchIds=" );
					redirectURLSB.append( Integer.toString( searchId ) );

					searchIdsSet.add( searchId );
				}
			} else {

				redirectURLSB.append( "searchId=" );
				redirectURLSB.append( Integer.toString( form.getSearchId() ) );

				searchIdsSet.add( form.getSearchId() );

			}


			ProteinQueryJSONRoot proteinQueryJSONRoot = new ProteinQueryJSONRoot();


			proteinQueryJSONRoot.setExcludeProtein( form.getExcludeProtein() );
			proteinQueryJSONRoot.setExcludeTaxonomy( form.getExcludeTaxonomy() );
			proteinQueryJSONRoot.setFilterNonUniquePeptides( form.isFilterNonUniquePeptides() );
			proteinQueryJSONRoot.setFilterOnlyOnePeptide( form.isFilterOnlyOnePeptide() );
			proteinQueryJSONRoot.setFilterOnlyOnePSM( form.isFilterOnlyOnePSM() );


			CutoffValuesRootLevel cutoffValuesRootLevel = new CutoffValuesRootLevel();

			proteinQueryJSONRoot.setCutoffs( cutoffValuesRootLevel );

			
			
			//  Copy psm and peptpide cutoffs to the Generic
			


			Map<Integer, Map<Integer, AnnotationTypeDTO>> psmFilterableAnnotationType_DTOMapPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIdsSet );

			Map<Integer, Map<Integer, AnnotationTypeDTO>> peptideFilterableAnnotationType_DTOMapPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsSet );


			String psmQValueCutoffString = Double.toString( form.getPsmQValueCutoff() );
			String peptideQValueCutoff = Double.toString( form.getPeptideQValueCutoff() );


			Map<String, CutoffValuesSearchLevel> searches = cutoffValuesRootLevel.getSearches();

			for ( Integer searchId : searchIdsSet ) {

				CutoffValuesSearchLevel search = new CutoffValuesSearchLevel();

				searches.put( searchId.toString(), search);
				
				search.setSearchId( searchId );
				
				Map<String,CutoffValuesAnnotationLevel> psmCutoffValuesMap = search.getPsmCutoffValues();
				Map<String,CutoffValuesAnnotationLevel> peptideCutoffValuesMap = search.getPeptideCutoffValues();
				
				Map<Integer, AnnotationTypeDTO> psmFilterableAnnotationType_DTOMap =
						psmFilterableAnnotationType_DTOMapPerSearchIdMap.get( searchId );

				Map<Integer, AnnotationTypeDTO> peptideFilterableAnnotationType_DTOMap =
						peptideFilterableAnnotationType_DTOMapPerSearchIdMap.get( searchId );
				
				
				processPsmOrPeptideAnnotationType(
						"psm",// psmOrPeptideType
						searchId,
						psmQValueCutoffString,
						psmFilterableAnnotationType_DTOMap,
						psmCutoffValuesMap );

				processPsmOrPeptideAnnotationType(
						"peptide", // psmOrPeptideType
						searchId,
						peptideQValueCutoff,
						peptideFilterableAnnotationType_DTOMap,
						peptideCutoffValuesMap );
			}


			//  Jackson JSON Mapper object for JSON deserialization and serialization

			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

			String proteinQueryJSONRootString = jacksonJSON_Mapper.writeValueAsString( proteinQueryJSONRoot );


			//  Create URI Encoded JSON for passing to Image and Structure pages in hash 

			String proteinQueryJSONRootStringURIEncoded = URLEncodeDecodeAURL.urlEncodeAURL( proteinQueryJSONRootString );

			
			redirectURLSB.append( "&queryJSON=" );
			redirectURLSB.append( proteinQueryJSONRootStringURIEncoded );
			
			

			String redirectURL = redirectURLSB.toString();

			response.sendRedirect( redirectURL );


		} catch ( Exception e ) {

			String msg = "Exception caught: " + e.toString();

			log.error( msg, e );

			return mapping.findForward( StrutsGlobalForwardNames.GENERAL_ERROR );
		}

		return null;  // nothing to forward to since setting redirect here
	}



	/**
	 * @param psmOrPeptideType
	 * @param searchId
	 * @param value
	 * @param annotationType_DTOMap
	 * @param cutoffValuesMap
	 * @throws ProxlWebappDataException
	 */
	private void processPsmOrPeptideAnnotationType(
			String psmOrPeptideType,
			Integer searchId,
			String value,
			Map<Integer, AnnotationTypeDTO> annotationType_DTOMap,
			Map<String,CutoffValuesAnnotationLevel> cutoffValuesMap ) throws ProxlWebappDataException {

		boolean foundQValueAnnotationTypeDTO = false;

		for ( Map.Entry<Integer, AnnotationTypeDTO> annotationTypeDTOEntry : annotationType_DTOMap.entrySet() ) {

			AnnotationTypeDTO annotationTypeDTO = annotationTypeDTOEntry.getValue();

			if ( PreGenericURLHandlingConstants.ANNOTATION_TYPE_NAME_Q_VALUE.equals( annotationTypeDTO.getName() ) ) {

				CutoffValuesAnnotationLevel cutoffValuesAnnotationLevel = new CutoffValuesAnnotationLevel();

				cutoffValuesAnnotationLevel.setId( annotationTypeDTO.getId() );
				cutoffValuesAnnotationLevel.setValue( value );
				String annotationTypeIdString = Integer.toString( annotationTypeDTO.getId() );

				cutoffValuesMap.put( annotationTypeIdString, cutoffValuesAnnotationLevel );
				
				foundQValueAnnotationTypeDTO = true;

				break;
			}
		}
		
		if ( ! foundQValueAnnotationTypeDTO ) {
			
			String msg = "Failed to find " + psmOrPeptideType + " annotation type with name '" 
					+ PreGenericURLHandlingConstants.ANNOTATION_TYPE_NAME_Q_VALUE
					+ "' for search id: " + searchId;
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}
	}
	
}
