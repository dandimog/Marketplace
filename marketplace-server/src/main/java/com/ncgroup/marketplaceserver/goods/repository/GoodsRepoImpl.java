package com.ncgroup.marketplaceserver.goods.repository;

import com.ncgroup.marketplaceserver.goods.exceptions.GoodAlreadyExistsException;
import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.GoodDto;
import com.ncgroup.marketplaceserver.shopping.cart.repository.ShoppingCartItemRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@PropertySource("classpath:database/productQueries.properties")
@Repository
@Slf4j
public class GoodsRepoImpl implements GoodsRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public GoodsRepoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Value("${firm.insert}")
    private String firmInsert;
    @Value("${category.insert}")
    private String categoryInsert;
    @Value("${product.insert}")
    private String productInsert;
    @Value("${good.insert}")
    private String goodInsert;


    public Long createFirm(String firmName) {
        SqlParameterSource firmParameters = new MapSqlParameterSource()
                .addValue("firmName", firmName);
        KeyHolder firmHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(firmInsert, firmParameters, firmHolder);
        return firmHolder.getKey().longValue();
    }

    public Long createCategory(String categoryName) {
        SqlParameterSource categoryParameters = new MapSqlParameterSource()
                .addValue("categoryName", categoryName);
        KeyHolder categoryHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(categoryInsert, categoryParameters, categoryHolder);
        return categoryHolder.getKey().longValue();
    }

    public Long createProduct(String goodName, Long categoryId) {
        KeyHolder productHolder = new GeneratedKeyHolder();
        SqlParameterSource productParameters = new MapSqlParameterSource()
                .addValue("productName", goodName)
                .addValue("categoryId", categoryId);
        namedParameterJdbcTemplate.update(productInsert, productParameters, productHolder);
        return productHolder.getKey().longValue();
    }

    public Long createGood(GoodDto goodDto, Long productId, Long firmId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource goodParameters = new MapSqlParameterSource()
                .addValue("goodQuantity", goodDto.getQuantity())
                .addValue("goodPrice", goodDto.getPrice())
                .addValue("goodDiscount", goodDto.getDiscount())
                .addValue("goodInStock", goodDto.isInStock())
                .addValue("goodDescription", goodDto.getDescription())
                .addValue("productId", productId)
                .addValue("firmId", firmId);
        namedParameterJdbcTemplate.update(goodInsert, goodParameters, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Long create(GoodDto goodDto) throws GoodAlreadyExistsException {

        // TODO: make changes with status, shipping date and unit fields

        Long firmId = findByName
                (goodDto.getFirmName(), "firmName", findFirmByName);
        if (firmId == null) {
            firmId = createFirm(goodDto.getFirmName());
        }

        Long categoryId = findByName
                (goodDto.getCategoryName(), "categoryName", findCategoryByName);
        if (categoryId == null) {
            categoryId = createCategory(goodDto.getCategoryName());
        }

        Long productId = findByName
                (goodDto.getGoodName(), "productName", findProductByName);
        if (productId == null) {
            productId = createProduct(goodDto.getGoodName(), categoryId);
        }

        /**
         * goods are equal if their firm,
         * product and shipping date are equal
         */

        Long goodId = findGood(firmId, productId);
        if (goodId == null) {
            goodId = createGood(goodDto, productId, firmId);
            return goodId;
        }

        throw new GoodAlreadyExistsException
                ("Such good already exists! If you want to modify an existing good," +
                        " please go to the List of goods -> Good -> Edit");
    }

    // TODO: delete this method
//    @Value("${products.show}")
//    private String showAllProducts;

//    @Override
//    public List<Good> findAll() {
//        return namedParameterJdbcTemplate.query(showAllProducts, this::mapRow);
//    }

    @Value("${firm.find-by-name}")
    private String findFirmByName;
    @Value("${category.find-by-name}")
    private String findCategoryByName;
    @Value("${fake-product.find-by-name}")
    private String findProductByName;

    public Long findByName(String name, String paramName, String sqlQuery) {
        SqlParameterSource parameter = new MapSqlParameterSource()
                .addValue(paramName, name);
        try {
            return namedParameterJdbcTemplate
                    .queryForObject(sqlQuery, parameter, Long.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

//    public String getById(Long id, String paramName, String sqlQuery) {
//        SqlParameterSource parameter = new MapSqlParameterSource()
//                .addValue("id", id);
//        try {
//            return namedParameterJdbcTemplate
//                    .queryForObject(sqlQuery, parameter, Long.class);
//        } catch (EmptyResultDataAccessException e) {
//            return null;
//        }
//    }

    // TODO: add shipping date here

    @Value("${good.find-by-firmId-productId}")
    private String findGood;

    public Long findGood(Long firmId, Long productId) {
        SqlParameterSource goodParameters = new MapSqlParameterSource()
                .addValue("firmId", firmId)
                .addValue("productId", productId);
        try {
            return namedParameterJdbcTemplate
                    .queryForObject(findGood, goodParameters, Long.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Value("${product.update}")
    private String updateProduct;

    @Override
    public void edit(Good good) {

        Long firmId = findByName
                (good.getFirmName(), "firmName", findFirmByName);
        if (firmId == null) {
            firmId = createFirm(good.getFirmName());
        }

        Long categoryId = findByName
                (good.getCategoryName(), "categoryName", findCategoryByName);
        if (categoryId == null) {
            categoryId = createCategory(good.getCategoryName());
        }

        Long productId = findByName
                (good.getGoodName(), "productName", findProductByName);
        if (productId == null) {
            productId = createProduct(good.getGoodName(), categoryId);
        }

        editGood(good, productId, firmId);
    }

    public void editGood(Good good, Long productId, Long firmId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", good.getId()) // for search purpose

                .addValue("prodId", productId)
                .addValue("firmId", firmId)
                .addValue("quantity", good.getQuantity())
                .addValue("price", good.getPrice())
                .addValue("discount", good.getDiscount())
                .addValue("inStock", good.isInStock())
                .addValue("description", good.getDescription());

        namedParameterJdbcTemplate.update(updateProduct, parameters);
    }


    @Value("${good.find-by-id}")
    private String findGoodById;

    @Override
    public Optional<Good> findById(long id) {
        SqlParameterSource productParameter = new MapSqlParameterSource()
                .addValue("goodId", id);
        Good good;

        // TODO: QUESTION: specification + Optional.ofNullable()

        try {
            good = namedParameterJdbcTemplate
                    .queryForObject(findGoodById, productParameter, this::mapRow);
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

        return Optional.ofNullable(good);
    }

    @Value("${good.get-all}")
    private String getAllGoods;
    @Override
    public Collection<Good> getAllGoods() {

        return namedParameterJdbcTemplate.query(
                getAllGoods,
                this::mapRow
        );
    }
//    @Override
//    public List<Good> display(Optional<String> filter, Optional<String> category,
//                              Optional<String> minPrice, Optional<String> maxPrice,
//                              Optional<String> sortBy, Optional<String> sortDirection,
//                              Optional<Integer> page) {
//        return null;
//    }


//    @Value("${product.find-by-name}")
//    private String findByNameQuery;

//    @Override
//    public List<Good> findByName(String name) {
//        SqlParameterSource goodsParams = new MapSqlParameterSource()
//                .addValue("subName", name);
//        return namedParameterJdbcTemplate.query(findByNameQuery,
//                goodsParams,
//                this::mapRow
//        );
//    }

//    @Value("${product.filter-by-category}")
//    private String filterByGoodCategory;

//    @Override
//    public List<Good> filterByGoodCategory(long categoryId) {
//        SqlParameterSource goodsParams = new MapSqlParameterSource()
//                .addValue("categoryId", categoryId);
//        return namedParameterJdbcTemplate.query(
//                filterByGoodCategory,
//                goodsParams,
//                this::mapRow
//        );
//    }

//    @Value("${product.filter-by-price-range}")
//    private String filterByPriceRange;


//    public List<Good> filterByPrice(int downLimit, int upLimit) {
//        SqlParameterSource goodsParams = new MapSqlParameterSource()
//                .addValue("downLimit", downLimit)
//                .addValue("upLimit", upLimit);
//        return namedParameterJdbcTemplate.query(
//                filterByPriceRange,
//                goodsParams,
//                this::mapRow
//        );
//    }

    private Good mapRow(ResultSet rs, int rowNum) throws SQLException {

        return Good.builder()
                .id(rs.getLong("id"))
                .quantity(rs.getInt("quantity"))
                .categoryName(rs.getString("category_name"))
                .goodName(rs.getString("product_name"))
                .firmName(rs.getString("firm_name"))
                .price(rs.getDouble("price"))
                .discount(rs.getByte("discount"))
                .inStock(rs.getBoolean("in_stock"))
                .description(rs.getString("description"))
                .build();
    }
}
