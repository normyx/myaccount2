SELECT report_data.date, 
	IF(report_data.has_operation = 1, report_data.amount, NULL) + initial_amount.amount AS operation_amount, 
	IF(report_data.has_operation = 0, report_data.amount, NULL) + initial_amount.amount AS predictive_amount
FROM 
	(SELECT amount_data.date, 
		amount_data.has_operation, 
		@amount \:= if(has_operation = 1, @amount + amount_data.operation_amount, @amount + amount_data.budget_amount) AS amount
	FROM
	(SELECT  
	        rpt_dated_data.jhi_date AS date,  
	        rpt_dated_data.month AS month, 
	        (rpt_dated_data.jhi_date <= account_max_op_date.max_op_date) AS has_operation, 
	        IFNULL(SUM(operation_amount.amount), 0) AS operation_amount, 
	        IFNULL(SUM(budget_smoothed.amount) / rpt_dated_data.n_days_in_month, 0) + IFNULL(SUM(budget_not_smoothed.amount), 0) AS budget_amount 
	FROM (SELECT   
	    days.jhi_date AS jhi_date,  
	    days.n_days_in_month AS n_days_in_month, 
	    user2.account_id AS account_id,  
	    days.month AS month  
	FROM param_days AS days 
	JOIN (SELECT jhi_user.id AS account_id FROM jhi_user) AS user2) rpt_dated_data 
	LEFT JOIN (SELECT op.account_id AS account_id, MAX(op.jhi_date) AS max_op_date FROM operation op GROUP BY op.account_id) account_max_op_date  
	    ON rpt_dated_data.account_id = account_max_op_date.account_id  
	LEFT JOIN (  
	    SELECT  
	        SUM(bip.amount) AS amount,  
	        bip.month AS month, 
	        bi.account_id AS account_id
	    FROM budget_item_period AS bip  
	    LEFT JOIN budget_item AS bi ON bip.budget_item_id = bi.id 
	    WHERE bip.is_smoothed = 1 
	    GROUP BY bip.month, bi.account_id) AS budget_smoothed  
	ON rpt_dated_data.month = budget_smoothed.month 
	    AND rpt_dated_data.account_id = budget_smoothed.account_id  
	LEFT JOIN (  
	    SELECT  
	        bip.jhi_date AS jhi_date,  
	        SUM(bip.amount) AS amount, 
	        bi.account_id AS account_id  
	    FROM budget_item_period AS bip  
	    LEFT JOIN budget_item AS bi ON bip.budget_item_id = bi.id 
	    WHERE bip.is_smoothed = 0
	    GROUP BY bip.jhi_date, bi.account_id) AS budget_not_smoothed  
	ON rpt_dated_data.jhi_date = budget_not_smoothed.jhi_date 
	    AND rpt_dated_data.account_id = budget_not_smoothed.account_id  
	LEFT JOIN ( 
	    SELECT   
	        op.jhi_date AS jhi_date,  
	        SUM(op.amount) AS amount, 
	        op.account_id AS account_id  
	    FROM operation AS op  INNER JOIN bank_account ba ON op.bank_account_id = ba.id 
		WHERE ba.archived = 0 
	    GROUP BY op.jhi_date, op.account_id) AS operation_amount  
	ON rpt_dated_data.jhi_date = operation_amount.jhi_date 
	        AND rpt_dated_data.account_id = operation_amount.account_id  
	WHERE rpt_dated_data.account_id = :accountId
	GROUP BY rpt_dated_data.jhi_date, account_max_op_date.max_op_date 
	ORDER BY rpt_dated_data.jhi_date) AS amount_data 
	JOIN (SELECT @amount \:= 0)d
	ORDER BY amount_data.date) AS report_data,
	(SELECT SUM(ba.initial_amount) AS amount FROM bank_account ba WHERE ba.archived = 0 AND ba.account_id = :accountId) AS initial_amount 
WHERE report_data.date >= :dateFrom AND report_data.date <= :dateTo