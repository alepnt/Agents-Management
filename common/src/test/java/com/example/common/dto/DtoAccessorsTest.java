package com.example.common.dto;

import com.example.common.enums.ContractStatus;
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.common.enums.InvoiceStatus;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DtoAccessorsTest {

    private static final Instant SAMPLE_INSTANT = Instant.parse("2024-01-01T00:00:00Z");
    private static final LocalDate SAMPLE_LOCAL_DATE = LocalDate.parse("2024-01-01");
    private static final LocalDateTime SAMPLE_LOCAL_DATE_TIME = LocalDateTime.parse("2024-01-01T00:00:00");
    private static final BigDecimal SAMPLE_AMOUNT = new BigDecimal("10.50");

    private static final Map<Class<?>, Supplier<?>> SAMPLE_VALUES = Map.ofEntries(
            Map.entry(Long.class, () -> 1L),
            Map.entry(Integer.class, () -> 2),
            Map.entry(String.class, () -> "sample"),
            Map.entry(Boolean.class, () -> Boolean.TRUE),
            Map.entry(Instant.class, () -> SAMPLE_INSTANT),
            Map.entry(LocalDate.class, () -> SAMPLE_LOCAL_DATE),
            Map.entry(LocalDateTime.class, () -> SAMPLE_LOCAL_DATE_TIME),
            Map.entry(BigDecimal.class, () -> SAMPLE_AMOUNT),
            Map.entry(DocumentType.class, () -> DocumentType.CONTRACT),
            Map.entry(DocumentAction.class, () -> DocumentAction.CREATED),
            Map.entry(InvoiceStatus.class, () -> InvoiceStatus.PAID),
            Map.entry(ContractStatus.class, () -> ContractStatus.ACTIVE)
    );

    private static final List<Class<?>> ID_BASED_DTOS = List.of(
            AgentDTO.class,
            ArticleDTO.class,
            CommissionDTO.class,
            ContractDTO.class,
            CustomerDTO.class,
            DocumentHistoryDTO.class,
            InvoiceDTO.class,
            InvoiceLineDTO.class,
            InvoicePaymentRequest.class,
            MessageDTO.class,
            NotificationDTO.class,
            NotificationSubscriptionDTO.class,
            RoleDTO.class,
            TeamDTO.class
    );

    @TestFactory
    Stream<DynamicTest> shouldRoundTripAccessors() {
        return ID_BASED_DTOS.stream()
                .map(dtoClass -> DynamicTest.dynamicTest(dtoClass.getSimpleName(), () -> verifyDto(dtoClass)));
    }

    private void verifyDto(Class<?> dtoClass) throws Exception {
        Object instance = dtoClass.getConstructor().newInstance();

        for (Method setter : dtoClass.getMethods()) {
            if (isSetter(setter)) {
                Object value = sampleValue(setter.getParameterTypes()[0]);
                setter.invoke(instance, value);
                Method getter = findGetter(dtoClass, setter);
                Object readValue = getter.invoke(instance);
                assertThat(readValue)
                        .as("Getter should return the value set by %s", setter.getName())
                        .isEqualTo(value);
            }
        }

        verifyEqualsAndHashCode(dtoClass);
    }

    private void verifyEqualsAndHashCode(Class<?> dtoClass) throws Exception {
        Method idSetter = dtoClass.getMethod("setId", Long.class);
        Object first = dtoClass.getConstructor().newInstance();
        Object second = dtoClass.getConstructor().newInstance();
        Object different = dtoClass.getConstructor().newInstance();

        idSetter.invoke(first, 1L);
        idSetter.invoke(second, 1L);
        idSetter.invoke(different, 2L);

        assertThat(first)
                .as("Objects with the same id should be equal")
                .isEqualTo(second)
                .hasSameHashCodeAs(second);
        assertThat(first)
                .as("Objects with a different id should not be equal")
                .isNotEqualTo(different);
    }

    private static boolean isSetter(Method method) {
        return method.getName().startsWith("set")
                && method.getParameterCount() == 1
                && Modifier.isPublic(method.getModifiers());
    }

    private Object sampleValue(Class<?> parameterType) {
        if (List.class.isAssignableFrom(parameterType)) {
            return new ArrayList<>(List.of(new DocumentHistoryDTO()));
        }
        Supplier<?> supplier = SAMPLE_VALUES.get(parameterType);
        if (supplier != null) {
            return supplier.get();
        }
        if (parameterType.isEnum()) {
            Object[] constants = parameterType.getEnumConstants();
            return Objects.requireNonNull(constants)[0];
        }
        throw new IllegalArgumentException("No sample value configured for type: " + parameterType.getName());
    }

    private Method findGetter(Class<?> dtoClass, Method setter) throws NoSuchMethodException {
        String getterName = setter.getName().replaceFirst("set", "get");
        return dtoClass.getMethod(getterName);
    }
}
