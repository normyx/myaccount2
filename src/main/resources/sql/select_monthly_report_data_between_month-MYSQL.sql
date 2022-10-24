SELECT  
    CONCAT(br.account_id, 
            '-', 
            br.month) AS id, 
    br.account_id AS account_id, 
    br.month AS month, 
    1 AS category_id, 
    '' AS category_name, 
    SUM(br.budget_amount) AS budget_amount, 
    SUM(opr.amount) AS amount, 
    SUM(opr.amount_avg_3) AS amount_avg3, 
    SUM(opr.amount_avg_12) AS amount_avg12 
FROM 
    (SELECT  
        bi.account_id AS account_id, 
            bip.month AS month, 
            SUM(bip.amount) AS budget_amount 
    FROM 
        (budget_item bi 
    JOIN budget_item_period bip ON bi.id = bip.budget_item_id) 
    GROUP BY bi.account_id , bip.month) AS br 
        LEFT JOIN 
    (SELECT  
        m1.month AS month, 
            m1.account_id AS account_id, 
            m1.amount AS amount, 
            AVG(m2.amount) AS amount_avg_3, 
            AVG(m3.amount) AS amount_avg_12 
    FROM 
        (SELECT  
        STR_TO_DATE(CONCAT(YEAR(operation.jhi_date), '-', MONTH(operation.jhi_date), '-', '01'), '%Y-%m-%d') AS month, 
			operation.account_id AS account_id, 
            SUM(operation.amount) AS amount 
    FROM operation 
    GROUP BY month , operation.account_id) AS m1 
    JOIN (SELECT  
        STR_TO_DATE(CONCAT(YEAR(operation.jhi_date), '-', MONTH(operation.jhi_date), '-', '01'), '%Y-%m-%d') AS month, 
            operation.account_id AS account_id, 
            SUM(operation.amount) AS amount 
    FROM operation 
    GROUP BY month , operation.account_id) AS m2 ON m1.account_id = m2.account_id 
    JOIN (SELECT  
        STR_TO_DATE(CONCAT(YEAR(operation.jhi_date), '-', MONTH(operation.jhi_date), '-', '01'), '%Y-%m-%d') AS month, 
            operation.account_id AS account_id, 
            SUM(operation.amount) AS amount 
    FROM operation 
    GROUP BY month , operation.account_id) AS m3 ON m1.account_id = m3.account_id 
    WHERE 
        m2.month <= m1.month 
            AND m2.month >= (m1.month + INTERVAL -(2) MONTH) 
            AND m3.month <= m1.month 
            AND m3.month >= (m1.month + INTERVAL -(11) MONTH) 
    GROUP BY m1.month , m1.account_id 
    ORDER BY m1.month) AS opr ON br.account_id = opr.account_id 
        AND br.month = opr.month 
        
WHERE br.account_id = :accountId AND br.month >= :fromDate AND br.month <= :toDate 
GROUP BY br.month, br.account_id
ORDER BY month ASC