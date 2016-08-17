package org.yeastrc.xlink.www.proxl_xml_file_import.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingStatusValLkupDTO;

/**
 * 
 * table proxl_xml_file_import_tracking_status_values_lookup
 */
public class ProxlXMLFileImportTrackingStatusValuesLookupDAO {

	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTrackingStatusValuesLookupDAO.class);
	

	//  private constructor
	private ProxlXMLFileImportTrackingStatusValuesLookupDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static ProxlXMLFileImportTrackingStatusValuesLookupDAO getInstance() { 
		return new ProxlXMLFileImportTrackingStatusValuesLookupDAO(); 
	}
	
	



	/**
	 * @return 
	 * @throws Exception
	 */
	public List<ProxlXMLFileImportTrackingStatusValLkupDTO> getAll( ) throws Exception {


		List<ProxlXMLFileImportTrackingStatusValLkupDTO>  returnList = new ArrayList<>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		final String sql =  "SELECT * FROM proxl_xml_file_import_tracking_status_values_lookup ";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			
			rs = pstmt.executeQuery();
			
			while( rs.next() ) {
				
				ProxlXMLFileImportTrackingStatusValLkupDTO returnItem = populateResultObject( rs );
				
				returnList.add(returnItem);
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select ProxlXMLFileImportTrackingStatusValLkupDTO, sql: " + sql;
			
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
		
		return returnList;
	}
	

	

	/**
	 * @param id
	 * @return 
	 * @throws Exception
	 */
	public ProxlXMLFileImportTrackingStatusValLkupDTO getForId( int id ) throws Exception {


		ProxlXMLFileImportTrackingStatusValLkupDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM proxl_xml_file_import_tracking WHERE id = ?";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				
				result = populateResultObject( rs );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select ProxlXMLFileImportTrackingStatusValLkupDTO, id: " + id + ", sql: " + sql;
			
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
	
	
	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public ProxlXMLFileImportTrackingStatusValLkupDTO populateResultObject(ResultSet rs) throws SQLException {
		
		ProxlXMLFileImportTrackingStatusValLkupDTO returnItem = new ProxlXMLFileImportTrackingStatusValLkupDTO();
		
		returnItem.setId( rs.getInt( "id" ) );
		
		returnItem.setStatusDisplayText( rs.getString( "display_text" ) );
		
		return returnItem;
	}
	
}
