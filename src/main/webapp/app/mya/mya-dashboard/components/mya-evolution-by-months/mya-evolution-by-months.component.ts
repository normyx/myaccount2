import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnChanges } from '@angular/core';
import { ChartConfiguration, ChartOptions, Tick } from 'chart.js';
import 'chartjs-adapter-moment';
import dayjs from 'dayjs';
import { MyaDashboardService } from '../../service/mya-dashboard.service';

@Component({
  selector: 'jhi-mya-evolution-by-months',
  templateUrl: './mya-evolution-by-months.component.html',
})
export class MyaEvolutionByMonthsComponent implements OnChanges {
  @Input() categoryId: number | null = null;
  @Input() monthTo: Date | null = null;
  @Input() monthFrom: Date | null = null;
  @Input() height = '30vh';
  @Input() displayX = true;
  data: ChartConfiguration<'line'>['data'] | null = null;
  options: ChartOptions<'line'> | null = null;

  constructor(private dashboardService: MyaDashboardService) {}

  loadAll(): void {
    if (this.monthTo && this.monthFrom) {
      this.dashboardService
        .getEvolutionByMonth(this.categoryId, dayjs(this.monthFrom), dayjs(this.monthTo))
        .subscribe((res: HttpResponse<any>) => {
          this.data = {
            labels: res.body ? res.body.months : null,
            datasets: [
              {
                label: 'Montant',
                data: res.body ? res.body.amounts : null,
                borderColor: '#49ab81',
                backgroundColor: '#49ab81',
                pointBorderColor: '#49ab81',
                pointBackgroundColor: '#49ab81',
                fill: false,
                pointRadius: 0,
                cubicInterpolationMode: 'monotone',
                tension: 0.4,
              },
              {
                label: 'Budget',
                data: res.body ? res.body.budgetAmounts : null,
                borderColor: '#3b5998',
                backgroundColor: '#3b5998',
                pointBorderColor: '#3b5998',
                pointBackgroundColor: '#3b5998',
                fill: false,
                pointRadius: 0,
                cubicInterpolationMode: 'monotone',
                tension: 0.4,
              },
              {
                label: 'Montant Moy. 3 mois',
                data: res.body ? res.body.amountsAvg3 : null,
                borderColor: '#8b9dc3',
                backgroundColor: '#8b9dc3',
                pointBorderColor: '#8b9dc3',
                pointBackgroundColor: '#8b9dc3',
                fill: false,
                borderWidth: 2,
                pointRadius: 0,
                cubicInterpolationMode: 'monotone',
                tension: 0.4,
              },
              {
                label: 'Montant Moy. 12 mois',
                data: res.body ? res.body.amountsAvg12 : null,
                borderColor: '#dfe3ee',
                backgroundColor: '#dfe3ee',
                pointBorderColor: '#dfe3ee',
                pointBackgroundColor: '#dfe3ee',
                fill: false,
                borderWidth: 2,
                pointRadius: 0,
                cubicInterpolationMode: 'monotone',
                tension: 0.4,
              },
            ],
          };
          this.options = {
            responsive: true,
            interaction: {
              intersect: false,
            },
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
                    return [dayjs(context[0].label).format('MMM-YY')];
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
                  unit: 'month',
                  stepSize: 1,
                  displayFormats: {
                    month: 'MMM YYYY',
                  },
                },
              },
            },
          };
        });
    }
  }

  ngOnChanges(): void {
    this.loadAll();
  }
}
