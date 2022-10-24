SELECT 
    cat.category_name, subcat.sub_category_name, SUM(op.amount) / :numberOfMonths
FROM
    operation op
        JOIN
    sub_category AS subcat ON subcat.id = op.sub_category_id
        JOIN
    category AS cat ON cat.id = subcat.category_id
WHERE
    op.account_id = :accountId AND cat.id = :categoryId
        AND op.jhi_date >= DATE_ADD(DATE_ADD(LAST_DAY(:month),
            INTERVAL 1 DAY),
        INTERVAL - (:numberOfMonths + 1) MONTH)
        AND op.jhi_date <= DATE_ADD(LAST_DAY(:month), INTERVAL  -1 MONTH)
GROUP BY op.sub_category_id
ORDER BY cat.id , subcat.id