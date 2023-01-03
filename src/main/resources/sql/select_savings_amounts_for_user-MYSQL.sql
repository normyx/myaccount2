
select 
	op_date,
	sum(amount) over (
order by
	op_date) + sum(initial_amount) over (
order by
	op_date) as amount,
	CAST(NULL AS SIGNED)
from
	(
	select
		jhi_date as op_date,
		SUM(amount) as amount,
		ba_data.initial_amount
	from
		operation op
	inner join bank_account ba on
		op.bank_account_id = ba.id
	left join (
		select
			ba.id as ba_id, 
			min(jhi_date) as op_date2,
			ba.initial_amount  as initial_amount
		from
			operation op
		inner join bank_account ba on
			op.bank_account_id = ba.id
		where
			ba.account_id = :accountId
			AND (ba.account_type = 'SAVINGSACCOUNT')
		GROUP by
			ba.id) as ba_data on
		op.jhi_date = ba_data.op_date2
		AND op.bank_account_id = ba_data.ba_id
	where
		ba.account_id = :accountId
		AND (ba.account_type = 'SAVINGSACCOUNT')
	group by
		op_date, ba_data.initial_amount
) as day_op