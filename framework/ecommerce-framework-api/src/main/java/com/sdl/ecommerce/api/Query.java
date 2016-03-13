package com.sdl.ecommerce.api;

import com.sdl.ecommerce.api.model.Category;
import com.sdl.ecommerce.api.model.FacetParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Query
 *
 * Abstract base class for query implementations.
 *
 * @author nic
 */
public abstract class Query implements Cloneable {

    // TODO: Add the possiblity to attach filters that can exclude for example all products that do not have a thumbnail etc
    // Have this as abstract instead??
    // And then allow to hook in extensions here somehow?? Or is that query filter???? where entities and customizations can add specific stuff there (which is up to the implementation to handle or not...)


    private Category category;
    private String searchPhrase;
    private List<FacetParameter> facets;
    private int startIndex;
    private int viewSize;
    private List<QueryFilterAttribute> filterAttributes;
    private ViewType viewType;

    // TODO: Add Localization here as one important parameter
    // TODO: Add support for sorting

    public Query category(Category category) {
        this.category = category;
        return this;
    }

    public Query searchPhrase(String searchPhrase) {
        this.searchPhrase = searchPhrase;
        return this;
    }

    public Query facets(List<FacetParameter> facets) {
        this.facets = facets;
        return this;
    }

    public Query facet(FacetParameter facet) {
        if ( this.facets == null ) {
            this.facets = new ArrayList<>();
        }
        this.facets.add(facet);
        return this;
    }

    public Query startIndex(int startIndex) {
        this.startIndex = startIndex;
        return this;
    }

    public Query viewSize(int viewSize) {
        this.viewSize = viewSize;
        return this;
    }

    public Query filterAttribute(QueryFilterAttribute filterAttribute) {
        if ( this.filterAttributes == null ) {
            this.filterAttributes = new ArrayList<>();
        }
        this.filterAttributes.add(filterAttribute);
        return this;
    }

    public Query viewType(ViewType viewType) {
        this.viewType = viewType;
        return this;
    }

    public Category getCategory() {
        return category;
    }

    public List<FacetParameter> getFacets() {
        return facets;
    }

    public String getSearchPhrase() {
        return searchPhrase;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getViewSize() {
        return viewSize;
    }

    public List<QueryFilterAttribute> getFilterAttributes() {
        return filterAttributes;
    }

    public ViewType getViewType() {
        return viewType;
    }

    public Query clone() {
        try {
            return (Query) super.clone();
        }
        catch ( CloneNotSupportedException e ) {
            throw new RuntimeException("Could not clone E-Commerce query", e);
        }
    }
}
