import { HttpResponse } from '@angular/common/http';
import { Component, Input, OnChanges, OnDestroy } from '@angular/core';
import { DATE_FORMAT } from 'app/config/input.constants';
import { Chart, Tick } from 'chart.js';
import 'chartjs-adapter-moment';
import dayjs from 'dayjs';
import { MyaDashboardService } from '../../service/mya-dashboard.service';

@Component({
  selector: 'jhi-mya-all-account-evolution',
  templateUrl: './mya-all-account-evolution.html',
})
export class MyaAllAccountEvolutionComponent implements OnChanges, OnDestroy {
  @Input() data: string | null = null;
  @Input() type: string | null = null;
  chart: Chart | null = null;
  //data: ChartConfiguration<'line'>['data'] | null = null;
  //options: ChartOptions<'line'> | null = null;

  constructor(private dashboardService: MyaDashboardService) {}
  ngOnDestroy(): void {
    this.chart?.destroy();
    //throw new Error('Method not implemented.');
  }

  changeRange(range: string): void {
    switch (range) {
      case '5y':
        this.chart!.options.scales!.x!.min = dayjs().add(-5, 'year').format(DATE_FORMAT);
        break;
      case '2y':
        this.chart!.options.scales!.x!.min = dayjs().add(-2, 'year').format(DATE_FORMAT);
        break;
      case '1y':
        this.chart!.options.scales!.x!.min = dayjs().add(-1, 'year').format(DATE_FORMAT);
        break;
      case 'all':
        this.chart!.options.scales!.x!.min = typeof this.chart!.data.labels![0] === 'string' ? this.chart!.data.labels![0] : 0;
        break;
      case '6m':
        this.chart!.options.scales!.x!.min = dayjs().add(-6, 'month').format(DATE_FORMAT);
        break;
      case '1m':
        this.chart!.options.scales!.x!.min = dayjs().add(-1, 'month').format(DATE_FORMAT);
        break;
    }
    //this.chart!.options.scales!.x!.min = '2020-01-01';
    this.chart?.update();
  }

  loadAll(): void {
    if (this.type && this.type === 'all') {
      this.dashboardService.getAllAccountEvolutionByType().subscribe((res: HttpResponse<any>) => {
        this.buildSingleChart(res.body);
      });
    }
  }

  ngOnChanges(): void {
    this.loadAll();
  }

  getPoints(data: any): number[] {
    const values: number[] = new Array<number>();
    data.points.forEach((point: any) => {
      values.push(point.data ? point.data.value : null);
    });
    return values;
  }

  buildSingleChart(points: any): void {
    if (this.chart) {
      this.chart.destroy();
    }
    if (this.type) {
      const plugins = {
        legend: {
          display: true,
          position: 'top',
        },

        tooltip: {
          position: 'average',
          mode: 'index',
          intersect: false,
          callbacks: {
            title(context: any): string {
              let sum = 0;
              context.forEach((item: any) => {
                sum += item.parsed.y;
              });
              let title = dayjs(context[0].label).format('D-MMM-YY') + ' : ';
              title += sum.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' });
              return title;
            },
            label(context: any): string {
              let label: string = context.dataset.label || '';
              if (label) {
                label += ' : ';
              }
              label += context.parsed.y.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' });
              return label;
            },
          },
        },
      };
      this.chart = new Chart('myaAmountEvolution', {
        type: 'line',

        data: {
          labels: points.dates,
          datasets: [
            {
              label: 'Courant',
              data: this.getPoints(points.currentPoints),
              borderColor: '#FC7300',
              backgroundColor: '#FC730080',
              pointBorderColor: '#FC7300',
              pointBackgroundColor: '#FC7300',
              fill: true,
              pointRadius: 0,
              cubicInterpolationMode: 'monotone',
              tension: 0.4,
              borderWidth: 2,
            },
            {
              label: 'Epargne',
              data: this.getPoints(points.savingsPoints),
              borderColor: '#BFDB38',
              backgroundColor: '#BFDB3880',
              pointBorderColor: '#BFDB38',
              pointBackgroundColor: '#BFDB38',
              fill: true,
              pointRadius: 0,
              cubicInterpolationMode: 'monotone',
              tension: 0.4,
              borderWidth: 2,
            },
            {
              label: 'Actions',
              data: this.getPoints(points.stockData),
              borderColor: '#1F8A70',
              backgroundColor: '#1F8A7080',
              pointBorderColor: '#1F8A70',
              pointBackgroundColor: '#1F8A70',
              fill: true,
              pointRadius: 0,
              cubicInterpolationMode: 'monotone',
              tension: 0.4,
              borderWidth: 2,
            },
            {
              label: 'Immobilier',
              data: this.getPoints(points.realEstatePoints),
              borderColor: '#00425A',
              backgroundColor: '#00425A80',
              pointBorderColor: '#00425A',
              pointBackgroundColor: '#00425A',
              fill: true,
              pointRadius: 0,
              cubicInterpolationMode: 'monotone',
              tension: 0.4,
              borderWidth: 2,
            },
          ],
        },
        options: {
          responsive: true,
          interaction: {
            intersect: false,
          },

          plugins: {
            legend: {
              display: true,
              position: 'top',
            },

            tooltip: {
              position: 'average',
              mode: 'index',
              intersect: false,
              callbacks: {
                title(context: any): string {
                  let sum = 0;
                  context.forEach((item: any) => {
                    sum += item.parsed.y;
                  });
                  let title = dayjs(context[0].label).format('D-MMM-YY') + ' : ';
                  title += sum.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' });
                  return title;
                },
                label(context: any): string {
                  let label: string = context.dataset.label || '';
                  if (label) {
                    label += ' : ';
                  }
                  label += context.parsed.y.toLocaleString('fr-FR', { style: 'currency', currency: 'EUR' });
                  return label;
                },
              },
            },
          },
          scales: {
            y: {
              stacked: true,
              beginAtZero: true,
              title: {
                display: false,
              },
              ticks: {
                callback(value: string | number, index: number, ticks: Tick[]): string {
                  return String(value);
                },
              },
            },
            x: {
              display: true,
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
        },

        plugins: [
          {
            id: 'try',
            afterDatasetsDraw(chart): void {
              if (chart.tooltip?.getActiveElements()?.length) {
                let squaredDistance: number | null = null;
                let xVal: number | null = null;
                let yVal: number | null = null;
                const cursorX = chart.tooltip.x;
                const cursorY = chart.tooltip.y;
                chart.tooltip.getActiveElements().forEach(element => {
                  if (element.element.active) {
                    const pointX = element.element.x;
                    const pointY = element.element.y;
                    const pointSquareDistance = (pointX - cursorX) * (pointX - cursorX) + (pointY - cursorY) * (pointY - cursorY);
                    if (squaredDistance === null || pointSquareDistance < squaredDistance) {
                      squaredDistance = pointSquareDistance;
                      xVal = pointX;
                      yVal = pointY;
                    }
                  }
                });

                const yAxis = chart.scales.y;
                const xAxis = chart.scales.x;
                const ctx = chart.ctx;

                ctx.save();
                ctx.beginPath();

                ctx.moveTo(xAxis.left, yVal!);
                ctx.lineTo(xAxis.right, yVal!);
                ctx.lineWidth = 1;
                ctx.strokeStyle = '#ff0000';
                ctx.setLineDash([2, 2]);
                ctx.stroke();
                ctx.restore();
              }
            },
          },
        ],
      });
    }
  }

  buildStockChart(points: any[]): void {
    if (this.chart) {
      this.chart.destroy();
    }
    if (this.type) {
      const dates: string[] = new Array<string>();
      const valuesInCurrency: number[] = new Array<number>();
      const valuesInEuro: number[] = new Array<number>();
      points.forEach(point => {
        dates.push(point.date);
      });
      points.forEach(point => {
        valuesInCurrency.push(point.data.valueInCurrency);
      });
      points.forEach(point => {
        valuesInEuro.push(point.data.valueInEuro);
      });
      this.chart = new Chart('myaAmountEvolution', {
        type: 'line',
        //points.forEach(point => { valueInEuro.push(point.data.valueInEuro) });
        data: {
          labels: dates,
          datasets: [
            {
              label: 'Value in Currency',
              data: valuesInCurrency,
              borderColor: '#49ab81',
              backgroundColor: '#49ab81',
              pointBorderColor: '#49ab81',
              pointBackgroundColor: '#49ab81',
              fill: false,
              pointRadius: 0,
              cubicInterpolationMode: 'monotone',
              tension: 0.4,
              borderWidth: 2,
            },
            {
              label: 'Value in Euro',
              data: valuesInEuro,
              borderColor: '#3b5998',
              backgroundColor: '#3b5998',
              pointBorderColor: '#3b5998',
              pointBackgroundColor: '#3b5998',
              fill: false,
              pointRadius: 0,
              cubicInterpolationMode: 'monotone',
              tension: 0.4,
              borderWidth: 2,
            },
          ],
        },
        options: {
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
                  return [dayjs(context[0].label).format('D-MMM-YY')];
                },
              },
            },
          },
          scales: {
            y: {
              beginAtZero: true,
              title: {
                display: false,
              },
              ticks: {
                callback(value: string | number, index: number, ticks: Tick[]): string {
                  return String(value);
                },
              },
            },
            x: {
              display: true,
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
        },
      });
    }
  }
}
