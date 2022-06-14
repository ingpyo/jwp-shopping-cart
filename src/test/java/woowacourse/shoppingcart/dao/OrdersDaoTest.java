package woowacourse.shoppingcart.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import woowacourse.shoppingcart.dao.entity.CustomerEntity;
import woowacourse.shoppingcart.dao.entity.OrdersEntity;

@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Sql(scripts = "classpath:schema.sql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings({"NonAsciiCharacters"})
class OrdersDaoTest {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final OrdersDao ordersDao;

    public OrdersDaoTest(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.ordersDao = new OrdersDao(jdbcTemplate);
    }

    @Test
    void 주문_추가() {
        //given
        Long customerId = 고객_등록();

        //when
        Long orderId = ordersDao.save(customerId);

        //then
        assertThat(orderId).isNotNull();
    }

    @Test
    void 회원_id로_주문_목록_조회() {
        //given
        Long customerId = 고객_등록();
        int insertCount = 3;
        for (int i = 0; i < insertCount; i++) {
            주문_등록(customerId);
        }

        //when
        List<Long> orderIdsByCustomerId = ordersDao.findByCustomerId(customerId)
                .stream()
                .map(OrdersEntity::getId)
                .collect(Collectors.toUnmodifiableList());

        //then
        assertThat(orderIdsByCustomerId).hasSize(insertCount);
    }

    @Test
    void 존재하는_고객_id_및_주문_id_인지_검증() {
        // given
        Long customerId = 고객_등록();
        Long orderId = 주문_등록(customerId);

        // when

        // then
        assertThat(ordersDao.existsOrderId(customerId, orderId)).isTrue();
    }

    private Long 고객_등록() {
        String sql = "INSERT INTO customer (account, nickname, password, address, phone_number) "
                + "VALUES (:account, :nickname, :password, :address, :phoneNumber)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource source = new BeanPropertySqlParameterSource(
                new CustomerEntity(null, "yeonlog", "연로그", "aA!12345", "ㅇ", "01011112222"));

        jdbcTemplate.update(sql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private Long 주문_등록(Long customerId) {
        String sql = "INSERT INTO orders (customer_id) VALUES (:id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource source = new MapSqlParameterSource("id", customerId);

        jdbcTemplate.update(sql, source, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
}