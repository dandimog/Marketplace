package com.ncgroup.marketplaceserver.goods.repository;

import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.GoodDto;
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


    public Long createFirm(GoodDto goodDto) {
        SqlParameterSource firmParameters = new MapSqlParameterSource()
                .addValue("firmName", goodDto.getFirmName());
        KeyHolder firmHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(firmInsert, firmParameters, firmHolder);
        return firmHolder.getKey().longValue();
    }

    public Long createCategory(GoodDto goodDto) {
        SqlParameterSource categoryParameters = new MapSqlParameterSource()
                .addValue("categoryName", goodDto.getCategoryName());
        KeyHolder categoryHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(categoryInsert, categoryParameters, categoryHolder);
        return categoryHolder.getKey().longValue();
    }

    public Long createProduct(GoodDto goodDto, Long categoryId) {
        KeyHolder productHolder = new GeneratedKeyHolder();
        SqlParameterSource productParameters = new MapSqlParameterSource()
                .addValue("productName", goodDto.getGoodName())
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
    public Good create(GoodDto goodDto) {

        // TODO: make changes with status, shipping date and unit fields

        Long firmId = findByName
                (goodDto.getFirmName(), "firmName", findFirmByName);

        if (firmId == null) {
            firmId = createFirm(goodDto);
        }

        Long categoryId = findByName
                (goodDto.getCategoryName(), "categoryName", findCategoryByName);

        if (categoryId == null) {
            categoryId = createCategory(goodDto);
        }

        Long productId = findByName
                (goodDto.getGoodName(), "productName", findProductByName);

        if (productId == null) {
            productId = createProduct(goodDto, categoryId);
        }

        /**
         * goods are equal if their firm, product and shipping date are equal
         */
        Long goodId = findGood(firmId, productId);
        if (goodId == null) {
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
            goodId = keyHolder.getKey().longValue();

        }

        Good good = goodDto.convertToGood();
        good.setId(goodId);
        return good;
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

        Long firmId = findByName(good.getFirmName(), "firmName", findFirmByName);
        if (firmId == null) {
            SqlParameterSource firmParameters = new MapSqlParameterSource()
                    .addValue("firmName", good.getFirmName());
            KeyHolder firmHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(firmInsert, firmParameters, firmHolder);
            firmId = firmHolder.getKey().longValue();
        }


        Long categoryId = findByName(good.getCategoryName(), "categoryName", findCategoryByName);
        if (categoryId == null) {
            SqlParameterSource categoryParameters = new MapSqlParameterSource()
                    .addValue("categoryName", good.getCategoryName());
            KeyHolder categoryHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(categoryInsert, categoryParameters, categoryHolder);
            categoryId = categoryHolder.getKey().longValue();
        }


        Long productId = findByName(good.getGoodName(), "productName", findProductByName);
        if (productId == null) {
            KeyHolder productHolder = new GeneratedKeyHolder();
            SqlParameterSource productParameters = new MapSqlParameterSource()
                    .addValue("productName", good.getGoodName())
                    .addValue("categoryId", categoryId);
            namedParameterJdbcTemplate.update(categoryInsert, productParameters, productHolder);
            productId = productHolder.getKey().longValue();
        }

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("prodId", productId)
                .addValue("firmId", firmId)
                .addValue("id", good.getId())
                .addValue("quantity", good.getQuantity())
                .addValue("price", good.getPrice())
                .addValue("discount", good.getDiscount())
                .addValue("inStock", good.isInStock())
                .addValue("description", good.getDescription());

        namedParameterJdbcTemplate.update(updateProduct, parameters);
        //return good;
    }


    @Value("${good.find-by-id}")
    private String findGoodById;
    @Override
    public Optional<Good> findById(long id) {
        SqlParameterSource productParameter = new MapSqlParameterSource()
                .addValue("goodId", id);

        return Optional.ofNullable(
                namedParameterJdbcTemplate.queryForObject(
                        findGoodById, productParameter, Good.class
                )
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

//    private Good mapRow(ResultSet rs, int rowNum) throws SQLException {
//        return Good.builder()
//                .id(rs.getLong("id"))
//                .quantity(rs.getInt("quantity"))
//                .price(rs.getDouble("price"))
//                .discount(rs.getByte("discount"))
//                .inStock(rs.getBoolean("in_stock"))
//                .description(rs.getString("description"))
//                .build();
//    }
}
