import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import dayjs from 'dayjs';
import { Dayjs } from 'dayjs';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApplicationConfigService } from '../../../core/config/application-config.service';
import { createRequestOption } from '../../../core/request/request-util';
import { SearchWithPagination } from '../../../core/request/request.model';
import { IOperation } from '../../../entities/operation/operation.model';
import { EntityArrayResponseType, OperationService, RestOperation } from '../../../entities/operation/service/operation.service';
import { MyaDateUtils } from '../../util/mya-date-util.service';
import { MyaFileUploadModel, uploadFile } from '../../util/mya-file-upload.service';
import {
  convertResponseImportOperationActionsFromServer,
  IOperationImportActions,
  RestOperationImportActions,
} from '../import/mya-operation-import-action.model';

export type PartialUpdateOperation = Partial<IOperation> & Pick<IOperation, 'id'>;

@Injectable({ providedIn: 'root' })
export class MyaOperationService extends OperationService {
  static readonly MYA_OPERATION_API = 'api/mya-operations';
  static readonly MYA_OPERATION_SEARCH_API = 'api/_search/mya-operations';
  static readonly MYA_WITH_SIGNEDIN_USER_SUFFIX = '/with-signedin-user';
  static readonly MYA_CLOSE_TO_BUDGET_SUFFIX = '/close-to-budget';
  static readonly MYA_XITH_BUDGET_ITEM_PERIOD_SUFFIX = '/with-budget_item-period';
  protected myaResourceUrl = this.applicationConfigService.getEndpointFor(MyaOperationService.MYA_OPERATION_API);
  protected myaResourceSearchUrl = this.applicationConfigService.getEndpointFor(MyaOperationService.MYA_OPERATION_SEARCH_API);
  protected myaResourceFromSignedInUserUrl = this.applicationConfigService.getEndpointFor(
    MyaOperationService.MYA_OPERATION_API + MyaOperationService.MYA_WITH_SIGNEDIN_USER_SUFFIX
  );
  protected myaResourceFromSignedInUserSearchUrl = this.applicationConfigService.getEndpointFor(
    MyaOperationService.MYA_OPERATION_SEARCH_API + MyaOperationService.MYA_WITH_SIGNEDIN_USER_SUFFIX
  );

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService, protected dateUtils: MyaDateUtils) {
    super(http, applicationConfigService);
  }

  queryWithSignedInUser(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOperation[]>(this.myaResourceFromSignedInUserUrl, { params: options, observe: 'response' })
      .pipe(map(res => super.convertResponseArrayFromServer(res)));
  }
  searchWithSignedInUser(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOperation[]>(this.myaResourceFromSignedInUserSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => super.convertResponseArrayFromServer(res)));
  }

  countOperationsCloseToBudgetItemPeriod(
    amount: number | null | undefined,
    categoryId: number | null | undefined,
    date: Dayjs | null | undefined
  ): Observable<HttpResponse<number>> {
    if (!date || !amount || !categoryId) {
      throw new Error('Date does not have to be null');
    }
    const dateStr: string = this.dateUtils.convertToString(date);
    return this.http.get<number>(
      `${this.myaResourceUrl}${MyaOperationService.MYA_CLOSE_TO_BUDGET_SUFFIX}/count/${amount}/${categoryId}/${dateStr}`,
      {
        observe: 'response',
      }
    );
  }

  sumOfAmountForBankAccount(bankAccountId: number): Observable<HttpResponse<number>> {
    return this.http.get<number>(`${this.myaResourceUrl}/bank-account/amount/${bankAccountId}`, {
      observe: 'response',
    });
  }

  lastOperationDateForBankAccount(bankAccountId: number): Observable<HttpResponse<Dayjs>> {
    return this.http
      .get<string>(`${this.myaResourceUrl}/bank-account/lastOperationDate/${bankAccountId}`, {
        observe: 'response',
      })
      .pipe(map(res => res.clone({ body: dayjs(res.body) })));
  }

  findOperationsCloseToBudgetItemPeriod(amount: number, categoryId: number, date: Dayjs): Observable<EntityArrayResponseType> {
    const dateStr: string = this.dateUtils.convertToString(date);
    return this.http
      .get<RestOperation[]>(
        `${this.myaResourceUrl}${MyaOperationService.MYA_CLOSE_TO_BUDGET_SUFFIX}/get/${amount}/${categoryId}/${dateStr}`,
        {
          observe: 'response',
        }
      )
      .pipe(map(res => super.convertResponseArrayFromServer(res)));
  }
  deleteWithBudgetItemPeriod(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.myaResourceUrl}${MyaOperationService.MYA_XITH_BUDGET_ITEM_PERIOD_SUFFIX}/${id}`, {
      observe: 'response',
    });
  }
  uploadOperations(file: MyaFileUploadModel): Observable<HttpResponse<IOperationImportActions>> {
    return uploadFile(file, `${this.myaResourceUrl}/upload-csv-file`, this.http).pipe(
      map(res => convertResponseImportOperationActionsFromServer(res))
    );
  }

  validateOperationsImport(actions: RestOperationImportActions): Observable<any> {
    return this.http.post<any>(`${this.myaResourceUrl}/validate-operations-import`, actions, { observe: 'response' });
  }
}
