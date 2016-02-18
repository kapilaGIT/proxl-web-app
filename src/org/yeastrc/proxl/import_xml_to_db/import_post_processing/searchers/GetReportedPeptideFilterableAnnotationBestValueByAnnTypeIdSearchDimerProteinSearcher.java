package org.yeastrc.proxl.import_xml_to_db.import_post_processing.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.import_post_processing.objects.BestFilterableAnnotationValue;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDimerGenericLookupDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;


/**
 * 
 * Get "best" value from srch__rep_pept__annotation for annotation_type_id, search_id and searchDimerGenericLookupDTO
 */
public class GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchDimerProteinSearcher {
	
	private static final Logger log = Logger.getLogger(GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchDimerProteinSearcher.class);
	
	private GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchDimerProteinSearcher() { }
	private static final GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchDimerProteinSearcher _INSTANCE = new GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchDimerProteinSearcher();
	public static GetReportedPeptideFilterableAnnotationBestValueByAnnTypeIdSearchDimerProteinSearcher getInstance() { return _INSTANCE; }
	
	

	/**
	 * Get "best" value from srch__rep_pept__annotation for annotation_type_id, search_id and searchDimerGenericLookupDTO
	 * 
	 * @param annotation_type_id
	 * @param searchDimerGenericLookupDTO
	 * @param filterDirection - for peptide_filterable_annotation_type based on annotation_type_id
	 * @return null if no record found for selection criteria
	 * @throws Exception
	 */
	public BestFilterableAnnotationValue getBestAnnotationValue( int annotation_type_id, SearchDimerGenericLookupDTO searchDimerGenericLookupDTO, FilterDirectionType filterDirectionType ) throws Exception {
		
		BestFilterableAnnotationValue result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String orderDirection = null;
		

		if ( filterDirectionType == FilterDirectionType.ABOVE ) {
			
			orderDirection = "DESC";  //  Largest best so sort so largest is first
					
		} else if ( filterDirectionType == FilterDirectionType.BELOW ) {
			
			orderDirection = "ASC";  //  Smallest best so sort so smallest is first
			
		} else {
			
			throw new IllegalArgumentException( "filterDirection Unknown value" + filterDirectionType.toString() );
		}

		
		final String sql = 
				"SELECT value_double, value_string FROM srch__rep_pept__annotation " 
						+ " INNER JOIN psm "
						+ 	" ON srch__rep_pept__annotation.search_id = psm.search_id "
						+ 	"    AND srch__rep_pept__annotation.reported_peptide_id = psm.reported_peptide_id  "
						
						+ " INNER JOIN dimer ON dimer.psm_id = psm.id "
						
						+ " WHERE srch__rep_pept__annotation.annotation_type_id = ? "
						+ " AND  psm.search_id = ? "
						+ " AND  dimer.nrseq_id_1 = ? AND dimer.nrseq_id_2 = ?  "

						+ " ORDER BY value_double " + orderDirection + " LIMIT 1 ";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, annotation_type_id );
			paramCounter++;
			pstmt.setInt( paramCounter, searchDimerGenericLookupDTO.getSearchId() );
			paramCounter++;
			pstmt.setInt( paramCounter, searchDimerGenericLookupDTO.getNrseqId1() );
			paramCounter++;
			pstmt.setInt( paramCounter, searchDimerGenericLookupDTO.getNrseqId2() );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {

				result = new BestFilterableAnnotationValue();
				
				
				result.setBestValue( rs.getDouble( "value_double" ) );
				result.setBestValueString( rs.getString( "value_string" ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getBestAnnotationValue(), sql: " + sql;
			
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		return result;
	}
	
	
}
