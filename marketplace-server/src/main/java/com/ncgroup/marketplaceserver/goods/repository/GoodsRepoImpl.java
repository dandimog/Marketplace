package com.ncgroup.marketplaceserver.goods.repository;

import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.dto.GoodDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@PropertySource("classpath:database/productQueries.properties")
@Repository
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

    @Override
    public Good create(GoodDto goodDto) {

        // TODO: make changes with status, shipping date and unit fields

        KeyHolder firmHolder = new GeneratedKeyHolder();
        SqlParameterSource firmParameters = new MapSqlParameterSource()
                .addValue("firmName", goodDto.getFirmName());
        namedParameterJdbcTemplate.update(firmInsert, firmParameters, firmHolder);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource categoryParameters = new MapSqlParameterSource()
                .addValue("categoryName", goodDto.getCategoryName());
        namedParameterJdbcTemplate.update(categoryInsert, categoryParameters, keyHolder);

        KeyHolder productHolder = new GeneratedKeyHolder();
        SqlParameterSource productParameters = new MapSqlParameterSource()
                .addValue("productName", goodDto.getGoodName())
                .addValue("categoryId", keyHolder.getKey().longValue());
        namedParameterJdbcTemplate.update(productInsert, productParameters, productHolder);

        keyHolder = new GeneratedKeyHolder();
        SqlParameterSource goodParameters = new MapSqlParameterSource()
                .addValue("goodQuantity", goodDto.getQuantity())
                .addValue("goodPrice", goodDto.getPrice())
                .addValue("goodDiscount", goodDto.getDiscount())
                .addValue("goodInStock", goodDto.isInStock())
                .addValue("goodDescription", goodDto.getDescription())
                .addValue("productId", productHolder.getKey().longValue())
                .addValue("firmId", firmHolder.getKey().longValue());
        namedParameterJdbcTemplate.update(goodInsert, goodParameters, keyHolder);

        Good good = goodDto.convertToGood();
        good.setId(keyHolder.getKey().longValue());
        return good;
    }

    // TODO: delete this method
    @Value("${products.show}")
    private String showAllProducts;

    @Override
    public List<Good> findAll() {
        return namedParameterJdbcTemplate.query(showAllProducts, this::mapRow);
    }

    @Value("${product.update}")
    private String updateProduct;

    @Value("${firm.find-by-name}")
    private String findFirmByName;

    public Long findFirmByName(String name) {
        SqlParameterSource firmParameter = new MapSqlParameterSource()
                .addValue("firmName", name);
        return namedParameterJdbcTemplate.
                queryForObject(findFirmByName, firmParameter, Long.class);
    }

    @Value("${category.find-by-name}")
    private String findCategoryByName;

    public Long findCategoryByName(String name) {
        SqlParameterSource categoryParameter = new MapSqlParameterSource()
                .addValue("categoryName", name);
        return namedParameterJdbcTemplate.
                queryForObject(findCategoryByName, categoryParameter, Long.class);
    }

    @Value("${fake-product.find-by-name}")
    private String findProductByName;

    public Long findProductByName(String name) {
        SqlParameterSource productParameter = new MapSqlParameterSource()
                .addValue("productName", name);
        return namedParameterJdbcTemplate.
                queryForObject(findProductByName, productParameter, Long.class);
    }

    @Override
    public Good edit(Good good) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        Long firmId = findFirmByName(good.getFirmName());
        if (firmId == null) {
            SqlParameterSource firmParameters = new MapSqlParameterSource()
                    .addValue("firmName", good.getFirmName());
            KeyHolder firmHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(firmInsert, firmParameters, firmHolder);
            //firmId = firmHolder.getKey().longValue();
        }


        Long categoryId = findCategoryByName(good.getCategoryName());
        if (categoryId == null) {
            SqlParameterSource categoryParameters = new MapSqlParameterSource()
                    .addValue("categoryName", good.getCategoryName());
            KeyHolder categoryHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(categoryInsert, categoryParameters, categoryHolder);
            categoryId = categoryHolder.getKey().longValue();
        }


        Long productId = findProductByName(good.getGoodName());
        if (productId == null) {
            KeyHolder productHolder = new GeneratedKeyHolder();
            SqlParameterSource productParameters = new MapSqlParameterSource()
                    .addValue("productName", good.getGoodName())
                    .addValue("categoryId", categoryId);
            namedParameterJdbcTemplate.update(categoryInsert, productParameters, productHolder);
            //productId = productHolder.getKey().longValue();
        }

//        parameters
//                .addValue("prodId", productId)
//                .addValue("firmId", firmId)
//                .addValue("categoryId", categoryId)
//                .addValue("id", good.getId())
//                .addValue("quantity", good.getQuantity())
//                .addValue("price", good.getPrice())
//                .addValue("discount", good.getDiscount())
//                .addValue("inStock", good.isInStock())
//                .addValue("description", good.getDescription());

//        /**
//         * getting a modified java Good object
//         * (category name, good name, firm name)
//         */
//        Good res = namedParameterJdbcTemplate
//                .queryForObject(updateProduct, parameters, this::mapRow);
//
//        res.setCategoryName(good.getCategoryName());
//        res.setFirmName(good.getFirmName());
//        res.setGoodName(good.getGoodName());
        return good;
    }


    @Value("${product.find-by-id}")
    private String findByIdQuery;

    @Override
    public Optional<Good> findById(long id) {
        SqlParameterSource productParameter = new MapSqlParameterSource()
                .addValue("goodId", id);

        return Optional.ofNullable(
                namedParameterJdbcTemplate.queryForObject(
                        findByIdQuery,
                        productParameter,
                        this::mapRow
                )
        );
    }

    @Override
    public List<Good> display(Optional<String> filter, Optional<String> category,
                              Optional<String> minPrice, Optional<String> maxPrice,
                              Optional<String> sortBy, Optional<String> sortDirection,
                              Optional<Integer> page) {
        return null;
    }


    @Value("${product.find-by-name}")
    private String findByNameQuery;

    @Override
    public List<Good> findByName(String name) {
        SqlParameterSource goodsParams = new MapSqlParameterSource()
                .addValue("subName", name);
        return namedParameterJdbcTemplate.query(
                findByNameQuery,
                goodsParams,
                this::mapRow
        );
    }

    @Value("${product.filter-by-category}")
    private String filterByGoodCategory;

    @Override
    public List<Good> filterByGoodCategory(long categoryId) {
        SqlParameterSource goodsParams = new MapSqlParameterSource()
                .addValue("categoryId", categoryId);
        return namedParameterJdbcTemplate.query(
                filterByGoodCategory,
                goodsParams,
                this::mapRow
        );
    }

    @Value("${product.filter-by-price-range}")
    private String filterByPriceRange;

    @Override
    public List<Good> filterByPrice(int downLimit, int upLimit) {
        SqlParameterSource goodsParams = new MapSqlParameterSource()
                .addValue("downLimit", downLimit)
                .addValue("upLimit", upLimit);
        return namedParameterJdbcTemplate.query(
                filterByPriceRange,
                goodsParams,
                this::mapRow
        );
    }

    private Good mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Good.builder()
                .id(rs.getLong("id"))
//                .goodName(rs.getString("goodName"))
//                .firmName(rs.getString("firmName"))
//                .categoryName(rs.getString("categoryName"))
                .quantity(rs.getInt("quantity"))
                .price(rs.getDouble("price"))
                .discount(rs.getByte("discount"))
                .inStock(rs.getBoolean("in_stock"))
                .description(rs.getString("description"))
                .build();
    }
}
