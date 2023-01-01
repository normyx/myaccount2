import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnChanges } from '@angular/core';
import { ChartConfiguration, ChartOptions, Tick } from 'chart.js';
import 'chartjs-adapter-moment';
import dayjs from 'dayjs';
import { MyaDashboardService } from '../../service/mya-dashboard.service';

@Component({
  selector: 'jhi-mya-evolution-in-month',
  templateUrl: './mya-evolution-in-month.component.html',
})
export class MyaEvolutionInMonthReportComponent implements OnChanges {
  @Input() month: Date | null = null;
  @Input() categoryId: number | null = null;
  @Input() height = '40vh';
  @Input() displayX = true;
  // accountCategoryMonthReport: IAccountCategoryMonthReport;
  data: ChartConfiguration<'line'>['data'] | null = null;
  options: ChartOptions<'line'> | null = null;

  constructor(private dashboardService: MyaDashboardService) {}

  feedReportData(res: HttpResponse<any>): void {
    this.data = {
      labels: res.body.dates,
      datasets: [
        {
          label: 'Operation',
          data: res.body.operationAmounts,
          borderColor: '#0099ff',
          backgroundColor: '#0099ff',
          pointBorderColor: '#0099ff',
          pointBackgroundColor: '#0099ff',
          fill: false,
          pointRadius: 0,
          cubicInterpolationMode: 'monotone',
          tension: 0.4,
          borderWidth: 2,
        },
        {
          label: 'Budget',
          data: res.body.budgetAmounts,
          borderColor: '#565656',
          backgroundColor: '#565656',
          pointBorderColor: '#565656',
          pointBackgroundColor: '#565656',
          borderWidth: 1,
          fill: false,
          pointRadius: 0,
          cubicInterpolationMode: 'monotone',
          tension: 0.4,
        },
        {
          label: 'Evolution prévue',
          data: res.body.predictiveBudgetAmounts,
          borderColor: '#ff0000',
          backgroundColor: '#ff0000',
          pointBorderColor: '#ff0000',
          pointBackgroundColor: '#ff0000',
          fill: false,
          pointRadius: 0,
          cubicInterpolationMode: 'monotone',
          tension: 0.4,
          borderWidth: 2,
        },
      ],
    };
    this.options = {
      responsive: true,
      plugins: {
        legend: {
          display: false,
          position: 'bottom',
        },
        tooltip: {
          position: 'average',
          mode: 'index',
          intersect: false,
          callbacks: {
            title(context: any): string[] {
              return [dayjs(context[0].label).format('DD MMM-YY')];
            },
            label(context: any): string {
              let label: string = context.dataset.label || '';
              if (label) {
                label += ' : ';
              }
              label += Math.round(context.parsed.y * 100) / 100;
              label += ' €';
              return label;
            },
          },
        },
      },
      scales: {
        y: {
          title: {
            display: false,
            text: 'Montants',
            //labelString: 'Montants',
          },
          ticks: {
            //suggestedMax: 0,
            callback(value: string | number, index: number, ticks: Tick[]): string {
              return String(value) + ' €';
            },
          },
        },
        x: {
          display: this.displayX,
          title: {
            display: true,
          },
          type: 'time',

          time: {
            unit: 'day',
            stepSize: 1,
            displayFormats: {
              month: 'DD MMM-YY',
            },
          },
        },
      },
    };
  }

  loadAll(): void {
    if (this.month) {
      this.dashboardService
        .getEvolutionInMonth(dayjs(this.month), this.categoryId)
        .subscribe((res: HttpResponse<any>) => this.feedReportData(res));
    }
  }

  ngOnChanges(): void {
    this.loadAll();
  }
}
