SELECT  
    CONCAT(br.account_id, 
            '-', 
            br.category_id, 
            '-', 
            br.month) AS id, 
    br.account_id AS account_id, 
    br.month AS month, 
    br.category_id AS category_id, 
    br.category_name AS category_name, 
    br.budget_amount AS budget_amount, 
    opr.amount AS amount, 
    opr.amount_avg_3 AS amount_avg3, 
    opr.amount_avg_12 AS amount_avg12 
FROM 
    (SELECT  
        bi.account_id AS account_id, 
            bi.category_id AS category_id, 
            cat.category_name AS category_name, 
            bip.month AS month, 
            SUM(bip.amount) AS budget_amount 
    FROM 
        (budget_item bi 
    JOIN budget_item_period bip ON bi.id = bip.budget_item_id 
    JOIN category cat ON cat.id = bi.category_id) 
    GROUP BY bi.account_id , bi.category_id , bip.month) AS br 
        LEFT JOIN 
    (SELECT  
        m1.month AS month, 
            m1.account_id AS account_id, 
            m1.category_id AS category_id, 
            m1.amount AS amount, 
            AVG(m2.amount) AS amount_avg_3, 
            AVG(m3.amount) AS amount_avg_12 
    FROM 
        (SELECT  
        STR_TO_DATE(CONCAT(YEAR(operation.jhi_date), '-', MONTH(operation.jhi_date), '-', '01'), '%Y-%m-%d') AS month, 
            sub_category.category_id AS category_id, 
            operation.account_id AS account_id, 
            SUM(operation.amount) AS amount 
    FROM operation 
    JOIN sub_category ON operation.sub_category_id = sub_category.id 
    GROUP BY month , sub_category.category_id , operation.account_id) AS m1 
    JOIN (SELECT  
        STR_TO_DATE(CONCAT(YEAR(operation.jhi_date), '-', MONTH(operation.jhi_date), '-', '01'), '%Y-%m-%d') AS month, 
            sub_category.category_id AS category_id, 
            operation.account_id AS account_id, 
            SUM(operation.amount) AS amount 
    FROM 
        (operation 
    JOIN sub_category ON ((operation.sub_category_id = sub_category.id))) 
    GROUP BY month , sub_category.category_id , operation.account_id) AS m2 ON m1.category_id = m2.category_id 
        AND m1.account_id = m2.account_id 
    JOIN (SELECT  
        STR_TO_DATE(CONCAT(YEAR(operation.jhi_date), '-', MONTH(operation.jhi_date), '-', '01'), '%Y-%m-%d') AS month, 
            sub_category.category_id AS category_id, 
            operation.account_id AS account_id, 
            SUM(operation.amount) AS amount 
    FROM 
        (operation 
    JOIN sub_category ON ((operation.sub_category_id = sub_category.id))) 
    GROUP BY month , sub_category.category_id , operation.account_id) AS m3 ON m1.category_id = m3.category_id 
        AND m1.account_id = m3.account_id 
    WHERE 
        m2.month <= m1.month 
            AND m2.month >= (m1.month + INTERVAL -(2) MONTH) 
            AND m3.month <= m1.month 
            AND m3.month >= (m1.month + INTERVAL -(11) MONTH) 
    GROUP BY m1.month , m1.category_id , m1.account_id 
    ORDER BY m1.category_id , m1.month) AS opr ON br.account_id = opr.account_id 
        AND br.month = opr.month 
        AND br.category_id = opr.category_id 
WHERE br.account_id = :accountId AND br.category_id = :categoryId AND br.month >= :fromDate AND br.month <= :toDate
ORDER BY month ASC