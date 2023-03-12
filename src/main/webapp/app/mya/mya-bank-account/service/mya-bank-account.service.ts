import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IRealEstateItem } from 'app/entities/real-estate-item/real-estate-item.model';
import { Observable } from 'rxjs';
import { ApplicationConfigService } from '../../../core/config/application-config.service';
import { createRequestOption } from '../../../core/request/request-util';
import { IBankAccount } from '../../../entities/bank-account/bank-account.model';
import { BankAccountService, EntityArrayResponseType } from '../../../entities/bank-account/service/bank-account.service';

@Injectable({ providedIn: 'root' })
export class MyaBankAccountService extends BankAccountService {
  static readonly MYA_BANK_ACCOUNT_API = 'api/mya/bank-accounts';
  static readonly MYA_WITH_SIGNEDIN_USER_SUFFIX = '/with-signedin-user';
  protected myaResourceUrl = this.applicationConfigService.getEndpointFor(MyaBankAccountService.MYA_BANK_ACCOUNT_API);
  protected myaResourceFromSignedInUserUrl = this.applicationConfigService.getEndpointFor(
    MyaBankAccountService.MYA_BANK_ACCOUNT_API + MyaBankAccountService.MYA_WITH_SIGNEDIN_USER_SUFFIX
  );

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {
    super(http, applicationConfigService);
  }

  queryWithSignedInUser(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBankAccount[]>(this.myaResourceFromSignedInUserUrl, { params: options, observe: 'response' });
  }

  lastRealEstateItemFromBankAccount(bankAccountId: number): Observable<HttpResponse<IRealEstateItem>> {
    return this.http.get<IRealEstateItem>(
      this.applicationConfigService.getEndpointFor('api/mya-real-estate-items/last-from-bank-account/' + bankAccountId.toString()),
      { observe: 'response' }
    );
  }
}
