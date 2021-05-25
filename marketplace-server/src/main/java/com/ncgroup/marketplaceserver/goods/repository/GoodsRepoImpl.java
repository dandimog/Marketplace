package com.ncgroup.marketplaceserver.goods.repository;

import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;

@PropertySource("classpath:database/productQueries.properties")
@Repository
public class GoodsRepoImpl implements GoodsRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public GoodsRepoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Good create(Good good) {
        return null;
    }


    
    @Value("${products.show}")
    private String showAllProducts;

    @Override
    public Collection<Good> showAll() {
        return namedParameterJdbcTemplate.query(showAllProducts, this::mapRowToGood);
    }


    @Override
    public Good edit(Good good) {
        return null;
    }


    @Value("${product.find-by-name}")
    private String findByNameQuery;

    @Override
    public Collection<Good> findByName(String name) {
        SqlParameterSource goodsParams = new MapSqlParameterSource()
                .addValue("subName", name);
        return namedParameterJdbcTemplate.query(
                findByNameQuery,
                goodsParams,
                this::mapRowToGood
        );
    }

    @Value("${product.filter-by-category}")
    private String filterByGoodCategory;

    @Override
    public Collection<Good> filterByGoodCategory(long categoryId) {
        SqlParameterSource goodsParams = new MapSqlParameterSource()
                .addValue("categoryId", categoryId);
        return namedParameterJdbcTemplate.query(
                filterByGoodCategory,
                goodsParams,
                this::mapRowToGood
        );
    }

    @Value("${product.filter-by-price-range}")
    private String filterByPriceRange;

    @Override
    public Collection<Good> filterByPrice(int downLimit, int upLimit) {
        SqlParameterSource goodsParams = new MapSqlParameterSource()
                .addValue("downLimit", downLimit)
                .addValue("upLimit", upLimit);
        return namedParameterJdbcTemplate.query(
                filterByPriceRange,
                goodsParams,
                this::mapRowToGood
        );
    }

    private Good mapRowToGood(ResultSet rs, int rowNum) throws SQLException {
        return Good.builder()
                .id(rs.getLong("id"))
                .firmId(rs.getLong("firm_id"))
                .quantity(rs.getInt("quantity"))
                .price(rs.getDouble("price"))
                .unit(Unit.valueOf(rs.getString("unit")))
                .discount(rs.getByte("discount"))
                .shippingDate(rs.getObject("shipping_date", LocalDateTime.class))
                .inStock(rs.getBoolean("in_stock"))
                .description(rs.getString("description"))
                .categoryId(rs.getLong("category_id"))
                .build();
    }
}
