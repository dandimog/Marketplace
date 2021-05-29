package com.ncgroup.marketplaceserver.goods.repository;

import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.Unit;
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
import java.time.LocalDateTime;
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

    @Value("${category.insert}")
    private String createCategory;
    @Value("${product.insert}")
    private String createProduct;
//    @Value("${unit.insert}")
//    private String createUnit;
    @Value("${firm.insert}")
    private String createFirm;
    @Value("${good.insert}")
    private String createGood;

    @Override
    public Good create(GoodDto goodDto) {

        // TODO: looks horrible but is there a way to do it cleaner?
        // TODO: make changes with status, shipping date and unit fields

        Good good = goodDto.convertToGood();

        /**
         * insert a new category and return it's ID
         * if such category exists return the category's ID anyway
         */
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("categoryName", good.getCategoryName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(createCategory, parameters, keyHolder);

        /**
         * insert a new product, it's related category ID and return it's ID
         */
        parameters = new MapSqlParameterSource()
                .addValue("productName", good.getGoodName())
                .addValue("categoryId", keyHolder.getKey().longValue());
        keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(createProduct, parameters, keyHolder);

        // TODO: do not forget about Unit

        /**
         * insert a new firm and return it's ID
         */
        parameters = new MapSqlParameterSource()
                .addValue("firmName", good.getFirmName());
        KeyHolder firmHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(createFirm, parameters, firmHolder);

        /**
         * finally, insert a new good, it's related firm ID,
         * product ID and return it's ID
         */
        parameters = new MapSqlParameterSource()
                .addValue("prodId", keyHolder.getKey().longValue())
                .addValue("firmId", firmHolder.getKey().longValue())
                .addValue("quantity", good.getQuantity())
                .addValue("price", good.getPrice())
                //.addValue("unitId", unitHolder.getKey().longValue())
                .addValue("discount", good.getDiscount())
                //.addValue("shippingDate", good.getShippingDate())
                .addValue("inStock", good.isInStock())
                .addValue("description", good.getDescription());
        KeyHolder goodHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(createGood, parameters, goodHolder);

        good.setId(goodHolder.getKey().longValue());
        return good;
    }


    @Value("${products.show}")
    private String showAllProducts;

    @Override
    public List<Good> showAll() {
        return namedParameterJdbcTemplate.query(showAllProducts, this::mapRow);
    }


    @Override
    public Good edit(Good good) {
        return null;
    }


    @Value("${product.find-by-id}")
    private String findByIdQuery;

    @Override
    public Optional<Good> findById(long id) {
        SqlParameterSource productParameter = new MapSqlParameterSource()
                .addValue("id", id);

        return Optional.ofNullable(
                namedParameterJdbcTemplate.queryForObject(
                                findByIdQuery,
                                productParameter,
                                this::mapRow
                        ));
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
                .firmName(rs.getString("firm_id"))
                .quantity(rs.getInt("quantity"))
                .price(rs.getDouble("price"))
                //.unit(Unit.valueOf(rs.getString("unit")))
                .discount(rs.getByte("discount"))
                //.shippingDate(rs.getObject("shipping_date", LocalDateTime.class))
                .inStock(rs.getBoolean("in_stock"))
                .description(rs.getString("description"))
                .categoryName(rs.getString("category_id"))
                .build();
    }
}
