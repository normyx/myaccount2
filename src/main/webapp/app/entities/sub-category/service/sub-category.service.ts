import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ISubCategory, NewSubCategory } from '../sub-category.model';

export type PartialUpdateSubCategory = Partial<ISubCategory> & Pick<ISubCategory, 'id'>;

export type EntityResponseType = HttpResponse<ISubCategory>;
export type EntityArrayResponseType = HttpResponse<ISubCategory[]>;

@Injectable({ providedIn: 'root' })
export class SubCategoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sub-categories');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/sub-categories');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(subCategory: NewSubCategory): Observable<EntityResponseType> {
    return this.http.post<ISubCategory>(this.resourceUrl, subCategory, { observe: 'response' });
  }

  update(subCategory: ISubCategory): Observable<EntityResponseType> {
    return this.http.put<ISubCategory>(`${this.resourceUrl}/${this.getSubCategoryIdentifier(subCategory)}`, subCategory, {
      observe: 'response',
    });
  }

  partialUpdate(subCategory: PartialUpdateSubCategory): Observable<EntityResponseType> {
    return this.http.patch<ISubCategory>(`${this.resourceUrl}/${this.getSubCategoryIdentifier(subCategory)}`, subCategory, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISubCategory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISubCategory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISubCategory[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getSubCategoryIdentifier(subCategory: Pick<ISubCategory, 'id'>): number {
    return subCategory.id;
  }

  compareSubCategory(o1: Pick<ISubCategory, 'id'> | null, o2: Pick<ISubCategory, 'id'> | null): boolean {
    return o1 && o2 ? this.getSubCategoryIdentifier(o1) === this.getSubCategoryIdentifier(o2) : o1 === o2;
  }

  addSubCategoryToCollectionIfMissing<Type extends Pick<ISubCategory, 'id'>>(
    subCategoryCollection: Type[],
    ...subCategoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const subCategories: Type[] = subCategoriesToCheck.filter(isPresent);
    if (subCategories.length > 0) {
      const subCategoryCollectionIdentifiers = subCategoryCollection.map(
        subCategoryItem => this.getSubCategoryIdentifier(subCategoryItem)!
      );
      const subCategoriesToAdd = subCategories.filter(subCategoryItem => {
        const subCategoryIdentifier = this.getSubCategoryIdentifier(subCategoryItem);
        if (subCategoryCollectionIdentifiers.includes(subCategoryIdentifier)) {
          return false;
        }
        subCategoryCollectionIdentifiers.push(subCategoryIdentifier);
        return true;
      });
      return [...subCategoriesToAdd, ...subCategoryCollection];
    }
    return subCategoryCollection;
  }
}
