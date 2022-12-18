select 
	op_date,
	sum(amount) over (
order by
	op_date) + initial_amount + adjustment_amount as amount,
	CAST(NULL AS SIGNED)
from
	(
	select
		jhi_date as op_date,
		SUM(amount)  as amount,
		SUM(ba.initial_amount) as initial_amount,
		SUM(ba.adjustment_amount) as adjustment_amount
	from
		operation op
	inner join bank_account ba on
		op.bank_account_id = ba.id
	where
		ba.account_id  = :accountId AND (ba.account_type = 'CURRENTACCOUNT')
	group by
		op_date) as day_op