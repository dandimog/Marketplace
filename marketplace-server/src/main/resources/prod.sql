

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
    VALUES (:productName, category_ins.categoryId)
    RETURNING id AS productId
)
INSERT INTO goods(prod_id, firm_id, quantity,
                  price, discount, in_stock, description)
VALUES (product_ins.productId, firm_ins.firmId, :quantity, :price, :discount,
  :inStock, :description)
RETURNING id


-- UPDATE goods SET quantity = :quantity, price = :price, \
-- discount = :discount, in_stock = :inStock, description = :description,\
-- prod_id = :prodId, firm_id = :firmId, category_id\
-- WHERE id = :id RETURNING *
