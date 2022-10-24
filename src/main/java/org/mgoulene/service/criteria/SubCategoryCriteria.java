package org.mgoulene.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.mgoulene.domain.SubCategory} entity. This class is used
 * in {@link org.mgoulene.web.rest.SubCategoryResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /sub-categories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubCategoryCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter subCategoryName;

    private LongFilter categoryId;

    private Boolean distinct;

    public SubCategoryCriteria() {}

    public SubCategoryCriteria(SubCategoryCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.subCategoryName = other.subCategoryName == null ? null : other.subCategoryName.copy();
        this.categoryId = other.categoryId == null ? null : other.categoryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SubCategoryCriteria copy() {
        return new SubCategoryCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getSubCategoryName() {
        return subCategoryName;
    }

    public StringFilter subCategoryName() {
        if (subCategoryName == null) {
            subCategoryName = new StringFilter();
        }
        return subCategoryName;
    }

    public void setSubCategoryName(StringFilter subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            categoryId = new LongFilter();
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SubCategoryCriteria that = (SubCategoryCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(subCategoryName, that.subCategoryName) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subCategoryName, categoryId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubCategoryCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (subCategoryName != null ? "subCategoryName=" + subCategoryName + ", " : "") +
            (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
