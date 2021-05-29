

WITH firm_ins AS (
    INSERT INTO firm (name)
    VALUES (:firmName)
    RETURNING id AS firmId
),
    category_ins AS (
    INSERT INTO category (name)
    VALUES (:categoryName)
    RETURNING id AS categoryId
),
    product_ins AS (
    INSERT INTO product (name, category_id)
    VALUES (:productName, categoryId)
    RETURNING id AS productId
),
INSERT INTO goods(prod_id, firm_id, quantity,
                  price, discount, in_stock, description)
VALUES (productId, firmId, :quantity, :price, :discount,
  :inStock, :description)
