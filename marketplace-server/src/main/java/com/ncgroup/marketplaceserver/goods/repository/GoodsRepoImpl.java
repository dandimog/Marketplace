package com.ncgroup.marketplaceserver.goods.repository;

import com.ncgroup.marketplaceserver.goods.exceptions.GoodAlreadyExistsException;
import com.ncgroup.marketplaceserver.goods.model.Good;
import com.ncgroup.marketplaceserver.goods.model.GoodDto;
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
import java.util.List;
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

    @Value("${firm.insert}")
    private String firmInsert;
    @Value("${category.insert}")
    private String categoryInsert;
    @Value("${product.insert}")
    private String productInsert;
    @Value("${good.insert}")
    private String goodInsert;

    public Long createFirm(String firmName) {
        Long firmId = findByName
                (firmName, "firmName", findFirmByName);
        if (firmId == null) {
            SqlParameterSource firmParameters = new MapSqlParameterSource()
                    .addValue("firmName", firmName);
            KeyHolder firmHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(firmInsert, firmParameters, firmHolder);
            firmId = firmHolder.getKey().longValue();
        }
        return firmId;
    }

    public Long createCategory(String categoryName) {
        Long categoryId = findByName
                (categoryName, "categoryName", findCategoryByName);
        if (categoryId == null) {
            SqlParameterSource categoryParameters = new MapSqlParameterSource()
                    .addValue("categoryName", categoryName);
            KeyHolder categoryHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(categoryInsert, categoryParameters, categoryHolder);
            categoryId = categoryHolder.getKey().longValue();
        }
        return categoryId;
    }

    public Long createProduct(String goodName, Long categoryId) {
        Long productId = findByName
                (goodName, "productName", findProductByName);
        if (productId == null) {
            KeyHolder productHolder = new GeneratedKeyHolder();
            SqlParameterSource productParameters = new MapSqlParameterSource()
                    .addValue("productName", goodName)
                    .addValue("categoryId", categoryId);
            namedParameterJdbcTemplate.update(productInsert, productParameters, productHolder);
            productId = productHolder.getKey().longValue();
        }
        return productId;
    }

    public Long createGood(GoodDto goodDto, Long productId, Long firmId)
            throws GoodAlreadyExistsException {
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
            return keyHolder.getKey().longValue();
        }
        throw new GoodAlreadyExistsException
                ("Such good already exists! " +
                        "If you want to modify an existing good," +
                        " please go to the list of goods, select good and click edit.");
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

    @Override
    public Long create(GoodDto goodDto) throws GoodAlreadyExistsException {

        // TODO: make changes with status, shipping date and unit fields
        Long firmId = createFirm(goodDto.getFirmName());
        Long categoryId = createCategory(goodDto.getCategoryName());
        Long productId = createProduct(goodDto.getGoodName(), categoryId);

        /**
         * goods are equal if their firm,
         * product and shipping date are equal
         */

        return createGood(goodDto, productId, firmId);
    }

    @Value("${product.update}")
    private String updateProduct;
    public void editGood(GoodDto goodDto, Long id, Long productId, Long firmId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id) // for search purpose
                .addValue("prodId", productId)
                .addValue("firmId", firmId)
                .addValue("quantity", goodDto.getQuantity())
                .addValue("price", goodDto.getPrice())
                .addValue("discount", goodDto.getDiscount())
                .addValue("inStock", goodDto.isInStock())
                .addValue("description", goodDto.getDescription());
        namedParameterJdbcTemplate.update(updateProduct, parameters);
    }

    @Override
    public void edit(GoodDto goodDto, Long id) {
        Long firmId = createFirm(goodDto.getFirmName());
        Long categoryId = createCategory(goodDto.getCategoryName());
        Long productId = createProduct(goodDto.getGoodName(), categoryId);
        editGood(goodDto, id, productId, firmId);
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

    @Override
    public List<Good> display(String query) {
        return namedParameterJdbcTemplate.query(
                query,
                this::mapRow
        );
    }

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
