package com.example.common.dto; // Package dei DTO condivisi, incluso il relativo test.

import com.example.common.enums.ContractStatus; // Enum usati nei sample values.
import com.example.common.enums.DocumentAction;
import com.example.common.enums.DocumentType;
import com.example.common.enums.InvoiceStatus;

import org.junit.jupiter.api.DynamicTest; // Permette la generazione dinamica di test.
import org.junit.jupiter.api.TestFactory; // Annotazione per metodi che producono Stream di test.

import java.lang.reflect.Method; // Riflessività per ispezionare getter/setter.
import java.lang.reflect.Modifier; // Per verificare modifier dei metodi.
import java.math.BigDecimal; // Tipi supportati nei sample values.
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList; // Per creare liste fittizie nei test.
import java.util.List;
import java.util.Map; // Mappa tipo → sample value supplier.
import java.util.Objects; // Utility per validazioni.
import java.util.function.Supplier; // Funzione che genera valori sample.
import java.util.stream.Stream; // Stream usato per generare DynamicTest.

import static org.assertj.core.api.Assertions.assertThat; // AssertJ per asserzioni fluenti.

/**
 * Test parametrico che verifica:
 * - corretto comportamento getter/setter di tutti i DTO basati su id
 * - comportamento coerente di equals() e hashCode()
 *
 * L'obiettivo è garantire che i DTO siano strutturalmente corretti,
 * specialmente dopo modifiche o refactoring.
 */
class DtoAccessorsTest {

    // ---------- SAMPLE VALUES USATI PER POPOLARE I DTO ----------

    private static final Instant SAMPLE_INSTANT = Instant.parse("2024-01-01T00:00:00Z");
    private static final LocalDate SAMPLE_LOCAL_DATE = LocalDate.parse("2024-01-01");
    private static final LocalDateTime SAMPLE_LOCAL_DATE_TIME = LocalDateTime.parse("2024-01-01T00:00:00");
    private static final BigDecimal SAMPLE_AMOUNT = new BigDecimal("10.50");

    /**
     * Mappa tra tipi supportati e funzioni per generare valori "validi" di test.
     * Serve per alimentare i setter dei vari DTO in modo automatico.
     */
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
            Map.entry(ContractStatus.class, () -> ContractStatus.ACTIVE));

    /**
     * Elenco dei DTO che possiedono:
     * - un campo id (Long)
     * - getter/setter pubblici standard
     * - equals/hashCode basati su id
     */
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
            TeamDTO.class);

    // ---------- GENERATORE DI TEST DINAMICI ----------

    /**
     * Genera un DynamicTest per ogni DTO nella lista ID_BASED_DTOS.
     * Ogni test verifica getter/setter e equals/hashCode.
     */
    @TestFactory
    Stream<DynamicTest> shouldRoundTripAccessors() {
        return ID_BASED_DTOS.stream()
                .map(dtoClass -> DynamicTest.dynamicTest(dtoClass.getSimpleName(),
                        () -> verifyDto(dtoClass)));
    }

    // ---------- VERIFICA DELLE PROPRIETÀ DEL DTO ----------

    /**
     * Verifica:
     * 1. Tutti i setter accettano un valore valido (sample)
     * 2. Il rispettivo getter restituisce esattamente lo stesso valore
     * 3. equals/hashCode funzionano basandosi solo sul campo id
     */
    private void verifyDto(Class<?> dtoClass) throws Exception {
        Object instance = dtoClass.getConstructor().newInstance(); // Crea nuova istanza via riflessione.

        for (Method setter : dtoClass.getMethods()) { // Analizza tutti i metodi pubblici.
            if (isSetter(setter)) { // Filtra solo i setter.
                Object value = sampleValue(setter.getParameterTypes()[0]); // Valore di test per il tipo richiesto.
                setter.invoke(instance, value); // Invoca il setter.
                Method getter = findGetter(dtoClass, setter); // Trova il getter corrispondente.
                Object readValue = getter.invoke(instance); // Legge il valore.
                assertThat(readValue) // Assert → getter deve restituire il valore del setter.
                        .as("Getter should return the value set by %s", setter.getName())
                        .isEqualTo(value);
            }
        }

        verifyEqualsAndHashCode(dtoClass); // Verifica comportamento di equals/hashCode.
    }

    // ---------- VERIFICA EQUALS/HASHCODE BASATI SU ID ----------

    private void verifyEqualsAndHashCode(Class<?> dtoClass) throws Exception {
        Method idSetter = dtoClass.getMethod("setId", Long.class); // Setter dell'id obbligatorio.

        Object first = dtoClass.getConstructor().newInstance();
        Object second = dtoClass.getConstructor().newInstance();
        Object different = dtoClass.getConstructor().newInstance();

        idSetter.invoke(first, 1L);
        idSetter.invoke(second, 1L);
        idSetter.invoke(different, 2L);

        assertThat(first) // Stesso id → oggetti uguali + stesso hashCode.
                .as("Objects with the same id should be equal")
                .isEqualTo(second)
                .hasSameHashCodeAs(second);

        assertThat(first) // Id diversi → oggetti diversi.
                .as("Objects with a different id should not be equal")
                .isNotEqualTo(different);
    }

    // ---------- UTILITIES PER RIFLESSIONE ----------

    /**
     * Un metodo viene considerato setter se:
     * - inizia con "set"
     * - è pubblico
     * - ha un solo parametro
     */
    private static boolean isSetter(Method method) {
        return method.getName().startsWith("set")
                && method.getParameterCount() == 1
                && Modifier.isPublic(method.getModifiers());
    }

    /**
     * Restituisce un valore di esempio per il tipo del parametro del setter.
     */
    private Object sampleValue(Class<?> parameterType) {
        if (List.class.isAssignableFrom(parameterType)) { // Gestione speciale: setter che accettano una lista.
            return new ArrayList<>(List.of(new DocumentHistoryDTO()));
        }
        Supplier<?> supplier = SAMPLE_VALUES.get(parameterType); // Valori configurati esplicitamente.
        if (supplier != null) {
            return supplier.get();
        }
        if (parameterType.isEnum()) { // Enum non nella mappa → usa il primo valore.
            Object[] constants = parameterType.getEnumConstants();
            return Objects.requireNonNull(constants)[0];
        }
        throw new IllegalArgumentException(
                "No sample value configured for type: " + parameterType.getName());
    }

    /**
     * Trova automaticamente il getter corrispondente a un setter.
     * Esempio: setName → getName
     */
    private Method findGetter(Class<?> dtoClass, Method setter) throws NoSuchMethodException {
        String getterName = setter.getName().replaceFirst("set", "get");
        return dtoClass.getMethod(getterName);
    }
}
