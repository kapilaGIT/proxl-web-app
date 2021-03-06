package org.yeastrc.xlink.www.objects;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.ProteinSequenceObject;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ProteinDescriptionFor_SearchProtein;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ProteinNameFor_SearchProtein;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.ProteinDescriptionFor_SearchProtein_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.ProteinNameFor_SearchProtein_Request;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.ProteinDescriptionFor_SearchProtein_Result;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.ProteinNameFor_SearchProtein_Result;

public class SearchProtein implements IProtein {

	private static final Logger log = Logger.getLogger(SearchProtein.class);
	
	// Instantiate SearchProtein
	public SearchProtein( SearchDTO search, ProteinSequenceObject protein ) {
		this.search = search;
		this.proteinSequenceObject = protein;
	}
	public int hashCode() {
		return ("" + this.getProteinSequenceObject().getProteinSequenceId() + "-" + this.getSearch().getSearchId() ).hashCode();
	}
	public boolean equals( Object o ) {
		if( !( o instanceof SearchProtein ) ) return false;
		if( ((SearchProtein)o).getProteinSequenceObject().getProteinSequenceId() != this.getProteinSequenceObject().getProteinSequenceId() )
			return false;
		if( ((SearchProtein)o).getSearch().getSearchId() != this.getSearch().getSearchId() )
			return false;
		return true;
	}
	public String getName() throws Exception {
		try {
			if( this.name == null ) {
				ProteinNameFor_SearchProtein_Request proteinNameFor_SearchProtein_Request = new ProteinNameFor_SearchProtein_Request();
				proteinNameFor_SearchProtein_Request.setSearchId( this.getSearch().getSearchId() );
				proteinNameFor_SearchProtein_Request.setProteinSequenceId( this.getProteinSequenceObject().getProteinSequenceId() );
				ProteinNameFor_SearchProtein_Result proteinNameFor_SearchProtein_Result = 
						Cached_ProteinNameFor_SearchProtein.getInstance()
						.getProteinNameFor_SearchProtein_Result( proteinNameFor_SearchProtein_Request );
				this.name = proteinNameFor_SearchProtein_Result.getProteinName();
			}
			return this.name;
		} catch ( Exception e ) {
			String msg = "Exception in getName()";
			log.error( msg, e );
			throw e;
		}
	}
	public String getDescription() throws Exception {
		try {
			if( this.description == null ) {
				ProteinDescriptionFor_SearchProtein_Request proteinDescriptionFor_SearchProtein_Request = new ProteinDescriptionFor_SearchProtein_Request();
				proteinDescriptionFor_SearchProtein_Request.setSearchId( this.getSearch().getSearchId() );
				proteinDescriptionFor_SearchProtein_Request.setProteinSequenceId( this.getProteinSequenceObject().getProteinSequenceId() );
				ProteinDescriptionFor_SearchProtein_Result proteinDescriptionFor_SearchProtein_Result = 
						Cached_ProteinDescriptionFor_SearchProtein.getInstance()
						.getProteinDescriptionFor_SearchProtein_Result( proteinDescriptionFor_SearchProtein_Request );
				this.description = proteinDescriptionFor_SearchProtein_Result.getProteinDescription();	
			}
			return this.description;
		} catch ( Exception e ) {
			String msg = "Exception in getDescription()";
			log.error( msg, e );
			throw e;
		}
	}
	public ProteinSequenceObject getProteinSequenceObject() {
		return proteinSequenceObject;
	}
	public void setProteinSequenceObject(ProteinSequenceObject proteinSequenceObject) {
		this.proteinSequenceObject = proteinSequenceObject;
	}
	public SearchDTO getSearch() {
		return search;
	}
	public void setSearch(SearchDTO search) {
		this.search = search;
	}
	
	private ProteinSequenceObject proteinSequenceObject;
	private SearchDTO search;
	private String name;
	private String description;
}
