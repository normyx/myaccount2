export const EVENT_UPDATE_BUDGET_ITEM_ROW = 'myaBudgetItemRowModification';
export const EVENT_LOAD_BUDGET_ITEMS = 'myaBudgetItemsLoad';
export const EVENT_REORDER_BUDGET_ITEM = 'myaReorderBudgetItem';
export const EVENT_LOAD_OPERATIONS = 'myaOperationsLoad';

export class MyaReorderBudgetItem {
  id: number;
  direction: string;
  constructor(id: number, direction: string) {
    this.id = id;
    this.direction = direction;
  }
}
