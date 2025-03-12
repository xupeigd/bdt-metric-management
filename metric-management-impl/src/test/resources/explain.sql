SELECT year_id,
       month_id,
       group_id,
       COUNT(DISTINCT shop_id) AS shushuo_active_shop_cnt
FROM (SELECT year_id,
             month_id,
             group_id,
             shop_idFROM aggr_bill_shop_day AS on79_src_0) AS subq_3
WHERE month_id <> '01'
  AND group_id <> '5726'
  AND year_id <> '2023'
GROUP BY year_id, month_id, group_idORDER BY year_id