select
	item_date,
	(total_value - loan_value) * percent_owned / 100,
	cast(null as SIGNED)
from
	real_estate_item
where
	bank_account_id = :bankAccountId
