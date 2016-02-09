package com.sdl.ecommerce.dxa.model;

import com.sdl.ecommerce.api.model.Category;
import com.sdl.ecommerce.api.model.Editable;
import com.sdl.ecommerce.api.model.FacetGroup;
import com.sdl.ecommerce.api.model.Promotion;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;


import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

/**
 * Facets Widget
 *
 * @author nic
 */
@SemanticEntity(entityName = "FacetsWidget", vocabulary = SDL_CORE, prefix = "e", public_ = false)
public class FacetsWidget extends AbstractEntityModel {

    // TODO: Add metadata fields for customizing the experience

    @SemanticProperty("e:categoryPath")
    private String categoryPath;

    private Category category;

    private String categoryUrl;

    private List<FacetGroup> facetGroups;

    // TODO: Is this the best way of doing this???
    private List<Promotion> relatedPromotions;

    public String getCategoryPath() {
        return categoryPath;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(String categoryUrl) {
        this.categoryUrl = categoryUrl;
    }

    public List<FacetGroup> getFacetGroups() {
        return facetGroups;
    }

    public void setFacetGroups(List<FacetGroup> facetGroups) {
        this.facetGroups = facetGroups;
    }

    public List<Promotion> getRelatedPromotions() {
        return relatedPromotions;
    }

    public void setRelatedPromotions(List<Promotion> relatedPromotions) {
        this.relatedPromotions = relatedPromotions;
    }

    public String getPromotionViewName(Promotion promotion) {
        return promotion.getClass().getInterfaces()[0].getSimpleName();
    }

}