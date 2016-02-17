package com.sdl.ecommerce.demandware;

import com.sdl.ecommerce.api.ECommerceException;
import com.sdl.ecommerce.api.ProductCategoryService;
import com.sdl.ecommerce.api.model.Category;
import com.sdl.ecommerce.demandware.api.DemandwareShopClient;
import com.sdl.ecommerce.demandware.model.DemandwareCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Demandware Category Service
 *
 * @author nic
 */
@Component
public class DemandwareCategoryService implements ProductCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(DemandwareCategoryService.class);

    @Autowired
    private DemandwareShopClient shopClient;

    private Category rootCategory = new DemandwareCategory(null, "root", null);

    @Value("${demandware.categoryExpiryTimeout}")
    private long categoryExpiryTimeout = 60*1000*1000; // categories refreshed each 1 hour

    @PostConstruct
    public void initialize() {
        // TODO: Should this be one at initialize? This makes the whole startup a bit senstive...
        this.readTopLevelCategories();
    }

    @Override
    public Category getCategoryById(String id) throws ECommerceException {
        return this.getCategoryById(id, this.getTopLevelCategories());
    }

    private Category getCategoryById(String id, List<Category> categories) {
        for ( Category category : categories ) {
            if ( category.getId().equals(id) ) {
                return category;
            }
            if ( ((DemandwareCategory)category).needsRefresh() && id.startsWith(category.getId()) ) {
                this.loadCategories(category);
            }
            if ( category.getCategories() != null ) {
                Category foundCategory = this.getCategoryById(id, category.getCategories());
                if (foundCategory != null) {
                    return foundCategory;
                }
            }
        }
        return null;
    }

    @Override
    public Category getCategoryByPath(String path)  throws ECommerceException {

        LOG.debug("Getting category by path: " + path);
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        Category category = null;
        while ( tokenizer.hasMoreTokens() ) {
            String pathName = tokenizer.nextToken();
            if ( category == null ) {
                category = getCategoryByPathName(this.getTopLevelCategories(), pathName);
            }
            else {
                if ( ((DemandwareCategory)category).needsRefresh() ) {
                    this.loadCategories(category);
                }
                category = getCategoryByPathName(category.getCategories(), pathName);
            }
            if ( category == null ) {
                return null;
            }
        }
        if ( category != null && ((DemandwareCategory)category).needsRefresh() ) {
            this.loadCategories(category);
        }
        return category;
    }

    private Category getCategoryByPathName(List<Category> categories, String pathName) {
        for ( Category category : categories ) {
            if ( category.getPathName().equals(pathName) ) {
                return category;
            }
        }
        return null;
    }

    private List<Category> getTopLevelCategories() {
        if ( ((DemandwareCategory)this.rootCategory).needsRefresh() ) {
            loadCategories(this.rootCategory);
        }
        return this.rootCategory.getCategories();
    }

    private void readTopLevelCategories() {

        List<com.sdl.ecommerce.demandware.api.model.Category> topLevelCategories = this.shopClient.getTopLevelCategories(2);
        List<Category> categories = new ArrayList<>();
        for (com.sdl.ecommerce.demandware.api.model.Category dwreCategory : topLevelCategories ) {
            DemandwareCategory category = new DemandwareCategory(null, dwreCategory.getId(), dwreCategory.getName());
            categories.add(category);
            if ( dwreCategory.getCategories() != null ) {
                List<Category> subCategories = new ArrayList<>();
                for ( com.sdl.ecommerce.demandware.api.model.Category subDwreCategory : dwreCategory.getCategories() ) {
                    Category subCategory = new DemandwareCategory(category, subDwreCategory.getId(), subDwreCategory.getName());
                     subCategories.add(subCategory);
                }
                category.setCategories(subCategories, System.currentTimeMillis() + categoryExpiryTimeout);
            }
        }
        ((DemandwareCategory) this.rootCategory).setCategories(categories, System.currentTimeMillis() + categoryExpiryTimeout);
    }

    private void loadCategories(Category parent) {

        LOG.debug("Loading sub-categories for category: " + (parent == this.rootCategory ? "ROOT" : parent.getName()));

        List<com.sdl.ecommerce.demandware.api.model.Category> dwreCategories = this.shopClient.getCategories(parent.getId(),1);
        List<Category> categories;
        if ( dwreCategories != null ) {
            categories = this.toCategoryList(parent != this.rootCategory ? parent : null, dwreCategories);
            List<Category> existingCategories = parent.getCategories();

            if (existingCategories != null) {
                // If a refresh -> rebuild the list and reuse items
                //
                synchronized (parent) {
                    for (int i = 0; i < categories.size(); i++) {
                        Category category = categories.get(i);
                        Category existingCategory = this.getCategory(category.getId(), existingCategories);
                        if (existingCategory != null) {
                            categories.add(i, existingCategory);
                            categories.remove(i + 1);
                        }
                    }
                }
            }
        }
        else {
            categories = new ArrayList<>();
        }
        ((DemandwareCategory) parent).setCategories(categories, System.currentTimeMillis() + this.categoryExpiryTimeout);
    }

    private List<Category> toCategoryList(Category parent, List<com.sdl.ecommerce.demandware.api.model.Category> dwreCategories) {
        List<Category> categories = new ArrayList<>();
        for (com.sdl.ecommerce.demandware.api.model.Category dwreCategory : dwreCategories ) {
            DemandwareCategory category = new DemandwareCategory(parent, dwreCategory.getId(), dwreCategory.getName());
            categories.add(category);
        }
        return categories;
    }

    private Category getCategory(String id, List<Category> categories) {
        for ( Category category : categories ) {
            if ( category.getId().equals(id) ) {
                return category;
            }
        }
        return null;
    }

}