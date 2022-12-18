import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
//import { ChartModule } from 'primeng/chart';
import { SharedModule } from '../../shared/shared.module';
import { MyaBudgetItemModule } from '../mya-budget-item/mya-budget-item.module';
import { MyaOperationModule } from '../mya-operation/mya-operation.module';
import { MyaDashboardAccountComponent } from './account/mya-dashboard-account.component';
import { MyaDashboardCategoryDetailsComponent } from './category-details/mya-dashboard-category-details.component';
import { MyaDashboardCategoryComponent } from './category/mya-dashboard-category.component';
import { MyaCategorySplitComponent } from './components/mya-category-split/mya-category-split.component';
import { MyaCategorySummaryComponent } from './components/mya-category-summary/mya-caterory-summary.component';
import { MyaCategoryWeatherComponent } from './components/mya-category-weather/mya-category-weather.component';
import { MyaEvolutionBetweenDatesForBankAccountComponent } from './components/mya-evolution-between-dates-for-bank-account/mya-evolution-between-dates-for-bank-account.component';
import { MyaEvolutionBetweenDatesComponent } from './components/mya-evolution-between-dates/mya-evolution-between-dates.component';
import { MyaEvolutionByMonthsWithSmoothedAndMarkedComponent } from './components/mya-evolution-by-months-with-smoothed-and-marked/mya-evolution-by-months-with-smoothed-and-marked.component';
import { MyaEvolutionByMonthsComponent } from './components/mya-evolution-by-months/mya-evolution-by-months.component';
import { MyaEvolutionInMonthReportComponent } from './components/mya-evolution-in-month/mya-evolution-in-month.component';
import { MyaDashboardRoutingModule } from './route/mya-dashboard-routing.module';
import { NgChartsModule } from 'ng2-charts';
import { MyaAccountEvolutionComponent } from './components/mya-account-evolution/mya-account-evolution';

@NgModule({
  imports: [
    SharedModule,
    /*ChartModule,*/ MyaDashboardRoutingModule,
    MyaBudgetItemModule,
    MyaOperationModule,
    BsDatepickerModule.forRoot(),
    NgChartsModule,
  ],
  declarations: [
    MyaDashboardAccountComponent,
    MyaEvolutionBetweenDatesComponent,
    MyaEvolutionInMonthReportComponent,
    MyaEvolutionByMonthsComponent,
    MyaCategorySummaryComponent,
    MyaCategoryWeatherComponent,
    MyaEvolutionByMonthsWithSmoothedAndMarkedComponent,
    MyaCategorySplitComponent,
    MyaDashboardCategoryComponent,
    MyaDashboardCategoryDetailsComponent,
    MyaEvolutionBetweenDatesForBankAccountComponent,
    MyaAccountEvolutionComponent,
  ],
  exports: [MyaEvolutionBetweenDatesComponent, MyaAccountEvolutionComponent],
  entryComponents: [MyaEvolutionBetweenDatesComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class MyaDashboardModule {}
